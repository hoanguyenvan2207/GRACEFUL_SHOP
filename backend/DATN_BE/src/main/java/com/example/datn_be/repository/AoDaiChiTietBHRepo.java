package com.example.datn_be.repository;

import com.example.datn_be.entiy.AoDaiChiTietBH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AoDaiChiTietBHRepo extends JpaRepository<AoDaiChiTietBH, Integer> {
    @Query("""
    SELECT a FROM AoDaiChiTietBH a 
    JOIN a.aoDai ad 
    JOIN a.mauSac ms 
    JOIN a.kichThuoc kt 
    JOIN ad.chatLieu cl 
    WHERE (:keyWord IS NULL OR 
           LOWER(a.maAoDaiChiTiet) LIKE LOWER(CONCAT('%', :keyWord, '%')) OR 
           LOWER(ad.tenAoDai) LIKE LOWER(CONCAT('%', :keyWord, '%')) OR 
           LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', :keyWord, '%')) OR 
           LOWER(kt.ten) LIKE LOWER(CONCAT('%', :keyWord, '%')) OR 
           LOWER(cl.ten) LIKE LOWER(CONCAT('%', :keyWord, '%')))
""")
    List<AoDaiChiTietBH> timKiemSanPhamTheoKeyWord(@Param("keyWord") String keyWord);

    @Modifying
    @Transactional
    @Query("UPDATE AoDaiChiTietBH a SET a.soLuong = a.soLuong + :quantity WHERE a.id = :id")
    int congLaiSoLuong(@Param("id") Integer id, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE AoDaiChiTietBH a SET a.soLuong = a.soLuong - :soLuong WHERE a.id = :sanPhamId AND a.soLuong >= :soLuong")
    int truSoLuong(@Param("sanPhamId") Integer sanPhamId, @Param("soLuong") Integer soLuong);

    @Query("SELECT a.soLuong FROM AoDaiChiTietBH a WHERE a.id = :sanPhamId")
    int kiemTraSoLuong(@Param("sanPhamId") Integer sanPhamId);
}
