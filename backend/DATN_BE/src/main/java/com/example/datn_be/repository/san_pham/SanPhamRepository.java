package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
    public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

        @Query(value = "SELECT * FROM ao_dai sp " +
                "WHERE (:keyword IS NULL OR (" +
                "LOWER(sp.ten_ao_dai) COLLATE Latin1_General_CI_AI LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(sp.ma_ao_dai) COLLATE Latin1_General_CI_AI LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                "AND sp.trang_thai = :trangThai", nativeQuery = true)
        Page<SanPham> searchByKeywordWithTrangThai(@Param("keyword") String keyword,
                                                   @Param("trangThai") Boolean trangThai,
                                                   Pageable pageable);

        @Query("SELECT s FROM SanPham s " +
                "WHERE LOWER(s.maAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(s.tenAoDai) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        Page<SanPham> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

        @Query("SELECT s FROM SanPham s " +
                "WHERE (COALESCE(:keyword, '') = '' " +
                "       OR LOWER(s.maAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "       OR LOWER(s.tenAoDai) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                "AND (:tenLoaiAoDai IS NULL OR s.loaiAoDai.tenLoaiAoDai IN :tenLoaiAoDai) " +
                "AND (:tenChatLieu IS NULL OR s.chatLieu.ten IN :tenChatLieu) " +
                "AND (:tenTaAo IS NULL OR s.taAo.ten IN :tenTaAo) " +
                "AND (:tenNhaCungCap IS NULL OR s.nhaCungCap.tenNhaCungCap IN :tenNhaCungCap) " +
                "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
                "ORDER BY " +
                "CASE WHEN :sortDate = 'ASC' THEN s.ngayTao END ASC, " +
                "CASE WHEN :sortDate = 'DESC' THEN s.ngayTao END DESC")
        Page<SanPham> searchAndFilterSanPham(
                @Param("keyword") String keyword,
                @Param("tenLoaiAoDai") List<String> tenLoaiAoDai,
                @Param("tenChatLieu") List<String> tenChatLieu,
                @Param("tenTaAo") List<String> tenTaAo,
                @Param("tenNhaCungCap") List<String> tenNhaCungCap,
                @Param("trangThai") Boolean trangThai,
                @Param("sortDate") String sortDate,
                Pageable pageable
        );

        List<SanPham> findSanPhamsByTrangThaiIsTrue();

        @Query("SELECT s FROM SanPham s JOIN s.sanPhamChiTietList spct " +
                "WHERE s.trangThai = true " +
                "  AND ((:minPrice IS NULL OR :maxPrice IS NULL) OR spct.giaBan BETWEEN :minPrice AND :maxPrice) " +
                "  AND (:listLoaiAoDai IS NULL OR s.loaiAoDai.tenLoaiAoDai IN :listLoaiAoDai) " +
                "  AND (:listChatLieu IS NULL OR s.chatLieu.ten IN :listChatLieu) " +
                "  AND (:listTaAo IS NULL OR s.taAo.ten IN :listTaAo) " +
                "  AND (:listNhaCungCap IS NULL OR s.nhaCungCap.tenNhaCungCap IN :listNhaCungCap) " +
                "  AND (:listMauSac IS NULL OR spct.mauSac.tenMauSac IN :listMauSac) " +
                "  AND (:listKichThuoc IS NULL OR spct.kichThuoc.ten IN :listKichThuoc) " +
                "  AND ((:keyword IS NULL) OR " +
                "       (LOWER(s.maAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "        OR LOWER(s.tenAoDai) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
                "GROUP BY s " +
                "HAVING SUM(spct.soLuong) > 0 " +
                "ORDER BY " +
                "CASE WHEN :sortPrice IS NOT NULL AND :sortPrice = 'ASC' THEN MIN(spct.giaBan) END ASC, " +
                "CASE WHEN :sortPrice IS NOT NULL AND :sortPrice = 'DESC' THEN MIN(spct.giaBan) END DESC, " +
                "CASE WHEN :sortDate IS NOT NULL AND :sortDate = 'ASC' THEN s.ngayTao END ASC, " +
                "CASE WHEN :sortDate IS NOT NULL AND :sortDate = 'DESC' THEN s.ngayTao END DESC, " +
                "s.id ASC")
        Page<SanPham> filterProducts(
                @Param("listLoaiAoDai") List<String> listLoaiAoDai,
                @Param("listChatLieu") List<String> listChatLieu,
                @Param("listTaAo") List<String> listTaAo,
                @Param("listNhaCungCap") List<String> listNhaCungCap,
                @Param("listMauSac") List<String> listMauSac,
                @Param("listKichThuoc") List<String> listKichThuoc,
                @Param("minPrice") BigDecimal minPrice,
                @Param("maxPrice") BigDecimal maxPrice,
                @Param("keyword") String keyword,
                @Param("sortPrice") String sortPrice,
                @Param("sortDate") String sortDate,
                Pageable pageable
        );

    @Query(value =
            "SELECT TOP 5 s.* " +
                    "FROM ao_dai s " +
                    "LEFT JOIN ( " +
                    "    SELECT sct.id_ao_dai, SUM(hdct.so_luong) AS totalSold " +
                    "    FROM ao_dai_chi_tiet sct " +
                    "    INNER JOIN hoa_don_chi_tiet hdct ON hdct.id_ao_dai_chi_tiet = sct.id " +
                    "    INNER JOIN hoa_don hd ON hdct.id_hoa_don = hd.id " +
                    "    WHERE hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') " +
                    "    GROUP BY sct.id_ao_dai " +
                    ") sales ON sales.id_ao_dai = s.id " +
                    "WHERE s.trang_thai = 1 " +
                    "ORDER BY COALESCE(sales.totalSold, 0) DESC",
            nativeQuery = true
    )
    List<SanPham> findTopSellingProducts();



    boolean existsSanPhamByTenAoDai(String tenAoDai);

        Optional<SanPham> findSanPhamByMaAoDai(String tenAoDai);
    }

