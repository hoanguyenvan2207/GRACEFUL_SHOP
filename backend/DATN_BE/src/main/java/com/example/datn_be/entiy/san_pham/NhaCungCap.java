package com.example.datn_be.entiy.san_pham;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "nha_cung_cap")
public class NhaCungCap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_nha_cung_cap", unique = true)
    private String tenNhaCungCap;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "so_dien_thoai", unique = true)
    private String soDienThoai;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_tao")
    private ZonedDateTime ngayTao;
}