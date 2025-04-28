package com.example.datn_be.entiy.giam_gia;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "anh")
public class AnhKM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "anh_url")
    private String anhUrl;

    @Column(name = "ngay_tao")
    private ZonedDateTime ngayTao;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "id_ao_dai", referencedColumnName = "id")
    private SanPhamKM aoDai;

    public AnhKM(String anhUrl) {
    }
}