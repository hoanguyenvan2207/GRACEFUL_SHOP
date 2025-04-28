package com.example.datn_be.dto.khuyenmai;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SanPhamChiTietKhuyenMaiDTO {
    private Integer id;
    private String maAoDaiChiTiet;
    private String tenAoDai;
    private Double giaGoc;
    private Integer idKhuyenMai;
    private String kichThuoc;
    private String mauSac;

}
