package com.example.datn_be.repository.chat;

import com.example.datn_be.entiy.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByIdChatRoomOrderByNgayGuiAsc(Integer idChatRoom);

    @Query("SELECT m FROM ChatMessage m WHERE m.idChatRoom = :roomId ORDER BY m.ngayGui DESC")
    List<ChatMessage> findLatestMessages(@Param("roomId") Integer roomId);

    @Modifying
    @Transactional
    @Query("UPDATE ChatMessage m SET m.ngayDoc = :now WHERE m.idChatRoom = :roomId AND m.senderType = :senderType AND (m.ngayDoc IS NULL OR m.ngayDoc = m.ngayGui)")
    int markMessagesAsRead(@Param("roomId") Integer roomId, @Param("senderType") String senderType, @Param("now") LocalDateTime now);

}


