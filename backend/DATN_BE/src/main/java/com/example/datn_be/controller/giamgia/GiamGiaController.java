package com.example.datn_be.controller.giamgia;

import com.example.datn_be.entiy.giam_gia.GiamGia;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.GiamGiaRepository;
import com.example.datn_be.service.giamgia.GiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/giam-gia")
public class GiamGiaController {
    @Autowired
    GiamGiaService giamGiaService;
    @Autowired
    GiamGiaRepository giamGiaRepository;

    @GetMapping("/loc")
    public ResponseEntity<Page<GiamGia>> timKiemGiamGia(
            @RequestParam(value = "keyq", required = false) String keyq,
            @RequestParam(value = "status", required = false) Boolean status,
            @RequestParam(value = "validity", required = false) Integer validity,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<GiamGia> result = giamGiaRepository.searchByKey(keyq, status, validity, pageable, startDate, endDate, pageSize);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/hien-thi")
    public Map<String, Object> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page
            , @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<GiamGia> pageResult = giamGiaService.getAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalPages", pageResult.getTotalPages());

        return response;
    }

    @PostMapping("/them")
    public ResponseEntity<?> themGiamGia(@RequestBody GiamGia giamGia) {
        try {
            giamGiaService.them(giamGia);
            return ResponseEntity.ok("Thêm giảm giá thành công!");
        } catch (DuplicateFieldException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", e.getFieldErrors()));
        }
    }

    @PutMapping("cap-nhat")
    public ResponseEntity<?> update(@RequestBody GiamGia giamGiaRequest) {
        GiamGia giamGia = giamGiaRepository.findById(giamGiaRequest.getId()).orElse(null);
        if (giamGia == null) {
            return ResponseEntity.ok("Cập nhật thất bại");
        }
        try {
            // Copy dữ liệu từ request vào đối tượng tìm được trong database
            giamGia.setTenGiamGia(giamGiaRequest.getTenGiamGia());
            giamGia.setMaGiamGia(giamGiaRequest.getMaGiamGia());
            giamGia.setGiaTriGiam(giamGiaRequest.getGiaTriGiam());
            giamGia.setToiDaGiamGia(giamGiaRequest.getToiDaGiamGia());
            giamGia.setGiaTriToiThieu(giamGiaRequest.getGiaTriToiThieu());
            giamGia.setNgayBatDau(giamGiaRequest.getNgayBatDau());
            giamGia.setNgayKetThuc(giamGiaRequest.getNgayKetThuc());
            giamGia.setTrangThai(giamGiaRequest.getTrangThai());
            giamGia.setMoTa(giamGiaRequest.getMoTa());
            giamGia.setSoLuong(giamGiaRequest.getSoLuong());

            giamGiaService.capNhat(giamGia);
            return ResponseEntity.ok("Cập nhật thành công");
        } catch (DuplicateFieldException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", e.getFieldErrors()));
        }
    }
}


