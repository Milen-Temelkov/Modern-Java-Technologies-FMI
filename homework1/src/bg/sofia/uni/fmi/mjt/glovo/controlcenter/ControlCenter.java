package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.UnreachableDestinationException;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class ControlCenter implements ControlCenterApi {

    record Pair<K, V> (K fst, V snd) { }

    public final MapEntity[][] layout;

    public ControlCenter(char[][] mapLayout) {
        layout = new MapEntity[mapLayout.length][];

        for (int row = 0; row < mapLayout.length; row++) {
            layout[row] = new MapEntity[mapLayout[row].length];

            for (int col = 0; col < mapLayout[row].length; col++) {
                char current = mapLayout[row][col];
                Location currLocation = new Location(row, col);

                MapEntityType type =  switch(current) {
                    case '.' -> MapEntityType.ROAD;
                    case '#' -> MapEntityType.WALL;
                    case 'R' -> MapEntityType.RESTAURANT;
                    case 'C' -> MapEntityType.CLIENT;
                    case 'A' -> MapEntityType.DELIVERY_GUY_CAR;
                    case 'B' -> MapEntityType.DELIVERY_GUY_BIKE;
                    default -> null;
                };

                layout[row][col] = new MapEntity(currLocation, type);
            }
        }
    }

    /**
     * Finds the optimal delivery person for a given delivery task. The method
     * selects the best delivery option based on the provided cost and time constraints.
     * If no valid delivery path exists, it returns null.
     *
     * @param restaurantLocation The location of the restaurant to start the delivery from.
     * @param clientLocation     The location of the client receiving the delivery.
     * @param maxPrice           The maximum price allowed for the delivery. Use -1 for no cost constraint.
     * @param maxTime            The maximum time allowed for the delivery. Use -1 for no time constraint.
     * @param shippingMethod     The method for shipping the delivery.
     * @return A DeliveryInfo object containing the optimal delivery guy, the total cost,
     * the total time, and the delivery type. Returns null if no valid path is found.
     * @throws IllegalArgumentException if any of the locations or shipping method is null
     * @throws UnreachableDestinationException if the client could not be reached from the restaurant
     */
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                        double maxPrice, int maxTime, ShippingMethod shippingMethod) {

        if (restaurantLocation == null || clientLocation == null) {
            throw new IllegalArgumentException("Locations cannot be null");
        }

        if (shippingMethod == null) {
            throw new IllegalArgumentException("Shipping method cannot be null");
        }

        int distanceFromRestaurantToClient = getDistance(layout[restaurantLocation.x()][restaurantLocation.y()],
                                                                layout[clientLocation.x()][clientLocation.y()]);

        Pair<MapEntity, Integer> carDeliveryGuy = getClosestEntityTo(restaurantLocation, MapEntityType.DELIVERY_GUY_CAR);
        Pair<MapEntity, Integer> bikeDeliveryGuy = getClosestEntityTo(restaurantLocation, MapEntityType.DELIVERY_GUY_BIKE);

        if (carDeliveryGuy == null && bikeDeliveryGuy == null) {
            return null;
        }

        DeliveryInfo carDelivery = carDeliveryGuy != null ? calculateDeliveryInfo(carDeliveryGuy, distanceFromRestaurantToClient, DeliveryType.CAR) : null;
        DeliveryInfo bikeDelivery = bikeDeliveryGuy != null ? calculateDeliveryInfo(bikeDeliveryGuy, distanceFromRestaurantToClient, DeliveryType.BIKE) : null;

        return findOptimalDelivery(carDelivery, bikeDelivery, maxPrice, maxTime, shippingMethod);
    }

    /**
     * Calculates the delivery information based on the delivery guy's distance to restaurant,
     * the distance from the restaurant to the client, and the type of delivery.
     *
     * @param deliveryGuy a Pair containing the delivery person's MapEntity
     *                    and the distance to the restaurant (in kilometers)
     * @param distanceFromRestToClient the distance from the restaurant to the client (in kilometers)
     * @param type the {@link DeliveryType} indicating the pricing and time calculation rules
     * @return a {@link DeliveryInfo} object containing the starting location of the delivery,
     *         the total price, the total time, and the delivery type
     */
    private DeliveryInfo calculateDeliveryInfo(Pair<MapEntity, Integer> deliveryGuy, int distanceFromRestToClient, DeliveryType type) {
        int totalDistance = deliveryGuy.snd + distanceFromRestToClient;
        int totalPrice = totalDistance * type.getPricePerKM();
        int totalTime = totalDistance * type.getTimePerKM();
        return new DeliveryInfo(deliveryGuy.fst.location(), totalPrice, totalTime, type);
    }

    /**
     * Finds the optimal delivery option based on the given constraints and shipping method.
     * The method compares car and bike delivery options and filters them according to the
     * specified maximum price and time. It then selects the best option based on the
     * shipping method (e.g., fastest or cheapest).
     *
     * @param carDelivery the {@link DeliveryInfo} for car delivery, which may be null
     * @param bikeDelivery the {@link DeliveryInfo} for bike delivery, which may be null
     * @param maxPrice the maximum acceptable price for the delivery; if -1, price is not limited
     * @param maxTime the maximum acceptable estimated time for the delivery; if -1, time is not limited
     * @param method the {@link ShippingMethod} to determine whether to prioritize speed or cost
     * @return the {@link DeliveryInfo} object representing the optimal delivery option, or null if no option meets the constraints
     */
    private DeliveryInfo findOptimalDelivery(DeliveryInfo carDelivery, DeliveryInfo bikeDelivery,
                                             double maxPrice, int maxTime, ShippingMethod method) {

        if (carDelivery != null &&
            ((maxPrice != -1 && carDelivery.price() > maxPrice) ||
                (maxTime != -1 && carDelivery.estimatedTime() > maxTime))) {
            carDelivery = null;
        }

        if (bikeDelivery != null &&
            ((maxPrice != -1 && bikeDelivery.price() > maxPrice) ||
                (maxTime != -1 && bikeDelivery.estimatedTime() > maxTime))) {
            bikeDelivery = null;
        }

        return switch (method) {
            case FASTEST ->
                (carDelivery != null) && (bikeDelivery == null || carDelivery.estimatedTime() < bikeDelivery.estimatedTime()) ?
                carDelivery : bikeDelivery;
            case CHEAPEST ->
                (bikeDelivery != null) && (carDelivery == null || bikeDelivery.price() < carDelivery.price()) ?
                    bikeDelivery : carDelivery;
        };
    }

    /**
     * Calculates the shortest distance in kilometers between two map entities using
     * a breadth-first search (BFS) algorithm.
     *
     * @param from the starting {@link MapEntity} from which the search begins
     * @param to the target {@link MapEntity} to which the distance is calculated
     * @return the shortest distance (in kilometers) between the two entities, or -1
     *         if the target cannot be reached from the starting point
     */
    private int getDistance(MapEntity from, MapEntity to) {
        Set<MapEntity> visited = new HashSet<>();
        Queue<Pair<MapEntity, Integer>> toVisit = new ArrayDeque<>();

        toVisit.add(new Pair<>(from, 0));

        while (!toVisit.isEmpty()) {
            Pair<MapEntity, Integer> curr = toVisit.poll();

            if (curr.fst == to) {
                return curr.snd;
            }

            if (!visited.contains(curr.fst)) {
                Set<MapEntity> neighbours = getValidNeighboursOf(curr.fst);

                for (MapEntity neighbour : neighbours) {
                    toVisit.add(new Pair<>(neighbour, curr.snd + 1));
                }

                visited.add(curr.fst);
            }
        }

        throw new UnreachableDestinationException("Destination could not be reached from the given starting point");
    }

    /**
     * Finds the closest map entity of a specific type to a given location using
     * a breadth-first search (BFS) algorithm.
     *
     * @param location the {@link Location} from which the search begins
     * @param ofType the {@link MapEntityType} specifying the type of entity to find
     * @return a {@link Pair} containing the closest {@link MapEntity} of the specified type
     *         and the distance (in kilometers) to it, or null if no such entity is reachable
     */
    private Pair<MapEntity, Integer> getClosestEntityTo(Location location, MapEntityType ofType) {

        Set<MapEntity> visited = new HashSet<>();
        Queue<Pair<MapEntity, Integer>> toVisit = new ArrayDeque<>();

        toVisit.add(new Pair<>(layout[location.x()][location.y()], 0));

        while (!toVisit.isEmpty()) {
            Pair<MapEntity, Integer> curr = toVisit.poll();

            if (curr.fst.type() == ofType) {
                return curr;
            }

            if (!visited.contains(curr.fst)) {
                Set<MapEntity> neighbours =  getValidNeighboursOf(curr.fst());

                for (MapEntity neighbour : neighbours) {
                    toVisit.add(new Pair<>(neighbour, curr.snd + 1));
                }

                visited.add(curr.fst);
            }
        }

        return null;
    }

    /**
     * Retrieves all valid neighboring map entities adjacent to the given map entity.
     * A neighbor is considered valid if it is within the bounds of the layout and
     * passes the {@link #isValidNeighbour(int, int)} check.
     *
     * @param tile the {@link MapEntity} whose valid neighbors are to be found
     * @return a {@link Set} of valid neighboring {@link MapEntity} objects
     */
    private Set<MapEntity> getValidNeighboursOf(MapEntity tile) {
        Set<MapEntity> neighbours = new HashSet<>();

        int rowIndex = tile.location().x();
        int colIndex = tile.location().y();

        if (rowIndex - 1 >= 0 && isValidNeighbour(rowIndex - 1, colIndex)) {
            neighbours.add(layout[rowIndex - 1][colIndex]);
        }
        if (rowIndex + 1 < layout.length && isValidNeighbour(rowIndex + 1, colIndex)) {
            neighbours.add(layout[rowIndex + 1][colIndex]);
        }
        if (colIndex - 1 >= 0 && isValidNeighbour(rowIndex, colIndex - 1)) {
            neighbours.add(layout[rowIndex][colIndex - 1]);
        }
        if (colIndex + 1 < layout[0].length && isValidNeighbour(rowIndex, colIndex + 1)) {
            neighbours.add(layout[rowIndex][colIndex + 1]);
        }

        return neighbours;
    }

    /**
     * Checks if the map entity at the specified position is a valid neighbor.
     * A neighbor is considered valid if it is not of type {@link MapEntityType#WALL}.
     *
     * @param row the row index of the map entity to check
     * @param col the column index of the map entity to check
     * @return {@code true} if the map entity at the specified position is valid, {@code false} otherwise
     */
    private boolean isValidNeighbour(int row, int col) {
        return layout[row][col].type() != MapEntityType.WALL;
    }

    /**
     * Returns the map
     *
     * @return A MapEntity[][] containing the map
     */
    public MapEntity[][] getLayout() {
        return this.layout;
    }
}
