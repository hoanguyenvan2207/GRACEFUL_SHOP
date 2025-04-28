package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.TaAoDTO;
import com.example.datn_be.entiy.san_pham.TaAo;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.TaAoRepository;
import com.example.datn_be.service.sanpham.TaAoService;
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
public class TaAoServiceImpl implements TaAoService {

    @Autowired
    private TaAoRepository taAoRepository;

    // Chuyển từ Entity sang DTO
    public static TaAoDTO toDTO(TaAo taAo) {
        if (taAo == null) {
            return null;
        }
        return new TaAoDTO(
                taAo.getId(),
                taAo.getTen(),
                taAo.getMoTa(),
                taAo.getTrangThai(),
                taAo.getNgayTao()
        );
    }

    // Chuyển từ DTO sang Entity
    public static TaAo toEntity(TaAoDTO dto) {
        if (dto == null) {
            return null;
        }
        TaAo taAo = new TaAo();
        taAo.setId(dto.getId());
        taAo.setTen(dto.getTenTaAo());
        taAo.setMoTa(dto.getMoTa());
        taAo.setTrangThai(dto.getTrangThai());
        taAo.setNgayTao(dto.getNgayTao());
        return taAo;
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
    public List<TaAoDTO> getTaAosByTrangThaiTrue(){
        return taAoRepository.findTaAosByTrangThaiIsTrue().stream().map(TaAoServiceImpl::toDTO).toList();
    }

    @Override
    public List<TaAoDTO> findAllTaAo() {
        return taAoRepository.findAll().stream().map(TaAoServiceImpl::toDTO).toList();
    }

    @Override
    public Page<TaAoDTO> searchTaAos(String keyword, int page, int size, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TaAo> taAoPage = taAoRepository.findTaAosByTenContainingIgnoreCase(removeAccentAndToLower(keyword), trangThai, pageable);
        return taAoPage.map(TaAoServiceImpl::toDTO);
    }

    @Override
    public TaAoDTO getTaAoByName(String ten){
        Optional<TaAo> taAo = taAoRepository.findTaAoByTen(ten);
        return taAo.map(TaAoServiceImpl::toDTO).orElse(null);
    }

    @Override
    public TaAoDTO getTaAoById(int id) {
        Optional<TaAo> taAo = taAoRepository.findById(id);
        return taAo.map(TaAoServiceImpl::toDTO).orElse(null);
    }

    @Override
    public TaAoDTO addTaAo(TaAoDTO taAoDTO) {
        if (taAoRepository.existsTaAoByTen(taAoDTO.getTenTaAo())) {
            throw new DuplicateFieldException(Map.of("tenTaAo", "Tên tà áo đã tồn tại"));
        }

        TaAo taAo = toEntity(taAoDTO);
        taAo.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))); // Gán ngày tạo là ngày hiện tại
        TaAo savedTaAo = taAoRepository.save(taAo);
        return toDTO(savedTaAo);
    }

    @Override
    public TaAoDTO updateTaAo(int id, TaAoDTO taAoDTO) {
        Optional<TaAo> existingTaAoOptional = taAoRepository.findById(id);

        if (existingTaAoOptional.isPresent()) {
            TaAo existingTaAo = existingTaAoOptional.get();

            if (!existingTaAo.getTen().equals(taAoDTO.getTenTaAo()) &&
                    taAoRepository.existsTaAoByTen(taAoDTO.getTenTaAo())) {
                throw new DuplicateFieldException(Map.of("tenTaAo", "Tên tà áo đã tồn tại"));
            }

            // Cập nhật các trường cần thiết
            existingTaAo.setTen(taAoDTO.getTenTaAo());
            existingTaAo.setMoTa(taAoDTO.getMoTa());
            existingTaAo.setTrangThai(taAoDTO.getTrangThai());

            // Lưu lại vào cơ sở dữ liệu
            TaAo updatedTaAo = taAoRepository.save(existingTaAo);
            return toDTO(updatedTaAo);
        } else {
            return null; // Nếu không tìm thấy TaAo để cập nhật
        }
    }
}