package com.example.datn_be.repository;

import com.example.datn_be.entiy.giam_gia.GiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface GiamGiaRepository extends JpaRepository<GiamGia,Integer> {
    @Query(value = """
    SELECT * FROM giam_gia gg
    WHERE (:keyq IS NULL OR gg.ma_giam_gia LIKE %:keyq% OR gg.ten_giam_gia LIKE %:keyq% OR gg.gia_tri_giam LIKE %:keyq% )
    AND (:status IS NULL OR gg.trang_thai = :status)
    AND (
        :validity IS NULL
        OR (:validity = 1 AND CAST(GETDATE() AS DATE) >= gg.ngay_bat_dau AND CAST(GETDATE() AS DATE) <= gg.ngay_ket_thuc)  -- Còn hiệu lực
        OR (:validity = 2 AND CAST(GETDATE() AS DATE) > gg.ngay_ket_thuc)                             -- Hết hiệu lực
        OR (:validity = 3 AND CAST(GETDATE() AS DATE) < gg.ngay_bat_dau)                             -- Chưa bắt đầu
    )AND (:startDate IS NULL OR :endDate IS NULL OR (gg.ngay_bat_dau >= :startDate AND gg.ngay_ket_thuc <= :endDate))
    ORDER BY gg.ngay_tao DESC
""", nativeQuery = true)
    Page<GiamGia> searchByKey(@Param("keyq") String keyq,
                              @Param("status") Boolean status,
                              @Param("validity") Integer validity,
                              Pageable pageable,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              int pageSize);

    boolean existsGiamGiaByTenGiamGia(String tenGiamGia);

    boolean existsByTenGiamGiaAndIdNot(String tenGiamGia, Integer id);
    }
