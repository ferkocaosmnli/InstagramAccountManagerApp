package dao;

import model.AccountInfo;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountInfoDAO {

    // Yeni hesap ekle
    public void addAccount(AccountInfo acc) {
        String sql = "INSERT INTO accounts_info(username, password, email, email_password, " +
                     "recovery_email, email_onayli, phone, phone_kayitli, account_created, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getPassword());
            ps.setString(3, acc.getEmail());
            ps.setString(4, acc.getEmailPassword());
            ps.setString(5, acc.getRecoveryEmail());
            ps.setString(6, acc.getEmailOnayli());
            ps.setString(7, acc.getPhone());
            ps.setString(8, acc.getPhoneKayitli());
            ps.setDate(9, acc.getAccountCreated());
            ps.setString(10, acc.getNotes());

            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hesapları listele
    public List<AccountInfo> getAllAccounts() {
        List<AccountInfo> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts_info";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                AccountInfo acc = new AccountInfo();
                acc.setId(rs.getInt("id"));
                acc.setUsername(rs.getString("username"));
                acc.setPassword(rs.getString("password"));
                acc.setEmail(rs.getString("email"));
                acc.setEmailPassword(rs.getString("email_password"));
                acc.setRecoveryEmail(rs.getString("recovery_email"));
                acc.setEmailOnayli(rs.getString("email_onayli"));
                acc.setPhone(rs.getString("phone"));
                acc.setPhoneKayitli(rs.getString("phone_kayitli"));
                acc.setAccountCreated(rs.getDate("account_created"));
                acc.setNotes(rs.getString("notes"));

                list.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hesap sil
    public void deleteAccount(int id) {
        String sql = "DELETE FROM accounts_info WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Hesap güncelle
public void updateAccount(AccountInfo acc) {
    String sql = "UPDATE accounts_info SET username=?, password=?, email=?, email_password=?, " +
                 "recovery_email=?, email_onayli=?, phone=?, phone_kayitli=?, account_created=?, notes=? " +
                 "WHERE id=?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, acc.getUsername());
        ps.setString(2, acc.getPassword());
        ps.setString(3, acc.getEmail());
        ps.setString(4, acc.getEmailPassword());
        ps.setString(5, acc.getRecoveryEmail());
        ps.setString(6, acc.getEmailOnayli());
        ps.setString(7, acc.getPhone());
        ps.setString(8, acc.getPhoneKayitli());
        ps.setDate(9, acc.getAccountCreated());
        ps.setString(10, acc.getNotes());
        ps.setInt(11, acc.getId());

        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}