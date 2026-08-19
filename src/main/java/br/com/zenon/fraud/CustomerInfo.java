package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.util.Objects;

public record CustomerInfo(String name,
                           BigDecimal oldBalance,
                           BigDecimal newBalance) {

    public CustomerInfo {
        Objects.requireNonNull(name);
        Objects.requireNonNull(oldBalance);
        Objects.requireNonNull(newBalance);

        if(name.trim().isEmpty()) throw new IllegalArgumentException("name should not be empty");
        if(oldBalance.signum() < 0) throw new IllegalArgumentException("oldBalance should be positive: " + oldBalance);
        if(newBalance.signum() < 0) throw new IllegalArgumentException("newBalance should be positive: " + newBalance);
    }

    @Override
    public String toString() {
        return  "CustomerInfo[name=" + name +
                ", oldBalance=" + oldBalance +
                ", newBalance=" + newBalance + "]";
    }
}
