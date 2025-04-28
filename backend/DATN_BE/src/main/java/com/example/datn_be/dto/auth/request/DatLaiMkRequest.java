package com.example.datn_be.dto.auth.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatLaiMkRequest {

    private String email;

    private String otp;

    private String matKhau;

}