package bg.sofia.uni.fmi.mjt.gameplatform.store.item.category;

import bg.sofia.uni.fmi.mjt.gameplatform.store.item.StoreItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Game implements StoreItem {
    private String title;
    private BigDecimal price;
    private LocalDateTime releaseDate;
    private String genre;
    private double rating;
    private int ratingsCount;

    public Game(String title, BigDecimal price, LocalDateTime releaseDate, String genre) {
        setTitle(title);
        setPrice(price);
        setReleaseDate(releaseDate);
        setGenre(genre);
        rating = 0;
        ratingsCount = 0;
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

    public String getGenre() {
        return this.genre;
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

    public void setGenre(String genre) {
        if(genre == null || genre.isEmpty()) {
            this.genre = "Unknown";
        }
        else {
            this.genre = genre;
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
