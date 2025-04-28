package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.AnhDTO;
import com.example.datn_be.entiy.san_pham.Anh;
import com.example.datn_be.entiy.san_pham.SanPham;
import com.example.datn_be.repository.san_pham.AnhRepository;
import com.example.datn_be.repository.san_pham.SanPhamRepository;
import com.example.datn_be.service.sanpham.AnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnhServiceImpl implements AnhService {

    @Autowired
    private AnhRepository anhRepository;

    @Override
    public AnhDTO createAnh(AnhDTO anhDTO) {
        Anh anh = new Anh();
        anh.setAnhUrl(anhDTO.getAnhUrl());
        anh.setTrangThai(anhDTO.getTrangThai());
        anh.setNgayTao(anhDTO.getNgayTao());

        SanPham sanPham = new SanPham();
        sanPham.setId(anhDTO.getSanPhamId());
        anh.setAoDai(sanPham);

        anhRepository.save(anh); // Lưu ảnh vào database
        return anhDTO;
    }

}
