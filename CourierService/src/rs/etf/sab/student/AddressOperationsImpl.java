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
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author matij
 */
public class AddressOperationsImpl implements AddressOperations {

    private final Connection connection = DB.getInstance().getConnection();
    
    @Override
    public int insertAddress(@NotNull String street, int number, int cityId, int xCord, int yCord) {
        
        String insertAddressQuery = "INSERT INTO [dbo].[Address] " +
                                    "       (Street, Number, Xcoord, Ycoord, IdC) \n" +
                                    "   VALUES (?, ?, ?, ?, ?) ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertAddressQuery, Statement.RETURN_GENERATED_KEYS);) {           
            ps.setString(1, street);
            ps.setInt(2, number);
            ps.setInt(3, xCord);
            ps.setInt(4, yCord);
            ps.setLong(5, cityId);

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
    public int deleteAddresses(@NotNull String name, int number) {
        
        String deleteCityByNameAndNumberQuery = "DELETE FROM [dbo].[Address] " +
                    "	WHERE Street LIKE ? " +
                    "       AND Number = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(deleteCityByNameAndNumberQuery);) {           
            ps.setString(1, name);
            ps.setInt(2, number);

            int numberOfDeletedAddresses = ps.executeUpdate();
            return numberOfDeletedAddresses;

        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return 0;        
        
    }

    @Override
    public boolean deleteAdress(int idAddress) {

        String deleteAddressByIdQuery = "DELETE FROM [dbo].[Address] " 
                                        + " WHERE IdA = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(deleteAddressByIdQuery);) {           
            ps.setInt(1, idAddress);

            int numberOfDeletedAddresses = ps.executeUpdate();
            return numberOfDeletedAddresses != 0;
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return false;

    }

    @Override
    public int deleteAllAddressesFromCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllAddresses() {

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdAQuery = "SELECT IdA "
                                + " FROM [dbo].[Address]; ";
       
        try(Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(getAllIdAQuery)){
            
            while(rs.next()){
                listOfIds.add(rs.getInt(1));
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return listOfIds;
    }

    @Override
    public List<Integer> getAllAddressesFromCity(int idCity) {
        
        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdAFromCityQuery = "SELECT IdA "
                                        + " FROM [dbo].[Address] "
                                        + " WHERE IdC = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllIdAFromCityQuery);) {           
            
            ps.setInt(1, idCity);
            try(ResultSet rs = ps.executeQuery()){
                
                while(rs.next()){
                    listOfIds.add(rs.getInt(1));
                }
                if(!listOfIds.isEmpty())
                    return listOfIds;
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return null;
    }
    
    public static void main(String[] args) {

        GeneralOperations generalOperations = new GeneralOperationsImpl();
        generalOperations.eraseAll();
        
        CityOperations cityOperations = new CityOperationsImpl();
        int bgId = cityOperations.insertCity("Beograd", "11000");        
        int vaId = cityOperations.insertCity("Valjevo", "14000");        
        
        AddressOperations addressOperations = new AddressOperationsImpl();
        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 10, 10);
        System.out.println(addressBg1Id);
        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 100, 100);        
        System.out.println(addressBg2Id);
        int addressVa1Id = addressOperations.insertAddress("Petnicka", 20, vaId, 5, 5);        
        System.out.println(addressVa1Id);
        int addressMissingCityId = addressOperations.insertAddress("Koste Abrasevica", 39, -1, 1, 1);        
        System.out.println(addressMissingCityId);
        int sameAddressInDifferentCity = addressOperations.insertAddress("Petnicka", 20, bgId, 5, 5);           
        System.out.println(sameAddressInDifferentCity);
        
        List<Integer> listOfIdA = addressOperations.getAllAddresses();
        System.out.println(listOfIdA.size()); // 4
        for (int i: listOfIdA) {
            System.out.print(i + " ");
        }
        System.out.println();

        List<Integer> listOfVaIdA = addressOperations.getAllAddressesFromCity(vaId);
        System.out.println(listOfVaIdA.size()); // 1
        for (int i: listOfVaIdA) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        List<Integer> listOfMissingCityIdA = addressOperations.getAllAddressesFromCity(-1);
        System.out.println(listOfMissingCityIdA); // null

        int deletedExistingAddressesCount = addressOperations.deleteAddresses("Petnicka", 20);
        System.out.println(deletedExistingAddressesCount);
        int deletedMissingAddressesCount = addressOperations.deleteAddresses("Petnicka", 20);
        System.out.println(deletedMissingAddressesCount);
        System.out.println(addressOperations.deleteAdress(sameAddressInDifferentCity));
        System.out.println(addressOperations.deleteAdress(addressBg1Id));
        
    }
    
}
