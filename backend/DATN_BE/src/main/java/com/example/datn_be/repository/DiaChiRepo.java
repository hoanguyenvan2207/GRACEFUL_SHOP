package com.example.datn_be.repository;

import com.example.datn_be.dto.khachhang.response.DiaChiResponse;
import com.example.datn_be.entiy.khach_hang.DiaChiTk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface DiaChiRepo extends JpaRepository<DiaChiTk, Integer> {
    @Query(value = """
        select 
            dc.id as id,
            kh.ho_ten as hoTen,
            kh.so_dien_thoai as soDienThoai,
            dc.tinh_thanh_pho as tinhThanhPho,
            dc.quan_huyen as quanHuyen,
            dc.xa_phuong as xaPhuong,
            dc.duong as duong,
            dc.mac_dinh as macDinh
        from dia_chi dc join khach_hang kh on dc.id_khach_hang= kh.id
""",nativeQuery = true)
    List<DiaChiResponse> getAllDiaChi();

    @Query(value = """
        SELECT 
            dc.id as id,
            kh.ho_ten as hoTen,
            kh.so_dien_thoai as soDienThoai,
            dc.tinh_thanh_pho as tinhThanhPho,
            dc.quan_huyen as quanHuyen,
            dc.xa_phuong as xaPhuong,
            dc.duong as duong,
            dc.mac_dinh as macDinh
        FROM dia_chi dc
        JOIN khach_hang kh ON dc.id_khach_hang = kh.id
        WHERE kh.id = :khachHangId
        """, nativeQuery = true)
    List<DiaChiResponse> findByKhachHangId(@Param("khachHangId") Integer khachHangId);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM dia_chi
            WHERE id= :id
              AND id_khach_hang=:khachHangId;
            """,nativeQuery = true)
    void deleteById(@Param("id")Integer id, @Param("khachHangId")Integer khachHangId);

    List<DiaChiTk> findByKhachHang_IdAndDuongAndXaPhuongAndQuanHuyenAndTinhThanhPho(
            Integer khachHangId, String duong, String xaPhuong, String quanHuyen, String tinhThanhPho);

    List<DiaChiTk> findByKhachHang_IdAndDuongAndXaPhuongAndQuanHuyenAndTinhThanhPhoAndIdNot(
            Integer khachHangId, String duong, String xaPhuong, String quanHuyen, String tinhThanhPho, Integer id);
}
