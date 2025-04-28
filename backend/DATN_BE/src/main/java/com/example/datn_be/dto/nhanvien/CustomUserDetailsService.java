package com.example.datn_be.dto.nhanvien;

import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private NhanVienRepo nhanVienRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        nhanvien nhanVien = nhanVienRepository.findByTenDangNhap(username);
        if (nhanVien == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        String role = nhanVien.getVaiTro().getTenVaiTro();
        if (role != null) {
            role = role.toUpperCase();
            if ("QUẢN LÝ".equals(role)) {
                role = "QUANLY";
            }else if ("NHÂN VIÊN".equals(role)) {
                role = "NHANVIEN";
            }
        } else {
            role = "user";
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return new User(
                nhanVien.getTenDangNhap(),
                nhanVien.getMatKhau(),
                Collections.singleton(authority)
        );
    }
}
