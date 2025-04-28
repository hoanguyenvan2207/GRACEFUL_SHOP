package com.example.datn_be.service.nhanvien;

import com.example.datn_be.dto.nhanvien.NhanVienRequest;
import com.example.datn_be.entiy.NhanVien.nhanvien;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NhanVienService {
    ResponseEntity<?> getAllNhanVien(Boolean status,String keyword, Pageable pageable);

    ResponseEntity<?> addNhanVien(NhanVienRequest nhanVienRequest);

    nhanvien updateThongTin(Integer id, NhanVienRequest nhanVienRequest);

    ResponseEntity<?> updateNhanVien(Integer id, NhanVienRequest nhanVienRequest);
    ResponseEntity<?> getNhanVienById(Integer id);
    List<nhanvien> getAllNhanViens();




}
