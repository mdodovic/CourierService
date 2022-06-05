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
        
        String fetchAllUndeliveredStockedPackagesFromCourierCityQuery = 
                "SELECT P.IdP AS IdP, P.IdStartAddress AS StartAddress, " + 
                    " P.IdEndAddress AS EndAddress, P.Weight AS Weight, " + 
                    " P.Price AS Price, SA.IdC AS StartCity, EA.IdC AS EndCity " +
                    "	FROM [dbo].[Package] P " +
                    "		INNER JOIN [dbo].[Address] SA on (P.IdStartAddress = SA.IdA) " +
                    "		INNER JOIN [dbo].[Address] EA on (P.IdEndAddress = EA.IdA) " +
                    "	WHERE SA.IdC = ? " +
                    "		AND PackageStatus = 1 " +
                    "		AND P.IdP IN (" +
                    "				  SELECT IdP " +
                    "                                 FROM PackageStockroom " +
                    "                            ) " +
                    "	ORDER BY P.CreateTime, P.Weight; ";
                
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
            
            // Create current drive and record this drive
            Long driveId = createDrive(courierId, vehicleId);
            logDrive(courierId, vehicleId);
            
            // Phase 1
            // Collect packages from courier's city (first from addresses then from stockroom)
            
            List<ReducedPackage> undeliveredUnstockedPackagesFromCourierCity = fetchUndeliveredUnstockedPackagesFromCourierCity(courierCityId);
            List<ReducedPackage> undeliveredStockedPackagesFromCourierCity = fetchUndeliveredStockedPackagesFromCourierCity(courierCityId);
            
            
            
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
