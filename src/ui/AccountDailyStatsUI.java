package instatracker.ui;

import dao.AccountDailyStatsDAO;
import dao.AccountInfoDAO;
import model.AccountDailyStats;
import model.AccountInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccountDailyStatsUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private final AccountDailyStatsDAO dailyDao = new AccountDailyStatsDAO();
    private final AccountInfoDAO infoDao = new AccountInfoDAO();

    // username -> account_id
    private final Map<String, Integer> accountMap = new LinkedHashMap<>();

    // --- Filtre bileşenleri ---
    private JCheckBox chkToday, chkAccount, chkRange;
    private JComboBox<String> cbFilterAccount;
    private JSpinner spFrom, spTo;

    public AccountDailyStatsUI() {
        setLayout(new BorderLayout());

        // ===== ÜST: TOOLBAR =====
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        JButton btnAddToday = new JButton("➕ Bugün Girişi");
        JButton btnRefresh  = new JButton("🔄 Yenile");
        JButton btnDelete   = new JButton("🗑️ Seçili Satırı Sil");
        toolbar.add(btnAddToday);
        toolbar.add(btnRefresh);
        toolbar.addSeparator();
        toolbar.add(btnDelete);
        add(toolbar, BorderLayout.NORTH);

        // ===== TOOLBAR ALTINA İNCE FİLTRE PANELİ =====
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                UIManager.getColor("Panel.background").darker()));

        chkToday = new JCheckBox("Sadece bugün");
        chkAccount = new JCheckBox("Hesaba göre");
        cbFilterAccount = new JComboBox<>();
        fillAccountsCombo(cbFilterAccount);           // hesapları doldur
        cbFilterAccount.setEnabled(false);
        chkAccount.addActionListener(e ->
                cbFilterAccount.setEnabled(chkAccount.isSelected()));

        chkRange = new JCheckBox("Tarih aralığı");
        spFrom = new JSpinner(new SpinnerDateModel());
        spTo   = new JSpinner(new SpinnerDateModel());
        spFrom.setEditor(new JSpinner.DateEditor(spFrom, "yyyy-MM-dd"));
        spTo.setEditor(new JSpinner.DateEditor(spTo, "yyyy-MM-dd"));
        spFrom.setEnabled(false);
        spTo.setEnabled(false);
        chkRange.addActionListener(e -> {
            boolean on = chkRange.isSelected();
            spFrom.setEnabled(on);
            spTo.setEnabled(on);
        });

        JButton btnApply = new JButton("Filtrele");

        filterBar.add(chkToday);
        filterBar.add(chkAccount);
        filterBar.add(cbFilterAccount);
        filterBar.add(chkRange);
        filterBar.add(new JLabel("Başlangıç:"));
        filterBar.add(spFrom);
        filterBar.add(new JLabel("Bitiş:"));
        filterBar.add(spTo);
        filterBar.add(btnApply);

        // Toolbar’ın hemen altına yerleştiriyoruz (üstte iki şerit gibi durur)
        add(filterBar, BorderLayout.AFTER_LAST_LINE);

        // ===== ORTA: TABLO (DEĞİŞMEDİ) =====
        model = new DefaultTableModel(new String[]{
                "ID", "Hesap", "Tarih", "Gönderi", "Takipçi +", "Takipçi -",
                "Following +", "Following -", "Beğeni", "Yorum", "Not"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== EVENTLER =====
        btnAddToday.addActionListener(e -> showDailyEntryDialog());
        btnRefresh.addActionListener(e -> loadTableFiltered(false, null, null, null));
        btnDelete.addActionListener(e -> deleteSelected());

        btnApply.addActionListener(e -> {
            Integer accId = null;
            if (chkAccount.isSelected() && cbFilterAccount.getSelectedItem() != null) {
                accId = accountMap.get(cbFilterAccount.getSelectedItem().toString());
            }
            java.util.Date from = chkRange.isSelected() ? (java.util.Date) spFrom.getValue() : null;
            java.util.Date to   = chkRange.isSelected() ? (java.util.Date) spTo.getValue()   : null;
            loadTableFiltered(chkToday.isSelected(), accId, from, to);
        });

        // Başlangıçta tüm kayıtları yükle
        loadTableFiltered(false, null, null, null);
    }

    // === Yardımcılar ===
    private void fillAccountsCombo(JComboBox<String> combo) {
        combo.removeAllItems();
        accountMap.clear();
        try {
            List<AccountInfo> accounts = infoDao.getAllAccounts();
            for (AccountInfo a : accounts) {
                combo.addItem(a.getUsername());
                accountMap.put(a.getUsername(), a.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTableFiltered(Boolean onlyToday, Integer accountId, java.util.Date from, java.util.Date to) {
        model.setRowCount(0);

        java.sql.Date sFrom = (from != null) ? new java.sql.Date(from.getTime()) : null;
        java.sql.Date sTo   = (to   != null) ? new java.sql.Date(to.getTime())   : null;

        if (Boolean.TRUE.equals(onlyToday)) {
            java.time.LocalDate today = java.time.LocalDate.now();
            sFrom = java.sql.Date.valueOf(today);
            sTo   = java.sql.Date.valueOf(today);
        }

        List<Object[]> rows = dailyDao.getDailyWithFilters(sFrom, sTo, accountId);
        for (Object[] r : rows) model.addRow(r);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Silmek için bir satır seçin.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        dailyDao.deleteById(id);
        loadTableFiltered(false, null, null, null);
        JOptionPane.showMessageDialog(this, "Kayıt silindi.");
    }

    // === Modal form: Bugün girişi ===
    private void showDailyEntryDialog() {
        JComboBox<String> cbAccounts = new JComboBox<>();
        fillAccountsCombo(cbAccounts);
        if (cbAccounts.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Önce bir hesap ekleyin (Hesap Bilgileri sekmesi).");
            return;
        }

        JSpinner spPosts    = spinner();
        JSpinner spGain     = spinner();
        JSpinner spLost     = spinner();
        JSpinner spAdd      = spinner();
        JSpinner spRem      = spinner();
        JSpinner spLikes    = spinner();
        JSpinner spComments = spinner();
        JTextField tfNotes  = new JTextField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        addRow(form, gc, y++, "Hesap", cbAccounts);
        addRow(form, gc, y++, "Gönderi", spPosts);
        addRow(form, gc, y++, "Takipçi +", spGain);
        addRow(form, gc, y++, "Takipçi -", spLost);
        addRow(form, gc, y++, "Following +", spAdd);
        addRow(form, gc, y++, "Following -", spRem);
        addRow(form, gc, y++, "Beğeni", spLikes);
        addRow(form, gc, y++, "Yorum", spComments);
        addRow(form, gc, y++, "Not", tfNotes);

        int res = JOptionPane.showConfirmDialog(this, form, "Bugün İçin Giriş",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String username = (String) cbAccounts.getSelectedItem();
        int accountId = accountMap.get(username);

        AccountDailyStats s = new AccountDailyStats();
        s.setAccountId(accountId);
        s.setDailyPosts((Integer) spPosts.getValue());
        s.setDailyFollowersGain((Integer) spGain.getValue());
        s.setDailyFollowersLost((Integer) spLost.getValue());
        s.setDailyFollowingAdded((Integer) spAdd.getValue());
        s.setDailyFollowingRemoved((Integer) spRem.getValue());
        s.setDailyLikes((Integer) spLikes.getValue());
        s.setDailyComments((Integer) spComments.getValue());
        s.setNotes(tfNotes.getText());

        dailyDao.insertOrUpdateToday(s);
        loadTableFiltered(false, null, null, null);
        JOptionPane.showMessageDialog(this, "Bugün için kayıt kaydedildi / güncellendi.");
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int y, String label, JComponent comp) {
        gc.gridx = 0; gc.gridy = y; gc.weightx = 0; panel.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = y; gc.weightx = 1; panel.add(comp, gc);
    }

    private JSpinner spinner() {
        return new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
    }
}
