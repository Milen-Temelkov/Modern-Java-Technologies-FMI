package bg.sofia.uni.fmi.mjt.frauddetector.analyzer;

import bg.sofia.uni.fmi.mjt.frauddetector.rule.FrequencyRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.LocationsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.Rule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.SmallTransactionsRule;
import bg.sofia.uni.fmi.mjt.frauddetector.rule.ZScoreRule;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionAnalyzerImplTest {

    private static Reader reader;
    private static TransactionAnalyzerImpl transactionAnalyzer;

    @BeforeAll
    public static void setUp() {
        List<Rule> rules = List.of(
            new ZScoreRule(0.5, 0.3),
            new LocationsRule(3, 0.4),
            new FrequencyRule(4, Period.ofWeeks(4), 0.25),
            new SmallTransactionsRule(1, 10.20, 0.05)
        );

        String inputData = """
            transactionId,accountId,amount,transactionDate,location,channel
            TX000001,AC00128,10.0,2023-12-04 10:00:00,Burgas,ATM
            TX000002,AC00129,20.0,2023-12-04 10:01:00,Burgas,Online
            TX000003,AC00130,20.0,2023-12-04 10:02:00,Varna,Branch
            TX000004,AC00128,20.0,2023-12-04 10:03:00,Plovdiv,ATM
            """;

        reader = new StringReader(inputData);

        transactionAnalyzer = new TransactionAnalyzerImpl(reader, rules);
    }

    @Test
    public void testCreationWithInvalidReader() {
        assertThrows(IllegalArgumentException.class, () -> new TransactionAnalyzerImpl(null, Collections.emptyList()),
            "InvalidArgumentException should be thrown when reader is null");
    }

    @Test
    public void testCreationWithInvalidRulesWeight() {
        List<Rule> rules = List.of(
            new ZScoreRule(1, 1),
            new LocationsRule(3, 1),
            new FrequencyRule(4, Period.ofWeeks(4), 1),
            new SmallTransactionsRule(1, 10.20, 1)
        );

        assertThrows(IllegalArgumentException.class,
            () -> new TransactionAnalyzerImpl(reader, rules),
            "InvalidArgumentException should be thrown when rules weight surpasses 1");
    }

    @Test
    public void testAllTransactions() {
        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128", 10.0, LocalDateTime.of(2023, 12, 4, 10, 0, 0), "Burgas", Channel.ATM),
            new Transaction("TX000002", "AC00129", 20.0, LocalDateTime.of(2023, 12, 4, 10, 1, 0), "Burgas", Channel.ONLINE),
            new Transaction("TX000003", "AC00130", 20.0, LocalDateTime.of(2023, 12, 4, 10, 2, 0), "Varna", Channel.BRANCH),
            new Transaction("TX000004", "AC00128", 20.0, LocalDateTime.of(2023, 12, 4, 10, 3, 0), "Plovdiv", Channel.ATM)
        );

        assertIterableEquals(transactions, transactionAnalyzer.allTransactions(), "Transactions are set incorrectly");
    }

    @Test
    public void testAllAccountIDs() {
        List<String> accountIDs = List.of("AC00128", "AC00129", "AC00130");

        assertIterableEquals(accountIDs, transactionAnalyzer.allAccountIDs(),
            "Account IDs are incorrect");
    }

    @Test
    public void testTransactionCountByChannel() {
        Map<Channel, Integer> result = new HashMap<>();
        result.put(Channel.ATM, 2);
        result.put(Channel.ONLINE, 1);
        result.put(Channel.BRANCH, 1);

        assertEquals(result, transactionAnalyzer.transactionCountByChannel(),
            "Transaction count by channel is not calculated correctly");
    }

    @Test
    public void testTransactionCountByChannelWithMissingChannel() {
        String inputData = """
            transactionId,accountId,amount,transactionDate,location,channel
            TX000001,AC00128,10.0,2023-12-04 10:00:00,Burgas,ATM
            TX000002,AC00129,20.0,2023-12-04 10:01:00,Burgas,Online
            """;

        Reader localReader = new StringReader(inputData);

        TransactionAnalyzerImpl localAnalyzer = new TransactionAnalyzerImpl(localReader, List.of(new ZScoreRule(1, 1)));

        Map<Channel, Integer> result = new EnumMap<>(Channel.class);
        result.put(Channel.ATM, 1);
        result.put(Channel.ONLINE, 1);


        assertEquals(result, localAnalyzer.transactionCountByChannel(),
            "Transaction count by channel is not calculated correctly");
    }

    @Test
    public void testAmountSpentByUser() {
        final double result = 30d;

        assertEquals(result, transactionAnalyzer.amountSpentByUser("AC00128"),
            "Amount spent by AC00128 is different from expected result");
    }

    @Test
    public void testAllTransactionsByUser() {
        List<Transaction> result = List.of(
            new Transaction("TX000001", "AC00128", 10.0, LocalDateTime.of(2023, 12, 4, 10, 0, 0), "Burgas", Channel.ATM),
            new Transaction("TX000004", "AC00128", 20.0, LocalDateTime.of(2023, 12, 4, 10, 3, 0), "Plovdiv", Channel.ATM)
        );

        assertIterableEquals(result, transactionAnalyzer.allTransactionsByUser("AC00128"),
            "Transactions by user don't match actual");
    }

    @Test
    public void testAccountRatingWithUserWithZeroTransactions() {
        assertEquals(0.0, transactionAnalyzer.accountRating("AC00131"),
            "Account rating should return 0 because this user doesn't have any transactions");
    }

    @Test
    public void testAccountRating() {
        assertEquals(0.0, transactionAnalyzer.accountRating("AC00129"),
            "AC00129 rating should be 0 because no rules is applicable");

        assertEquals(0.35, transactionAnalyzer.accountRating("AC00128"),
            "AC00128 rating should be 0.35 because [SmallTransactionsRule + ZScoreRule] = 0.35");
    }

    @Test
    public void testAccountsRisk() {
        SortedMap<String, Double> result = new TreeMap<>();
        result.put("AC00128", 0.35);
        result.put("AC00129", 0.0);
        result.put("AC00130", 0.0);

        assertEquals(result, transactionAnalyzer.accountsRisk(),
            "Accounts risk doesn't match actual");
    }
}
