package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookRecommenderTest {

    private final Book first;
    private final Book second;
    private final Book third;
    private final BookRecommender recommender;

    public BookRecommenderTest() {
        first = new Book("1",
            "title1",
            "author1",
            "description1",
            List.of("genre1", "genre2", "genre3"),
            5.0,
            10,
            "https://www.book1.com");

        second = new Book("2",
            "title2",
            "author2",
            "description2",
            List.of("genre1", "genre2", "genre4", "genre5", "genre6", "genre7"),
            4.0,
            11,
            "https://www.book2.com");

        third = new Book("3",
            "title3",
            "author3",
            "crime murder mystery club",
            List.of("genre1", "genre2", "genre3"),
            4.5,
            9,
            "https://www.book3.com");

        recommender = new BookRecommender(Set.of(second, third), new GenresOverlapSimilarityCalculator());
    }

    @Test
    void testRecommendBookWithNullOrigin() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(null, 1),
            "Method should throw Illegal argument exception when given null book!");
    }

    @Test
    void testRecommendBookWithInvalidN() {
        assertThrows(IllegalArgumentException.class, () -> recommender.recommendBooks(first, 0),
            "Method should throw Illegal argument exception when given maxN <= 0!");
    }

    @Test
    void testRecommendBook() {
        SortedMap<Book, Double> result = recommender.recommendBooks(first, 1);
        assertEquals(1, result.keySet().size(),
        "Result map should contain only 1 key");
        assertEquals(1, result.get(third),
        "First and third book have same genres so the score should be 1");
    }
}
