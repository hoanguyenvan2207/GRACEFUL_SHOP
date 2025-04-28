package com.example.datn_be.service.auth;

import com.example.datn_be.dto.auth.request.DoiMkRequest;
import com.example.datn_be.dto.auth.request.KhachHang_DangKi_Request;
import com.example.datn_be.dto.auth.request.KhachHang_DangNhap_Request;
import com.example.datn_be.dto.auth.request.KhachHang_Update_Request;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> DangKi(KhachHang_DangKi_Request request);

    ResponseEntity<?> DangNhap(KhachHang_DangNhap_Request request);

    ResponseEntity<?> KhachHang_Update(Integer id, KhachHang_Update_Request request);

    ResponseEntity<?> getCustomerById(Integer id);

    ResponseEntity<?> doiMatKhau(String email, DoiMkRequest request);

    ResponseEntity<?> getKhachHangByToken(HttpServletRequest request);
}
