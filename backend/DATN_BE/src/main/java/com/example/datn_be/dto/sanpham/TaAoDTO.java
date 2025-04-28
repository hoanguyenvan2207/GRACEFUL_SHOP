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
public class TaAoDTO {
    private Integer id;

    @NotBlank(message = "Tên tà áo không được để trống.")
    @Size(min = 3, max = 100, message = "Tên tà áo phải có từ 3 đến 100 ký tự.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên tà áo chỉ được chứa chữ cái và khoảng trắng.")
    private String tenTaAo;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 5, max = 500, message = "Mô tả phải có từ 3 đến 500 ký tự.")
    private String moTa;

    private Boolean trangThai;

    private ZonedDateTime ngayTao;
}

