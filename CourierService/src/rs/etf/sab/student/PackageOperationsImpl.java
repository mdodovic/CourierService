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
    public List<Integer> getAllPackagesWithSpecificType(int type) {

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdPByTypeQuery = "SELECT IdP "
                                        + " FROM [dbo].[Package] "
                                        + " WHERE PackageType = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllIdPByTypeQuery);) {           
            
            ps.setInt(1, type);
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

    @Override
    public List<Integer> getAllUndeliveredPackages() {

        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllIdAQuery = "SELECT IdP "
                                + " FROM [dbo].[Package]"
                                + " WHERE PackageStatus IN (1, 2); ";
       
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
    public List<Integer> getAllUndeliveredPackagesFromCity(int cityId) {
        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllUndeliveredIdPSendFromCityQuery = "SELECT P.IdP " +
                                                        "   FROM [dbo].[Package] P " +
                                                        "       INNER JOIN [dbo].[Address] A ON (P.IdStartAddress = A.IdA) " +
                                                        "   WHERE P.PackageStatus IN (1, 2) " +
                                                        "           AND A.IdC = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllUndeliveredIdPSendFromCityQuery);) {           
            
            ps.setInt(1, cityId);
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

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int cityId) {
        // TODO: CHECK THIS!!! (2nd and 3rd query are not tested yet)
        List<Integer> listOfIds = new ArrayList<Integer>();
        
        String getAllAcceptedIdPFromCityQuery = "SELECT P.IdP " +
                                                "	FROM [dbo].[Package] P " +
                                                "		INNER JOIN [dbo].[Address] A ON (P.IdStartAddress = A.IdA) " +
                                                "	WHERE P.PackageStatus = 1 " +
                                                "		AND A.IdC = ?; ";
        
        try(PreparedStatement ps = connection.prepareStatement(getAllAcceptedIdPFromCityQuery);) {                       
            ps.setInt(1, cityId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    listOfIds.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        String getAllStockedIdPFromCityQuery = "SELECT P.IdP " +
                                                "	FROM [dbo].[Package] P " +
                                                "		INNER JOIN [dbo].[PackageStockroom] PS ON (P.IdP = PS.IdP) " +
                                                "		INNER JOIN [dbo].[Stockroom] S ON (S.IdS = PS.IdS) " +
                                                "		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA) " +
                                                "	WHERE P.PackageStatus = 2 " +
                                                "			AND A.IdC = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllStockedIdPFromCityQuery);) {                       
            ps.setInt(1, cityId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    listOfIds.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        String getAllDeliveredIdPFromCityQuery = "SELECT P.IdP " +
                                                "	FROM [dbo].[Package] P " +
                                                "		INNER JOIN [dbo].[Address] A ON (P.IdEndAddress = A.IdA) " +
                                                "	WHERE P.PackageStatus = 3 " +
                                                "		AND A.IdC = ?; ";

        try(PreparedStatement ps = connection.prepareStatement(getAllDeliveredIdPFromCityQuery);) {                       
            ps.setInt(1, cityId);
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    listOfIds.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return listOfIds;
        
    }

    @Override
    public boolean deletePackage(int packageId) {

        String deletePackageQuery = "DELETE FROM [dbo].[Package] " +
                                                    "	WHERE IdP = ? " +
                                                    "       AND PackageStatus IN (0, 4); ";
        
        try(PreparedStatement ps = connection.prepareStatement(deletePackageQuery);) {
            ps.setInt(1, packageId);
            int numberOfDeletedPackages = ps.executeUpdate();
            return numberOfDeletedPackages != 0;                                        
            
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;

    }

    @Override
    public boolean changeWeight(int packageId, @NotNull BigDecimal newWeight) {
        String changeTypeQuery = "UPDATE [dbo].[Package] " +
                                                "   SET Weight = ? " +
                                                "   WHERE IdP = ? " +
                                                "       AND PackageStatus = 0; ";
        
        try(PreparedStatement ps = connection.prepareStatement(changeTypeQuery);) {           
               
            ps.setBigDecimal(1, newWeight);
            ps.setInt(2, packageId);

            int numberOfChangedTypes = ps.executeUpdate();
            return numberOfChangedTypes != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;
    }

    @Override
    public boolean changeType(int packageId, int newType) {

        String changeTypeQuery = "UPDATE [dbo].[Package] " +
                                                "   SET PackageType = ? " +
                                                "   WHERE IdP = ? " +
                                                "       AND PackageStatus = 0; ";
        
        try(PreparedStatement ps = connection.prepareStatement(changeTypeQuery);) {           
               
            ps.setInt(1, newType);
            ps.setInt(2, packageId);

            int numberOfChangedTypes = ps.executeUpdate();
            return numberOfChangedTypes != 0;                                        
            
        } catch (SQLException ex) {
            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return false;
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
    public BigDecimal getPriceOfDelivery(int packageId) {
        
        String getPackageDeliveryStatusQuery = "SELECT Price " +
                                                "   FROM [dbo].[Package] " +
                                                "   WHERE IdP = ?; ";
        BigDecimal price = null;
        try(PreparedStatement ps = connection.prepareStatement(getPackageDeliveryStatusQuery);) {           
            
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){
                
                if(rs.next()){
                    price = rs.getBigDecimal(1);
                }
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }
        return price;
    }

    @Override
    public int getCurrentLocationOfPackage(int packageId) {

        int packageStatus = getDeliveryStatus(packageId);
        
        String getLocationOfThePackageQuery = null;
        
        switch (packageStatus) {
            case 1:
                // Package is accepted, it is at the start address
                getLocationOfThePackageQuery = "SELECT A.IdC " +
                                            "	FROM [dbo].[Package] P " +
                                            "       INNER JOIN [dbo].[Address] A ON (P.IdStartAddress = A.IdA) " +
                                            "	WHERE P.PackageStatus = 1 " +
                                            "           AND P.IdP = ?; ";
                break;
            case 2:
                // Package is picked up
                // It is either in the CurrentDrivePackage (currently in the van)
                // or it is in PackageStockroom (stored in stockroom for the next picking up)
                getLocationOfThePackageQuery = "SELECT A.IdC " +
                                            "	FROM [dbo].[Package] P " +
                                            "		INNER JOIN [dbo].[PackageStockroom] PS ON (P.IdP = PS.IdP) " +
                                            "		INNER JOIN [dbo].[Stockroom] S ON (S.IdS = PS.IdS) " +
                                            "		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA) " +
                                            "	WHERE P.PackageStatus = 2 " +
                                            "			AND P.IdP = ?; ";
                break;
            case 3:
                // Package is deliveres, it is at the end address
                getLocationOfThePackageQuery = "SELECT A.IdC " +
                                            "	FROM [dbo].[Package] P " +
                                            "       INNER JOIN [dbo].[Address] A ON (P.IdEndAddress = A.IdA) " +
                                            "	WHERE P.PackageStatus = 3 " +
                                            "           AND P.IdP = ?; ";
                break;
            default:
                break;
        }
        
        
        try(PreparedStatement ps = connection.prepareStatement(getLocationOfThePackageQuery);) {           
            
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){                
                if(rs.next()){
                    return rs.getInt(1);
                }                
            }
        } catch (SQLException ex) {
//            Logger.getLogger(CityOperationsImpl.class.getName()).log(Level.SEVERE, null, ex);            
        }

        return -1;
    }

    @Override
    public Date getAcceptanceTime(int packageId) {

        String getAcceptanceTimeQuery = "SELECT AcceptRejectTime " +
                                        "	FROM [dbo].[Package] " +
                                        "	WHERE IdP = ? " +
                                        "		AND PackageStatus IN (1, 2, 3); ";
        
        try(PreparedStatement ps = connection.prepareStatement(getAcceptanceTimeQuery);) {           
            
            ps.setInt(1, packageId);
            try(ResultSet rs = ps.executeQuery()){                
                if(rs.next()){
                    return rs.getDate(1);
                }
                
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

        int package5 = packageOperations.insertPackage(addressVa1Id, addressBg2Id, "vdodovic", 2, null);
        System.out.println(package3);
        int package6 = packageOperations.insertPackage(addressBg1Id, addressBg2Id, "vdodovic", 2, null);
        System.out.println(package3);
                
        System.out.println(packageOperations.acceptAnOffer(package1));
        System.out.println(packageOperations.acceptAnOffer(package1)); // not in status 0
        System.out.println(packageOperations.rejectAnOffer(package2));
        System.out.println(packageOperations.rejectAnOffer(package2)); // not in status 0
        System.out.println(packageOperations.acceptAnOffer(package3));
        System.out.println(packageOperations.acceptAnOffer(package5));

        List<Integer> listOfPackagesId = packageOperations.getAllPackages();
        System.out.println(listOfPackagesId.size()); // 6
        for (int i: listOfPackagesId) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.print(packageOperations.getDeliveryStatus(package1) + " "); // 1
        System.out.print(packageOperations.getDeliveryStatus(package2) + " "); // 4
        System.out.print(packageOperations.getDeliveryStatus(package3) + " "); // 1
        System.out.print(packageOperations.getDeliveryStatus(package4) + " "); // 0
        System.out.print(packageOperations.getDeliveryStatus(package5) + " "); // 1
        System.out.println(packageOperations.getDeliveryStatus(package6) + " "); // 0
                
        System.out.println(packageOperations.getPriceOfDelivery(package1) + " " + packageOperations.getAcceptanceTime(package1));
        System.out.println(packageOperations.getPriceOfDelivery(package2) + " " + packageOperations.getAcceptanceTime(package2));
        System.out.println(packageOperations.getPriceOfDelivery(package3) + " " + packageOperations.getAcceptanceTime(package3));
        System.out.println(packageOperations.getPriceOfDelivery(package4) + " " + packageOperations.getAcceptanceTime(package4));
        System.out.println(packageOperations.getPriceOfDelivery(package5) + " " + packageOperations.getAcceptanceTime(package5));
        System.out.println(packageOperations.getPriceOfDelivery(package6) + " " + packageOperations.getAcceptanceTime(package6));

        System.out.println(packageOperations.changeType(package6, 1));
        System.out.println(packageOperations.getPriceOfDelivery(package6));

        System.out.println(packageOperations.changeWeight(package6, new BigDecimal(5.2D)));
        System.out.println(packageOperations.getPriceOfDelivery(package6));

        System.out.println(packageOperations.changeType(package1, 0));
        System.out.println(packageOperations.changeType(package2, 0));

        List<Integer> listOfIdP1 = packageOperations.getAllPackagesWithSpecificType(1);
        System.out.println(listOfIdP1.size()); // 2
        for (int i: listOfIdP1) {
            System.out.print(i + " ");
        }
        System.out.println();

        List<Integer> listOfIdP0 = packageOperations.getAllPackagesWithSpecificType(0);
        System.out.println(listOfIdP0.size()); // 2
        for (int i: listOfIdP0) {
            System.out.print(i + " ");
        }
        System.out.println();

        List<Integer> listOfIdPUndelivered = packageOperations.getAllUndeliveredPackages();
        System.out.println(listOfIdPUndelivered.size()); // 3
        for (int i: listOfIdPUndelivered) {
            System.out.print(i + " ");
        }
        System.out.println();

        List<Integer> listOfBgPackages = packageOperations.getAllPackagesCurrentlyAtCity(bgId);
        System.out.println(listOfBgPackages.size()); // 2
        for (int i: listOfBgPackages) {
            System.out.print(i + " ");
        }
        System.out.println();

        List<Integer> listOfVaPackages = packageOperations.getAllPackagesCurrentlyAtCity(vaId);
        System.out.println(listOfVaPackages.size()); // 1
        for (int i: listOfVaPackages) {
            System.out.print(i + " ");
        }
        System.out.println();
        
        System.out.println(bgId + " " + vaId);
        System.out.println(packageOperations.getCurrentLocationOfPackage(package1)); // bg
        System.out.println(packageOperations.getCurrentLocationOfPackage(package3)); // bg
        System.out.println(packageOperations.getCurrentLocationOfPackage(package5)); // bg
        
        
    }
    
}
