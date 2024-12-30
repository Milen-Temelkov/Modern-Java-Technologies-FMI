package bg.sofia.uni.fmi.mjt.goodreads.tokenizer;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TextTokenizerTest {

    private final TextTokenizer tokenizer;

    public TextTokenizerTest() {
        String stopwords = """
            is
            it
            i
            dont
            the
            """;

        Reader reader = new StringReader(stopwords);

        tokenizer = new TextTokenizer(reader);
    }

    @Test
    void testTokenize() {
        String input = "\"Is it really necessary?\" I asked, unsure of what to do. " +
                        "\"I don't know,\" he replied, shrugging his shoulders. \"But if it isn't, why bother?\" " +
                        "Still, I couldn't shake the feeling that something wasn't right.";

        List<String> tokenizedWords = List.of("really",
            "necessary",
            "asked",
            "unsure",
            "of",
            "what",
            "to",
            "do",
            "know",
            "he",
            "replied",
            "shrugging",
            "his",
            "shoulders",
            "but",
            "if",
            "isnt",
            "why",
            "bother",
            "still",
            "couldnt",
            "shake",
            "feeling",
            "that",
            "something",
            "wasnt",
            "right");

        assertIterableEquals(tokenizedWords, tokenizer.tokenize(input),
            "Tokenizer misses words!");
    }
}
