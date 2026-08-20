package br.com.zenon.fraud;

import java.util.Optional;

public interface ITransactionRepository {

    Optional<Transaction> retrieveTransactionsByNameOrig(String name);
}
