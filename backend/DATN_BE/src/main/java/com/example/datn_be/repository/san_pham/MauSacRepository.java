package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    @Query("SELECT ms FROM MauSac ms WHERE LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR ms.trangThai = :trangThai)")
    Page<MauSac> findMauSacsByTenContainingIgnoreCase(@Param("ten") String ten,
                                                                @Param("trangThai") Boolean trangThai,
                                                                Pageable pageable);

    Optional<MauSac> findMauSacByTenMauSac(String tenMauSac);

    List<MauSac> findMauSacsByTrangThaiIsTrue();

    boolean existsMauSacByTenMauSac(String tenMauSac);
}
