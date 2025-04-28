package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.KichThuoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    @Query("SELECT kt FROM KichThuoc kt WHERE LOWER(kt.ten) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR kt.trangThai = :trangThai)")
    Page<KichThuoc> findKichThuocsByTenContainingIgnoreCase(@Param("ten") String ten,
                                                          @Param("trangThai") Boolean trangThai,
                                                          Pageable pageable);

    Optional<KichThuoc> findKichThuocByTen(String ten);

    List<KichThuoc> findKichThuocsByTrangThaiIsTrue();

    boolean existsKichThuocByTen(String ten);
}
