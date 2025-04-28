package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.NhaCungCapDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NhaCungCapService {

    List<NhaCungCapDTO> getNhaCungCapsByTrangThaiTrue();

    NhaCungCapDTO getNhaCungCapByName(String ten);

    List<NhaCungCapDTO> findAllNhaCungCap();

    Page<NhaCungCapDTO> searchNhaCungCaps(String keyword, int page, int size, Boolean trangThai);

    NhaCungCapDTO addNhaCungCap(NhaCungCapDTO nhaCungCapDTO);

    NhaCungCapDTO updateNhaCungCap(int id, NhaCungCapDTO nhaCungCapDTO);

    NhaCungCapDTO getNhaCungCapById(int id);
}