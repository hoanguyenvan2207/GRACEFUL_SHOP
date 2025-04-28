package com.example.datn_be.entiy.chat;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @Column(name = "id")
    private Integer id; // ID bằng ID khách hàng

    @Column(name = "id_nhan_vien")
    private Integer idNhanVien; // Có thể null

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
