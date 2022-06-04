/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import rs.etf.sab.operations.AddressOperations;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DriveOperation;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.StockroomOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author matij
 */
public class StudentTest {
    
    private AddressOperations addressOperations = new AddressOperationsImpl();
    private CityOperations cityOperations = new CityOperationsImpl();
    private CourierOperations courierOperations = new CourierOperationsImpl();
    private CourierRequestOperation courierRequestOperation = new CourierRequestOperationsImpl();
    private DriveOperation driveOperation = null;
    private GeneralOperations generalOperations = new GeneralOperationsImpl();
    private PackageOperations packageOperations = new PackageOperationsImpl();
    private StockroomOperations stockroomOperations = new StockroomOperationsImpl();
    private UserOperations userOperations = new UserOperationsImpl();
    private VehicleOperations vehicleOperations = new VehicleOperationsImpl();
    
    int insertCity(String name, String postalCode) {
        int idCity = this.cityOperations.insertCity(name, postalCode);
        return idCity;
    }

    Map<Integer, Pair<Integer, Integer>> addressesCoords = new HashMap<>();
    
    int insertAddress(String street, int number, int idCity, int x, int y) {
        int idAddress = this.addressOperations.insertAddress(street, number, idCity, x, y);
        this.addressesCoords.put(Integer.valueOf(idAddress), new Pair<>(Integer.valueOf(x), Integer.valueOf(y)));
        return idAddress;
    }

    String insertUser(String username, String firstName, String lastName, String password, int idAddress) {
        this.userOperations.insertUser(username, firstName, lastName, password, idAddress);
        return username;
    }    

    String insertCourier(String username, String firstName, String lastName, String password, int idAddress, String driverLicenceNumber) {
        insertUser(username, firstName, lastName, password, idAddress);
        this.courierOperations.insertCourier(username, driverLicenceNumber);
        return username;
    }

    public void insertAndParkVehicle(String licencePlateNumber, BigDecimal fuelConsumption, BigDecimal capacity, int fuelType, int idStockroom) {
        this.vehicleOperations.insertVehicle(licencePlateNumber, fuelType, fuelConsumption, capacity);
        this.vehicleOperations.parkVehicle(licencePlateNumber, idStockroom);
    }
  
    public int insertStockroom(int idAddress) {
        int stockroomId = this.stockroomOperations.insertStockroom(idAddress);
        return stockroomId;
    }    
 
    Map<Integer, BigDecimal> packagePrice = new HashMap<>();

int insertAndAcceptPackage(int addressFrom, int addressTo, String userName, int packageType, BigDecimal weight) {
    int idPackage = this.packageOperations.insertPackage(addressFrom, addressTo, userName, packageType, weight);
    this.packageOperations.acceptAnOffer(idPackage);

//    BigDecimal price = Util.getPackagePrice(packageType, weight, 
//        Util.getDistance((Pair<Integer, Integer>[])new Pair[] { this.addressesCoords.get(Integer.valueOf(addressFrom)), this.addressesCoords.get(Integer.valueOf(addressTo)) }));
//    Assert.assertTrue((this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(1.05D))) < 0));
//    Assert.assertTrue((this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(0.95D))) > 0));
//    this.packagePrice.put(Integer.valueOf(idPackage), price);
    return idPackage;
}
    
