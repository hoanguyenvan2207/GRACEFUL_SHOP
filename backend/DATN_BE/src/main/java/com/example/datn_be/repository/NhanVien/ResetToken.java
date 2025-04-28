package com.example.datn_be.repository.NhanVien;

public class ResetToken {
    private String email;
    private long expiryTime;

    public ResetToken(String email, long expiryTime) {
        this.email = email;
        this.expiryTime = expiryTime;
    }

    public String getEmail() {
        return email;
    }

    public long getExpiryTime() {
        return expiryTime;
    }
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
