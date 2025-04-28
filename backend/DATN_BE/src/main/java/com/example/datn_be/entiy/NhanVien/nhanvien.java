package com.example.datn_be.entiy.NhanVien;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "nhan_vien")
public class nhanvien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "ma_nhan_vien", insertable = false, updatable = false)// Ánh xạ cột ma_nhan_vien nhưng không cần lưu giá trị vào nó
    private String maNhanVien;
    @Column(name = "ten_dang_nhap")
    private String tenDangNhap;
    @Column(name = "ho_va_ten")
    private String hoVaTen;
    @Column(name = "gioi_tinh")
    private Integer gioiTinh;
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;
    @Column(name = "email")
    private String email;
    @Column(name = "mat_khau")
    private String matKhau;
    @Column(name = "so_dien_thoai")
    private String soDienThoai;
    @Column(name = "dia_chi")
    private String diaChi;
    //@JsonIgnore // Bỏ qua khi trả về JSON
    @Column(name = "trang_thai")
    private Boolean trangThai;
    @Column(name = "mat_khau_tam_thoi", nullable = false)
    private Integer mat_khau_tam_thoi = 0;
    @ManyToOne
    @JoinColumn(name = "id_vai_tro",referencedColumnName = "id")
    private VaiTro vaiTro;

}