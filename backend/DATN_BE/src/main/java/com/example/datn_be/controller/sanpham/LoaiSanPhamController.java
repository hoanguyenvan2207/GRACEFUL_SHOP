package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.LoaiSanPhamDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.LoaiSanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loai-san-pham")
@CrossOrigin("*")
public class LoaiSanPhamController {

    private final LoaiSanPhamService loaiSanPhamService;

    @Autowired
    public LoaiSanPhamController(LoaiSanPhamService loaiSanPhamService) {
        this.loaiSanPhamService = loaiSanPhamService;
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<LoaiSanPhamDTO>> getLoaiSanPhams() {
        return ResponseEntity.ok(loaiSanPhamService.findAllLoaiSanPhams());
    }

    @GetMapping("list/enables")
    public ResponseEntity<List<LoaiSanPhamDTO>> getLoaiSanPhamsEn() {
        return ResponseEntity.ok(loaiSanPhamService.getLoaiSanPhamsByTrangThaiTrue());
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<LoaiSanPhamDTO> getLoaiSanPhamByName(@PathVariable String ten){
        return ResponseEntity.ok(loaiSanPhamService.getLoaiSanPhamByName(ten));
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<LoaiSanPhamDTO>> searchLoaiSanPhams(@RequestParam(defaultValue = "0" ) int page,
                                                                      @RequestParam(defaultValue = "5") int size,
                                                                      @RequestParam(required = false) String keyword,
                                                                      @RequestParam(required = false) Boolean trangThai) {
        return ResponseEntity.ok(loaiSanPhamService.searchLoaiSanPhams(keyword, page, size, trangThai));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLoaiSanPham(@Valid @RequestBody LoaiSanPhamDTO loaiSanPhamDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            LoaiSanPhamDTO savedDTO = loaiSanPhamService.addLoaiSanPham(loaiSanPhamDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLoaiSanPham(@Valid @PathVariable int id, @RequestBody LoaiSanPhamDTO loaiSanPhamDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            LoaiSanPhamDTO savedDTO = loaiSanPhamService.updateLoaiSanPham(id, loaiSanPhamDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<LoaiSanPhamDTO> getLoaiSanPhamById(@PathVariable int id) {
        LoaiSanPhamDTO loaiSanPhamDTO = loaiSanPhamService.getLoaiSanPhamById(id);
        return loaiSanPhamDTO != null ? ResponseEntity.ok(loaiSanPhamDTO) : ResponseEntity.notFound().build();
    }
}

