package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.pricing.PriceList;

import java.time.Duration;
import java.time.LocalDateTime;

public class Bicycle extends Vehicle {

    private PriceList prices;

    public Bicycle(String id, String model, double pricePerDay, double pricePerHour) {
        super(id, model);
        setPrices(pricePerDay, pricePerHour);
    }

    public void setPrices(double pricePerDay, double pricePerHour) {
        this.prices = new PriceList(0 , pricePerDay, pricePerHour);
    }

    /**
     * Simulates end of rental for the vehicle - it is no longer rented by a driver.
     * @param rentalEnd time of end of rental
     * @throws IllegalArgumentException in case @rentalEnd is null
     * @throws VehicleNotRentedException in case the vehicle is not rented at all
     * @throws InvalidRentingPeriodException in case the rentalEnd is before the currently noted start date of rental or
     * in case the Vehicle does not allow the passed period for rental, e.g. Caravans must be rented for at least a day
     * and the driver tries to return them after an hour.
     */

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException {
        if (super.getStartRentTime() != null &&  rentalEnd.isAfter(super.getStartRentTime().plusDays(6).plusHours(23).plusMinutes(59))) {
            throw new InvalidRentingPeriodException("Bicycles cannot be rented for more than a week.");
        }

        super.returnBack(rentalEnd);
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

        if (endOfRent.isAfter(startOfRent.plusDays(6).plusHours(23).plusMinutes(59))) {
            throw new InvalidRentingPeriodException("Bicycles cannot be rented for more than a week.");
        }

        Duration duration = Duration.between(startOfRent, endOfRent);

        long days = duration.toDays();

        duration = duration.minusDays(days);

        long hours = duration.toHours();

        return prices.pricePerHour() * hours + prices.pricePerDay() * days;
    }

}
