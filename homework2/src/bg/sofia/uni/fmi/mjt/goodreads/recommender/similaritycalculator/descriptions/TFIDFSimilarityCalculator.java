package bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.descriptions;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.recommender.similaritycalculator.SimilarityCalculator;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TFIDFSimilarityCalculator implements SimilarityCalculator {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public TFIDFSimilarityCalculator(Set<Book> books, TextTokenizer tokenizer) {
        this.books = books;
        this.tokenizer = tokenizer;
    }

    @Override
    public double calculateSimilarity(Book first, Book second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Arguments should not be null!");
        }

        Map<String, Double> tfIdfScoresFirst = computeTFIDF(first);
        Map<String, Double> tfIdfScoresSecond = computeTFIDF(second);

        return cosineSimilarity(tfIdfScoresFirst, tfIdfScoresSecond);
    }

    public Map<String, Double> computeTFIDF(Book book) {
        Map<String, Double> tFMap = computeTF(book);
        Map<String, Double> iDFMap = computeIDF(book);

        return tFMap.keySet().stream()
            .collect(Collectors.toMap(
                key -> key,
                key -> tFMap.get(key) * iDFMap.get(key)
            ));
    }

    public Map<String, Double> computeTF(Book book) {

        List<String> tokenizedDescription = tokenizer.tokenize(book.description());

        int totalWords = tokenizedDescription.size();

        Map<String, Double> tfMap = new HashMap<>();

        for (String word : tokenizedDescription) {
            tfMap.put(word, tfMap.getOrDefault(word, 0d) + 1);
        }

        tfMap.replaceAll((w, _) -> tfMap.get(w) / totalWords);

        return tfMap;
    }

    public Map<String, Double> computeIDF(Book book) {
        int totalBooks = books.size();

        Set<String> wordsInOrigin = new HashSet<>(tokenizer.tokenize(book.description()));

        List<Set<String>> wordsInEachBook = books.stream()
            .map(bk -> new HashSet<>(tokenizer.tokenize(bk.description())))
            .collect(Collectors.toList());

        Map<String, Integer> freqsPerWord = wordsInOrigin.stream()
                                            .collect(Collectors.toMap(
                                                word -> word,
                                                word -> Math.toIntExact(wordsInEachBook.stream()
                                                    .filter(bookWords -> bookWords.contains(word))
                                                    .count())));

        return wordsInOrigin.stream()
            .collect(Collectors.toMap(
                word -> word,
                word -> Math.log10((double) totalBooks / freqsPerWord.get(word))));
    }

    private double cosineSimilarity(Map<String, Double> first, Map<String, Double> second) {
        double magnitudeFirst = magnitude(first.values());
        double magnitudeSecond = magnitude(second.values());

        return dotProduct(first, second) / (magnitudeFirst * magnitudeSecond);
    }

    private double dotProduct(Map<String, Double> first, Map<String, Double> second) {
        Set<String> commonKeys = new HashSet<>(first.keySet());
        commonKeys.retainAll(second.keySet());

        return commonKeys.stream()
                .mapToDouble(word -> first.get(word) * second.get(word))
                .sum();
    }

    private double magnitude(Collection<Double> input) {
        double squaredMagnitude = input.stream()
                .map(v -> v * v)
                .reduce(0.0, Double::sum);

        return Math.sqrt(squaredMagnitude);
    }
}
