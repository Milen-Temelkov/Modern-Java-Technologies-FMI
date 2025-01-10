package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.pricing.PriceList;

import java.time.Duration;
import java.time.LocalDateTime;

public class Caravan extends Vehicle {

    private PriceList prices;
    private final FuelType fuelType;
    private final int numberOfSeats;
    private double pricePerSeat = 5;
    private final int numberOfBeds;
    private double pricePerBed = 10;

    public Caravan(String id,
                   String model,
                   FuelType fuelType,
                   int numberOfSeats,
                   int numberOfBeds,
                   double pricePerWeek,
                   double pricePerDay,
                   double pricePerHour) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
        this.numberOfBeds = numberOfBeds;
        setPrices(pricePerWeek, pricePerDay, pricePerHour);
    }

    public void setPrices(double pricePerWeek, double pricePerDay, double pricePerHour) {
        this.prices = new PriceList(pricePerWeek , pricePerDay, pricePerHour);
    }

    public void setPricePerSeat(double pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public void setPricePerBed(double pricePerBed) {
        this.pricePerBed = pricePerBed;
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
        if (super.getStartRentTime() != null && rentalEnd.isBefore(super.getStartRentTime().plusDays(1))) {
            throw new InvalidRentingPeriodException("Caravans cannot be rented for less than a day.");
        }

        super.returnBack(rentalEnd);

//        try {
//
//        }
//        catch (InvalidRentingPeriodException e) {
//            throw new InvalidRentingPeriodException(e.getMessage(), e);
//        }
//        catch (VehicleNotRentedException e) {
//            throw new VehicleNotRentedException(e.getMessage(), e);
//        }
//        catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException(e.getMessage(), e);
//        }
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

        if (endOfRent.isBefore(startOfRent.plusDays(1))) {
            throw new InvalidRentingPeriodException("Caravans cannot be rented for less than a day.");
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
                numberOfBeds * pricePerBed +
                fuelType.getDailyTax() * (days + weeks * 7) +
                super.getDriver().getTax();
    }


}
