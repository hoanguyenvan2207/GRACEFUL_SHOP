package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatLieuDTO {
    private Integer id;

    @NotBlank(message = "Tên chất liệu không được để trống.")
    @Size(min = 3, max = 100, message = "Tên chất liệu phải có từ 3 đến 100 ký tự.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên chất liệu chỉ được chứa chữ cái và khoảng trắng.")
    private String tenChatLieu;

    @NotBlank(message = "Mô tả không được để trống.")
    @Size(min = 5, max = 500, message = "Mô tả phải có từ 3 đến 500 ký tự.")
    private String moTa;

    @NotNull(message = "Trạng thái không được để trống.")
    private Boolean trangThai;

    private ZonedDateTime ngayTao;
}
