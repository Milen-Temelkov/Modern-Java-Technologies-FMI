package bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReleaseDateItemFilter implements ItemFilter {
    private final LocalDateTime lowerBound;
    private final LocalDateTime upperBound;

    public ReleaseDateItemFilter(LocalDateTime lowerBound, LocalDateTime upperBound) {
        if(lowerBound.isAfter(upperBound)) {
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
        return item.getReleaseDate().isAfter(lowerBound) &&
                item.getReleaseDate().isBefore(upperBound);
    }
}