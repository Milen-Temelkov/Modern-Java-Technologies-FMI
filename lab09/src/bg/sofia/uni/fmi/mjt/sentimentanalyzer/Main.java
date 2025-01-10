import bg.sofia.uni.fmi.mjt.sentimentanalyzer.AnalyzerInput;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.ParallelSentimentAnalyzer;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.SentimentScore;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.SequentialSentimentAnalyzer;
import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Примерен лексикон с оценки на думи
        Map<String, SentimentScore> sentimentLexicon = new HashMap<>();
        sentimentLexicon.put("love", SentimentScore.fromScore(4));
        sentimentLexicon.put("hate", SentimentScore.fromScore(-4));
        sentimentLexicon.put("amazing", SentimentScore.fromScore(5));
        sentimentLexicon.put("great", SentimentScore.fromScore(3));
        sentimentLexicon.put("excited", SentimentScore.fromScore(4));
        sentimentLexicon.put("frustrating", SentimentScore.fromScore(-3));
        sentimentLexicon.put("fantastic", SentimentScore.fromScore(5));
        sentimentLexicon.put("overwhelming", SentimentScore.fromScore(-2));
        sentimentLexicon.put("disappointing", SentimentScore.fromScore(-3));
        sentimentLexicon.put("joy", SentimentScore.fromScore(4));
        sentimentLexicon.put("stressful", SentimentScore.fromScore(-3));
        sentimentLexicon.put("empowering", SentimentScore.fromScore(4));
        sentimentLexicon.put("challenging", SentimentScore.fromScore(-2));
        sentimentLexicon.put("rewarding", SentimentScore.fromScore(4));

        // Примерен списък със стоп думи
        Set<String> stopWords = new HashSet<>();
        stopWords.add("and");
        stopWords.add("is");
        stopWords.add("the");
        stopWords.add("it");
        stopWords.add("to");
        stopWords.add("of");
        stopWords.add("a");

        // Създаване на примерни входни данни
        AnalyzerInput input1 = new AnalyzerInput("doc1", new StringReader(
            "I absolutely love the new features in Java! The performance improvements are amazing, and I am excited to see how much faster the applications will run. It's great to see the Java community working so hard to make it better. However, I do hate the bugs that sometimes occur when you're working on a project, especially when they happen unexpectedly."
        ));

        AnalyzerInput input2 = new AnalyzerInput("doc2", new StringReader(
            "Although I enjoy programming in Java, sometimes I feel that there are too many libraries to keep track of. It can get overwhelming, and at times I feel frustrated when the libraries don't work as expected. I also hate when I encounter long compilation times, which can sometimes make it feel like I'm wasting time instead of making progress. Despite all this, I still appreciate the flexibility and power of Java as a language."
        ));

        AnalyzerInput input3 = new AnalyzerInput("doc3", new StringReader(
            "Python is a fantastic language for rapid development, but sometimes it can be a bit slow. I love how simple and intuitive the syntax is, which makes it great for beginners. However, I don't like the fact that Python is not as performant as other languages like C++ when it comes to computational-heavy tasks. I am also a bit annoyed by the fact that Python has such a vast number of frameworks and libraries, which makes it difficult to know where to start."
        ));

        AnalyzerInput input4 = new AnalyzerInput("doc4", new StringReader(
            "I really love how JavaScript allows you to create dynamic and interactive web applications. It's such a versatile language, and I enjoy the flexibility it offers. On the other hand, JavaScript can be quite frustrating at times due to its asynchronous nature and the number of callback functions needed to handle things like API requests. Sometimes it feels like you can never escape the callback hell. Despite that, I would say JavaScript is still one of my favorite languages."
        ));

        AnalyzerInput input5 = new AnalyzerInput("doc5", new StringReader(
            "Learning to program can be a challenging yet rewarding experience. It can be overwhelming at first, especially when you're trying to grasp concepts like object-oriented programming, recursion, or data structures. But once you get the hang of it, the sense of accomplishment is incredibly satisfying. While programming can sometimes feel frustrating due to bugs and issues that arise unexpectedly, the ability to solve problems with code is truly empowering."
        ));

        AnalyzerInput input6 = new AnalyzerInput("doc6", new StringReader(
            "Working with databases can sometimes be an absolute nightmare. The constant need to optimize queries, handle indexing issues, and deal with database performance can be frustrating. However, when everything is running smoothly, there's nothing quite like the satisfaction of executing a well-optimized query. I also really appreciate the power of SQL and how it allows you to interact with data in such a flexible and efficient way. Still, the complexities of database management can be a bit overwhelming at times."
        ));

        AnalyzerInput input7 = new AnalyzerInput("doc7", new StringReader(
            "React.js has revolutionized the way we build modern web applications. It allows developers to create reusable components and build applications with great user interfaces. That said, learning React can be a steep learning curve for new developers, and it sometimes feels like there's an overwhelming number of tools and libraries to learn. Despite these challenges, React is still my go-to library for web development, and I love the speed at which I can build applications using it."
        ));

        AnalyzerInput input8 = new AnalyzerInput("doc8", new StringReader(
            "Machine learning is one of the most exciting fields in technology right now. There are endless possibilities for how it can revolutionize industries from healthcare to finance to entertainment. The algorithms are becoming more sophisticated, and the data we have access to is growing exponentially. However, I do find the complexity of machine learning models a bit overwhelming, and sometimes it feels like there's so much to learn and keep up with. But the potential of this technology makes it all worth it."
        ));

        AnalyzerInput input9 = new AnalyzerInput("doc9", new StringReader(
            "In today's fast-paced world, it's easy to feel overwhelmed and stressed. The constant demands of work, social media, and personal life can leave little time for relaxation. It's important to find ways to unwind and recharge, but that's easier said than done. Meditation and mindfulness have been a huge help for me in managing stress, though I sometimes struggle to make time for them amidst everything else going on. Still, I'm glad I've made it a priority."
        ));

        AnalyzerInput input10 = new AnalyzerInput("doc10", new StringReader(
            "Traveling has always been one of my biggest passions. Exploring new cities, experiencing different cultures, and trying new foods is something that brings me immense joy. That said, travel can also come with its frustrations. Flight delays, long security lines, and unexpected changes in plans can be stressful. But despite these occasional setbacks, the joy of discovering new places always makes it worth it. Traveling is one of the best ways to grow as a person."
        ));

        // Създаване на ParallelSentimentAnalyzer обект с 3 worker нишки
        ParallelSentimentAnalyzer analyzer = new ParallelSentimentAnalyzer(3, stopWords, sentimentLexicon);
        // SequentialSentimentAnalyzer analyzer = new SequentialSentimentAnalyzer(stopWords, sentimentLexicon);

        try {
            // Извършване на анализа
            Map<String, SentimentScore> results = analyzer.analyze(input1, input2, input3, input4, input5, input6, input7, input8, input9, input10);

            // Извеждане на резултатите
            results.forEach((id, score) -> System.out.println("Document ID: " + id + " - Sentiment: " + score.getDescription()));
        } catch (SentimentAnalysisException e) {
            System.out.println("An error occurred during sentiment analysis: " + e.getMessage());
        }
    }
}
