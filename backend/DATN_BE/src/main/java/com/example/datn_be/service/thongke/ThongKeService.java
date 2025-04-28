package com.example.datn_be.service.thongke;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface ThongKeService {
    ResponseEntity<?> thongKeTop5BanChay();

    ResponseEntity<?> thongKeDoanhThuTheoNgay(LocalDate ngay);

    ResponseEntity<?> thongKeDoanhThuTrongNgay();

    ResponseEntity<?> thongKeDoanhThuTheoThang(Integer thang, Integer nam);

    ResponseEntity<?> thongKeDoanhThuTheoNam(Integer nam);

    ResponseEntity<?> thongKeDoanhThuTheoKhoangThoiGian(LocalDate startDate, LocalDate endDate);

    ResponseEntity<?> getLoaiAoDaiBanNhieu();
}
