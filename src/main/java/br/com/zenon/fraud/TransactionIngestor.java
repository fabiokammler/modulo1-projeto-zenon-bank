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

    private static final int LIMIT=10_000;
    private final List<Transaction> transactionList;
    private final Path filePath;

    private record ReportTransaction(BigDecimal amount, boolean isFraud) {

    }

    public record Statistics(long totalTransactions, long totalFraud, BigDecimal totalAmount) {

        private final static Statistics ZERO = new Statistics(0, 0, BigDecimal.ZERO);

        private Statistics addReportTransaction(ReportTransaction rt) {
            return new Statistics(
                    totalTransactions + 1,
                    totalFraud + (rt.isFraud ? 1 : 0),
                    totalAmount.add(rt.amount));
        }
    }

    public TransactionIngestor(Path filePath) {
        this.filePath = filePath;
        this.transactionList = new ArrayList<>();
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

    public Statistics reduceIngest() {
        try(Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .skip(1)
                    .map(reduceConverter())
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(Statistics.ZERO,
                            Statistics::addReportTransaction,
                            (s1, s2) -> s1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Function<String, Optional<ReportTransaction>> reduceConverter() {
        return (line) -> {
            List<String> lineContent = Arrays.stream(line.split(",\\s*")).toList();
            Optional<ReportTransaction> transaction;

            try {
                boolean fraud = lineContent.get(9).equals("1") ? Boolean.TRUE : Boolean.FALSE;

                transaction = Optional.of(new ReportTransaction(
                        new BigDecimal(lineContent.get(2).trim()),
                        fraud
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
                boolean fraud = lineContent.get(9).equals("1") ? Boolean.TRUE : Boolean.FALSE;
                boolean flaggedFraud = lineContent.get(10).equals("1") ? Boolean.TRUE : Boolean.FALSE;

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
