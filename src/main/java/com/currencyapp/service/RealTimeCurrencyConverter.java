package com.currencyapp.service;

import com.currencyapp.model.Currency;
import com.currencyapp.model.GenericCurrency;
import com.currencyapp.repository.DataRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementasi real-time dari interface CurrencyConverter
 */
public class RealTimeCurrencyConverter implements CurrencyConverter {
    
    private final Map<String, Currency> currencies = new HashMap<>();
    private final ExchangeRateService rateService;
    private final DataRepository repository;
    private final Timer updateTimer;
    
    public RealTimeCurrencyConverter(ExchangeRateService rateService, DataRepository repository) {
        this.rateService = rateService;
        this.repository = repository;
        
        // Inisialisasi currencies
        initializeCurrencies();
        
        // Update rate secara periodik
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Updating exchange rates...");
                    updateExchangeRates();
                } catch (Exception e) {
                    System.err.println("Scheduled update failed: " + e.getMessage());
                }
            }
        }, 5000, 60 * 60 * 1000); // Tunggu 5 detik sebelum update pertama, lalu update setiap jam
    }
    
    private void initializeCurrencies() {
        System.out.println("Initializing currencies...");
        
        // Get all supported currencies
        String[] currencyCodes = GenericCurrency.getAllCurrencyCodes();
        System.out.println("Found " + currencyCodes.length + " supported currencies");
        
        // Default rates
        Map<String, Double> defaultRates = new HashMap<>();
        defaultRates.put("USD", 1.0);       // Base currency
        defaultRates.put("IDR", 15645.0);   // 1 USD = 15645 IDR
        defaultRates.put("EUR", 0.92);      // 1 USD = 0.92 EUR
        defaultRates.put("JPY", 149.0);     // 1 USD = 149 JPY
        defaultRates.put("GBP", 0.78);      // 1 USD = 0.78 GBP
        defaultRates.put("AUD", 1.51);      // 1 USD = 1.51 AUD
        defaultRates.put("CAD", 1.37);      // 1 USD = 1.37 CAD
        defaultRates.put("SGD", 1.34);      // 1 USD = 1.34 SGD
        defaultRates.put("CNY", 7.24);      // 1 USD = 7.24 CNY
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
        
        // Create currency objects for all supported currencies
        for (String code : currencyCodes) {
            double rate = defaultRates.getOrDefault(code, 1.0);
            currencies.put(code, new GenericCurrency(code, rate));
        }
        
        System.out.println("Successfully initialized " + currencies.size() + " currencies");
        
        // Debug: print first 5 currencies
        int count = 0;
        for (Map.Entry<String, Currency> entry : currencies.entrySet()) {
            if (count < 5) {
                System.out.println("Currency: " + entry.getKey() + " => " + entry.getValue().getName() + 
                                  " with rate " + entry.getValue().getRate());
                count++;
            } else {
                break;
            }
        }
        
        // Update with latest rates
        try {
            updateExchangeRates();
        } catch (Exception e) {
            System.err.println("Failed to update rates: " + e.getMessage());
            // Continue with default rates
        }
    }
    
    @Override
    public double convert(Currency source, Currency target, double amount) {
        if (source == null) {
            throw new IllegalArgumentException("Source currency cannot be null");
        }
        
        if (target == null) {
            throw new IllegalArgumentException("Target currency cannot be null");
        }
        
        try {
            System.out.println("Converting " + amount + " " + source.getCode() + " to " + target.getCode());
            System.out.println("Source rate: " + source.getRate() + ", Target rate: " + target.getRate());
            
            double result = source.convertTo(target, amount);
            System.out.println("Conversion result: " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            throw e;
        }
    }
    
    @Override
    public void updateExchangeRates() {
        try {
            System.out.println("Updating exchange rates...");
            Map<String, Double> latestRates = rateService.getLatestExchangeRates();
            
            // Update rate untuk setiap mata uang
            for (Map.Entry<String, Double> entry : latestRates.entrySet()) {
                Currency currency = currencies.get(entry.getKey());
                if (currency != null) {
                    currency.setRate(entry.getValue());
                    System.out.println("Updated " + entry.getKey() + " rate to " + entry.getValue());
                }
            }
            
            System.out.println("Exchange rates updated successfully");
        } catch (Exception e) {
            System.err.println("Failed to update exchange rates: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public Currency getCurrency(String code) {
        if (code == null) {
            System.err.println("getCurrency: Currency code is null!");
            return null;
        }
        
        String upperCode = code.toUpperCase();
        Currency currency = currencies.get(upperCode);
        
        if (currency == null) {
            System.err.println("Currency not found: " + upperCode);
            System.err.println("Available currencies: " + currencies.keySet());
            
            // Try to create it if supported
            if (GenericCurrency.isSupported(upperCode)) {
                System.out.println("Creating currency on-the-fly: " + upperCode);
                currency = new GenericCurrency(upperCode, 1.0);
                currencies.put(upperCode, currency);
            }
        }
        
        return currency;
    }
    
    public void shutdown() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }
}