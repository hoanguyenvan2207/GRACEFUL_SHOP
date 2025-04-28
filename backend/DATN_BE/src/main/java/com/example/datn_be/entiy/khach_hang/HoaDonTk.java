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
@Table(name = "hoa_don")
public class HoaDonTk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoa_don",insertable = false, updatable = false)
    private String maHoaDon;

    @Column(name = "phuong_thuc_thanh_toan")
    private Boolean phuongThucThanhToan;

    @Column(name = "tong_tien")
    private Double tongTien;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "phi_giao_hang")
    private Double phiGiaoHang;

    @Column(name = "hinh_thuc_mua_hang")
    private Boolean hinhThucMuaHang;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang",referencedColumnName = "id")
    private KhachHangTk khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id")
    private NhanVienTk nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_giam_gia",referencedColumnName = "id")
    private GiamGiaTk giamGia;
}
