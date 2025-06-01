package com.currencyapp.model;

import java.util.Map;

/**
 * Model untuk menyimpan nilai tukar mata uang
 */
public class ExchangeRate {
    private long timestamp;
    private Map<String, Double> rates;
    
    public ExchangeRate() {
        // Default constructor
    }
    
    public ExchangeRate(long timestamp, Map<String, Double> rates) {
        this.timestamp = timestamp;
        this.rates = rates;
    }
    
    // Getters and setters
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Double> getRates() {
        return rates;
    }
    
    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}