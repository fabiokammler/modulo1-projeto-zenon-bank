package br.com.zenon.fraud;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionSQLRepository implements ITransactionRepository {

    @Override
    public Optional<Transaction> retrieveTransactionByOrigName(String name) {
        String query = """
               SELECT step, `type`, amount, name_origin, old_balance_origin, new_balance_origin,
                   name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud
               FROM zenon.transaction
               WHERE name_origin = ?
               ORDER BY step
               LIMIT 1
               """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery();) {
                if(rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transacao da origem: " + name, e);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) {
        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");
            String nameOrigin = rs.getString("name_origin");
            BigDecimal oldBalanceOrigin = rs.getBigDecimal("old_balance_origin");
            BigDecimal newBalanceOrigin = rs.getBigDecimal("new_balance_origin");
            String nameDest = rs.getString("name_recipient");
            BigDecimal oldBalanceDest = rs.getBigDecimal("old_balance_recipient");
            BigDecimal newBalanceDest = rs.getBigDecimal("new_balance_recipient");
            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlaggedFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount,
                    new CustomerInfo(nameOrigin, oldBalanceOrigin, newBalanceOrigin),
                    new CustomerInfo(nameDest, oldBalanceDest, newBalanceDest),
                    isFraud, isFlaggedFraud);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Transaction transaction) {
        String sql = """
                INSERT INTO transaction (step, `type`, amount, name_origin, old_balance_origin, new_balance_origin,
                name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());

            ps.setString(4, transaction.customerOrigInfo().name());
            ps.setBigDecimal(5, transaction.customerOrigInfo().oldBalance());
            ps.setBigDecimal(6, transaction.customerOrigInfo().newBalance());

            ps.setString(7, transaction.customerDestInfo().name());
            ps.setBigDecimal(8, transaction.customerDestInfo().oldBalance());
            ps.setBigDecimal(9, transaction.customerDestInfo().newBalance());

            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.setInt(1, transaction.step());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar transacao!", e);
        }
    }

    public void saveMultiThreadBatch(List<Transaction> transactionsList) {

        if (transactionsList != null && !transactionsList.isEmpty()) {
            try (Connection conn = ConnectionFactory.getConnection();) {
                conn.setAutoCommit(false);
                String sql = """
                INSERT INTO transaction (step, `type`, amount, name_origin, old_balance_origin, new_balance_origin,
                name_recipient, old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

                try (PreparedStatement ps = conn.prepareStatement(sql);) {
                    for (var transaction:transactionsList) {
                        //IO.println("Adicionando transacao no lote...");
                        //for(var transaction:transactionEntry.getValue()){
                        ps.setInt(1, transaction.step());
                        ps.setString(2, transaction.type().name());
                        ps.setBigDecimal(3, transaction.amount());

                        ps.setString(4, transaction.customerOrigInfo().name());
                        ps.setBigDecimal(5, transaction.customerOrigInfo().oldBalance());
                        ps.setBigDecimal(6, transaction.customerOrigInfo().newBalance());

                        ps.setString(7, transaction.customerDestInfo().name());
                        ps.setBigDecimal(8, transaction.customerDestInfo().oldBalance());
                        ps.setBigDecimal(9, transaction.customerDestInfo().newBalance());

                        ps.setBoolean(10, transaction.isFraud());
                        ps.setBoolean(11, transaction.isFlaggedFraud());

                        ps.setInt(1, transaction.step());
                        ps.addBatch();
                        //ps.clearParameters();
                    }
                    ps.executeBatch();
                    conn.commit();
                } catch (SQLException e) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        throw new RuntimeException("Erro ao executar rollback", ex);
                    }
                    throw new RuntimeException("Erro ao salvar nova transacao: ", e);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro na conexao com o BD...", e);
            }
        }
    }
}
