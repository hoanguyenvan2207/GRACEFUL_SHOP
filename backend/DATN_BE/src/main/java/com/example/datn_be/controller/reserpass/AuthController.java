package com.example.datn_be.controller.reserpass;

import com.example.datn_be.request.ChangePasswordRequest;
import com.example.datn_be.service.pass.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        boolean result = userService.resetPassword(username);

        if (result) {
            return ResponseEntity.ok("Mật khẩu đã được đặt lại về mặc định!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể đặt lại mật khẩu!");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        System.out.println("Received request: " + request.getUsername());
        try {
            userService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
