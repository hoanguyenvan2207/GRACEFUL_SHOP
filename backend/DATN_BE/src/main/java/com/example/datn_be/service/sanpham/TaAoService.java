package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.TaAoDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaAoService {

    List<TaAoDTO> getTaAosByTrangThaiTrue();

    List<TaAoDTO> findAllTaAo();

    Page<TaAoDTO> searchTaAos(String keyword, int page, int size, Boolean trangThai);

    TaAoDTO getTaAoByName(String ten);

    TaAoDTO getTaAoById(int id);

    TaAoDTO addTaAo(TaAoDTO taAoDTO);

    TaAoDTO updateTaAo(int id, TaAoDTO taAoDTO);
}