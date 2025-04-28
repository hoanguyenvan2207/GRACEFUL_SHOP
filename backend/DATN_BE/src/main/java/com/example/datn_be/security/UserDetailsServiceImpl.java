package com.example.datn_be.security;

import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import com.example.datn_be.repository.KhachHangRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final KhachHangRepo khachHangRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        KhachHangTk khachHang = khachHangRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                khachHang.getEmail(),
                khachHang.getMatKhau(),
                new ArrayList<>()  // Có thể thêm roles/authorities nếu cần
        );
    }


}
