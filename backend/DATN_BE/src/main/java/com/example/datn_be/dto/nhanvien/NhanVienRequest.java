package com.example.datn_be.dto.nhanvien;

import com.example.datn_be.entiy.NhanVien.VaiTro;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienRequest {
    private Integer id;

    private String maNhanVien;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String tenDangNhap;

    @NotBlank(message = "Họ và Tên không được để trống")
    @Pattern(regexp = ".*\\S.*", message = "Họ và Tên không hợp lệ")
    @Size(min = 1, message = "Họ và Tên không hợp lệ")
    private String hoVaTen;


    @NotNull(message = "Giới tính không được để trống")
    private Integer gioiTinh;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

//    @NotBlank(message = "Email không được để trống")
//    @Email(message = "Email không hợp lệ")
@NotBlank(message = "Email không được để trống")
@Pattern(regexp = ".*@.*", message = "Email phải chứa ký tự @")
    private String email;

    private String matKhau;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean trangThai;

    @NotNull(message = "Vai trò không được để trống")
    private VaiTro vaiTro;

}
