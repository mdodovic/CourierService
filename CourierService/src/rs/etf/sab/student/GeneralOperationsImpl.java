/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author matij
 */
public class GeneralOperationsImpl implements GeneralOperations {

    private final Connection connection = DB.getInstance().getConnection();
    
    @Override
    public void eraseAll() {
        
        String eraseAllQuery = "{ call eraseAll() }; ";
        
        try(CallableStatement cs = connection.prepareCall(eraseAllQuery)) {
            
            cs.execute();
            
        } catch (SQLException ex) {
            Logger.getLogger(GeneralOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
