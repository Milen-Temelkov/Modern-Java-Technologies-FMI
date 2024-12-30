package bg.sofia.uni.fmi.mjt.goodreads.recommender;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookRecommender implements BookRecommenderAPI {

    private Set<Book> initialBooks;
    private SimilarityCalculator calculator;

    public BookRecommender(Set<Book> initialBooks, SimilarityCalculator calculator) {
        this.initialBooks = initialBooks;
        this.calculator = calculator;
    }

    @Override
    public SortedMap<Book, Double> recommendBooks(Book origin, int maxN) {
        validateInput(origin, maxN);

        Map<Book, Double> booksWithScores = initialBooks.stream()
            .filter(book -> !book.equals(origin))
            .collect(Collectors.toMap(
                book -> book,
                book -> calculator.calculateSimilarity(origin, book)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<Book, Double>comparingByValue(Comparator.reverseOrder()))
            .limit(maxN)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        SortedMap<Book, Double> sortedMap = new TreeMap<>((b1, b2) -> {
            int scoreComparison = booksWithScores.get(b2).compareTo(booksWithScores.get(b1));
            if (scoreComparison != 0) {
                return scoreComparison;
            }
            return b1.ID().compareTo(b2.ID());
        });

        sortedMap.putAll(booksWithScores);
        return sortedMap;
    }

    private void validateInput(Book origin, int maxN) {
        if (origin == null) {
            throw new IllegalArgumentException("Origin book should not be null!");
        }

        if (maxN <= 0) {
            throw new IllegalArgumentException("Max number of entries returned(maxN) should be positive number!");
        }
    }
}
