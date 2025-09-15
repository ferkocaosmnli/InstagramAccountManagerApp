package instatracker.ui;

import dao.AccountInfoDAO;
import model.AccountInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class AccountInfoUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AccountInfoDAO dao = new AccountInfoDAO();

    public AccountInfoUI() {
        setLayout(new BorderLayout());

        // Tüm sütunlar
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Username", "Password", "Email", "Email Password",
                "Recovery Email", "Email Onaylı", "Phone", "Phone Kayıtlı",
                "Kuruluş Tarihi", "Notes"
        }, 0);

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Butonlar
        JButton btnLoad = new JButton("🔄 Yenile");
        JButton btnAdd = new JButton("➕ Hesap Ekle");
        JButton btnDelete = new JButton("❌ Hesap Sil");
        JButton btnUpdate = new JButton("✏️ Hesap Güncelle");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLoad);
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);

        // Ekrana ekle
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Eventler
        btnLoad.addActionListener(e -> loadAccounts());
        btnAdd.addActionListener(e -> addAccount());
        btnDelete.addActionListener(e -> deleteAccount());
        btnUpdate.addActionListener(e -> updateAccount());

        // İlk açılışta yükle
        loadAccounts();
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        List<AccountInfo> accounts = dao.getAllAccounts();
        for (AccountInfo acc : accounts) {
            tableModel.addRow(new Object[]{
                    acc.getId(),
                    acc.getUsername(),
                    acc.getPassword(),
                    acc.getEmail(),
                    acc.getEmailPassword(),
                    acc.getRecoveryEmail(),
                    acc.getEmailOnayli(),
                    acc.getPhone(),
                    acc.getPhoneKayitli(),
                    acc.getAccountCreated(),
                    acc.getNotes()
            });
        }
    }

    private void addAccount() {
        AccountInfo acc = showAccountForm(null);
        if (acc != null) {
            dao.addAccount(acc);
            loadAccounts();
            JOptionPane.showMessageDialog(this, "Hesap eklendi!");
        }
    }

    private void deleteAccount() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            dao.deleteAccount(id);
            loadAccounts();
            JOptionPane.showMessageDialog(this, "Hesap silindi!");
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen silmek için bir hesap seçin!");
        }
    }

    private void updateAccount() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            AccountInfo acc = new AccountInfo();
            acc.setId((int) tableModel.getValueAt(selectedRow, 0));
            acc.setUsername((String) tableModel.getValueAt(selectedRow, 1));
            acc.setPassword((String) tableModel.getValueAt(selectedRow, 2));
            acc.setEmail((String) tableModel.getValueAt(selectedRow, 3));
            acc.setEmailPassword((String) tableModel.getValueAt(selectedRow, 4));
            acc.setRecoveryEmail((String) tableModel.getValueAt(selectedRow, 5));
            acc.setEmailOnayli((String) tableModel.getValueAt(selectedRow, 6));
            acc.setPhone((String) tableModel.getValueAt(selectedRow, 7));
            acc.setPhoneKayitli((String) tableModel.getValueAt(selectedRow, 8));
            acc.setAccountCreated((Date) tableModel.getValueAt(selectedRow, 9));
            acc.setNotes((String) tableModel.getValueAt(selectedRow, 10));

            // Formu aç ve güncel değerleri doldur
            AccountInfo updatedAcc = showAccountForm(acc);
            if (updatedAcc != null) {
                dao.updateAccount(updatedAcc);
                loadAccounts();
                JOptionPane.showMessageDialog(this, "Hesap güncellendi!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen güncellemek için bir hesap seçin!");
        }
    }

    // Ortak form: ekleme + güncelleme
    private AccountInfo showAccountForm(AccountInfo acc) {
        JTextField usernameField = new JTextField(acc != null ? acc.getUsername() : "");
        JTextField passwordField = new JTextField(acc != null ? acc.getPassword() : "");
        JTextField emailField = new JTextField(acc != null ? acc.getEmail() : "");
        JTextField emailPasswordField = new JTextField(acc != null ? acc.getEmailPassword() : "");
        JTextField recoveryEmailField = new JTextField(acc != null ? acc.getRecoveryEmail() : "");
        JComboBox<String> emailOnayliBox = new JComboBox<>(new String[]{"yes", "no"});
        emailOnayliBox.setSelectedItem(acc != null ? acc.getEmailOnayli() : "no");
        JTextField phoneField = new JTextField(acc != null ? acc.getPhone() : "");
        JComboBox<String> phoneKayitliBox = new JComboBox<>(new String[]{"yes", "no"});
        phoneKayitliBox.setSelectedItem(acc != null ? acc.getPhoneKayitli() : "yes");
        JTextField dateField = new JTextField(acc != null && acc.getAccountCreated() != null ? acc.getAccountCreated().toString() : "2025-01-01");
        JTextField notesField = new JTextField(acc != null ? acc.getNotes() : "");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Kullanıcı Adı:"));
        panel.add(usernameField);
        panel.add(new JLabel("Şifre:"));
        panel.add(passwordField);
        panel.add(new JLabel("E-posta:"));
        panel.add(emailField);
        panel.add(new JLabel("E-posta Şifresi:"));
        panel.add(emailPasswordField);
        panel.add(new JLabel("Kurtarma E-postası:"));
        panel.add(recoveryEmailField);
        panel.add(new JLabel("E-posta Onaylı mı?"));
        panel.add(emailOnayliBox);
        panel.add(new JLabel("Telefon:"));
        panel.add(phoneField);
        panel.add(new JLabel("Telefon Kayıtlı mı?"));
        panel.add(phoneKayitliBox);
        panel.add(new JLabel("Kuruluş Tarihi (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Notlar:"));
        panel.add(notesField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                acc == null ? "Yeni Hesap Ekle" : "Hesabı Güncelle", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            AccountInfo newAcc = new AccountInfo();
            if (acc != null) newAcc.setId(acc.getId()); // update için ID lazım
            newAcc.setUsername(usernameField.getText());
            newAcc.setPassword(passwordField.getText());
            newAcc.setEmail(emailField.getText());
            newAcc.setEmailPassword(emailPasswordField.getText());
            newAcc.setRecoveryEmail(recoveryEmailField.getText());
            newAcc.setEmailOnayli(emailOnayliBox.getSelectedItem().toString());
            newAcc.setPhone(phoneField.getText());
            newAcc.setPhoneKayitli(phoneKayitliBox.getSelectedItem().toString());
            newAcc.setAccountCreated(Date.valueOf(dateField.getText()));
            newAcc.setNotes(notesField.getText());

            return newAcc;
        }
        return null;
    }
}
