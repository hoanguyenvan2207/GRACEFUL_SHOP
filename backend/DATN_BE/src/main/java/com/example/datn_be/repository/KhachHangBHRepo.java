package com.example.datn_be.repository;

import com.example.datn_be.entiy.KhachHangBH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface KhachHangBHRepo extends JpaRepository<KhachHangBH, Integer> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO khach_hang (ho_ten, so_dien_thoai) VALUES (:hoTen, :soDienThoai)", nativeQuery = true)
    void insertKhachHang(String hoTen, String soDienThoai);

    Optional<KhachHangBH> findBySoDienThoai(String soDienThoai);

}
