package com.example.datn_be.repository;

import com.example.datn_be.dto.khachhang.response.KhachHangResponse;
import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepo extends JpaRepository<KhachHangTk, Integer> {
    @Query(value = """
            SELECT
                kh.id AS id,
                kh.ma_khach_hang AS maKhachHang,
                kh.ho_ten AS hoTen,
                kh.gioi_tinh AS gioiTinh,
                kh.ngay_sinh AS ngaySinh,
                kh.email AS email,
                kh.so_dien_thoai AS soDienThoai,
                kh.ngay_tao AS ngayTao,
                (
                    SELECT 
                        dc.id AS id,
                        dc.duong AS duong,
                        dc.xa_phuong AS xaPhuong,
                        dc.quan_huyen AS quanHuyen,
                        dc.tinh_thanh_pho AS tinhThanhPho,
                        dc.mac_dinh AS macDinh
                    FROM dia_chi dc
                    WHERE dc.id_khach_hang = kh.id
                    FOR JSON PATH
                ) AS diaChis
            FROM khach_hang kh
            WHERE (:keyword IS NULL
                OR kh.ma_khach_hang LIKE '%'+:keyword+'%'
                OR kh.ho_ten LIKE '%'+:keyword+'%'
                OR kh.email LIKE '%'+:keyword+'%'
                OR kh.so_dien_thoai LIKE '%'+:keyword+'%')
            ORDER BY kh.ngay_tao DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT kh.id)
                    FROM khach_hang kh
                    WHERE (:keyword IS NULL
                        OR kh.ma_khach_hang LIKE '%'+:keyword+'%'
                        OR kh.ho_ten LIKE '%'+:keyword+'%'
                        OR kh.email LIKE '%'+:keyword+'%'
                        OR kh.so_dien_thoai LIKE '%'+:keyword+'%')
                    """, nativeQuery = true)
    Page<KhachHangResponse> getAllKhachHang(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHangTk k WHERE k.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHangTk k WHERE k.soDienThoai = :soDienThoai")
    boolean existsBySoDienThoai(@Param("soDienThoai") String soDienThoai);

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHangTk k WHERE k.email = :email AND k.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM KhachHangTk k WHERE k.soDienThoai = :soDienThoai AND k.id != :id")
    boolean existsBySoDienThoaiAndIdNot(@Param("soDienThoai") String soDienThoai, @Param("id") Integer id);

    Optional<KhachHangTk> findByEmail(String email);

    Optional<KhachHangTk> findBySoDienThoai(String soDienThoai);

}
