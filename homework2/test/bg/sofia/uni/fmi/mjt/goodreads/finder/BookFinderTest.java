package bg.sofia.uni.fmi.mjt.goodreads.finder;

import bg.sofia.uni.fmi.mjt.goodreads.book.Book;
import bg.sofia.uni.fmi.mjt.goodreads.tokenizer.TextTokenizer;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BookFinderTest {

    private final Book first = new Book("1",
        "title1",
        "author1",
        "academy superhero club superhero",
        List.of("genre1", "genre2", "genre3"),
        5.0,
        10,
        "https://www.book1.com");

    private final Book second = new Book("2",
        "title2",
        "author2",
        "superhero mission save club",
        List.of("genre1", "genre2", "genre4", "genre5", "genre6", "genre7"),
        4.0,
        11,
        "https://www.book2.com");

    private final Book third = new Book("3",
        "title3",
        "author3",
        "crime murder mystery club",
        List.of("genre1", "genre2", "genre3"),
        4.5,
        9,
        "https://www.book3.com");

    private final BookFinderAPI finder;

    public BookFinderTest() {
        finder = new BookFinder(Set.of(first, second, third),
                                new TextTokenizer(Reader.nullReader()));
    }

    @Test
    void testAllGenres() {
        Set<String> genres = Set.of("genre1", "genre2", "genre3", "genre4", "genre5", "genre6", "genre7");

        assertTrue(finder.allGenres().containsAll(genres) && genres.containsAll(finder.allGenres()),
            "Genres don't match!");
    }

    @Test
    void testSearchByAuthor() {
        assertTrue(finder.searchByAuthor("author1").containsAll(List.of(first)),
            "Books don't match");
    }

    @Test
    void testSearchByAuthorNullAuthorName() {
        assertThrows(IllegalArgumentException.class, () -> finder.searchByAuthor(null),
            "Method should throw Illegal argument when given null");
    }

    @Test
    void testSearchByAuthorEmptyAuthorName() {
        assertThrows(IllegalArgumentException.class, () -> finder.searchByAuthor(""),
            "Method should throw Illegal argument when given empty name");
    }

    @Test
    void testSearchByAllGenres() {
        Set<String> genres= Set.of("genre1", "genre2", "genre3");

        assertTrue(finder.searchByGenres(genres, MatchOption.MATCH_ALL).containsAll(List.of(first, third)),
            "Result list should include only the first and third book");
    }

    @Test
    void testSearchByAnyGenres() {
        Set<String> genres= Set.of("genre1", "genre2", "genre3");

        assertTrue(finder.searchByGenres(genres, MatchOption.MATCH_ANY).containsAll(List.of(first, second, third)) &&
                List.of(first, second, third).containsAll(finder.searchByGenres(genres, MatchOption.MATCH_ANY)),
            "Result list should include all books");
    }

    @Test
    void testSearchByNullGenres() {
        assertThrows(IllegalArgumentException.class, () -> finder.searchByGenres(null, MatchOption.MATCH_ANY),
            "Method should throw Illegal argument when given null genres");
    }

    @Test
    void testSearchByAllKeywords() {
        Set<String> keywords = Set.of("title1", "academy", "superhero", "club");

        assertTrue(finder.searchByKeywords(keywords, MatchOption.MATCH_ALL).containsAll(List.of(first)) &&
                List.of(first).containsAll(finder.searchByKeywords(keywords, MatchOption.MATCH_ALL)),
            "Result list should include only the first book");
    }

    @Test
    void testSearchByAnyKeywords() {
        Set<String> keywords = Set.of("title1", "superhero", "club");

        assertTrue(finder.searchByKeywords(keywords, MatchOption.MATCH_ANY).containsAll(List.of(first, second, third)) &&
                List.of(first, second, third).containsAll(finder.searchByKeywords(keywords, MatchOption.MATCH_ANY)),
            "Result list should include all books");
    }
}
