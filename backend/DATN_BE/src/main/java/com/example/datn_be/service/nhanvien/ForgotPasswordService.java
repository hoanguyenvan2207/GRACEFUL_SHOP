package com.example.datn_be.service.nhanvien;

import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import com.example.datn_be.repository.NhanVien.ResetToken;
import com.example.datn_be.repository.NhanVien.ResetTokenStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ForgotPasswordService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NhanVienRepo nhanVienRepo;

    public void sendForgotPasswordEmail(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Chào bạn,\n\nBạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu:\n"
                + resetUrl + "\n\nNếu không phải bạn yêu cầu, hãy bỏ qua email này.\n\nTrân trọng");
        mailSender.send(message);
    }
    public void resetPassword(String token, String newPassword) {
        ResetToken tokenData = ResetTokenStore.get(token);

        if (tokenData == null || tokenData.isExpired()) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn.");
        }

        nhanvien user = nhanVienRepo.findByEmail(tokenData.getEmail());
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng với email: " + tokenData.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(newPassword);

        user.setMatKhau(encodedPassword);
        user.setMat_khau_tam_thoi(1);
        nhanVienRepo.save(user);

        ResetTokenStore.remove(token);
    }
}
