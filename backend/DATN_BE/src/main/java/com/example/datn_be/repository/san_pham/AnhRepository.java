package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.Anh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AnhRepository extends JpaRepository<Anh, Integer> {

    @Modifying
    @Query("DELETE FROM Anh a WHERE a.anhUrl IN :anhUrls AND a.aoDai.id = :sanPhamId")
    void deleteAllByAnhUrlsAndSanPhamId(@Param("anhUrls") Set<String> anhUrls, @Param("sanPhamId") int sanPhamId);

    @Query("SELECT a FROM Anh a WHERE a.aoDai.id = :id")
    List<Anh> findAllBySanPhamId(@Param("id") Integer id);

}
