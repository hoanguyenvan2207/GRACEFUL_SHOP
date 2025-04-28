package com.example.datn_be.controller.khachhang;

import com.example.datn_be.dto.khachhang.resquest.KhachHangRequest;
import com.example.datn_be.service.khachhang.impl.KhachHangServieImpl;
import com.example.datn_be.validator.KhachHangValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
@RequestMapping("/api/admin/khach-hang")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class KhachHangController {

    @Autowired
    private final KhachHangServieImpl khachHangServie;

    @Autowired
    private final KhachHangValidator khachHangValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(khachHangValidator);
    }

    @GetMapping("hien-thi")
    public ResponseEntity<?> hienThi(@RequestParam(required = false) String keyword,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 5);
        return khachHangServie.getAllKhachHang(keyword, pageable);
    }

    @PostMapping("add")
    public ResponseEntity<?> addKhachHang(@Valid @RequestBody KhachHangRequest khachHangRequest,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        return khachHangServie.addKhachHang(khachHangRequest);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateKhachHang(@PathVariable Integer id,
                                             @Valid @RequestBody KhachHangRequest khachHangRequest,
                                             BindingResult bindingResult) {
        if (!id.equals(khachHangRequest.getId())) {
            Map<String, String> error = new HashMap<>();
//            error.put("error", "ID trong path và body không khớp");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        return khachHangServie.updateKhachHang(id, khachHangRequest);
    }

    @GetMapping("details/{id}")
    public ResponseEntity<?> detailsKhachHang(@PathVariable Integer id) {
        return khachHangServie.getKhachHangById(id);
    }
}
