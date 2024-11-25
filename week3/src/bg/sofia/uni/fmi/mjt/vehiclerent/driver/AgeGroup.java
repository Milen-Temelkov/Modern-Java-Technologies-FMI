package bg.sofia.uni.fmi.mjt.vehiclerent.driver;

public enum AgeGroup {
    JUNIOR(10),
    EXPERIENCED(0),
    SENIOR(15);

    private final int tax;

    private AgeGroup(int tax) {
        this.tax = tax;
    }

    public int getTax() {
        return this.tax;
    }
}
