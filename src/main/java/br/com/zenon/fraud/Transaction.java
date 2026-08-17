package br.com.zenon.fraud;

import java.math.BigDecimal;

public record Transaction(int step,
                          TransactionType type,
                          BigDecimal amount,
                          CustomerInfo customerOrigInfo,
                          CustomerInfo customerDestInfo,
                          boolean fraud,
                          boolean flaggedFraud) {

    @Override
    public String toString() {
        return  "\r\n Transaction[" + "step=" + step +
                ", type=" + type +
                ", amount=" + amount +
                ", origin=" + customerOrigInfo +
                ", recipient=" + customerDestInfo +
                ", fraud=" + (fraud ? 1 : 0) +
                ", flaggedFraud=" + (flaggedFraud ? 1 : 0) + "]";
    }
}
