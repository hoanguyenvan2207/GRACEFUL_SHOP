package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.LoaiSanPhamDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoaiSanPhamService {

    LoaiSanPhamDTO getLoaiSanPhamByName(String ten);

    List<LoaiSanPhamDTO> getLoaiSanPhamsByTrangThaiTrue();

    List<LoaiSanPhamDTO> findAllLoaiSanPhams();

    Page<LoaiSanPhamDTO> searchLoaiSanPhams(String keyword, int page, int size, Boolean trangThai);

    LoaiSanPhamDTO addLoaiSanPham(LoaiSanPhamDTO loaiSanPhamDTO);

    LoaiSanPhamDTO updateLoaiSanPham(int id, LoaiSanPhamDTO loaiSanPhamDTO);

    LoaiSanPhamDTO getLoaiSanPhamById(int id);
}