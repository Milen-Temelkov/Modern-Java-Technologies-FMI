package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZScoreRuleTest {
    @Test
    public void testApplicableWithNullTransactions() {
        Rule rule = new ZScoreRule(10, 1);

        assertThrows(IllegalArgumentException.class, () -> rule.applicable(null),
            "Applicable should throw IllegalArgumentException when given null list");
    }

    @Test
    public void testApplicableWithEmptyTransactions() {
        Rule rule = new ZScoreRule(10, 1);

        List<Transaction> transactions = Collections.emptyList();
        
        assertFalse(rule.applicable(transactions),
            "Applicable should throw IllegalArgumentException when given empty list");
    }

    @Test
    void testApplicable() {
        Rule rule = new ZScoreRule(1, 1);

        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128", 100.0, LocalDateTime.now().minusMinutes(2), "A", Channel.ATM),
            new Transaction("TX000002", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "B", Channel.ATM),
            new Transaction("TX000003", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "C", Channel.ATM)
        );

        assertTrue(rule.applicable(transactions),
            "Z score rule should be applicable when one z score is greater than z score threshold");
    }

    @Test
    void testNonApplicable() {
        Rule rule = new ZScoreRule(1, 1);

        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128", 10, LocalDateTime.now().minusMinutes(2), "A", Channel.ATM),
            new Transaction("TX000002", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "B", Channel.ATM),
            new Transaction("TX000003", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "C", Channel.ATM)
        );

        assertFalse(rule.applicable(transactions),
            "Z score rule should not be applicable when no one z score is greater than z score threshold");
    }

    @Test
    void testNonApplicableWithStandardDeviation() {
        Rule rule = new ZScoreRule(1, 1);

        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128", 20.0, LocalDateTime.now().minusMinutes(2), "A", Channel.ATM),
            new Transaction("TX000002", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "B", Channel.ATM),
            new Transaction("TX000003", "AC00128", 20.0, LocalDateTime.now().minusMinutes(1), "C", Channel.ATM)
        );

        assertFalse(rule.applicable(transactions),
            "Z score rule should not be applicable when no one z score is greater than z score threshold");
    }
}
