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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author matij
 */
public class VehicleOperationsImpl implements VehicleOperations {

    private final Connection connection = DB.getInstance().getConnection();

    @Override
    public boolean insertVehicle(@NotNull String licencePlateNumber, int fuelType, BigDecimal fuelConsumtion, BigDecimal capacity) {

        
        String insertAddressQuery = "INSERT INTO [dbo].[Vehicle] " +
                                    "       (LicencePlateNumber, FuelType, FuelConsumption, Capacity)" +
                                    "   VALUES (?, ?, ?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertAddressQuery);) {           
            ps.setString(1, licencePlateNumber);
            ps.setInt(2, fuelType);
            ps.setBigDecimal(3, fuelConsumtion);
            ps.setBigDecimal(4, capacity);
            
            int numberOfInsertedVehicles = ps.executeUpdate();
            return numberOfInsertedVehicles != 0;
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return false;        

    }

    @Override
    public int deleteVehicles(@NotNull String... licencePlateNumbers) {
        String deleteVehicelByLicencePlateNumbersQuery = "DELETE FROM [dbo].[Vehicle] " 
                                                        + " WHERE LicencePlateNumber LIKE ?; ";
        int numberOfDeletedVehicles = 0;
        try(PreparedStatement ps = connection.prepareStatement(deleteVehicelByLicencePlateNumbersQuery);) {           
            for(String licencePlateNumber: licencePlateNumbers) {

                ps.setString(1, licencePlateNumber);
                numberOfDeletedVehicles += ps.executeUpdate();
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return numberOfDeletedVehicles;
    }

    @Override
    public List<String> getAllVehichles() {

        List<String> listOfDrivingLicenceNumbers = new ArrayList<String>();
        
        String getAllUsernamesQuery = "SELECT LicencePlateNumber "
                                      + " FROM [dbo].[Vehicle]; ";
       
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(getAllUsernamesQuery)){
            
            while(rs.next()){
                listOfDrivingLicenceNumbers.add(rs.getString(1));
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfDrivingLicenceNumbers;
    }

    @Override
    public boolean changeFuelType(String string, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeCapacity(String string, BigDecimal bd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean parkVehicle(@NotNull String licencePlateNumbers, int idStockroom) {
        // TODO: check if the vehicle is currently used!!!
    }
    
    
    public static void main(String[] args) {
        VehicleOperations vehicleOperations = new VehicleOperationsImpl();
        
        System.out.println(vehicleOperations.insertVehicle("BG1675DA", 1, new BigDecimal(6.3D), new BigDecimal(100.5D)));
//        System.out.println(vehicleOperations.insertVehicle("BG1675DA", 1, new BigDecimal(6.3D), new BigDecimal(100.5D))); // same plate number
//        System.out.println(vehicleOperations.insertVehicle("VA119RT", 3, new BigDecimal(6.3D), new BigDecimal(100.5D))); // fuel type out of range

        
        List<String> listOfVehicles = vehicleOperations.getAllVehichles();
        System.out.println(listOfVehicles.size()); // 1
        for (String s: listOfVehicles) {
            System.out.println(s);
        }

    }
    
}