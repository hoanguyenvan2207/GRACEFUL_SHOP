package com.example.datn_be.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Cái này là bắt lỗi ở @Valid (@NotNull, @NotBlank, ...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", Objects.requireNonNull(error.getDefaultMessage())
                ))
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Validation Error",
                "errors", errors
        ));
    }

    //Cái này là bắt lỗi ở service (trùng,... mấy cái @Valid không có)class ChatLieuDuplicateException tự định nghĩa
    @ExceptionHandler(ChatLieuDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateResourceException(ChatLieuDuplicateException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Duplicate Resource",
                "errors", List.of(Map.of("field", "tenChatLieu", "message", ex.getMessage()))
        ));
    }

    // Cái này bỏ cũng được bắt lỗi ở database hình như thế :)))
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Database Error",
                "errors", List.of(Map.of("field", "tenChatLieu", "message", "Tên chất liệu đã tồn tại."))
        ));
    }
       @ExceptionHandler(KhachHangException.class)
    public ResponseEntity<Map<String, String>> handleKhachHangException(KhachHangException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Map<String, String>> handleTokenException(TokenException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
