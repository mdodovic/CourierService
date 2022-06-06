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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.student.utility.ReducedPackage;

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
    
    public long createDrive(Long courierId, Long vehicleId) throws Exception {

        String createCurrentDriveQuery = "INSERT INTO [dbo].[CurrentDrive] " +
                                        "       (IdU, IdV) " +
                                        "   VALUES (?, ?) ";

        try(PreparedStatement ps = connection.prepareStatement(createCurrentDriveQuery, Statement.RETURN_GENERATED_KEYS);) {           

            ps.setLong(1, courierId);
            ps.setLong(2, vehicleId);
            
            int numberOfDrivesCreated = ps.executeUpdate();
            
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
                                    "       (IdCD, OrdinalVisitNumber, VisitReason, IdP, IdA)" +
                                    "   VALUES (?, ?, ?, ?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertNextStopQuery);) {           
            ps.setLong(1, driveId);
            ps.setInt(2, nextPlanPoint);
            ps.setInt(3, visitReason);
            ps.setLong(4, packageId);
            ps.setLong(5, destinationAddressId);
            
            int numberOfInsertedNextStops = ps.executeUpdate();
            if(numberOfInsertedNextStops == 1)
                return;
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
       
        throw new Exception("Error in inserting next stop!");          
        
    }
    
    private BigDecimal addNextStopsIntoCurrentDrivePlan(Long driveId, BigDecimal vehicleCapacity, List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity, int visitReason) throws Exception {
        
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
                insertNextStopInCurrentDrivingPlan(driveId, nextPlanPoint, visitReason, rp.getPackageId(), destinationAddressId);
                nextPlanPoint += 1;
            }
        }
        
        return spaceLeft;
    }
    
    @Override
    public boolean planingDrive(@NotNull String courierUsername) {
        
        try {
            
            connection.setAutoCommit(false);
            
            Long courierId = userOperationsImpl.fetchUserIdByUsername(courierUsername);
            Long courierCityId = addressOperationsImpl.fetchCityIdOfUser(courierId);
            Long stockroomCourierCityId = stockroomOperationsImpl.fetchStockroomIdByCityId(courierCityId);            
            
            // Courier is now driving -> status = 1 (drive)
            courierOperationsImpl.changeCourierStatus(courierId, 1); 

            // Fetch one (not so important which) vehicle from stockroom
            Long vehicleId = stockroomOperationsImpl.removeVehicleFromStockroom(stockroomCourierCityId); 
            BigDecimal vehicleCapacity = vehicleOperationsImpl.fetchCapacity(vehicleId); 
            
            // Create current drive and record this drive
            Long driveId = createDrive(courierId, vehicleId);
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

    @Override
    public int nextStop(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getPackagesInVehicle(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    


}
