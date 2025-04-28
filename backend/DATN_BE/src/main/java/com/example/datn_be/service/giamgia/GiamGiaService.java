package com.example.datn_be.service.giamgia;

import com.example.datn_be.entiy.giam_gia.GiamGia;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.GiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class GiamGiaService {
    @Autowired
    GiamGiaRepository giamGiaRepository;

    public Page<GiamGia> getAll(Pageable pageable){
        return giamGiaRepository.findAll(pageable);
    }
   public void them(GiamGia giamGia){
        if(giamGiaRepository.existsGiamGiaByTenGiamGia(giamGia.getTenGiamGia())){
            throw new DuplicateFieldException(Map.of("tenGiamGia", "Tên giảm giá đã tồn tại"));
        }
        giamGia.setNgayTao(LocalDateTime.now());
    giamGiaRepository.save(giamGia);
   }

    public void capNhat(GiamGia giamGia){

        if(giamGiaRepository.existsByTenGiamGiaAndIdNot(giamGia.getTenGiamGia(), giamGia.getId())){
            throw new DuplicateFieldException(Map.of("tenGiamGia", "Tên giảm giá đã tồn tại"));
        }
        giamGiaRepository.save(giamGia);
    }
}
