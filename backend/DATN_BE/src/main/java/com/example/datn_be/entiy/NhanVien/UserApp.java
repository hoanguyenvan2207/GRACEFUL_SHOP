package com.example.datn_be.entiy.NhanVien;

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
@Table(name = "nhan_vien")

public class UserApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_dang_nhap", nullable = false)
    private String username;

    @Column(name = "mat_khau", nullable = false)
    private String matKhau;
    @Column(name = "mat_khau_tam_thoi", nullable = false)
    private Integer mat_khau_tam_thoi =1;
    @Column(name = "id_vai_tro", nullable = false)
    private String role;
}

