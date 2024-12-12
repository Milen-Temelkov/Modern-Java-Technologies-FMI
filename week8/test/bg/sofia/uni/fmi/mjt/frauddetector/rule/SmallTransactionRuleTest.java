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

public class SmallTransactionRuleTest {
    @Test
    public void testApplicableWithNullTransactions() {
        Rule rule = new SmallTransactionsRule(2, 2,1);

        assertThrows(IllegalArgumentException.class, () -> rule.applicable(null),
            "Applicable should throw IllegalArgumentException when given null list");
    }

    @Test
    public void testApplicableWithEmptyTransactions() {
        Rule rule = new SmallTransactionsRule(2, 2,1);

        List<Transaction> transactions = Collections.emptyList();

        assertFalse(rule.applicable(transactions), "Applicable should return false when given empty list");
    }

    @Test
    public void testApplicableWithLessLocationsThanLocationsCountThreshold() {
        Rule rule = new SmallTransactionsRule(2, 2,1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(new Transaction("TX000001", "AC00128",
            1.0, now.minusMinutes(2), "San Diego", Channel.ATM));

        assertFalse(rule.applicable(transactions),
            "Applicable should return false when transactions are less than threshold");
    }

    @Test
    public void testApplicableTrue() {
        Rule rule = new SmallTransactionsRule(2, 2,1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128",
                1.0, now.minusMinutes(2), "San Diego", Channel.ATM),
            new Transaction("TX000002", "AC00128",
                1.0, now.minusMinutes(2), "Diego San", Channel.ONLINE)
        );

        assertTrue(rule.applicable(transactions),
            "Small Transactions rule should be applicable " +
                "when small transactions count is greater or equal to threshold");
    }

    @Test
    public void testApplicableFalse() {
        Rule rule = new SmallTransactionsRule(2, 2,1);

        LocalDateTime now = LocalDateTime.now();
        List<Transaction> transactions = List.of(
            new Transaction("TX000001", "AC00128",
                1.0, now.minusMinutes(1), "San Diego", Channel.ATM),
            new Transaction("TX000002", "AC00128",
                10.0, now.minusMinutes(3), "San Diego", Channel.ONLINE)
        );

        assertFalse(rule.applicable(transactions),
            "Small Transactions rule should not be applicable " +
                "when small transactions count is less than threshold");
    }
}
