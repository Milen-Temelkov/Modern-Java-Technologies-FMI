package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;


import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GameBundle implements StoreItem {


    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private Game[] games;
    private double rating;
    private int ratingsCount;

    public GameBundle(String title, BigDecimal price, LocalDateTime releaseDate, Game[] games) {
        setTitle(title);
        setPrice(price);
        setReleaseDate(releaseDate);
        setGames(games);
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

    public Game[] getGames() {
        return this.games;
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

    public void setGames(Game[] games) {
        if(games == null || games.length == 0) {
            this.games = new Game[0]; //????
        }
        else {
            this.games = games;
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
