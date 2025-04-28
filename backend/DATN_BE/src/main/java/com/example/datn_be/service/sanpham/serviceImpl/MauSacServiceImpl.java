package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.MauSacDTO;
import com.example.datn_be.entiy.san_pham.MauSac;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.MauSacRepository;
import com.example.datn_be.service.sanpham.MauSacService;
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
public class MauSacServiceImpl implements MauSacService {

    @Autowired
    private MauSacRepository mauSacRepository;

    // Chuyển từ Entity sang DTO
    public static MauSacDTO toDTO(MauSac mauSac) {
        if (mauSac == null) {
            return null;
        }
        return new MauSacDTO(
                mauSac.getId(),
                mauSac.getTenMauSac(),
                mauSac.getMoTa(),
                mauSac.getTrangThai(),
                mauSac.getNgayTao()
        );
    }

    // Chuyển từ DTO sang Entity
    public static MauSac toEntity(MauSacDTO dto) {
        if (dto == null) {
            return null;
        }
        MauSac mauSac = new MauSac();
        mauSac.setId(dto.getId());
        mauSac.setTenMauSac(dto.getTenMauSac());
        mauSac.setMoTa(dto.getMoTa());
        mauSac.setTrangThai(dto.getTrangThai());
        mauSac.setNgayTao(dto.getNgayTao());
        return mauSac;
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
    public List<MauSacDTO> getMauSacsByTrangThaiTrue() {
        return mauSacRepository.findMauSacsByTrangThaiIsTrue().stream().map(MauSacServiceImpl::toDTO).toList();
    }

    @Override
    public List<MauSacDTO> findAllMauSacs() {
        return mauSacRepository.findAll().stream().map(MauSacServiceImpl::toDTO).toList();
    }

    @Override
    public MauSacDTO getMauSacByName(String ten){
        Optional<MauSac> mauSac = mauSacRepository.findMauSacByTenMauSac(ten);

        return mauSac.map(MauSacServiceImpl::toDTO).orElse(null);
    }

    @Override
    public Page<MauSacDTO> searchMauSacs(String keyword, int page, int size, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MauSac> mauSacPage = mauSacRepository.findMauSacsByTenContainingIgnoreCase(removeAccentAndToLower(keyword), trangThai,pageable);
        return mauSacPage.map(MauSacServiceImpl::toDTO);
    }

    @Override
    public MauSacDTO addMauSac(MauSacDTO mauSacDTO) {
        if(mauSacRepository.existsMauSacByTenMauSac(mauSacDTO.getTenMauSac())) {
            throw new DuplicateFieldException(Map.of("tenMauSac", "Tên màu sắc đã tồn tại"));
        }

        MauSac mauSac = toEntity(mauSacDTO);

        mauSac.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        MauSac savedMauSac = mauSacRepository.save(mauSac);

        return toDTO(savedMauSac);
    }

    @Override
    public MauSacDTO updateMauSac(int id, MauSacDTO mauSacDTO) {
        Optional<MauSac> existingMauSacOptional = mauSacRepository.findById(id);

        if (existingMauSacOptional.isPresent()) {
            MauSac existingMauSac = existingMauSacOptional.get();

            if(!existingMauSac.getTenMauSac().equals(mauSacDTO.getTenMauSac()) &&
                    mauSacRepository.existsMauSacByTenMauSac(mauSacDTO.getTenMauSac())) {
                throw new DuplicateFieldException(Map.of("tenMauSac", "Tên màu sắc đã tồn tại"));
            }
            // Cập nhật các trường cần thiết
            existingMauSac.setTenMauSac(mauSacDTO.getTenMauSac());
            existingMauSac.setMoTa(mauSacDTO.getMoTa());
            existingMauSac.setTrangThai(mauSacDTO.getTrangThai());

            // Lưu lại vào cơ sở dữ liệu
            MauSac updatedMauSac = mauSacRepository.save(existingMauSac);

            return toDTO(updatedMauSac);
        } else {
            return null;
        }
    }

    @Override
    public MauSacDTO getMauSacById(int id) {
        Optional<MauSac> mauSacOptional = mauSacRepository.findById(id);
        if (mauSacOptional.isPresent()) {
            MauSac mauSac = mauSacOptional.get();
            return toDTO(mauSac);
        } else {
            return null;
        }
    }

}
