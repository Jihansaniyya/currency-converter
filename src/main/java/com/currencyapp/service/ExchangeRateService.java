package com.currencyapp.service;

import java.util.Map;

/**
 * Interface untuk layanan exchange rate
 */
public interface ExchangeRateService {
    
    /**
     * Mendapatkan kurs terbaru untuk semua mata uang yang didukung
     * 
     * @return Map berisi kode mata uang dan rate-nya terhadap USD
     */
    Map<String, Double> getLatestExchangeRates() throws Exception;
    
    /**
     * Mendapatkan kurs historis untuk mata uang tertentu
     * 
     * @param date Tanggal dalam format YYYY-MM-DD
     * @return Map berisi kode mata uang dan rate-nya terhadap USD
     */
    Map<String, Double> getHistoricalRates(String date) throws Exception;
}