package bg.sofia.uni.fmi.mjt.eventbus.events;

public class StringPayload implements Payload<String> {

    private String payload;

    public StringPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public int getSize() {
        return payload.length();
    }

    @Override
    public String getPayload() {
        return payload;
    }
}
