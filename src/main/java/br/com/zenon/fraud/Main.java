package br.com.zenon.fraud;

import java.math.BigDecimal;

public class Main {
    void main() {
        var transaction1 = new Transaction(1,
                TransactionType.valueOf("PAYMENT"),
                BigDecimal.valueOf(9839.64),
                new CustomerInfo("C1231006815",
                        BigDecimal.valueOf(170136.0),
                        BigDecimal.valueOf(160296.36)
                ),
                new CustomerInfo("M1979787155",
                        BigDecimal.valueOf(0.0),
                        BigDecimal.valueOf(0.0)
                ),
                Boolean.FALSE,
                Boolean.FALSE
        );

        var transaction2 = new Transaction(743,
                TransactionType.valueOf("CASH_OUT"),
                BigDecimal.valueOf(850002.52),
                new CustomerInfo("C1280323807",
                        BigDecimal.valueOf(850002.52),
                        BigDecimal.valueOf(0.0)
                ),
                new CustomerInfo("C873221189",
                        BigDecimal.valueOf(6510099.11),
                        BigDecimal.valueOf(7360101.63)
                ),
                Boolean.TRUE,
                Boolean.FALSE
        );

        System.out.println("Transaction 1:" + transaction1);
        System.out.println("Transaction 2:" + transaction2);
    }
}
