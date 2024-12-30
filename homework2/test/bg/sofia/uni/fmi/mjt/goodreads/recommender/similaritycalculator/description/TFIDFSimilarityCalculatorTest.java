package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.description;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions.TFIDFSimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TFIDFSimilarityCalculatorTest {

    private final Book first;
    private final Book second;
    private final Book third;
    private final TFIDFSimilarityCalculator calc;

    public TFIDFSimilarityCalculatorTest() {
        first = new Book("1",
            "title1",
            "author1",
            "academy superhero club superhero",
            List.of("genre1", "genre2", "genre3"),
            5.0,
            10,
            "https://www.book1.com");

        second = new Book("2",
            "title2",
            "author2",
            "superhero mission save club",
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

        TextTokenizer tokenizer = new TextTokenizer(Reader.nullReader());

        calc = new TFIDFSimilarityCalculator(Set.of(first, second, third), tokenizer);

    }

    @Test
    void testComputeTF() {
        Map<String, Double> freqs = new HashMap<>();
        freqs.put("academy", 0.25d);
        freqs.put("club", 0.25d);
        freqs.put("superhero", 0.5d);
        assertEquals(freqs, calc.computeTF(first), "TF is not calculated properly");
    }

    @Test
    void testComputeIDF() {
        Map<String, Double> freqs = new HashMap<>();
        freqs.put("academy", Math.log10(3d));
        freqs.put("club", Math.log10(1d));
        freqs.put("superhero", Math.log10(3/2d));
        assertEquals(freqs, calc.computeIDF(first), "IDF is not calculated properly");
    }

    @Test
    void testComputeTFIDF() {
        Map<String, Double> freqs = new HashMap<>();
        freqs.put("academy", 0.25d * Math.log10(3d));
        freqs.put("club", 0.25d * Math.log10(1d));
        freqs.put("superhero", 0.5d * Math.log10(3/2d));
        assertEquals(freqs, calc.computeTFIDF(first), "IDF is not calculated properly");
    }

    @Test
    public void testCalculateSimilarity() {
        double similarity = calc.calculateSimilarity(first, second);

        assertEquals(0.149, similarity, 0.001);
    }

    @Test
    public void testCalculateSimilarityWithNull() {
        assertThrows(IllegalArgumentException.class, () -> calc.calculateSimilarity(null, null),
            "Method should throw Illegal argument exception when given null!");
    }
}
