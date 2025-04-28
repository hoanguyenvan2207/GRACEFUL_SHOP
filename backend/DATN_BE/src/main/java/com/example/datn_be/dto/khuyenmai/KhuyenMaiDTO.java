package com.example.datn_be.dto.khuyenmai;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KhuyenMaiDTO {
    private Integer id;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private Double soTienGiam;
    private Integer phanTramGiam;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Boolean trangThai;
    private String moTa;
    private List<Integer> sanPhamChiTietIds;
}
