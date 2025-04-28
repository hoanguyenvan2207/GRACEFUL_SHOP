package com.example.datn_be.dto.thongke.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoanhThuTrongNgayRequest {

    private LocalDate ngay;

}
