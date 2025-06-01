package com.currencyapp.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Manager untuk menangani lokalisasi dan terjemahan
 */
public class LanguageManager {
    
    private static LanguageManager instance;
    private ResourceBundle messages;
    private Locale currentLocale;
    
    // Map untuk menyimpan nama bahasa
    private static final Map<String, String> SUPPORTED_LANGUAGES = new HashMap<>();
    
    static {
        SUPPORTED_LANGUAGES.put("id", "Bahasa Indonesia");
        SUPPORTED_LANGUAGES.put("en", "English");
    }
    
    private LanguageManager() {
        // Load saved language preference or default to Indonesian
        String langCode = ConfigUtil.getProperty("app.language", "id");
        currentLocale = Locale.forLanguageTag(langCode);
        loadMessages();
    }
    
    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }
    
    // Tambahkan metode static untuk getString
    public static String getString(String key) {
        return getInstance().getStringInternal(key);
    }

    // Metode internal untuk mengambil string dari resource bundle
    private String getStringInternal(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            System.err.println("Missing translation for key: " + key);
            return key;
        }
    }
    
    private void loadMessages() {
        try {
            messages = ResourceBundle.getBundle("messages", currentLocale);
            System.out.println("Loaded language: " + currentLocale + " successfully");
        } catch (Exception e) {
            System.err.println("Error loading messages: " + e.getMessage());
            
            // Fallback to hardcoded messages
            try {
                Properties props = new Properties();
                
                // Determine language for fallback
                String lang = currentLocale.getLanguage();
                
                if ("id".equals(lang)) {
                    // Indonesian fallback
                    props.setProperty("app.title", "Aplikasi Konversi Mata Uang");
                    props.setProperty("app.version", "Versi");
                    props.setProperty("app.author", "Dibuat oleh");
                    props.setProperty("conversion.title", "Konversi Mata Uang");
                    props.setProperty("conversion.amount", "Jumlah");
                    props.setProperty("conversion.from", "Dari");
                    props.setProperty("conversion.to", "Ke");
                    props.setProperty("conversion.button", "Konversi");
                    props.setProperty("conversion.result", "Hasil konversi akan ditampilkan di sini");
                    props.setProperty("conversion.swap", "Tukar mata uang");
                    props.setProperty("history.title", "Riwayat Konversi");
                    props.setProperty("history.refresh", "Perbarui");
                    props.setProperty("history.clear", "Hapus Riwayat");
                    props.setProperty("history.empty", "Belum ada riwayat konversi");
                    props.setProperty("history.column.time", "Waktu");
                    props.setProperty("history.column.from", "Dari");
                    props.setProperty("history.column.amount", "Jumlah");
                    props.setProperty("history.column.to", "Ke");
                    props.setProperty("history.column.result", "Hasil");
                    props.setProperty("menu.file", "File");
                    props.setProperty("menu.language", "Bahasa");
                    props.setProperty("menu.theme", "Tema");
                    props.setProperty("menu.help", "Bantuan");
                    props.setProperty("menu.about", "Tentang");
                    props.setProperty("menu.exit", "Keluar");
                    props.setProperty("menu.refresh", "Perbarui Kurs");
                    props.setProperty("menu.clear", "Hapus Riwayat");
                    props.setProperty("menu.changeApi", "Ubah API Key");
                    props.setProperty("menu.theme.light", "Tema Terang");
                    props.setProperty("menu.theme.dark", "Tema Gelap");
                    props.setProperty("menu.ratesInfo", "Info Kurs");
                    props.setProperty("dialog.error", "Error");
                    props.setProperty("dialog.success", "Sukses");
                    props.setProperty("dialog.info", "Informasi");
                    props.setProperty("dialog.confirm", "Konfirmasi");
                    props.setProperty("dialog.refresh.success", "Kurs mata uang berhasil diperbarui");
                    props.setProperty("dialog.refresh.error", "Gagal memperbarui kurs mata uang");
                    props.setProperty("dialog.clear.confirm", "Apakah Anda yakin ingin menghapus semua riwayat konversi?");
                    props.setProperty("dialog.clear.success", "Riwayat konversi berhasil dihapus");
                    props.setProperty("dialog.apikey.title", "Masukkan API Key baru");
                    props.setProperty("dialog.apikey.success", "API Key berhasil diubah");
                    props.setProperty("status.ready", "Siap");
                    props.setProperty("status.updated", "Terakhir diperbarui");
                    props.setProperty("status.disclaimer", "Nilai tukar hanya untuk referensi");
                    props.setProperty("dialog.language.changed", "Bahasa berhasil diubah");
                    props.setProperty("dialog.theme.changed", "Tema berhasil diubah");
                    props.setProperty("about.title", "Tentang Aplikasi");
                    props.setProperty("about.description", "Aplikasi konversi mata uang yang memungkinkan Anda mengonversi nilai antar berbagai mata uang dengan mudah. Aplikasi ini menyediakan kurs terkini dan menyimpan riwayat konversi untuk referensi Anda.");
                    props.setProperty("about.features", "Fitur Aplikasi:");
                    props.setProperty("about.features.list", "• Konversi mata uang real-time untuk 24+ mata uang\n• Riwayat konversi otomatis\n• Dukungan tema terang dan gelap\n• Interface multibahasa (Indonesia & Inggris)\n• Kurs mata uang terupdate secara berkala");
                    props.setProperty("about.disclaimer", "Catatan: Kurs mata uang bersifat indikatif dan mungkin berbeda dari sumber lain. Untuk transaksi sebenarnya, gunakan kurs resmi dari institusi keuangan.");
                    props.setProperty("ratesInfo.title", "Informasi Kurs");
                    props.setProperty("ratesInfo.source", "Kurs yang ditampilkan diperbarui dari ExchangeRate-API");
                    props.setProperty("ratesInfo.lastUpdate", "Terakhir diperbarui");
                    props.setProperty("ratesInfo.indicative", "Kurs bersifat indikatif dan dapat berbeda dari bank atau penyedia jasa keuangan");
                    props.setProperty("ratesInfo.official", "Untuk transaksi sebenarnya, harap gunakan kurs resmi dari institusi keuangan");
                    props.setProperty("ratesInfo.differences.title", "Kurs antar sumber dapat berbeda karena:");
                    props.setProperty("ratesInfo.differences.time", "Waktu pembaruan yang berbeda");
                    props.setProperty("ratesInfo.differences.source", "Sumber data yang berbeda");
                    props.setProperty("ratesInfo.differences.fees", "Biaya dan spread yang berbeda");
                    props.setProperty("ratesInfo.differences.rounding", "Perbedaan metode pembulatan");
                } else {
                    // English fallback
                    props.setProperty("app.title", "Currency Converter");
                    props.setProperty("app.version", "Version");
                    props.setProperty("app.author", "Created by");
                    props.setProperty("conversion.title", "Currency Conversion");
                    props.setProperty("conversion.amount", "Amount");
                    props.setProperty("conversion.from", "From");
                    props.setProperty("conversion.to", "To");
                    props.setProperty("conversion.button", "Convert");
                    props.setProperty("conversion.result", "Conversion result will be displayed here");
                    props.setProperty("conversion.swap", "Swap currencies");
                    props.setProperty("history.title", "Conversion History");
                    props.setProperty("history.refresh", "Refresh");
                    props.setProperty("history.clear", "Clear History");
                    props.setProperty("history.empty", "No conversion history yet");
                    props.setProperty("history.column.time", "Time");
                    props.setProperty("history.column.from", "From");
                    props.setProperty("history.column.amount", "Amount");
                    props.setProperty("history.column.to", "To");
                    props.setProperty("history.column.result", "Result");
                    props.setProperty("menu.file", "File");
                    props.setProperty("menu.language", "Language");
                    props.setProperty("menu.theme", "Theme");
                    props.setProperty("menu.help", "Help");
                    props.setProperty("menu.about", "About");
                    props.setProperty("menu.exit", "Exit");
                    props.setProperty("menu.refresh", "Refresh Rates");
                    props.setProperty("menu.clear", "Clear History");
                    props.setProperty("menu.changeApi", "Change API Key");
                    props.setProperty("menu.theme.light", "Light Theme");
                    props.setProperty("menu.theme.dark", "Dark Theme");
                    props.setProperty("menu.ratesInfo", "Rates Info");
                    props.setProperty("dialog.error", "Error");
                    props.setProperty("dialog.success", "Success");
                    props.setProperty("dialog.info", "Information");
                    props.setProperty("dialog.confirm", "Confirm");
                    props.setProperty("dialog.refresh.success", "Exchange rates successfully updated");
                    props.setProperty("dialog.refresh.error", "Failed to update exchange rates");
                    props.setProperty("dialog.clear.confirm", "Are you sure you want to delete all conversion history?");
                    props.setProperty("dialog.clear.success", "Conversion history successfully cleared");
                    props.setProperty("dialog.apikey.title", "Enter new API Key");
                    props.setProperty("dialog.apikey.success", "API Key successfully changed");
                    props.setProperty("status.ready", "Ready");
                    props.setProperty("status.updated", "Last updated");
                    props.setProperty("status.disclaimer", "Exchange rates are for reference only");
                    props.setProperty("dialog.language.changed", "Language successfully changed");
                    props.setProperty("dialog.theme.changed", "Theme successfully changed");
                    props.setProperty("about.title", "About Application");
                    props.setProperty("about.description", "A currency conversion application that allows you to easily convert values between various currencies. This application provides current exchange rates and saves conversion history for your reference.");
                    props.setProperty("about.features", "Application Features:");
                    props.setProperty("about.features.list", "• Real-time currency conversion for 24+ currencies\n• Automatic conversion history\n• Light and dark theme support\n• Multilingual interface (Indonesian & English)\n• Periodically updated exchange rates");
                    props.setProperty("about.disclaimer", "Note: Exchange rates are indicative and may differ from other sources. For actual transactions, please use official rates from financial institutions.");
                    props.setProperty("ratesInfo.title", "Rates Information");
                    props.setProperty("ratesInfo.source", "Exchange rates displayed are updated from ExchangeRate-API");
                    props.setProperty("ratesInfo.lastUpdate", "Last updated");
                    props.setProperty("ratesInfo.indicative", "Exchange rates are indicative and may differ from banks or financial service providers");
                    props.setProperty("ratesInfo.official", "For actual transactions, please use official rates from financial institutions");
                    props.setProperty("ratesInfo.differences.title", "Exchange rates between sources may differ due to:");
                    props.setProperty("ratesInfo.differences.time", "Different update times");
                    props.setProperty("ratesInfo.differences.source", "Different data sources");
                    props.setProperty("ratesInfo.differences.fees", "Different fees and spreads");
                    props.setProperty("ratesInfo.differences.rounding", "Different rounding methods");
                }
                
                // Konversi Properties ke InputStream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                props.store(outputStream, null);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                
                // Buat ResourceBundle dari InputStream
                messages = new PropertyResourceBundle(inputStream);
                System.out.println("Created fallback resource bundle for language: " + lang);
            } catch (IOException ioe) {
                System.err.println("Fatal error creating fallback messages: " + ioe.getMessage());
                throw new RuntimeException("Cannot load or create language resources", ioe);
            }
        }
    }
    
    public void setLanguage(String langCode) {
        if (SUPPORTED_LANGUAGES.containsKey(langCode)) {
            currentLocale = Locale.forLanguageTag(langCode);
            loadMessages();
            ConfigUtil.setProperty("app.language", langCode);
            System.out.println("Language changed to: " + langCode);
        }
    }
    
    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    public String getCurrentLanguage() {
        return SUPPORTED_LANGUAGES.get(currentLocale.getLanguage());
    }
    
    public Map<String, String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}