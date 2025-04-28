package com.example.datn_be.repository.chat;

import com.example.datn_be.entiy.chat.ChatRoomDTO;
import com.example.datn_be.entiy.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {

    @Query(value = "SELECT r.id, r.id_nhan_vien, r.ngay_tao, r.trang_thai, " +
            "       (SELECT TOP 1 m.noi_dung FROM chat_message m WHERE m.id_chat_room = r.id ORDER BY m.ngay_gui DESC) as lastMessage, " +
            "       (SELECT COUNT(*) FROM chat_message m WHERE m.id_chat_room = r.id AND m.ngay_doc IS NULL AND m.sender_type = 'CUSTOMER') as unreadCount " +
            "FROM chat_room r " +
            "WHERE (:status IS NULL OR r.trang_thai = :status) " +
            "ORDER BY COALESCE((SELECT TOP 1 m.ngay_gui FROM chat_message m WHERE m.id_chat_room = r.id ORDER BY m.ngay_gui DESC), r.ngay_tao) DESC",
            nativeQuery = true)
    List<ChatRoomDTO> findRoomsWithLastMessage(@Param("status") Boolean status);

}

