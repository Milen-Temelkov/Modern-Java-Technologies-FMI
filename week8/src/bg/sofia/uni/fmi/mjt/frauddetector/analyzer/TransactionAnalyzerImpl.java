package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Double.compare;

public class TransactionAnalyzerImpl implements TransactionAnalyzer {

    List<Rule> rules;
    List<Transaction> transactions;

    public TransactionAnalyzerImpl(Reader reader, List<Rule> rules) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null!");
        }

        double weightsSum = rules.stream().map(Rule::weight).reduce(Double::sum).orElse(0d);
        if (weightsSum != 1d) {
            throw new IllegalArgumentException("Weights doesn't sum to 1");
        }

        this.rules = rules;

        BufferedReader buffReader = new BufferedReader(reader);
        transactions = buffReader.lines().skip(1).map(Transaction::of).toList();
    }


    /**
     * Retrieves all transactions loaded into the analyzer.
     *
     * @return a list of all transactions.
     */
    @Override
    public List<Transaction> allTransactions() {
        return transactions;
    }

    /**
     * Retrieves all unique account IDs from the loaded transactions.
     *
     * @return a list of unique account IDs as strings.
     */
    @Override
    public List<String> allAccountIDs() {
        return transactions.stream().map(Transaction::accountID).distinct().toList();
    }

    /**
     * Retrieves a map of transaction counts grouped by the channel of the transaction.
     *
     * @return a map where keys are Channel values and values are the count of transactions for each channel.
     */
    @Override
    public Map<Channel, Integer> transactionCountByChannel() {
        Map<Channel, Integer> result = new EnumMap<>(Channel.class);

        for (Transaction tr : transactions) {
            result.putIfAbsent(tr.channel(), 0);
            result.put(tr.channel(), (result.get(tr.channel()) + 1));
        }

        return result;
    }

    /**
     * Calculates the total amount spent by a specific user, identified by their account ID.
     *
     * @param accountID the account ID for which the total amount spent is calculated.
     * @return the total amount spent by the user as a double.
     * @throws IllegalArgumentException if the accountID is null or empty.
     */
    @Override
    public double amountSpentByUser(String accountID) {
        if (accountID == null) {
            throw new IllegalArgumentException("AccountID cannot be null!");
        }

        if (accountID.isEmpty()) {
            throw new IllegalArgumentException("AccountID cannot be empty!");
        }

        Optional<Double> amount = transactions.stream()
            .filter(tr -> tr.accountID().equals(accountID))
            .map(Transaction::transactionAmount)
            .reduce(Double::sum);

        return amount.isPresent() ? amount.get() : 0;
    }

    /**
     * Retrieves all transactions associated with a specific account ID.
     *
     * @param accountId the account ID for which transactions are retrieved.
     * @return a list of Transaction objects associated with the specified account.
     * @throws IllegalArgumentException if the account ID is null or empty.
     */
    @Override
    public List<Transaction> allTransactionsByUser(String accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountID cannot be null!");
        }

        if (accountId.isEmpty()) {
            throw new IllegalArgumentException("AccountID cannot be empty!");
        }

        return transactions.stream()
            .filter(tr -> tr.accountID().equals(accountId))
            .toList();
    }

    /**
     * Returns the risk rating of an account with the specified ID.
     *
     * @return the risk rating as a double-precision floating-point number in the interval [0.0, 1.0].
     * @throws IllegalArgumentException if the account ID is null or empty.
     */
    @Override
    public double accountRating(String accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountID cannot be null!");
        }

        if (accountId.isEmpty()) {
            throw new IllegalArgumentException("AccountID cannot be empty!");
        }

        List<Transaction> transactionsOfUser = transactions.stream()
                                                .filter((tr) -> tr.accountID().equals(accountId))
                                                .toList();

        return rules.stream()
            .filter(rule -> rule.applicable(transactions))
            .mapToDouble(Rule::weight)
            .reduce(0.0, Double::sum);

    }

    /**
     * Calculates the risk score for each account based on the loaded rules.
     * The score for each account is a double-precision floating-point number in the interval [0, 1] and is
     * formed by summing up the weights of all applicable rules to the account transactions.
     * The result is sorted in descending order, with the highest-risk accounts listed first.
     *
     * @return a map where keys are account IDs (strings) and values are risk scores (doubles).
     */
    @Override
    public SortedMap<String, Double> accountsRisk() {
        Set<String> accounts = transactions.stream().map(Transaction::accountID).collect(Collectors.toSet());
        Map<String, Double> accountsRisk = new HashMap<>();

        for (String accID : accounts) {
            accountsRisk.put(accID, accountRating(accID));
        }

        List<Map.Entry<String, Double>> entries = new ArrayList<>(accountsRisk.entrySet());
        entries.sort((e1, e2) -> compare(e1.getValue(), e2.getValue()));

        SortedMap<String, Double> sortedAccountsRisk = new TreeMap<>(Comparator.naturalOrder());

        for (var entry : entries) {
            sortedAccountsRisk.put(entry.getKey(), entry.getValue());
        }

        return sortedAccountsRisk;
    }
}
