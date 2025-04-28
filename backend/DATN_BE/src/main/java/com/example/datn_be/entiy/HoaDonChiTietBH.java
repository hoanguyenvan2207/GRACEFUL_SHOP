package com.example.datn_be.entiy;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTietBH {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoa_don_chi_tiet", insertable = false, updatable = false)
    private String maHDCT;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don")
    @JsonBackReference
    private HoaDonBH hoaDon;

    @ManyToOne
    @JoinColumn(name = "id_ao_dai_chi_tiet")
    private AoDaiChiTietBH aoDaiChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "don_gia")
    private Double giaBan;
}

