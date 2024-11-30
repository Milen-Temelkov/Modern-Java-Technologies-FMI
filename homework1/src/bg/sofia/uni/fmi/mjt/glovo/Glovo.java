package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenterApi;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;

public class Glovo implements GlovoApi {

    private final int noLimitation = -1;

    ControlCenterApi controlCenter;

    public Glovo(char[][] mapLayout) {
        controlCenter = new ControlCenter(mapLayout);
    }

    /**
     * Validates whether the given map entity is within the map boundaries
     * and matches the entity located at its specified position in the control center's layout.
     *
     * @param entity the {@link MapEntity} to validate
     * @throws IllegalArgumentException if the entity is null
     * @throws InvalidOrderException if the entity is out of the map boundaries or
     *         does not match the entity at the specified location in the layout
     */
    private void validateEntity(MapEntity entity) {

        if (entity == null) {
            throw new IllegalArgumentException("Given entity is null");
        }

        int entityRow = entity.location().x();
        int entityCol = entity.location().y();

        if ((entityRow < 0 || entityRow >= controlCenter.getLayout().length) ||
            (entityCol < 0 || entityCol >= controlCenter.getLayout()[entityRow].length)) {
            throw new InvalidOrderException("Given entity is out of the map");
        }

        MapEntity entityToMatch = controlCenter.getLayout()[entityRow][entityCol];

        if (!entity.equals(entityToMatch)) {
            throw new InvalidOrderException("Entity does not match the one on the specified location");
        }
    }

    /**
     * Returns the cheapest delivery option for a specified food item from a restaurant to a client location.
     *
     * @param client     The delivery destination, represented by a MapEntity.
     * @param restaurant The location of the restaurant from which the food item is sourced,
     *                   represented by a MapEntity.
     * @param foodItem   The name of the food item to be delivered.
     * @return A Delivery object representing the cheapest available delivery option within the
     * specified constraints.
     * @throws InvalidOrderException           If there is no client or restaurant at the specified location on
     *                                         the map, or if the location is outside the map's defined boundaries.
     * @throws NoAvailableDeliveryGuyException If no delivery guys are available to complete the delivery.
     * @throws IllegalArgumentException        If any of the arguments is null(blank or empty for the item).
     */
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
        throws NoAvailableDeliveryGuyException {

        validateEntity(client);
        validateEntity(restaurant);

        if (foodItem == null) {
            throw new IllegalArgumentException("Food item cannot be null");
        }

        if (foodItem.isBlank()) {
            throw new IllegalArgumentException("Food item cannot be empty or blank");
        }

        DeliveryInfo delivery = controlCenter.findOptimalDeliveryGuy(
            restaurant.location(),
            client.location(),
            noLimitation,
            noLimitation,
            ShippingMethod.CHEAPEST);

        if (delivery == null) {
            throw new NoAvailableDeliveryGuyException("There is no delivery guy able to complete the delivery");
        }

        return new Delivery(
            client.location(),
            restaurant.location(),
            delivery.deliveryGuyLocation(),
            foodItem, delivery.price(), delivery.estimatedTime());
    }

    /**
     * Returns the fastest delivery option for a specified food item from a restaurant to a
     * client location.
     *
     * @param client     The delivery destination, represented by a MapEntity.
     * @param restaurant The location of the restaurant from which the food item is sourced,
     *                   represented by a MapEntity.
     * @param foodItem   The name of the food item to be delivered.
     * @return A Delivery object representing the fastest available delivery option within the specified
     * constraints.
     * @throws InvalidOrderException           If there is no client or restaurant at the specified location on
     *                                         the map, or if the location is outside the map's defined boundaries.
     * @throws NoAvailableDeliveryGuyException If no delivery guys are available to complete the delivery.
     * @throws IllegalArgumentException        If any of the arguments is null(blank or empty for the item).
     */
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
        throws NoAvailableDeliveryGuyException {

        validateEntity(client);
        validateEntity(restaurant);

        if (foodItem == null) {
            throw new IllegalArgumentException("Food item cannot be null");
        }

        if (foodItem.isBlank()) {
            throw new IllegalArgumentException("Food item cannot be empty or blank");
        }

        DeliveryInfo delivery = controlCenter.findOptimalDeliveryGuy(
            restaurant.location(),
            client.location(),
            noLimitation,
            noLimitation,
            ShippingMethod.FASTEST);

        if (delivery == null) {
            throw new NoAvailableDeliveryGuyException("There is no delivery guy able to complete the delivery");
        }

        return new Delivery(
            client.location(),
            restaurant.location(),
            delivery.deliveryGuyLocation(),
            foodItem, delivery.price(), delivery.estimatedTime());
    }

