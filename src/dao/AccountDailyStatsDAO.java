package dao;
import model.AccountDailyStats;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDailyStatsDAO {

    // Bugün için ekle veya güncelle (upsert)
    public void insertOrUpdateToday(AccountDailyStats s) {
        String sql = "INSERT INTO accounts_dailystats " +
                "(account_id, daily_posts, daily_followers_gain, daily_followers_lost, " +
                " daily_following_added, daily_following_removed, daily_likes, daily_comments, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                " daily_posts = VALUES(daily_posts), " +
                " daily_followers_gain = VALUES(daily_followers_gain), " +
                " daily_followers_lost = VALUES(daily_followers_lost), " +
                " daily_following_added = VALUES(daily_following_added), " +
                " daily_following_removed = VALUES(daily_following_removed), " +
                " daily_likes = VALUES(daily_likes), " +
                " daily_comments = VALUES(daily_comments), " +
                " notes = VALUES(notes)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getAccountId());
            ps.setInt(2, s.getDailyPosts());
            ps.setInt(3, s.getDailyFollowersGain());
            ps.setInt(4, s.getDailyFollowersLost());
            ps.setInt(5, s.getDailyFollowingAdded());
            ps.setInt(6, s.getDailyFollowingRemoved());
            ps.setInt(7, s.getDailyLikes());
            ps.setInt(8, s.getDailyComments());
            ps.setString(9, s.getNotes());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Günlük kayıtları username ile getir (rapor/görüntüleme için)
    public List<Object[]> getAllDailyWithUsernames() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT d.id, ai.username, d.created_date, " +
                     "d.daily_posts, d.daily_followers_gain, d.daily_followers_lost, " +
                     "d.daily_following_added, d.daily_following_removed, " +
                     "d.daily_likes, d.daily_comments, d.notes " +
                     "FROM accounts_dailystats d " +
                     "JOIN accounts_info ai ON d.account_id = ai.id " +
                     "ORDER BY d.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getDate("created_date"),
                    rs.getInt("daily_posts"),
                    rs.getInt("daily_followers_gain"),
                    rs.getInt("daily_followers_lost"),
                    rs.getInt("daily_following_added"),
                    rs.getInt("daily_following_removed"),
                    rs.getInt("daily_likes"),
                    rs.getInt("daily_comments"),
                    rs.getString("notes")
                };
                list.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Belirli bir hesap için liste
    public List<AccountDailyStats> listByAccount(int accountId) {
        List<AccountDailyStats> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts_dailystats WHERE account_id=? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AccountDailyStats s = new AccountDailyStats();
                s.setId(rs.getInt("id"));
                s.setAccountId(rs.getInt("account_id"));
                s.setDailyPosts(rs.getInt("daily_posts"));
                s.setDailyFollowersGain(rs.getInt("daily_followers_gain"));
                s.setDailyFollowersLost(rs.getInt("daily_followers_lost"));
                s.setDailyFollowingAdded(rs.getInt("daily_following_added"));
                s.setDailyFollowingRemoved(rs.getInt("daily_following_removed"));
                s.setDailyLikes(rs.getInt("daily_likes"));
                s.setDailyComments(rs.getInt("daily_comments"));
                s.setNotes(rs.getString("notes"));
                s.setCreatedAt(rs.getTimestamp("created_at"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                s.setCreatedDate(rs.getDate("created_date"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Kayıt sil
     public void deleteById(int id) {
        String sql = "DELETE FROM accounts_dailystats WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     public List<Object[]> getDailyWithFilters(java.sql.Date start, java.sql.Date end, Integer accountId) {
    List<Object[]> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder(
        "SELECT d.id, ai.username, d.created_date, " +
        "d.daily_posts, d.daily_followers_gain, d.daily_followers_lost, " +
        "d.daily_following_added, d.daily_following_removed, " +
        "d.daily_likes, d.daily_comments, d.notes " +
        "FROM accounts_dailystats d " +
        "JOIN accounts_info ai ON d.account_id = ai.id WHERE 1=1 "
    );
    if (start != null) sql.append("AND d.created_date >= ? ");
    if (end   != null) sql.append("AND d.created_date <= ? ");
    if (accountId != null) sql.append("AND d.account_id = ? ");
    sql.append("ORDER BY d.created_at DESC");

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

        int i = 1;
        if (start != null) ps.setDate(i++, start);
        if (end   != null) ps.setDate(i++, end);
        if (accountId != null) ps.setInt(i++, accountId);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = {
                rs.getInt("id"),
                rs.getString("username"),
                rs.getDate("created_date"),
                rs.getInt("daily_posts"),
                rs.getInt("daily_followers_gain"),
                rs.getInt("daily_followers_lost"),
                rs.getInt("daily_following_added"),
                rs.getInt("daily_following_removed"),
                rs.getInt("daily_likes"),
                rs.getInt("daily_comments"),
                rs.getString("notes")
            };
            list.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
}