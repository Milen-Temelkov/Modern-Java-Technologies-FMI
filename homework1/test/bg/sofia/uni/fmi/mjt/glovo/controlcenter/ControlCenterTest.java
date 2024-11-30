package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.UnreachableDestinationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ControlCenterTest {

    final int noLimitation = -1;

    char[][] validLayout = {
        {'#', '#', '#', '.', '#'},
        {'#', 'B', '.', 'R', '.'},
        {'.', '.', '#', '.', '#'},
        {'#', 'C', '.', 'A', '.'},
        {'#', '.', '#', '#', '#'}
    };

    char[][] unreachableClientLayout = {
        {'#', '#', '#', '.', '#'},
        {'#', 'B', '.', 'R', '.'},
        {'.', '.', '.', '.', '#'},
        {'#', '#', '.', 'A', '.'},
        {'C', '#', '#', '#', '#'}
    };

    char[][] unreachableRestaurantLayout = {
        {'#', '#', '#', '.', '#'},
        {'#', 'B', '.', 'C', '.'},
        {'.', '.', '.', '.', '#'},
        {'#', '#', '.', 'A', '.'},
        {'R', '#', '#', '#', '#'}
    };

    char[][] unreachableDeliveryGuysLayout = {
        {'A', '#', '#', '.', '#'},
        {'#', '#', '.', 'C', '.'},
        {'.', '.', 'R', '.', '#'},
        {'#', '#', '.', '.', '.'},
        {'B', '#', '#', '#', '#'}
    };

    char[][] unreachableCarGuyLayout = {
        {'A', '#', '#', '.', '#'},
        {'#', '#', '.', 'C', '.'},
        {'.', '.', 'R', '.', '#'},
        {'#', '.', '.', '.', '.'},
        {'B', '.', '#', '#', '#'}
    };

    char[][] unreachableBikeGuyLayout = {
        {'A', '.', '#', '.', '#'},
        {'#', '.', '.', 'C', '.'},
        {'.', '.', 'R', '.', '#'},
        {'#', '#', '.', '.', '.'},
        {'B', '#', '#', '#', '#'}
    };

    @Test
    void testFindOptimalDeliveryGuyNoPriceAndTimeConstraintsCheapestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.CHEAPEST);

        assertEquals(DeliveryType.BIKE, info.deliveryType(),"Cheapest delivery for same distance delivery guys should be with bike");
        assertEquals(new Location(1, 1), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(18, info.price(), "Price of bike delivery should be 18");
        assertEquals(30, info.estimatedTime(), "Time of bike delivery should be 30");
    }

    @Test
    void testFindOptimalDeliveryGuyNoPriceAndTimeConstraintsFastestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST);

        assertEquals(DeliveryType.CAR, info.deliveryType(),"Fastest delivery for same distance delivery guys should be with bike");
        assertEquals(new Location(3, 3), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(30, info.price(), "Price of car delivery should be 30");
        assertEquals(18, info.estimatedTime(), "Time of bike delivery should be 18");
    }

    @Test
    void testFindOptimalDeliveryGuyWithLowPriceConstraintsCheapestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, 15, noLimitation, ShippingMethod.CHEAPEST);

        assertNull(info, "No optimal delivery should be found with the given price constraint");
    }

    @Test
    void testFindOptimalDeliveryGuyWithLowTimeConstraintsCheapestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, 15, ShippingMethod.CHEAPEST);

        assertNull(info, "No optimal delivery should be found with the given time constraint");
    }

    @Test
    void testFindOptimalDeliveryGuyWithLowPriceConstraintsFastestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, 15, noLimitation, ShippingMethod.FASTEST);

        assertNull(info, "No optimal delivery should be found with the given price constraint");
    }

    @Test
    void testFindOptimalDeliveryGuyWithLowTimeConstraintsFastestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, 15, ShippingMethod.FASTEST);

        assertNull(info, "No optimal delivery should be found with the given time constraint");
    }

    @Test
    void testFindOptimalDeliveryGuyWithNormalPriceConstraintsCheapestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, 20, noLimitation, ShippingMethod.CHEAPEST);

        assertEquals(DeliveryType.BIKE, info.deliveryType(),"Cheapest delivery with price constraint should be with bike");
        assertEquals(new Location(1, 1), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(18, info.price(), "Price of bike delivery should be 18");
        assertEquals(30, info.estimatedTime(), "Time of bike delivery should be 30");
    }

    @Test
    void testFindOptimalDeliveryGuyWithNormalTimeConstraintsCheapestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, 20, ShippingMethod.CHEAPEST);

        assertEquals(DeliveryType.CAR, info.deliveryType(),"Cheapest delivery with time constraint should be with car");
        assertEquals(new Location(3, 3), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(30, info.price(), "Price of car delivery should be 30");
        assertEquals(18, info.estimatedTime(), "Time of bike delivery should be 18");
    }

    @Test
    void testFindOptimalDeliveryGuyWithNormalPriceConstraintsFastestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, 20, noLimitation, ShippingMethod.FASTEST);

        assertEquals(DeliveryType.BIKE, info.deliveryType(),"Fastest delivery with price constraint should be with bike");
        assertEquals(new Location(1, 1), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(18, info.price(), "Price of bike delivery should be 18");
        assertEquals(30, info.estimatedTime(), "Time of bike delivery should be 30");
    }

    @Test
    void testFindOptimalDeliveryGuyWithNormalTimeConstraintsFastestMethod() {
        ControlCenterApi controlCenter = new ControlCenter(validLayout);
        Location client = new Location(3, 1);
        Location restaurant = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, 20, ShippingMethod.FASTEST);

        assertEquals(DeliveryType.CAR, info.deliveryType(),"Fastest delivery with time constraint should be with bike");
        assertEquals(new Location(3, 3), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
        assertEquals(30, info.price(), "Price of car delivery should be 30");
        assertEquals(18, info.estimatedTime(), "Time of bike delivery should be 18");
    }

    @Test
    void testFindOptimalDeliveryGuyWithUnreachableClient() {
        ControlCenterApi controlCenter = new ControlCenter(unreachableClientLayout);
        Location client = new Location(4, 0);
        Location restaurant = new Location(1, 3);

        assertThrows(
            UnreachableDestinationException.class, () -> controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST), "Client should not be reachable");
    }

    @Test
    void testFindOptimalDeliveryGuyWithUnreachableRestaurant() {
        ControlCenterApi controlCenter = new ControlCenter(unreachableRestaurantLayout);
        Location client = new Location(1, 3);
        Location restaurant = new Location(4, 0);

        assertThrows(
            UnreachableDestinationException.class, () -> controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST), "Client should not be reachable");
    }

    @Test
    void testFindOptimalDeliveryGuyWithUnreachableDeliveryGuys() {
        ControlCenterApi controlCenter = new ControlCenter(unreachableDeliveryGuysLayout);
        Location restaurant = new Location(2, 2);
        Location client = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST);

        assertNull(info, "Delivery guys should be unreachable should not be reachable");
    }

    @Test
    void testFindOptimalDeliveryGuyWithUnreachableCarGuy() {
        ControlCenterApi controlCenter = new ControlCenter(unreachableCarGuyLayout);
        Location restaurant = new Location(2, 2);
        Location client = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST);

        assertEquals(DeliveryType.BIKE, info.deliveryType(),"The only delivery guy available should be with bike");
        assertEquals(new Location(4, 0), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
    }

    @Test
    void testFindOptimalDeliveryGuyWithUnreachableBikeGuy() {
        ControlCenterApi controlCenter = new ControlCenter(unreachableBikeGuyLayout);
        Location restaurant = new Location(2, 2);
        Location client = new Location(1, 3);

        DeliveryInfo info = controlCenter.findOptimalDeliveryGuy(restaurant, client, noLimitation, noLimitation, ShippingMethod.FASTEST);

        assertEquals(DeliveryType.CAR, info.deliveryType(),"The only delivery guy available should be with car");
        assertEquals(new Location(0, 0), info.deliveryGuyLocation(), "Delivery guy location calculated wrong");
    }
}