package com.currencyapp.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementasi mock untuk ExchangeRateService
 */
public class MockExchangeRateService implements ExchangeRateService {
    
    @Override
    public Map<String, Double> getLatestExchangeRates() throws Exception {
        Map<String, Double> rates = new HashMap<>();
        
        // Rate default (sesuaikan dengan rate terkini)
        rates.put("USD", 1.0);       // Base currency
        rates.put("IDR", 15645.0);   // 1 USD = 15645 IDR
        rates.put("EUR", 0.92);      // 1 USD = 0.92 EUR
        rates.put("JPY", 149.0);     // 1 USD = 149 JPY
        rates.put("GBP", 0.78);      // 1 USD = 0.78 GBP
        rates.put("AUD", 1.51);      // 1 USD = 1.51 AUD
        rates.put("CAD", 1.37);      // 1 USD = 1.37 CAD
        rates.put("SGD", 1.34);      // 1 USD = 1.34 SGD
        rates.put("CNY", 7.24);      // 1 USD = 7.24 CNY
        rates.put("MYR", 4.72);      // 1 USD = 4.72 MYR
        rates.put("THB", 35.93);     // 1 USD = 35.93 THB
        rates.put("PHP", 56.80);     // 1 USD = 56.80 PHP
        rates.put("KRW", 1360.0);    // 1 USD = 1360 KRW
        rates.put("INR", 83.47);     // 1 USD = 83.47 INR
        rates.put("RUB", 92.0);      // 1 USD = 92 RUB
        rates.put("BRL", 5.08);      // 1 USD = 5.08 BRL
        rates.put("ZAR", 18.12);     // 1 USD = 18.12 ZAR
        rates.put("CHF", 0.90);      // 1 USD = 0.90 CHF
        rates.put("SEK", 10.38);     // 1 USD = 10.38 SEK
        rates.put("MXN", 17.06);     // 1 USD = 17.06 MXN
        rates.put("NZD", 1.63);      // 1 USD = 1.63 NZD
        rates.put("ARS", 360.0);     // 1 USD = 360 ARS
        rates.put("EGP", 30.90);     // 1 USD = 30.90 EGP
        rates.put("NGN", 1548.0);    // 1 USD = 1548 NGN
        
        return rates;
    }
    
    @Override
    public Map<String, Double> getHistoricalRates(String date) throws Exception {
        // Untuk mock service, kita cukup mengembalikan nilai yang sama dengan latest rates
        return getLatestExchangeRates();
    }
}