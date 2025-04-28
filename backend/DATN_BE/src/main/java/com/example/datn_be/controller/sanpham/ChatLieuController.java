package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.ChatLieuDTO;
import com.example.datn_be.service.sanpham.ChatLieuService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat-lieu")
@CrossOrigin("*")
public class ChatLieuController {

    private final ChatLieuService chatLieuService;

    @Autowired
    public ChatLieuController(ChatLieuService chatLieuService) {
        this.chatLieuService = chatLieuService;
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<ChatLieuDTO>> getChatLieu() {
        return ResponseEntity.ok(chatLieuService.findAllChatLieu());
    }

    @GetMapping("list/enables")
    public ResponseEntity<List<ChatLieuDTO>> getChatLieuEnables() {
        return ResponseEntity.ok(chatLieuService.getChatLieusByTrangThaiTrue());
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<ChatLieuDTO>> searchChatLieus(@RequestParam(defaultValue = "0" ) int page,
                                                                @RequestParam(defaultValue = "5") int size,
                                                                @RequestParam(required = false, defaultValue = "") String keyword,
                                                                @RequestParam(required = false) Boolean trangThai)
    {
        return ResponseEntity.ok(chatLieuService.findByTenContainingIgnoreCaseAndTrangThaiIsNullOrTrangThai(page, size, keyword, trangThai));
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<ChatLieuDTO> findChatLieuByName(@PathVariable(required = true) String ten){
        return ResponseEntity.ok(chatLieuService.getChatLieuByTen(ten));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addChatLieu(@Valid @RequestBody ChatLieuDTO chatLieuDTO) {
        ChatLieuDTO savedChatLieu = chatLieuService.addChatLieu(chatLieuDTO);
        return ResponseEntity.ok(savedChatLieu);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ChatLieuDTO> updateChatLieu(@Valid @PathVariable int id, @RequestBody ChatLieuDTO chatLieuDTO) {
        ChatLieuDTO updatedChatLieu = chatLieuService.updateChatLieu(id, chatLieuDTO);
        if (updatedChatLieu != null) {
            return ResponseEntity.ok(updatedChatLieu);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ChatLieuDTO> getChatLieuById(@PathVariable int id) {
        ChatLieuDTO chatLieuDTO = chatLieuService.getChatLieuById(id);
        return chatLieuDTO != null ? ResponseEntity.ok(chatLieuDTO) : ResponseEntity.notFound().build();
    }
}
