/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import com.sun.istack.internal.NotNull;
import java.sql.Connection;
import java.sql.SQLException;
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
    
    
    
    @Override
    public boolean planingDrive(@NotNull String courierUsername) {
        
        try {
            
            connection.setAutoCommit(false);
            
            Long courierId = userOperationsImpl.fetchUserIdByUsername(courierUsername);
            Long courierCityId = addressOperationsImpl.fetchCityIdOfUser(courierId);
            Long stockroomCourierCityId = stockroomOperationsImpl.fetchStockroomIdByCityId(courierCityId);            

            // Courier is now driving -> status = 1
            courierOperationsImpl.changeCourierStatus(courierId, 1); 

            // Fetch one (not so important which) vehicle from stockroom
//            Long vehicleId = vehicleOperationsImpl.removeVehicleFromStockroom(stockroomCourierCityId); 
//            
//            // Create current drive 
//            createDrive(courierId, vehicleId);
            
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
