package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.util.List;

public class SmallTransactionsRule implements Rule {

    private final int countThreshold;
    private final double amountThreshold;
    private final double weight;

    public SmallTransactionsRule(int countThreshold, double amountThreshold, double weight) {
        this.countThreshold = countThreshold;
        this.amountThreshold = amountThreshold;
        this.weight = weight;
    }

    /**
     * Determines whether the rule is applicable based on the given list of transactions.
     *
     * @param transactions the list of objects to evaluate.
     *                     These transactions are used to determine if the rule
     *                     conditions are satisfied.
     * @return true, if the rule is applicable based on the transactions.
     */
    @Override
    public boolean applicable(List<Transaction> transactions) {
        if (transactions == null) {
            throw new IllegalArgumentException("Transactions cannot be null!");
        }

        if (transactions.isEmpty()) {
            return false;
        }

        return countThreshold <= transactions.stream()
                                            .map(Transaction::transactionAmount)
                                            .filter((amount) -> amount <= amountThreshold)
                                            .count();
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
