/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import com.sun.istack.internal.NotNull;
import java.sql.Connection;
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
    private final VehicleOperationsImpl vehicleOperationsImpl = new VehicleOperationsImpl();
    private final AddressOperationsImpl addressOperationsImpl = new AddressOperationsImpl();
    
    @Override
    public boolean planingDrive(@NotNull String courierUsername) {
        
        try {

            Long courierId = userOperationsImpl.fetchUserIdByUsername(courierUsername);
            Long courierCityId = addressOperationsImpl.fetchCityIdOfUser(courierId);

            System.out.println("rs.etf.sab.student.DriveOperationImpl.planingDrive()");

        } catch (Exception ex) {
            Logger.getLogger(DriveOperationImpl.class.getName()).log(Level.SEVERE, null, ex);
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
