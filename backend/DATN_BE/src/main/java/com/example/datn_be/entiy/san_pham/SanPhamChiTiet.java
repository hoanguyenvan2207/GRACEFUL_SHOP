package com.example.datn_be.entiy.san_pham;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ao_dai_chi_tiet")
public class SanPhamChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_ao_dai_chi_tiet", insertable = false, updatable = false, unique = true)
    private String maAoDaiChiTiet;

    @Column(name = "gia_ban_ban_dau")
    private Double giaGoc;

    @Column(name = "gia_ban")
    private Double giaBan;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "anh_url")
    private String anhUrl;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private ZonedDateTime ngayTao;

    @ManyToOne
    @JoinColumn(name = "id_ao_dai", referencedColumnName = "id")
    private SanPham aoDai;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", referencedColumnName = "id")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc", referencedColumnName = "id")
    private KichThuoc kichThuoc;

    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai", referencedColumnName = "id")
    private KhuyenMaiSP khuyenMaiSP;
}
