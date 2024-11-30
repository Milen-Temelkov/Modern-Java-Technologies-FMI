package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GlovoTest {

    private GlovoApi glovo;

    @BeforeEach
    void setup() {
        char[][] layout = {
            {'#', '#', '#', '.', '#'},
            {'#', 'B', '.', 'R', '.'},
            {'.', '.', '#', '.', '#'},
            {'#', 'C', '.', 'A', '.'},
            {'#', '.', '#', '#', '#'}
        };

        glovo = new Glovo(layout);
    }

    @Test
    void testLocationOfClientOutOfMap() {
        Location clientLoc = new Location(5, 5);
        Location restaurantLoc = new Location(1, 3);

        MapEntity client= new MapEntity(clientLoc, MapEntityType.CLIENT);
        MapEntity restaurant = new MapEntity(restaurantLoc, MapEntityType.RESTAURANT);

        assertThrows(InvalidOrderException.class, () -> glovo.getCheapestDelivery(client, restaurant, "Lukanka"),
            "Method should throw invalidOrderExc when given Client location out of map");
    }

    @Test
    void testLocationOfRestaurantOutOfMap() {
        Location clientLoc = new Location(3, 1);
        Location restaurantLoc = new Location(5, 5);

        MapEntity client= new MapEntity(clientLoc, MapEntityType.CLIENT);
        MapEntity restaurant = new MapEntity(restaurantLoc, MapEntityType.RESTAURANT);

        assertThrows(InvalidOrderException.class, () -> glovo.getCheapestDelivery(client, restaurant, "Lukanka"),
            "Method should throw invalidOrderExc when given Restaurant location out of map");
    }

    @Test
    void testClientNotOnSpecifiedLocation() {
        Location clientLoc = new Location(1, 1);
        Location restaurantLoc = new Location(1, 3);

        MapEntity client= new MapEntity(clientLoc, MapEntityType.CLIENT);
        MapEntity restaurant = new MapEntity(restaurantLoc, MapEntityType.RESTAURANT);

        assertThrows(InvalidOrderException.class, () -> glovo.getCheapestDelivery(client, restaurant, "Lukanka"),
            "Method should throw invalidOrderExc when the Client is not on the specified location");
    }

    @Test
    void testRestaurantNotOnSpecifiedLocation() {
        Location clientLoc = new Location(3, 1);
        Location restaurantLoc = new Location(1, 1);

        MapEntity client= new MapEntity(clientLoc, MapEntityType.CLIENT);
        MapEntity restaurant = new MapEntity(restaurantLoc, MapEntityType.RESTAURANT);

        assertThrows(InvalidOrderException.class, () -> glovo.getCheapestDelivery(client, restaurant, "Lukanka"),
            "Method should throw invalidOrderExc when the Restaurant is not on the specified location");
    }
}
