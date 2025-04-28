package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.KichThuocDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.KichThuocService;
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
@RequestMapping("/api/kich-thuoc")
@CrossOrigin("*")
public class KichThuocController {

    private final KichThuocService kichThuocService;

    @Autowired
    public KichThuocController(KichThuocService kichThuocService) {
        this.kichThuocService = kichThuocService;
    }

    @GetMapping("list/enables")
    public ResponseEntity<List<KichThuocDTO>> findKichThuocsEn() {
        return ResponseEntity.ok(kichThuocService.getKichThuocsByTrangThaiTrue());
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<KichThuocDTO>> searchKichThuocs(@RequestParam(defaultValue = "0" ) int page,
                                                                  @RequestParam(defaultValue = "5") int size,
                                                                  @RequestParam(required = false) String keyword,
                                                                  @RequestParam(required = false) Boolean trangThai) {
        return ResponseEntity.ok(kichThuocService.searchKichThuocs(keyword, page, size, trangThai));
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<KichThuocDTO> getKichThuocByName(@PathVariable String ten){
        return ResponseEntity.ok(kichThuocService.getKichThuocByTen(ten));
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<KichThuocDTO>> findKichThuocs() {
        return ResponseEntity.ok(kichThuocService.findAllKichThuoc());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addKichThuoc(@Valid @RequestBody KichThuocDTO kichThuocDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            KichThuocDTO savedDTO = kichThuocService.addKichThuoc(kichThuocDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateKichThuoc(@Valid @PathVariable int id, @RequestBody KichThuocDTO kichThuocDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
       try {
            KichThuocDTO savedDTO = kichThuocService.updateKichThuoc(id, kichThuocDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<KichThuocDTO> getKichThuocById(@PathVariable int id) {
        KichThuocDTO kichThuocDTO = kichThuocService.getKichThuocById(id);
        return kichThuocDTO != null ? ResponseEntity.ok(kichThuocDTO) : ResponseEntity.notFound().build();
    }
}

