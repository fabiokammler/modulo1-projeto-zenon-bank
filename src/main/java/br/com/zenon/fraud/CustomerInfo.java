package br.com.zenon.fraud;

import java.math.BigDecimal;

public record CustomerInfo(String name,
                           BigDecimal oldBalance,
                           BigDecimal newBalance) {

    @Override
    public String toString() {
        return  "CustomerInfo[name=" + name +
                ", oldBalance=" + oldBalance +
                ", newBalance=" + newBalance + "]";
    }
}
