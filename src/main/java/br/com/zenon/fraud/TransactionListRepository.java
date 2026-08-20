package br.com.zenon.fraud;

import java.util.List;
import java.util.Optional;

public class TransactionListRepository implements ITransactionRepository {

    private final List<Transaction> transactionList;

    public TransactionListRepository(final List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public Optional<Transaction> retrieveTransactionsByNameOrig(String name) {
       return transactionList
                .stream()
                .filter(transaction -> transaction.customerOrigInfo().name().equals(name))
                .findFirst();
    }
}
