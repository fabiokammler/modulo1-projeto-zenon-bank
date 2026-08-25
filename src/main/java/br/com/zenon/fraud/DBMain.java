package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.List;

public class DBMain {

    void main() {

        /*ConnectionFactory.getConnection();
        IO.println("Conexão com o DB criada! :)");*/

        ITransactionRepository transactionSQLRepository = new TransactionSQLRepository();

        transactionSQLRepository.retrieveTransactionByOrigName("C1000001")
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente %s".formatted("C1000001")));
        transactionSQLRepository.retrieveTransactionByOrigName("C1235")
                .ifPresentOrElse(IO::println, () -> IO.println("Transação não encontrada para o cliente %s".formatted("C1235")));

        var transaction1 = new Transaction(1,
                TransactionType.valueOf("PAYMENT"),
                BigDecimal.valueOf(9839.64),
                new CustomerInfo("C1231006815",
                        BigDecimal.valueOf(170136.0),
                        BigDecimal.valueOf(160296.36)
                ),
                new CustomerInfo("M1979787155",
                        BigDecimal.valueOf(0.0),
                        BigDecimal.valueOf(0.0)
                ),
                Boolean.FALSE,
                Boolean.FALSE
        );

        transactionSQLRepository.save(transaction1);
        IO.println("Transacao salva com sucesso no banco");

        IO.println("---------------------------------------------------------------------------------------");

        TransactionIngestor transactionIngestor = new TransactionIngestor(Paths.get("data/PS_log.csv"));
        List<Transaction> transactionList = transactionIngestor.ingest();
        IO.println("Size -> "+transactionList.size());

        IO.println("Iniciando adicao das transacoes no BD");
        transactionList.forEach(transactionSQLRepository::save);
    }
}
