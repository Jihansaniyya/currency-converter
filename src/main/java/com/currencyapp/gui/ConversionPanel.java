package com.currencyapp.gui;

import com.currencyapp.model.Currency;
import com.currencyapp.service.CurrencyConverter;
import com.currencyapp.repository.DataRepository;
import com.currencyapp.util.LanguageManager;
import com.currencyapp.util.ThemeManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel untuk melakukan konversi mata uang dengan ComboBox yang lebih baik untuk dark mode
 */
public class ConversionPanel extends JPanel implements ActionListener {
    
    private final CurrencyConverter converter;
    private final DataRepository repository;
    private final HistoryPanel historyPanel;
    private final LanguageManager languageManager;
    private final ThemeManager themeManager;
    
    private JComboBox<String> fromCurrencyCombo;
    private JComboBox<String> toCurrencyCombo;
    private JTextField amountField;
    private JLabel resultLabel;
    private JButton swapButton;
    private JButton convertButton;
    private JLabel amountLabel;
    private JLabel fromLabel;
    private JLabel toLabel;
    private boolean isProcessing = false;
    
    public ConversionPanel(CurrencyConverter converter, DataRepository repository, 
                           HistoryPanel historyPanel, LanguageManager languageManager) {
        this.converter = converter;
        this.repository = repository;
        this.historyPanel = historyPanel;
        this.languageManager = languageManager;
        this.themeManager = ThemeManager.getInstance();
        
        setupUI();
        
        // Tampilkan hasil konversi terakhir jika ada
        displayLastConversion();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(LanguageManager.getString("conversion.title")),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Apply theme to main panel
        themeManager.applyThemeTo(this);
        setBackground(themeManager.getCurrentBackgroundColor());
        
        // Panel untuk input
        JPanel inputPanel = new JPanel(new GridBagLayout());
        themeManager.applyThemeTo(inputPanel);
        inputPanel.setBackground(themeManager.getCurrentBackgroundColor());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Label untuk jumlah
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        amountLabel = new JLabel(LanguageManager.getString("conversion.amount") + ":");
        amountLabel.setForeground(themeManager.getCurrentForegroundColor());
        inputPanel.add(amountLabel, gbc);
        
        // Field untuk jumlah
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        amountField = new JTextField("10000000");
        amountField.setBackground(themeManager.getCurrentFieldColor());
        amountField.setForeground(themeManager.getCurrentForegroundColor());
        amountField.setCaretColor(themeManager.getCurrentForegroundColor());
        amountField.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        inputPanel.add(amountField, gbc);
        
        // Tambahkan document listener untuk reset hasil saat input berubah
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearResult();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                clearResult();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                clearResult();
            }
        });
        
        // Label dari
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        fromLabel = new JLabel(LanguageManager.getString("conversion.from") + ":");
        fromLabel.setForeground(themeManager.getCurrentForegroundColor());
        inputPanel.add(fromLabel, gbc);
        
        // Combo box dari dengan custom renderer untuk dark mode
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fromCurrencyCombo = new JComboBox<>(new String[]{"IDR", "USD", "EUR", "JPY", "GBP", "AUD", "CAD", "SGD", "CNY", "MYR", "THB", "PHP", "KRW", "INR", "RUB", "BRL", "ZAR", "CHF", "SEK", "MXN", "NZD", "ARS", "EGP", "NGN"});
        setupComboBoxTheme(fromCurrencyCombo);
        fromCurrencyCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    clearResult();
                }
            }
        });
        inputPanel.add(fromCurrencyCombo, gbc);
        
        // Tombol swap
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        swapButton = new JButton("⇆");
        swapButton.setToolTipText(LanguageManager.getString("conversion.swap"));
        swapButton.setBackground(themeManager.getCurrentBackgroundColor());
        swapButton.setForeground(themeManager.getCurrentForegroundColor());
        swapButton.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        swapButton.addActionListener(this);
        inputPanel.add(swapButton, gbc);
        
        // Label ke
        gbc.gridx = 0;
        gbc.gridy = 2;
        toLabel = new JLabel(LanguageManager.getString("conversion.to") + ":");
        toLabel.setForeground(themeManager.getCurrentForegroundColor());
        inputPanel.add(toLabel, gbc);
        
        // Combo box ke dengan custom renderer untuk dark mode
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        toCurrencyCombo = new JComboBox<>(new String[]{"IDR", "USD", "EUR", "JPY", "GBP", "AUD", "CAD", "SGD", "CNY", "MYR", "THB", "PHP", "KRW", "INR", "RUB", "BRL", "ZAR", "CHF", "SEK", "MXN", "NZD", "ARS", "EGP", "NGN"});
        toCurrencyCombo.setSelectedItem("NGN"); // Default ke NGN
        setupComboBoxTheme(toCurrencyCombo);
        toCurrencyCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    clearResult();
                }
            }
        });
        inputPanel.add(toCurrencyCombo, gbc);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themeManager.applyThemeTo(buttonPanel);
        buttonPanel.setBackground(themeManager.getCurrentBackgroundColor());
        
        convertButton = new JButton(LanguageManager.getString("conversion.button"));
        convertButton.setBackground(themeManager.getCurrentBackgroundColor());
        convertButton.setForeground(themeManager.getCurrentForegroundColor());
        convertButton.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        convertButton.addActionListener(this);
        buttonPanel.add(convertButton);
        
        // Panel hasil
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        themeManager.applyThemeTo(resultPanel);
        resultPanel.setBackground(themeManager.getCurrentBackgroundColor());
        
        resultLabel = new JLabel(LanguageManager.getString("conversion.result"));
        resultLabel.setFont(new Font(resultLabel.getFont().getName(), Font.BOLD, 16));
        resultLabel.setForeground(themeManager.getCurrentForegroundColor());
        resultPanel.add(resultLabel);
        
        // Tambahkan semua panel ke panel utama
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Setup theme untuk ComboBox dengan renderer custom yang lebih baik untuk dark mode
     */
    private void setupComboBoxTheme(JComboBox<String> comboBox) {
        comboBox.setBackground(themeManager.getCurrentFieldColor());
        comboBox.setForeground(themeManager.getCurrentForegroundColor());
        comboBox.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        
        // Custom renderer untuk dropdown items
        comboBox.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (isSelected) {
                    setBackground(themeManager.isDarkMode() ? 
                        new Color(60, 80, 120) : new Color(184, 207, 229));
                    setForeground(themeManager.getCurrentForegroundColor());
                } else {
                    setBackground(themeManager.getCurrentFieldColor());
                    setForeground(themeManager.getCurrentForegroundColor());
                }
                
                // Add padding untuk better spacing
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                return this;
            }
        });
        
        // Customize popup appearance
        Object popup = comboBox.getUI().getAccessibleChild(comboBox, 0);
        if (popup instanceof JPopupMenu) {
            JPopupMenu popupMenu = (JPopupMenu) popup;
            popupMenu.setBackground(themeManager.getCurrentFieldColor());
            popupMenu.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == convertButton) {
            if (!isProcessing) {
                isProcessing = true;
                performConversion();
                
                // Reset flag setelah beberapa saat
                Timer timer = new Timer(500, event -> isProcessing = false);
                timer.setRepeats(false);
                timer.start();
            }
        } else if (e.getSource() == swapButton) {
            swapCurrencies();
            clearResult();
        }
    }
    
    private void clearResult() {
        resultLabel.setText(LanguageManager.getString("conversion.result"));
    }
    
    private void performConversion() {
        try {
            // Ambil nilai input
            double amount = Double.parseDouble(amountField.getText().replace(",", ""));
            String fromCode = (String) fromCurrencyCombo.getSelectedItem();
            String toCode = (String) toCurrencyCombo.getSelectedItem();
            
            // Debug log
            System.out.println("\n=== CONVERSION PROCESS ===");
            System.out.println("Converting " + amount + " " + fromCode + " to " + toCode);
            
            // Ambil objek mata uang
            Currency fromCurrency = converter.getCurrency(fromCode);
            Currency toCurrency = converter.getCurrency(toCode);
            
            System.out.println("Source currency: " + fromCurrency.getCode() + " with rate " + fromCurrency.getRate());
            System.out.println("Target currency: " + toCurrency.getCode() + " with rate " + toCurrency.getRate());
            
            // Lakukan konversi
            double result = converter.convert(fromCurrency, toCurrency, amount);
            
            System.out.println("Conversion result: " + result);
            
            // Format hasil
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String formattedAmount = df.format(amount);
            String formattedResult = df.format(result);
            
            // Tampilkan hasil
            resultLabel.setText(fromCurrency.getSymbol() + " " + formattedAmount + " = " + 
                                toCurrency.getSymbol() + " " + formattedResult);
            
            // Simpan data konversi ke repository
            Map<String, Object> conversionData = new HashMap<>();
            conversionData.put("timestamp", System.currentTimeMillis());
            conversionData.put("sourceCurrency", fromCurrency.getCode());
            conversionData.put("targetCurrency", toCurrency.getCode());
            conversionData.put("amount", amount);
            conversionData.put("result", result);
            
            // Log untuk debugging
            System.out.println("Saving conversion data: " + conversionData);
            
            // Simpan data
            repository.save("conversion_history", conversionData);
            
            // Refresh panel history untuk menampilkan entri baru
            if (historyPanel != null) {
                historyPanel.refreshHistory();
            }
            
            System.out.println("=== CONVERSION COMPLETE ===\n");
            
        } catch (Exception ex) {
            System.err.println("ERROR in conversion: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                LanguageManager.getString("dialog.error"),
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void swapCurrencies() {
        Object fromSelection = fromCurrencyCombo.getSelectedItem();
        Object toSelection = toCurrencyCombo.getSelectedItem();
        
        fromCurrencyCombo.setSelectedItem(toSelection);
        toCurrencyCombo.setSelectedItem(fromSelection);
    }
    
    public void refreshRates() {
        // Refresh UI setelah rate diupdate
        if (!amountField.getText().trim().isEmpty()) {
            performConversion();
        }
    }
    
    public void refreshLanguage() {
        // Update labels
        amountLabel.setText(LanguageManager.getString("conversion.amount") + ":");
        fromLabel.setText(LanguageManager.getString("conversion.from") + ":");
        toLabel.setText(LanguageManager.getString("conversion.to") + ":");
        convertButton.setText(LanguageManager.getString("conversion.button"));
        setBorder(BorderFactory.createTitledBorder(LanguageManager.getString("conversion.title")));
        swapButton.setToolTipText(LanguageManager.getString("conversion.swap"));
        
        // Check if result is the default text
        if (resultLabel.getText().equals(LanguageManager.getString("conversion.result")) || 
            resultLabel.getText().contains("Hasil konversi")) {
            resultLabel.setText(LanguageManager.getString("conversion.result"));
        }
    }
    
    public void refreshTheme() {
        // Apply theme to main panel
        themeManager.applyThemeTo(this);
        setBackground(themeManager.getCurrentBackgroundColor());
        
        // Apply theme to all sub-components
        if (amountLabel != null) amountLabel.setForeground(themeManager.getCurrentForegroundColor());
        if (fromLabel != null) fromLabel.setForeground(themeManager.getCurrentForegroundColor());
        if (toLabel != null) toLabel.setForeground(themeManager.getCurrentForegroundColor());
        if (resultLabel != null) resultLabel.setForeground(themeManager.getCurrentForegroundColor());
        
        if (amountField != null) {
            amountField.setBackground(themeManager.getCurrentFieldColor());
            amountField.setForeground(themeManager.getCurrentForegroundColor());
            amountField.setCaretColor(themeManager.getCurrentForegroundColor());
            amountField.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        }
        
        if (fromCurrencyCombo != null) {
            setupComboBoxTheme(fromCurrencyCombo);
        }
        
        if (toCurrencyCombo != null) {
            setupComboBoxTheme(toCurrencyCombo);
        }
        
        if (swapButton != null) {
            swapButton.setBackground(themeManager.getCurrentBackgroundColor());
            swapButton.setForeground(themeManager.getCurrentForegroundColor());
            swapButton.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        }
        
        if (convertButton != null) {
            convertButton.setBackground(themeManager.getCurrentBackgroundColor());
            convertButton.setForeground(themeManager.getCurrentForegroundColor());
            convertButton.setBorder(BorderFactory.createLineBorder(themeManager.getCurrentBorderColor()));
        }

        // Refresh parent panels
        Component parent = getParent();
        while (parent != null) {
            if (parent instanceof JPanel) {
                ((JPanel) parent).setBackground(themeManager.getCurrentBackgroundColor());
            }
            parent = parent.getParent();
        }
        
        repaint();
    }
    
    private void displayLastConversion() {
        try {
            List<Map<String, Object>> historyData = repository.load("conversion_history");
            if (!historyData.isEmpty()) {
                // Ambil konversi terakhir
                Map<String, Object> lastConversion = historyData.get(historyData.size() - 1);
                
                // Ambil nilai dari konversi terakhir
                String fromCurrency = (String) lastConversion.get("sourceCurrency");
                String toCurrency = (String) lastConversion.get("targetCurrency");
                
                // Ambil dan format jumlah
                Object amountObj = lastConversion.get("amount");
                double amount = 0.0;
                if (amountObj instanceof Number) {
                    amount = ((Number) amountObj).doubleValue();
                }
                
                // Ambil dan format hasil
                Object resultObj = lastConversion.get("result");
                double result = 0.0;
                if (resultObj instanceof Number) {
                    result = ((Number) resultObj).doubleValue();
                }
                
                // Format nilai untuk tampilan
                DecimalFormat df = new DecimalFormat("#,##0.00");
                String formattedAmount = df.format(amount);
                String formattedResult = df.format(result);
                
                // Set nilai di UI
                amountField.setText(String.valueOf((int)amount));
                
                // Cari index untuk mata uang
                setComboBoxSelectedItem(fromCurrencyCombo, fromCurrency);
                setComboBoxSelectedItem(toCurrencyCombo, toCurrency);
                
                // Tampilkan hasil
                Currency from = converter.getCurrency(fromCurrency);
                Currency to = converter.getCurrency(toCurrency);
                if (from != null && to != null) {
                    resultLabel.setText(from.getSymbol() + " " + formattedAmount + " = " + 
                                      to.getSymbol() + " " + formattedResult);
                }
            }
        } catch (Exception e) {
            System.err.println("Error displaying last conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to safely set selected item in combo box
     */
    private void setComboBoxSelectedItem(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        // If not found, leave default selection
    }
}