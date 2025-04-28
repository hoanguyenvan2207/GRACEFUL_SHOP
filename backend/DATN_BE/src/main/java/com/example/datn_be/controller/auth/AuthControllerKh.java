package com.example.datn_be.controller.auth;

import com.example.datn_be.dto.auth.request.DoiMkRequest;
import com.example.datn_be.dto.auth.request.KhachHang_DangKi_Request;
import com.example.datn_be.dto.auth.request.KhachHang_DangNhap_Request;
import com.example.datn_be.dto.auth.request.KhachHang_Update_Request;
import com.example.datn_be.service.auth.impl.AuthServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins =  {
        "http://localhost:5173",        // cho dev mode
        "https://graceful56.shop"       // cho production
},allowCredentials = "true")
@RequiredArgsConstructor
public class AuthControllerKh {

    @Autowired
    private final AuthServiceImpl authService;

    @PostMapping("/dang-ki")
    public ResponseEntity<?> dangKi(@Valid @RequestBody KhachHang_DangKi_Request request){
        return authService.DangKi(request);
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@Valid @RequestBody KhachHang_DangNhap_Request request){
        return authService.DangNhap(request);
    }

    @PutMapping("customer/update/{id}")
    public ResponseEntity<?> customer(@Valid @PathVariable Integer id, @RequestBody KhachHang_Update_Request request,
                                      BindingResult bindingResult){
        if (!id.equals(request.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "ID trong path và body không khớp");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        return authService.KhachHang_Update(id, request);
    }

    @GetMapping("customer/details/{id}")
    public ResponseEntity<?> details(@PathVariable Integer id){
        return authService.getCustomerById(id);
    }

    @PostMapping("/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(@Valid @RequestBody DoiMkRequest request){
        return authService.doiMatKhau(request.getEmail(), request);
    }

    @PostMapping("/dang-xuat")
    public ResponseEntity<?> dangXuat(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(responseMap);
    }


    @GetMapping("customer/hien-tai")
    public ResponseEntity<?> customerHientai(HttpServletRequest request){
        return authService.getKhachHangByToken(request);
    }

}
