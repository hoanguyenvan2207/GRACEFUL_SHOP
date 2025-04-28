package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.NhaCungCapDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.NhaCungCapService;
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
@RequestMapping("/api/nha-cung-cap")
@CrossOrigin("*")
public class NhaCungCapController {

    @Autowired
    private NhaCungCapService nhaCungCapService;

    @GetMapping("/list/all")
    public ResponseEntity<List<NhaCungCapDTO>> getNhaCungCaps() {
        return ResponseEntity.ok(nhaCungCapService.findAllNhaCungCap());
    }

    @GetMapping("/list/enables")
    public ResponseEntity<List<NhaCungCapDTO>> getNhaCungCapsEn() {
        return ResponseEntity.ok(nhaCungCapService.getNhaCungCapsByTrangThaiTrue());
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<NhaCungCapDTO>> searchNhaCungCaps(@RequestParam(defaultValue = "0" ) int page,
                                                                    @RequestParam(defaultValue = "5") int size,
                                                                    @RequestParam(required = false) String keyword,
                                                                    @RequestParam(required = false) Boolean trangThai) {
        return ResponseEntity.ok(nhaCungCapService.searchNhaCungCaps(keyword, page, size, trangThai));
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<NhaCungCapDTO> getNhaCungCapByName(@PathVariable String ten){
        return ResponseEntity.ok(nhaCungCapService.getNhaCungCapByName(ten));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNhaCungCap(@Valid @RequestBody NhaCungCapDTO nhaCungCapDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            NhaCungCapDTO savedDTO = nhaCungCapService.addNhaCungCap(nhaCungCapDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNhaCungCap(@Valid @PathVariable int id, @RequestBody NhaCungCapDTO nhaCungCapDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            NhaCungCapDTO savedDTO = nhaCungCapService.updateNhaCungCap(id, nhaCungCapDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<NhaCungCapDTO> getNhaCungCapById(@PathVariable int id) {
        NhaCungCapDTO nhaCungCapDTO = nhaCungCapService.getNhaCungCapById(id);
        return nhaCungCapDTO != null ? ResponseEntity.ok(nhaCungCapDTO) : ResponseEntity.notFound().build();
    }
}

