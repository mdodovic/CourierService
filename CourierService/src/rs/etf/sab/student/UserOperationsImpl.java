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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author matij
 */
public class UserOperationsImpl implements UserOperations {

    private final Connection connection = DB.getInstance().getConnection();
    
    private void checkPassword(String password) throws Exception {
        String regex = "^(?=.*[0-9])"
                       + "(?=.*[a-z])(?=.*[A-Z])"
                       + "(?=.*[_@#$%^&+=])"
                       + "(?=\\S+$).{8,20}$";
  
        Pattern p = Pattern.compile(regex);  
        Matcher m = p.matcher(password);
        if(!m.matches())
            throw new Exception("Bad password!");
    }
    
    @Override
    public boolean insertUser(@NotNull String userName, @NotNull String firstName, @NotNull String lastName, @NotNull String password, @NotNull int idAddress) {
        
        String insertAddressQuery = "INSERT INTO [dbo].[User] " +
                                    "       (Firstname, Lastname, Username, Password, IdA) \n" +
                                    "   VALUES (?, ?, ?, ?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertAddressQuery);) {           
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userName);
            checkPassword(password);
            ps.setString(4, password);
            ps.setInt(5, idAddress);
            
            int numberOfInsertedUsers = ps.executeUpdate();
            return numberOfInsertedUsers != 0;
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (Exception ex) {
//            Logger.getLogger(UserOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;        
        
    }

    @Override
    public boolean declareAdmin(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSentPackages(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int deleteUsers(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<String> getAllUsers() {

        List<String> listOfUsernames = new ArrayList<String>();
        
        String getAllUsernamesQuery = "SELECT Username "
                                + " FROM [dbo].[User]; ";
       
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
        

        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_123", addressVa1Id));
        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_123", addressVa1Id)); // same username
        System.out.println(userOperations.insertUser("vdodovic", "Vlado", "Dodovic", "Vlado.123", -1)); // invalid address
        
        System.out.println(userOperations.insertUser("user1", "vlado", "Dodovic", "Vlado.123", addressBg1Id)); // firstname not capital
        System.out.println(userOperations.insertUser("user2", "Vlado", "dodovic", "Vlado.123", addressBg1Id)); // lastname not capital

        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_123", addressVa1Id)); // password fail
        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata123", addressVa1Id)); // password fail
        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "mata_123", addressVa1Id)); // password fail
        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_----", addressVa1Id)); // password fail
        System.out.println(userOperations.insertUser("mdodovic", "Matija", "Dodovic", "M123s", addressVa1Id)); // password fail
        
        List<String> listOfUsernames = userOperations.getAllUsers();
        System.out.println(listOfUsernames.size()); // 1
        for (String s: listOfUsernames) {
            System.out.println(s);
        }
        
    }
}
