package com.example.datn_be.repository.NhanVien;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResetTokenStore {
    private static Map<String, ResetToken> store = new ConcurrentHashMap<>();

    public static void put(String token, ResetToken tokenData) {
        store.put(token, tokenData);
    }

    public static ResetToken get(String token) {
        return store.get(token);
    }

    public static void remove(String token) {
        store.remove(token);
    }
}
