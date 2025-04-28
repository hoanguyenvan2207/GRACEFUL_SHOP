package com.example.datn_be.dto.khachhang.response;


import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;

public interface KhachHangResponse {

    Integer getId();

    String getMaKhachHang();

    String getHoTen();

    Boolean getGioiTinh();

    LocalDate getNgaySinh();

    String getEmail();

    String getSoDienThoai();


    LocalDate getNgayTao();

    String getDiaChis();

}
