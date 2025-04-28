package com.example.datn_be.service.auth.impl;

import com.example.datn_be.dto.auth.request.DoiMkRequest;
import com.example.datn_be.dto.auth.request.KhachHang_DangKi_Request;
import com.example.datn_be.dto.auth.request.KhachHang_DangNhap_Request;
import com.example.datn_be.dto.auth.request.KhachHang_Update_Request;
import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import com.example.datn_be.exception.KhachHangException;
import com.example.datn_be.repository.KhachHangRepo;
import com.example.datn_be.security.jwt.JwtUtil;
import com.example.datn_be.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KhachHangRepo khachHangRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> DangKi(KhachHang_DangKi_Request request) {
        if (khachHangRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }
        if (khachHangRepo.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }
        KhachHangTk khachHang = new KhachHangTk();
        khachHang.setHoTen(request.getHoTen());
        khachHang.setEmail(request.getEmail());
        khachHang.setSoDienThoai(request.getSoDienThoai());
        khachHang.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        KhachHangTk savedKhachHang = khachHangRepo.save(khachHang);
        String token = jwtUtil.generateToken(savedKhachHang);
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        httpRequest.getSession(true).setAttribute("JWT_TOKEN", token);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Đăng ký thành công");
        response.put("user", savedKhachHang);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> DangNhap(KhachHang_DangNhap_Request request) {
        KhachHangTk khachHang = null;

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            khachHang = khachHangRepo.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email không đúng"));
        }

        else if (request.getSoDienThoai() != null && !request.getSoDienThoai().isEmpty()) {
            khachHang = khachHangRepo.findBySoDienThoai(request.getSoDienThoai())
                    .orElseThrow(() -> new RuntimeException("Số điện thoại không đúng"));
        }
        else {
            throw new RuntimeException("Vui lòng cung cấp email hoặc số điện thoại");
        }

        boolean isAuthenticated = khachHang.getMatKhau().startsWith("$2a$")
                ? passwordEncoder.matches(request.getMatKhau(), khachHang.getMatKhau())
                : request.getMatKhau().equals(khachHang.getMatKhau());

        if (!isAuthenticated) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        String token = jwtUtil.generateToken(khachHang);
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        httpRequest.getSession(true).setAttribute("KHACHHANG", khachHang);
        httpRequest.getSession(true).setAttribute("JWT_TOKEN", token);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("type", "Bearer");
        response.put("id", khachHang.getId());
        response.put("email", khachHang.getEmail());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> KhachHang_Update(@PathVariable Integer id, @Valid @RequestBody KhachHang_Update_Request request) {
        try {
            KhachHangTk khachHang = khachHangRepo.findById(id)
                    .orElseThrow(() -> new KhachHangException("Khách hàng không tồn tại với ID: " + id));

            // Nếu khách hàng thay đổi số điện thoại thì kiểm tra trùng lặp
            if (!khachHang.getSoDienThoai().equals(request.getSoDienThoai())
                    && khachHangRepo.existsBySoDienThoai(request.getSoDienThoai())) {
                throw new KhachHangException("Số điện thoại đã tồn tại trong hệ thống.");
            }

            // Kiểm tra ngày sinh phải khác null và là ngày trong quá khứ (không được bằng hoặc sau ngày hiện tại)
//            if (request.getNgaySinh() == null || !request.getNgaySinh().isBefore(LocalDate.now())) {
//                throw new KhachHangException("Ngày sinh phải là ngày trong quá khứ.");
//            }

            khachHang.setHoTen(request.getHoTen());
            khachHang.setGioiTinh(request.getGioiTinh());
            khachHang.setNgaySinh(request.getNgaySinh());
            khachHang.setSoDienThoai(request.getSoDienThoai());
            return new ResponseEntity<>(khachHangRepo.save(khachHang), HttpStatus.OK);
        }  catch (KhachHangException e) {
            // --- SỬA TỪ ĐÂY ---
            Map<String, String> body = new HashMap<>();
            body.put("message", e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
            // --- ĐẾN ĐÂY ---
        } catch (Exception e) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "Lỗi hệ thống, vui lòng thử lại sau.");
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    public ResponseEntity<?> getCustomerById(Integer id) {
        return khachHangRepo.findById(id)
                .map(customer->{
                    return new ResponseEntity<>(customer, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public ResponseEntity<?> doiMatKhau(String email, DoiMkRequest request) {
        KhachHangTk khachHang = khachHangRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        khachHang.setMatKhau(passwordEncoder.encode(request.getMatKhau()));
        khachHangRepo.save(khachHang);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đổi mật khẩu thành công");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getKhachHangByToken(HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute("JWT_TOKEN");
        if (token == null) {
            return ResponseEntity.ok(null);
        }
        Integer userId = jwtUtil.getCustomerIdFromToken(token);
        KhachHangTk user = khachHangRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));
        return ResponseEntity.ok(user);
    }

}