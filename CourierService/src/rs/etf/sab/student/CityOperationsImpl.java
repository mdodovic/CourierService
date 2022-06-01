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
import rs.etf.sab.operations.*;

/**
 *
 * @author matij
 */
public class CityOperationsImpl implements CityOperations {
    
    private final Connection connection = DB.getInstance().getConnection();

    @Override
    public int insertCity(@NotNull String name, String postalCode) {
           
        String insertCityQuery = "INSERT INTO [dbo].[City] " +
                                "           (Name, PostalCode) " +
                                "       VALUES (?, ?); ";

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
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return -1;
    }
    
    
       
    @Override
    public int deleteCity(String... names) {
        
        String deleteCityByNameQuery = "DELETE FROM [dbo].[City] " 
                                    + " WHERE Name LIKE ?; ";
        int numberOfDeletedCities = 0;
        try(PreparedStatement ps = connection.prepareStatement(deleteCityByNameQuery);) {           
            for(String cityName: names) {

                ps.setString(1, cityName);
                numberOfDeletedCities += ps.executeUpdate();
                
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return numberOfDeletedCities;
        
    }

    @Override
    public boolean deleteCity(int idCity) {

        String deleteCityByIdQuery = "DELETE FROM [dbo].[City] " 
                                    + " WHERE IdC = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(deleteCityByIdQuery);) {           
            ps.setInt(1, idCity);

            int numberOfDeletedCities = ps.executeUpdate();
            return numberOfDeletedCities != 0;
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return false;
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdCQuery = "SELECT IdC "
                                + " FROM [dbo].[City]; ";
       
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(getAllIdCQuery)){
            
            while(rs.next()){
                listOfIds.add(rs.getInt(1));
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfIds;

    }
    
    public static void main(String[] args) {
        
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        generalOperations.eraseAll();
        
        CityOperations cityOperations = new CityOperationsImpl();
        
        int bgId = cityOperations.insertCity("Beograd", "11000");        
        System.out.println(bgId); // 1

        int vaId = cityOperations.insertCity("Valjevo", "14000");
        System.out.println(vaId); // 2

        int nsId = cityOperations.insertCity("Novi Sad", "21000");    
        System.out.println(nsId); // 3

        int vaBothSameId = cityOperations.insertCity("Valjevo", "14000");
        System.out.println(vaBothSameId); // -1
 
        int vaSamePostalCodeId = cityOperations.insertCity("Valjevo_2", "14000");
        System.out.println(vaSamePostalCodeId); // -1

        int vaSameNameId = cityOperations.insertCity("Valjevo", "14011");
        System.out.println(vaSameNameId); // 4
        
        List<Integer> listOfIdC = cityOperations.getAllCities();
        System.out.println(listOfIdC.size()); // 4
        for (int i: listOfIdC) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.println(cityOperations.deleteCity(vaSameNameId));
        System.out.println(cityOperations.deleteCity(-1));
        
        System.out.println(cityOperations.deleteCity(new String[] {"Beograd", "Novi Sad", "Zrenjanin"}));
        
    }
    
}
