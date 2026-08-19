package br.com.zenon.fraud;

import java.nio.file.Paths;
import java.util.List;

public class MainTransactionIngestorBadData {
    static void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/paysim_with_bad_data.csv"));
        List<Transaction> transactionList = transactionIngestor.ingest();

        transactionList.forEach(IO::print);
    }
}
