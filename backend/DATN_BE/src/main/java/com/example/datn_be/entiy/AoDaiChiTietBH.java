package com.example.datn_be.entiy;

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
@Table(name = "ao_dai_chi_tiet")
public class AoDaiChiTietBH {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_ao_dai_chi_tiet",insertable = false, updatable = false)
    private String maAoDaiChiTiet;

    @ManyToOne
    @JoinColumn(name = "id_ao_dai")
    private AoDaiBH aoDai;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac")
    private MauSacBH mauSac;

    @Column(name = "gia_ban_ban_dau")
    private Double giaGoc;

    @Column(name = "gia_ban")
    private Double giaBan;

    @Column(name = "anh_url")
    private String anhUrl;

    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc")
    private KichThuocBH kichThuoc;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

