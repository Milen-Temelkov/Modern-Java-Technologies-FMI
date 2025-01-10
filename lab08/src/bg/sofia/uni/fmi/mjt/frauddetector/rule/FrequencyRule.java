package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class FrequencyRule implements Rule {

    private final int transactionCountThreshold;
    private final TemporalAmount timeWindow;
    private final double weight;

    public FrequencyRule(int transactionCountThreshold, TemporalAmount timeWindow, double weight) {
        this.transactionCountThreshold = transactionCountThreshold;
        this.timeWindow = timeWindow;
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

        transactions.sort(Comparator.comparing(Transaction::transactionDate));
        LocalDateTime leftEndDate = transactions.getFirst().transactionDate();
        LocalDateTime rightEndDate = null;
        Queue<Transaction> slidingWindow = new ArrayDeque<>();
        slidingWindow.add(transactions.getFirst());

        for (Transaction tr : transactions) {
            slidingWindow.add(tr);
            rightEndDate = tr.transactionDate();

            while (!slidingWindow.isEmpty() &&
                    isOutsideWindow(leftEndDate, rightEndDate)) {
                leftEndDate = slidingWindow.poll().transactionDate();
            }

            if (slidingWindow.size() >= transactionCountThreshold) {
                return true;
            }
        }
        return false;
    }

    private boolean isOutsideWindow(LocalDateTime leftEndDate, LocalDateTime rightEndDate) {
        if (timeWindow instanceof Duration) {
            return Duration.between(leftEndDate, rightEndDate).compareTo((Duration) timeWindow) >= 0;

        } else if (timeWindow instanceof Period) {
            LocalDate leftDate = leftEndDate.toLocalDate();
            LocalDate rightDate = rightEndDate.toLocalDate();
            Period actualPeriod = Period.between(leftDate, rightDate);

            return actualPeriod.getYears() > ((Period) timeWindow).getYears() ||
                (actualPeriod.getYears() == ((Period) timeWindow).getYears() &&
                    actualPeriod.getMonths() > ((Period) timeWindow).getMonths()) ||
                (actualPeriod.getYears() == ((Period) timeWindow).getYears() &&
                    actualPeriod.getMonths() == ((Period) timeWindow).getMonths() &&
                    actualPeriod.getDays() >= ((Period) timeWindow).getDays());
        } else {
            return false;
        }
    }

    /**
     * Retrieves the weight of the rule.
     * The weight represents the importance or priority of the rule
     * and is a double-precision floating-point number in the interval [0, 1].
     *
     * @return the weight of the rule.
     */
    @Override
    public double weight() {
        return weight;
    }
}
