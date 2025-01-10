package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;
import java.util.function.Function;

public class ZScoreRule implements Rule {

    public static final double DELTA = 0.0001;

    private final double zScoreThreshold;
    private final double weight;

    public ZScoreRule(double zScoreThreshold, double weight) {
        this.zScoreThreshold = zScoreThreshold;
        this.weight = weight;
    }

    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions cannot be null!");
        }

        if (transactions.isEmpty()) {
            return false;
        }

        double mean = calculateMetric(transactions, Transaction::transactionAmount);
        double variance = calculateMetric(transactions,
            ((Transaction tr) -> Math.pow((tr.transactionAmount() - mean), 2)));

        if (variance == 0) {
            return false;
        }

        for (Transaction tr : transactions) {
            double zScore = calculateZScore(tr, mean, variance);

            if ((zScore - zScoreThreshold) > DELTA) {
                return true;
            }
        }

        return false;
    }

    private double calculateZScore(Transaction transaction, double mean, double variance) {
        return (transaction.transactionAmount() - mean) / Math.sqrt(variance);
    }

    private double calculateMetric(List<Transaction> transactions, Function<Transaction, Double> sumArgument) {
        int count = transactions.size();
        double amount = 0d;

        for (Transaction tr : transactions) {
            amount += sumArgument.apply(tr);
        }

        return count == 0 ? 0 : amount / count;
    }

    @Override
    public double weight() {
        return weight;
    }
}
