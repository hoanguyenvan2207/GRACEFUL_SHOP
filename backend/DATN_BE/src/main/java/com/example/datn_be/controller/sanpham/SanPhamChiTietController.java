package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.MauSacDTO;
import com.example.datn_be.dto.sanpham.SanPhamChiTietDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.SanPhamChiTietService;
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
@RequestMapping("/api/san-pham-chi-tiet")
@CrossOrigin("*")
public class SanPhamChiTietController {

    private final SanPhamChiTietService sanPhamChiTietService;

    @Autowired
    public SanPhamChiTietController(SanPhamChiTietService sanPhamChiTietService) {
        this.sanPhamChiTietService = sanPhamChiTietService;
    }

    @GetMapping("/list/all")
    public List<SanPhamChiTietDTO> findAll(){
        return sanPhamChiTietService.findAllSanPhamChiTiet();
    }

    @GetMapping("/list-by-san-pham")
    public List<SanPhamChiTietDTO> findSanPhamChiTietBySanPhamId(@RequestParam Integer aoDaiId){
        return sanPhamChiTietService.findSanPhamChiTietBySanPhamId(aoDaiId);
    }

    @GetMapping("/page/filter")
    public Page<SanPhamChiTietDTO> filterSanPhamChiTiet(
            @RequestParam(required = false, defaultValue = "" ) String keyword,
            @RequestParam(required = false) List<String> listTenAoDai,
            @RequestParam(required = false) List<String> listMauSac,
            @RequestParam(required = false) List<String> listKichThuoc,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        listTenAoDai = (listTenAoDai != null && !listTenAoDai.isEmpty()) ? listTenAoDai : null;
        listMauSac = (listMauSac != null && !listMauSac.isEmpty()) ? listMauSac : null;
        listKichThuoc = (listKichThuoc != null && !listKichThuoc.isEmpty()) ? listKichThuoc : null;
        return sanPhamChiTietService.filterSanPhamChiTiet(keyword, listTenAoDai, listMauSac, listKichThuoc, trangThai, sort, page, size);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSanPhamChiTiet(@Valid @RequestBody SanPhamChiTietDTO sanPhamChiTietDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            SanPhamChiTietDTO savedSanPhamChiTiet = sanPhamChiTietService.addSanPhamChiTiet(sanPhamChiTietDTO);
            return ResponseEntity.ok(savedSanPhamChiTiet);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PostMapping("/adds")
    public ResponseEntity<?> addSanPhamChiTiets(@Valid @RequestBody List<SanPhamChiTietDTO> sanPhamChiTietDTOs, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            List<SanPhamChiTietDTO> savedSanPhamChiTiets = sanPhamChiTietService.addSanPhamChiTiets(sanPhamChiTietDTOs);
            return ResponseEntity.ok(savedSanPhamChiTiets);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSanPhamChiTiet(@Valid @PathVariable int id, @RequestBody SanPhamChiTietDTO sanPhamChiTietDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            SanPhamChiTietDTO savedSanPhamChiTiet = sanPhamChiTietService.updateSanPhamChiTiet(id, sanPhamChiTietDTO);
            return ResponseEntity.ok(savedSanPhamChiTiet);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<SanPhamChiTietDTO> getSanPhamChiTietById(@PathVariable int id) {
        SanPhamChiTietDTO sanPhamChiTiet = sanPhamChiTietService.getSanPhamChiTietById(id);
        if (sanPhamChiTiet != null) {
            return ResponseEntity.ok(sanPhamChiTiet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

