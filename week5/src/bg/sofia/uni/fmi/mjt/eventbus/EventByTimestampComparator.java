package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import java.util.Comparator;

public class EventByTimestampComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        if (e1.getTimestamp().isBefore(e2.getTimestamp())) {
            return -1;
        } else if (e1.getTimestamp().isAfter(e2.getTimestamp())) {
            return 1;
        } else {
            return 0;
        }
    }
}
