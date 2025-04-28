package com.example.datn_be.controller.thongke;

import com.example.datn_be.service.thongke.impl.ThongKeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("api/admin/thong-ke")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ThongKeController {

    @Autowired
    private final ThongKeServiceImpl thongKeService;

    @GetMapping("hien-thi-top-5")
    public ResponseEntity<?> hienThiTop5() {
        return new ResponseEntity<>(thongKeService.thongKeTop5BanChay(), HttpStatus.OK);
    }

    @GetMapping("hien-thi-doanh-thu-theo-ngay")
    public ResponseEntity<?> hienThiDoanhThuTheoNgay(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                     LocalDate ngay) {
        return new ResponseEntity<>(thongKeService.thongKeDoanhThuTheoNgay(ngay), HttpStatus.OK);
    }

    @GetMapping("hien-thi-doanh-thu-trong-ngay")
    public ResponseEntity<?> hienThiDoanhThuTrongNgay() {
        return new ResponseEntity<>(thongKeService.thongKeDoanhThuTrongNgay(), HttpStatus.OK);
    }

    @GetMapping("hien-thi-doanh-thu-theo-thang")
    public ResponseEntity<?> hienThiDoanhThuTheoThang(@RequestParam Integer thang, @RequestParam Integer nam) {
        return new ResponseEntity<>(thongKeService.thongKeDoanhThuTheoThang(thang, nam), HttpStatus.OK);
    }

    @GetMapping("hien-thi-doanh-thu-theo-nam")
    public ResponseEntity<?> hienThiDoanhThuTheoNam(@RequestParam Integer nam) {
        return new ResponseEntity<>(thongKeService.thongKeDoanhThuTheoNam(nam), HttpStatus.OK);
    }

    @GetMapping("hien-thi-doanh-thu-theo-khoang-thoi-gian")
    public ResponseEntity<?> hienThiDoanhThuTheoThang(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return new ResponseEntity<>(thongKeService.thongKeDoanhThuTheoKhoangThoiGian(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("hien-thi-loai-ao-dai-ban-nhieu")
    public ResponseEntity<?> hienThiLoaiAoDaiBanNhieu() {
        return new ResponseEntity<>(thongKeService.getLoaiAoDaiBanNhieu(), HttpStatus.OK);
    }

}
