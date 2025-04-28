package com.example.datn_be.controller.login;

import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class Login {
    @Autowired
    private NhanVienRepo nhanVienRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    // Khởi tạo phiên bản theo thời điểm khởi động ứng dụng
//    private final String backendVersion = String.valueOf(System.currentTimeMillis());
//
//    @GetMapping("/version")
//    public ResponseEntity<Map<String, String>> getVersion() {
//        Map<String, String> version = new HashMap<>();
//        version.put("version", backendVersion);
//        return ResponseEntity.ok(version);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody nhanvien loginRequest, HttpServletRequest httpRequest) {
        nhanvien nhanVien = nhanVienRepo.findByTenDangNhap(loginRequest.getTenDangNhap());

        if (nhanVien == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tên đăng nhập không tồn tại");
        }
        if (nhanVien.getTrangThai() == false) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Tài khoản này đã nghỉ việc và không thể đăng nhập.");
        }
        if (!passwordEncoder.matches(loginRequest.getMatKhau(), nhanVien.getMatKhau())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Mật khẩu không chính xác");
        }
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginRequest.getTenDangNhap(), loginRequest.getMatKhau());


        Authentication authentication = authenticationManager.authenticate(authRequest);


        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpRequest.getSession(true);
        httpRequest.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        Map<String, Object> response = new HashMap<>();
        response.put("idNhanVien", nhanVien.getId());
        response.put("maNhanVien", nhanVien.getMaNhanVien());
        response.put("hoVaTen", nhanVien.getHoVaTen());
        response.put("tenDangNhap", nhanVien.getTenDangNhap());
        response.put("ngaySinh", nhanVien.getNgaySinh());
        response.put("email", nhanVien.getEmail());
        response.put("soDienThoai", nhanVien.getSoDienThoai());
        response.put("diaChi", nhanVien.getDiaChi());
        response.put("mat_khau_tam_thoi", nhanVien.getMat_khau_tam_thoi());
        response.put("vaiTro", nhanVien.getVaiTro().getTenVaiTro());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(HttpSession session, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (principal == null) {
            return ResponseEntity.status(401).body("User is not authenticated.");
        }
        String username = principal.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> {
                    if (role.equalsIgnoreCase("ROLE_QUANLY") || role.equalsIgnoreCase("quản lý")) {
                        return "ROLE_QUANLY";
                    }else if (role.equalsIgnoreCase("ROLE_NHANVIEN")) {
                        return "ROLE_NHANVIEN";
                    }
                    return role.toLowerCase();
                })
                .collect(Collectors.toList());
        nhanvien nhanVien = nhanVienRepo.findByTenDangNhap(username);
        if (nhanVien == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("roles", roles);
        response.put("idNhanVien", nhanVien.getId());
        response.put("maNhanVien", nhanVien.getMaNhanVien());
        response.put("hoVaTen", nhanVien.getHoVaTen());
        response.put("tenDangNhap", nhanVien.getTenDangNhap());
        response.put("ngaySinh", nhanVien.getNgaySinh());
        response.put("email", nhanVien.getEmail());
        response.put("soDienThoai", nhanVien.getSoDienThoai());
        response.put("diaChi", nhanVien.getDiaChi());
        response.put("mat_khau_tam_thoi", nhanVien.getMat_khau_tam_thoi());
        response.put("vaiTro", nhanVien.getVaiTro().getTenVaiTro());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuthStatus(Principal principal) {
        Map<String, Boolean> result = new HashMap<>();
        boolean isAuthenticated = principal != null;
        result.put("authenticated", isAuthenticated);

        if (isAuthenticated) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

}