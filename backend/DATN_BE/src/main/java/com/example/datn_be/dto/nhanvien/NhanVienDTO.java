package com.example.datn_be.dto.nhanvien;




import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhanVienDTO {
    private String maNhanVien;
    private String hoVaTen;
    private LocalDate ngaySinh;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String tenDangNhap;
    private String vaiTro;
}

