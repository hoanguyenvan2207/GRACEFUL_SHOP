package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.GioHangDTO;
import com.example.datn_be.entiy.san_pham.GioHangChiTiet;
import com.example.datn_be.entiy.san_pham.KhachHang;
import com.example.datn_be.entiy.san_pham.SanPhamChiTiet;
import com.example.datn_be.repository.san_pham.GioHangRepository;
import com.example.datn_be.service.sanpham.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GioHangServiceImpl implements GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    public static GioHangDTO toDTO(GioHangChiTiet entity) {
        if (entity == null) {
            return null;
        }
        GioHangDTO dto = new GioHangDTO();
        dto.setId(entity.getId());
        dto.setIdKhachHang(entity.getKhachHang() != null ? entity.getKhachHang().getId() : null);
        dto.setIdSanPhamChiTiet(entity.getSanPhamChiTiet() != null ? entity.getSanPhamChiTiet().getId() : null);
        dto.setSoLuong(entity.getSoLuong());
        dto.setNgayTao(entity.getNgayTao());
        return dto;
    }

    public static GioHangChiTiet toEntity(GioHangDTO dto) {
        if (dto == null) {
            return null;
        }
        GioHangChiTiet entity = new GioHangChiTiet();
        entity.setId(dto.getId());
        if (dto.getIdKhachHang() != null) {
            KhachHang kh = new KhachHang();
            kh.setId(dto.getIdKhachHang());
            entity.setKhachHang(kh);
        }
        if (dto.getIdSanPhamChiTiet() != null) {
            SanPhamChiTiet spct = new SanPhamChiTiet();
            spct.setId(dto.getIdSanPhamChiTiet());
            entity.setSanPhamChiTiet(spct);
        }
        entity.setSoLuong(dto.getSoLuong());
        entity.setNgayTao(dto.getNgayTao());
        return entity;
    }

    @Override
    public GioHangDTO createGioHang(GioHangDTO dto) {
        GioHangChiTiet entity = toEntity(dto);
        if (entity.getNgayTao() == null) {
            entity.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        }
        GioHangChiTiet saved = gioHangRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public GioHangDTO updateGioHang(Integer id, GioHangDTO dto) {
        GioHangChiTiet existing = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại với id: " + id));
        // Cập nhật các trường cần thiết
        existing.setSoLuong(dto.getSoLuong());
        GioHangChiTiet updated = gioHangRepository.save(existing);
        return toDTO(updated);
    }

    @Override
    public void deleteGioHang(Integer id) {
        gioHangRepository.deleteById(id);
    }

    @Override
    public GioHangDTO getGioHang(Integer id) {
        GioHangChiTiet entity = gioHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại với id: " + id));
        return toDTO(entity);
    }

    @Override
    public List<GioHangDTO> findAllByKhachHangId(Integer idKhachHang) {
        List<GioHangChiTiet> gioHangChiTietList = gioHangRepository.findAllByKhachHangId(idKhachHang);
        return gioHangChiTietList.stream()
                .map(GioHangServiceImpl::toDTO)
                .toList();
    }
}
