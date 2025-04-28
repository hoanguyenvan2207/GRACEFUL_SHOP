package com.example.datn_be.entiy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "hoa_don")
public class HoaDonBH {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hoa_don", insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private String maHoaDon;

    @Column(name = "phuong_thuc_thanh_toan")
    private Boolean phuongThucThanhToan;

    @Column(name = "phuong_thuc_thanh_toan_online") // 1 chuyển khoản (sau này phát triển thành vnpay) 0 shipcod (thu hộ == phí ship)
    private Boolean phuongThucThanhToanOnline;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "tien_truoc_giam")
    private Double tienTruocGiam;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHangBH khachHang;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVienBH nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_giam_gia")
    private GiamGiaBH giamGia;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<HoaDonChiTietBH> chiTietHoaDon;

}
