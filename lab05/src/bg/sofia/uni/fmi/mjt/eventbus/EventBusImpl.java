package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EventBusImpl implements EventBus {

    private Map<Class<?>, Set<Subscriber<?>>> events = new HashMap<>();
    private Map<Class<?>, Set<Event<?>>> eventsOfType = new HashMap<>();

    public EventBusImpl() { }

    /**
     * Subscribes the given subscriber to the given event type.
     *
     * @param eventType  the type of event to subscribe to
     * @param subscriber the subscriber to subscribe
     * @throws IllegalArgumentException if the event type is null
     * @throws IllegalArgumentException if the subscriber is null
     */
    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null!");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null!");
        }

        if (events.get(eventType) == null) {
            events.put(eventType, new HashSet<>());
        }
        events.get(eventType).add(subscriber);
    }

    /**
     * Unsubscribes the given subscriber from the given event type.
     *
     * @param eventType  the type of event to unsubscribe from
     * @param subscriber the subscriber to unsubscribe
     * @throws IllegalArgumentException     if the event type is null
     * @throws IllegalArgumentException     if the subscriber is null
     * @throws MissingSubscriptionException if the subscriber is not subscribed to the event type
     */
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
        throws MissingSubscriptionException {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null!");
        }

        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null!");
        }

        Set<Subscriber<?>> subscribers = events.get(eventType);

        if (subscribers == null || !subscribers.contains(subscriber)) {
            throw new MissingSubscriptionException("The subscriber is not subscribed to this event type");
        }

        subscribers.remove(subscriber);
    }

    /**
     * Publishes the given event to all subscribers of the event type.
     *
     * @param event the event to publish
     * @throws IllegalArgumentException if the event is null
     */
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null!");
        }

        Set<Subscriber<?>> subs = events.get(event.getClass());

//        if (subs != null) {
//            for (Subscriber<?> sub : subs) {
//                sub.onEvent(event);
//            }
//        }
    }

    /**
     * Clears all subscribers and event logs.
     */
    public void clear() {
        events.clear();
    }

    /**
     * Returns all events of the given event type that occurred between the given timestamps. If
     * {@code from} and {@code to} are equal the returned collection is empty.
     * <p> {@code from} - inclusive, {@code to} - exclusive. </p>
     *
     * @param eventType the type of event to get
     * @param from      the start timestamp (inclusive)
     * @param to        the end timestamp (exclusive)
     * @return an unmodifiable collection of events of the given event type that occurred between
     * the given timestamps
     * @throws IllegalArgumentException if the event type is null
     * @throws IllegalArgumentException if the start timestamp is null
     * @throws IllegalArgumentException if the end timestamp is null
     */
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from,
                                                Instant to) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        if (from == null) {
            throw new IllegalArgumentException("Instance 'from' cannot be null!");
        }

        if (to == null) {
            throw new IllegalArgumentException("Instance 'to' cannot be null!");
        }

        Set<? extends Event<?>> eventLogs = new TreeSet<>(new EventByTimestampComparator());

//        for (Event<?> event : events.keySet()) {
//            if (eventType.isAssignableFrom(event.getClass())) {
//                eventLogs.add(event);
//            }
//        }

        return Collections.emptyList();
    }

    /**
     * Returns all subscribers for the given event type in an unmodifiable collection. If there are
     * no subscribers for the event type, the method returns an empty unmodifiable collection.
     *
     * @param eventType the type of event to get subscribers for
     * @return an unmodifiable collection of subscribers for the given event type
     * @throws IllegalArgumentException if the event type is null
     */
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null!");
        }

        Set<Subscriber<?>> subs = events.get(eventType);

        return subs == null ? Collections.emptyList() : Collections.unmodifiableSet(subs);
    }

}
