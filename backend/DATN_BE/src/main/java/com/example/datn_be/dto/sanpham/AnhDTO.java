package com.example.datn_be.dto.sanpham;

import lombok.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnhDTO {

    private Integer id;

    private String anhUrl;

    private ZonedDateTime ngayTao;

    private Boolean trangThai;

    private Integer sanPhamId;
}