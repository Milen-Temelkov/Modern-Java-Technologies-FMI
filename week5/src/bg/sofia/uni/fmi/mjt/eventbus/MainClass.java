package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.MyEvent;
import bg.sofia.uni.fmi.mjt.eventbus.events.StringPayload;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.DeferredEventSubscriber;

import java.time.Instant;
import java.util.Iterator;

public class MainClass {
    public static void main(String[] args) {
        DeferredEventSubscriber<MyEvent> sub = new DeferredEventSubscriber<>();
        StringPayload payload = new StringPayload("This is a test payload");
        MyEvent event = new MyEvent(Instant.now(), 1, "System", payload);

        sub.onEvent(event);

        Iterator<?> it = sub.iterator();
        while (it.hasNext()) {
            System.out.println("iofwe");
            it.next();
        }
    }
}
