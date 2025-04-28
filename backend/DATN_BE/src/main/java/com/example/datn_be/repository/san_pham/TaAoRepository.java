package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.TaAo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaAoRepository extends JpaRepository<TaAo, Integer> {

    @Query("SELECT ta FROM TaAo ta WHERE LOWER(ta.ten) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR ta.trangThai = :trangThai)")
    Page<TaAo> findTaAosByTenContainingIgnoreCase(@Param("ten") String ten,
                                                      @Param("trangThai") Boolean trangThai,
                                                      Pageable pageable);

    Optional<TaAo> findTaAoByTen(String ten);

    List<TaAo> findTaAosByTrangThaiIsTrue();

    boolean existsTaAoByTen(String ten);
}
