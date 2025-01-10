package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;

public class PriceItemFilter implements ItemFilter {
    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;

    public PriceItemFilter(BigDecimal lowerBound, BigDecimal upperBound) {
        if (lowerBound.compareTo(upperBound) > 0) {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
        }
        else {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }
    }

    @Override
    public boolean matches(StoreItem item) {
        return (item.getPrice().compareTo(this.lowerBound) > 0) &&
                (item.getPrice().compareTo(this.upperBound) < 0);
    }
}
