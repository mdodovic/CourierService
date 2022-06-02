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
    public boolean deleteCourierRequest(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeDriverLicenceNumberInCourierRequest(String string, String string1) {
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
    public boolean grantRequest(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        System.out.println(listOfUsernames.size()); // 2
        
    }
    

}
