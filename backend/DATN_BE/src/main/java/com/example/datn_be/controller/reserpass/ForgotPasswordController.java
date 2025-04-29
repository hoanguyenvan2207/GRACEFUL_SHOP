package com.example.datn_be.controller.reserpass;

import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import com.example.datn_be.repository.NhanVien.ResetToken;
import com.example.datn_be.repository.NhanVien.ResetTokenStore;
import com.example.datn_be.service.nhanvien.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ForgotPasswordController {
    @Autowired
    private ForgotPasswordService forgotPasswordService;
    @Autowired
    private NhanVienRepo nhanVienRepo;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được để trống.");
        }
        if (!nhanVienRepo.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Không tìm thấy người dùng với email: " + email);
        }
        String token = UUID.randomUUID().toString();
        ResetTokenStore.put(token, new ResetToken(email, System.currentTimeMillis() + 15 * 60 * 1000));
        String resetUrl = "https://graceful56.shop/reset-password?token=" + token;
        forgotPasswordService.sendForgotPasswordEmail(email, resetUrl);

        return ResponseEntity.ok("Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Token và mật khẩu mới không được để trống.");
        }
        try {
            forgotPasswordService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
