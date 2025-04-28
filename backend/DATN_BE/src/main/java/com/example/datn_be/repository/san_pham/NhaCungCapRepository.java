package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.NhaCungCap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Integer> {

    @Query("SELECT ncc FROM NhaCungCap ncc WHERE LOWER(ncc.tenNhaCungCap) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR ncc.trangThai = :trangThai)")
    Page<NhaCungCap> findNhaCungCapsByTenContainingIgnoreCase(@Param("ten") String ten,
                                                              @Param("trangThai") Boolean trangThai,
                                                              Pageable pageable);

    Optional<NhaCungCap> findNhaCungCapByTenNhaCungCap(String tenNhaCungCap);
    
    List<NhaCungCap> findNhaCungCapsByTrangThaiIsTrue();

    boolean existsNhaCungCapByTenNhaCungCap(String tenNhaCungCap);

    boolean existsNhaCungCapBySoDienThoai(String soDienThoai);

    boolean existsNhaCungCapByEmail(String email);
}
