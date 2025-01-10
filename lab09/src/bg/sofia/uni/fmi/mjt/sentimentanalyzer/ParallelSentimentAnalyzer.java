package bg.sofia.uni.fmi.mjt.sentimentanalyzer;

import bg.sofia.uni.fmi.mjt.sentimentanalyzer.exceptions.SentimentAnalysisException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ParallelSentimentAnalyzer implements SentimentAnalyzerAPI {

    private static final int MIN_SCORE = -5;
    private static final int MAX_SCORE = 5;

    private final int workersCount;
    private final Set<String> stopWords;
    private final Map<String, SentimentScore> sentimentLexicon;

    private final Queue<AnalyzerTask> taskQueue = new LinkedList<>();
    private final Map<String, SentimentScore> resultMap = new HashMap<>();

    private int producersCount;
    private final AtomicInteger producersDoneCount = new AtomicInteger(0);

    public ParallelSentimentAnalyzer(int workersCount,
                                     Set<String> stopWords,
                                     Map<String, SentimentScore> sentimentLexicon) {
        this.workersCount = workersCount;
        this.stopWords = stopWords;
        this.sentimentLexicon = sentimentLexicon;
    }

    @Override
    public Map<String, SentimentScore> analyze(AnalyzerInput... inputs) {
        this.producersCount = inputs.length;

        List<Thread> producerThreads = new ArrayList<>(producersCount);
        List<Thread> consumerThreads = new ArrayList<>(workersCount);

        for (int i = 0; i < producersCount; i++) {
            final AnalyzerInput input = inputs[i];
            producerThreads.add(new Thread(() -> produceTask(input)));
            producerThreads.getLast().start();
        }

        for (int i = 0; i < workersCount; i++) {
            consumerThreads.add(new Thread(this::consumeTasks));
            consumerThreads.getLast().start();
        }

        for (Thread producer : producerThreads) {
            joinThread(producer);
        }

        for (Thread consumer : consumerThreads) {
            joinThread(consumer);
        }

        return new HashMap<>(resultMap);
    }

    private void joinThread(Thread th) {
        try {
            th.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SentimentAnalysisException("Thread: " + th.getName() + " interrupted", e);
        }
    }

    private void produceTask(AnalyzerInput input) {
        synchronized (taskQueue) {
            String content;

            try (BufferedReader reader = new BufferedReader(input.inputReader())) {
                content = reader.lines().collect(Collectors.joining(" "));
            } catch (IOException e) {
                throw new SentimentAnalysisException("Error processing input: " + input.inputID(), e);
            }

            taskQueue.add(new AnalyzerTask(input.inputID(), content));
            producersDoneCount.incrementAndGet();
            taskQueue.notifyAll();
        }
    }

    private void consumeTasks() {
        while (!(taskQueue.isEmpty() && producersDoneCount.get() == producersCount)) {
            AnalyzerTask task;

            synchronized (taskQueue) {
                while (taskQueue.isEmpty() && producersDoneCount.get() < producersCount) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                task = taskQueue.poll();
            }

            if (task != null) {
                SentimentScore score = analyzeInput(task);
                synchronized (resultMap) {
                    resultMap.put(task.inputID(), score);
                }
            }
        }
    }

    private SentimentScore analyzeInput(AnalyzerTask task) {
        String[] words = task.content().toLowerCase()
                                .replaceAll("\\p{Punct}", "")
                                .split("\\s+");

        int totalScore = 0;

        for (String word : words) {
            if (!stopWords.contains(word)) {
                totalScore += sentimentLexicon.getOrDefault(word, SentimentScore.fromScore(0)).getScore();
            }
        }

        return SentimentScore.fromScore(Math.max(MIN_SCORE, Math.min(MAX_SCORE, totalScore)));
    }
}
