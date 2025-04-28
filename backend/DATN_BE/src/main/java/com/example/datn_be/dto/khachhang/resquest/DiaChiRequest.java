package com.example.datn_be.dto.khachhang.resquest;

import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiRequest {

    private Integer id;

    private String hoTen;

    private String soDienThoai;

    private String tinhThanhPho;

    private String quanHuyen;

    private String xaPhuong;

    private String duong;

    private Boolean macDinh;
}
