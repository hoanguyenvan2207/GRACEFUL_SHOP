package com.example.datn_be.service.chat;

import com.example.datn_be.entiy.chat.ChatRoomDTO;
import com.example.datn_be.entiy.chat.ChatMessage;
import com.example.datn_be.entiy.chat.ChatRoom;
import com.example.datn_be.repository.chat.ChatMessageRepository;
import com.example.datn_be.repository.chat.ChatRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    public ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatMessageRepository chatMessageRepository;

    public List<ChatRoomDTO> loadRooms(Boolean status) {
        return chatRoomRepository.findRoomsWithLastMessage(status);
    }

    public ChatRoom getOrCreateRoom(Integer customerId) {
        return chatRoomRepository.findById(customerId)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();
                    room.setId(customerId);
                    room.setTrangThai(false); // Mới tạo thì đóng
                    return chatRoomRepository.save(room);
                });
    }

    public ChatRoom joinRoom(Integer roomId, Integer staffId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        if (room.getIdNhanVien() == null && !room.getTrangThai()) {
            room.setIdNhanVien(staffId);
            room.setTrangThai(true); // Đã có nhân viên join
            return chatRoomRepository.save(room);
        }
        throw new RuntimeException("Room is already taken or closed");
    }

    public ChatRoom outRoom(Integer roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        // Kiểm tra nếu phòng đang mở và có nhân viên
        if (room.getTrangThai() && room.getIdNhanVien() != null) {
            room.setIdNhanVien(null);
            room.setTrangThai(false);
            return chatRoomRepository.save(room);
        }
        throw new RuntimeException("Room is not active or already closed");
    }

    public ChatMessage sendMessage(Integer roomId, String senderType, String noiDung) {
        ChatMessage message = new ChatMessage();
        message.setIdChatRoom(roomId);
        message.setSenderType(senderType);
        message.setNoiDung(noiDung);
        chatMessageRepository.save(message);

        return message;
    }

    public int markMessagesAsRead(Integer roomId, String readerType) {
        LocalDateTime now = LocalDateTime.now();

        String senderType = "STAFF".equals(readerType) ? "STAFF" : "CUSTOMER";

        return chatMessageRepository.markMessagesAsRead(roomId, senderType, now);
    }

}