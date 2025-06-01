package com.currencyapp.model;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementasi generic untuk mata uang yang dapat mewakili semua jenis mata uang
 * tanpa perlu membuat class khusus untuk setiap mata uang.
 */
public class GenericCurrency extends Currency {
    private static final Map<String, Locale> CURRENCY_LOCALES = new TreeMap<>();
    private static final Map<String, String> CURRENCY_NAMES = new TreeMap<>();
    private static final Map<String, String> CURRENCY_SYMBOLS = new TreeMap<>();
    
    // Inisialisasi data mata uang
    static {
        // BASE CURRENCIES - Include these first for better organization
        CURRENCY_LOCALES.put("USD", Locale.US);
        CURRENCY_NAMES.put("USD", "US Dollar");
        CURRENCY_SYMBOLS.put("USD", "$");
        
        CURRENCY_LOCALES.put("IDR", new Locale("id", "ID"));
        CURRENCY_NAMES.put("IDR", "Indonesian Rupiah");
        CURRENCY_SYMBOLS.put("IDR", "Rp");
        
        CURRENCY_LOCALES.put("EUR", Locale.GERMANY); // Euro menggunakan locale Jerman
        CURRENCY_NAMES.put("EUR", "Euro");
        CURRENCY_SYMBOLS.put("EUR", "€");
        
        // ASIAN CURRENCIES
        CURRENCY_LOCALES.put("JPY", Locale.JAPAN);
        CURRENCY_NAMES.put("JPY", "Japanese Yen");
        CURRENCY_SYMBOLS.put("JPY", "¥");
        
        CURRENCY_LOCALES.put("CNY", Locale.CHINA);
        CURRENCY_NAMES.put("CNY", "Chinese Yuan");
        CURRENCY_SYMBOLS.put("CNY", "¥");
        
        CURRENCY_LOCALES.put("SGD", new Locale("en", "SG"));
        CURRENCY_NAMES.put("SGD", "Singapore Dollar");
        CURRENCY_SYMBOLS.put("SGD", "S$");
        
        CURRENCY_LOCALES.put("MYR", new Locale("ms", "MY"));
        CURRENCY_NAMES.put("MYR", "Malaysian Ringgit");
        CURRENCY_SYMBOLS.put("MYR", "RM");
        
        CURRENCY_LOCALES.put("THB", new Locale("th", "TH"));
        CURRENCY_NAMES.put("THB", "Thai Baht");
        CURRENCY_SYMBOLS.put("THB", "฿");
        
        CURRENCY_LOCALES.put("PHP", new Locale("fil", "PH"));
        CURRENCY_NAMES.put("PHP", "Philippine Peso");
        CURRENCY_SYMBOLS.put("PHP", "₱");
        
        CURRENCY_LOCALES.put("KRW", Locale.KOREA);
        CURRENCY_NAMES.put("KRW", "South Korean Won");
        CURRENCY_SYMBOLS.put("KRW", "₩");
        
        CURRENCY_LOCALES.put("INR", new Locale("hi", "IN"));
        CURRENCY_NAMES.put("INR", "Indian Rupee");
        CURRENCY_SYMBOLS.put("INR", "₹");
        
        // EUROPEAN CURRENCIES
        CURRENCY_LOCALES.put("GBP", Locale.UK);
        CURRENCY_NAMES.put("GBP", "British Pound Sterling");
        CURRENCY_SYMBOLS.put("GBP", "£");
        
        CURRENCY_LOCALES.put("CHF", new Locale("de", "CH"));
        CURRENCY_NAMES.put("CHF", "Swiss Franc");
        CURRENCY_SYMBOLS.put("CHF", "CHF");
        
        CURRENCY_LOCALES.put("RUB", new Locale("ru", "RU"));
        CURRENCY_NAMES.put("RUB", "Russian Ruble");
        CURRENCY_SYMBOLS.put("RUB", "₽");
        
        CURRENCY_LOCALES.put("SEK", new Locale("sv", "SE"));
        CURRENCY_NAMES.put("SEK", "Swedish Krona");
        CURRENCY_SYMBOLS.put("SEK", "kr");
        
        // NORTH AMERICAN CURRENCIES
        CURRENCY_LOCALES.put("CAD", new Locale("en", "CA"));
        CURRENCY_NAMES.put("CAD", "Canadian Dollar");
        CURRENCY_SYMBOLS.put("CAD", "C$");
        
        CURRENCY_LOCALES.put("MXN", new Locale("es", "MX"));
        CURRENCY_NAMES.put("MXN", "Mexican Peso");
        CURRENCY_SYMBOLS.put("MXN", "$");
        
        // OCEANIA CURRENCIES
        CURRENCY_LOCALES.put("AUD", new Locale("en", "AU"));
        CURRENCY_NAMES.put("AUD", "Australian Dollar");
        CURRENCY_SYMBOLS.put("AUD", "A$");
        
        CURRENCY_LOCALES.put("NZD", new Locale("en", "NZ"));
        CURRENCY_NAMES.put("NZD", "New Zealand Dollar");
        CURRENCY_SYMBOLS.put("NZD", "NZ$");
        
        // SOUTH AMERICAN CURRENCIES
        CURRENCY_LOCALES.put("BRL", new Locale("pt", "BR"));
        CURRENCY_NAMES.put("BRL", "Brazilian Real");
        CURRENCY_SYMBOLS.put("BRL", "R$");
        
        CURRENCY_LOCALES.put("ARS", new Locale("es", "AR"));
        CURRENCY_NAMES.put("ARS", "Argentine Peso");
        CURRENCY_SYMBOLS.put("ARS", "$");
        
        // AFRICAN CURRENCIES
        CURRENCY_LOCALES.put("ZAR", new Locale("en", "ZA"));
        CURRENCY_NAMES.put("ZAR", "South African Rand");
        CURRENCY_SYMBOLS.put("ZAR", "R");
        
        CURRENCY_LOCALES.put("EGP", new Locale("ar", "EG"));
        CURRENCY_NAMES.put("EGP", "Egyptian Pound");
        CURRENCY_SYMBOLS.put("EGP", "£");
        
        CURRENCY_LOCALES.put("NGN", new Locale("en", "NG"));
        CURRENCY_NAMES.put("NGN", "Nigerian Naira");
        CURRENCY_SYMBOLS.put("NGN", "₦");
    }
    
