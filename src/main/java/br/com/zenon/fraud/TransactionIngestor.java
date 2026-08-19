package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class TransactionIngestor {

    private static final int LIMIT=1000;
    private final List<Transaction> transactionList;
    private final Path filePath;

    public TransactionIngestor(Path filePath) {
        this.filePath = filePath;
        this.transactionList = new ArrayList<Transaction>();
    }

    public List<Transaction> ingest() {
        try(Stream<String> lines = Files.lines(filePath)) {
            transactionList.addAll(lines
                    .skip(1)
                    .limit(LIMIT)
                    .map(converter())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionList;
    }

    private Function<String, Optional<Transaction>> converter() {
        return (line) -> {
            List<String> lineContent = Arrays.stream(line.split(",\\s*")).toList();
            Optional<Transaction> transaction;

            try {
                String originName = lineContent.get(3).trim();
                BigDecimal originOldBalance = new BigDecimal(lineContent.get(4).trim());
                BigDecimal originNewBalance = new BigDecimal(lineContent.get(5).trim());
                String recipientName = lineContent.get(6).trim();
                BigDecimal recipientOldBalance = new BigDecimal(lineContent.get(7).trim());
                BigDecimal recipientNewBalance = new BigDecimal(lineContent.get(8).trim());
                boolean fraud = Boolean.parseBoolean(lineContent.get(9));
                boolean flaggedFraud = Boolean.parseBoolean(lineContent.get(10));

                transaction = Optional.of(new Transaction(
                        Integer.parseInt(lineContent.getFirst().trim()),
                        TransactionType.valueOf(lineContent.get(1).trim()),
                        new BigDecimal(lineContent.get(2).trim()),
                        new CustomerInfo(originName, originOldBalance, originNewBalance),
                        new CustomerInfo(recipientName, recipientOldBalance, recipientNewBalance),
                        fraud,
                        flaggedFraud
                ));
            } catch (Exception ex) {
                System.err.printf("Erro: %s | %s: %s %n"
                        , line
                        , ex.getClass().getCanonicalName()
                        , ex.getMessage());
                return Optional.empty();
            }
            return transaction;
        };
    }
}
