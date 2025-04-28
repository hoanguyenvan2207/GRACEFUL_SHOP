package com.example.datn_be.controller.auth;

import com.example.datn_be.dto.auth.request.DatLaiMkRequest;
import com.example.datn_be.dto.auth.request.QuenMkRequest;
import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import com.example.datn_be.payload.MessageResponse;
import com.example.datn_be.repository.KhachHangRepo;
import com.example.datn_be.service.auth.EmailService;
import com.example.datn_be.service.auth.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/auth/customer")
@CrossOrigin(origins =  {
        "http://localhost:5173",        // cho dev mode
        "https://graceful56.shop"       // cho production
},allowCredentials = "true")
@RequiredArgsConstructor
public class DatLaiMkController {
    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<?> quenMatKhau(@RequestBody QuenMkRequest request) {
        KhachHangTk khachHang = khachHangRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));
        String otp = otpService.generateOTP(request.getEmail());
        emailService.sendOTPEmail(request.getEmail(), otp);
        return ResponseEntity.ok()
                .body(new MessageResponse("Mã OTP đã được gửi đến email của bạn"));
    }

    @PostMapping("/dat-lai-mat-khau")
    public ResponseEntity<?> datLaiMatKhau(@RequestBody DatLaiMkRequest request) {
        if (!otpService.validateOTP(request.getEmail(), request.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Mã OTP không hợp lệ hoặc đã hết hạn"));
        }

        KhachHangTk khachHang = khachHangRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        khachHang.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        khachHangRepo.save(khachHang);

        return ResponseEntity.ok()
                .body(new MessageResponse("Mật khẩu đã được cập nhật thành công"));
    }
}