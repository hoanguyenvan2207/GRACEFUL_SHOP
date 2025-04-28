package com.example.datn_be.dto.sanpham;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHangDTO {

    private Integer id;

    private Integer idKhachHang;

    private Integer idSanPhamChiTiet;

    private Integer soLuong;

    private ZonedDateTime ngayTao;
}
