package br.com.zenon.fraud;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    private static final int LIMIT=50_000;
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

    public EfficientTransactionIngestor(Path filePath) {
        this.filePath = filePath;
    }

    public void readAsBatch(int chunkSize, Consumer<List<Transaction>> listConsumer) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(10);
             Stream<String> lines = Files.lines(filePath).skip(1);) {

            Spliterator<String> spliterator = lines.spliterator();
            IO.println("Starting to process...");

            List<String> lineBatch = new ArrayList<>(chunkSize);
            AtomicInteger index = new AtomicInteger(0);

            while(spliterator.tryAdvance(lineBatch::add)) {

                if(lineBatch.size() >= chunkSize) {
                    IO.println("LOTE -> " + index.getAndIncrement());
                    List<String> copyBatch = List.copyOf(lineBatch);

                    executorService.submit(() -> executeReadFileBatch(copyBatch, listConsumer));
                    lineBatch.clear();
                }
            }
            if(!lineBatch.isEmpty()) {
                executorService.submit(() -> executeReadFileBatch(lineBatch, listConsumer));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeReadFileBatch(List<String> batch, Consumer<List<Transaction>> listConsumer) {
        long initialTime = System.nanoTime();
        List<Transaction> list = batch.stream()
                .parallel()
                .map(converter())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        double finalTime = (System.nanoTime() - initialTime) / 1_000_000.0;
        IO.println("Tempo para processar CPU: "+finalTime);
        long initialTime2 = System.nanoTime();
        listConsumer.accept(list);
        double finalTime2 = (System.nanoTime() - initialTime2) / 1_000_000.0;
        IO.println("Tempo para processar I/O: "+finalTime2);
    }

    /*private void executeReadFileBatch(Stream<String> stream, int chunkSize, Consumer<List<Transaction>> listConsumer) {
        AtomicInteger index = new AtomicInteger(0);
        stream
            .collect(Collectors.groupingBy(line -> index.getAndIncrement() / chunkSize))
            .values()
            .parallelStream()
            .map(converter())
            .forEach(listConsumer);
    }*/

    private Function<String, Optional<Transaction>> converter() {
        return (line) -> {
            Transaction transaction;
            List<String> lineContent = Arrays.stream(line.split(",\\s*")).toList();

            try {
                String originName = lineContent.get(3).trim();
                BigDecimal originOldBalance = new BigDecimal(lineContent.get(4).trim());
                BigDecimal originNewBalance = new BigDecimal(lineContent.get(5).trim());
                String recipientName = lineContent.get(6).trim();
                BigDecimal recipientOldBalance = new BigDecimal(lineContent.get(7).trim());
                BigDecimal recipientNewBalance = new BigDecimal(lineContent.get(8).trim());
                boolean fraud = lineContent.get(9).equals("1") ? Boolean.TRUE : Boolean.FALSE;
                boolean flaggedFraud = lineContent.get(10).equals("1") ? Boolean.TRUE : Boolean.FALSE;

                transaction = new Transaction(
                        Integer.parseInt(lineContent.getFirst().trim()),
                        TransactionType.valueOf(lineContent.get(1).trim()),
                        new BigDecimal(lineContent.get(2).trim()),
                        new CustomerInfo(originName, originOldBalance, originNewBalance),
                        new CustomerInfo(recipientName, recipientOldBalance, recipientNewBalance),
                        fraud,
                        flaggedFraud
                );
            } catch (Exception ex) {
                System.err.printf("Erro: %s | %s: %s %n"
                        , line
                        , ex.getClass().getCanonicalName()
                        , ex.getMessage());
                return Optional.empty();
            }
            return Optional.of(transaction);
        };
    }
}
