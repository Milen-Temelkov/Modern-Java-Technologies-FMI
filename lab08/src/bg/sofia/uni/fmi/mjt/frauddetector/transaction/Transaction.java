package bg.sofia.uni.fmi.mjt.frauddetector.transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(String transactionID, String accountID, double transactionAmount,
                          LocalDateTime transactionDate, String location, Channel channel) {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final int TRANSACTION_ID_INDEX = 0;
    public static final int ACCOUNT_ID_INDEX = 1;
    public static final int TRANSACTION_AMOUNT_INDEX = 2;
    public static final int TRANSACTION_DATE_INDEX = 3;
    public static final int LOCATION_INDEX = 4;
    public static final int CHANNEL_INDEX = 5;
    public static final int FIELDS_COUNT = 6;

    public static Transaction of(String line) {
        String[] fields = line.split(",");

        if (fields.length != FIELDS_COUNT) {
            throw new IllegalArgumentException("Field number mismatch!");
        }

        Channel channel = switch (fields[CHANNEL_INDEX]) {
            case "ATM" -> Channel.ATM;
            case "Branch" -> Channel.BRANCH;
            case "Online" -> Channel.ONLINE;
            default -> throw new IllegalArgumentException("Error in csv, unknown channel detected!");
        };

        return new Transaction(fields[TRANSACTION_ID_INDEX],
            fields[ACCOUNT_ID_INDEX].strip(),
            Double.parseDouble(fields[TRANSACTION_AMOUNT_INDEX].strip()),
            LocalDateTime.parse(fields[TRANSACTION_DATE_INDEX].strip(), FORMATTER),
            fields[LOCATION_INDEX].strip(),
            channel);
    }
}
