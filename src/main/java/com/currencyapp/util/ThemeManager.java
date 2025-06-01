package com.currencyapp.util;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.ColorUIResource;
import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

/**
 * Manager untuk menangani tema aplikasi (light/dark mode)
 */
public class ThemeManager {
    
    private static ThemeManager instance;
    
    private boolean darkMode;
    
    // Theme colors - Dark Mode yang benar-benar gelap
    private static final Color LIGHT_BACKGROUND = new Color(248, 248, 248);
    private static final Color LIGHT_FOREGROUND = new Color(33, 33, 33);
    private static final Color LIGHT_PANEL = new Color(255, 255, 255);
    
    private static final Color DARK_BACKGROUND = new Color(32, 32, 32);
    private static final Color DARK_FOREGROUND = new Color(220, 220, 220);
    private static final Color DARK_PANEL = new Color(40, 40, 40);
    private static final Color DARK_BORDER = new Color(70, 70, 70);
    private static final Color DARK_BUTTON = new Color(50, 50, 50);
    private static final Color DARK_FIELD = new Color(35, 35, 35);
    private static final Color DARK_SELECTION = new Color(60, 80, 120);
    
    private ThemeManager() {
        // Load saved theme preference or default to light mode
        darkMode = ConfigUtil.getBooleanProperty("app.darkMode", false);
    }
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void toggleDarkMode() {
        darkMode = !darkMode;
        ConfigUtil.setProperty("app.darkMode", String.valueOf(darkMode));
        applyTheme();
    }
    
    public boolean isDarkMode() {
        return darkMode;
    }
    
    public void applyTheme() {
        try {
            if (darkMode) {
                applyDarkTheme();
            } else {
                applyLightTheme();
            }
            
            // Update all open windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            System.err.println("Failed to apply theme: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void applyLightTheme() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        
        // Basic colors
        UIManager.put("control", new ColorUIResource(LIGHT_BACKGROUND));
        UIManager.put("text", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("nimbusBase", new ColorUIResource(220, 220, 220));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(235, 235, 235));
        UIManager.put("nimbusBorder", new ColorUIResource(190, 190, 190));
        UIManager.put("nimbusSelection", new ColorUIResource(185, 205, 255));
        
        // Component-specific colors
        UIManager.put("Panel.background", new ColorUIResource(LIGHT_PANEL));
        UIManager.put("Table.background", new ColorUIResource(255, 255, 255));
        UIManager.put("Table.alternateRowColor", new ColorUIResource(245, 245, 245));
        UIManager.put("Table.foreground", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("Table.selectionBackground", new ColorUIResource(185, 205, 255));
        UIManager.put("TextField.background", new ColorUIResource(255, 255, 255));
        UIManager.put("TextField.foreground", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("ComboBox.background", new ColorUIResource(255, 255, 255));
        UIManager.put("ComboBox.foreground", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("Button.background", new ColorUIResource(240, 240, 240));
        UIManager.put("Button.foreground", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("Label.foreground", new ColorUIResource(LIGHT_FOREGROUND));
        UIManager.put("TitledBorder.titleColor", new ColorUIResource(LIGHT_FOREGROUND));
    }
    
    private void applyDarkTheme() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        
        // Basic colors for dark theme - BENAR-BENAR GELAP
        UIManager.put("control", new ColorUIResource(DARK_BACKGROUND));
        UIManager.put("text", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("nimbusBase", new ColorUIResource(25, 25, 25));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(DARK_PANEL));
        UIManager.put("nimbusBorder", new ColorUIResource(DARK_BORDER));
        UIManager.put("nimbusSelection", new ColorUIResource(DARK_SELECTION));
        
        // Component-specific colors for PURE dark theme
        UIManager.put("Panel.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("Table.background", new ColorUIResource(DARK_FIELD));
        UIManager.put("Table.alternateRowColor", new ColorUIResource(DARK_BACKGROUND));
        UIManager.put("Table.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("Table.selectionBackground", new ColorUIResource(DARK_SELECTION));
        UIManager.put("Table.selectionForeground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("TableHeader.background", new ColorUIResource(DARK_BUTTON));
        UIManager.put("TableHeader.foreground", new ColorUIResource(DARK_FOREGROUND));
        
        // TextField - HARUS GELAP
        UIManager.put("TextField.background", new ColorUIResource(DARK_FIELD));
        UIManager.put("TextField.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(DARK_FIELD));
        UIManager.put("TextField.inactiveForeground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("TextField.selectionBackground", new ColorUIResource(DARK_SELECTION));
        UIManager.put("TextField.selectionForeground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("TextField.caretForeground", new ColorUIResource(DARK_FOREGROUND));
        
        // ComboBox - HARUS GELAP
        UIManager.put("ComboBox.background", new ColorUIResource(DARK_FIELD));
        UIManager.put("ComboBox.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(DARK_SELECTION));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("ComboBox.buttonBackground", new ColorUIResource(DARK_BUTTON));
        UIManager.put("ComboBox.buttonShadow", new ColorUIResource(DARK_BORDER));
        UIManager.put("ComboBox.buttonDarkShadow", new ColorUIResource(DARK_BORDER));
        UIManager.put("ComboBox.buttonHighlight", new ColorUIResource(DARK_BUTTON));
        
        // Button colors
        UIManager.put("Button.background", new ColorUIResource(DARK_BUTTON));
        UIManager.put("Button.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("Button.shadow", new ColorUIResource(DARK_BORDER));
        UIManager.put("Button.darkShadow", new ColorUIResource(DARK_BORDER));
        UIManager.put("Button.highlight", new ColorUIResource(DARK_BUTTON));
        
        // Label and other text
        UIManager.put("Label.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("TitledBorder.titleColor", new ColorUIResource(DARK_FOREGROUND));
        
        // ScrollPane
        UIManager.put("ScrollPane.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("Viewport.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("ScrollBar.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("ScrollBar.thumb", new ColorUIResource(DARK_BUTTON));
        UIManager.put("ScrollBar.track", new ColorUIResource(DARK_BACKGROUND));
        
        // Menu colors
        UIManager.put("MenuBar.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("Menu.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("Menu.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("MenuItem.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("MenuItem.foreground", new ColorUIResource(DARK_FOREGROUND));
        UIManager.put("MenuItem.selectionBackground", new ColorUIResource(DARK_SELECTION));
        UIManager.put("MenuItem.selectionForeground", new ColorUIResource(DARK_FOREGROUND));
        
        // Dialog colors
        UIManager.put("OptionPane.background", new ColorUIResource(DARK_PANEL));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(DARK_FOREGROUND));
        
        // Border colors
        UIManager.put("TitledBorder.border", new ColorUIResource(DARK_BORDER));
        UIManager.put("Border.color", new ColorUIResource(DARK_BORDER));
    }
    
    public void applyThemeTo(Component component) {
        if (component == null) return;
        
        if (darkMode) {
            component.setBackground(DARK_PANEL);
            component.setForeground(DARK_FOREGROUND);
        } else {
            component.setBackground(LIGHT_PANEL);
            component.setForeground(LIGHT_FOREGROUND);
        }
    }
    
    public Color getCurrentBackgroundColor() {
        return darkMode ? DARK_PANEL : LIGHT_PANEL;
    }
    
    public Color getCurrentForegroundColor() {
        return darkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND;
    }
    
    public Color getCurrentBorderColor() {
        return darkMode ? DARK_BORDER : new Color(190, 190, 190);
    }
    
    public Color getCurrentFieldColor() {
        return darkMode ? DARK_FIELD : new Color(255, 255, 255);
    }
}