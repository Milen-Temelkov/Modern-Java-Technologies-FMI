package bg.sofia.uni.fmi.mjt.gameplatform.store;


import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;
import bg.sofia.uni.fmi.mjt.gameplatform.store.item.filter.ItemFilter;

import java.math.BigDecimal;
import java.util.Arrays;

public class GameStore implements StoreAPI {
    private StoreItem[] availableItems;
    private final boolean[] appliedDiscountCodes; // :(

    public GameStore(StoreItem[] availableItems) {
        if (availableItems == null) {
            availableItems = new StoreItem[0];
        }
        else {
            this.availableItems = availableItems;
        }
        this.appliedDiscountCodes = new boolean[2]; //lo6o magi4esko 4islo
    }

    @Override
    public StoreItem[] findItemByFilters(ItemFilter[] itemFilters) {
        StoreItem[] foundItems = new StoreItem[this.availableItems.length];
        int foundIndex = 0;
        boolean matchesFilters = true;

        for (StoreItem item : availableItems) {
            for (ItemFilter filter : itemFilters) {
                if (!filter.matches(item)) {
                    matchesFilters = false;
                    break;
                }
            }
            if (matchesFilters) {
                foundItems[foundIndex] = item;
                foundIndex++;
            }
            matchesFilters = true;
        }

        return Arrays.copyOfRange(foundItems, 0, foundIndex);
    }

    @Override
    public void applyDiscount(String promoCode) {
        double percentage = switch (promoCode) {
            case "VAN40" -> {
                if (!appliedDiscountCodes[0]) {
                    appliedDiscountCodes[0] = true;
                    yield 0.6;
                }
                yield 1.0;
            }
            case "100YO" -> {
                if (!appliedDiscountCodes[1]) {
                    appliedDiscountCodes[1] = true;
                    yield 0.0;
                }
                yield 1.0;
            }
            default -> 1.0;
        };



        for (StoreItem item : availableItems) {
            item.setPrice(item.getPrice().multiply(new BigDecimal(percentage)));
        }
    }

    @Override
    public boolean rateItem(StoreItem item, int rating) {
        if (rating < 1 || rating > 5) {
            return false;
        }
        for (StoreItem searchedItem : availableItems) {
            if (searchedItem.equals(item)) {
                searchedItem.rate(rating);
                return true;
            }
        }
        return false;
    }
}
