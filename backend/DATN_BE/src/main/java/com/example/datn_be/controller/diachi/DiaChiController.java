package com.example.datn_be.controller.diachi;

import com.example.datn_be.dto.khachhang.resquest.DiaChiRequest;
import com.example.datn_be.service.diachi.impl.DiaChiServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/dia-chi")
@CrossOrigin(origins =  {
        "http://localhost:5173",        // cho dev mode
        "https://graceful56.shop"       // cho production
},allowCredentials = "true")
@RequiredArgsConstructor
public class DiaChiController {

    @Autowired
    private final DiaChiServiceImpl diaChiService;

    @GetMapping("hien-thi")
    public ResponseEntity<?> hienThi(){
        return diaChiService.getAllDiaChi();
    }

    @PostMapping("add")
    public ResponseEntity<?> addDiaChi(@RequestBody DiaChiRequest diaChiRequest){
        return diaChiService.addDiaChi(diaChiRequest);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateDiaChi(@PathVariable Integer id, @RequestBody DiaChiRequest diaChiRequest){
        return diaChiService.updateDiaChi(id,diaChiRequest);
    }

    @GetMapping("details/{id}")
    public ResponseEntity<?> detailsDiaChi(@PathVariable Integer id){
        return diaChiService.getDiaChiById(id);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteDiaChi(@PathVariable Integer id){
        return diaChiService.deleteDiaChiById(id);
    }

    @PutMapping("mac-dinh/{id}")
    public ResponseEntity<?> macDinh(@PathVariable Integer id){
        return diaChiService.setDefaultAddress(id);
    }

    @GetMapping("khach-hang/{id}")
    public ResponseEntity<?> diaChiKhachHang(@PathVariable Integer id){
        return diaChiService.getDiaChiByKhachHangId(id);
    }
}
