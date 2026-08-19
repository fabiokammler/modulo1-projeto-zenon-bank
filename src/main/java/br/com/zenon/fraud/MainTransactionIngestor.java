package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class MainTransactionIngestor {
    void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/PS_log.csv"));
        List<Transaction> transactionList = transactionIngestor.ingest();
        /*boolean sizeGreater = transactionList.size() > 10;
        if(sizeGreater) {
            transactionList = transactionList.stream().limit(10).toList();
        }*/
        //transactionList.forEach(IO::print);

        var fraudAnalyzer = new FraudAnalyzer(transactionList);

        long listSize = fraudAnalyzer.fraudCount();
        IO.println("1. Total de Fraudes: " + listSize);

        IO.println("2. Top 3 Fraudes de Maior Valor:");
        List<Transaction> listHighestValueFrauds = fraudAnalyzer.listHighestValuesFrauds();
        listHighestValueFrauds.forEach(transaction -> IO.println("- %.2f".formatted(transaction.amount())));

        IO.println("3. Clientes Suspeitos:");
        List<String> listSuspiciousClients = fraudAnalyzer.listSuspiciousClients();
        listSuspiciousClients.forEach(IO::println);
        //listSuspiciousClients.forEach(transaction -> IO.println(transaction.customerOrigInfo().name()));

        BigDecimal total = fraudAnalyzer.total();
        IO.println("4. Prejuízo Total: " + total);

        IO.println("5. Fraudes por Tipo:");
        Map<String, List<Transaction>> listFraudTypes = fraudAnalyzer.listFraudTypes();
        listFraudTypes.forEach((type, list) -> IO.println("- " + type + ": " + list.size()));
    }
}
