package com.currencyapp.repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementasi DataRepository yang menyimpan data dalam file JSON
 * dengan implementasi yang lebih sederhana dan kuat
 */
public class FileRepository implements DataRepository {
    
    private final String dataDir;
    private Map<String, List<Map<String, Object>>> cache = new HashMap<>();
    
    public FileRepository(String dataDir) {
        this.dataDir = dataDir;
        
        // Buat direktori jika belum ada
        File dir = new File(dataDir);
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            System.out.println("Created data directory: " + dirCreated + " at " + dir.getAbsolutePath());
        }
        
        System.out.println("FileRepository initialized with directory: " + new File(dataDir).getAbsolutePath());
    }

    @Override
    public void save(String key, Map<String, Object> data) {
        System.out.println("Saving data for key: " + key + ", data: " + data);
        
        // Load existing data
        List<Map<String, Object>> existingData = load(key);
        
        // Add new data
        existingData.add(new HashMap<>(data)); // Create a copy to avoid reference issues
        
        // Update cache
        cache.put(key, existingData);
        
        // Save to file (manually create JSON)
        saveToJson(key, existingData);
    }

    @Override
    public List<Map<String, Object>> load(String key) {
        System.out.println("Loading data for key: " + key);
        
        // Check cache first
        if (cache.containsKey(key)) {
            List<Map<String, Object>> cachedData = cache.get(key);
            System.out.println("  Returning " + cachedData.size() + " items from cache");
            return new ArrayList<>(cachedData); // Return a copy to avoid modification
        }
        
        // If not in cache, load from file
        List<Map<String, Object>> data = loadFromJson(key);
        
        // Store in cache
        cache.put(key, data);
        
        return new ArrayList<>(data); // Return a copy
    }

    @Override
    public void clear(String key) {
        System.out.println("Clearing data for key: " + key);
        
        // Clear from cache
        cache.remove(key);
        
        // Delete file
        File file = new File(dataDir, key + ".json");
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("  Deleted file: " + deleted + " at " + file.getAbsolutePath());
        }
    }
    
    /**
     * Simple manual JSON serialization - more reliable than using libraries
     */
    private void saveToJson(String key, List<Map<String, Object>> data) {
        String filePath = new File(dataDir, key + ".json").getAbsolutePath();
        System.out.println("Saving to file: " + filePath);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Start array
            writer.write("[\n");
            
            // Write each item
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> item = data.get(i);
                writer.write("  {\n");
                
                // Write each field
                int fieldCount = 0;
                for (Map.Entry<String, Object> entry : item.entrySet()) {
                    writer.write("    \"" + entry.getKey() + "\": ");
                    
                    // Handle different value types
                    Object value = entry.getValue();
                    if (value == null) {
                        writer.write("null");
                    } else if (value instanceof String) {
                        writer.write("\"" + ((String) value).replace("\"", "\\\"") + "\"");
                    } else if (value instanceof Number) {
                        writer.write(value.toString());
                    } else if (value instanceof Boolean) {
                        writer.write(value.toString());
                    } else {
                        writer.write("\"" + value.toString() + "\"");
                    }
                    
                    if (fieldCount < item.size() - 1) {
                        writer.write(",");
                    }
                    writer.write("\n");
                    fieldCount++;
                }
                
                writer.write("  }");
                if (i < data.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }
            
            // End array
            writer.write("]\n");
            
            System.out.println("  Saved " + data.size() + " items to file");
        } catch (IOException e) {
            System.err.println("ERROR saving to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Simple manual JSON parsing - more reliable than using libraries
     */
    private List<Map<String, Object>> loadFromJson(String key) {
        List<Map<String, Object>> result = new ArrayList<>();
        File file = new File(dataDir, key + ".json");
        
        if (!file.exists()) {
            System.out.println("  File does not exist: " + file.getAbsolutePath());
            return result;
        }
        
        System.out.println("Loading from file: " + file.getAbsolutePath());
        
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            
            // Super simple parser for our specific JSON format
            if (content.trim().startsWith("[") && content.trim().endsWith("]")) {
                // Split into objects
                content = content.trim().substring(1, content.length() - 1).trim();
                
                if (content.isEmpty()) {
                    return result;
                }
                
                // Find all objects
                int depth = 0;
                int startPos = 0;
                
                for (int i = 0; i < content.length(); i++) {
                    char c = content.charAt(i);
                    
                    if (c == '{') {
                        if (depth == 0) {
                            startPos = i;
                        }
                        depth++;
                    } else if (c == '}') {
                        depth--;
                        if (depth == 0) {
                            // Extract one object
                            String objectStr = content.substring(startPos, i + 1);
                            Map<String, Object> item = parseJsonObject(objectStr);
                            result.add(item);
                        }
                    }
                }
            }
            
            System.out.println("  Loaded " + result.size() + " items from file");
            
            // Debug: Show what was loaded
            for (Map<String, Object> item : result) {
                System.out.println("  Item: " + item);
            }
            
        } catch (Exception e) {
            System.err.println("ERROR loading from file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Parse a JSON object string into a Map
     */
    private Map<String, Object> parseJsonObject(String json) {
        Map<String, Object> result = new HashMap<>();
        
        // Remove braces and split by fields
        json = json.trim();
        if (json.startsWith("{")) {
            json = json.substring(1);
        }
        if (json.endsWith("}")) {
            json = json.substring(0, json.length() - 1);
        }
        
        boolean inString = false;
        StringBuilder field = new StringBuilder();
        String currentKey = null;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                inString = !inString;
            } else if (c == ':' && !inString && currentKey == null) {
                // Found key-value separator
                currentKey = field.toString().trim();
                if (currentKey.startsWith("\"") && currentKey.endsWith("\"")) {
                    currentKey = currentKey.substring(1, currentKey.length() - 1);
                }
                field = new StringBuilder();
            } else if (c == ',' && !inString && currentKey != null) {
                // End of field
                String value = field.toString().trim();
                result.put(currentKey, parseJsonValue(value));
                currentKey = null;
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        
        // Last field
        if (currentKey != null && field.length() > 0) {
            String value = field.toString().trim();
            result.put(currentKey, parseJsonValue(value));
        }
        
        return result;
    }
    
    /**
     * Parse a JSON value string into its Java equivalent
     */
    private Object parseJsonValue(String value) {
        value = value.trim();
        
        // String
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        
        // Null
        if (value.equals("null")) {
            return null;
        }
        
        // Boolean
        if (value.equals("true")) {
            return Boolean.TRUE;
        }
        if (value.equals("false")) {
            return Boolean.FALSE;
        }
        
        // Number
        try {
            // Try as double first
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            
            // Then as long
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // Default to string if cannot parse
            return value;
        }
    }
}