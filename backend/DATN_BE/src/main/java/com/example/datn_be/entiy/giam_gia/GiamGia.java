package com.example.datn_be.entiy.giam_gia;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "giam_gia")
public class GiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giam_gia" ,insertable = false, updatable = false)
    private String maGiamGia;

    @Column(name = "ten_giam_gia")
    private String tenGiamGia;

    @Column(name = "loai_giam_gia")
    private String loaiGiamGia;

    @Column(name = "gia_tri_giam")
    private Double giaTriGiam;

    @Column(name = "toi_da_giam_gia", precision = 18, scale = 0)
    private BigDecimal toiDaGiamGia;

    @Column(name = "gia_tri_toi_thieu", precision = 18, scale = 0)
    private BigDecimal giaTriToiThieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_tao" , insertable = false, updatable = false)
    private LocalDateTime ngayTao;


}
