package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.LoaiSanPhamDTO;
import com.example.datn_be.entiy.san_pham.LoaiSanPham;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.LoaiSanPhamRepository;
import com.example.datn_be.service.sanpham.LoaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LoaiSanPhamServiceImpl implements LoaiSanPhamService {

    @Autowired
    private LoaiSanPhamRepository loaiSanPhamRepository;

    public static LoaiSanPhamDTO toDTO(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null) {
            return null;
        }
        return new LoaiSanPhamDTO(
                loaiSanPham.getId(),
                loaiSanPham.getTenLoaiAoDai(),
                loaiSanPham.getMoTa(),
                loaiSanPham.getTrangThai(),
                loaiSanPham.getNgayTao()
        );
    }

    public static LoaiSanPham toEntity(LoaiSanPhamDTO dto) {
        if (dto == null) {
            return null;
        }
        LoaiSanPham loaiSanPham = new LoaiSanPham();
        loaiSanPham.setId(dto.getId());
        loaiSanPham.setTenLoaiAoDai(dto.getTenLoaiAoDai());
        loaiSanPham.setMoTa(dto.getMoTa());
        loaiSanPham.setTrangThai(dto.getTrangThai());
        loaiSanPham.setNgayTao(dto.getNgayTao());
        return loaiSanPham;
    }

    public static String removeAccentAndToLower(String input) {
        if (input == null) {
            return null;
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noAccent = normalized.replaceAll("\\p{M}", "");

        return noAccent.toLowerCase();
    }

    @Override
    public LoaiSanPhamDTO getLoaiSanPhamByName(String ten){
        Optional<LoaiSanPham> loaiSanPham = loaiSanPhamRepository.findLoaiSanPhamByTenLoaiAoDai(ten);

        return loaiSanPham.map(LoaiSanPhamServiceImpl::toDTO).orElse(null);
    }

    @Override
    public List<LoaiSanPhamDTO> getLoaiSanPhamsByTrangThaiTrue() {return loaiSanPhamRepository.findLoaiSanPhamsByTrangThaiIsTrue().stream().map(LoaiSanPhamServiceImpl::toDTO).toList();}

    @Override
    public List<LoaiSanPhamDTO> findAllLoaiSanPhams() {
        return loaiSanPhamRepository.findAll().stream().map(LoaiSanPhamServiceImpl::toDTO).toList();
    }

    @Override
    public Page<LoaiSanPhamDTO> searchLoaiSanPhams(String keyword, int page, int size, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LoaiSanPham> loaiSanPhamPage = loaiSanPhamRepository.findLoaiSanPhamsByTenContainingIgnoreCase(removeAccentAndToLower(keyword), trangThai,  pageable);
        return loaiSanPhamPage.map(LoaiSanPhamServiceImpl::toDTO);
    }

    @Override
    public LoaiSanPhamDTO addLoaiSanPham(LoaiSanPhamDTO loaiSanPhamDTO) {
        if(loaiSanPhamRepository.existsLoaiSanPhamByTenLoaiAoDai(loaiSanPhamDTO.getTenLoaiAoDai())) {
            throw new DuplicateFieldException(Map.of("tenLoaiAoDai", "Tên loại áo dài đã tồn tại"));
        }
        LoaiSanPham loaiSanPham = toEntity(loaiSanPhamDTO);

        // Gán ngày tạo là ngày hiện tại
        loaiSanPham.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        // Lưu vào cơ sở dữ liệu
        LoaiSanPham savedLoaiSanPham = loaiSanPhamRepository.save(loaiSanPham);

        return toDTO(savedLoaiSanPham);
    }

    @Override
    public LoaiSanPhamDTO updateLoaiSanPham(int id, LoaiSanPhamDTO loaiSanPhamDTO) {
        Optional<LoaiSanPham> existingLoaiSanPhamOptional = loaiSanPhamRepository.findById(id);

        if (existingLoaiSanPhamOptional.isPresent()) {
            LoaiSanPham existingLoaiSanPham = existingLoaiSanPhamOptional.get();

            if (!existingLoaiSanPham.getTenLoaiAoDai().equals(loaiSanPhamDTO.getTenLoaiAoDai()) &&
                    loaiSanPhamRepository.existsLoaiSanPhamByTenLoaiAoDai(loaiSanPhamDTO.getTenLoaiAoDai())) {
                throw new DuplicateFieldException(Map.of("tenLoaiAoDai", "Tên loại áo dài đã tồn tại"));
            }
            // Cập nhật các trường cần thiết
            existingLoaiSanPham.setTenLoaiAoDai(loaiSanPhamDTO.getTenLoaiAoDai());
            existingLoaiSanPham.setMoTa(loaiSanPhamDTO.getMoTa());
            existingLoaiSanPham.setTrangThai(loaiSanPhamDTO.getTrangThai());

            // Lưu lại vào cơ sở dữ liệu
            LoaiSanPham updatedLoaiSanPham = loaiSanPhamRepository.save(existingLoaiSanPham);

            return toDTO(updatedLoaiSanPham);
        } else {
            return null;
        }
    }

    @Override
    public LoaiSanPhamDTO getLoaiSanPhamById(int id) {
        Optional<LoaiSanPham> loaiSanPhamOptional = loaiSanPhamRepository.findById(id);
        if (loaiSanPhamOptional.isPresent()) {
            LoaiSanPham loaiSanPham = loaiSanPhamOptional.get();
            return toDTO(loaiSanPham);
        } else {
            return null;
        }
    }
}