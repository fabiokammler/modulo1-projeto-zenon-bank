package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FraudAnalyzer {

    private final List<Transaction> transactionList;

    public FraudAnalyzer(final List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public long fraudCount() {
        return transactionList
                .stream()
                .filter(Transaction::isFraud)
                .count();
    }

    public List<Transaction> listHighestValuesFrauds() {
        return transactionList.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .limit(3)
                .toList();
    }

    public List<String> listSuspiciousClients() {
        return transactionList.stream()
                .filter(Transaction::isFraud)
                .sorted(Comparator.comparing(Transaction::amount).reversed())
                .map(transaction -> transaction.customerOrigInfo().name())
                .distinct()
                .limit(5)
                .toList();
    }

    public BigDecimal total() {
        return transactionList.stream()
                .filter(Transaction::isFraud)
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, List<Transaction>> listFraudTypes() {
        return transactionList.stream()
                .filter(Transaction::isFraud)
                .collect(Collectors.groupingBy(transaction -> transaction.type().name()));
    }
}
