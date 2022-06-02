package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new AddressOperationsImpl();
        CityOperations cityOperations = new CityOperationsImpl();
        CourierOperations courierOperations = null; // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new CourierRequestOperationsImpl();
        DriveOperation driveOperation = null;
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        PackageOperations packageOperations = null;
        StockroomOperations stockroomOperations = new StockroomOperationsImpl();
        UserOperations userOperations = new UserOperationsImpl();
        VehicleOperations vehicleOperations = null;


        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    }
}
