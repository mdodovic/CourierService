/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import com.sun.istack.internal.NotNull;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.*;

/**
 *
 * @author matij
 */
public class PackageOperationsImpl implements PackageOperations {
    
    private final Connection connection = DB.getInstance().getConnection();

    private final UserOperationsImpl userOperationsImpl = new UserOperationsImpl();
    
    @Override
    public int insertPackage(int addressFrom, int addressTo, @NotNull String userName, int packageType, BigDecimal weight) {
        
        String insertPackageQuery = "INSERT INTO [dbo].[Package] " +
                                "           (IdStartAddress, IdEndAddress, IdU, PackageType";
        
        if (weight != null) {
            insertPackageQuery += ", Weight";
        }
        insertPackageQuery += ") VALUES (?, ?, ?, ?";  
        if (weight != null) {
            insertPackageQuery += ", ?";
        }                
        insertPackageQuery += "); ";
        
        try(PreparedStatement ps = connection.prepareStatement(insertPackageQuery, Statement.RETURN_GENERATED_KEYS);) {           
            Long userId = userOperationsImpl.fetchUserIdByUsername(userName);
            ps.setInt(1, addressFrom);
            ps.setInt(2, addressTo);
            ps.setLong(3, userId);
            ps.setInt(4, packageType);
            if (weight != null) {
                ps.setBigDecimal(5, weight);
            }

            ps.executeUpdate();
            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        } catch (Exception ex) {
            Logger.getLogger(PackageOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
 


    }

    @Override
    public boolean acceptAnOffer(int packageId) {
        
        String changePackageStatusToAcceptedQuery = "UPDATE [dbo].[Package] " +
                                                "	SET PackageStatus = 1, " +
                                                "           AcceptRejectTime = ? " +
                                                "	WHERE PackageStatus = 0 " +
                                                "		AND IdP = ?";
        
        try(PreparedStatement ps = connection.prepareStatement(changePackageStatusToAcceptedQuery);) {           
               
            ps.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.setInt(2, packageId);

            int numberOfAcceptedOffers = ps.executeUpdate();
            return numberOfAcceptedOffers != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;
    }

    @Override
    public boolean rejectAnOffer(int packageId) {
        String changePackageStatusToRejectedQuery = "UPDATE [dbo].[Package] " +
                                                "	SET PackageStatus = 4, " +
                                                "           AcceptRejectTime = ? " +
                                                "	WHERE PackageStatus = 0 " +
                                                "		AND IdP = ?";
        
        try(PreparedStatement ps = connection.prepareStatement(changePackageStatusToRejectedQuery);) {           

            ps.setTimestamp(1, new Timestamp(Calendar.getInstance().getTimeInMillis()));
            ps.setInt(2, packageId);

            int numberOfAcceptedOffers = ps.executeUpdate();
            return numberOfAcceptedOffers != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;

    }

    @Override
    public List<Integer> getAllPackages() {

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdAQuery = "SELECT IdP "
                                + " FROM [dbo].[Package]; ";
       
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
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deletePackage(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean changeType(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDeliveryStatus(int packageId) {
        
        String getPackageDeliveryStatusQuery = "SELECT PackageStatus " +
                                                "   FROM [dbo].[Package] " +
                                                "   WHERE IdP = ?; ";
        int packageStatus = -1;
        try(PreparedStatement ps = connection.prepareStatement(getPackageDeliveryStatusQuery);) {           
            
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                    packageStatus = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return packageStatus;
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCurrentLocationOfPackage(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Date getAcceptanceTime(int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) {
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        generalOperations.eraseAll();
        
        CityOperations cityOperations = new CityOperationsImpl();
        int bgId = cityOperations.insertCity("Beograd", "11000");        
        int vaId = cityOperations.insertCity("Valjevo", "14000");        
        
        AddressOperations addressOperations = new AddressOperationsImpl();        
        int addressBg1Id = addressOperations.insertAddress("Bulevar kralja Aleksandra", 73, bgId, 0, 0);
        int addressBg2Id = addressOperations.insertAddress("Kraljice Natalije", 37, bgId, 12, 12);        
        int addressVa1Id = addressOperations.insertAddress("Petnicka", 20, vaId, 60, 60);        

        UserOperations userOperations = new UserOperationsImpl();
        
        userOperations.insertUser("postar1", "Zika", "Zikic", "Ziki_123", addressVa1Id);
        userOperations.insertUser("mdodovic", "Matija", "Dodovic", "Mata_123", addressBg1Id);
        userOperations.insertUser("gdodovic", "Gordana", "Dodovic", "Goca_123", addressVa1Id);
        userOperations.insertUser("vdodovic", "Vlado", "Dodovic", "Vlado.123", addressVa1Id);
        
        CourierOperations courierOperation = new CourierOperationsImpl();        
        courierOperation.insertCourier("postar1", "12345678");
        
        PackageOperations packageOperations = new PackageOperationsImpl();
           
        int package1 = packageOperations.insertPackage(addressBg1Id, addressVa1Id, "mdodovic", 0, new BigDecimal(2));
        System.out.println(package1);
        int package2 = packageOperations.insertPackage(addressBg2Id, addressVa1Id, "mdodovic", 3, new BigDecimal(111));
        System.out.println(package2);
        int package3 = packageOperations.insertPackage(addressBg1Id, addressBg2Id, "mdodovic", 0, null);
        System.out.println(package3);
        int package4 = packageOperations.insertPackage(addressVa1Id, addressBg2Id, "mdodovic", 1, new BigDecimal(3.4D));
        System.out.println(package3);

        int package5 = packageOperations.insertPackage(addressBg1Id, addressBg2Id, "vdodovic", 2, null);
        System.out.println(package3);
        int package6 = packageOperations.insertPackage(addressBg1Id, addressBg2Id, "vdodovic", 2, null);
        System.out.println(package3);
        
        List<Integer> listOfPackagesId = packageOperations.getAllPackages();
        System.out.println(listOfPackagesId.size()); // 6
        for (int i: listOfPackagesId) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.println(packageOperations.acceptAnOffer(package1));
        System.out.println(packageOperations.acceptAnOffer(package1)); // not in status 0
        System.out.println(packageOperations.rejectAnOffer(package2));
        System.out.println(packageOperations.rejectAnOffer(package2)); // not in status 0
        System.out.println(packageOperations.acceptAnOffer(package3));
        System.out.println(packageOperations.acceptAnOffer(package5));
        
        System.out.println(packageOperations.getDeliveryStatus(package1));
        System.out.println(packageOperations.getDeliveryStatus(package2));
        System.out.println(packageOperations.getDeliveryStatus(package3));
        System.out.println(packageOperations.getDeliveryStatus(package4));
        System.out.println(packageOperations.getDeliveryStatus(package5));
        System.out.println(packageOperations.getDeliveryStatus(package6));


//        List<Integer> listOfVaIdA = addressOperations.getAllAddressesFromCity(vaId);
//        System.out.println(listOfVaIdA.size()); // 1
//        for (int i: listOfVaIdA) {
//            System.out.print(i + " ");
//        }
//        System.out.println();

        
    }
    
}
