package com.currencyapp.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Utility class untuk membaca dan menulis konfigurasi dari file properties
 */
public class ConfigUtil {
    
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;
    private static final String CONFIG_FILE = "config.properties";
    
    private ConfigUtil() {
        // Private constructor untuk utility class
    }
    
    private static void loadProperties() {
        if (!isLoaded) {
            // Tambahkan default values dahulu
            properties.setProperty("app.language", "id");
            properties.setProperty("api.key", "demo");
            properties.setProperty("api.baseUrl", "https://api.exchangerate-api.com/v4");
            properties.setProperty("use.mock.api", "true");
            properties.setProperty("data.path", "src/main/resources/data");
            properties.setProperty("app.version", "1.0");
            properties.setProperty("app.author", "Moneyest");
            properties.setProperty("app.darkMode", "false");
            
            try {
                // Coba load dari classpath
                InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
                
                if (input != null) {
                    properties.load(input);
                    input.close();
                    System.out.println("Config loaded successfully from classpath");
                } else {
                    System.err.println("Failed to load config file: " + CONFIG_FILE + " not found in classpath");
                    System.out.println("Using default values");
                }
                isLoaded = true;
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        }
    }
    
    public static String getProperty(String key) {
        loadProperties();
        return properties.getProperty(key);
    }
    
    public static String getProperty(String key, String defaultValue) {
        loadProperties();
        String value = properties.getProperty(key);
        if (value == null) {
            System.out.println("Property '" + key + "' not found, using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }
    
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    public static void setProperty(String key, String value) {
        loadProperties();
        properties.setProperty(key, value);
        saveProperties();
    }
    
    public static void saveProperties() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Currency Converter Configuration");
            System.out.println("Config saved successfully to config.properties");
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }
}