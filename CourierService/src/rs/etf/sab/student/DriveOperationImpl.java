/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import com.sun.istack.internal.NotNull;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.student.utility.Pair;
import rs.etf.sab.student.utility.ReducedPackage;
import rs.etf.sab.student.utility.Util;

/**
 *
 * @author matij
 */
public class DriveOperationImpl implements DriveOperation {

    private final Connection connection = DB.getInstance().getConnection();
    
    private final UserOperationsImpl userOperationsImpl = new UserOperationsImpl();
    private final AddressOperationsImpl addressOperationsImpl = new AddressOperationsImpl();
    private final StockroomOperationsImpl stockroomOperationsImpl = new StockroomOperationsImpl();
    private final CourierOperationsImpl courierOperationsImpl = new CourierOperationsImpl();
    private final VehicleOperationsImpl vehicleOperationsImpl = new VehicleOperationsImpl();
    
    private List<ReducedPackage> allDestinationStops;
    private Long lastStopAddressId;

    private enum DeliveryStatus_CurrentDrivePackage {
        PickedUpToBeDelivered(0),
        PickedUpToBeStocked(1);
        
        private int value;
        private DeliveryStatus_CurrentDrivePackage(int value) {
            this.value = value;
        }
    };

    private enum VisitReason_CurrentDrivePlan {
                
        PickUpFromUserAddressToDeliver(0),
        PickUpFromStockroomToDeliver(1),
        DeliverPackage(2),
        PickUpFromUserAddressToStock(3),
        PickUpFromStockroomToStock(4),
        FinishDriveAtTheCourierStockroom(5);
        
        private int value;
        private VisitReason_CurrentDrivePlan(int value) {
            this.value = value;
        }
    };

    private enum CourierStatus_Courier {
                
        NotDrive(0),
        Drive(1);
        
