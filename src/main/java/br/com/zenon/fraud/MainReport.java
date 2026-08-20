package br.com.zenon.fraud;

import java.nio.file.Paths;

public class MainReport {

    void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/PS_log.csv"));
        TransactionIngestor.Statistics statistics = transactionIngestor.reduceIngest();

        IO.println("""
            Total de linhas: %d
            Total de fraudes: %d
            Valor total transacionado: %.2f
            """.formatted(statistics.totalTransactions(), statistics.totalFraud(), statistics.totalAmount()));
    }
}
