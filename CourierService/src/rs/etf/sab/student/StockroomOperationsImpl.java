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
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.StockroomOperations;

/**
 *
 * @author matij
 */
public class StockroomOperationsImpl implements StockroomOperations {

    private final Connection connection = DB.getInstance().getConnection();

    
    public Long removeVehicleFromStockroom(Long stockroomCourierCityId) throws Exception {

        String fetchVehicleFromStockroomByStockroomIdQuery = "select IdVS, IdV " +
                                                            "	from VehicleStockroom VS " +
                                                            "	WHERE VS.IdS = ?; ";
        
        String deleteVehicleFromStockroomByStockroomIdQuery = "DELETE FROM [dbo].[VehicleStockroom] " 
                                                            + " WHERE IdVS = ?; ";
        

        try(PreparedStatement psFetch = connection.prepareStatement(fetchVehicleFromStockroomByStockroomIdQuery);
            PreparedStatement psDelete = connection.prepareStatement(deleteVehicleFromStockroomByStockroomIdQuery);) {           

            psFetch.setLong(1, stockroomCourierCityId);
            
            try(ResultSet rsFetch = psFetch.executeQuery()){
                if(rsFetch.next()) {
                    Long vehicleStockroomId = rsFetch.getLong(1);
                    Long vehicleId = rsFetch.getLong(2);
                    
                    psDelete.setLong(1, vehicleStockroomId);
                    int numberOfDeletedVehicleStockrooms = psDelete.executeUpdate();
                    if (numberOfDeletedVehicleStockrooms == 1)
                        return vehicleId;                       
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Error in removing vehicle from VehicleStockroom (id: " + stockroomCourierCityId + ")!");
        
    }
    

    
    public long fetchStockroomIdByCityId(@NotNull long cityId) throws Exception {
 
        String getStockroomByCityIdQuery = "SELECT S.IdS " +
                                            "	FROM [dbo].[Address] A " +
                                            "       INNER JOIN [dbo].[Stockroom] S on (A.IdA = S.IdA) " +
                                            "	WHERE A.IdC = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(getStockroomByCityIdQuery);) {           
            
            ps.setLong(1, cityId);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                        return rs.getLong(1);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Stockroom with city id: " + cityId + " does not exists!");
        
    }
    
    public Long fetchAddressIdByStockroom(Long stockroomCourierCityId) throws Exception {
        
        String getStockroomByCityIdQuery = "SELECT IdA " +
                                            "	FROM [dbo].[Stockroom] " +
                                            "	WHERE IdS = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(getStockroomByCityIdQuery);) {           
            
            ps.setLong(1, stockroomCourierCityId);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                        return rs.getLong(1);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        throw new Exception("Stockroom with id: " + stockroomCourierCityId + " does not exists!");
        
    }
    

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
    public boolean deleteStockroom(int idStockroom) {
        
//        String emptyStockroomCheckQuery = "SELECT IdS " +
//                                        "	FROM [dbo].[PackageStockroom] " +
//                                        "	WHERE IdS = ? " +
//                                        "   UNION " +
//                                        "  SELECT IdS " +
//                                        "	FROM [dbo].[VehicleStockroom] " +
//                                        "	WHERE IdS = ?; ";

        String deleteStockroomByIdQuery = "DELETE FROM [dbo].[Stockroom] " 
                                        + " WHERE IdS = ?; ";
        

        try(/*PreparedStatement psCheck = connection.prepareStatement(emptyStockroomCheckQuery);           */
            PreparedStatement psDelete = connection.prepareStatement(deleteStockroomByIdQuery);) {           
//            psCheck.setLong(1, idStockroom);
//            psCheck.setLong(2, idStockroom);
            
//            try(ResultSet rsCheck = psCheck.executeQuery()){
//                if(rsCheck.next()) {
//                    return false;
//                } else {            
                    psDelete.setLong(1, idStockroom);
                    int numberOfDeletedStockrooms = psDelete.executeUpdate();
                    return numberOfDeletedStockrooms != 0;                                        
//                }
//            }
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;
        
    }

    @Override
    public int deleteStockroomFromCity(int idCity) {
        
        String getStockroomByCityIdQuery = "SELECT S.IdS " +
                                            "	FROM [dbo].[Address] A " +
                                            "       INNER JOIN [dbo].[Stockroom] S on (A.IdA = S.IdA) " +
                                            "	WHERE A.IdC = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(getStockroomByCityIdQuery);) {           
            
            ps.setInt(1, idCity);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                    int IdS = rs.getInt(1);
                    if(deleteStockroom(IdS)) {
                        return IdS;
                    }
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return -1;
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
        int stockroomVaId = stockroomOperations.insertStockroom(addressVa1Id);
        System.out.println(stockroomVaId);
        
        List<Integer> listOfIdS = stockroomOperations.getAllStockrooms();
        System.out.println(listOfIdS.size()); // 4
        for (int i: listOfIdS) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.println(stockroomOperations.deleteStockroom(stockroomVaId));
        System.out.println(stockroomOperations.deleteStockroom(stockroomVaId));
        
        System.out.println(stockroomOperations.deleteStockroomFromCity(bgId));
        System.out.println(stockroomOperations.deleteStockroomFromCity(bgId));
        
        
    }


}
