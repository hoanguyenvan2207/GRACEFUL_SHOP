package com.example.datn_be.repository;

import com.example.datn_be.entiy.HoaDonBH;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface QuanLyDonHangRepo extends JpaRepository<HoaDonBH, Integer> {

    Page<HoaDonBH> findByHinhThucMuaHang(Boolean hinhThucMuaHang, Pageable pageable);

    @Query("SELECT h FROM HoaDonBH h JOIN h.khachHang kh " +
            "WHERE h.hinhThucMuaHang = true AND " +
            "(h.maHoaDon LIKE CONCAT('%', :keyword, '%') " +
            " OR kh.soDienThoai LIKE CONCAT('%', :keyword, '%') " +
            " OR kh.hoTen LIKE CONCAT('%', :keyword, '%'))")
    Page<HoaDonBH> searchOnlineOrdersByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<HoaDonBH> findByHinhThucMuaHangAndTrangThai(Boolean hinhThucMuaHang, String trangThai, Pageable pageable);

    Page<HoaDonBH> findByHinhThucMuaHangAndNgayTaoBetween(Boolean hinhThucMuaHang, Date ngayBatDau, Date ngayKetThuc, Pageable pageable);

    Page<HoaDonBH> findByHinhThucMuaHangAndTrangThaiAndNgayTaoBetween(Boolean hinhThucMuaHang, String trangThai, Date ngayBatDau, Date ngayKetThuc, Pageable pageable);

    Page<HoaDonBH> findByHinhThucMuaHangOrderByNgayTaoDesc(boolean hinhThucMuaHang, Pageable pageable);
    @Modifying
    @Transactional
    @Query("UPDATE HoaDonBH h SET h.trangThai = :trangThai WHERE h.id = :id")
    int updateTrangThai(@Param("id") Integer id, @Param("trangThai") String trangThai);

     List<HoaDonBH> findByKhachHang_IdAndHinhThucMuaHangOrderByNgayTaoDesc(Integer khachHangId, boolean hinhThucMuaHang);
}
