package com.currencyapp.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Custom combo box untuk memilih mata uang
 */
public class CurrencyComboBox extends JComboBox<String> {
    
    public CurrencyComboBox(String[] currencies) {
        super(currencies);
        setRenderer(new CurrencyListRenderer());
        setFont(new Font(getFont().getName(), Font.PLAIN, 14));
    }
    
    /**
     * Custom renderer untuk menampilkan baris di combo box
     */
    private static class CurrencyListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                     int index, boolean isSelected,
                                                     boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            
            if (value != null) {
                String currencyCode = (String) value;
                String currencyName = getCurrencyName(currencyCode);
                String currencySymbol = getCurrencySymbol(currencyCode);
                
                label.setText(currencyCode + " - " + currencyName + " (" + currencySymbol + ")");
                
                try {
                    // Coba load flag icon jika ada
                    String iconPath = "/icons/" + currencyCode.toLowerCase() + ".png";
                    ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                    if (icon.getIconWidth() > 0) {
                        label.setIcon(icon);
                    }
                } catch (Exception e) {
                    // Ignore if icon not found
                }
            }
            
            return label;
        }
        
        private String getCurrencyName(String code) {
            switch (code) {
                case "IDR":
                    return "Indonesian Rupiah";
                case "USD":
                    return "US Dollar";
                case "EUR":
                    return "Euro";
                default:
                    return code;
            }
        }
        
        private String getCurrencySymbol(String code) {
            switch (code) {
                case "IDR":
                    return "Rp";
                case "USD":
                    return "$";
                case "EUR":
                    return "€";
                default:
                    return "";
            }
        }
    }
}