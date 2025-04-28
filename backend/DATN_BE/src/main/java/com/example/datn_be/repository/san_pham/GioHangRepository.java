package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangRepository extends JpaRepository<GioHangChiTiet, Integer> {


    @Query("SELECT gh FROM GioHangChiTiet gh WHERE gh.khachHang.id = :idKhachHang")
    List<GioHangChiTiet> findAllByKhachHangId(@Param("idKhachHang") Integer idKhachHang);
}
