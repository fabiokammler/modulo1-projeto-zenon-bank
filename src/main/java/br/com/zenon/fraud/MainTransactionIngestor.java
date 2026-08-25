package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainTransactionIngestor {
    void main() {

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/PS_log.csv"));
        List<Transaction> transactionList = transactionIngestor.ingest();
        /*boolean sizeGreater = transactionList.size() > 10;
        if(sizeGreater) {
            transactionList = transactionList.stream().limit(10).toList();
        }*/
        //transactionList.forEach(IO::print);

        IO.println("------------------------------------------------------------------------------------------");

        var fraudAnalyzer = new FraudAnalyzer(transactionList);

        long listSize = fraudAnalyzer.fraudCount();
        IO.println("1. Total de Fraudes: " + listSize);

        IO.println("2. Top 3 Fraudes de Maior Valor:");
        List<Transaction> listHighestValueFrauds = fraudAnalyzer.listHighestValuesFrauds();
        listHighestValueFrauds.forEach(transaction -> IO.println("- %.2f".formatted(transaction.amount())));

        IO.println("3. Clientes Suspeitos:");
        List<String> listSuspiciousClients = fraudAnalyzer.listSuspiciousClients();
        listSuspiciousClients.forEach(IO::println);

        BigDecimal total = fraudAnalyzer.total();
        IO.println("4. Prejuízo Total: " + total);

        IO.println("5. Fraudes por Tipo:");
        Map<String, List<Transaction>> listFraudTypes = fraudAnalyzer.listFraudTypes();
        listFraudTypes.forEach((type, list) -> IO.println("- " + type + ": " + list.size()));

        IO.println("------------------------------------------------------------------------------------------");

        ITransactionRepository transactionRepository = new TransactionListRepository(transactionList);

        Optional<Transaction> naoExistente = transactionRepository.retrieveTransactionByOrigName("C12345");
        naoExistente.ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente %s".formatted("C12345")));

        long tempoInicial = System.nanoTime();
        Optional<Transaction> existente = transactionRepository.retrieveTransactionByOrigName("C1868032458");
        existente.ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente %s".formatted("C1231006815")));
        double tempoFinal = (double) (System.nanoTime() - tempoInicial) / 1_000_000.0;

        IO.println("Tempo1 de execucao: " + tempoFinal);

        ITransactionRepository transactionMapRepository = new TransactionMapRepository(transactionList);

        long tempoInicial2 = System.nanoTime();
        Optional<Transaction> existente2 = transactionMapRepository.retrieveTransactionByOrigName("C1868032458");
        existente2.ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente %s".formatted("C1868032458")));
        double tempoFinal2 = (double) (System.nanoTime() - tempoInicial2) / 1_000_000.0;

        IO.println("Tempo2 de execucao: " + tempoFinal2);


    }
}