    /**
     * Returns the fastest delivery option under a specified price for a given food item from a restaurant
     * to a client location.
     *
     * @param client     The delivery destination, represented by a MapEntity.
     * @param restaurant The location of the restaurant from which the food item is sourced,
     *                   represented by a MapEntity.
     * @param foodItem   The name of the food item to be delivered.
     * @param maxPrice   The maximum price the client is willing to pay for the delivery.
     * @return A Delivery object representing the fastest available delivery option under the specified
     * price limit.
     * @throws InvalidOrderException           If there is no client or restaurant at the specified location
     *                                         on the map,or if the location is outside the map's defined boundaries.
     * @throws NoAvailableDeliveryGuyException If no delivery guys are available to complete the delivery.
     * @throws IllegalArgumentException        If any of the arguments is null(blank or empty for the item), or the price is <0.
     */
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant, String foodItem, double maxPrice)
        throws NoAvailableDeliveryGuyException {

        validateEntity(client);
        validateEntity(restaurant);

        if (foodItem == null) {
            throw new IllegalArgumentException("Food item cannot be null");
        }

        if (foodItem.isBlank()) {
            throw new IllegalArgumentException("Food item cannot be empty or blank");
        }

        if (maxPrice < 0) {
            throw new IllegalArgumentException("Max price should not be negative");
        }

        DeliveryInfo delivery = controlCenter.findOptimalDeliveryGuy(
            restaurant.location(),
            client.location(),
            maxPrice,
            noLimitation,
            ShippingMethod.FASTEST);

        if (delivery == null) {
            throw new NoAvailableDeliveryGuyException("There is no delivery guy able to complete the delivery");
        }

        return new Delivery(
            client.location(),
            restaurant.location(),
            delivery.deliveryGuyLocation(),
            foodItem, delivery.price(), delivery.estimatedTime());
    }

    /**
     * Returns the cheapest delivery option within a specified time limit for a given food item from a restaurant
     * to a client location.
     *
     * @param client     The delivery destination, represented by a MapEntity.
     * @param restaurant The location of the restaurant from which the food item is sourced,
     *                   represented by a MapEntity.
     * @param foodItem   The name of the food item to be delivered.
     * @param maxTime    The maximum allowable delivery time in minutes.
     * @return A Delivery object representing the cheapest available delivery option within the specified
     * time limit.
     * @throws InvalidOrderException           If there is no client or restaurant at the specified location
     *                                         on the map, or if the location is outside the map's defined boundaries.
     * @throws NoAvailableDeliveryGuyException If no delivery guys are available to complete the delivery.
     * @throws IllegalArgumentException        If any of the arguments is null(blank or empty for the item) or the time is <0
     */
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant, String foodItem, int maxTime)
        throws NoAvailableDeliveryGuyException {

        validateEntity(client);
        validateEntity(restaurant);

        if (foodItem == null) {
            throw new IllegalArgumentException("Food item cannot be null");
        }

        if (foodItem.isBlank()) {
            throw new IllegalArgumentException("Food item cannot be empty or blank");
        }

        if (maxTime < 0) {
            throw new IllegalArgumentException("Max time should not be negative");
        }

        DeliveryInfo delivery = controlCenter.findOptimalDeliveryGuy(
            restaurant.location(),
            client.location(),
            noLimitation,
            maxTime,
            ShippingMethod.CHEAPEST);

        if (delivery == null) {
            throw new NoAvailableDeliveryGuyException("There is no delivery guy able to complete the delivery");
        }

        return new Delivery(
            client.location(),
            restaurant.location(),
            delivery.deliveryGuyLocation(),
            foodItem, delivery.price(), delivery.estimatedTime());
    }

}
