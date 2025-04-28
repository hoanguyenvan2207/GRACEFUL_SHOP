package com.example.datn_be.service.auth;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    private final Map<String, OTPInfo> otpMap = new ConcurrentHashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;

    private static class OTPInfo {
        String otp;
        long timestamp;

        OTPInfo(String otp) {
            this.otp = otp;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public String generateOTP(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpMap.put(email, new OTPInfo(otp));
        return otp;
    }

    public boolean validateOTP(String email, String otp) {
        if (email == null || otp == null) {
            return false;
        }
        OTPInfo otpInfo = otpMap.get(email);
        if (otpInfo == null) {
            return false;
        }
        if (System.currentTimeMillis() - otpInfo.timestamp > OTP_VALID_DURATION) {
            otpMap.remove(email);
            return false;
        }
        boolean isValid = otpInfo.otp.equals(otp);
        if (isValid) {
            otpMap.remove(email);
        }
        return isValid;
    }
}
