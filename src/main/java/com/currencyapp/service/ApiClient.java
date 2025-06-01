package com.currencyapp.service;

import com.currencyapp.util.ConfigUtil;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementasi ExchangeRateService yang menggunakan API ExchangeRate-API
 * https://www.exchangerate-api.com/ (contoh API, Anda bisa menggunakan yang lain)
 */
public class ApiClient implements ExchangeRateService {
    
    private final String apiKey;
    private final String baseUrl;
    
    public ApiClient() {
        // Baca API key dari file konfigurasi
        this.apiKey = ConfigUtil.getProperty("api.key");
        this.baseUrl = ConfigUtil.getProperty("api.baseUrl");
    }
    
    @Override
    public Map<String, Double> getLatestExchangeRates() throws Exception {
        String endpoint = baseUrl + "/latest?base=USD&apikey=" + apiKey;
        String jsonResponse = sendHttpRequest(endpoint);
        return parseExchangeRateResponse(jsonResponse);
    }
    
    @Override
    public Map<String, Double> getHistoricalRates(String date) throws Exception {
        String endpoint = baseUrl + "/" + date + "?base=USD&apikey=" + apiKey;
        String jsonResponse = sendHttpRequest(endpoint);
        return parseExchangeRateResponse(jsonResponse);
    }
    
    private String sendHttpRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Failed to fetch exchange rates: HTTP error code " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        return response.toString();
    }
    
    private Map<String, Double> parseExchangeRateResponse(String jsonResponse) throws Exception {
        Map<String, Double> rates = new HashMap<>();
        
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject ratesObject = jsonObject.getJSONObject("rates");
            
            // Kita hanya tertarik dengan IDR, USD, dan EUR
            rates.put("IDR", ratesObject.getDouble("IDR"));
            rates.put("USD", 1.0); // USD selalu 1.0 karena sebagai base currency
            rates.put("EUR", ratesObject.getDouble("EUR"));
            
            return rates;
            
        } catch (Exception e) {
            throw new Exception("Failed to parse exchange rate data: " + e.getMessage());
        }
    }
}