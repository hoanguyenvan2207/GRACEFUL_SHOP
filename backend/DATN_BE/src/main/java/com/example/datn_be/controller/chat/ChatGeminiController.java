package com.example.datn_be.controller.chat;

import com.example.datn_be.service.sanpham.ChatGeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/advisor")
@CrossOrigin("*")
public class ChatGeminiController {

    @Autowired
    private ChatGeminiService chatGeminiService;

    @PostMapping("/consult")
    public ResponseEntity<?> getProductAdvice(@RequestBody Map<String, String> request) {
        String customerQuery = request.get("query");
        if (customerQuery == null || customerQuery.isEmpty()) {
            return ResponseEntity.badRequest().body("Câu hỏi không được để trống");
        }

        String advice = chatGeminiService.getProductAdvice(customerQuery);

        Map<String, Object> response = new HashMap<>();
        response.put("advice", advice);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body("Tin nhắn không được để trống");
        }

        String response = chatGeminiService.askGemini(message);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("reply", response);

        return ResponseEntity.ok(responseMap);
    }
}