package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.SanPhamChiTiet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamChiTietRepository extends JpaRepository<SanPhamChiTiet, Integer> {

    List<SanPhamChiTiet> findByAoDaiId(Integer aoDaiId);

    @Query("SELECT spct FROM SanPhamChiTiet spct " +
            "WHERE (" +
            "    :keyword IS NULL OR :keyword = '' OR " +
            "    LOWER(spct.maAoDaiChiTiet) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "    LOWER(spct.aoDai.maAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "    LOWER(spct.aoDai.tenAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "    LOWER(CONCAT(spct.aoDai.tenAoDai, " +
            "           CASE WHEN spct.kichThuoc.ten IS NOT NULL THEN CONCAT(' size ', spct.kichThuoc.ten) ELSE '' END, " +
            "           CASE WHEN spct.mauSac.tenMauSac IS NOT NULL THEN CONCAT(' mau ', spct.mauSac.tenMauSac) ELSE '' END)) " +
            "         LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "    LOWER(CONCAT(spct.aoDai.tenAoDai, " +
            "           CASE WHEN spct.mauSac.tenMauSac IS NOT NULL THEN CONCAT(' mau ', spct.mauSac.tenMauSac) ELSE '' END, " +
            "           CASE WHEN spct.kichThuoc.ten IS NOT NULL THEN CONCAT(' size ', spct.kichThuoc.ten) ELSE '' END)) " +
            "         LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") " +
            "AND (:listTenAoDai IS NULL OR spct.aoDai.tenAoDai IN :listTenAoDai) " +
            "AND (:listMauSac IS NULL OR spct.mauSac.tenMauSac IN :listMauSac) " +
            "AND (:listKichThuoc IS NULL OR spct.kichThuoc.ten IN :listKichThuoc) " +
            "AND (:trangThai IS NULL OR spct.trangThai = :trangThai) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'ngayTao_ASC' THEN spct.ngayTao END ASC, " +
            "CASE WHEN :sort = 'ngayTao_DESC' THEN spct.ngayTao END DESC, " +
            "CASE WHEN :sort = 'soLuong_ASC' THEN spct.soLuong END ASC, " +
            "CASE WHEN :sort = 'soLuong_DESC' THEN spct.soLuong END DESC, " +
            "CASE WHEN :sort = 'giaBan_ASC' THEN spct.giaGoc END ASC, " +
            "CASE WHEN :sort = 'giaBan_DESC' THEN spct.giaGoc END DESC")
    Page<SanPhamChiTiet> filterSanPhamChiTiet(
            @Param("keyword") String keyword,
            @Param("listTenAoDai") List<String> listTenAoDai,
            @Param("listMauSac") List<String> listMauSac,
            @Param("listKichThuoc") List<String> listKichThuoc,
            @Param("trangThai") Boolean trangThai,
            @Param("sort") String sort,
            Pageable pageable
    );

    boolean existsByAoDaiIdAndMauSacIdAndKichThuocId(Integer aoDaiId, Integer mauSacId, Integer kichThuocId);

}
