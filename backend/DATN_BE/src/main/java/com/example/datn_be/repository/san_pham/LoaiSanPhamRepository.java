package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.LoaiSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham, Integer> {

    @Query("SELECT lsp FROM LoaiSanPham lsp WHERE LOWER(lsp.tenLoaiAoDai) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR lsp.trangThai = :trangThai)")
    Page<LoaiSanPham> findLoaiSanPhamsByTenContainingIgnoreCase(@Param("ten") String ten,
                                                                @Param("trangThai") Boolean trangThai,
                                                                Pageable pageable);

    Optional<LoaiSanPham> findLoaiSanPhamByTenLoaiAoDai(String tenLoaiAoDai);

    List<LoaiSanPham> findLoaiSanPhamsByTrangThaiIsTrue();

    boolean existsLoaiSanPhamByTenLoaiAoDai(String ten);
}
