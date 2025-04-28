package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KichThuocDTO {
    private Integer id;

    @NotBlank(message = "Tên kích thước không được để trống.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên chất liệu chỉ được chứa chữ cái và khoảng trắng.")
    private String tenKichThuoc;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 3, max = 500, message = "Mô tả phải có từ 3 đến 500 ký tự.")
    private String moTa;

    private Boolean trangThai;

    private ZonedDateTime ngayTao;
}

