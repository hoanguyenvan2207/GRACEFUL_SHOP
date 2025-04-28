package com.example.datn_be.dto.sanpham;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamDTO {
    private Integer id;

    private String maAoDai;

    @NotBlank(message = "Tên áo dài không được để trống.")
    @Size(min = 3, max = 100, message = "Tên áo dài phải có từ 3 đến 100 ký tự.")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên áo dài chỉ được chứa chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt")
    private String tenAoDai;

    @NotBlank(message = "Mô tả không được để trống.")
    private String moTa;

    private String linkYoutube;

    private Boolean trangThai;

    private ZonedDateTime ngayTao;

    @NotNull(message = "Loại áo dài không được để trống.")
    private Integer loaiAoDaiId;

    private String tenLoaiAoDai;

    @NotNull(message = "Chất liệu không được để trống.")
    private Integer chatLieuId;

    private String tenChatLieu;

    @NotNull(message = "Tà áo không được để trống.")
    private Integer taAoId;

    private String tenTaAo;

    @NotNull(message = "Nhà cung cấp không được để trống.")
    private Integer nhaCungCapId;

    private String tenNhaCungCap;

    @NotEmpty(message = "Danh sách ảnh không được để trống.")
    private List<String> anhList;

    private List<SanPhamChiTietDTO> sanPhamChiTietList;
}
