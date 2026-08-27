package br.com.zenon.fraud;

import java.nio.file.Paths;

public class MainIngestion {

    static void main() {

        EfficientTransactionIngestor transactionIngestor = new EfficientTransactionIngestor(Paths.get("data/PS_log.csv"));
        TransactionSQLRepository transactionSQLRepository = new TransactionSQLRepository();

        long initialTime = System.nanoTime();

        transactionIngestor.readAsBatch(10_000, transactionSQLRepository::saveMultiThreadBatch);

        double finalTime = (System.nanoTime() - initialTime) / 1_000_000.0;
        IO.println("Tempo para processar: "+finalTime);
    }
}
