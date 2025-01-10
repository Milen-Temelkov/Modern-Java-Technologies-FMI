package bg.sofia.uni.fmi.mjt.frauddetector.transcaction;

import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Channel;
import bg.sofia.uni.fmi.mjt.frauddetector.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {

    @Test
    public void testTransactionFromInvalidLine() {
        assertThrows(IllegalArgumentException.class,
            () -> Transaction.of("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego"),
            "Method should throw IllegalArgumentException when given line with less or more than 6 values!");

    }

    @Test
    public void testTransactionFromValidLineATMChannel() {
        Transaction transaction = Transaction.of("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,ATM");

        LocalDateTime time = LocalDateTime.of(2023, 4, 11, 16, 29, 14);

        assertEquals("TX000001", transaction.transactionID(), "Transaction ID is wrong");
        assertEquals("AC00128", transaction.accountID(), "Account ID is wrong");
        assertEquals(14.09, transaction.transactionAmount(), "Transaction amount is wrong");
        assertEquals(time, transaction.transactionDate(), "Transaction date is wrong");
        assertEquals("San Diego", transaction.location(), "Transaction ID is wrong");
        assertEquals(Channel.ATM, transaction.channel(), "Transaction channel is wrong");
    }

    @Test
    public void testTransactionFromValidLineBranchChannel() {
        Transaction transaction = Transaction.of("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,Branch");

        LocalDateTime time = LocalDateTime.of(2023, 4, 11, 16, 29, 14);

        assertEquals("TX000001", transaction.transactionID(), "Transaction ID is wrong");
        assertEquals("AC00128", transaction.accountID(), "Account ID is wrong");
        assertEquals(14.09, transaction.transactionAmount(), "Transaction amount is wrong");
        assertEquals(time, transaction.transactionDate(), "Transaction date is wrong");
        assertEquals("San Diego", transaction.location(), "Transaction ID is wrong");
        assertEquals(Channel.BRANCH, transaction.channel(), "Transaction channel is wrong");
    }

    @Test
    public void testTransactionFromValidLineOnlineChannel() {
        Transaction transaction = Transaction.of("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,Online");

        LocalDateTime time = LocalDateTime.of(2023, 4, 11, 16, 29, 14);

        assertEquals("TX000001", transaction.transactionID(), "Transaction ID is wrong");
        assertEquals("AC00128", transaction.accountID(), "Account ID is wrong");
        assertEquals(14.09, transaction.transactionAmount(), "Transaction amount is wrong");
        assertEquals(time, transaction.transactionDate(), "Transaction date is wrong");
        assertEquals("San Diego", transaction.location(), "Transaction ID is wrong");
        assertEquals(Channel.ONLINE, transaction.channel(), "Transaction channel is wrong");
    }


    @Test
    public void testTransactionWithUnknowChanel() {
        assertThrows(IllegalArgumentException.class,
            () -> Transaction.of("TX000001,AC00128,14.09,2023-04-11 16:29:14,San Diego,NZ"),
            "Method should throw IllegalArgumentException when unknown channel is read from csv!");
    }

}
