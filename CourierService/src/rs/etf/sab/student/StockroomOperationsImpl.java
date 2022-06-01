/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author matij
 */
public class StockroomOperationsImpl implements StockroomOperations {

    private final Connection connection = DB.getInstance().getConnection();
    

    @Override
    public int insertStockroom(int address) {

        String insertAddressQuery = "INSERT INTO [dbo].[Stockroom] (IdA) " +
                                                            " VALUES (?);  ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertAddressQuery, Statement.RETURN_GENERATED_KEYS);) {           
            ps.setLong(1, address);
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
    public boolean deleteStockroom(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int deleteStockroomFromCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllStockrooms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    public static void main(String[] args) {
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        generalOperations.eraseAll();
        
        CityOperations cityOperations = new CityOperationsImpl();
        int bgId = cityOperations.insertCity("Beograd", "11000");        
        int niId = cityOperations.insertCity("Nis", "700000");        
        
        AddressOperations addressOperations = new AddressOperationsImpl();        
        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 10, 10);
        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 30, 30);        
        int addressNi1Id = addressOperations.insertAddress("Vojvode Stepe", 73, niId, 100, 100);        

        StockroomOperations stockroomOperations = new StockroomOperationsImpl();
        
        int stockroomBg1Id = stockroomOperations.insertStockroom(addressBg1Id);
        System.out.println(stockroomBg1Id);
        int stockroomBg1AgainId = stockroomOperations.insertStockroom(addressBg1Id);
        System.out.println(stockroomBg1AgainId);
        
        
    }
    
}
