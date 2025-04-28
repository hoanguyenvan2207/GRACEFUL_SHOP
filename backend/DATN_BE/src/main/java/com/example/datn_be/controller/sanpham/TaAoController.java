package com.example.datn_be.controller.sanpham;

import com.example.datn_be.dto.sanpham.TaAoDTO;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.service.sanpham.TaAoService;
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
@RequestMapping("/api/ta-ao")
@CrossOrigin("*")
public class TaAoController {

    private final TaAoService taAoService;

    @Autowired
    public TaAoController(TaAoService taAoService) {
        this.taAoService = taAoService;
    }

    @GetMapping("/list/all")
    public ResponseEntity<List<TaAoDTO>> getTaAos() {
        return ResponseEntity.ok(taAoService.findAllTaAo());
    }

    @GetMapping("/list/enables")
    public ResponseEntity<List<TaAoDTO>> getTaAosEn() {
        return ResponseEntity.ok(taAoService.getTaAosByTrangThaiTrue());
    }

    @GetMapping("/page/all")
    public ResponseEntity<Page<TaAoDTO>> searchTaAos(@RequestParam(defaultValue = "0" ) int page,
                                                           @RequestParam(defaultValue = "5") int size,
                                                           @RequestParam(required = false) String keyword,
                                                           @RequestParam(required = false) Boolean trangThai) {
        return ResponseEntity.ok(taAoService.searchTaAos(keyword, page, size, trangThai));
    }

    @GetMapping("/by-ten/{ten}")
    public ResponseEntity<TaAoDTO> getTaAoByName(@PathVariable String ten){
        return ResponseEntity.ok(taAoService.getTaAoByName(ten));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<TaAoDTO> getTaAoById(@PathVariable int id) {
        TaAoDTO taAoDTO = taAoService.getTaAoById(id);
        if (taAoDTO != null) {
            return ResponseEntity.ok(taAoDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTaAo(@Valid @RequestBody TaAoDTO taAoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            TaAoDTO savedTaAoDTO = taAoService.addTaAo(taAoDTO);
            return ResponseEntity.ok(savedTaAoDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTaAo(@Valid @PathVariable int id, @RequestBody TaAoDTO taAoDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            TaAoDTO savedTaAoDTO = taAoService.updateTaAo(id, taAoDTO);
            return ResponseEntity.ok(savedTaAoDTO);
        } catch (DuplicateFieldException ex) {
            return ResponseEntity.badRequest().body(Map.of("errors", ex.getFieldErrors()));
        }
    }
}
