/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import rs.etf.sab.student.utility.Pair;
import rs.etf.sab.student.utility.Util;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private DriveOperation driveOperation = new DriveOperationImpl();
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
    BigDecimal price = Util.getPackagePrice(packageType, weight, 
            Util.getDistance((Pair<Integer, Integer>[])new Pair[] { this.addressesCoords.get(Integer.valueOf(addressFrom)), this.addressesCoords.get(Integer.valueOf(addressTo)) }));
    if(!((this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(1.05D))) < 0))) {
        System.err.println("PRICES ARE NOT THE SAME");
    }
    if(!((this.packageOperations.getPriceOfDelivery(idPackage).compareTo(price.multiply(new BigDecimal(0.95D))) > 0))) {
        System.err.println("PRICES ARE NOT THE SAME");
    }
    this.packagePrice.put(Integer.valueOf(idPackage), price);
    return idPackage;
}
    
    public void test1() throws Exception {
        
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
        this.driveOperation.planingDrive(courierUsernameBG);

        
        double d10 = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { 
            new AddressOperationsImpl().fetchCoordinates(idAddressCA1),
            new AddressOperationsImpl().fetchCoordinates(idAddressBG4)            
            
            });
        double d20 = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { 
            new AddressOperationsImpl().fetchCoordinates(idAddressVA1),
            new AddressOperationsImpl().fetchCoordinates(idAddressBG4)            
                
            });
        double d30 = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { 
            new AddressOperationsImpl().fetchCoordinates(idAddressKG1),
            new AddressOperationsImpl().fetchCoordinates(idAddressBG4)            
                
            });
        System.out.println(idAddressBG4);
        System.out.println(idPackage1 + " " + d10);
        System.out.println(idPackage2 + " " + d20);
        System.out.println(idPackage3 + " " + d30);
        int type4 = 3;
        BigDecimal weight4 = new BigDecimal(2);
        int idPackage4 = insertAndAcceptPackage(idAddressBG2, idAddressKG2, username, type4, weight4);
        if(4L != this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size()){
            System.err.println("BAD PACKAGE PLACES");
        }
        /* stop 1 */
        if(-2L != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }

        if(2L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(1L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(1L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(3L != this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(1L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }

        /* stop 2 */
        if(-2L != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }

        if(2L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(1L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(2L != this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(2L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }
        
        /* stop 3 */
        if(-2L != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }
        
        if(2L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(1L != this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(3L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }

        /* stop 4 */
        if(idPackage2 != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }

        if(2L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(3L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(1L != this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(2L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }

        /* stop 5 */
        if(idPackage1 != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }

        if(3L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(3L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L != this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(1L != this.packageOperations.getAllPackagesCurrentlyAtCity(CA).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(1L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }
        
        /* stop 6 */
        if(idPackage3 != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }

        if(3L != this.packageOperations.getDeliveryStatus(idPackage1)) {
            System.err.println("BAD STATUS");            
        }
        if(3L != this.packageOperations.getDeliveryStatus(idPackage2)) {
            System.err.println("BAD STATUS");            
        }
        if(3L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD STATUS");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage1)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage2)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(-1L == this.packageOperations.getCurrentLocationOfPackage(idPackage3)) {
            System.err.println("BAD CURRENT LOCATION");            
        }
        if(1L != this.packageOperations.getAllPackagesCurrentlyAtCity(KG).size()) {
            System.err.println("BAD PACKAGES IN CITY");            
        }
        if(0L != this.driveOperation.getPackagesInVehicle(courierUsernameBG).size()) {
            System.err.println("BAD BACKAGES IN VEHICLE");            
        }
        
        /* stop 7 */
        if(-1L != this.driveOperation.nextStop(courierUsernameBG)) {
            System.err.println("BAD NEXT STOP");            
        }
        
        if(1L != this.packageOperations.getAllUndeliveredPackages().size()
            &&     
            this.packageOperations.getAllUndeliveredPackages().contains(Integer.valueOf(idPackage4))
            ) {
            System.err.println("BAD UNPLANED PACKAGE");
        }
        double distance = Util.getDistance((Pair<Integer, Integer>[])new Pair[] { 
                this.addressesCoords.get(Integer.valueOf(idAddressBG1)), 
                this.addressesCoords.get(Integer.valueOf(idAddressBG2)), 
                this.addressesCoords.get(Integer.valueOf(idAddressBG3)), 
                this.addressesCoords.get(Integer.valueOf(idAddressBG4)), 
                this.addressesCoords.get(Integer.valueOf(idAddressVA1)), 
                this.addressesCoords.get(Integer.valueOf(idAddressCA1)), 
                this.addressesCoords.get(Integer.valueOf(idAddressKG1)), 
                this.addressesCoords.get(Integer.valueOf(idAddressBG1)) });
        System.out.println(distance);

        BigDecimal profit = ((BigDecimal)this.packagePrice.get(Integer.valueOf(idPackage1))).add(this.packagePrice.get(Integer.valueOf(idPackage2))).add(this.packagePrice.get(Integer.valueOf(idPackage3)));
        System.out.println(profit);
        BigDecimal cost = (new BigDecimal(36)).multiply(new BigDecimal(6.3D)).multiply(new BigDecimal(distance));
        System.out.println(cost);
        profit = profit.subtract(cost);
        if(!((this.courierOperations.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(1.05D))) < 0)
             && (this.courierOperations.getAverageCourierProfit(3).compareTo(profit.multiply(new BigDecimal(0.95D))) > 0)
                )) {
            System.err.println("BAD FINAL PRICE");
        }
            
    }
    
    public void test2() throws Exception {
        
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
        insertCourier(courierUsernameVA, "Pera", "Peric", "Postar_73", idAddressVA2, "123456");
        int type = 1;
        BigDecimal weight = new BigDecimal(4);
        int idPackage1 = insertAndAcceptPackage(idAddressBG2, idAddressKG1, username, type, weight);
        int idPackage2 = insertAndAcceptPackage(idAddressKG2, idAddressBG4, username, type, weight);
        int idPackage3 = insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
        int idPackage4 = insertAndAcceptPackage(idAddressCA2, idAddressBG4, username, type, weight);
        if(0L != this.courierOperations.getCouriersWithStatus(1).size()) {
            System.err.println("BAD: COURIER STATUS");
        }

//        this.driveOperation.planingDrive(courierUsernameBG);
        this.driveOperation.planingDrive(courierUsernameVA); // pick up from its own city, deliver to the location, pick up for the stockroom in his own city
        
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(idPackage1, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage1));

        if(-2L != this.driveOperation.nextStop(courierUsernameVA)) {
            System.err.println("BAD: PICK UP PACKAGE");
        }
        if(idPackage3 != this.driveOperation.nextStop(courierUsernameVA)) {
            System.err.println("BAD: DELIVER PACKAGE");            
        }
        if(3L != this.packageOperations.getDeliveryStatus(idPackage3)) {
            System.err.println("BAD: DELIVERED PACKAGE STATUS");            
        }

//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2));

        if(-2L != this.driveOperation.nextStop(courierUsernameVA)) {
            System.err.println("BAD: PICK UP PACKAGE");
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage4)) {
            System.err.println("BAD: DELIVERED PACKAGE STATUS");            
        }

//    Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG)); // FINISH courierUsernameBG
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage2));

//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage2)));
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(KG).contains(Integer.valueOf(idPackage1)));

        if(-1L != this.driveOperation.nextStop(courierUsernameVA)) {
            // FINISH courierUsernameVA
            System.err.println("BAD: END OF DRIVE");
        }
        if(2L != this.packageOperations.getDeliveryStatus(idPackage4)) {
            System.err.println("BAD: DELIVERED PACKAGE STATUS");            
        }
        
        if(!(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size() == 1
                && this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(Integer.valueOf(idPackage4)))) {
            System.err.println("BAD: STORAGED PACKAGE");                        
        }
        
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(CA).contains(Integer.valueOf(idPackage3)));

    // NEXT DRIVE (has storages)



//    int idPackage5 = insertAndAcceptPackage(idAddressVA2, idAddressCA1, username, type, weight);
//    int idPackage6 = insertAndAcceptPackage(idAddressBG3, idAddressVA3, username, type, weight);


//    this.driveOperation.planingDrive(courierUsernameBG);

//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage6));
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage2)));
//    Assert.assertFalse(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage6)));
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage2)));
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage6)));
//    Assert.assertEquals(idPackage2, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage2));
//    Assert.assertEquals(idPackage6, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage6));
//    Assert.assertEquals(0L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage5));
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage5)));
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.packageOperations.getDeliveryStatus(idPackage4));
//    Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage4)));
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage5)));
//    Assert.assertEquals(1L, this.packageOperations.getAllPackagesCurrentlyAtCity(VA).size());
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(VA).contains(Integer.valueOf(idPackage6)));
//    Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(0L, this.packageOperations.getAllUndeliveredPackagesFromCity(BG).size());
//    Assert.assertEquals(3L, this.packageOperations.getAllPackagesCurrentlyAtCity(BG).size());
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage2)));
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage4)));
//    Assert.assertTrue(this.packageOperations.getAllPackagesCurrentlyAtCity(BG).contains(Integer.valueOf(idPackage5)));
//    this.driveOperation.planingDrive(courierUsernameBG);
//    Assert.assertEquals(0L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertEquals(-2L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(2L, this.driveOperation.getPackagesInVehicle(courierUsernameBG).size());
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage4)));
//    Assert.assertTrue(this.driveOperation.getPackagesInVehicle(courierUsernameBG).contains(Integer.valueOf(idPackage5)));
//    Assert.assertEquals(idPackage4, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage4));
//    Assert.assertEquals(idPackage5, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(3L, this.packageOperations.getDeliveryStatus(idPackage5));
//    Assert.assertEquals(-1L, this.driveOperation.nextStop(courierUsernameBG));
//    Assert.assertEquals(0L, this.packageOperations.getAllUndeliveredPackages().size());
//    Assert.assertEquals(2L, this.courierOperation.getCouriersWithStatus(0).size());
//    Assert.assertTrue((this.courierOperation.getAverageCourierProfit(1).compareTo(new BigDecimal(0)) > 0));
//    Assert.assertTrue((this.courierOperation.getAverageCourierProfit(5).compareTo(new BigDecimal(0)) > 0));


    }

    
    public static void main(String[] args) {
        try {
//            new StudentTest().test1();
            new StudentTest().test2();
        } catch (Exception ex) {
            Logger.getLogger(StudentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
