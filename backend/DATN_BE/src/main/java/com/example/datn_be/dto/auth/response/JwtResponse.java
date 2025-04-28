package com.example.datn_be.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;

    private String type = "Bearer";

    private Integer id;

    private String email;

    private String hoTen;
}
