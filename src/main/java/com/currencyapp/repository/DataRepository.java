package com.currencyapp.repository;

import java.util.List;
import java.util.Map;

/**
 * Interface untuk persistensi data
 */
public interface DataRepository {
    
    /**
     * Menyimpan data
     * 
     * @param key Key untuk menyimpan data
     * @param data Data yang akan disimpan
     */
    void save(String key, Map<String, Object> data);
    
    /**
     * Mengambil data berdasarkan key
     * 
     * @param key Key data yang ingin diambil
     * @return List data
     */
    List<Map<String, Object>> load(String key);
    
    /**
     * Menghapus semua data untuk key tertentu
     * 
     * @param key Key data yang ingin dihapus
     */
    void clear(String key);
}