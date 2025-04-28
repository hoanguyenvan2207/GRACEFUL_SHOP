package com.example.datn_be.entiy.san_pham;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "khuyen_mai")
public class KhuyenMaiSP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ten_khuyen_mai")
    private String tenKhuyenMai;

    @Column(name = "ma_khuyen_mai")
    private String maKhuyenMai;

    @Column(name = "so_tien_giam")
    private Double soTienGiam;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "phan_tram_giam")
    private Integer phanTramGiam;

    @Column(name = "ngay_bat_dau")
    private ZonedDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private ZonedDateTime ngayKetThuc;
}