        private int value;
        private CourierStatus_Courier(int value) {
            this.value = value;
        }
    };


    
    public long createDrive(Long courierId, Long vehicleId, Long startAddressId) throws Exception {

        String createCurrentDriveQuery = "INSERT INTO [dbo].[CurrentDrive] " +
                                        "       (IdU, IdV, IdA) " +
                                        "   VALUES (?, ?, ?) ";

        try(PreparedStatement ps = connection.prepareStatement(createCurrentDriveQuery, Statement.RETURN_GENERATED_KEYS);) {           

            ps.setLong(1, courierId);
            ps.setLong(2, vehicleId);
            ps.setLong(3, startAddressId);
            
            ps.executeUpdate();
            
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    return rs.getLong(1);
                }
            }               

        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        throw new Exception("Error in creating new drive!");                

    }

    public void logDrive(Long courierId, Long vehicleId) throws Exception {

        String createCurrentDriveQuery = "INSERT INTO [dbo].[HistoryDrive] " +
                                        "       (IdU, IdV) " +
                                        "   VALUES (?, ?) ";

        try(PreparedStatement ps = connection.prepareStatement(createCurrentDriveQuery);) {           

            ps.setLong(1, courierId);
            ps.setLong(2, vehicleId);
            
            int numberOfDrivesCreated = ps.executeUpdate();
               
            if(numberOfDrivesCreated == 1)
                return;
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        throw new Exception("Error in logging drive!");                

    }

    private void insertPureProfitToCurrentDrive(Long driveId) throws Exception {

        String updatePureProfit = "UPDATE [dbo].[CurrentDrive] " +
                                "	SET RealizedProfit = ( " +
                                "				SELECT COALESCE(SUM(P.Price), 0) " +
                                "                                   FROM [dbo].[CurrentDrivePlan] CDP " +
                                "					INNER JOIN [dbo].[Package] P ON (CDP.IdP = P.IdP) " +
                                "                                   WHERE CDP.IdCD = ? " + 
                                "                                       AND CDP.VisitReason = 2 " +
                                "                           ), " +
                                "           NumberOfDeliveries = ( " +
                                "				SELECT COUNT(*) " +
                                "                                   FROM [dbo].[CurrentDrivePlan] CDP " +
                                "					INNER JOIN [dbo].[Package] P ON (CDP.IdP = P.IdP) " +
                                "                                   WHERE CDP.IdCD = ? " + 
                                "                                       AND CDP.VisitReason = 2 " +
                                "                           ) " +
                                "	WHERE IdCD = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(updatePureProfit);) {           

            ps.setLong(1, driveId);
            ps.setLong(2, driveId);
            ps.setLong(3, driveId);

            int updatedDrivesNumber = ps.executeUpdate();
            
            if(updatedDrivesNumber == 1)
                return;
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Error in updating drive!");        
        
    }

    
    public List<ReducedPackage> fetchUndeliveredUnstockedPackagesFromGivenCity(Long cityId) throws Exception {

        List<ReducedPackage> listOfPackages = new ArrayList<ReducedPackage>();
        
        String fetchAllUndeliveredUnstockedPackagesFromCourierCityQuery = 
                "SELECT P.IdP AS IdP, P.IdStartAddress AS StartAddress, " + 
                    " P.IdEndAddress AS EndAddress, P.Weight AS Weight, " + 
                    " P.Price AS Price, SA.IdC AS StartCity, EA.IdC AS EndCity " +
                    "	FROM [dbo].[Package] P " +
                    "		INNER JOIN [dbo].[Address] SA on (P.IdStartAddress = SA.IdA) " +
                    "		INNER JOIN [dbo].[Address] EA on (P.IdEndAddress = EA.IdA) " +
                    "	WHERE SA.IdC = ? " +
                    "		AND PackageStatus = 1 " +
                    "		AND P.IdP NOT IN (" +
                    "				  SELECT IdP " +
                    "                                 FROM PackageStockroom " +
                    "                            ) " +
                    "	ORDER BY P.CreateTime, P.Weight; ";
                
        try(PreparedStatement ps = connection.prepareStatement(fetchAllUndeliveredUnstockedPackagesFromCourierCityQuery);) {
            
            ps.setLong(1, cityId);
            
            try(ResultSet rs = ps.executeQuery()){
            
                while(rs.next()){
                    ReducedPackage rp =  new ReducedPackage();
                    rp.setPackageId(rs.getLong("IdP"));
                    rp.setStartAddressId(rs.getLong("StartAddress"));
                    rp.setStartCityId(rs.getLong("StartCity"));
                    rp.setEndAddressId(rs.getLong("EndAddress"));
                    rp.setEndCityId(rs.getLong("EndCity"));
                    rp.setPrice(rs.getBigDecimal("Price"));
                    rp.setWeight(rs.getBigDecimal("Weight"));
                    listOfPackages.add(rp);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfPackages;

    }
    
    public List<ReducedPackage> fetchUndeliveredStockedPackagesFromGivenCity(Long cityId) throws Exception {

        List<ReducedPackage> listOfPackages = new ArrayList<ReducedPackage>();
        
        String fetchAllUndeliveredStockedPackagesFromCityQuery = "" +
            " SELECT P.IdP AS IdP, S.IdA AS StartAddress, P.IdEndAddress AS EndAddress, " +
            " P.Weight AS Weight, P.Price AS Price, PSA.IdC AS StartCity, EA.IdC AS EndCity " +
            "	FROM [dbo].[PackageStockroom] PS " +
            "       INNER JOIN [dbo].[Stockroom] S ON (PS.IdS = S.IdS) " +
            "       INNER JOIN [dbo].[Address] PSA on (S.IdA = PSA.IdA) " +
            "       INNER JOIN [dbo].[Package] P ON (PS.IdP = P.IdP) " +
            "       INNER JOIN [dbo].[Address] EA on (P.IdEndAddress = EA.IdA) " +
            "	WHERE PSA.IdC = ? " +
            "       AND PackageStatus = 2 " +
            "	ORDER BY P.CreateTime, P.Weight; ";
                
        try(PreparedStatement ps = connection.prepareStatement(fetchAllUndeliveredStockedPackagesFromCityQuery);) {
            
            ps.setLong(1, cityId);
            
            try(ResultSet rs = ps.executeQuery()){
            
                while(rs.next()){
                    ReducedPackage rp =  new ReducedPackage();
                    rp.setPackageId(rs.getLong("IdP"));
                    rp.setStartAddressId(rs.getLong("StartAddress"));
                    rp.setStartCityId(rs.getLong("StartCity"));
                    rp.setEndAddressId(rs.getLong("EndAddress"));
                    rp.setEndCityId(rs.getLong("EndCity"));
                    rp.setPrice(rs.getBigDecimal("Price"));
                    rp.setWeight(rs.getBigDecimal("Weight"));
                    listOfPackages.add(rp);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfPackages;

    }

    private Long insertNextStopInCurrentDrivingPlan(Long driveId, int nextPlanPoint, int visitReason, Long packageId, Long destinationAddressId) throws Exception {
        
        String insertNextStopQuery = "INSERT INTO [dbo].[CurrentDrivePlan] " +
                                    "       (IdCD, OrdinalVisitNumber, VisitReason, ";
        if(packageId != null) {
            insertNextStopQuery += "IdP, "; 
        }
        insertNextStopQuery += " IdA) VALUES (?, ?, ?, ";
        if(packageId != null) {
            insertNextStopQuery += "?, ";
        }
        insertNextStopQuery += " ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertNextStopQuery, Statement.RETURN_GENERATED_KEYS);) {           
            ps.setLong(1, driveId);
            ps.setInt(2, nextPlanPoint);
            ps.setInt(3, visitReason);
            if(packageId != null) {
                ps.setLong(4, packageId);
                ps.setLong(5, destinationAddressId);
            } else {
                ps.setLong(4, destinationAddressId);
            }
            
            int numberOfInsertedNextStops = ps.executeUpdate();
            if(numberOfInsertedNextStops == 1) {
                try(ResultSet rs = ps.getGeneratedKeys()){
                    if(rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
       
        throw new Exception("Error in inserting next stop!");          
        
    }
    
    private int fetchNextPlanPoint(Long driveId) throws Exception {
        String fetchLastPlanPointQuery = "SELECT MAX(OrdinalVisitNumber) "
                                        + " FROM [dbo].[CurrentDrivePlan] "
                                        + " WHERE IdCD = ?; ";
        int nextPlanPoint = 0;
        try(PreparedStatement ps = connection.prepareStatement(fetchLastPlanPointQuery);) {           
            
            ps.setLong(1, driveId);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                    nextPlanPoint = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            throw new Exception(ex);
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        nextPlanPoint += 1;
        return nextPlanPoint;
    }
    
    private void markStockedPackageToBePickedUp(Long currentDrivePlanId, Long packageId, Long stockroomId) throws Exception {
        
        String insertPackageFromStockroomForCurrentDrive = "INSERT INTO [dbo].[StockedPackagesForCurrentDrive] " +
                                                            " (IdPlan, IdPS, IdP) " + 
                                                            " VALUES (?, ?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertPackageFromStockroomForCurrentDrive);) {           
            ps.setLong(1, currentDrivePlanId);
            ps.setLong(2, this.stockroomOperationsImpl.fetchPackageStockroomIdByStockroomAndPackage(stockroomId, packageId));
            ps.setLong(3, packageId);
            
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
            throw new Exception("Error in inserting next stop!");          
        }
       
    }

    
    private BigDecimal addNextStopsIntoCurrentDrivePlan(Long driveId, BigDecimal vehicleCapacity, List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity, int visitReason) throws Exception {
        
        int nextPlanPoint = fetchNextPlanPoint(driveId);
        
        BigDecimal spaceLeft = vehicleCapacity;
        boolean firstPackageFromStockroom = true;
        Long currentDrivePlanId = null;
        Long destinationAddressId = null;
        
        while(!undeliveredUnstockedPackagesFromCourierCity.isEmpty()) {
            ReducedPackage rp = undeliveredUnstockedPackagesFromCourierCity.remove(0);

            if(spaceLeft.compareTo(rp.getWeight()) > 0) {
                spaceLeft = spaceLeft.subtract(rp.getWeight());

                if(visitReason == VisitReason_CurrentDrivePlan.PickUpFromUserAddressToDeliver.ordinal()) {
                    // 0 - Pick up Package (IdP) from the User Address (IdA) to deliver
                    destinationAddressId = rp.getStartAddressId();
                    lastStopAddressId = destinationAddressId;
                    insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, rp.getPackageId(), destinationAddressId);
                    allDestinationStops.add(rp);
                    nextPlanPoint += 1;

                } else if(visitReason == VisitReason_CurrentDrivePlan.PickUpFromStockroomToDeliver.ordinal()) {
                    // 1 - Pick up Packages (IdP = null, those IdP are in StockRoom for given IdA) from the Stockroom Address (IdA) to deliver
                    if(firstPackageFromStockroom == true) {
                        firstPackageFromStockroom = false;
                        destinationAddressId = rp.getStartAddressId();
                        lastStopAddressId = destinationAddressId;
                        currentDrivePlanId = insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, null, destinationAddressId);
                        nextPlanPoint += 1;
                    }
                    Long packageStockroomId = this.stockroomOperationsImpl.fetchStockroomIdByCityId(this.addressOperationsImpl.fetchCityIdOfAddress(lastStopAddressId));
                    allDestinationStops.add(rp);
                    markStockedPackageToBePickedUp(currentDrivePlanId, rp.getPackageId(), packageStockroomId);
                    
                } else if(visitReason == VisitReason_CurrentDrivePlan.DeliverPackage.ordinal()) {
                    // 2 - Deliver Package (IdP) to the Destination Address (IdA)
                    throw new Exception("Not possible scenario!");
                } else if(visitReason == VisitReason_CurrentDrivePlan.PickUpFromUserAddressToStock.ordinal()) {
                    // 3 - Pick up Package (IdP) from the User Address (IdA) to stock
                    destinationAddressId = rp.getStartAddressId();
                    lastStopAddressId = destinationAddressId;
                    insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, rp.getPackageId(), destinationAddressId);
                    nextPlanPoint += 1;
                    
                } else if(visitReason == VisitReason_CurrentDrivePlan.PickUpFromStockroomToStock.ordinal()) {
                    // 4 - Pick up Package (IdP = null, those IdP are in StockRoom for given IdA) from the stockroom Address (IdA) to stock in User's city stockroom
                    if(firstPackageFromStockroom == true) {
                        firstPackageFromStockroom = false;
                        destinationAddressId = rp.getStartAddressId();
                        lastStopAddressId = destinationAddressId;
                        currentDrivePlanId = insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, null, destinationAddressId);
                        nextPlanPoint += 1;
                    }

                    Long packageStockroomId = this.stockroomOperationsImpl.fetchStockroomIdByCityId(this.addressOperationsImpl.fetchCityIdOfAddress(lastStopAddressId));
                    markStockedPackageToBePickedUp(currentDrivePlanId, rp.getPackageId(), packageStockroomId);
                    
                } else if(visitReason == VisitReason_CurrentDrivePlan.FinishDriveAtTheCourierStockroom.ordinal()) {
                    // 5 - Finish drive at the Stockroom Address (IdA) (in the User's city)
                    throw new Exception("Not possible scenario!");
                }                
                
            }
        }
        
        return spaceLeft;
    }

    private void sortByDestination(List<ReducedPackage> packageList, Long baseAddressId) {
        Collections.sort(packageList, (ds1, ds2)-> {
            Pair p0 = null, p1 = null, p2 = null;
            try {
                p0 = this.addressOperationsImpl.fetchCoordinates(baseAddressId);
                p1 = this.addressOperationsImpl.fetchCoordinates(ds1.getEndAddressId());
                p2 = this.addressOperationsImpl.fetchCoordinates(ds2.getEndAddressId());
            } catch (Exception ex) {
                Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            if(Util.getDistance((Pair<Integer, Integer>[])new Pair[] { p0, p1 }) 
                    > 
               Util.getDistance((Pair<Integer, Integer>[])new Pair[] { p0, p2 }))
                    return 1;
            if(Util.getDistance((Pair<Integer, Integer>[])new Pair[] { p0, p1 }) 
                    < 
               Util.getDistance((Pair<Integer, Integer>[])new Pair[] { p0, p2 }))
                    return -1;
            return 0;            
        });
    }
    
    private List<ReducedPackage> fetchAllPackagesFromTheSameDestinationCity(Long currentCity) {
        List<ReducedPackage> packagesFromTheSameDestCity = new ArrayList<>();
//        List<Integer> packagesFromTheSameDestCityIndexes = new ArrayList<>();

        for(int i = 0; i < allDestinationStops.size(); i++) {
            ReducedPackage rp = allDestinationStops.get(i);
            if(Objects.equals(rp.getEndCityId(), currentCity)) {
                packagesFromTheSameDestCity.add(rp);
//                packagesFromTheSameDestCityIndexes.add(i);
            }
        }
        for(ReducedPackage rp: packagesFromTheSameDestCity) {
            allDestinationStops.remove(rp);
        }
        
        return packagesFromTheSameDestCity;
    }
    
    private int insertAllOthersPackagesFromTheSameDestinationCity(Long driveId, List<ReducedPackage> packagesForTheSameCity, int nextPlanPoint) throws Exception {
        
        while(!packagesForTheSameCity.isEmpty()) {
           
            sortByDestination(packagesForTheSameCity, lastStopAddressId); 
            ReducedPackage rp = packagesForTheSameCity.remove(0);
            
            lastStopAddressId = rp.getEndAddressId();
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, VisitReason_CurrentDrivePlan.DeliverPackage.ordinal(), rp.getPackageId(), lastStopAddressId);
            nextPlanPoint += 1;
        }
        return nextPlanPoint;
    }
    
    private void addNextStopsAfterPickingUp(Long driveId, BigDecimal vehicleCapacity, Long currentCity) throws Exception {
        
        int nextPlanPoint = fetchNextPlanPoint(driveId);
//         TODO: SORT
        List<ReducedPackage> packagesForDeliveryToCourierCity = fetchAllPackagesFromTheSameDestinationCity(currentCity);

        while(!packagesForDeliveryToCourierCity.isEmpty()) {

            sortByDestination(packagesForDeliveryToCourierCity, lastStopAddressId); 
            ReducedPackage rp = packagesForDeliveryToCourierCity.remove(0);

            lastStopAddressId = rp.getEndAddressId();
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, VisitReason_CurrentDrivePlan.DeliverPackage.ordinal(), rp.getPackageId(), lastStopAddressId);
            nextPlanPoint += 1;
        }
        
        while(!allDestinationStops.isEmpty()) {
            // Sort all destinations by distance from the last picking up address
            sortByDestination(allDestinationStops, lastStopAddressId);            
            // fetch first stop, add it to 
            ReducedPackage rp = allDestinationStops.remove(0);
            currentCity = rp.getEndCityId();

            List<ReducedPackage> packagesForTheSameCity = fetchAllPackagesFromTheSameDestinationCity(currentCity);
            
            lastStopAddressId = rp.getEndAddressId();
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, VisitReason_CurrentDrivePlan.DeliverPackage.ordinal(), rp.getPackageId(), lastStopAddressId);
            allDestinationStops.remove(rp);
            nextPlanPoint += 1;
                        
//         TODO: SORT!
            nextPlanPoint = insertAllOthersPackagesFromTheSameDestinationCity(driveId, packagesForTheSameCity, nextPlanPoint);
            
            // Now all the packages for currentCity are delivered, so the following needs to be done:
            // Collect packages from currentCity - this is the city where (all of them if packagesForTheSameCity has anything) last package were delivered
            // First packages are collected from (users') addresses 
            // Then they are collected from currentCity's stockroom
            
            List<ReducedPackage> undeliveredUnstockedPackagesFromCurrentCity = fetchUndeliveredUnstockedPackagesFromGivenCity(currentCity);
            List<ReducedPackage> undeliveredStockedPackagesFromCurrentCity = fetchUndeliveredStockedPackagesFromGivenCity(currentCity);

            // Add them to the vehicle (up to the vehicle's maximal weight)
            vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, 
                    undeliveredUnstockedPackagesFromCurrentCity, VisitReason_CurrentDrivePlan.PickUpFromUserAddressToStock.ordinal());

            if (vehicleCapacity.compareTo(BigDecimal.ZERO) > 0){
                System.out.println("Check stockroom (to stock) " + vehicleCapacity);
                vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, 
                        undeliveredStockedPackagesFromCurrentCity, VisitReason_CurrentDrivePlan.PickUpFromStockroomToStock.ordinal());
            }

        }
    }

    @Override
    public boolean planingDrive(@NotNull String courierUsername) {
        
        try {
            
            connection.setAutoCommit(false);
            
            allDestinationStops = new ArrayList<>();
            
            Long courierId = userOperationsImpl.fetchUserIdByUsername(courierUsername);
            Long courierCityId = addressOperationsImpl.fetchCityIdOfUser(courierId);
            Long stockroomCourierCityId = stockroomOperationsImpl.fetchStockroomIdByCityId(courierCityId);            
            Long startAddressId = stockroomOperationsImpl.fetchAddressIdByStockroom(stockroomCourierCityId);
            lastStopAddressId = startAddressId;
            
            // Courier is now driving -> status = 1 (drive)
            courierOperationsImpl.changeCourierStatus(courierId, CourierStatus_Courier.Drive.ordinal()); 

            // Fetch one (not so important which) vehicle from stockroom
            Long vehicleId = stockroomOperationsImpl.removeVehicleFromStockroom(stockroomCourierCityId); 
            BigDecimal vehicleCapacity = vehicleOperationsImpl.fetchCapacity(vehicleId); 
            
            // Create current drive and record this drive
            Long driveId = createDrive(courierId, vehicleId, startAddressId);
            logDrive(courierId, vehicleId);
            
            // Phase 1
            // Collect packages from courier's city (first from addresses then from stockroom)
            
            List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity = fetchUndeliveredUnstockedPackagesFromGivenCity(courierCityId);
            List<ReducedPackage> undeliveredStockedPackagesFromCourierCity = fetchUndeliveredStockedPackagesFromGivenCity(courierCityId);
            
            // Add them to the vehicle (up to the vehicle's maximal weight)
            
            vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, 
                    vehicleCapacity, undeliveredUnstockedPackagesFromCourierCity, 
                    VisitReason_CurrentDrivePlan.PickUpFromUserAddressToDeliver.ordinal());

            if (vehicleCapacity.compareTo(BigDecimal.ZERO) > 0){
                System.out.println("Check stockroom " + vehicleCapacity);
                vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, 
                        vehicleCapacity, undeliveredStockedPackagesFromCourierCity, 
                        VisitReason_CurrentDrivePlan.PickUpFromStockroomToDeliver.ordinal());
            }
            
            
            // Phase 2
            // Order of delivery
            addNextStopsAfterPickingUp(driveId, vehicleCapacity, courierCityId);

            insertPureProfitToCurrentDrive(driveId);

            // Phase 3
            // Return to stockroom
            // There Vehicle and Packages will be leaved
            int nextPlanPoint = fetchNextPlanPoint(driveId);
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, VisitReason_CurrentDrivePlan.FinishDriveAtTheCourierStockroom.ordinal(), null, startAddressId);            

        System.out.println("rs.etf.sab.student.DriveOperationImpl.planingDrive()");

        } catch (SQLException ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false;
        
    }
    
    // 
    
    private void updateProfitCurrentPlanPointAndAddressInCurrentDrive(Long currentDriveId, Long endAddressId, double distance, int nextPlanPoint, Long vehicleId) throws Exception {

        String fetchFuelPricePerKmQuery = "SELECT FuelConsumption * CASE FuelType " +
                                            "                   	WHEN 0 THEN 15 " +
                                            "                           WHEN 1 THEN 32 " +
                                            "                           WHEN 2 THEN 36 " +
                                            "                       END AS FuelPricePerKm " +
                                            "	FROM [dbo].[Vehicle] " +
                                            "	WHERE IdV = ?; ";
        
        String updateCurrentDrive = "UPDATE [dbo].[CurrentDrive] " +
                                    "	SET " +
                                    "		IdA = ?, " +
                                    "		CurrentPlanPoint = ?, " +
                                    "		RealizedProfit = RealizedProfit - ? " +
                                    "	WHERE IdCD = ?; ";
        try(PreparedStatement psFetch = connection.prepareStatement(fetchFuelPricePerKmQuery);
            PreparedStatement psUpdate = connection.prepareStatement(updateCurrentDrive);) {           
                                    
            psFetch.setLong(1, vehicleId);
            
            try(ResultSet rsFetch = psFetch.executeQuery()){
                if(rsFetch.next()) {
                    BigDecimal fuelPricePerKm = rsFetch.getBigDecimal(1);
                    
                    psUpdate.setLong(1, endAddressId);
                    psUpdate.setInt(2, nextPlanPoint);
                    psUpdate.setBigDecimal(3, new BigDecimal(distance).multiply(fuelPricePerKm));
                    psUpdate.setLong(4, currentDriveId);

                    int updatedCurrentDriveNumber = psUpdate.executeUpdate();
                    if(updatedCurrentDriveNumber == 1)
                        return;
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Error in updating current drive!");
       
    }    
    
    private void updatePackageStatusInPackage(Long packageId, int newStatus) throws Exception {

        String updatePackage = "UPDATE [dbo].[Package] " +
                                    "	SET " +
                                    "		PackageStatus = ? " +
                                    "	WHERE IdP = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(updatePackage);) {           

            ps.setInt(1, newStatus);
            ps.setLong(2, packageId);

            int updatedPackagesStatusNumber = ps.executeUpdate();
            
            if(updatedPackagesStatusNumber == 1)
                return;
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Error in updating package!");
    }

    private long insertPackageInCurrentDrivePackage(Long currentDriveId, Long packageId, int deliverySatus) throws Exception {

        String createCurrentDriveQuery = "INSERT INTO [dbo].[CurrentDrivePackage] " +
                                        "       (IdCD, IdP, DeliveryStatus) " +
                                        "   VALUES (?, ?, ?) ";

        try(PreparedStatement ps = connection.prepareStatement(createCurrentDriveQuery, Statement.RETURN_GENERATED_KEYS);) {           

            ps.setLong(1, currentDriveId);
            ps.setLong(2, packageId);
            ps.setInt(3, deliverySatus);
            
            ps.executeUpdate();
            
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    return rs.getLong(1);
                }
            }               

        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        throw new Exception("Error in inserting package into drive vehicle!");                
    }

    private void deleteCurrentDrivePlan(Long planId) throws Exception {
        String deleteCurrentDrivePlan = "DELETE FROM [dbo].[CurrentDrivePlan] " 
                                    + " WHERE IdPlan = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(deleteCurrentDrivePlan);) {           
            ps.setLong(1, planId);

            int numberOfDeletedPlans = ps.executeUpdate();
            if(numberOfDeletedPlans == 1)
                return; 
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
                
        throw new Exception("Error in deleting current drive plan!");             
        
    }
    
    private void removePackageFromCurrentDrivePackage(Long currentDriveId, Long packageId) throws Exception {
        String deleteCurrentDrivePlan = "DELETE FROM [dbo].[CurrentDrivePackage] " 
                                    + " WHERE IdCD = ?"
                                    + "     AND IdP = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(deleteCurrentDrivePlan);) {           
            ps.setLong(1, currentDriveId);
            ps.setLong(2, packageId);

            int numberOfDeletedPackages = ps.executeUpdate();
            if(numberOfDeletedPackages == 1)
                return; 
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
                
        throw new Exception("Error in package from van!");             
 
    }

    private void deleteCurrentDrive(Long currentDriveId) throws Exception {
        String deleteCurrentDrivePlan = "DELETE FROM [dbo].[CurrentDrive] " 
                                        + " WHERE IdCD = ?";

        try(PreparedStatement ps = connection.prepareStatement(deleteCurrentDrivePlan);) {           
            ps.setLong(1, currentDriveId);
    
            int numberOfDeletedCurrentDrives = ps.executeUpdate();
            if(numberOfDeletedCurrentDrives == 1)
                return; 
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
                
        throw new Exception("Error in current drive deleting!");             
 
    }

    private int leavePackageToPackageStockroom(Long currentDriveId, Long stockroomId) throws Exception {
        
        String fetchPackagesFromVehicle = "SELECT IdP " +
                                            "	FROM [dbo].[CurrentDrivePackage] " +
                                            "	WHERE IdCD = ?; ";
        
        String insertPackagesToPackageStockroom = "INSERT INTO [dbo].[PackageStockroom] " +
                                                    "       (IdS, IdP) " +
                                                    "   VALUES (?, ?); ";

        String deletePackagesFromVehicle = "DELETE FROM [dbo].[CurrentDrivePackage] " +
                                            "	WHERE IdCD = ?; ";

        int stockedPackages = 0;  
        int deletedPackagesFromVehicle = 0;  
        try(PreparedStatement psFetch = connection.prepareStatement(fetchPackagesFromVehicle);
            PreparedStatement psInsert = connection.prepareStatement(insertPackagesToPackageStockroom);           
            PreparedStatement psDelete = connection.prepareStatement(deletePackagesFromVehicle);) {           
                                    
            psFetch.setLong(1, currentDriveId);
            
            try(ResultSet rsFetch = psFetch.executeQuery()){
                while(rsFetch.next()) {

                    psInsert.setLong(1, stockroomId);
                    psInsert.setLong(2, rsFetch.getLong("IdP"));

                    stockedPackages += psInsert.executeUpdate();
                }
            }
            
            psDelete.setLong(1, currentDriveId);
            deletedPackagesFromVehicle = psDelete.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        if(deletedPackagesFromVehicle == stockedPackages)
            return stockedPackages;

        throw new Exception("Error in stocking packages!");
                

    }
    
    
    
    private int transferPackageFromTemporaryPackageStockroomToVehicle(Long planId, Long currentDriveId, int deliveryStatus) throws Exception {

        String getAllpackagesIdFromTemporaryPackageStockroomQuery = "" +
                " SELECT IdP, IdPS " +
                "	FROM [dbo].[StockedPackagesForCurrentDrive] " +
                "	WHERE IdPlan = ?; ";
        
        String insertPackagesToVehicleQuery = "INSERT INTO [dbo].[CurrentDrivePackage] " +
                                                    "       (IdCD, IdP, DeliveryStatus) " +
                                                    "   VALUES (?, ?, ?); ";

        String deletePackagesFromTemporaryStockroom = "DELETE FROM [dbo].[StockedPackagesForCurrentDrive] " +
                                                        " WHERE IdPlan = ?; ";

        String deletePackageFromPackageStockroomQuery = "DELETE FROM [dbo].[PackageStockroom] " +
                                                         " WHERE IdPS = ?; ";

        
        int addedPackagesToVehicleNumber = 0;  
        int deletedPackagesFromTemporaryStockroom = 0;  
        int deletedPackagesFromStockroom = 0;  
        List<Long> stockroomIdOfRemovedPackages = new ArrayList<>();
        try(PreparedStatement psFetch = connection.prepareStatement(getAllpackagesIdFromTemporaryPackageStockroomQuery);
            PreparedStatement psInsert = connection.prepareStatement(insertPackagesToVehicleQuery);           
            PreparedStatement psDelete1 = connection.prepareStatement(deletePackagesFromTemporaryStockroom);          
            PreparedStatement psDelete2 = connection.prepareStatement(deletePackageFromPackageStockroomQuery);) {           
                                    
            psFetch.setLong(1, planId);
            
            try(ResultSet rsFetch = psFetch.executeQuery()){
                while(rsFetch.next()) {
                    
                    stockroomIdOfRemovedPackages.add(rsFetch.getLong("IdPS"));
                    
                    psInsert.setLong(1, currentDriveId);
                    psInsert.setLong(2, rsFetch.getLong("IdP"));
                    psInsert.setInt(3, deliveryStatus);
                    
                    addedPackagesToVehicleNumber += psInsert.executeUpdate();
                }
            }
            
            psDelete1.setLong(1, planId);
            deletedPackagesFromTemporaryStockroom = psDelete1.executeUpdate();
 
            for(Long stockroomId: stockroomIdOfRemovedPackages) {
                psDelete2.setLong(1, stockroomId);
                deletedPackagesFromStockroom += psDelete2.executeUpdate();
            }
 
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        if(deletedPackagesFromTemporaryStockroom == addedPackagesToVehicleNumber
            && addedPackagesToVehicleNumber == deletedPackagesFromStockroom)
            return addedPackagesToVehicleNumber;

        throw new Exception("Error in stocking packages!");
                

    }
    

    @Override
    public int nextStop(@NotNull String courierUsername) {
        int returnValue = -3;
        try {
            
            connection.setAutoCommit(false);
            
            Long courierId = this.userOperationsImpl.fetchUserIdByUsername(courierUsername);
            
            String getCurrentDriveQuery = "SELECT IdCD, IdV, CurrentPlanPoint, IdA " +
                                            "	FROM [dbo].[CurrentDrive] " +
                                            "	WHERE IdU = ?; ";
            
            Long currentDriveId = -1L;
            Long vehicleId = -1L;
            int currentPlanPoint = -1;
            Long startAddressId = -1L;
            try(PreparedStatement ps = connection.prepareStatement(getCurrentDriveQuery);) {           

                ps.setLong(1, courierId);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        currentDriveId = rs.getLong("IdCD");
                        vehicleId = rs.getLong("IdV");
                        currentPlanPoint = rs.getInt("CurrentPlanPoint");
                        startAddressId = rs.getLong("IdA");
                    }
                }
            }
            
            String getCurrentPlanQuery = "SELECT IdPlan, VisitReason, IdP, IdA " +
                                            "	FROM [dbo].[CurrentDrivePlan] " +
                                            "	WHERE OrdinalVisitNumber = ?; ";
            Long planId = -1L;
            int visitReason = -1;
            Long packageId = -1L;
            Long endAddressId = -1L;

            try(PreparedStatement ps = connection.prepareStatement(getCurrentPlanQuery);) {           

                ps.setInt(1, currentPlanPoint + 1);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        planId = rs.getLong("IdPlan");
                        visitReason = rs.getInt("VisitReason");
                        packageId = rs.getLong("IdP");
                        endAddressId = rs.getLong("IdA");
 
                    }
                }
            }
            
            double distance = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { 
                this.addressOperationsImpl.fetchCoordinates(startAddressId), 
                this.addressOperationsImpl.fetchCoordinates(endAddressId), 
            });
            
            updateProfitCurrentPlanPointAndAddressInCurrentDrive(currentDriveId, endAddressId, distance, currentPlanPoint + 1, vehicleId);
            
            if(visitReason == VisitReason_CurrentDrivePlan.PickUpFromUserAddressToDeliver.ordinal()) {
                // 0 - Pick up Package (IdP) from the User Address (IdA) to deliver
                returnValue = -2; // return -2
                updatePackageStatusInPackage(packageId, 2);
                insertPackageInCurrentDrivePackage(currentDriveId, packageId, DeliveryStatus_CurrentDrivePackage.PickedUpToBeDelivered.ordinal());
            } else if(visitReason == 1) {
                returnValue = -2; // return -2
                transferPackageFromTemporaryPackageStockroomToVehicle(
                        planId, currentDriveId, DeliveryStatus_CurrentDrivePackage.PickedUpToBeDelivered.ordinal());
                
            } else if(visitReason == VisitReason_CurrentDrivePlan.DeliverPackage.ordinal()) {
                // 2 - Deliver Package (IdP) to the Destination Address (IdA)
                returnValue = packageId.intValue();
                updatePackageStatusInPackage(packageId, 3);
                removePackageFromCurrentDrivePackage(currentDriveId, packageId);
                
            } else if(visitReason == 3) {
                // 3 - Pick up Package (IdP) from the User Address (IdA) to stock
                returnValue = -2; // return -2
                updatePackageStatusInPackage(packageId, 2);
                insertPackageInCurrentDrivePackage(currentDriveId, packageId, DeliveryStatus_CurrentDrivePackage.PickedUpToBeStocked.ordinal());
                
            } else if(visitReason == 4) {
                returnValue = -2; // return -2
                transferPackageFromTemporaryPackageStockroomToVehicle(
                        planId, currentDriveId, DeliveryStatus_CurrentDrivePackage.PickedUpToBeStocked.ordinal());
                
            } else if(visitReason == VisitReason_CurrentDrivePlan.FinishDriveAtTheCourierStockroom.ordinal()) {
                returnValue = -1;
                leavePackageToPackageStockroom(currentDriveId, this.stockroomOperationsImpl.fetchStockroomIdByCityId(this.addressOperationsImpl.fetchCityIdOfAddress(endAddressId)));
                
                // Courier is not driving any more -> status = 1 (not drive)
                courierOperationsImpl.changeCourierStatus(courierId, CourierStatus_Courier.NotDrive.ordinal()); 
                courierOperationsImpl.changeCourierProfitAndNumberOfDeliveries(courierId);
            }
        
            
            deleteCurrentDrivePlan(planId);
            
            if(visitReason == VisitReason_CurrentDrivePlan.FinishDriveAtTheCourierStockroom.ordinal()) {
                deleteCurrentDrive(currentDriveId);
                this.vehicleOperationsImpl.parkVehicle(vehicleId, this.stockroomOperationsImpl.fetchStockroomIdByCityId(this.addressOperationsImpl.fetchCityIdOfAddress(endAddressId)));

            }
            
        } catch (SQLException ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (Exception ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return returnValue;
        
    }

    @Override
    public List<Integer> getPackagesInVehicle(String courierUsername) {

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllPackagesInVehicleQuery = "SELECT CDP.IdP " +
                                            "	FROM CurrentDrivePackage CDP " +
                                            "		INNER JOIN CurrentDrive CD ON (CD.IdCD = CDP.IdCD) " +
                                            "	WHERE CD.IdU = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllPackagesInVehicleQuery);) {           
            Long userId = this.userOperationsImpl.fetchUserIdByUsername(courierUsername);
            ps.setLong(1, userId);
            try(ResultSet rs = ps.executeQuery()){
                
                while(rs.next()){
                    listOfIds.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (Exception ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return listOfIds;        

    }    
    
    public static void main(String[] args) {

        AddressOperations addressOperations = new AddressOperationsImpl();
        CityOperations cityOperations = new CityOperationsImpl();
        CourierOperations courierOperations = new CourierOperationsImpl();
        CourierRequestOperation courierRequestOperation = new CourierRequestOperationsImpl();
        DriveOperation driveOperation = new DriveOperationImpl();
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        PackageOperations packageOperations = new PackageOperationsImpl();
        StockroomOperations stockroomOperations = new StockroomOperationsImpl();
        UserOperations userOperations = new UserOperationsImpl();
        VehicleOperations vehicleOperations = new VehicleOperationsImpl();

        
        generalOperations.eraseAll();
        
        int BG = cityOperations.insertCity("Belgrade", "11000");
        int KG = cityOperations.insertCity("Kragujevac", "550000");
        int VA = cityOperations.insertCity("Valjevo", "14000");
        int CA = cityOperations.insertCity("Cacak", "32000");
        int idAddressBG1 = addressOperations.insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        int idAddressBG2 = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        int idAddressBG3 = addressOperations.insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        int idAddressBG4 = addressOperations.insertAddress("Takovska", 7, BG, 11, 12);
        int idAddressBG5 = addressOperations.insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        int idAddressKG1 = addressOperations.insertAddress("Daniciceva", 1, KG, 4, 310);
        int idAddressKG2 = addressOperations.insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        int idAddressVA1 = addressOperations.insertAddress("Cika Ljubina", 8, VA, 102, 101);
        int idAddressVA2 = addressOperations.insertAddress("Karadjordjeva", 122, VA, 104, 103);
        int idAddressVA3 = addressOperations.insertAddress("Milovana Glisica", 45, VA, 101, 101);
        int idAddressCA1 = addressOperations.insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        int idAddressCA2 = addressOperations.insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);
        int idStockroomBG = stockroomOperations.insertStockroom(idAddressBG1);
        int idStockroomVA = stockroomOperations.insertStockroom(idAddressVA1);
        
        vehicleOperations.insertVehicle("BG1675DA", 2, new BigDecimal(6.3D), new BigDecimal(1000.5D));
        vehicleOperations.parkVehicle("BG1675DA", idStockroomBG);

        vehicleOperations.insertVehicle("VA1675DA", 1, new BigDecimal(7.3D), new BigDecimal(500.5D));
        vehicleOperations.parkVehicle("VA1675DA", idStockroomVA);
        
        String username = "crno.dete";
        userOperations.insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);
        String courierUsernameBG = "postarBG";
        userOperations.insertUser(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2);
        courierOperations.insertCourier(courierUsernameBG, "654321");

        String courierUsernameVA = "postarVA";
        userOperations.insertUser(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressVA2);
        courierOperations.insertCourier(courierUsernameVA, "123456");

        int type = 1;
        BigDecimal weight = new BigDecimal(4);
        int idPackage1 = packageOperations.insertPackage(idAddressBG1, idAddressVA1, username, type, weight);
        packageOperations.acceptAnOffer(idPackage1);

        int idPackage2 = packageOperations.insertPackage(idAddressBG2, idAddressVA2, username, type, weight);
        packageOperations.acceptAnOffer(idPackage2);

        int idPackage3 = packageOperations.insertPackage(idAddressBG3, idAddressVA3, username, type, weight);
        packageOperations.acceptAnOffer(idPackage3);

        
        driveOperation.planingDrive(courierUsernameBG); 
        
    }
    
}
