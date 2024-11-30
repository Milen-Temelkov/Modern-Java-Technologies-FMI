package bg.sofia.uni.fmi.mjt.glovo.delivery;

public enum DeliveryType {
    CAR(5, 3),
    BIKE(3, 5);

    private final int pricePerKM;
    private final int timePerKM;

    DeliveryType(int pricePerKM, int timePerKM) {
        this.pricePerKM = pricePerKM;
        this.timePerKM = timePerKM;
    }

    public int getPricePerKM() {
        return this.pricePerKM;
    }

    public int getTimePerKM() {
        return this.timePerKM;
    }
}
