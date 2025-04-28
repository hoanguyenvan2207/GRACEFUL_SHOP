package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.MauSacDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.MauSacService;
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
@RequestMapping("/api/mau-sac")
@CrossOrigin("*")
public class MauSacController {

    private final MauSacService mauSacService;

    @Autowired
    public MauSacController(MauSacService mauSacService) {
        this.mauSacService = mauSacService;
    }

    @GetMapping("/list/enables")
    public ResponseEntity<List<MauSacDTO>> getMauSacsEn() {
        return ResponseEntity.ok(mauSacService.getMauSacsByTrangThaiTrue());
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<MauSacDTO>> searchMauSacs(@RequestParam(defaultValue = "0" ) int page,
                                                            @RequestParam(defaultValue = "5") int size,
                                                            @RequestParam(required = false) String keyword,
                                                            @RequestParam(required = false) Boolean trangThai) {
        return ResponseEntity.ok(mauSacService.searchMauSacs(keyword, page, size, trangThai));
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<MauSacDTO> getMauSacByName(@PathVariable String ten){
        return ResponseEntity.ok(mauSacService.getMauSacByName(ten));
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<MauSacDTO>> getMauSacs() {
        return ResponseEntity.ok(mauSacService.findAllMauSacs());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addMauSac(@Valid @RequestBody MauSacDTO mauSacDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            MauSacDTO savedDTO = mauSacService.addMauSac(mauSacDTO);
            return ResponseEntity.ok(savedDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MauSacDTO> updateMauSac(@PathVariable int id, @RequestBody MauSacDTO mauSacDTO) {
        MauSacDTO updatedMauSac = mauSacService.updateMauSac(id, mauSacDTO);
        if (updatedMauSac != null) {
            return ResponseEntity.ok(updatedMauSac);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<MauSacDTO> getMauSacById(@PathVariable int id) {
        MauSacDTO mauSacDTO = mauSacService.getMauSacById(id);
        return mauSacDTO != null ? ResponseEntity.ok(mauSacDTO) : ResponseEntity.notFound().build();
    }
}

