package com.currencyapp.model;

import java.util.Date;

/**
 * Model untuk menyimpan record konversi
 */
public class ConversionRecord {
    private Date timestamp;
    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double result;
    
    public ConversionRecord() {
        // Default constructor untuk GSON
    }
    
    public ConversionRecord(Date timestamp, String fromCurrency, String toCurrency, 
                           double amount, double result) {
        this.timestamp = timestamp;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.result = result;
    }
    
    // Getters and setters
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getFromCurrency() {
        return fromCurrency;
    }
    
    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }
    
    public String getToCurrency() {
        return toCurrency;
    }
    
    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getResult() {
        return result;
    }
    
    public void setResult(double result) {
        this.result = result;
    }
}