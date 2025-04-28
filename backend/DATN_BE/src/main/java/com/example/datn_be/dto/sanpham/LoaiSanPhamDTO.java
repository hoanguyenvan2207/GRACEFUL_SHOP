package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiSanPhamDTO {
    private Integer id;

    @NotBlank(message = "Tên loại áo không được để trống.")
    @Size(min = 3, max = 100, message = "Tên loại áo phải có từ 3 đến 100 ký tự.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên loại áo chỉ được chứa chữ cái và khoảng trắng.")
    private String tenLoaiAoDai;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 5, max = 500, message = "Mô tả phải có từ 3 đến 500 ký tự.")
    private String moTa;

    private Boolean trangThai;

    private ZonedDateTime ngayTao;
}

