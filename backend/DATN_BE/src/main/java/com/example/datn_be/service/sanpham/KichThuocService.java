package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.KichThuocDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KichThuocService {

    List<KichThuocDTO> getKichThuocsByTrangThaiTrue();

    KichThuocDTO getKichThuocByTen(String ten);

    Page<KichThuocDTO> searchKichThuocs(String ten, int page, int size, Boolean trangThai);

    List<KichThuocDTO> findAllKichThuoc();

    KichThuocDTO addKichThuoc(KichThuocDTO kichThuocDTO);

    KichThuocDTO updateKichThuoc(int id, KichThuocDTO kichThuocDTO);

    KichThuocDTO getKichThuocById(int id);
}