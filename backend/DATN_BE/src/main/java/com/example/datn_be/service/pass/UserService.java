package com.example.datn_be.service.pass;

import com.example.datn_be.entiy.NhanVien.UserApp;
import com.example.datn_be.repository.NhanVien.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean resetPassword(String username) {
        Optional<UserApp> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            UserApp user = userOpt.get();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("123456");
            user.setMatKhau(encodedPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        Optional<UserApp> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        UserApp users = optionalUser.get();
        if (!passwordEncoder.matches(oldPassword, users.getMatKhau())) {
            throw new RuntimeException("Old password is incorrect");
        }
        users.setMatKhau(passwordEncoder.encode(newPassword));
        users.setMat_khau_tam_thoi(1);
        userRepository.save(users);
        System.out.println("Đổi mật khẩu thành công!");

        return true;
    }


}
