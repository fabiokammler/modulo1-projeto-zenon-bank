package br.com.zenon.fraud;

import java.util.Optional;

public interface ITransactionRepository {

    Optional<Transaction> retrieveTransactionByOrigName(String name);
    void save(Transaction transaction);
}
