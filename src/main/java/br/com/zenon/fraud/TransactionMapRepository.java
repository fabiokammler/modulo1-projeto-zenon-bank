package br.com.zenon.fraud;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransactionMapRepository implements ITransactionRepository {

    private final Map<String, Transaction> transactionMap;

    public TransactionMapRepository(final List<Transaction> transactionList) {
        transactionMap = transactionList
                .stream()
                .collect(Collectors.toMap(transaction -> transaction.customerOrigInfo().name(), Function.identity()));
    }

    public Optional<Transaction> retrieveTransactionByOrigName(String name) {
       return Optional.ofNullable(transactionMap.get(name));
    }

    @Override
    public void save(Transaction transaction) {

    }

}
