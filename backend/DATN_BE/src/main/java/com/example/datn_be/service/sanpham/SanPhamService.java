package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.SanPhamDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SanPhamService {

    List<SanPhamDTO> getSanPhamByTrangThaiTrue();

    Page<SanPhamDTO> searchAndFilterSanPham(
            String keyword,
            List<String> tenLoaiAoDai,
            List<String> tenChatLieu,
            List<String> tenTaAo,
            List<String> tenNhaCungCap,
            Boolean trangThai,
            String sortDate,
            int page,
            int size
    );

    Page<SanPhamDTO> filterSanPham(
            List<String> listLoaiAoDai,
            List<String> listChatLieu,
            List<String> listTaAo,
            List<String> listNhaCungCap,
            List<String> listMauSac,
            List<String> listKichThuoc,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword,
            String sortPrice,
            String sortDate,
            int page, int size);

    SanPhamDTO getSanPhamById(int id);

    SanPhamDTO getSanPhamByCode(String ma);

    List<SanPhamDTO> getTop5SanPhamBanChay();

    List<Map<String, Object>> getTopProductsSale();

    List<SanPhamDTO> getSanPhamAll();

    @Transactional
    SanPhamDTO addSanPham(SanPhamDTO sanPhamDTO);

    @Transactional
    List<SanPhamDTO> addSanPhams(List<SanPhamDTO> sanPhamDTOList);

    @Transactional
    SanPhamDTO updateSanPham(int id, SanPhamDTO sanPhamDTO);

    List<Map<String, Object>> getAllProducts();

}