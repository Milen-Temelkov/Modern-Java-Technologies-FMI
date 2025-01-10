package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DLC implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private Game game;
    private double rating;
    private int ratingsCount;

    public DLC(String title, BigDecimal price, LocalDateTime releaseDate, Game game) {
        setTitle(title);
        setPrice(price);
        setReleaseDate(releaseDate);
        setGame(game);
    }

    @Override
    public String getTitle() {
        return  this.title;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public double getRating() {
        return this.rating;
    }

    @Override
    public LocalDateTime getReleaseDate() {
        return this.releaseDate;
    }

    public Game getGame() {
        return this.game;
    }

    @Override
    public void setTitle(String title) {
        if(title == null || title.isEmpty()) {
            this.title = "Unknown";
        }
        else {
            this.title = title;
        }
    }

    @Override
    public void setPrice(BigDecimal price) {
        if(price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            this.price = BigDecimal.ZERO.setScale(2);
        }
        else {
            this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

    @Override
    public void setReleaseDate(LocalDateTime releaseDate) {
        if(releaseDate == null || releaseDate.isAfter(LocalDateTime.now())) {
            this.releaseDate = LocalDateTime.now();
        }
        else {
            this.releaseDate = releaseDate;
        }
    }

    public void setGame(Game game) {
        if(game == null) {
            this.game = new Game("", BigDecimal.ZERO, LocalDateTime.now(), "");
        }
        else {
            this.game = game;
        }
    }

    @Override
    public void rate(double rating) {
        if(rating < 1) {
            rating = 1;
        }

        if(rating > 5) {
            rating = 5;
        }

        this.rating = ((this.rating * this.ratingsCount) + rating) / (this.ratingsCount + 1);
        this.ratingsCount++;
    }
}
