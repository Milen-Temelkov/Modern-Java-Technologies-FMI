package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.time.Instant;

public class MyEvent implements Event<StringPayload> {

    private Instant timestamp;
    private int priority;
    private String source;
    private StringPayload payload;

    public MyEvent(Instant timestamp, int priority, String source, StringPayload payload) {
        this.timestamp = timestamp;
        this.priority = priority;
        this.source = source;
        this.payload = payload;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public StringPayload getPayload() {
        return payload;
    }
}
