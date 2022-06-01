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
import java.util.ArrayList;
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
        
        String stockroomInCityQuery = "SELECT S.IdS " +
                                      "     FROM [dbo].[Stockroom] S " +
                                      "         INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA) " +
                                      "     WHERE A.IdC = ( " +
                                      "                     SELECT C.IdC " +
                                      "                         FROM [dbo].[City] C " +
                                      "                             INNER JOIN [dbo].[Address] A on (C.IdC = A.IdC) " +
                                      "                         WHERE A.IdA = ? " +
                                      "                   ) ;"; 
        
        String insertAddressQuery = "INSERT INTO [dbo].[Stockroom] (IdA) " +
                                                            " VALUES (?);  ";
        
        try(PreparedStatement psCheck = connection.prepareStatement(stockroomInCityQuery);           
            PreparedStatement psInsert = connection.prepareStatement(insertAddressQuery, Statement.RETURN_GENERATED_KEYS);) {           
            psCheck.setLong(1, address);
            try(ResultSet rsCheck = psCheck.executeQuery()){
                if(rsCheck.next()) {
                    return -1;
                } else {            
                    psInsert.setLong(1, address);
                    psInsert.executeUpdate();

                    try(ResultSet rsInsert = psInsert.getGeneratedKeys()){
                        if(rsInsert.next()) {
                            return rsInsert.getInt(1);
                        }
                    }
                }
            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
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

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdCQuery = "SELECT IdS "
                                + " FROM [dbo].[Stockroom]; ";
       
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
        int vaId = cityOperations.insertCity("Valjevo", "14000");        
        
        AddressOperations addressOperations = new AddressOperationsImpl();        
        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 10, 10);
        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 30, 30);        
        int addressVa1Id = addressOperations.insertAddress("Petnicka", 20, vaId, 5, 5);        

        StockroomOperations stockroomOperations = new StockroomOperationsImpl();
        
        int stockroomBgId = stockroomOperations.insertStockroom(addressBg1Id);
        System.out.println(stockroomBgId);
        int stockroomBg1AgainId = stockroomOperations.insertStockroom(addressBg1Id);
        System.out.println(stockroomBg1AgainId);
        int stockroomBgAgainId = stockroomOperations.insertStockroom(addressBg2Id);
        System.out.println(stockroomBgAgainId);
//        int stockroomVaId = stockroomOperations.insertStockroom(addressVa1Id);
//        System.out.println(stockroomVaId);
        
        List<Integer> listOfIdS = stockroomOperations.getAllStockrooms();
        System.out.println(listOfIdS.size()); // 4
        for (int i: listOfIdS) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        
    }
    
}
