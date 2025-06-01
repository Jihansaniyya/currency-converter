package com.currencyapp.gui;

import com.currencyapp.repository.DataRepository;
import com.currencyapp.util.LanguageManager;
import com.currencyapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Panel untuk menampilkan history konversi dengan pemisah antar baris
 */
public class HistoryPanel extends JPanel {
    
    private final DataRepository repository;
    private final LanguageManager languageManager;
    private final ThemeManager themeManager;
    
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    
    public HistoryPanel(DataRepository repository, LanguageManager languageManager) {
        this.repository = repository;
        this.languageManager = languageManager;
        this.themeManager = ThemeManager.getInstance();
        
        setupUI();
        refreshHistory();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(LanguageManager.getString("history.title")),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Apply theme to main panel
        themeManager.applyThemeTo(this);
        setBackground(themeManager.getCurrentBackgroundColor());
        
        // Buat model tabel
        String[] columnNames = {
            LanguageManager.getString("history.column.time"),
            LanguageManager.getString("history.column.from"),
            LanguageManager.getString("history.column.amount"),
            LanguageManager.getString("history.column.to"),
            LanguageManager.getString("history.column.result")
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua cell tidak bisa diedit
            }
        };
        
        // Buat tabel
        historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.getTableHeader().setReorderingAllowed(false);
        
