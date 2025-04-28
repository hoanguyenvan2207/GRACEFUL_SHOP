package com.example.datn_be.entiy.chat;

import java.time.LocalDateTime;

public interface ChatRoomDTO {
    Integer getId();
    Integer getIdNhanVien();
    LocalDateTime getNgayTao();
    Boolean getTrangThai();
    String getLastMessage();
    Integer getUnreadCount();
}

