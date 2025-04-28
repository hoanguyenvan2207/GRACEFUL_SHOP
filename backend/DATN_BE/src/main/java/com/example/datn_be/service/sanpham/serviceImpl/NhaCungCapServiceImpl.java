package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.NhaCungCapDTO;
import com.example.datn_be.entiy.san_pham.NhaCungCap;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.NhaCungCapRepository;
import com.example.datn_be.service.sanpham.NhaCungCapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NhaCungCapServiceImpl implements NhaCungCapService {

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    // Converter Entity -> DTO
    private static NhaCungCapDTO toDTO(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null) {
            return null;
        }
        return new NhaCungCapDTO(
                nhaCungCap.getId(),
                nhaCungCap.getTenNhaCungCap(),
                nhaCungCap.getDiaChi(),
                nhaCungCap.getSoDienThoai(),
                nhaCungCap.getEmail(),
                nhaCungCap.getTrangThai(),
                nhaCungCap.getMoTa(),
                nhaCungCap.getNgayTao()
        );
    }

    // Converter DTO -> Entity
    private static NhaCungCap toEntity(NhaCungCapDTO dto) {
        if (dto == null) {
            return null;
        }
        NhaCungCap nhaCungCap = new NhaCungCap();
        nhaCungCap.setId(dto.getId());
        nhaCungCap.setTenNhaCungCap(dto.getTenNhaCungCap());
        nhaCungCap.setDiaChi(dto.getDiaChi());
        nhaCungCap.setSoDienThoai(dto.getSoDienThoai());
        nhaCungCap.setEmail(dto.getEmail());
        nhaCungCap.setTrangThai(dto.getTrangThai());
        nhaCungCap.setMoTa(dto.getMoTa());
        nhaCungCap.setNgayTao(dto.getNgayTao());
        return nhaCungCap;
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
    public List<NhaCungCapDTO> getNhaCungCapsByTrangThaiTrue(){
            return nhaCungCapRepository.findNhaCungCapsByTrangThaiIsTrue().stream().map(NhaCungCapServiceImpl::toDTO).toList();
    }

    @Override
    public NhaCungCapDTO getNhaCungCapByName(String ten){
        Optional<NhaCungCap> nhaCungCap = nhaCungCapRepository.findNhaCungCapByTenNhaCungCap(ten);

        return nhaCungCap.map(NhaCungCapServiceImpl::toDTO).orElse(null);
    }

    @Override
    public List<NhaCungCapDTO> findAllNhaCungCap() {
        return nhaCungCapRepository.findAll().stream().map(NhaCungCapServiceImpl::toDTO).toList();
    }

    @Override
    public Page<NhaCungCapDTO> searchNhaCungCaps(String keyword, int page, int size, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NhaCungCap> nhaCungCapPage = nhaCungCapRepository.findNhaCungCapsByTenContainingIgnoreCase(removeAccentAndToLower(keyword),trangThai, pageable);
        return nhaCungCapPage.map(NhaCungCapServiceImpl::toDTO);
    }

    @Override
    public NhaCungCapDTO addNhaCungCap(NhaCungCapDTO nhaCungCapDTO) {
        Map<String, String> errors = new HashMap<>();
        if (nhaCungCapRepository.existsNhaCungCapByTenNhaCungCap(nhaCungCapDTO.getTenNhaCungCap())) {
            errors.put("tenNhaCungCap", "Tên nhà cung cấp đã tồn tại");
        }

        if (nhaCungCapRepository.existsNhaCungCapByEmail(nhaCungCapDTO.getEmail())) {
            errors.put("email", "Email đã tồn tại");
        }

        if (nhaCungCapRepository.existsNhaCungCapBySoDienThoai(nhaCungCapDTO.getSoDienThoai())) {
            errors.put("soDienThoai", "Số điện thoại đã tồn tại");
        }

        if (!errors.isEmpty()) {
            throw new DuplicateFieldException(errors);
        }

        NhaCungCap nhaCungCap = toEntity(nhaCungCapDTO);

        // Gán ngày tạo là ngày hiện tại
        nhaCungCap.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        // Lưu vào cơ sở dữ liệu
        NhaCungCap savedNhaCungCap = nhaCungCapRepository.save(nhaCungCap);

        return toDTO(savedNhaCungCap);
    }

    @Override
    public NhaCungCapDTO updateNhaCungCap(int id, NhaCungCapDTO nhaCungCapDTO) {
        Optional<NhaCungCap> existingNhaCungCapOptional = nhaCungCapRepository.findById(id);
        Map<String, String> errors = new HashMap<>();

        if (existingNhaCungCapOptional.isPresent()) {
            NhaCungCap existingNhaCungCap = existingNhaCungCapOptional.get();

            if (!existingNhaCungCap.getTenNhaCungCap().equals(nhaCungCapDTO.getTenNhaCungCap()) &&
                    nhaCungCapRepository.existsNhaCungCapByTenNhaCungCap(nhaCungCapDTO.getTenNhaCungCap())) {
                errors.put("tenNhaCungCap", "Tên nhà cung cấp đã tồn tại");
            }

            if (!existingNhaCungCap.getEmail().equals(nhaCungCapDTO.getEmail()) &&
                    nhaCungCapRepository.existsNhaCungCapByEmail(nhaCungCapDTO.getEmail())) {
                errors.put("email", "Email đã tồn tại");
            }

            if (!existingNhaCungCap.getSoDienThoai().equals(nhaCungCapDTO.getSoDienThoai()) &&
                    nhaCungCapRepository.existsNhaCungCapBySoDienThoai(nhaCungCapDTO.getSoDienThoai())) {
                errors.put("soDienThoai", "Số điện thoại đã tồn tại");
            }

            if (!errors.isEmpty()) {
                throw new DuplicateFieldException(errors);
            }

            // Cập nhật các trường cần thiết
            existingNhaCungCap.setTenNhaCungCap(nhaCungCapDTO.getTenNhaCungCap());
            existingNhaCungCap.setDiaChi(nhaCungCapDTO.getDiaChi());
            existingNhaCungCap.setSoDienThoai(nhaCungCapDTO.getSoDienThoai());
            existingNhaCungCap.setEmail(nhaCungCapDTO.getEmail());
            existingNhaCungCap.setTrangThai(nhaCungCapDTO.getTrangThai());
            existingNhaCungCap.setMoTa(nhaCungCapDTO.getMoTa());

            // Lưu lại vào cơ sở dữ liệu
            NhaCungCap updatedNhaCungCap = nhaCungCapRepository.save(existingNhaCungCap);

            return toDTO(updatedNhaCungCap);
        } else {
            return null;
        }
    }

    @Override
    public NhaCungCapDTO getNhaCungCapById(int id) {
        Optional<NhaCungCap> nhaCungCapOptional = nhaCungCapRepository.findById(id);
        if (nhaCungCapOptional.isPresent()) {
            NhaCungCap nhaCungCap = nhaCungCapOptional.get();
            return toDTO(nhaCungCap);
        } else {
            return null;
        }
    }
}
