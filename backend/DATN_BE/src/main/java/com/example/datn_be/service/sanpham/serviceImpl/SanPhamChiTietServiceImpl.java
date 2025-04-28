package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.SanPhamChiTietDTO;
import com.example.datn_be.entiy.san_pham.*;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.KichThuocRepository;
import com.example.datn_be.repository.san_pham.MauSacRepository;
import com.example.datn_be.repository.san_pham.SanPhamChiTietRepository;
import com.example.datn_be.service.sanpham.SanPhamChiTietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    // Method chuyển từ Entity -> DTO
    static SanPhamChiTietDTO toDTO(SanPhamChiTiet sanPhamChiTiet) {
        if (sanPhamChiTiet == null) {
            return null;
        }
        return new SanPhamChiTietDTO(
                sanPhamChiTiet.getId(),
                sanPhamChiTiet.getMaAoDaiChiTiet(),
                sanPhamChiTiet.getGiaGoc(),
                sanPhamChiTiet.getGiaBan(),
                sanPhamChiTiet.getSoLuong(),
                sanPhamChiTiet.getTrangThai(),
                sanPhamChiTiet.getAnhUrl(),
                sanPhamChiTiet.getNgayTao(),
                sanPhamChiTiet.getAoDai() != null ? sanPhamChiTiet.getAoDai().getId() : null,
                sanPhamChiTiet.getAoDai() != null ? sanPhamChiTiet.getAoDai().getMaAoDai() : null,
                sanPhamChiTiet.getAoDai() != null ? sanPhamChiTiet.getAoDai().getTenAoDai() : null,
                sanPhamChiTiet.getMauSac() != null ? sanPhamChiTiet.getMauSac().getId() : null,
                sanPhamChiTiet.getMauSac() != null ? sanPhamChiTiet.getMauSac().getTenMauSac() : null,
                sanPhamChiTiet.getKichThuoc() != null ? sanPhamChiTiet.getKichThuoc().getId() : null,
                sanPhamChiTiet.getKichThuoc() != null ? sanPhamChiTiet.getKichThuoc().getTen() : null,
                sanPhamChiTiet.getKhuyenMaiSP()!=null?sanPhamChiTiet.getKhuyenMaiSP().getTenKhuyenMai():null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getId() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getMaKhuyenMai() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getPhanTramGiam() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getSoTienGiam() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getNgayBatDau() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getNgayKetThuc() : null,
                sanPhamChiTiet.getKhuyenMaiSP() != null ? sanPhamChiTiet.getKhuyenMaiSP().getTrangThai() : null
        );
    }

    // Method chuyển từ DTO -> Entity
    static SanPhamChiTiet toEntity(SanPhamChiTietDTO dto) {
        if (dto == null) {
            return null;
        }
        SanPhamChiTiet sanPhamChiTiet = new SanPhamChiTiet();
        sanPhamChiTiet.setId(dto.getId());
        sanPhamChiTiet.setMaAoDaiChiTiet(dto.getMaAoDaiChiTiet());
        sanPhamChiTiet.setGiaGoc(dto.getGiaGoc());
        sanPhamChiTiet.setGiaBan(dto.getGiaBan());
        sanPhamChiTiet.setSoLuong(dto.getSoLuong());
        sanPhamChiTiet.setTrangThai(dto.getTrangThai());
        sanPhamChiTiet.setAnhUrl(dto.getAnhUrl());
        sanPhamChiTiet.setNgayTao(dto.getNgayTao());

        if (dto.getAoDaiId() != null) {
            SanPham sanPham = new SanPham();
            sanPham.setId(dto.getAoDaiId());
            sanPhamChiTiet.setAoDai(sanPham);
        }

        if (dto.getMauSacId() != null) {
            MauSac mauSac = new MauSac();
            mauSac.setId(dto.getMauSacId());
            sanPhamChiTiet.setMauSac(mauSac);
        }

        if (dto.getKichThuocId() != null) {
            KichThuoc kichThuoc = new KichThuoc();
            kichThuoc.setId(dto.getKichThuocId());
            sanPhamChiTiet.setKichThuoc(kichThuoc);
        }

        return sanPhamChiTiet;
    }

    public static String removeAccentAndToLower(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noAccent = normalized.replaceAll("\\p{M}", "");
        String[] tokens = noAccent.trim().toLowerCase().split("\\s+");

        String tokensCombined = String.join("%", tokens);

        return tokensCombined.toLowerCase();
    }

    @Override
    public List<SanPhamChiTietDTO> findSanPhamChiTietBySanPhamId(Integer aoDaiId) {
        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepository.findByAoDaiId(aoDaiId);

        return sanPhamChiTietList.stream()
                .map(SanPhamChiTietServiceImpl::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SanPhamChiTietDTO> findAllSanPhamChiTiet() {
        List<SanPhamChiTiet> sanPhamChiTietList = sanPhamChiTietRepository.findAll();
        return sanPhamChiTietList.stream()
                .map(SanPhamChiTietServiceImpl::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SanPhamChiTietDTO> filterSanPhamChiTiet(
            String keyword,
            List<String> listTenAoDai,
            List<String> listMauSac,
            List<String> listKichThuoc,
            Boolean trangThai,
            String sort,
            int page,
            int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPhamChiTiet> sanPhamChiTiets = sanPhamChiTietRepository.filterSanPhamChiTiet(removeAccentAndToLower(keyword) ,listTenAoDai, listMauSac, listKichThuoc, trangThai, sort, pageable);
        return sanPhamChiTiets.map(SanPhamChiTietServiceImpl::toDTO);
    }

    @Override
    @Transactional
    public SanPhamChiTietDTO addSanPhamChiTiet(SanPhamChiTietDTO sanPhamChiTietDTO) {
        if(sanPhamChiTietRepository.existsByAoDaiIdAndMauSacIdAndKichThuocId(sanPhamChiTietDTO.getAoDaiId(), sanPhamChiTietDTO.getMauSacId(), sanPhamChiTietDTO.getKichThuocId())) {
            throw new DuplicateFieldException(Map.of("aoDaiId", "Biến thể đã tồn tại", "mauSacId", sanPhamChiTietDTO.getMauSacId().toString(), "kichThuocId", sanPhamChiTietDTO.getKichThuocId().toString()));
        }
        SanPhamChiTiet sanPhamChiTiet = toEntity(sanPhamChiTietDTO);

        sanPhamChiTiet.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        sanPhamChiTiet.setGiaBan(sanPhamChiTiet.getGiaGoc());

        SanPhamChiTiet savedSanPhamChiTiet = sanPhamChiTietRepository.save(sanPhamChiTiet);

        return toDTO(savedSanPhamChiTiet);
    }

    @Transactional
    @Override
    public List<SanPhamChiTietDTO> addSanPhamChiTiets(List<SanPhamChiTietDTO> sanPhamChiTietDTOs) {
        if (sanPhamChiTietDTOs == null || sanPhamChiTietDTOs.isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm chi tiết không được trống");
        }
        List<SanPhamChiTiet> entities = sanPhamChiTietDTOs.stream()
                .map(dto -> {
                    if(sanPhamChiTietRepository.existsByAoDaiIdAndMauSacIdAndKichThuocId(dto.getAoDaiId(), dto.getMauSacId(), dto.getKichThuocId())) {
                        String tenMauSac = mauSacRepository.findById(dto.getMauSacId()).get().getTenMauSac();
                        String tenKichThuoc = kichThuocRepository.findById(dto.getKichThuocId()).get().getTen();
                        throw new DuplicateFieldException(Map.of("duplicate", "Biến thể có màu "+ tenMauSac + ", kích thức " + tenKichThuoc + " đã tồn tại"));
                    }
                    SanPhamChiTiet entity = toEntity(dto);
                    entity.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                    entity.setGiaBan(dto.getGiaGoc());
                    return entity;
                })
                .collect(Collectors.toList());

        List<SanPhamChiTiet> savedEntities = sanPhamChiTietRepository.saveAll(entities);

        return savedEntities.stream().map(SanPhamChiTietServiceImpl::toDTO).collect(Collectors.toList());
    }

    @Override
    public SanPhamChiTietDTO updateSanPhamChiTiet(int id, SanPhamChiTietDTO sanPhamChiTietDTO) {
        Optional<SanPhamChiTiet> existingSanPhamChiTietOptional = sanPhamChiTietRepository.findById(id);

        if (existingSanPhamChiTietOptional.isPresent()) {
            SanPhamChiTiet existingSanPhamChiTiet = existingSanPhamChiTietOptional.get();

            // Cập nhật thông tin cơ bản
            existingSanPhamChiTiet.setGiaGoc(sanPhamChiTietDTO.getGiaGoc());
            // Mặc định giá bán = giá gốc
            existingSanPhamChiTiet.setGiaBan(sanPhamChiTietDTO.getGiaGoc());

            // Nếu có mã khuyến mãi thì xử lý cập nhật giá bán theo khuyến mãi
            if (sanPhamChiTietDTO.getMaKhuyenMai() != null) {
                ZonedDateTime now = ZonedDateTime.now();

                if (sanPhamChiTietDTO.getNgayBatDau() != null &&
                        sanPhamChiTietDTO.getNgayKetThuc() != null &&
                        sanPhamChiTietDTO.getTrangThaiKhuyenMai() != null &&
                        sanPhamChiTietDTO.getTrangThaiKhuyenMai()) {

                    if (now.isAfter(sanPhamChiTietDTO.getNgayBatDau()) && now.isBefore(sanPhamChiTietDTO.getNgayKetThuc())) {
                        double discount = 0.0;

                        if (sanPhamChiTietDTO.getPhanTramGiam() != 0) {
                            discount = sanPhamChiTietDTO.getGiaGoc() * (sanPhamChiTietDTO.getPhanTramGiam() / 100.0);
                        } else if (sanPhamChiTietDTO.getSoTienGiam() != 0) {
                            discount = sanPhamChiTietDTO.getSoTienGiam();
                        }

                        double newGiaBan = sanPhamChiTietDTO.getGiaGoc() - discount;

                        // Nếu giá bán bị âm, đặt giá bán về 0
                        existingSanPhamChiTiet.setGiaBan(Math.max(newGiaBan, 0));

                        KhuyenMaiSP khuyenMai = new KhuyenMaiSP();
                        khuyenMai.setId(sanPhamChiTietDTO.getKhuyenMaiId());
                        existingSanPhamChiTiet.setKhuyenMaiSP(khuyenMai);
                    }
                }
            }

            // Cập nhật các thông tin khác
            existingSanPhamChiTiet.setSoLuong(sanPhamChiTietDTO.getSoLuong());
            existingSanPhamChiTiet.setTrangThai(sanPhamChiTietDTO.getTrangThai());
            existingSanPhamChiTiet.setAnhUrl(sanPhamChiTietDTO.getAnhUrl());

            if (sanPhamChiTietDTO.getAoDaiId() != null) {
                SanPham sanPham = new SanPham();
                sanPham.setId(sanPhamChiTietDTO.getAoDaiId());
                existingSanPhamChiTiet.setAoDai(sanPham);
            }
            if (sanPhamChiTietDTO.getMauSacId() != null) {
                MauSac mauSac = new MauSac();
                mauSac.setId(sanPhamChiTietDTO.getMauSacId());
                existingSanPhamChiTiet.setMauSac(mauSac);
            }
            if (sanPhamChiTietDTO.getKichThuocId() != null) {
                KichThuoc kichThuoc = new KichThuoc();
                kichThuoc.setId(sanPhamChiTietDTO.getKichThuocId());
                existingSanPhamChiTiet.setKichThuoc(kichThuoc);
            }

            SanPhamChiTiet updatedSanPhamChiTiet = sanPhamChiTietRepository.save(existingSanPhamChiTiet);

            return toDTO(updatedSanPhamChiTiet);
        } else {
            return null;
        }
    }



    @Override
    public SanPhamChiTietDTO getSanPhamChiTietById(int id) {
        Optional<SanPhamChiTiet> sanPhamChiTietOptional = sanPhamChiTietRepository.findById(id);
        return sanPhamChiTietOptional.map(SanPhamChiTietServiceImpl::toDTO).orElse(null);
    }
}
