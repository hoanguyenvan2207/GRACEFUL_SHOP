package com.example.datn_be.repository;

import com.example.datn_be.entiy.GiamGiaBH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GiamGiaBHRepo extends JpaRepository<GiamGiaBH, Integer> {
    List<GiamGiaBH> findByTrangThaiTrueAndSoLuongGreaterThan(int soLuong);

    @Query(value = """
    SELECT *
    FROM giam_gia gg
    WHERE gg.trang_thai = 1
      AND gg.so_luong > 0
      AND CAST(GETDATE() AS DATE) BETWEEN gg.ngay_bat_dau AND gg.ngay_ket_thuc
    ORDER BY
      CASE
        WHEN gg.loai_giam_gia = 0 THEN gg.gia_tri_giam
        ELSE gg.toi_da_giam_gia
      END DESC
    """, nativeQuery = true)
    List<GiamGiaBH> getAllGiamGia();

}
