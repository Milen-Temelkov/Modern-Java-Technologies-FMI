package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookFinder implements BookFinderAPI {

    private final Set<Book> books;
    private final TextTokenizer tokenizer;

    public BookFinder(Set<Book> books, TextTokenizer tokenizer) {
        this.books = books;
        this.tokenizer = tokenizer;
    }

    public Set<Book> allBooks() {
        return books;
    }

    @Override
    public Set<String> allGenres() {
        return books.stream()
            .map(Book::genres)
            .flatMap(List::stream)
            .collect(Collectors.toSet());

    }

    @Override
    public List<Book> searchByAuthor(String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty!");
        }

        return books.stream()
            .filter((book) -> book.author().equals(authorName))
            .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByGenres(Set<String> genres, MatchOption option) {
        if (genres == null) {
            throw new IllegalArgumentException("Genres cannot be null!");
        }

        return switch (option) {
            case MATCH_ALL -> books.stream()
                .filter(book -> new HashSet<>(book.genres()).containsAll(genres))
                .collect(Collectors.toList());
            case MATCH_ANY -> books.stream()
                .filter(book -> !Collections.disjoint(book.genres(), genres))
                .collect(Collectors.toList());
        };
    }

    @Override
    public List<Book> searchByKeywords(Set<String> keywords, MatchOption option) {
        return switch (option) {

            case MATCH_ALL -> books.stream()
                .filter(book -> {
                    Set<String> wordsInBook = new HashSet<>();

                    wordsInBook.addAll(tokenizer.tokenize(book.title()));
                    wordsInBook.addAll(tokenizer.tokenize(book.description()));

                    return wordsInBook.containsAll(keywords);
                })
                .collect(Collectors.toList());
            case MATCH_ANY -> books.stream()
                .filter(book -> {
                    return !Collections.disjoint(new HashSet<>(tokenizer.tokenize(book.title())), keywords) ||
                        !Collections.disjoint(new HashSet<>(tokenizer.tokenize(book.description())), keywords);
                })
                .collect(Collectors.toList());
        };
    }
    
}
