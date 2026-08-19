package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(int step,
                          TransactionType type,
                          BigDecimal amount,
                          CustomerInfo customerOrigInfo,
                          CustomerInfo customerDestInfo,
                          boolean isFraud,
                          boolean isFlaggedFraud) {

    public Transaction {
        Objects.requireNonNull(type);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(customerOrigInfo);
        Objects.requireNonNull(customerDestInfo);

        if(step <= 0) throw new IllegalArgumentException("step should be positive or greater then zero: " + step);
        if(amount.signum() < 0) throw new IllegalArgumentException("amount should be positive: " + amount);
    }

    @Override
    public String toString() {
        return  "\r\n Transaction[" + "step=" + step +
                ", type=" + type +
                ", amount=" + amount +
                ", origin=" + customerOrigInfo +
                ", recipient=" + customerDestInfo +
                ", isFraud=" + (isFraud ? 1 : 0) +
                ", isFlaggedFraud=" + (isFlaggedFraud ? 1 : 0) + "]";
    }
}
