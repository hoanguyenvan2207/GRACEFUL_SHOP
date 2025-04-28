package com.example.datn_be.repository;

import com.example.datn_be.entiy.HoaDonChiTietBH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HoaDonChiTietRepo extends JpaRepository<HoaDonChiTietBH, Integer> {
    @Query("SELECT h FROM HoaDonChiTietBH h WHERE h.hoaDon.id = :idHoaDon")
    List<HoaDonChiTietBH> findByHoaDonId(@Param("idHoaDon") Integer idHoaDon);

    // Phương thức tìm kiếm theo id hóa đơn và id sản phẩm (aoDaiChiTiet)
    Optional<HoaDonChiTietBH> findByHoaDon_IdAndAoDaiChiTiet_Id(Integer hoaDonId, Integer aoDaiChiTietId);

}
