package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamChiTietDTO {
    private Integer id;

    private String maAoDaiChiTiet;

    @NotNull(message = "Giá gốc không được để trống.")
    private Double giaGoc;

    private Double giaBan;

    @NotNull(message = "Số lượng không được để trống.")
    private Integer soLuong;

    private Boolean trangThai;

    @NotBlank(message = "Ảnh không được để trống")
    private String anhUrl;

    private ZonedDateTime ngayTao;

    @NotNull(message = "Áo dài không được để trống.")
    private Integer aoDaiId;

    private String maAoDai;

    private String tenAoDai;

    @NotNull(message = "Màu sắc không được để trống.")
    private Integer mauSacId;

    private String tenMauSac;

    @NotNull(message = "Kích thước không được để trống.")
    private Integer kichThuocId;

    private String tenKichThuoc;

    private String tenKhuyenMai;

    private Integer khuyenMaiId;

    private String maKhuyenMai;

    private Integer phanTramGiam;

    private Double soTienGiam;

    private ZonedDateTime ngayBatDau;

    private ZonedDateTime ngayKetThuc;

    private Boolean trangThaiKhuyenMai;

}

