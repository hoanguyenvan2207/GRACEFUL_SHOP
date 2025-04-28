package com.example.datn_be.repository;

import com.example.datn_be.dto.khuyenmai.KhuyenMaiDTO;
import com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO;
import com.example.datn_be.entiy.giam_gia.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {


    @Query(value = """
    SELECT * FROM khuyen_mai km 
    WHERE (:keyq IS NULL OR km.ma_khuyen_mai LIKE %:keyq% OR km.ten_khuyen_mai LIKE %:keyq% 
    OR km.phan_tram_giam LIKE %:keyq% 
     OR CAST(km.so_tien_giam AS VARCHAR) LIKE %:keyq%
    ) 
    AND (:status IS NULL OR km.trang_thai = :status)
    AND (
        :validity IS NULL 
        OR (:validity = 1 AND CAST(GETDATE() AS DATE) >= km.ngay_bat_dau AND CAST(GETDATE() AS DATE) <= km.ngay_ket_thuc)  -- Còn hiệu lực
        OR (:validity = 2 AND  CAST(GETDATE() AS DATE) > km.ngay_ket_thuc)                           -- Hết hiệu lực
        OR (:validity = 3 AND CAST(GETDATE() AS DATE) < km.ngay_bat_dau)                            -- Chưa bắt đầu
    )AND (:startDate IS NULL OR :endDate IS NULL OR (km.ngay_bat_dau >= :startDate AND km.ngay_ket_thuc <= :endDate))
    ORDER BY km.ngay_tao DESC
""", nativeQuery = true)
    Page<KhuyenMai> searchByKey(@Param("keyq") String keyq,
                                @Param("status") Boolean status,
                                @Param("validity") Integer validity,
                                Pageable pageable,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                int pageSize);



    @Query("SELECT km FROM KhuyenMai km")
    Page<KhuyenMai> getListKM(Pageable pageable);

    boolean existsKhuyenMaiByTenKhuyenMai(String tenKhuyenMai);

    boolean existsByTenKhuyenMaiAndIdNot(String tenKhuyenMai, Integer id);
}

