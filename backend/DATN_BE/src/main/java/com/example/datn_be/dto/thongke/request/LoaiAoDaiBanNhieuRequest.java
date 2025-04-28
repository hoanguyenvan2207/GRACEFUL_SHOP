package com.example.datn_be.dto.thongke.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiAoDaiBanNhieuRequest {

    private String tenLoaiAoDai;

    private Integer soLuong;

    private Double phanTram;

}
