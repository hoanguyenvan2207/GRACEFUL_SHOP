package com.example.datn_be.dto.khachhang.resquest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiEditRequest {

    private Integer id;

    private String duong;

    private String xaPhuong;

    private String quanHuyen;

    private String tinhThanhPho;

    private Boolean macDinh;
}
