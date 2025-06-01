package com.currencyapp.gui.components;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Custom text field untuk input angka yang diformat
 */
public class FormattedTextField extends JFormattedTextField {
    
    public FormattedTextField() {
        super();
        setupFormatter();
        setFont(new Font(getFont().getName(), Font.PLAIN, 14));
        setColumns(10);
        setValue(1.0); // Default value
    }
    
    private void setupFormatter() {
        // Formatter untuk angka dengan format lokal
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        if (format instanceof DecimalFormat) {
            ((DecimalFormat) format).setMinimumFractionDigits(2);
            ((DecimalFormat) format).setMaximumFractionDigits(2);
        }
        
        // Formatter untuk menampilkan angka
        NumberFormatter displayFormatter = new NumberFormatter(format);
        displayFormatter.setValueClass(Double.class);
        
        // Formatter untuk editing
        NumberFormatter editFormatter = new NumberFormatter(format) {
            @Override
            public Object stringToValue(String text) throws ParseException {
                if (text.isEmpty()) {
                    return null;
                }
                return super.stringToValue(text);
            }
        };
        editFormatter.setValueClass(Double.class);
        editFormatter.setMinimum(0.0); // Tidak boleh negatif
        
        // Set formatter factory
        setFormatterFactory(new DefaultFormatterFactory(
            displayFormatter, 
            displayFormatter, 
            editFormatter
        ));
    }
}