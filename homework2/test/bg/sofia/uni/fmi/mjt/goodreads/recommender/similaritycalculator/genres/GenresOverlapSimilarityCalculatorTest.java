package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GenresOverlapSimilarityCalculatorTest {

    private final SimilarityCalculator calc = new GenresOverlapSimilarityCalculator();

    @Test
    void testCalculateSimilarity() {
        Book first = new Book("1",
            "title1",
            "author1",
            "description1",
            List.of("genre1", "genre2", "genre3"),
            5.0,
            10,
            "https://www.book1.com");

        Book second = new Book("2",
            "title2",
            "author2",
            "description2",
            List.of("genre1", "genre2", "genre4", "genre5", "genre6", "genre7"),
            4.0,
            11,
            "https://www.book2.com");

        assertEquals(2d/3d, calc.calculateSimilarity(first, second));
    }

    @Test
    void testCalculateSimilarityNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> calc.calculateSimilarity(null, null),
            "Method should throw Illegal argument exception if any of the 2 arguments is null");
    }

}
