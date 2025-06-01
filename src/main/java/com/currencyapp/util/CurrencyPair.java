package com.currencyapp.util;

/**
 * Class generic untuk menyimpan pasangan mata uang
 * Contoh penerapan Generics
 */
public class CurrencyPair<F, T> {
    private final F from;
    private final T to;
    
    public CurrencyPair(F from, T to) {
        this.from = from;
        this.to = to;
    }
    
    public F getFrom() {
        return from;
    }
    
    public T getTo() {
        return to;
    }
    
    @Override
    public String toString() {
        return from + " -> " + to;
    }
}