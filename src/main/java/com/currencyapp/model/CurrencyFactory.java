package com.currencyapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class untuk membuat instansi mata uang
 */
public class CurrencyFactory {
    
    // Singleton pattern
    private static CurrencyFactory instance;
    
    private CurrencyFactory() {
        // Private constructor
    }
    
    public static CurrencyFactory getInstance() {
        if (instance == null) {
            instance = new CurrencyFactory();
        }
        return instance;
    }
    
    /**
     * Membuat objek mata uang berdasarkan kode mata uang
     */
    public Currency createCurrency(String code, double rate) {
        if (code == null) {
            throw new IllegalArgumentException("Currency code cannot be null");
        }
        
        try {
            return GenericCurrency.create(code, rate);
        } catch (Exception e) {
            System.err.println("Error creating currency: " + e.getMessage());
            // Fallback to USD if there's an error
            return GenericCurrency.create("USD", 1.0);
        }
    }
    
    /**
     * Mendapatkan kode semua mata uang yang didukung
     */
    public String[] getAllCurrencyCodes() {
        return GenericCurrency.getAllCurrencyCodes();
    }
    
    /**
     * Mendapatkan kode mata uang utama
     */
    public String[] getMainCurrencyCodes() {
        return GenericCurrency.getMainCurrencyCodes();
    }
    
    /**
     * Membuat semua objek mata uang yang didukung dengan rate default
     */
    public Currency[] createAllCurrencies() {
        Map<String, Double> defaultRates = new HashMap<>();
        // Default rates untuk mata uang utama
        defaultRates.put("USD", 1.0);       // Base currency
        defaultRates.put("IDR", 15645.0);   // 1 USD = 15645 IDR
        defaultRates.put("EUR", 0.92);      // 1 USD = 0.92 EUR
        defaultRates.put("JPY", 149.0);     // 1 USD = 149 JPY
        defaultRates.put("GBP", 0.78);      // 1 USD = 0.78 GBP
        defaultRates.put("AUD", 1.51);      // 1 USD = 1.51 AUD
        defaultRates.put("CAD", 1.37);      // 1 USD = 1.37 CAD
        defaultRates.put("SGD", 1.34);      // 1 USD = 1.34 SGD
        defaultRates.put("CNY", 7.24);      // 1 USD = 7.24 CNY
        
        // Default rates untuk mata uang lainnya
        defaultRates.put("MYR", 4.72);      // 1 USD = 4.72 MYR
        defaultRates.put("THB", 35.93);     // 1 USD = 35.93 THB
        defaultRates.put("PHP", 56.80);     // 1 USD = 56.80 PHP
        defaultRates.put("KRW", 1360.0);    // 1 USD = 1360 KRW
        defaultRates.put("INR", 83.47);     // 1 USD = 83.47 INR
        defaultRates.put("RUB", 92.0);      // 1 USD = 92 RUB
        defaultRates.put("BRL", 5.08);      // 1 USD = 5.08 BRL
        defaultRates.put("ZAR", 18.12);     // 1 USD = 18.12 ZAR
        defaultRates.put("CHF", 0.90);      // 1 USD = 0.90 CHF
        defaultRates.put("SEK", 10.38);     // 1 USD = 10.38 SEK
        defaultRates.put("MXN", 17.06);     // 1 USD = 17.06 MXN
        defaultRates.put("NZD", 1.63);      // 1 USD = 1.63 NZD
        defaultRates.put("ARS", 360.0);     // 1 USD = 360 ARS
        defaultRates.put("EGP", 30.90);     // 1 USD = 30.90 EGP
        defaultRates.put("NGN", 1548.0);    // 1 USD = 1548 NGN
        
        return createAllCurrencies(defaultRates);
    }
    
    /**
     * Membuat semua objek mata uang yang didukung dengan rate yang diberikan
     */
    public Currency[] createAllCurrencies(Map<String, Double> rates) {
        String[] codes = getAllCurrencyCodes();
        Currency[] currencies = new Currency[codes.length];
        
        for (int i = 0; i < codes.length; i++) {
            // Gunakan rate default (1.0) jika tidak ada di map
            double rate = rates.getOrDefault(codes[i], 1.0);
            
            try {
                currencies[i] = createCurrency(codes[i], rate);
            } catch (Exception e) {
                System.err.println("Error creating currency " + codes[i] + ": " + e.getMessage());
                // Use a default currency if there's an error
                currencies[i] = new GenericCurrency("USD", 1.0);
            }
        }
        
        return currencies;
    }
}