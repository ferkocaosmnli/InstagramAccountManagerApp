package dao;

import model.AccountOps;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountOpsDAO {

    // UNIQUE (account_id, action_date, action_type) nedeniyle UPSERT
    public void insertOrUpdate(AccountOps op) {
        String sql = "INSERT INTO account_ops " +
                "(account_id, action_date, action_type, quantity, tool, target_tag, notes, status) " +
                "VALUES (?, COALESCE(?, CURRENT_DATE), ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "quantity = VALUES(quantity), tool = VALUES(tool), target_tag = VALUES(target_tag), " +
                "notes = VALUES(notes), status = VALUES(status)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, op.getAccountId());
            if (op.getActionDate() != null) ps.setDate(2, op.getActionDate()); else ps.setNull(2, Types.DATE);
            ps.setString(3, op.getActionType());
            ps.setInt(4, op.getQuantity());
            ps.setString(5, emptyToNull(op.getTool()));
            ps.setString(6, emptyToNull(op.getTargetTag()));
            ps.setString(7, emptyToNull(op.getNotes()));
            ps.setString(8, op.getStatus() == null ? "done" : op.getStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(int id) {
        String sql = "DELETE FROM account_ops WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tümünü username ile listele (en yeni üstte)
    public List<Object[]> listAllWithUsernames() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT op.id, ai.username, op.action_date, op.action_type, op.quantity, " +
                     "op.tool, op.target_tag, op.status, op.notes " +
                     "FROM account_ops op " +
                     "JOIN accounts_info ai ON op.account_id = ai.id " +
                     "ORDER BY op.action_date DESC, op.id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDate("action_date"),
                        rs.getString("action_type"),
                        rs.getInt("quantity"),
                        rs.getString("tool"),
                        rs.getString("target_tag"),
                        rs.getString("status"),
                        rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    // Filtreli liste
    public List<Object[]> listFiltered(Date from, Date to, Integer accountId, String actionType) {
        List<Object[]> rows = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT op.id, ai.username, op.action_date, op.action_type, op.quantity, " +
            "op.tool, op.target_tag, op.status, op.notes " +
            "FROM account_ops op " +
            "JOIN accounts_info ai ON op.account_id = ai.id WHERE 1=1 "
        );
        if (from != null) sb.append("AND op.action_date >= ? ");
        if (to   != null) sb.append("AND op.action_date <= ? ");
        if (accountId != null) sb.append("AND op.account_id = ? ");
        if (actionType != null && !"ALL".equalsIgnoreCase(actionType)) sb.append("AND op.action_type = ? ");
        sb.append("ORDER BY op.action_date DESC, op.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int i = 1;
            if (from != null) ps.setDate(i++, from);
            if (to   != null) ps.setDate(i++, to);
            if (accountId != null) ps.setInt(i++, accountId);
            if (actionType != null && !"ALL".equalsIgnoreCase(actionType)) ps.setString(i++, actionType);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getDate("action_date"),
                        rs.getString("action_type"),
                        rs.getInt("quantity"),
                        rs.getString("tool"),
                        rs.getString("target_tag"),
                        rs.getString("status"),
                        rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
