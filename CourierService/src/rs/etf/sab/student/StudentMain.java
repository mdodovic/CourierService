package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new AddressOperationsImpl();
        CityOperations cityOperations = new CityOperationsImpl();
        CourierOperations courierOperations = new CourierOperationsImpl();
        CourierRequestOperation courierRequestOperation = new CourierRequestOperationsImpl();
        DriveOperation driveOperation = new DriveOperationImpl();
        GeneralOperations generalOperations = new GeneralOperationsImpl();
        PackageOperations packageOperations = new PackageOperationsImpl();
        StockroomOperations stockroomOperations = new StockroomOperationsImpl();
        UserOperations userOperations = new UserOperationsImpl();
        VehicleOperations vehicleOperations = new VehicleOperationsImpl();


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
