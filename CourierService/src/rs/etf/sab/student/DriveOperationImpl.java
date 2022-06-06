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
import rs.etf.sab.operations.DriveOperation;
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

    public List<ReducedPackage> fetchUndeliveredUnstockedPackagesFromCourierCity(Long courierCityId) throws Exception {

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
            
            ps.setLong(1, courierCityId);
            
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
    
    public List<ReducedPackage> fetchUndeliveredStockedPackagesFromCourierCity(Long courierCityId) throws Exception {

        List<ReducedPackage> listOfPackages = new ArrayList<ReducedPackage>();
        
        String fetchAllUndeliveredStockedPackagesFromCourierCityQuery = null;
//                "SELECT P.IdP AS IdP, SA.IdA AS StartAddress, P.IdEndAddress AS EndAddress, P.Weight AS Weight, P.Price AS Price, SA.IdC AS StartCity, EA.IdC AS EndCity" +
//                "	FROM [dbo].[PackageStockroom] PS " +
//                "		INNER JOIN [dbo].[Package] P ON (PS.IdP = P.IdP) " +
//                "		INNER JOIN [dbo].[Stockroom] S ON (PS.IdS = S.IdS) " +
//                "		INNER JOIN [dbo].[Address] SA on (S.IdA = SA.IdA) " +
//                "		INNER JOIN [dbo].[Address] EA on (P.IdEndAddress = EA.IdA) " +
//                "	WHERE SA.IdC = ? " +
//                "		AND PackageStatus = 1 " +
//                "	ORDER BY P.CreateTime, P.Weight; ";
                
        try(PreparedStatement ps = connection.prepareStatement(fetchAllUndeliveredStockedPackagesFromCourierCityQuery);) {
            
            ps.setLong(1, courierCityId);
            
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

    private void insertNextStopInCurrentDrivingPlan(Long driveId, int nextPlanPoint, int visitReason, Long packageId, Long destinationAddressId) throws Exception {
        
                
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
        
        try(PreparedStatement ps = connection.prepareStatement(insertNextStopQuery);) {           
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
            if(numberOfInsertedNextStops == 1)
                return;
            
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
    
    private BigDecimal addNextStopsIntoCurrentDrivePlan(Long driveId, BigDecimal vehicleCapacity, List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity, int visitReason) throws Exception {
        
        int nextPlanPoint = fetchNextPlanPoint(driveId);
        
        BigDecimal spaceLeft = vehicleCapacity;
        
        for(ReducedPackage rp: undeliveredUnstockedPackagesFromCourierCity) {
            if(spaceLeft.compareTo(rp.getWeight()) > 0) {
                spaceLeft = spaceLeft.subtract(rp.getWeight());
                Long destinationAddressId = null;
                if(visitReason == 0) {
                    // 0 - Pick up Package (IdP) from the User Address (IdA)
                    destinationAddressId = rp.getStartAddressId();
                } else {
                    // 1 - Pick up Packages (IdP) from the Stockroom Address (IdA) - 
                    // there are multiple rows with (1, IdA) packages, 
                    // all of them should be transfered to CurrentDrivePackages and removed from CurrendDrivePlan
//                    TODO: NOT IMPLEMENTED!!!!!
                    destinationAddressId = null;
                }                
                lastStopAddressId = destinationAddressId;
                insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, rp.getPackageId(), destinationAddressId);
                allDestinationStops.add(rp);
                nextPlanPoint += 1;
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
        List<Integer> packagesFromTheSameDestCityIndexes = new ArrayList<>();

        for(int i = 0; i < allDestinationStops.size(); i++) {
            ReducedPackage rp = allDestinationStops.get(i);
            if(Objects.equals(rp.getEndCityId(), currentCity)) {
                packagesFromTheSameDestCity.add(rp);
                packagesFromTheSameDestCityIndexes.add(i);
            }
        }
        for(int i: packagesFromTheSameDestCityIndexes) {
            allDestinationStops.remove(i);
        }
        
        return packagesFromTheSameDestCity;
    }
    
    private void addNextStopsAfterPickingUp(Long driveId, BigDecimal vehicleCapacity, Long currentCity) throws Exception {
        
        int nextPlanPoint = fetchNextPlanPoint(driveId);
        
        // TODO: SAME CITY AS COURIER
                
        while(!allDestinationStops.isEmpty()) {
            // Sort all destinations by distance from the last picking up address
            sortByDestination(allDestinationStops, lastStopAddressId);            
            // fetch first stop, add it to 
            ReducedPackage rp = allDestinationStops.remove(0);
            currentCity = rp.getEndCityId();
            List<ReducedPackage> packagesForTheSameCity = fetchAllPackagesFromTheSameDestinationCity(currentCity);
            
            lastStopAddressId = rp.getEndAddressId();
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, 2, rp.getPackageId(), lastStopAddressId);
            allDestinationStops.remove(rp);
            nextPlanPoint += 1;
            
//            nextPlanPoint = insertAllOthersPackagesFromTheSameDestinationCity(driveId, packagesForTheSameCity, nextPlanPoint);
            
//            // Collect packages from courier's city (first from addresses then from stockroom)
//            
//            List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity = fetchUndeliveredUnstockedPackagesFromCourierCity(courierCityId);
////            TODO: BAD IMPLEMENTATION            
////            List<ReducedPackage> undeliveredStockedPackagesFromCourierCity = fetchUndeliveredStockedPackagesFromCourierCity(courierCityId);
//            
//            // Add them to the vehicle (up to the vehicle's maximal weight)
//            
//            vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, undeliveredUnstockedPackagesFromCourierCity, 3);
//
////            TODO: BAD IMPLEMENTATION            
//            if (vehicleCapacity.compareTo(BigDecimal.ZERO) > 0){
//                System.out.println("Check stockroom " + vehicleCapacity);
////                vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, undeliveredStockedPackagesFromCourierCity, 3);
//            }

            
            
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
            
            // Courier is now driving -> status = 1 (drive)
            courierOperationsImpl.changeCourierStatus(courierId, 1); 

            // Fetch one (not so important which) vehicle from stockroom
            Long vehicleId = stockroomOperationsImpl.removeVehicleFromStockroom(stockroomCourierCityId); 
            BigDecimal vehicleCapacity = vehicleOperationsImpl.fetchCapacity(vehicleId); 
            
            // Create current drive and record this drive
            Long driveId = createDrive(courierId, vehicleId, startAddressId);
            logDrive(courierId, vehicleId);
            
            // Phase 1
            // Collect packages from courier's city (first from addresses then from stockroom)
            
            List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity = fetchUndeliveredUnstockedPackagesFromCourierCity(courierCityId);
//            TODO: BAD IMPLEMENTATION            
//            List<ReducedPackage> undeliveredStockedPackagesFromCourierCity = fetchUndeliveredStockedPackagesFromCourierCity(courierCityId);
            
            // Add them to the vehicle (up to the vehicle's maximal weight)
            
            vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, undeliveredUnstockedPackagesFromCourierCity, 0);

//            TODO: BAD IMPLEMENTATION            
            if (vehicleCapacity.compareTo(BigDecimal.ZERO) > 0){
                System.out.println("Check stockroom " + vehicleCapacity);
//                vehicleCapacity = addNextStopsIntoCurrentDrivePlan(driveId, vehicleCapacity, undeliveredStockedPackagesFromCourierCity, 1);
            }
            
            // Phase 2
            // Order of delivery
            addNextStopsAfterPickingUp(driveId, vehicleCapacity, courierCityId);


            // Phase 3
            // Return to stockroom
            // There Vehicle and Packages will be leaved
            int nextPlanPoint = fetchNextPlanPoint(driveId);
            insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, 3, null, startAddressId);            

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

        String updateCurrentDrive = "UPDATE [dbo].[CurrentDrive] " +
                                    "	SET " +
                                    "		IdA = ?, " +
                                    "		CurrentPlanPoint = ?, " +
                                    "		RealizedProfit = RealizedProfit - ? " +
                                    "	WHERE IdCD = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(updateCurrentDrive);) {           

            ps.setLong(1, endAddressId);
            ps.setInt(2, nextPlanPoint);
            // TODO: multiply this with vehicle fuel consumption
            ps.setBigDecimal(3, new BigDecimal(distance));
            ps.setLong(4, currentDriveId);

            int updatedCurrentDriveNumber = ps.executeUpdate();
            if(updatedCurrentDriveNumber == 1)
                return;
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
            
            if(visitReason == 0) {
                // pick up package
                returnValue = -2; // return -2
                updatePackageStatusInPackage(packageId, 2);
                insertPackageInCurrentDrivePackage(currentDriveId, packageId, 0);
            } else if(visitReason == 1) {
                
            } else if(visitReason == 2) {
                // deliver package
                returnValue = packageId.intValue();
                updatePackageStatusInPackage(packageId, 3);
                removePackageFromCurrentDrivePackage(currentDriveId, packageId);
                
            } else if(visitReason == 3) {
                returnValue = -1;
                // TODO: IMPLEMENTS WHEN YOU HAVE TEST EXAMPLES
                //leavePackageToPackageStockroom(currentDriveId, packageId);
                
                // Courier is not driving any more -> status = 1 (not drive)
                courierOperationsImpl.changeCourierStatus(courierId, 0); 
                courierOperationsImpl.changeCourierProfit(courierId);
            }
        
            
            deleteCurrentDrivePlan(planId);
            
            if(visitReason == 3) {
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


}
