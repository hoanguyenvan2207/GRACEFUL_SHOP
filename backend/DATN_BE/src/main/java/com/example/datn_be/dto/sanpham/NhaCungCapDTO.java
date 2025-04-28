package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhaCungCapDTO {
    private Integer id;

    @NotBlank(message = "Tên nhà cung cấp không được để trống.")
    @Size(min = 3, max = 100, message = "Tên nhà cung cấp phải có từ 3 đến 100 ký tự.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên nhà cung cấp chỉ được chứa chữ cái và khoảng trắng.")
    private String tenNhaCungCap;

    @NotBlank(message = "Địa chỉ không được để trống.")
    @Size(min = 3, max = 100, message = "Địa chỉ phải có từ 3 đến 100 ký tự.")
    private String diaChi;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^(0[0-9]{9,10})$", message = "Số điện thoại không đúng định dạng.")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không đúng định dạng.")
    private String email;

    private Boolean trangThai;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 5, max = 500, message = "Mô tả phải có từ 5 đến 500 ký tự.")
    private String moTa;

    private ZonedDateTime ngayTao;
}
