package com.example.datn_be.service.khachhang;

import com.example.datn_be.dto.khachhang.resquest.KhachHangRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface KhachHangService {
    ResponseEntity<?> getAllKhachHang(String keyword, Pageable pageable);

    ResponseEntity<?> addKhachHang(KhachHangRequest khachHangRequest);

    ResponseEntity<?> updateKhachHang(Integer id, KhachHangRequest khachHangRequest);

    ResponseEntity<?> getKhachHangById(Integer id);

}
