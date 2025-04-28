package com.example.datn_be.repository;

import com.example.datn_be.entiy.HoaDonBH;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface HoaDonBHRepo extends JpaRepository<HoaDonBH, Integer> {

    // Tìm kiếm theo mã HD hoặc SDT KH có hỗ trợ lọc theo hình thức mua hàng
    @Query("SELECT h FROM HoaDonBH h WHERE " +
            "(:searchQuery IS NULL OR h.maHoaDon LIKE %:searchQuery% OR h.khachHang.soDienThoai LIKE %:searchQuery%) " +
            "AND h.hinhThucMuaHang = :hinhThucMuaHang")
    Page<HoaDonBH> timKiemHoaDonTheoHinhThuc(@Param("searchQuery") String searchQuery,
                                             @Param("hinhThucMuaHang") Boolean hinhThucMuaHang,
                                             Pageable pageable);

    // Các phương thức tìm kiếm, lọc theo khoảng thời gian và trạng thái có hỗ trợ lọc theo hình thức mua hàng
    Page<HoaDonBH> findByTrangThaiAndNgayTaoBetweenAndHinhThucMuaHang(String trangThai, Date ngayBatDau, Date ngayKetThuc, Boolean hinhThucMuaHang, Pageable pageable);

    @Query("SELECT h FROM HoaDonBH h WHERE h.ngayTao BETWEEN :ngayBatDau AND :ngayKetThuc AND h.hinhThucMuaHang = :hinhThucMuaHang")
    Page<HoaDonBH> findByNgayTaoBetweenAndHinhThucMuaHang(@Param("ngayBatDau") Date ngayBatDau,
                                                          @Param("ngayKetThuc") Date ngayKetThuc,
                                                          @Param("hinhThucMuaHang") Boolean hinhThucMuaHang,
                                                          Pageable pageable);

    Page<HoaDonBH> findByTrangThaiAndHinhThucMuaHang(String trangThai, Boolean hinhThucMuaHang, Pageable pageable);

    // Nếu không có các bộ lọc khác nhưng có yêu cầu theo hình thức mua hàng
    Page<HoaDonBH> findByHinhThucMuaHang(Boolean hinhThucMuaHang, Pageable pageable);

    // Các phương thức ban đầu của bạn
    @Query("SELECT h FROM HoaDonBH h WHERE " +
            "(:searchQuery IS NULL OR h.maHoaDon LIKE %:searchQuery% OR h.khachHang.soDienThoai LIKE %:searchQuery%)")
    Page<HoaDonBH> timKiemHoaDon(@Param("searchQuery") String searchQuery, Pageable pageable);

    Page<HoaDonBH> findByTrangThai(String trangThai, Pageable pageable);

    List<HoaDonBH> findByTrangThai(String trangThai);

    @Query("SELECT h FROM HoaDonBH h LEFT JOIN h.khachHang kh WHERE " +
            "(:tuKhoa IS NULL OR " +
            "LOWER(h.maHoaDon) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(COALESCE(kh.hoTen, '')) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(COALESCE(kh.soDienThoai, '')) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(COALESCE(kh.maKhachHang, '')) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
            "AND h.trangThai = 'Chưa thanh toán'")
    List<HoaDonBH> timKiemHoaDonTheoTuKhoa(@Param("tuKhoa") String tuKhoa);

    Page<HoaDonBH> findByNgayTaoBetween(Date ngayBatDau, Date ngayKetThuc, Pageable pageable);

    Page<HoaDonBH> findByTrangThaiAndNgayTaoBetween(String trangThai, Date ngayBatDau, Date ngayKetThuc, Pageable pageable);


}
