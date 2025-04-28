package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.MauSacDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MauSacService {

    List<MauSacDTO> getMauSacsByTrangThaiTrue();

    List<MauSacDTO> findAllMauSacs();

    MauSacDTO getMauSacByName(String ten);

    Page<MauSacDTO> searchMauSacs(String keyword, int page, int size, Boolean trangThai);

    MauSacDTO addMauSac(MauSacDTO mauSacDTO);

    MauSacDTO updateMauSac(int id, MauSacDTO mauSacDTO);

    MauSacDTO getMauSacById(int id);
}