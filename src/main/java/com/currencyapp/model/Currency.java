package com.currencyapp.model;

/**
 * Class abstrak yang mewakili mata uang
 * Ini adalah contoh penerapan Abstraksi
 */
public abstract class Currency {
    private String code;
    private String name;
    private String symbol;
    private double rate; // rate terhadap USD sebagai base currency
    
    public Currency(String code, String name, String symbol, double rate) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
        this.rate = rate;
    }
    
    // Abstract method yang harus diimplementasikan oleh subclass
    public abstract String format(double amount);
    
    // Method untuk konversi ke mata uang lain
    public double convertTo(Currency target, double amount) {
        // Debug log
        System.out.println("Converting " + amount + " " + this.code + " to " + target.getCode());
        System.out.println("Source rate: " + this.rate + ", Target rate: " + target.getRate());
        
        double result;
        
        if (this.code.equals("USD")) {
            // USD ke mata uang lain: langsung kalikan dengan rate target
            result = amount * target.getRate();
            System.out.println("USD to other: " + amount + " * " + target.getRate() + " = " + result);
        } 
        else if (target.getCode().equals("USD")) {
            // Mata uang lain ke USD: bagi dengan rate sumber
            result = amount / this.rate;
            System.out.println("Other to USD: " + amount + " / " + this.rate + " = " + result);
        } 
        else {
            // Konversi antar mata uang non-USD:
            // 1. Konversi ke USD dulu
            double amountInUSD = amount / this.rate;
            System.out.println("Step 1 - To USD: " + amount + " / " + this.rate + " = " + amountInUSD);
            
            // 2. Konversi dari USD ke target
            result = amountInUSD * target.getRate();
            System.out.println("Step 2 - From USD: " + amountInUSD + " * " + target.getRate() + " = " + result);
        }
        
        System.out.println("Final result: " + result);
        return result;
    }
    
    // Getters and setters
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public double getRate() {
        return rate;
    }
    
    public void setRate(double rate) {
        this.rate = rate;
    }
    
    @Override
    public String toString() {
        return code + " - " + name;
    }
}