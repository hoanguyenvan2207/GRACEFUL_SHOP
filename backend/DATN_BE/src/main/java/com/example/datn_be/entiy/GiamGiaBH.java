package com.example.datn_be.entiy;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "giam_gia")
public class GiamGiaBH {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giam_gia", insertable = false, updatable = false)
    private String maGiamGia;

    @Column(name = "ten_giam_gia")
    private String tenGiamGia;

    @Column(name = "loai_giam_gia") // loại 0 giảm theo tiền, 1 theo %
    private Integer loaiGiamGia;

    @Column(name = "gia_tri_giam")
    private Integer giaTriGiam;

    @Column(name = "toi_da_giam_gia")
    private Double toiDaGiamGia;

    @Column(name = "gia_tri_toi_thieu")
    private Double giaTriToiThieu;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "ngay_bat_dau")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private Date ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

}