package bg.sofia.uni.fmi.mjt.frauddetector.rule;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrequencyRuleTest {
    @Test
    public void testApplicableWithNullTransactions() {
        Rule rule = new FrequencyRule(2, Duration.ofMinutes(1), 1);

        assertThrows(IllegalArgumentException.class, () -> rule.applicable(null),
            "Applicable should throw IllegalArgumentException when given null list");
    }

    @Test
    public void testApplicableWithEmptyTransactions() {
        Rule rule = new FrequencyRule(2, Duration.ofMinutes(1), 1);

        List<Transaction> transactions = Collections.emptyList();

        assertFalse(rule.applicable(transactions), "Applicable should return false when given empty list");
    }

    @Test
    public void testApplicableWithLessTransactionsSizeThanTransactionCountThreshold() {
        Rule rule = new FrequencyRule(2, Duration.ofMinutes(1), 1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(new Transaction("TX000001", "AC00128",
            14.09, now.minusMinutes(2), "San Diego", Channel.ATM));

        assertFalse(rule.applicable(transactions),
            "Applicable should return false when transactions are less than transactionCountThreshold");
    }

    @Test
    public void testApplicableTrue() {
        Rule rule = new FrequencyRule(2, Duration.ofMinutes(10), 1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128",
                14.09, now.minusMinutes(2), "San Diego", Channel.ATM),
            new Transaction("TX000002", "AC00128",
                14.10, now.minusMinutes(2), "Diego San", Channel.ONLINE)
        );

        assertTrue(rule.applicable(transactions),
            "Frequency rule should be applicable when transactions in period" +
                " are greater or equal to transactionCountThreshold");
    }

    @Test
    public void testApplicableFalse() {
        Rule rule = new FrequencyRule(2, Duration.ofMinutes(1), 1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128",
                10.0, now.minusMinutes(1), "San Diego", Channel.ATM),
            new Transaction("TX000002", "AC00128",
                20.0, now.minusMinutes(3), "San Diego", Channel.ONLINE)
        );

        assertFalse(rule.applicable(transactions),
            "Frequency rule should not be applicable when transaction in period " +
                "is less than transactionCountThreshold");
    }
}
