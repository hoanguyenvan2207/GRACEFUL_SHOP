package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.AnhDTO;
import com.example.datn_be.dto.sanpham.SanPhamChiTietDTO;
import com.example.datn_be.dto.sanpham.SanPhamDTO;
import com.example.datn_be.entiy.san_pham.*;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.san_pham.AnhRepository;
import com.example.datn_be.repository.san_pham.SanPhamChiTietRepository;
import com.example.datn_be.repository.san_pham.SanPhamRepository;
import com.example.datn_be.service.sanpham.AnhService;
import com.example.datn_be.service.sanpham.SanPhamService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private AnhRepository anhRepository;

    @Autowired
    private AnhService anhService;

    @Autowired
    private SanPhamChiTietRepository sanPhamChiTietRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Chuyển từ Entity sang DTO
    public static SanPhamDTO toDTO(SanPham sanPham) {
        if (sanPham == null) {
            return null;
        }

        List<String> anhUrls = Optional.ofNullable(sanPham.getAnhList())
                .orElse(List.of())  // Trả về danh sách rỗng nếu null
                .stream()
                .map(Anh::getAnhUrl)
                .collect(Collectors.toList());

        // Lấy danh sách chi tiết sản phẩm, tránh NullPointerException
        List<SanPhamChiTietDTO> chiTietDTOs = Optional.ofNullable(sanPham.getSanPhamChiTietList())
                .orElse(List.of())
                .stream()
                .map(SanPhamChiTietServiceImpl::toDTO)
                .toList();

        return new SanPhamDTO(
                sanPham.getId(),
                sanPham.getMaAoDai(),
                sanPham.getTenAoDai(),
                sanPham.getMoTa(),
                sanPham.getLinkYoutube(),
                sanPham.getTrangThai(),
                sanPham.getNgayTao(),
                sanPham.getLoaiAoDai() != null ? sanPham.getLoaiAoDai().getId() : null,
                sanPham.getLoaiAoDai() != null ? sanPham.getLoaiAoDai().getTenLoaiAoDai() : null,
                sanPham.getChatLieu() != null ? sanPham.getChatLieu().getId() : null,
                sanPham.getChatLieu() != null ? sanPham.getChatLieu().getTen() : null,
                sanPham.getTaAo() != null ? sanPham.getTaAo().getId() : null,
                sanPham.getTaAo() != null ? sanPham.getTaAo().getTen() : null,
                sanPham.getNhaCungCap() != null ? sanPham.getNhaCungCap().getId() : null,
                sanPham.getNhaCungCap() != null ? sanPham.getNhaCungCap().getTenNhaCungCap() : null,
                anhUrls,
                sanPham.getSanPhamChiTietList() != null ? sanPham.getSanPhamChiTietList().stream().map(SanPhamChiTietServiceImpl::toDTO).toList() : null
        );
    }

    // Chuyển từ DTO sang Entity
    public static SanPham toEntity(SanPhamDTO dto) {
        if (dto == null) {
            return null;
        }
        SanPham sanPham = new SanPham();
        sanPham.setId(dto.getId());
        sanPham.setMaAoDai(dto.getMaAoDai());
        sanPham.setTenAoDai(dto.getTenAoDai());
        sanPham.setMoTa(dto.getMoTa());
        sanPham.setLinkYoutube(dto.getLinkYoutube());
        sanPham.setTrangThai(dto.getTrangThai());
        sanPham.setNgayTao(dto.getNgayTao());

        checkIdValid(dto, sanPham);

        return sanPham;
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
    public List<SanPhamDTO> getSanPhamByTrangThaiTrue() {
        return sanPhamRepository.findSanPhamsByTrangThaiIsTrue().stream().map(SanPhamServiceImpl::toDTO).toList();
    }

    @Override
    public Page<SanPhamDTO> searchAndFilterSanPham(
            String keyword,
            List<String> tenLoaiAoDai,
            List<String> tenChatLieu,
            List<String> tenTaAo,
            List<String> tenNhaCungCap,
            Boolean trangThai,
            String sortDate,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SanPham> sanPhams = sanPhamRepository.searchAndFilterSanPham(
                keyword, tenLoaiAoDai, tenChatLieu, tenTaAo, tenNhaCungCap, trangThai, sortDate, pageable);
        return sanPhams.map(SanPhamServiceImpl::toDTO);
    }


    @Override
    public Page<SanPhamDTO> filterSanPham(
            List<String> listLoaiAoDai,
            List<String> listChatLieu,
            List<String> listTaAo,
            List<String> listNhaCungCap,
            List<String> listMauSac,
            List<String> listKichThuoc,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword,
            String sortPrice,
            String sortDate,
            int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> sanPhams = sanPhamRepository.filterProducts(listLoaiAoDai, listChatLieu, listTaAo, listNhaCungCap, listMauSac, listKichThuoc, minPrice, maxPrice, keyword, sortPrice, sortDate, pageable);
        return sanPhams.map(SanPhamServiceImpl::toDTO);
    }

    @Override
    public SanPhamDTO getSanPhamById(int id) {
        Optional<SanPham> sanPham = sanPhamRepository.findById(id);
        return sanPham.map(SanPhamServiceImpl::toDTO).orElse(null);
    }

    @Override
    public SanPhamDTO getSanPhamByCode(String ma) {
        Optional<SanPham> sanPham = sanPhamRepository.findSanPhamByMaAoDai(ma);
        return sanPham.map(SanPhamServiceImpl::toDTO).orElse(null);
    }

    @Override
    public List<SanPhamDTO> getTop5SanPhamBanChay() {
        return sanPhamRepository.findTopSellingProducts()
                .stream()
                .map(SanPhamServiceImpl::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllProducts() {
        try {
            String body = restTemplate.getForObject(
                    "http://localhost:8080/api/san-pham/list/filter-products",
                    String.class
            );
            JsonNode root = objectMapper.readTree(body);

            JsonNode contentNode = root.path("content");
            if (!contentNode.isArray()) {
                return new ArrayList<>();
            }

            return objectMapper.convertValue(
                    contentNode,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getTopProductsSale() {
        try {
            String body = restTemplate.getForObject(
                    "http://localhost:8080/api/san-pham/top-sell",
                    String.class
            );
            JsonNode root = objectMapper.readTree(body);

            JsonNode arrayNode;
            if (root.isArray()) {
                arrayNode = root;
            } else {
                arrayNode = root.path("content");
            }

            if (!arrayNode.isArray()) {
                return new ArrayList<>();
            }

            return objectMapper.convertValue(
                    arrayNode,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    @Override
    public List<SanPhamDTO> getSanPhamAll() {
        List<SanPham> sanPham = sanPhamRepository.findAll();
        return sanPham.stream().map(SanPhamServiceImpl::toDTO).toList();
    }

    @Override
    @Transactional
    public SanPhamDTO addSanPham(SanPhamDTO sanPhamDTO) {
        if (sanPhamRepository.existsSanPhamByTenAoDai(sanPhamDTO.getTenAoDai())) {
            throw new DuplicateFieldException(Map.of("tenAoDai", "Tên áo dài đã tồn tại."));
        }

        try {
            SanPham sanPham = toEntity(sanPhamDTO);
            sanPham.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            SanPham savedSanPham = sanPhamRepository.save(sanPham);

            if (sanPhamDTO.getAnhList() != null && !sanPhamDTO.getAnhList().isEmpty()) {
                createAnh(sanPhamDTO, savedSanPham);
            }

            if (sanPhamDTO.getSanPhamChiTietList() != null && !sanPhamDTO.getSanPhamChiTietList().isEmpty()) {
                List<SanPhamChiTiet> chiTietList = sanPhamDTO.getSanPhamChiTietList().stream()
                        .map(dto -> {
                            SanPhamChiTiet chiTiet = SanPhamChiTietServiceImpl.toEntity(dto);
                            chiTiet.setAoDai(savedSanPham);
                            chiTiet.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
                            return chiTiet;
                        })
                        .collect(Collectors.toList());

                sanPhamChiTietRepository.saveAll(chiTietList);
            }

            return toDTO(savedSanPham);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm sản phẩm", e);
        }
    }

    @Override
    @Transactional
    public List<SanPhamDTO> addSanPhams(List<SanPhamDTO> sanPhamDTOList) {
        List<SanPhamDTO> addedSanPhams = new ArrayList<>();

        for (SanPhamDTO sanPhamDTO : sanPhamDTOList) {
            if (sanPhamRepository.existsSanPhamByTenAoDai(sanPhamDTO.getTenAoDai())) {
                throw new DuplicateFieldException(Map.of("tenAoDai", "Tên áo dài: '" + sanPhamDTO.getTenAoDai() + "' đã tồn tại."));
            }
            try {
                SanPham sanPham = toEntity(sanPhamDTO);
                sanPham.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))); // Đặt ngày tạo
                SanPham savedSanPham = sanPhamRepository.save(sanPham);

                // Lưu danh sách ảnh từ URL
                if (sanPhamDTO.getAnhList() != null && !sanPhamDTO.getAnhList().isEmpty()) {
                    createAnh(sanPhamDTO, savedSanPham);
                }

                addedSanPhams.add(toDTO(savedSanPham));
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi khi thêm sản phẩm: " + sanPhamDTO.getTenAoDai());
            }
        }

        return addedSanPhams;
    }

    @Override
    @Transactional
    public SanPhamDTO updateSanPham(int id, SanPhamDTO sanPhamDTO) {
        // Tìm sản phẩm hiện tại trong cơ sở dữ liệu
        Optional<SanPham> existingSanPhamOptional = sanPhamRepository.findById(id);
        if (existingSanPhamOptional.isPresent()) {
            SanPham existingSanPham = existingSanPhamOptional.get();

            if(!existingSanPham.getTenAoDai().equals(sanPhamDTO.getTenAoDai()) &&
                    sanPhamRepository.existsSanPhamByTenAoDai(sanPhamDTO.getTenAoDai())) {
                throw new DuplicateFieldException(Map.of("tenAoDai", "Tên áo dài đã tồn tại."));
            }

            // Cập nhật thông tin sản phẩm
            existingSanPham.setTenAoDai(sanPhamDTO.getTenAoDai());
            existingSanPham.setMoTa(sanPhamDTO.getMoTa());
            existingSanPham.setLinkYoutube(sanPhamDTO.getLinkYoutube());
            existingSanPham.setTrangThai(sanPhamDTO.getTrangThai());
            checkIdValid(sanPhamDTO, existingSanPham);

            // Xử lý cập nhật ảnh
            handleAnhUpdate(existingSanPham, sanPhamDTO.getAnhList());

            // Lưu sản phẩm đã cập nhật
            SanPham updatedSanPham = sanPhamRepository.save(existingSanPham);
            return toDTO(updatedSanPham);
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm với ID: " + id);
        }
    }

    private void handleAnhUpdate(SanPham existingSanPham, List<String> newAnhList) {
        List<Anh> existingAnhList = anhRepository.findAllBySanPhamId(existingSanPham.getId());

        Set<String> existingAnhUrls = existingAnhList.stream()
                .map(Anh::getAnhUrl)
                .collect(Collectors.toSet());

        Set<String> newAnhUrls = newAnhList != null ? new HashSet<>(newAnhList) : new HashSet<>();

        Set<String> anhToRemove = existingAnhUrls.stream()
                .filter(url -> !newAnhUrls.contains(url))
                .collect(Collectors.toSet());

        Set<String> anhToAdd = newAnhUrls.stream()
                .filter(url -> !existingAnhUrls.contains(url))
                .collect(Collectors.toSet());

        if (!anhToRemove.isEmpty()) {
            anhRepository.deleteAllByAnhUrlsAndSanPhamId(anhToRemove, existingSanPham.getId());
        }

        for (String url : anhToAdd) {
            AnhDTO anhDTO = new AnhDTO();
            anhDTO.setAnhUrl(url);
            anhDTO.setTrangThai(true);
            anhDTO.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            anhDTO.setSanPhamId(existingSanPham.getId());
            anhService.createAnh(anhDTO);
        }
    }

    private static void checkIdValid(SanPhamDTO sanPhamDTO, SanPham existingSanPham) {
        if (sanPhamDTO.getLoaiAoDaiId() != null) {
            LoaiSanPham loaiSanPham = new LoaiSanPham();
            loaiSanPham.setId(sanPhamDTO.getLoaiAoDaiId());
            existingSanPham.setLoaiAoDai(loaiSanPham);
        }

        if (sanPhamDTO.getChatLieuId() != null) {
            ChatLieu chatLieu = new ChatLieu();
            chatLieu.setId(sanPhamDTO.getChatLieuId());
            existingSanPham.setChatLieu(chatLieu);
        }

        if (sanPhamDTO.getTaAoId() != null) {
            TaAo taAo = new TaAo();
            taAo.setId(sanPhamDTO.getTaAoId());
            existingSanPham.setTaAo(taAo);
        }

        if (sanPhamDTO.getNhaCungCapId() != null) {
            NhaCungCap nhaCungCap = new NhaCungCap();
            nhaCungCap.setId(sanPhamDTO.getNhaCungCapId());
            existingSanPham.setNhaCungCap(nhaCungCap);
        }
    }

    private void createAnh(SanPhamDTO sanPhamDTO, SanPham existingSanPham) {
        if (sanPhamDTO.getAnhList() == null || sanPhamDTO.getAnhList().isEmpty()) {
            return;
        }

        for (String url : sanPhamDTO.getAnhList()) {
            AnhDTO anh = new AnhDTO();
            anh.setAnhUrl(url);
            anh.setTrangThai(true);
            anh.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
            anh.setSanPhamId(existingSanPham.getId());
            anhService.createAnh(anh); // Đảm bảo rằng anhService hoạt động đúng
        }
    }
}

