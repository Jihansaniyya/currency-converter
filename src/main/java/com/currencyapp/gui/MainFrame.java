package com.currencyapp.gui;

import com.currencyapp.repository.DataRepository;
import com.currencyapp.repository.FileRepository;
import com.currencyapp.service.ApiClient;
import com.currencyapp.service.ExchangeRateService;
import com.currencyapp.service.MockExchangeRateService;
import com.currencyapp.service.CurrencyConverter;
import com.currencyapp.service.RealTimeCurrencyConverter;
import com.currencyapp.util.ConfigUtil;
import com.currencyapp.util.LanguageManager;
import com.currencyapp.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Frame utama untuk aplikasi konversi mata uang dengan dialog text yang lebih visible
 */
public class MainFrame extends JFrame {
    
    private final CurrencyConverter converter;
    private final DataRepository repository;
    private final LanguageManager languageManager;
    private final ThemeManager themeManager;
    
    private ConversionPanel conversionPanel;
    private HistoryPanel historyPanel;
    private JLabel statusLabel;
    private JLabel disclaimerLabel;
    private JLabel timestampLabel;
    private long lastUpdateTime;
    
    public MainFrame() {
        // Inisialisasi manager
        languageManager = LanguageManager.getInstance();
        themeManager = ThemeManager.getInstance();
        
        // Aplikasikan tema
        themeManager.applyTheme();
        
        // Inisialisasi repository & service dengan error handling
        try {
            // Pastikan direktori data ada
            String dataPath = ConfigUtil.getProperty("data.path", "src/main/resources/data");
            File dataDir = new File(dataPath);
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                System.out.println("Created data directory: " + created);
            }
            
            // Inisialisasi repository
            repository = new FileRepository(dataPath);
            
            // Coba gunakan ApiClient untuk data real-time
            ExchangeRateService rateService;
            try {
                // Cek property use.mock.api
                boolean useMockApi = ConfigUtil.getBooleanProperty("use.mock.api", true); // Default true
                
                if (useMockApi) {
                    System.out.println("Using mock exchange rates (configured in config.properties)");
                    rateService = new MockExchangeRateService();
                } else {
                    System.out.println("Attempting to use real-time exchange rates from API");
                    rateService = new ApiClient();
                    System.out.println("Successfully connected to exchange rate API");
                }
            } catch (Exception e) {
                System.err.println("Failed to initialize API client: " + e.getMessage());
                System.out.println("Falling back to mock exchange rates");
                rateService = new MockExchangeRateService();
            }
            
            // Inisialisasi converter
            converter = new RealTimeCurrencyConverter(rateService, repository);
            
            // Catat waktu update terakhir
            lastUpdateTime = System.currentTimeMillis();
            
            setupUI();
            
            // Tambahkan window listener untuk clean up resources
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (converter instanceof RealTimeCurrencyConverter) {
                        ((RealTimeCurrencyConverter) converter).shutdown();
                    }
                    System.exit(0);
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error initializing application: " + e.getMessage(),
                LanguageManager.getString("dialog.error"), 
                JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e);
        }
    }
    
    private void setupUI() {
        setTitle(LanguageManager.getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(null); // Tampilkan di tengah layar
        
        // Layout utama
        setLayout(new BorderLayout());
        
        try {
            // Buat HistoryPanel terlebih dahulu
            historyPanel = new HistoryPanel(repository, languageManager);
            
            // Buat ConversionPanel dengan memberikan repository dan historyPanel
            conversionPanel = new ConversionPanel(converter, repository, historyPanel, languageManager);
            
            // Tambahkan panel ke frame
            add(conversionPanel, BorderLayout.NORTH);
            add(new JScrollPane(historyPanel), BorderLayout.CENTER);
            
            // Panel tombol
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            themeManager.applyThemeTo(buttonPanel);
            
            JButton refreshButton = new JButton(LanguageManager.getString("history.refresh"));
            refreshButton.addActionListener(e -> {
                try {
                    converter.updateExchangeRates();
                    lastUpdateTime = System.currentTimeMillis();
                    updateTimestampLabel();
                    conversionPanel.refreshRates();
                    JOptionPane.showMessageDialog(this, 
                        LanguageManager.getString("dialog.refresh.success"), 
                        LanguageManager.getString("dialog.success"), 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        LanguageManager.getString("dialog.refresh.error") + ": " + ex.getMessage(),
                        LanguageManager.getString("dialog.error"),
                        JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(refreshButton);
            
            JButton clearButton = new JButton(LanguageManager.getString("history.clear"));
            clearButton.addActionListener(e -> {
                int choice = JOptionPane.showConfirmDialog(this,
                    LanguageManager.getString("dialog.clear.confirm"),
                    LanguageManager.getString("dialog.confirm"),
                    JOptionPane.YES_NO_OPTION);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    repository.clear("conversion_history");
                    historyPanel.refreshHistory();
                }
            });
            buttonPanel.add(clearButton);
            
            // Menu bar
            setupMenuBar();
            
            // Status bar
            JPanel statusBar = createStatusBar();
            add(statusBar, BorderLayout.SOUTH);
            
            // Apply theme to main frame
            themeManager.applyThemeTo(this);
            
        } catch (Exception e) {
            System.err.println("Error setting up UI: " + e.getMessage());
            e.printStackTrace();
            
            // Tampilkan error di UI
            add(new JLabel("Error: " + e.getMessage(), JLabel.CENTER), BorderLayout.CENTER);
        }
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        themeManager.applyThemeTo(menuBar);
        
        // Menu File
        JMenu fileMenu = new JMenu(LanguageManager.getString("menu.file"));
        themeManager.applyThemeTo(fileMenu);
        
        // Tambahkan menu item untuk refresh rates
        JMenuItem refreshItem = new JMenuItem(LanguageManager.getString("menu.refresh"));
        refreshItem.addActionListener(e -> {
            try {
                converter.updateExchangeRates();
                lastUpdateTime = System.currentTimeMillis();
                updateTimestampLabel();
                conversionPanel.refreshRates();
                JOptionPane.showMessageDialog(this, 
                    LanguageManager.getString("dialog.refresh.success"), 
                    LanguageManager.getString("dialog.success"), 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.refresh.error") + ": " + ex.getMessage(),
                    LanguageManager.getString("dialog.error"),
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        fileMenu.add(refreshItem);
        
        // Tambahkan menu item untuk clear history
        JMenuItem clearHistoryItem = new JMenuItem(LanguageManager.getString("menu.clear"));
        clearHistoryItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                LanguageManager.getString("dialog.clear.confirm"),
                LanguageManager.getString("dialog.confirm"),
                JOptionPane.YES_NO_OPTION);
                
            if (choice == JOptionPane.YES_OPTION) {
                repository.clear("conversion_history");
                historyPanel.refreshHistory();
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.clear.success"),
                    LanguageManager.getString("dialog.success"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fileMenu.add(clearHistoryItem);
        
        fileMenu.addSeparator();
        
        // Tambahkan menu item untuk change API key
        JMenuItem changeApiKeyItem = new JMenuItem(LanguageManager.getString("menu.changeApi"));
        changeApiKeyItem.addActionListener(e -> {
            String currentKey = ConfigUtil.getProperty("api.key", "");
            String newKey = JOptionPane.showInputDialog(this, 
                LanguageManager.getString("dialog.apikey.title"), 
                currentKey);
            
            if (newKey != null && !newKey.trim().isEmpty()) {
                ConfigUtil.setProperty("api.key", newKey);
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.apikey.success"),
                    LanguageManager.getString("dialog.success"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        fileMenu.add(changeApiKeyItem);
        
        fileMenu.addSeparator();
        
        // Tambahkan menu item untuk exit
        JMenuItem exitItem = new JMenuItem(LanguageManager.getString("menu.exit"));
        exitItem.addActionListener(e -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(exitItem);
        
        // Menu Language
        JMenu languageMenu = new JMenu(LanguageManager.getString("menu.language"));
        themeManager.applyThemeTo(languageMenu);
        
        // Add language options
        for (Map.Entry<String, String> entry : languageManager.getSupportedLanguages().entrySet()) {
            String langCode = entry.getKey();
            String langName = entry.getValue();
            
            JMenuItem langItem = new JMenuItem(langName);
            langItem.addActionListener(e -> {
                languageManager.setLanguage(langCode);
                refreshUILanguage();
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.language.changed"),
                    LanguageManager.getString("dialog.info"),
                    JOptionPane.INFORMATION_MESSAGE);
            });
            languageMenu.add(langItem);
        }
        
        // Menu Theme
        JMenu themeMenu = new JMenu(LanguageManager.getString("menu.theme"));
        themeManager.applyThemeTo(themeMenu);
        
        JMenuItem lightThemeItem = new JMenuItem(LanguageManager.getString("menu.theme.light"));
        lightThemeItem.addActionListener(e -> {
            if (themeManager.isDarkMode()) {
                themeManager.toggleDarkMode();
                refreshUITheme();
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.theme.changed"),
                    LanguageManager.getString("dialog.info"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        themeMenu.add(lightThemeItem);
        
        JMenuItem darkThemeItem = new JMenuItem(LanguageManager.getString("menu.theme.dark"));
        darkThemeItem.addActionListener(e -> {
            if (!themeManager.isDarkMode()) {
                themeManager.toggleDarkMode();
                refreshUITheme();
                JOptionPane.showMessageDialog(this,
                    LanguageManager.getString("dialog.theme.changed"),
                    LanguageManager.getString("dialog.info"),
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        themeMenu.add(darkThemeItem);
        
        // Menu Help
        JMenu helpMenu = new JMenu(LanguageManager.getString("menu.help"));
        themeManager.applyThemeTo(helpMenu);
        
        // Tambahkan menu item untuk about
        JMenuItem aboutItem = new JMenuItem(LanguageManager.getString("menu.about"));
        aboutItem.addActionListener(e -> {
            showAboutDialog();
        });
        helpMenu.add(aboutItem);
        
        // Tambahkan menu item untuk rates info
        JMenuItem ratesInfoItem = new JMenuItem(LanguageManager.getString("menu.ratesInfo"));
        ratesInfoItem.addActionListener(e -> {
            showRatesInfoDialog();
        });
        helpMenu.add(ratesInfoItem);
        
        menuBar.add(fileMenu);
        menuBar.add(languageMenu);
        menuBar.add(themeMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel();
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setLayout(new BorderLayout());
        themeManager.applyThemeTo(statusBar);
        
        // Panel kiri - status
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel(LanguageManager.getString("status.ready"));
        leftPanel.add(statusLabel);
        themeManager.applyThemeTo(leftPanel);
        
        // Panel tengah - timestamp
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        timestampLabel = new JLabel(LanguageManager.getString("status.updated") + ": " + formatTimestamp(lastUpdateTime));
        centerPanel.add(timestampLabel);
        themeManager.applyThemeTo(centerPanel);
        
        // Panel kanan - disclaimer
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        disclaimerLabel = new JLabel(LanguageManager.getString("status.disclaimer"));
        disclaimerLabel.setFont(new Font(disclaimerLabel.getFont().getName(), Font.ITALIC, 10));
        disclaimerLabel.setForeground(themeManager.isDarkMode() ? new Color(160, 160, 160) : Color.GRAY);
        rightPanel.add(disclaimerLabel);
        themeManager.applyThemeTo(rightPanel);
        
        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(centerPanel, BorderLayout.CENTER);
        statusBar.add(rightPanel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, LanguageManager.getString("about.title"), true);
        aboutDialog.setLayout(new BorderLayout());
        aboutDialog.setResizable(false);
        
        // Apply theme to dialog itself
        themeManager.applyThemeTo(aboutDialog);
        
        // Main content panel with proper styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        themeManager.applyThemeTo(mainPanel);
        
        // Header panel with icon and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, themeManager.getCurrentBorderColor()),
            BorderFactory.createEmptyBorder(25, 25, 20, 25)
        ));
        themeManager.applyThemeTo(headerPanel);
        
        // App icon (placeholder)
        JLabel iconLabel = new JLabel("💰", JLabel.CENTER);
        iconLabel.setFont(new Font(iconLabel.getFont().getName(), Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setForeground(themeManager.getCurrentForegroundColor());
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        
        // Title
        JLabel titleLabel = new JLabel(LanguageManager.getString("app.title"));
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(themeManager.getCurrentForegroundColor());
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        
        // Version and Author with separator line
        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new BoxLayout(versionPanel, BoxLayout.Y_AXIS));
        versionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, themeManager.getCurrentBorderColor()),
            BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));
        themeManager.applyThemeTo(versionPanel);
        
        JLabel versionLabel = new JLabel(LanguageManager.getString("app.version") + ": " + 
                                        ConfigUtil.getProperty("app.version", "1.0"));
        versionLabel.setFont(new Font(versionLabel.getFont().getName(), Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setForeground(themeManager.getCurrentForegroundColor());
        versionPanel.add(versionLabel);
        
        JLabel authorLabel = new JLabel(LanguageManager.getString("app.author") + ": " + 
                                        ConfigUtil.getProperty("app.author", "Moneyest"));
        authorLabel.setFont(new Font(authorLabel.getFont().getName(), Font.PLAIN, 14));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authorLabel.setForeground(themeManager.getCurrentForegroundColor());
        versionPanel.add(authorLabel);
        
        headerPanel.add(versionPanel);
        
        // Content panel with clear sections
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        themeManager.applyThemeTo(contentPanel);
        
        // Description section with better text visibility
        JPanel descriptionSection = createVisibleTextSection("Description");
        
        JTextArea descriptionArea = createVisibleTextArea();
        descriptionArea.setText(LanguageManager.getString("about.description"));
        descriptionSection.add(descriptionArea, BorderLayout.CENTER);
        
        contentPanel.add(descriptionSection);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Separator line
        JSeparator separator1 = new JSeparator();
        separator1.setForeground(themeManager.getCurrentBorderColor());
        separator1.setBackground(themeManager.getCurrentBorderColor());
        contentPanel.add(separator1);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Features section with better text visibility
        JPanel featuresSection = createVisibleTextSection(LanguageManager.getString("about.features"));
        
        JTextArea featuresArea = createVisibleTextArea();
        featuresArea.setText(LanguageManager.getString("about.features.list"));
        featuresSection.add(featuresArea, BorderLayout.CENTER);
        
        contentPanel.add(featuresSection);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Another separator line
        JSeparator separator2 = new JSeparator();
        separator2.setForeground(themeManager.getCurrentBorderColor());
        separator2.setBackground(themeManager.getCurrentBorderColor());
        contentPanel.add(separator2);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Disclaimer section with better text visibility
        JPanel disclaimerSection = createVisibleTextSection("Disclaimer");
        
        JTextArea disclaimerArea = createVisibleTextArea();
        disclaimerArea.setText(LanguageManager.getString("about.disclaimer"));
        disclaimerArea.setFont(new Font(disclaimerArea.getFont().getName(), Font.ITALIC, 11));
        disclaimerSection.add(disclaimerArea, BorderLayout.CENTER);
        
        contentPanel.add(disclaimerSection);
        
        // Button panel with separator
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, themeManager.getCurrentBorderColor()),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        themeManager.applyThemeTo(buttonPanel);
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> aboutDialog.dispose());
        okButton.setBackground(themeManager.isDarkMode() ? new Color(70, 130, 180) : new Color(70, 130, 180));
        okButton.setForeground(Color.WHITE);
        okButton.setOpaque(true);
        buttonPanel.add(okButton);
        
        // Assemble dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        aboutDialog.add(mainPanel);
        aboutDialog.setSize(550, 650);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }
    
    private void showRatesInfoDialog() {
        JDialog ratesDialog = new JDialog(this, LanguageManager.getString("ratesInfo.title"), true);
        ratesDialog.setLayout(new BorderLayout());
        ratesDialog.setResizable(false);
        
        // Apply theme to dialog itself
        themeManager.applyThemeTo(ratesDialog);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        themeManager.applyThemeTo(mainPanel);
        
        // Header panel with separator
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, themeManager.getCurrentBorderColor()),
            BorderFactory.createEmptyBorder(25, 25, 20, 25)
        ));
        themeManager.applyThemeTo(headerPanel);
        
        // Info icon
        JLabel iconLabel = new JLabel("ℹ️", JLabel.CENTER);
        iconLabel.setFont(new Font(iconLabel.getFont().getName(), Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setForeground(themeManager.getCurrentForegroundColor());
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        
        // Title
        JLabel titleLabel = new JLabel(LanguageManager.getString("ratesInfo.title"));
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(themeManager.getCurrentForegroundColor());
        headerPanel.add(titleLabel);
        
        // Content panel with clear sections and better text visibility
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        themeManager.applyThemeTo(contentPanel);
        
        // Main Info Section with better visibility
        JPanel mainInfoSection = createVisibleTextSection("Exchange Rate Information");
        
        // Info points with better styling and visibility
        String[] infoPoints = {
            "• " + LanguageManager.getString("ratesInfo.source"),
            "• " + LanguageManager.getString("ratesInfo.lastUpdate") + ": " + formatTimestamp(lastUpdateTime),
            "• " + LanguageManager.getString("ratesInfo.indicative"),
            "• " + LanguageManager.getString("ratesInfo.official")
        };
        
        JPanel infoPointsPanel = new JPanel();
        infoPointsPanel.setLayout(new BoxLayout(infoPointsPanel, BoxLayout.Y_AXIS));
        infoPointsPanel.setBackground(themeManager.getCurrentBackgroundColor());
        
        for (int i = 0; i < infoPoints.length; i++) {
            JLabel pointLabel = new JLabel("<html><p style='margin: 8px 0; color: " + 
                (themeManager.isDarkMode() ? "#DCDCDC" : "#212121") + ";'>" + infoPoints[i] + "</p></html>");
            pointLabel.setFont(new Font(pointLabel.getFont().getName(), Font.PLAIN, 13));
            pointLabel.setForeground(themeManager.getCurrentForegroundColor());
            pointLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            pointLabel.setOpaque(true);
            pointLabel.setBackground(themeManager.getCurrentBackgroundColor());
            infoPointsPanel.add(pointLabel);
            
            // Add separator between points except for the last one
            if (i < infoPoints.length - 1) {
                JSeparator pointSeparator = new JSeparator();
                pointSeparator.setPreferredSize(new Dimension(300, 1));
                pointSeparator.setForeground(themeManager.getCurrentBorderColor());
                pointSeparator.setBackground(themeManager.getCurrentBorderColor());
                infoPointsPanel.add(pointSeparator);
            }
        }
        
        mainInfoSection.add(infoPointsPanel, BorderLayout.CENTER);
        
        contentPanel.add(mainInfoSection);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Major separator
        JSeparator majorSeparator = new JSeparator();
        majorSeparator.setPreferredSize(new Dimension(400, 2));
        majorSeparator.setForeground(themeManager.getCurrentBorderColor());
        majorSeparator.setBackground(themeManager.getCurrentBorderColor());
        contentPanel.add(majorSeparator);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Differences section with better visibility
        JPanel differencesSection = createVisibleTextSection(LanguageManager.getString("ratesInfo.differences.title"));
        
        String[] differences = {
            "- " + LanguageManager.getString("ratesInfo.differences.time"),
            "- " + LanguageManager.getString("ratesInfo.differences.source"),
            "- " + LanguageManager.getString("ratesInfo.differences.fees"),
            "- " + LanguageManager.getString("ratesInfo.differences.rounding")
        };
        
        JPanel differencesPointsPanel = new JPanel();
        differencesPointsPanel.setLayout(new BoxLayout(differencesPointsPanel, BoxLayout.Y_AXIS));
        differencesPointsPanel.setBackground(themeManager.getCurrentBackgroundColor());
        
        for (int i = 0; i < differences.length; i++) {
            JLabel diffLabel = new JLabel("<html><p style='margin: 6px 0; margin-left: 20px; color: " + 
                (themeManager.isDarkMode() ? "#DCDCDC" : "#212121") + ";'>" + differences[i] + "</p></html>");
            diffLabel.setFont(new Font(diffLabel.getFont().getName(), Font.PLAIN, 12));
            diffLabel.setForeground(themeManager.getCurrentForegroundColor());
            diffLabel.setBorder(BorderFactory.createEmptyBorder(3, 15, 3, 10));
            diffLabel.setOpaque(true);
            diffLabel.setBackground(themeManager.getCurrentBackgroundColor());
            differencesPointsPanel.add(diffLabel);
            
            // Add separator between differences except for the last one
            if (i < differences.length - 1) {
                JSeparator diffSeparator = new JSeparator();
                diffSeparator.setPreferredSize(new Dimension(250, 1));
                diffSeparator.setForeground(themeManager.getCurrentBorderColor());
                diffSeparator.setBackground(themeManager.getCurrentBorderColor());
                differencesPointsPanel.add(diffSeparator);
            }
        }
        
        differencesSection.add(differencesPointsPanel, BorderLayout.CENTER);
        
        contentPanel.add(differencesSection);
        
        // Button panel with separator
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, themeManager.getCurrentBorderColor()),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        themeManager.applyThemeTo(buttonPanel);
        
        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(100, 35));
        okButton.addActionListener(e -> ratesDialog.dispose());
        okButton.setBackground(themeManager.isDarkMode() ? new Color(70, 130, 180) : new Color(70, 130, 180));
        okButton.setForeground(Color.WHITE);
        okButton.setOpaque(true);
        buttonPanel.add(okButton);
        
        // Assemble dialog
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        ratesDialog.add(mainPanel);
        ratesDialog.setSize(520, 600);
        ratesDialog.setLocationRelativeTo(this);
        ratesDialog.setVisible(true);
    }
    
    /**
     * Helper method to create a text section with better visibility for both light and dark modes
     */
    private JPanel createVisibleTextSection(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(themeManager.getCurrentBorderColor(), 1),
                title
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        themeManager.applyThemeTo(section);
        section.setBackground(themeManager.getCurrentBackgroundColor());
        
        // Force border title color for better visibility
        if (section.getBorder() instanceof javax.swing.border.TitledBorder) {
            ((javax.swing.border.TitledBorder) section.getBorder()).setTitleColor(themeManager.getCurrentForegroundColor());
        }
        
        return section;
    }
    
    /**
     * Helper method to create a text area with better visibility for both light and dark modes
     */
    private JTextArea createVisibleTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(true);
        
        // Enhanced contrast colors for better visibility
        if (themeManager.isDarkMode()) {
            textArea.setBackground(new Color(48, 48, 48)); // Slightly lighter background for contrast
            textArea.setForeground(new Color(240, 240, 240)); // Very light foreground
        } else {
            textArea.setBackground(new Color(252, 252, 252)); // Very light background
            textArea.setForeground(new Color(25, 25, 25)); // Very dark foreground
        }
        
        textArea.setFont(new Font(textArea.getFont().getName(), Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        return textArea;
    }
    
    private void updateTimestampLabel() {
        if (timestampLabel != null) {
            timestampLabel.setText(LanguageManager.getString("status.updated") + ": " + formatTimestamp(lastUpdateTime));
        }
    }
    
    private String formatTimestamp(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(timestamp));
    }
    
    public void refreshUILanguage() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Update title
                setTitle(LanguageManager.getString("app.title"));
                
                // Update menu
                setupMenuBar();
                
                // Update panels
                if (conversionPanel != null) {
                    conversionPanel.refreshLanguage();
                }
                if (historyPanel != null) {
                    historyPanel.refreshLanguage();
                }
                
                // Update status bar
                if (statusLabel != null) {
                    statusLabel.setText(LanguageManager.getString("status.ready"));
                }
                updateTimestampLabel();
                if (disclaimerLabel != null) {
                    disclaimerLabel.setText(LanguageManager.getString("status.disclaimer"));
                }
                
                // Refresh the entire UI
                SwingUtilities.updateComponentTreeUI(this);
                repaint();
                
                System.out.println("UI language refreshed successfully");
            } catch (Exception e) {
                System.err.println("Error refreshing UI language: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public void refreshUITheme() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply theme to main frame and all components
                themeManager.applyThemeTo(this);
                setBackground(themeManager.getCurrentBackgroundColor());
                getContentPane().setBackground(themeManager.getCurrentBackgroundColor());
                
                // Refresh theme for sub-components
                if (conversionPanel != null) {
                    conversionPanel.refreshTheme();
                }
                if (historyPanel != null) {
                    historyPanel.refreshTheme();
                }
                
                // Update menu bar
                if (getJMenuBar() != null) {
                    themeManager.applyThemeTo(getJMenuBar());
                    getJMenuBar().setBackground(themeManager.getCurrentBackgroundColor());
                    for (int i = 0; i < getJMenuBar().getMenuCount(); i++) {
                        JMenu menu = getJMenuBar().getMenu(i);
                        themeManager.applyThemeTo(menu);
                        for (int j = 0; j < menu.getItemCount(); j++) {
                            if (menu.getItem(j) != null) {
                                themeManager.applyThemeTo(menu.getItem(j));
                            }
                        }
                    }
                }
                
                // Update status bar completely
                Component[] components = getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JPanel) {
                        themeManager.applyThemeTo(comp);
                        ((JPanel) comp).setBackground(themeManager.getCurrentBackgroundColor());
                        
                        // Update nested components
                        updateComponentsRecursively(comp);
                    }
                }
                
                // Update status bar components
                if (statusLabel != null) {
                    statusLabel.setForeground(themeManager.getCurrentForegroundColor());
                }
                if (timestampLabel != null) {
                    timestampLabel.setForeground(themeManager.getCurrentForegroundColor());
                }
                if (disclaimerLabel != null) {
                    disclaimerLabel.setForeground(themeManager.isDarkMode() ? new Color(160, 160, 160) : Color.GRAY);
                }
                
                // Update UI completely
                SwingUtilities.updateComponentTreeUI(this);
                repaint();
                
                System.out.println("UI theme refreshed successfully to " + (themeManager.isDarkMode() ? "dark" : "light") + " mode");
            } catch (Exception e) {
                System.err.println("Error refreshing UI theme: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void updateComponentsRecursively(Component component) {
        if (component instanceof Container) {
            Container container = (Container) component;
            container.setBackground(themeManager.getCurrentBackgroundColor());
            
            for (Component child : container.getComponents()) {
                if (child instanceof JPanel) {
                    child.setBackground(themeManager.getCurrentBackgroundColor());
                    child.setForeground(themeManager.getCurrentForegroundColor());
                    updateComponentsRecursively(child);
                } else if (child instanceof JLabel) {
                    child.setForeground(themeManager.getCurrentForegroundColor());
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Starting Currency Converter application...");
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and feel set successfully");
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // Run application
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                System.out.println("Application started successfully");
            } catch (Exception e) {
                System.err.println("Failed to start application: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}