    private final Locale locale;
    
    public GenericCurrency(String code, double rate) {
        super(
            code.toUpperCase(),
            CURRENCY_NAMES.getOrDefault(code.toUpperCase(), code.toUpperCase() + " Currency"),
            CURRENCY_SYMBOLS.getOrDefault(code.toUpperCase(), code.toUpperCase()),
            rate
        );
        this.locale = CURRENCY_LOCALES.getOrDefault(code.toUpperCase(), Locale.US);
    }
    
    @Override
    public String format(double amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            return formatter.format(amount);
        } catch (Exception e) {
            // Fallback jika ada error dengan locale
            NumberFormat defaultFormatter = NumberFormat.getCurrencyInstance(Locale.US);
            return getSymbol() + " " + defaultFormatter.format(amount).substring(1);
        }
    }
    
    /**
     * Mendapatkan daftar semua kode mata uang yang didukung
     */
    public static String[] getAllCurrencyCodes() {
        Set<String> keys = CURRENCY_NAMES.keySet();
        return keys.toArray(new String[0]);
    }
    
    /**
     * Mendapatkan daftar mata uang utama
     */
    public static String[] getMainCurrencyCodes() {
        return new String[] {"USD", "IDR", "EUR", "JPY", "GBP", "AUD", "CAD"};
    }
    
    /**
     * Memeriksa apakah kode mata uang didukung
     */
    public static boolean isSupported(String code) {
        if (code == null) return false;
        return CURRENCY_NAMES.containsKey(code.toUpperCase());
    }
    
    /**
     * Mendapatkan object mata uang berdasarkan kode dan rate
     */
    public static Currency create(String code, double rate) {
        if (code == null) {
            throw new IllegalArgumentException("Currency code cannot be null");
        }
        
        String upperCode = code.toUpperCase();
        if (!isSupported(upperCode)) {
            throw new IllegalArgumentException("Currency with code " + upperCode + " is not supported");
        }
        
        return new GenericCurrency(upperCode, rate);
    }
    
    /**
     * Menambahkan mata uang baru
     */
    public static void addCurrency(String code, String name, String symbol, Locale locale) {
        if (code == null || name == null || symbol == null || locale == null) {
            throw new IllegalArgumentException("All parameters must not be null");
        }
        
        String upperCode = code.toUpperCase();
        CURRENCY_NAMES.put(upperCode, name);
        CURRENCY_SYMBOLS.put(upperCode, symbol);
        CURRENCY_LOCALES.put(upperCode, locale);
    }
}