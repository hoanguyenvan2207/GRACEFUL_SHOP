package com.example.datn_be.controller.banhang;

import com.example.datn_be.entiy.HoaDonBH;
import com.example.datn_be.entiy.HoaDonChiTietBH;
import com.example.datn_be.repository.HoaDonBHRepo;
import com.example.datn_be.repository.HoaDonChiTietRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {

    @Autowired
    HoaDonBHRepo hoaDonRepo;

    @Autowired
    HoaDonChiTietRepo hoaDonChiTietRepo;

    @GetMapping("/hien-thi-phan-trang")
    public ResponseEntity<?> hienThiHoaDonPhanTrang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Boolean hinhThucMuaHang) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());

        try {
            if (searchQuery != null && !searchQuery.isEmpty()) {
                if (hinhThucMuaHang != null) {
                    Page<HoaDonBH> result = hoaDonRepo.timKiemHoaDonTheoHinhThuc(searchQuery, hinhThucMuaHang, pageable);
                    return ResponseEntity.ok(result);
                } else {
                    Page<HoaDonBH> result = hoaDonRepo.timKiemHoaDon(searchQuery, pageable);
                    return ResponseEntity.ok(result);
                }
            } else if (fromDate != null && toDate != null && trangThai != null && !trangThai.isEmpty()) {
                Date startDate = sdf.parse(fromDate);
                Date endDate = sdf.parse(toDate);
                if (endDate.before(startDate)) {
                    return ResponseEntity.badRequest()
                            .body("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.");
                }
                // Điều chỉnh endDate về cuối ngày
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();

                if (hinhThucMuaHang != null) {
                    Page<HoaDonBH> result = hoaDonRepo.findByTrangThaiAndNgayTaoBetweenAndHinhThucMuaHang(trangThai, startDate, endDate, hinhThucMuaHang, pageable);
                    return ResponseEntity.ok(result);
                } else {
                    Page<HoaDonBH> result = hoaDonRepo.findByTrangThaiAndNgayTaoBetween(trangThai, startDate, endDate, pageable);
                    return ResponseEntity.ok(result);
                }
            } else if (fromDate != null && toDate != null) {
                Date startDate = sdf.parse(fromDate);
                Date endDate = sdf.parse(toDate);
                if (endDate.before(startDate)) {
                    return ResponseEntity.badRequest()
                            .body("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.");
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                endDate = cal.getTime();

                if (hinhThucMuaHang != null) {
                    Page<HoaDonBH> result = hoaDonRepo.findByNgayTaoBetweenAndHinhThucMuaHang(startDate, endDate, hinhThucMuaHang, pageable);
                    return ResponseEntity.ok(result);
                } else {
                    Page<HoaDonBH> result = hoaDonRepo.findByNgayTaoBetween(startDate, endDate, pageable);
                    return ResponseEntity.ok(result);
                }
            } else if (trangThai != null && !trangThai.isEmpty()) {
                if (hinhThucMuaHang != null) {
                    Page<HoaDonBH> result = hoaDonRepo.findByTrangThaiAndHinhThucMuaHang(trangThai, hinhThucMuaHang, pageable);
                    return ResponseEntity.ok(result);
                } else {
                    Page<HoaDonBH> result = hoaDonRepo.findByTrangThai(trangThai, pageable);
                    return ResponseEntity.ok(result);
                }
            } else {
                if (hinhThucMuaHang != null) {
                    Page<HoaDonBH> result = hoaDonRepo.findByHinhThucMuaHang(hinhThucMuaHang, pageable);
                    return ResponseEntity.ok(result);
                } else {
                    Page<HoaDonBH> result = hoaDonRepo.findAll(pageable);
                    return ResponseEntity.ok(result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (hinhThucMuaHang != null) {
                Page<HoaDonBH> result = hoaDonRepo.findByHinhThucMuaHang(hinhThucMuaHang, pageable);
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.ok(hoaDonRepo.findAll(pageable));
            }
        }
    }


    @GetMapping("/chi-tiet")
    public List<HoaDonChiTietBH> getHoaDonChiTietByHoaDonId(@RequestParam("idHoaDon") Integer idHoaDon) {
        return hoaDonChiTietRepo.findByHoaDonId(idHoaDon);
    }

    @GetMapping("/detail")
    public HoaDonBH getHoaDonById(@RequestParam("idHoaDon") Integer idHoaDon) {
        return hoaDonRepo.findById(idHoaDon).orElse(null);
    }
}