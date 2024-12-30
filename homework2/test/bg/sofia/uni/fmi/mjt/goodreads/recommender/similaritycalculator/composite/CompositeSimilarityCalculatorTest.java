package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.composite;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.genres.GenresOverlapSimilarityCalculator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CompositeSimilarityCalculatorTest {

    private final Book first;
    private final Book second;
    private final SimilarityCalculator calc;

    public CompositeSimilarityCalculatorTest() {
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

        SimilarityCalculator calc1 = Mockito.mock(GenresOverlapSimilarityCalculator.class);
        when(calc1.calculateSimilarity(first, second)).thenReturn(2d/3d);


        SimilarityCalculator calc2 = Mockito.mock(TFIDFSimilarityCalculator.class);
        when(calc2.calculateSimilarity(first, second)).thenReturn(0.5);

        Map<SimilarityCalculator, Double> calcsMap = new HashMap<>();
        calcsMap.put(calc1, 0.5d);
        calcsMap.put(calc2, 0.5d);

        calc = new CompositeSimilarityCalculator(calcsMap);
    }

    @Test
    void testCalculateSimilarity() {
        assertEquals((2d/3d * 0.5d + 0.5d * 0.5d), calc.calculateSimilarity(first, second),
            "Calculate similarity doesn't calculate properly!");
    }

}
