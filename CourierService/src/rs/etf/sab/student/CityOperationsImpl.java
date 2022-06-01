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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author matij
 */
public class CityOperationsImpl implements CityOperations {
    
    private Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertCity(@NotNull String name, String postalCode) {
           
        String insertCityQuery = "INSERT INTO [dbo].[City] (Name, PostalCode) VALUES (?, ?); ";
       
        try(PreparedStatement ps = connection.prepareStatement(insertCityQuery, Statement.RETURN_GENERATED_KEYS);) {           
            ps.setString(1, name);
            ps.setString(2, postalCode);

            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return -1;
    }
    
    
       
    @Override
    public int deleteCity(String... strings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deleteCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllCities() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) {
        CityOperations cityOperations = new CityOperationsImpl();
        
        int bgId = cityOperations.insertCity("Beograd", "11000");
        int vaId = cityOperations.insertCity("Valjevo", "14000");
        
    }
    
}
