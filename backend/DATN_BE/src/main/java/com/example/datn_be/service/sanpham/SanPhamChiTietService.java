package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.SanPhamChiTietDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SanPhamChiTietService {

    List<SanPhamChiTietDTO> findSanPhamChiTietBySanPhamId(Integer aoDaiId);

    List<SanPhamChiTietDTO> findAllSanPhamChiTiet();

    Page<SanPhamChiTietDTO> filterSanPhamChiTiet(
            String keyword,
            List<String> listTenAoDai,
            List<String> listMauSac,
            List<String> listKichThuoc,
            Boolean trangThai,
            String sort,
            int page,
            int size
    );

    SanPhamChiTietDTO addSanPhamChiTiet(SanPhamChiTietDTO sanPhamChiTietDTO);

    @Transactional
    List<SanPhamChiTietDTO> addSanPhamChiTiets(List<SanPhamChiTietDTO> sanPhamChiTietDTOs);

    SanPhamChiTietDTO updateSanPhamChiTiet(int id, SanPhamChiTietDTO sanPhamChiTietDTO);

    SanPhamChiTietDTO getSanPhamChiTietById(int id);
}