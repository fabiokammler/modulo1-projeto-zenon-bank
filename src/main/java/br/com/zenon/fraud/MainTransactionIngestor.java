package br.com.zenon.fraud;

import java.nio.file.Paths;
import java.util.List;

public class MainTransactionIngestor {
    void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/PS_log.csv"));
        List<Transaction> transactionList = transactionIngestor.ingest();
        boolean sizeGreater = transactionList.size() > 10;
        if(sizeGreater) {
            transactionList = transactionList.stream().limit(10).toList();
        }
        transactionList.forEach(IO::print);
    }
}
