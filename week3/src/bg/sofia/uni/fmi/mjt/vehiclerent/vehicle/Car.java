package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.pricing.PriceList;

import java.time.Duration;
import java.time.LocalDateTime;

public class Car extends Vehicle {

    private PriceList prices;
    private final FuelType fuelType;
    private final int numberOfSeats;
    private double pricePerSeat = 5;

    public Car(String id,
               String model,
               FuelType fuelType,
               int numberOfSeats,
               double pricePerWeek,
               double pricePerDay,
               double pricePerHour) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
        setPrices(pricePerWeek, pricePerDay, pricePerHour);
    }

    public void setPrices(double pricePerWeek, double pricePerDay, double pricePerHour) {
        this.prices = new PriceList(pricePerWeek , pricePerDay, pricePerHour);
    }

    public void setPricePerSeat(double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    /**
     * Used to calculate potential rental price without the vehicle to be rented.
     * The calculation is based on the type of the Vehicle (Car/Caravan/Bicycle).
     *
     * @param startOfRent the beginning of the rental
     * @param endOfRent the end of the rental
     * @return potential price for rent
     * @throws InvalidRentingPeriodException in case the vehicle cannot be rented for that period of time or
     * the period is not valid (end date is before start date)
     */
    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {

        if (endOfRent.isBefore(startOfRent)) {
            throw new InvalidRentingPeriodException("Rental end cannot be before start time.");
        }

        Duration duration = Duration.between(startOfRent, endOfRent);

        long days = duration.toDays();
        long weeks = duration.toDays();

        duration = duration.minusDays(days);

        long hours = duration.toHours();
        weeks /= 7;
        days %= 7;


        return prices.pricePerWeek() * weeks +
                prices.pricePerHour() * hours +
                prices.pricePerDay() * days +
                numberOfSeats * pricePerSeat +
                fuelType.getDailyTax() * (days + weeks * 7) +
                super.getDriver().getTax();
    }

}
