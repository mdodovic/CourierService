/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import com.sun.istack.internal.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.DriveOperation;

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
