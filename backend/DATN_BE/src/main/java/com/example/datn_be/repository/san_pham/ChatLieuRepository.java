package com.example.datn_be.repository.san_pham;

import com.example.datn_be.entiy.san_pham.ChatLieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    @Query("SELECT c FROM ChatLieu c WHERE LOWER(c.ten) LIKE LOWER(CONCAT('%', :ten, '%')) " +
            "AND (:trangThai IS NULL OR c.trangThai = :trangThai)")
    Page<ChatLieu> findChatLieusByTenContainingIgnoreCase(@Param("ten") String ten,
                                                          @Param("trangThai") Boolean trangThai,
                                                          Pageable pageable);

    Optional<ChatLieu> findChatLieusByTen(String ten);

    List<ChatLieu> findChatLieusByTrangThaiIsTrue();

    boolean existsChatLieuByTen( String tenChatLieu);
}
