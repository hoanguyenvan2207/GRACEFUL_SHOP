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
@Table(name = "ao_dai")
public class AoDaiBH {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_ao_dai")
    private String maAoDai;

    @Column(name = "ten_ao_dai")
    private String tenAoDai;

    @ManyToOne
    @JoinColumn(name = "id_loai_ao_dai")
    private LoaiAoDaiBH loaiAoDai;

    @ManyToOne
    @JoinColumn(name = "id_ta_ao")
    private TaAoBH taAo;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieuBH chatLieu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}

