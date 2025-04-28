package com.example.datn_be.dto.nhanvien;

import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final NhanVienRepo nhanVienRepo;

    public CustomAuthenticationSuccessHandler(NhanVienRepo nhanVienRepo) {
        this.nhanVienRepo = nhanVienRepo;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        nhanvien nhanVien = nhanVienRepo.findByEmail((userDetails.getUsername()));

        boolean mat_khau_tam_thoi = (nhanVien.getMat_khau_tam_thoi() == 0);
        request.getSession().setAttribute("mat_khau_tam_thoi", nhanVien.getMat_khau_tam_thoi());
    }
}
