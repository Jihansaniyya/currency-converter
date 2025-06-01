package com.currencyapp.service;

import com.currencyapp.model.Currency;

/**
 * Interface untuk konversi mata uang
 * Ini adalah contoh penerapan Interface
 */
public interface CurrencyConverter {
    
    /**
     * Melakukan konversi dari satu mata uang ke mata uang lain
     * 
     * @param source Mata uang sumber
     * @param target Mata uang tujuan
     * @param amount Jumlah yang akan dikonversi
     * @return Hasil konversi
     */
    double convert(Currency source, Currency target, double amount);
    
    /**
     * Mendapatkan kurs terbaru untuk semua mata uang
     */
    void updateExchangeRates();
    
    /**
     * Mendapatkan objek mata uang berdasarkan kode
     * 
     * @param code Kode mata uang (IDR, USD, EUR)
     * @return Objek mata uang
     */
    Currency getCurrency(String code);
}