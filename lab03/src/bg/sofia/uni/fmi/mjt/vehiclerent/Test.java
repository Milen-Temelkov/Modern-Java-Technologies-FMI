package bg.sofia.uni.fmi.mjt.vehiclerent;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.driver.AgeGroup;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Bicycle;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Car;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.FuelType;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Vehicle;
import java.time.LocalDateTime;

public class Test {

    public static void main(String[] args) {
        RentalService rentalService = new RentalService();
        LocalDateTime rentStart = LocalDateTime.of(2024, 10, 10, 0, 0, 0);
        Driver experiencedDriver = new Driver(AgeGroup.EXPERIENCED);

        Vehicle bike = new Bicycle("1", "Tesla Model 3" , 150, 10);
        rentalService.rentVehicle(experiencedDriver, bike, rentStart);

        try {
            double priceToPay = rentalService.returnVehicle(bike, rentStart.plusDays(5)); // 770.0
            System.out.println(priceToPay);
        }
        catch (InvalidRentingPeriodException | VehicleNotRentedException e) {
            System.out.println(e.getMessage());
        }

    }
}
