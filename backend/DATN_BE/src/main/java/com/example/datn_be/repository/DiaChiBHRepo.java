package com.example.datn_be.repository;

import com.example.datn_be.entiy.DiaChiBH;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiBHRepo extends JpaRepository<DiaChiBH, Integer> {
    List<DiaChiBH> findByKhachHang_IdOrderByDiaChiMacDinhDesc(Integer khachHangId);
    List<DiaChiBH> findByKhachHang_Id(Integer khachHangId);
}
