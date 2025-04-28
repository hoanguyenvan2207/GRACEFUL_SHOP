package com.example.datn_be.entiy.khach_hang;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ao_dai")
public class AoDaiTk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_ao_dai",insertable = false, updatable = false)
    private String maAoDai;

    @Column(name = "ten_ao_dai")
    private String tenAoDai;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @ManyToOne
    @JoinColumn(name = "id_loai_ao_dai", referencedColumnName = "id")
    private LoaiAoDaiTk loaiAoDai;

    @ManyToOne
    @JoinColumn(name = "id_nha_cung_cap", referencedColumnName = "id")
    private NhaCungCapTk nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "id_ta_ao", referencedColumnName = "id")
    private TaAoTk taAo;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu", referencedColumnName = "id")
    private ChatLieuTk chatLieu;
}
