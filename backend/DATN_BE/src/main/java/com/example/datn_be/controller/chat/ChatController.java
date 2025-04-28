//package com.example.datn_be.controller.chat;
//
//import com.example.datn_be.entiy.chat.ChatMessage;
//import com.example.datn_be.entiy.chat.ChatRoom;
//import com.example.datn_be.entiy.chat.ChatRoomDTO;
//import com.example.datn_be.service.chat.ChatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/chat")
//@CrossOrigin("*")
//public class ChatController {
//
//    @Autowired
//    private ChatService chatService;
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//
//    @GetMapping("/room/{customerId}")
//    public ChatRoom getOrCreateRoom(@PathVariable Integer customerId) {
//        return chatService.getOrCreateRoom(customerId);
//    }
//
//    @PostMapping("/room/{roomId}/join")
//    public ChatRoom joinRoom(@PathVariable Integer roomId, @RequestParam Integer staffId) {
//        ChatRoom updatedRoom = chatService.joinRoom(roomId, staffId);
//        // Gửi thông báo cập nhật cho tất cả client
//        messagingTemplate.convertAndSend("/topic/room/update", updatedRoom);
//        return updatedRoom;
//    }
//
//    @PostMapping("/room/{roomId}/out")
//    public ChatRoom outRoom(@PathVariable Integer roomId) {
//        ChatRoom updatedRoom = chatService.outRoom(roomId);
//        messagingTemplate.convertAndSend("/topic/room/update", updatedRoom);
//        return updatedRoom;
//    }
//
//    @GetMapping("/rooms")
//    public List<ChatRoomDTO> getAllRooms(@RequestParam(required = false) Boolean status) {
//        return chatService.loadRooms(status);
//    }
//
//    @GetMapping("/messages/{roomId}")
//    public List<ChatMessage> getMessages(@PathVariable Integer roomId) {
//        return chatService.chatMessageRepository.findByIdChatRoomOrderByNgayGuiAsc(roomId);
//    }
//
//    @PostMapping("/messages/{roomId}/read")
//    public ResponseEntity<Map<String, Object>> markMessagesAsRead(
//            @PathVariable Integer roomId,
//            @RequestParam String readerType) {
//
//        int updatedCount = chatService.markMessagesAsRead(roomId, readerType);
//        return ResponseEntity.ok(Map.of(
//                "success", true,
//                "messagesMarked", updatedCount,
//                "roomId", roomId
//        ));
//    }
//
//    // ======= STOMP / WebSocket Endpoints =======
//
//    @MessageMapping("/chat.sendMessage")
//    public void sendMessage(@Payload ChatMessage message) {
//        // Lưu tin nhắn vào DB và lấy tin nhắn đã lưu
//        ChatMessage savedMessage = chatService.sendMessage(message.getIdChatRoom(),
//                message.getSenderType(),
//                message.getNoiDung());
//        messagingTemplate.convertAndSend("/topic/room/" + message.getIdChatRoom(), savedMessage);
//
//        List<ChatRoomDTO> updatedRooms = chatService.loadRooms(null);
//        messagingTemplate.convertAndSend("/topic/room/update", updatedRooms);
//    }
//
//    @MessageMapping("/fetch-rooms")
//    public void fetchRooms(@Payload Map<String, Object> payload) {
//        Boolean status = payload.get("status") != null ? Boolean.valueOf(payload.get("status").toString()) : null;
//        List<ChatRoomDTO> rooms = chatService.loadRooms(status);
//        messagingTemplate.convertAndSend("/topic/room/update", rooms);
//    }
//}
