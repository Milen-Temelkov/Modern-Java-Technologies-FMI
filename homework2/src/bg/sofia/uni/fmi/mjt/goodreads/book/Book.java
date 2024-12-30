package bg.sofia.uni.fmi.mjt.goodreads.book;

import java.util.List;

public record Book(
        String ID,
        String title,
        String author,
        String description,
        List<String> genres,
        double rating,
        int ratingCount,
        String URL
) {

    private static final int ID_INDEX = 0;
    private static final int TITLE_INDEX = 1;
    private static final int AUTHOR_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int GENRES_INDEX = 4;
    private static final int RATING_INDEX = 5;
    private static final int RATINGS_COUNT_INDEX = 6;
    private static final int URL_INDEX = 7;

    public static Book of(String[] tokens) {

        return new Book(
            tokens[ID_INDEX],
            tokens[TITLE_INDEX],
            tokens[AUTHOR_INDEX],
            tokens[DESCRIPTION_INDEX],
            parseGenres(tokens[GENRES_INDEX]),
            Double.parseDouble(tokens[RATING_INDEX]),
            parseRatingsCount(tokens[RATINGS_COUNT_INDEX]),
            tokens[URL_INDEX]);
    }

    private static List<String> parseGenres(String genres) {
        String stripped = genres.replace("[", "")
                                .replace("]", "")
                                .replace("'", "");
        String[] singleGenres = stripped.split(", ");

        return List.of(singleGenres);
    }

    private static int parseRatingsCount(String ratingsCount) {
        return Integer.parseInt(ratingsCount.replace(",", "").replace("\"", ""));
    }
}
