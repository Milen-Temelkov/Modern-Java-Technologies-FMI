package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SequentialSentimentAnalyzer implements SentimentAnalyzerAPI {

    private static final int MIN_SCORE = -5;
    private static final int MAX_SCORE = 5;

    private final Set<String> stopWords;
    private final Map<String, Integer> sentimentLexicon;

    private final Queue<AnalyzerTask> taskQueue = new LinkedList<>();
    private final Map<String, SentimentScore> resultMap = new HashMap<>();


    public SequentialSentimentAnalyzer(Set<String> stopWords, Map<String, Integer> sentimentLexicon) {
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... inputs) {
        for (final AnalyzerInput input : inputs) {
            produceTask(input);
        }

        for (int i = 0; i < inputs.length; i++) {
            consumeTasks();
        }

        return new HashMap<>(resultMap);
    }

    private void produceTask(AnalyzerInput input) {
        String content;

        try (BufferedReader reader = new BufferedReader(input.inputReader())) {
            content = reader.lines().collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new SentimentAnalysisException("Error processing input: " + input.inputID(), e);
        }

        taskQueue.add(new AnalyzerTask(input.inputID(), content));
    }

    private void consumeTasks() {
        AnalyzerTask task = taskQueue.poll();

        if (task != null) {
            SentimentScore score = analyzeInput(task);
            resultMap.put(task.inputID(), score);
        }
    }

    private SentimentScore analyzeInput(AnalyzerTask task) {
        String[] words = task.content().toLowerCase()
            .replaceAll("\\p{Punct}", "")
            .split("\\s+");

        int totalScore = 0;

        for (String word : words) {
            if (!stopWords.contains(word)) {
                totalScore += sentimentLexicon.getOrDefault(word, 0);
            }
        }

        return SentimentScore.fromScore(Math.max(MIN_SCORE, Math.min(MAX_SCORE, totalScore)));
    }
}
