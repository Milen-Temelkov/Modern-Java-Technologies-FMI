package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import java.util.Comparator;

public class PriorityDescOrderComparator implements Comparator<Event> {
    @Override
    public int compare(Event o1, Event o2) {
        return Integer.compare(o1.getPriority(), o2.getPriority()); // -1 за да ги обърне
    }
}
