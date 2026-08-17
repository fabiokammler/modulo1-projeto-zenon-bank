package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class TransactionIngestor {

    private static final int LIMIT=1000;
    private final List<Transaction> transactionList;
    private final Path filePath;

    public TransactionIngestor(Path filePath) {
        this.filePath = filePath;
        this.transactionList = new ArrayList<>();
    }

    public List<Transaction> ingest() {
        try(Stream<String> lines = Files.lines(filePath)) {
            transactionList.addAll(lines
                    .skip(1)
                    .limit(LIMIT)
                    .map(converter()).toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionList;
    }

    private Function<String, Transaction> converter() {
        return (line) -> {
            List<String> lineContent = Arrays.stream(line.split(",\\s*")).toList();

            Transaction transaction;
            List<String> transactionTypes = Arrays.stream(TransactionType.values()).map(TransactionType::name).toList();

            TransactionType transactionType =  validateTransactionTypeAndGet(1, lineContent, transactionTypes);
            String originName = lineContent.get(3).trim();
            BigDecimal originOldBalance = validateMoneyAndGet(4, lineContent);
            BigDecimal originNewBalance = validateMoneyAndGet(5, lineContent);
            String recipientName = lineContent.get(6).trim();
            BigDecimal recipientOldBalance = validateMoneyAndGet(7, lineContent);
            BigDecimal recipientNewBalance = validateMoneyAndGet(8, lineContent);
            boolean fraud = Boolean.parseBoolean(lineContent.get(9));
            boolean flaggedFraud = Boolean.parseBoolean(lineContent.get(10));

            transaction = new Transaction(
                    validateIntAndGet(0, lineContent),
                    transactionType,
                    validateMoneyAndGet(2, lineContent),
                    new CustomerInfo(originName, originOldBalance, originNewBalance),
                    new CustomerInfo(recipientName, recipientOldBalance, recipientNewBalance),
                    fraud,
                    flaggedFraud
            );

            return transaction;
        };
    }

    private BigDecimal validateMoneyAndGet(int index, List<String> lineContent) {
        BigDecimal resultValue = BigDecimal.ZERO;

        if(!lineContent.get(index).isEmpty()) {
            resultValue = new BigDecimal(lineContent.get(index).trim());
        }

        return resultValue;
    }

    private int validateIntAndGet(int index, List<String> lineContent) {
        int resultValue = 0;

        if (!lineContent.get(index).isEmpty()) {
            resultValue = Integer.parseInt(lineContent.get(index).trim());
        }

        return resultValue;
    }

    private TransactionType validateTransactionTypeAndGet(int index,
                                                          List<String> lineContent,
                                                          List<String> transactionTypes) {
        TransactionType transactionType = null;

        if (!lineContent.get(index).isEmpty() && transactionTypes.contains(lineContent.get(index))) {
            transactionType = TransactionType.valueOf(lineContent.get(index).trim());
        }

        return transactionType;
    }
}
