package com.example.datn_be.entiy.chat;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_chat_room")
    private Integer idChatRoom;

    @Column(name = "sender_type")
    private String senderType; // "khach_hang" hoặc "nhan_vien"

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "ngay_gui")
    private LocalDateTime ngayGui = LocalDateTime.now();

    @Column(name = "ngay_doc")
    private LocalDateTime ngayDoc;
}
