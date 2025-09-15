package instatracker.ui;

import dao.AccountStatsDAO;
import model.AccountStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class AccountStatsUI extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private AccountStatsDAO dao = new AccountStatsDAO();

    public AccountStatsUI() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{
                "ID", "Username", "Posts", "Followers", "Following", "Updated At"
        }, 0) {
            // Sayısal sütunlar için doğru tip döndür (opsiyonel ama faydalı)
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 3, 4 -> Integer.class; // ID, Posts, Followers, Following
                    default -> Object.class;
                };
            }
            // tabloyu kullanıcı düzenlemesin
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnAdd    = new JButton("➕ Ekle");
        JButton btnUpdate = new JButton("✏️ Güncelle");
        JButton btnDelete = new JButton("❌ Sil");
        JButton btnLoad   = new JButton("🔄 Yenile");

        JPanel panel = new JPanel();
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnLoad);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // --- SIRALAMA: numerik comparator + başlığa tıklanınca DESC
        table.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(2, (o1, o2) -> Integer.compare(((Number) o1).intValue(), ((Number) o2).intValue())); // Posts
        sorter.setComparator(3, (o1, o2) -> Integer.compare(((Number) o1).intValue(), ((Number) o2).intValue())); // Followers
        sorter.setComparator(4, (o1, o2) -> Integer.compare(((Number) o1).intValue(), ((Number) o2).intValue())); // Following
        table.setRowSorter(sorter);

        // "Posts" başlığına basılınca en yüksek -> en düşük sırala
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col == 2) { // 0:ID, 1:Username, 2:Posts
                    List<RowSorter.SortKey> keys = new ArrayList<>();
                    keys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
                    sorter.setSortKeys(keys);
                    sorter.sort();
                }
            }
        });

        btnLoad.addActionListener(e -> loadStats());
        btnUpdate.addActionListener(e -> updateStats());
        btnDelete.addActionListener(e -> deleteStats());
        btnAdd.addActionListener(e -> addStats()); // YENİ

        loadStats();
    }

    private void loadStats() {
        tableModel.setRowCount(0);
        List<Object[]> stats = dao.getAllStats();
        for (Object[] row : stats) {
            tableModel.addRow(row);
        }
    }

    // ---- YENİ: username yazmak yerine listeden seç ----
    private void addStats() {
        // accounts_info’dan (id, username) çek
        List<Object[]> accounts = dao.getAllAccounts();
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Önce Hesap Bilgileri sekmesinden hesap ekleyin.");
            return;
        }

        // username -> account_id map
        Map<String, Integer> accountMap = new LinkedHashMap<>();
        JComboBox<String> cbUsernames = new JComboBox<>();
        for (Object[] a : accounts) {
            int id = (int) a[0];
            String username = (String) a[1];
            cbUsernames.addItem(username);
            accountMap.put(username, id);
        }

        JSpinner spPosts     = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        JSpinner spFollowers = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));
        JSpinner spFollowing = new JSpinner(new SpinnerNumberModel(0, 0, 1_000_000, 1));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        addRow(form, gc, y++, "Username", cbUsernames);
        addRow(form, gc, y++, "Posts", spPosts);
        addRow(form, gc, y++, "Followers", spFollowers);
        addRow(form, gc, y++, "Following", spFollowing);

        int res = JOptionPane.showConfirmDialog(this, form, "Yeni İstatistik Ekle",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String username = cbUsernames.getSelectedItem().toString();
        int accountId = accountMap.get(username);

        AccountStats s = new AccountStats();
        s.setAccountId(accountId);
        s.setPosts((Integer) spPosts.getValue());
        s.setFollowers((Integer) spFollowers.getValue());
        s.setFollowing((Integer) spFollowing.getValue());

        boolean ok = dao.insertStats(s);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Ekleme sırasında hata oluştu.");
            return;
        }
        loadStats();
        JOptionPane.showMessageDialog(this, "Kayıt eklendi.");
    }

    private void updateStats() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            String username = (String) tableModel.getValueAt(row, 1);

            String posts = JOptionPane.showInputDialog(this, "Posts:", tableModel.getValueAt(row, 2));
            String followers = JOptionPane.showInputDialog(this, "Followers:", tableModel.getValueAt(row, 3));
            String following = JOptionPane.showInputDialog(this, "Following:", tableModel.getValueAt(row, 4));
            if (posts == null || followers == null || following == null) return;

            try {
                AccountStats stats = new AccountStats();
                stats.setId(id);
                stats.setPosts(Integer.parseInt(posts.trim()));
                stats.setFollowers(Integer.parseInt(followers.trim()));
                stats.setFollowing(Integer.parseInt(following.trim()));

                dao.updateStats(stats);
                loadStats();
                JOptionPane.showMessageDialog(this, username + " hesabı güncellendi!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçersiz sayı girdin.");
            }
        }
    }

    private void deleteStats() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Seçili kaydı silmek istiyor musun?",
                    "Onay", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            dao.deleteStats(id);
            loadStats();
            JOptionPane.showMessageDialog(this, "İstatistik silindi!");
        }
    }

    private void addRow(JPanel p, GridBagConstraints gc, int y, String label, JComponent c) {
        gc.gridx = 0; gc.gridy = y; gc.weightx = 0; p.add(new JLabel(label), gc);
        gc.gridx = 1; gc.gridy = y; gc.weightx = 1; p.add(c, gc);
    }
}
