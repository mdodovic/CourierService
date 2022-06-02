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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author matij
 */
public class CourierRequestOperationsImpl implements CourierRequestOperation {
    
    private final Connection connection = DB.getInstance().getConnection();
    private final UserOperationsImpl userOperations = new UserOperationsImpl();
    
    @Override
    public boolean insertCourierRequest​(@NotNull String userName, @NotNull String driverLicenceNumber) {

        String alreadyCourierQuery = "SELECT IdU " +
                                        "   FROM [dbo].[Courier] " +
                                        "   WHERE IdU = ?; "; 
        
        String declareAdminQuery = "INSERT INTO [dbo].[CourierRequest] " +
                                    "       (DrivingLicenceNumber, IdU) " +
                                    "   VALUES (?, ?); ";
                                    
        try(PreparedStatement psCheck = connection.prepareStatement(alreadyCourierQuery);
            PreparedStatement psInsert = connection.prepareStatement(declareAdminQuery);) {           

            Long userId = userOperations.fetchUserIdByUsername(userName);
            psCheck.setLong(1, userId);
            try(ResultSet rsCheck = psCheck.executeQuery()){
                if(rsCheck.next()) {
                    return false;
                } else {            
                    psInsert.setString(1, driverLicenceNumber);
                    psInsert.setLong(2, userId);

                    int numberOfInsertedCourierRequests = psInsert.executeUpdate();
                    return numberOfInsertedCourierRequests != 0;
                }
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (Exception ex) {
//            Logger.getLogger(UserOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;

    }

    @Override
    public boolean deleteCourierRequest(@NotNull String userName) {

        String deleteCourierRequestByUsernameQuery = "DELETE FROM [dbo].[CourierRequest] " +
                                                "      WHERE IdU = ( " +
                                                "                   SELECT IdU " +
                                                "                       FROM [dbo].[User] " +
                                                "                       WHERE Username = ? " +
                                                "                   ); ";
        
        try(PreparedStatement ps = connection.prepareStatement(deleteCourierRequestByUsernameQuery);) {           
            ps.setString(1, userName);
            int numberOfDeletedCourierRequests = ps.executeUpdate();
            return numberOfDeletedCourierRequests != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;

    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(@NotNull String userName, @NotNull String licencePlateNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllCourierRequests() {

        List<String> listOfUsernames = new ArrayList<String>();
        
        String getAllUsernamesQuery = "SELECT U.Username " +
                                    "       FROM [dbo].[CourierRequest] CR " +
                                    "		INNER JOIN [dbo].[User] U on (CR.IdU = U.IdU); ";
       
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
    public boolean grantRequest(@NotNull String username) {
        
        String insertCourierFromCourierRequestQuery = "INSERT INTO [dbo].[Courier] " +
                                                    "		(IdU, DrivingLicenceNumber) " +
                                                    "       SELECT U.IdU, CR.DrivingLicenceNumber " +
                                                    "		FROM [dbo].[CourierRequest] CR " +
                                                    "			INNER JOIN [dbo].[User] U on (CR.IdU = U.IdU) " +
                                                    "		WHERE U.Username = ?; ";
                                                      
        try(PreparedStatement ps = connection.prepareStatement(insertCourierFromCourierRequestQuery);) {           
            
            connection.setAutoCommit(false);
            
            ps.setString(1, username);
            
            int numberOfNewCouriers = ps.executeUpdate();
            
            boolean deletedCourierRequest = deleteCourierRequest(username);
            
            connection.commit();
            
            return numberOfNewCouriers != 0 && deletedCourierRequest;            
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                Logger.getLogger(CourierRequestOperationsImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(CourierRequestOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;

    }
    
    
    
    
    public static void main(String[] args) {
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        generalOperations.eraseAll();
        
        CityOperations cityOperations = new CityOperationsImpl();
        int bgId = cityOperations.insertCity("Beograd", "11000");        
        int vaId = cityOperations.insertCity("Valjevo", "14000");        
        
        AddressOperations addressOperations = new AddressOperationsImpl();        
        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 10, 10);
        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 30, 30);        
        int addressVa1Id = addressOperations.insertAddress("Petnicka", 20, vaId, 5, 5);        

        UserOperations userOperations = new UserOperationsImpl();
        
        userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_123", addressVa1Id);
        userOperations.insertUser("jcvetkovic", "Jelena", "Cvetkovic", "JecaLepotica.123", addressBg2Id);
        userOperations.insertUser("vdodovic", "Vlado", "Dodovic", "Vlado.123", addressBg1Id);
        
        userOperations.declareAdmin("jcvetkovic");
        
        CourierRequestOperation courierRequestOperation = new CourierRequestOperationsImpl();
        
        System.out.println(courierRequestOperation.insertCourierRequest("mdodovic", "12345678"));
        System.out.println(courierRequestOperation.insertCourierRequest("mdodovic", "12345678")); // same user
        System.out.println(courierRequestOperation.insertCourierRequest("vdodovic", "12345678")); // same driving licence
        System.out.println(courierRequestOperation.insertCourierRequest("mdodovic", "123456781")); // same username
        System.out.println(courierRequestOperation.insertCourierRequest("vdodovic", "87654321"));
        
        List<String> listOfUsernames = courierRequestOperation.getAllCourierRequests();
        System.out.println(listOfUsernames.size()); // 2
        for (String s: listOfUsernames) {
            System.out.println(s);
        }
        
        System.out.println(courierRequestOperation.grantRequest("vdodovic"));
        listOfUsernames = courierRequestOperation.getAllCourierRequests();
        System.out.println(listOfUsernames.size()); // 1
        
    }
    

}