        // Custom cell renderer untuk pemisah antar baris
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Alternating row colors dengan pemisah yang lebih jelas
                    if (row % 2 == 0) {
                        comp.setBackground(themeManager.getCurrentFieldColor());
                    } else {
                        Color altColor = themeManager.isDarkMode() ? 
                            new Color(45, 45, 45) : new Color(240, 240, 240);
                        comp.setBackground(altColor);
                    }
                } else {
                    comp.setBackground(themeManager.isDarkMode() ? 
                        new Color(60, 80, 120) : new Color(184, 207, 229));
                }
                
                comp.setForeground(themeManager.getCurrentForegroundColor());
                
                // Add border untuk pemisah antar baris
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, 
                        themeManager.isDarkMode() ? new Color(80, 80, 80) : new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
                
                return comp;
            }
        };
        
        // Apply custom renderer ke semua kolom
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }
        
        // Apply dark theme to table
        historyTable.setBackground(themeManager.getCurrentFieldColor());
        historyTable.setForeground(themeManager.getCurrentForegroundColor());
        historyTable.setSelectionBackground(themeManager.isDarkMode() ? 
            new Color(60, 80, 120) : new Color(184, 207, 229));
        historyTable.setSelectionForeground(themeManager.getCurrentForegroundColor());
        historyTable.setGridColor(themeManager.isDarkMode() ? 
            new Color(80, 80, 80) : new Color(200, 200, 200));
        historyTable.setShowGrid(true);
        historyTable.setIntercellSpacing(new Dimension(1, 2));
        historyTable.setRowHeight(40); // Increase row height untuk spacing yang lebih baik
        
        // Apply theme to table header
        JTableHeader header = historyTable.getTableHeader();
        header.setBackground(themeManager.getCurrentBackgroundColor());
        header.setForeground(themeManager.getCurrentForegroundColor());
        header.setFont(header.getFont().deriveFont(Font.BOLD));
        
        // Atur lebar kolom
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(160); // Waktu
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Dari
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Jumlah
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Ke
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Hasil
        
        // Create scroll pane with dark theme
        scrollPane = new JScrollPane(historyTable);
        scrollPane.setBackground(themeManager.getCurrentBackgroundColor());
        scrollPane.getViewport().setBackground(themeManager.getCurrentBackgroundColor());
        scrollPane.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        
        // Tambahkan ke panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void refreshHistory() {
        // Hapus semua data
        tableModel.setRowCount(0);
        
        // Ambil data history
        List<Map<String, Object>> historyData = repository.load("conversion_history");
        
        System.out.println("Loaded " + historyData.size() + " history items");
        
        // Format tanggal dan angka
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        
        // Tambahkan data ke tabel
        for (Map<String, Object> data : historyData) {
            try {
                // Ambil timestamp dengan penanganan tipe
                Object timestampObj = data.get("timestamp");
                long timestamp;
                if (timestampObj instanceof Long) {
                    timestamp = (Long) timestampObj;
                } else if (timestampObj instanceof Integer) {
                    timestamp = ((Integer) timestampObj).longValue();
                } else {
                    timestamp = System.currentTimeMillis(); // default jika format tidak valid
                }
                String formattedTime = dateFormat.format(new Date(timestamp));
                
                // Ambil kode mata uang
                String fromCurrency = (String) data.get("sourceCurrency");
                String toCurrency = (String) data.get("targetCurrency");
                
                // Ambil amount dengan penanganan tipe
                Object amountObj = data.get("amount");
                double amount;
                if (amountObj instanceof Number) {
                    amount = ((Number) amountObj).doubleValue();
                } else if (amountObj instanceof String) {
                    amount = Double.parseDouble((String) amountObj);
                } else {
                    System.out.println("Unknown amount type: " + (amountObj != null ? amountObj.getClass().getName() : "null"));
                    amount = 0.0;
                }
                
                // Ambil result dengan penanganan tipe
                Object resultObj = data.get("result");
                double result;
                if (resultObj instanceof Number) {
                    result = ((Number) resultObj).doubleValue();
                } else if (resultObj instanceof String) {
                    result = Double.parseDouble((String) resultObj);
                } else {
                    result = 0.0;
                }
                
                // Tambahkan baris ke tabel dengan formatting yang lebih baik
                tableModel.addRow(new Object[]{
                    formattedTime,
                    fromCurrency,
                    decimalFormat.format(amount),
                    toCurrency,
                    decimalFormat.format(result)
                });
            } catch (Exception e) {
                System.err.println("Error processing history data entry: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            // Tampilkan pesan jika tidak ada data
            JOptionPane.showMessageDialog(this,
                LanguageManager.getString("history.empty"),
                LanguageManager.getString("dialog.info"),
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void refreshLanguage() {
        // Update column headers
        String[] columnNames = {
            LanguageManager.getString("history.column.time"),
            LanguageManager.getString("history.column.from"),
            LanguageManager.getString("history.column.amount"),
            LanguageManager.getString("history.column.to"),
            LanguageManager.getString("history.column.result")
        };
        
        for (int i = 0; i < columnNames.length; i++) {
            historyTable.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);
        }
        
        historyTable.getTableHeader().repaint();
        
        // Update border title
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(LanguageManager.getString("history.title")),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    public void refreshTheme() {
        // Apply theme to main panel
        themeManager.applyThemeTo(this);
        setBackground(themeManager.getCurrentBackgroundColor());
        
        // Apply theme to table
        if (historyTable != null) {
            historyTable.setBackground(themeManager.getCurrentFieldColor());
            historyTable.setForeground(themeManager.getCurrentForegroundColor());
            historyTable.setSelectionBackground(themeManager.isDarkMode() ? 
                new Color(60, 80, 120) : new Color(184, 207, 229));
            historyTable.setSelectionForeground(themeManager.getCurrentForegroundColor());
            historyTable.setGridColor(themeManager.isDarkMode() ? 
                new Color(80, 80, 80) : new Color(200, 200, 200));
            
            // Refresh custom renderer
            DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    
                    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    if (!isSelected) {
                        if (row % 2 == 0) {
                            comp.setBackground(themeManager.getCurrentFieldColor());
                        } else {
                            Color altColor = themeManager.isDarkMode() ? 
                                new Color(45, 45, 45) : new Color(240, 240, 240);
                            comp.setBackground(altColor);
                        }
                    } else {
                        comp.setBackground(themeManager.isDarkMode() ? 
                            new Color(60, 80, 120) : new Color(184, 207, 229));
                    }
                    
                    comp.setForeground(themeManager.getCurrentForegroundColor());
                    
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, 
                            themeManager.isDarkMode() ? new Color(80, 80, 80) : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
                    
                    return comp;
                }
            };
            
            // Apply ke semua kolom
            for (int i = 0; i < historyTable.getColumnCount(); i++) {
                historyTable.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
            }
            
            // Apply theme to table header
            JTableHeader header = historyTable.getTableHeader();
            if (header != null) {
                header.setBackground(themeManager.getCurrentBackgroundColor());
                header.setForeground(themeManager.getCurrentForegroundColor());
            }
        }
        
        // Apply theme to scroll pane
        if (scrollPane != null) {
            scrollPane.setBackground(themeManager.getCurrentBackgroundColor());
            scrollPane.getViewport().setBackground(themeManager.getCurrentBackgroundColor());
            scrollPane.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        }
        
        repaint();
    }
}