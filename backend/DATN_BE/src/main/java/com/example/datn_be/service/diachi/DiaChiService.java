package com.example.datn_be.service.diachi;

import com.example.datn_be.dto.khachhang.resquest.DiaChiRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface DiaChiService {
    ResponseEntity<?> getAllDiaChi();

    ResponseEntity<?> addDiaChi(DiaChiRequest diaChiRequest);

    ResponseEntity<?> updateDiaChi(Integer id, DiaChiRequest diaChiRequest);

    ResponseEntity<?> getDiaChiById(Integer id);

    ResponseEntity<?> getDiaChiByKhachHangId(Integer khachHangId);

    ResponseEntity<?> deleteDiaChiById(Integer id);

    ResponseEntity<?> setDefaultAddress(Integer id);
}
