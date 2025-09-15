package instatracker.ui;

import dao.AccountOpsDAO;
import dao.AccountInfoDAO;
import model.AccountOps;
import model.AccountInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AccountOpsUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    private final AccountOpsDAO opsDao = new AccountOpsDAO();
    private final AccountInfoDAO infoDao = new AccountInfoDAO();

    // username -> id
    private final Map<String, Integer> accountMap = new LinkedHashMap<>();

    // filtre bileşenleri
    private JCheckBox chkToday, chkAccount, chkRange;
    private JComboBox<String> cbAccountFilter, cbTypeFilter;
    private JSpinner spFrom, spTo;

    public AccountOpsUI() {
        setLayout(new BorderLayout());

        // ===== Toolbar =====
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);
        JButton btnAdd = new JButton("➕ Aksiyon Ekle");
        JButton btnRefresh = new JButton("🔄 Yenile");
        JButton btnDelete = new JButton("🗑️ Sil");
        bar.add(btnAdd); bar.add(btnRefresh); bar.addSeparator(); bar.add(btnDelete);
        add(bar, BorderLayout.NORTH);

        // ===== Filtre paneli (ince) =====
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filter.setBorder(BorderFactory.createMatteBorder(0,0,1,0,
                UIManager.getColor("Panel.background").darker()));
        chkToday = new JCheckBox("Sadece bugün");

        chkAccount = new JCheckBox("Hesap");
        cbAccountFilter = new JComboBox<>();
        fillAccounts(cbAccountFilter);
        cbAccountFilter.setEnabled(false);
        chkAccount.addActionListener(e -> cbAccountFilter.setEnabled(chkAccount.isSelected()));

        cbTypeFilter = new JComboBox<>(new String[]{
                "ALL","follow","unfollow","like","comment","dm","post","story","other"
        });

        chkRange = new JCheckBox("Tarih aralığı");
        spFrom = new JSpinner(new SpinnerDateModel());
        spTo   = new JSpinner(new SpinnerDateModel());
        spFrom.setEditor(new JSpinner.DateEditor(spFrom, "yyyy-MM-dd"));
        spTo.setEditor(new JSpinner.DateEditor(spTo, "yyyy-MM-dd"));
        spFrom.setEnabled(false); spTo.setEnabled(false);
        chkRange.addActionListener(e -> {
            boolean on = chkRange.isSelected();
            spFrom.setEnabled(on); spTo.setEnabled(on);
        });

        JButton btnApply = new JButton("Filtrele");

        filter.add(chkToday);
        filter.add(chkAccount);
        filter.add(cbAccountFilter);
        filter.add(new JLabel("Tür:"));
        filter.add(cbTypeFilter);
        filter.add(chkRange);
        filter.add(new JLabel("Başlangıç:"));
        filter.add(spFrom);
        filter.add(new JLabel("Bitiş:"));
        filter.add(spTo);
        filter.add(btnApply);

        add(filter, BorderLayout.AFTER_LAST_LINE);

        // ===== Tablo =====
        model = new DefaultTableModel(new String[]{
                "ID","Hesap","Tarih","Tür","Adet","Araç","Hedef","Durum","Not"
        }, 0) { @Override public boolean isCellEditable(int r,int c){ return false; } };
        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Events
        btnAdd.addActionListener(e -> showAddDialog());
        btnRefresh.addActionListener(e -> loadTable(false, null, null, null, "ALL"));
        btnApply.addActionListener(e -> applyFilters());
        btnDelete.addActionListener(e -> deleteSelected());

        // İlk yükleme
        loadTable(false, null, null, null, "ALL");
    }

    private void fillAccounts(JComboBox<String> combo) {
        combo.removeAllItems();
        accountMap.clear();
        try {
            List<AccountInfo> list = infoDao.getAllAccounts();
            for (AccountInfo a : list) {
                combo.addItem(a.getUsername());
                accountMap.put(a.getUsername(), a.getId());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTable(boolean onlyToday, Integer accountId, java.util.Date from, java.util.Date to, String type) {
        model.setRowCount(0);
        Date sFrom = (from!=null)? new Date(from.getTime()) : null;
        Date sTo   = (to!=null)?   new Date(to.getTime())   : null;

        if (onlyToday) {
            var today = java.time.LocalDate.now();
            sFrom = Date.valueOf(today);
            sTo   = Date.valueOf(today);
        }

        List<Object[]> rows = opsDao.listFiltered(sFrom, sTo, accountId, type);
        for (Object[] r : rows) model.addRow(r);
    }

    private void applyFilters() {
        Integer accId = null;
        if (chkAccount.isSelected() && cbAccountFilter.getSelectedItem()!=null) {
            accId = accountMap.get(cbAccountFilter.getSelectedItem().toString());
        }
        java.util.Date from = chkRange.isSelected()? (java.util.Date) spFrom.getValue() : null;
        java.util.Date to   = chkRange.isSelected()? (java.util.Date) spTo.getValue()   : null;
        String type = cbTypeFilter.getSelectedItem()!=null? cbTypeFilter.getSelectedItem().toString() : "ALL";
        loadTable(chkToday.isSelected(), accId, from, to, type);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this,"Silmek için satır seçin."); return; }
        int id = (int) model.getValueAt(row, 0);
        opsDao.deleteById(id);
        applyFilters();
        JOptionPane.showMessageDialog(this, "Kayıt silindi.");
    }

    // Modal form (UPsert)
    private void showAddDialog() {
        JComboBox<String> cbAccount = new JComboBox<>();
        fillAccounts(cbAccount);
        if (cbAccount.getItemCount()==0) {
            JOptionPane.showMessageDialog(this, "Önce Hesap Bilgileri sekmesinden hesap ekleyin.");
            return;
        }

        JComboBox<String> cbType = new JComboBox<>(new String[]{
                "follow","unfollow","like","comment","dm","post","story","other"
        });
        JSpinner spQty = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        JTextField tfTool = new JTextField("instafly");
        JTextField tfTarget = new JTextField();
        JTextField tfNotes = new JTextField();
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"planned","done","skipped","failed"});

        JCheckBox chkCustomDate = new JCheckBox("Tarih ver");
        JSpinner spDate = new JSpinner(new SpinnerDateModel());
        spDate.setEditor(new JSpinner.DateEditor(spDate, "yyyy-MM-dd"));
        spDate.setEnabled(false);
        chkCustomDate.addActionListener(e -> spDate.setEnabled(chkCustomDate.isSelected()));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        addRow(form, gc, y++, "Hesap", cbAccount);
        addRow(form, gc, y++, "Tür", cbType);
        addRow(form, gc, y++, "Adet", spQty);
        addRow(form, gc, y++, "Araç", tfTool);
        addRow(form, gc, y++, "Hedef/Niş", tfTarget);
        addRow(form, gc, y++, "Durum", cbStatus);
        addRow(form, gc, y++, "Not", tfNotes);
        addRow(form, gc, y++, "Tarih", chkCustomDate);
        addRow(form, gc, y++, "", spDate);

        int res = JOptionPane.showConfirmDialog(this, form, "Aksiyon Ekle/Güncelle",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String username = cbAccount.getSelectedItem().toString();
        Integer accountId = accountMap.get(username);

        AccountOps op = new AccountOps();
        op.setAccountId(accountId);
        op.setActionType(cbType.getSelectedItem().toString());
        op.setQuantity((Integer) spQty.getValue());
        op.setTool(textOrNull(tfTool));
        op.setTargetTag(textOrNull(tfTarget));
        op.setNotes(textOrNull(tfNotes));
        op.setStatus(cbStatus.getSelectedItem().toString());
        if (chkCustomDate.isSelected()) {
            java.util.Date d = (java.util.Date) spDate.getValue();
            op.setActionDate(new Date(d.getTime()));
        } else {
            op.setActionDate(null); // DB CURRENT_DATE kullanacak
        }

        opsDao.insertOrUpdate(op);
        applyFilters();
        JOptionPane.showMessageDialog(this, "Kayıt eklendi/güncellendi.");
    }

    private void addRow(JPanel p, GridBagConstraints gc, int y, String label, JComponent comp) {
        gc.gridx=0; gc.gridy=y; gc.weightx=0; p.add(new JLabel(label), gc);
        gc.gridx=1; gc.gridy=y; gc.weightx=1; p.add(comp, gc);
    }

    private String textOrNull(JTextField tf) {
        String s = tf.getText();
        return (s==null || s.trim().isEmpty())? null : s.trim();
    }
}
