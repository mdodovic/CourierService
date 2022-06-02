/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.Connection;
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
public class CourierRequestOperationImpl implements CourierRequestOperation {
    
    private final Connection connection = DB.getInstance().getConnection();
    
    @Override
    public boolean insertCourierRequest(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        
    }
    

}
