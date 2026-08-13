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
        return  "\r\n" + "step=" + step + "\r\n" +
                ", type=" + type + "\r\n" +
                ", amount=" + amount + "\r\n" +
                ", " + customerOrigInfo + "\r\n" +
                ", " + customerDestInfo + "\r\n" +
                ", fraud=" + (fraud ? 1 : 0) + "\r\n" +
                ", flaggedFraud=" + (flaggedFraud ? 1 : 0);
    }
}
