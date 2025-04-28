package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.SanPhamDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin("*")
public class SanPhamController {

    private final SanPhamService sanPhamService;

    @Autowired
    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping("/list/search-and-filter")
    public Page<SanPhamDTO> filterSanPham(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false) List<String> tenLoaiAoDai,
            @RequestParam(required = false) List<String> tenChatLieu,
            @RequestParam(required = false) List<String> tenTaAo,
            @RequestParam(required = false) List<String> tenNhaCungCap,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false, defaultValue = "") String sortDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        tenLoaiAoDai = (tenLoaiAoDai != null && !tenLoaiAoDai.isEmpty()) ? tenLoaiAoDai : null;
        tenChatLieu = (tenChatLieu != null && !tenChatLieu.isEmpty()) ? tenChatLieu : null;
        tenTaAo = (tenTaAo != null && !tenTaAo.isEmpty()) ? tenTaAo : null;
        tenNhaCungCap = (tenNhaCungCap != null && !tenNhaCungCap.isEmpty()) ? tenNhaCungCap : null;

        return sanPhamService.searchAndFilterSanPham
                (keyword,
                tenLoaiAoDai,
                tenChatLieu, tenTaAo,
                tenNhaCungCap, trangThai,
                sortDate,
                page,
                size);
    }


    @GetMapping("/list/filter-products")
    public Page<SanPhamDTO> filterProductsDesc(
            @RequestParam(required = false) List<String> listLoaiAoDai,
            @RequestParam(required = false) List<String> listChatLieu,
            @RequestParam(required = false) List<String> listTaAo,
            @RequestParam(required = false) List<String> listNhaCungCap,
            @RequestParam(required = false) List<String> listMauSac,
            @RequestParam(required = false) List<String> listKichThuoc,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "") String sortPrice,
            @RequestParam(required = false, defaultValue = "") String sortDate,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        if (listLoaiAoDai != null && listLoaiAoDai.size() == 1 && listLoaiAoDai.get(0).trim().isEmpty()) {
            listLoaiAoDai = null;
        }
        if (listChatLieu != null && listChatLieu.size() == 1 && listChatLieu.get(0).trim().isEmpty()) {
            listChatLieu = null;
        }
        if (listTaAo != null && listTaAo.size() == 1 && listTaAo.get(0).trim().isEmpty()) {
            listTaAo = null;
        }
        if (listNhaCungCap != null && listNhaCungCap.size() == 1 && listNhaCungCap.get(0).trim().isEmpty()) {
            listNhaCungCap = null;
        }
        if (listMauSac != null && listMauSac.size() == 1 && listMauSac.get(0).trim().isEmpty()) {
            listMauSac = null;
        }
        if (listKichThuoc != null && listKichThuoc.size() == 1 && listKichThuoc.get(0).trim().isEmpty()) {
            listKichThuoc = null;
        }

        return sanPhamService.filterSanPham(
                listLoaiAoDai,
                listChatLieu,
                listTaAo,
                listNhaCungCap,
                listMauSac,
                listKichThuoc,
                minPrice,
                maxPrice,
                keyword,
                sortPrice,
                sortDate,
                page,
                size
        );
    }

    @GetMapping("/top-sell")
    public ResponseEntity<List<SanPhamDTO>> getTopSell() {
        return ResponseEntity.ok(sanPhamService.getTop5SanPhamBanChay());
    }

    @GetMapping("/list/enables")
    public ResponseEntity<List<SanPhamDTO>> getSanPhamsEn() {
        return ResponseEntity.ok(sanPhamService.getSanPhamByTrangThaiTrue());
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<SanPhamDTO>> getSanPhamAll() {
        return ResponseEntity.ok(sanPhamService.getSanPhamAll());
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<SanPhamDTO> getSanPhamById(@PathVariable int id) {
        SanPhamDTO sanPhamDTO = sanPhamService.getSanPhamById(id);
        if (sanPhamDTO != null) {
            return ResponseEntity.ok(sanPhamDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-ma/{ma}")
    public ResponseEntity<SanPhamDTO> getSanPhamByMa(@PathVariable String ma) {
        SanPhamDTO sanPhamDTO = sanPhamService.getSanPhamByCode(ma);
        return ResponseEntity.ok(sanPhamDTO);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamDTO sanPhamDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            SanPhamDTO savedDTO = sanPhamService.addSanPham(sanPhamDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PostMapping("/addSanPhams")
    public ResponseEntity<?> addSanPhams(@Valid @RequestBody List<SanPhamDTO> sanPhamDTOList, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            List<SanPhamDTO> savedDTOList = sanPhamService.addSanPhams(sanPhamDTOList);
            return ResponseEntity.ok(savedDTOList);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSanPham(@Valid @PathVariable int id, @RequestBody SanPhamDTO sanPhamDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            SanPhamDTO savedDTO = sanPhamService.updateSanPham(id, sanPhamDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }
}
