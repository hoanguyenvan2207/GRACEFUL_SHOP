package com.example.datn_be.entiy.khach_hang;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
public class KhachHangTk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khach_hang",insertable = false, updatable = false)
    private String maKhachHang;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "mat_khau",length = 100, nullable = false)
    private String matKhau;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDate ngayTao;


    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL,orphanRemoval = true,
               fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<DiaChiTk> diaChis;
}
