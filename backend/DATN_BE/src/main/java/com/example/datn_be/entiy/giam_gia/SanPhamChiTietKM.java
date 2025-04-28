package com.example.datn_be.entiy.giam_gia;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ao_dai_chi_tiet")
public class SanPhamChiTietKM {
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
    private SanPhamKM aoDai;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", referencedColumnName = "id")
    private MauSacKM mauSac;

    @ManyToOne
    @JoinColumn(name = "id_kich_thuoc", referencedColumnName = "id")
    private KichThuocKM kichThuoc;

    @ManyToOne
    @JoinColumn(name = "id_khuyen_mai", referencedColumnName = "id")
    @JsonBackReference // Ngăn chặn vòng lặp
    private KhuyenMai khuyenMai;


}