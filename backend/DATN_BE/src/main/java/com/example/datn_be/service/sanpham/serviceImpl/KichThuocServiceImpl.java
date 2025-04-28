package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.KichThuocDTO;
import com.example.datn_be.entiy.san_pham.KichThuoc;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.KichThuocRepository;
import com.example.datn_be.service.sanpham.KichThuocService;
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
public class KichThuocServiceImpl implements KichThuocService {

    @Autowired
    private KichThuocRepository kichThuocRepository;

    public static KichThuocDTO toDTO(KichThuoc kichThuoc) {
        if (kichThuoc == null) {
            return null;
        }
        return new KichThuocDTO(
                kichThuoc.getId(),
                kichThuoc.getTen(),
                kichThuoc.getMoTa(),
                kichThuoc.getTrangThai(),
                kichThuoc.getNgayTao()
        );
    }

    public static KichThuoc toEntity(KichThuocDTO dto) {
        if (dto == null) {
            return null;
        }
        KichThuoc kichThuoc = new KichThuoc();
        kichThuoc.setId(dto.getId());
        kichThuoc.setTen(dto.getTenKichThuoc());
        kichThuoc.setMoTa(dto.getMoTa());
        kichThuoc.setTrangThai(dto.getTrangThai());
        kichThuoc.setNgayTao(dto.getNgayTao());
        return kichThuoc;
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
    public List<KichThuocDTO> getKichThuocsByTrangThaiTrue() {
        List<KichThuoc> kichThuocs = kichThuocRepository.findKichThuocsByTrangThaiIsTrue();
        return kichThuocs.stream().map(KichThuocServiceImpl::toDTO).toList();
    }

    @Override
    public KichThuocDTO getKichThuocByTen(String ten){
        Optional<KichThuoc> kichThuoc = kichThuocRepository.findKichThuocByTen(ten);
        return kichThuoc.map(KichThuocServiceImpl::toDTO).orElse(null);
    }

    @Override
    public Page<KichThuocDTO> searchKichThuocs(String ten, int page, int size, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KichThuoc> kichThuocPage = kichThuocRepository.findKichThuocsByTenContainingIgnoreCase(removeAccentAndToLower(ten), trangThai, pageable);
        return kichThuocPage.map(KichThuocServiceImpl::toDTO);
    }

    @Override
    public List<KichThuocDTO> findAllKichThuoc() {
        List<KichThuoc> kichThuocs = kichThuocRepository.findAll();
        return kichThuocs.stream().map(KichThuocServiceImpl::toDTO).toList();
    }

    @Override
    public KichThuocDTO addKichThuoc(KichThuocDTO kichThuocDTO) {
        if (kichThuocRepository.existsKichThuocByTen(kichThuocDTO.getTenKichThuoc())) {
            throw new DuplicateFieldException(Map.of("tenKichThuoc", "Tên kích thước đã tồn tại"));
        }
        KichThuoc kichThuoc = toEntity(kichThuocDTO);
        kichThuoc.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        KichThuoc savedKichThuoc = kichThuocRepository.save(kichThuoc);
        return toDTO(savedKichThuoc);
    }

    @Override
    public KichThuocDTO updateKichThuoc(int id, KichThuocDTO kichThuocDTO) {
        Optional<KichThuoc> existingKichThuocOptional = kichThuocRepository.findById(id);

        if (existingKichThuocOptional.isPresent()) {
            KichThuoc existingKichThuoc = existingKichThuocOptional.get();

            if (!existingKichThuoc.getTen().equals(kichThuocDTO.getTenKichThuoc()) &&
                    kichThuocRepository.existsKichThuocByTen(kichThuocDTO.getTenKichThuoc())) {
                throw new DuplicateFieldException(
                        Map.of("tenKichThuoc", "Tên kích thước đã tồn tại")
                );
            }

            // Cập nhật các trường cần thiết
            existingKichThuoc.setTen(kichThuocDTO.getTenKichThuoc());
            existingKichThuoc.setMoTa(kichThuocDTO.getMoTa());
            existingKichThuoc.setTrangThai(kichThuocDTO.getTrangThai());

            // Lưu lại vào cơ sở dữ liệu
            KichThuoc updatedKichThuoc = kichThuocRepository.save(existingKichThuoc);

            return toDTO(updatedKichThuoc);
        } else {
            return null;
        }
    }

    @Override
    public KichThuocDTO getKichThuocById(int id) {
        Optional<KichThuoc> kichThuocOptional = kichThuocRepository.findById(id);
        if (kichThuocOptional.isPresent()) {
            KichThuoc kichThuoc = kichThuocOptional.get();
            return toDTO(kichThuoc);
        } else {
            return null;
        }
    }

}