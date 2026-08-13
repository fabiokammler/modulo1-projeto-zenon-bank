package br.com.zenon.fraud;

import java.math.BigDecimal;

public record CustomerInfo(String name,
                           BigDecimal oldBalance,
                           BigDecimal newBalance) {

    @Override
    public String toString() {
        return  "name='" + name + "\r\n" +
                ", oldBalance=" + oldBalance + "\r\n" +
                ", newBalance=" + newBalance;
    }
}
