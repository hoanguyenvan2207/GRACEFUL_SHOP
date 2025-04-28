package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.GioHangDTO;

import java.util.List;

public interface GioHangService {
    GioHangDTO createGioHang(GioHangDTO dto);

    GioHangDTO updateGioHang(Integer id, GioHangDTO dto);

    void deleteGioHang(Integer id);

    GioHangDTO getGioHang(Integer id);

    List<GioHangDTO> findAllByKhachHangId(Integer idKhachHang);
}