    public void test1() {
        
        generalOperations.eraseAll();
        
        int BG = insertCity("Belgrade", "11000");
        int KG = insertCity("Kragujevac", "550000");
        int VA = insertCity("Valjevo", "14000");
        int CA = insertCity("Cacak", "32000");

        int idAddressBG1 = insertAddress("Kraljice Natalije", 37, BG, 11, 15);
        int idAddressBG2 = insertAddress("Bulevar kralja Aleksandra", 73, BG, 10, 10);
        int idAddressBG3 = insertAddress("Vojvode Stepe", 39, BG, 1, -1);
        int idAddressBG4 = insertAddress("Takovska", 7, BG, 11, 12);
        int idAddressBG5 = insertAddress("Bulevar kralja Aleksandra", 37, BG, 12, 12);
        int idAddressKG1 = insertAddress("Daniciceva", 1, KG, 4, 310);
        int idAddressKG2 = insertAddress("Dure Pucara Starog", 2, KG, 11, 320);
        int idAddressVA1 = insertAddress("Cika Ljubina", 8, VA, 102, 101);
        int idAddressVA2 = insertAddress("Karadjordjeva", 122, VA, 104, 103);
        int idAddressVA3 = insertAddress("Milovana Glisica", 45, VA, 101, 101);
        int idAddressCA1 = insertAddress("Zupana Stracimira", 1, CA, 110, 309);
        int idAddressCA2 = insertAddress("Bulevar Vuka Karadzica", 1, CA, 111, 315);

        int idStockroomBG = insertStockroom(idAddressBG1);
        int idStockroomVA = insertStockroom(idAddressVA1);

        insertAndParkVehicle("BG1675DA", new BigDecimal(6.3D), new BigDecimal(1000.5D), 2, idStockroomBG);
        insertAndParkVehicle("VA1675DA", new BigDecimal(7.3D), new BigDecimal(500.5D), 1, idStockroomVA);

        String username = "crno.dete";
        insertUser(username, "Svetislav", "Kisprdilov", "Test_123", idAddressBG1);

        String courierUsernameBG = "postarBG";
        insertCourier(courierUsernameBG, "Pera", "Peric", "Postar_73", idAddressBG2, "654321");

        String courierUsernameVA = "postarVA";
        insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressBG2, "123456");

        int type1 = 0;
        BigDecimal weight1 = new BigDecimal(2);
        int idPackage1 = insertAndAcceptPackage(idAddressBG2, idAddressCA1, username, type1, weight1);
        int type2 = 1;
        BigDecimal weight2 = new BigDecimal(4);
        int idPackage2 = insertAndAcceptPackage(idAddressBG3, idAddressVA1, username, type2, weight2);
        int type3 = 2;
        BigDecimal weight3 = new BigDecimal(5);
        int idPackage3 = insertAndAcceptPackage(idAddressBG4, idAddressKG1, username, type3, weight3);
//        this.driveOperation.planingDrive(courierUsernameBG);
//    Assert.assertTrue(this.courierOperation.getCouriersWithStatus(1).contains(courierUsernameBG));
//    int type4 = 3;
//    BigDecimal weight4 = new BigDecimal(2);
//    int idPackage4 = insertAndAcceptPackage(idAddressBG2, idAddressKG2, username, type4, weight4);
//    Assert.assertEquals(4L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(1L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(1L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(3L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
//    Assert.assertEquals(1L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(1L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(2L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
//    Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
//    Assert.assertEquals(3L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(idPackage2, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
//    Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(idPackage1, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(CA).size());
//    Assert.assertEquals(1L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(idPackage3, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage1));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage3));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage1));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage2));
//    Assert.assertNotEquals(-1L, this.packageOperations.getCurrentLocationOfPackage(idPackage3));
//    Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(KG).size());
//    Assert.assertEquals(0L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(1L, this.packageOperations.getDeliveryStatus(idPackage4));
//    Assert.assertEquals(1L, this.packageOperations.getAllUndeliveredPackages().size());
//    Assert.assertTrue(this.packageOperations.getAllUndeliveredPackages().contains(Integer.valueOf(idPackage4)));
//    Assert.assertEquals(2L, this.courierOperation.getCouriersWithStatus(0).size());
//    double distance = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { this.addressesCoords.get(Integer.valueOf(idAddressBG1)), this.addressesCoords.get(Integer.valueOf(idAddressBG2)), this.addressesCoords
//          .get(Integer.valueOf(idAddressBG3)), this.addressesCoords.get(Integer.valueOf(idAddressBG4)), this.addressesCoords
//          .get(Integer.valueOf(idAddressVA1)), this.addressesCoords.get(Integer.valueOf(idAddressCA1)), this.addressesCoords.get(Integer.valueOf(idAddressKG1)), this.addressesCoords
//          .get(Integer.valueOf(idAddressBG1)) });
//    BigDecimal profit = ((BigDecimal)this.packagePrice.get(Integer.valueOf(idPackage1))).add(this.packagePrice.get(Integer.valueOf(idPackage2))).add(this.packagePrice.get(Integer.valueOf(idPackage3)));
//    profit = profit.subtract((new BigDecimal(36)).multiply(new BigDecimal(6.3D)).multiply(new BigDecimal(distance)));
//    Assert.assertTrue((this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(1.05D))) < 0));
//    Assert.assertTrue((this.courierOperation.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(0.95D))) > 0));

    }
    
    public static void main(String[] args) {
        new StudentTest().test1();
    }
    
}
