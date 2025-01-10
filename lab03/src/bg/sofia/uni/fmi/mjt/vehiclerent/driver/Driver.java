package bg.sofia.uni.fmi.mjt.vehiclerent.driver;

public class Driver {
    private AgeGroup group;

    public Driver(AgeGroup group) {
        this.group = group;
    }

    public double getTax() {
        return group.getTax();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
