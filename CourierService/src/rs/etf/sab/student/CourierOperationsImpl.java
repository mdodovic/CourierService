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
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author matij
 */
public class CourierOperationsImpl implements CourierOperations {

    private final Connection connection = DB.getInstance().getConnection();
    private final UserOperationsImpl userOperations = new UserOperationsImpl();

    public void changeCourierStatus(@NotNull Long courierId, @NotNull int newStatus) throws Exception {

        String changeCourierStatusQuery = "UPDATE [dbo].[Courier] " +
                                                "   SET CourierStatus = ? " +
                                                "   WHERE IdU = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(changeCourierStatusQuery);) {           
               
            ps.setInt(1, newStatus);
            ps.setLong(2, courierId);

            int numberOfChangedStatus = ps.executeUpdate();
            if (numberOfChangedStatus != 0)
                return;                 
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        throw new Exception("Error in changing status of the courier: " + courierId + "!");
        
    }
        
    @Override
    public boolean insertCourier(@NotNull String courierUserName, @NotNull String driverLicenceNumber) {

        String insertCourierQuery = "INSERT INTO [dbo].[Courier] " +
                                    "       (IdU, DrivingLicenceNumber)" +
                                    "   VALUES (?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertCourierQuery);) {           
            Long userId = userOperations.fetchUserIdByUsername(courierUserName);
            ps.setLong(1, userId);
            ps.setString(2, driverLicenceNumber);
            
            int numberOfInsertedCouriers = ps.executeUpdate();
            return numberOfInsertedCouriers != 0;
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (Exception ex) {
//            Logger.getLogger(CourierOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;        

    }

    @Override
    public boolean deleteCourier(@NotNull String courierUserName) {

        String deleteCourierByUsernameQuery = "DELETE FROM [dbo].[Courier] " +
                                                "      WHERE IdU = ( " +
                                                "                   SELECT IdU " +
                                                "                       FROM [dbo].[User] " +
                                                "                       WHERE Username = ? " +
                                                "                   ); ";
        
        try(PreparedStatement ps = connection.prepareStatement(deleteCourierByUsernameQuery);) {           
            ps.setString(1, courierUserName);
            int numberOfDeletedCouriers = ps.executeUpdate();
            return numberOfDeletedCouriers != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;

    }

    @Override
    public List<String> getCouriersWithStatus(int statusOfCourier) {
        List<String> listOfUsernames = new ArrayList<String>();
        
        String getAllUsernamesByStatusQuery = "SELECT U.Username " +
                                    "       FROM [dbo].[Courier] C " +
                                    "           INNER JOIN [dbo].[User] U on (C.IdU = U.IdU) " +
                                    "       WHERE C.CourierStatus = ? " +
                                    "       ORDER BY U.Username ASC";
       
        try(PreparedStatement ps = connection.prepareStatement(getAllUsernamesByStatusQuery);) {
            
            ps.setInt(1, statusOfCourier);
            
            try(ResultSet rs = ps.executeQuery()){
            
                while(rs.next()){
                    listOfUsernames.add(rs.getString(1));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfUsernames;
    }

    @Override
    public List<String> getAllCouriers() {
        List<String> listOfUsernames = new ArrayList<String>();
        
        String getAllUsernamesQuery = "SELECT U.Username " +
                                    "       FROM [dbo].[Courier] C " +
                                    "           INNER JOIN [dbo].[User] U on (C.IdU = U.IdU) " +
                                    "       ORDER BY U.Username ASC";
       
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(getAllUsernamesQuery)){
            
            while(rs.next()){
                listOfUsernames.add(rs.getString(1));
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfUsernames;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public static void main(String[] args) {
//        GeneralOperations generalOperations = new GeneralOperationsImpl();
//        generalOperations.eraseAll();
        
//        CityOperations cityOperations = new CityOperationsImpl();
//        int bgId = cityOperations.insertCity("Beograd", "11000");        
//        int vaId = cityOperations.insertCity("Valjevo", "14000");        
//        
//        AddressOperations addressOperations = new AddressOperationsImpl();        
//        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 10, 10);
//        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 30, 30);        
//        int addressVa1Id = addressOperations.insertAddress("Petnicka", 20, vaId, 5, 5);        
//
//        UserOperations userOperations = new UserOperationsImpl();
//        
//        userOperations.insertUser("postar1", "Zika", "Zikic", "Ziki_123", addressVa1Id);
//        userOperations.insertUser("postar2", "Pera", "Peric", "Peki_123", addressBg1Id);
//        
        CourierOperations courierOperation = new CourierOperationsImpl();
//        
//        System.out.println(courierOperation.insertCourier("postar1", "12345678"));
////        System.out.println(courierOperation.insertCourier("postar2", "12345678")); // same licence number 
//        System.out.println(courierOperation.insertCourier("postar2", "23456789"));
        
        List<String> listOfCouriers = courierOperation.getAllCouriers();
        System.out.println(listOfCouriers.size()); // 2
        for (String s: listOfCouriers) {
            System.out.println(s);
        }

        List<String> listOfDrivingCouriers = courierOperation.getCouriersWithStatus(1);
        System.out.println(listOfDrivingCouriers.size()); // 0
        for (String s: listOfDrivingCouriers) {
            System.out.println(s);
        }

        List<String> listOfNonDrivingCouriers = courierOperation.getCouriersWithStatus(0);
        System.out.println(listOfNonDrivingCouriers.size()); // 2
        for (String s: listOfNonDrivingCouriers) {
            System.out.println(s);
        }
        
        System.out.println(courierOperation.deleteCourier("postarBG"));
        
        listOfCouriers = courierOperation.getAllCouriers();
        System.out.println(listOfCouriers.size()); // 2
        for (String s: listOfCouriers) {
            System.out.println(s);
        }
        
        
    }
    
}
