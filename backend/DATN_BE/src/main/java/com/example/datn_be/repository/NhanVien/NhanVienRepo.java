package com.example.datn_be.repository.NhanVien;

import com.example.datn_be.dto.nhanvien.NhanVienDTO;
import com.example.datn_be.entiy.NhanVien.nhanvien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhanVienRepo extends JpaRepository<nhanvien,Integer> {
    @Query("""
        SELECT new  com.example.datn_be.dto.nhanvien.NhanVienDTO(
            nv.maNhanVien, 
            nv.hoVaTen, 
            nv.ngaySinh, 
            nv.email, 
            nv.soDienThoai, 
            nv.diaChi, 
            nv.tenDangNhap, 
            nv.vaiTro.tenVaiTro
        ) 
        FROM nhanvien nv
    """)
    List<NhanVienDTO> getAllNhanVienaccount();
    @Query("""
    SELECT nv
    FROM nhanvien nv
    WHERE (:status IS NULL OR nv.trangThai = :status)
    AND (:keyword IS NULL OR nv.hoVaTen LIKE %:keyword% 
        OR nv.soDienThoai LIKE %:keyword%
        OR CAST(nv.ngaySinh AS string) LIKE %:keyword%
        OR nv.maNhanVien LIKE %:keyword%
        OR nv.diaChi LIKE %:keyword%
        OR nv.email LIKE %:keyword%)
""")
    Page<nhanvien> findByKeywordWithPagination(@Param("keyword") String keyword,
                                               @Param("status") Boolean status,
                                               Pageable pageable);

    nhanvien findByTenDangNhapAndMatKhau(String tenDangNhap, String matKhau);
    boolean existsByEmail(String email);
    nhanvien findByTenDangNhap(String tenDangNhap);
    nhanvien findByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByTenDangNhap(String tenDangNhap);


}