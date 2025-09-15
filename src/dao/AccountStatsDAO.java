package dao;

import model.AccountStats;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountStatsDAO {

    public List<Object[]> getAllStats() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT s.id, ai.username, s.posts, s.followers, s.following, s.updated_at " +
                     "FROM accounts_stats s " +
                     "JOIN accounts_info ai ON s.account_id = ai.id";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getInt("posts"),
                        rs.getInt("followers"),
                        rs.getInt("following"),
                        rs.getTimestamp("updated_at")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStats(AccountStats stats) {
        String sql = "UPDATE accounts_stats SET posts=?, followers=?, following=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stats.getPosts());
            ps.setInt(2, stats.getFollowers());
            ps.setInt(3, stats.getFollowing());
            ps.setInt(4, stats.getId());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteStats(int id) {
        String sql = "DELETE FROM accounts_stats WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* -------- YENİ: ekleme + hesap listesi -------- */

    /** Yeni satır ekler */
    public boolean insertStats(AccountStats stats) {
        String sql = "INSERT INTO accounts_stats (account_id, posts, followers, following) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, stats.getAccountId());
            ps.setInt(2, stats.getPosts());
            ps.setInt(3, stats.getFollowers());
            ps.setInt(4, stats.getFollowing());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** accounts_info’dan (id, username) listesini döner */
    public List<Object[]> getAllAccounts() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, username FROM accounts_info ORDER BY username ASC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Object[]{ rs.getInt("id"), rs.getString("username") });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
