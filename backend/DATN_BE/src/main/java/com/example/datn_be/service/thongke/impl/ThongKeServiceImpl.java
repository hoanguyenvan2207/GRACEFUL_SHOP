package com.example.datn_be.service.thongke.impl;

import com.example.datn_be.repository.ThongKeRepo;
import com.example.datn_be.service.thongke.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {

    @Autowired
    private final ThongKeRepo thongKeRepo;

    @Override
    public ResponseEntity<?> thongKeTop5BanChay() {
        return new ResponseEntity<>(thongKeRepo.getTop5AoDaiBanChay(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> thongKeDoanhThuTheoNgay(LocalDate ngay) {
        return new ResponseEntity<>(thongKeRepo.getDoanhThuTrongNgay(ngay), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> thongKeDoanhThuTrongNgay() {
        LocalDate homNay = LocalDate.now();
        return new ResponseEntity<>(thongKeRepo.getDoanhThuTheoNgay(homNay), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> thongKeDoanhThuTheoThang(Integer thang, Integer nam) {
        return new ResponseEntity<>(thongKeRepo.getDoanhThuTheoThang(thang, nam), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> thongKeDoanhThuTheoNam(Integer nam) {
        return new ResponseEntity<>(thongKeRepo.getDoanhThuTheoNam(nam), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> thongKeDoanhThuTheoKhoangThoiGian(LocalDate startDate, LocalDate endDate) {
        return new ResponseEntity<>(thongKeRepo.getDoanhThuTheoKhoangThoiGian(startDate, endDate), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> getLoaiAoDaiBanNhieu() {
        return new ResponseEntity<>(thongKeRepo.getLoaiAoDaiBanNhieu(), HttpStatus.OK);
    }


}
