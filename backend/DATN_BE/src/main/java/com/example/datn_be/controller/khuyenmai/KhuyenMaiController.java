package com.example.datn_be.controller.khuyenmai;
import com.example.datn_be.dto.khuyenmai.KhuyenMaiDTO;
import com.example.datn_be.dto.khuyenmai.SPCTDTO;
import com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO;
import com.example.datn_be.entiy.giam_gia.KhuyenMai;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.KhuyenMaiRepository;
import com.example.datn_be.repository.SanPhamChiTietKMRepository;
import com.example.datn_be.service.khuyenmai.KhuyenMaiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/khuyen-mai")
public class KhuyenMaiController {
    @Autowired
    KhuyenMaiService khuyenMaiService;
    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private SanPhamChiTietKMRepository sanPhamChiTietRepository;

    @GetMapping("/lay-san-pham-chi-tiet")
    public ResponseEntity<List<SanPhamChiTietKhuyenMaiDTO>> getSanPhamChiTietDetails(@RequestParam Integer idKhuyenMai) {
        List<SanPhamChiTietKhuyenMaiDTO> sanPhamChiTietDetails = sanPhamChiTietRepository.findDetailsByKhuyenMaiId(idKhuyenMai);
        return ResponseEntity.ok(sanPhamChiTietDetails);
    }

    @GetMapping("/tim-kiem-khuyen-mai")
    public Page<KhuyenMai> timKiem(@RequestParam(value = "keyq",required = false) String keyq,
                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "pageSize" ,defaultValue = "5") int pageSize,
                                   @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                   @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                   @RequestParam(value = "status",required = false) Boolean status,
                                   @RequestParam(value = "validity",required = false) Integer validity) {
        Integer validityParam = (validity == null) ? -1 : validity;
        Pageable pageable = PageRequest.of(page, pageSize);
        return khuyenMaiService.seachByKeyq( keyq,  pageable,  pageSize,  validityParam,  status ,startDate,endDate);
    }


    @GetMapping("/kiem-tra-san-pham-khuyen-mai")
    public List<Integer> kiemTraSanPhamTrongKhuyenMai(@RequestParam String sanPhamIds) {
        List<Integer> ids = Arrays.stream(sanPhamIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return sanPhamChiTietRepository.timSanPhamTrongChuongTrinhKhuyenMai(ids);
    }


    @GetMapping("/hien-thi")
    public Map<String,Object> getAllGiamGia(@RequestParam(value = "page" , defaultValue = "0") Integer page ,
                                            @RequestParam(value = "pageSize" ,defaultValue = "5") Integer pageSize){
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<KhuyenMaiDTO> pageResult = khuyenMaiService.getAll(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalPages", pageResult.getTotalPages());
        return response;
    }
    @GetMapping("/tim-kiem-spct")
    public Page<SPCTDTO> timKiem(@RequestParam("keyq") String keyq,
                                 @RequestParam(value = "page", defaultValue = "0") Integer page) {
        int pageSize = 5; // Kích thước trang đã mặc định
        Pageable pageable = PageRequest.of(page, pageSize);
        return sanPhamChiTietRepository.searchSpctByKey(keyq,pageable);
    }
    @GetMapping("/tim-sp-by-id-km")
    public List<SanPhamChiTietKhuyenMaiDTO> timSPByidKm(Integer idKm){
        return sanPhamChiTietRepository.getListSanbyIdKhuyenMai(idKm);
    }
    @GetMapping("/spct")
    public Map<String,Object> getAllspctkm(@RequestParam(value = "page" , defaultValue = "0") Integer page){
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page,pageSize);
        Page<SanPhamChiTietKhuyenMaiDTO> pageResult = sanPhamChiTietRepository.getListSPCT(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalPages", pageResult.getTotalPages());
        return response;
    }


    @PostMapping("/them")
    public ResponseEntity<?> themKhuyenMai(@Valid @RequestBody KhuyenMaiDTO request ) {
        if (request == null || request.getTenKhuyenMai() == null || request.getPhanTramGiam() == null) {
            return ResponseEntity.badRequest().body("Thêm thất bại: Dữ liệu không hợp lệ");
        }
        try {
            // Chuyển đổi từ KhuyenMaiRequest sang KhuyenMai entity
            KhuyenMai khuyenMai = new KhuyenMai();
            khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
            khuyenMai.setSoTienGiam(request.getSoTienGiam());
            khuyenMai.setPhanTramGiam(request.getPhanTramGiam());
            khuyenMai.setNgayBatDau(request.getNgayBatDau());
            khuyenMai.setNgayKetThuc(request.getNgayKetThuc());
            khuyenMai.setTrangThai(request.getTrangThai());
            khuyenMai.setMoTa(request.getMoTa());
            // Gọi service để lưu khuyến mãi và cập nhật sản phẩm
            khuyenMaiService.themKhuyenMai(khuyenMai, request.getSanPhamChiTietIds());

            return ResponseEntity.ok("Thêm khuyến mãi thành công và cập nhật giá cho các sản phẩm đã chọn");
        } catch (DuplicateFieldException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", e.getFieldErrors()));
        }
    }

    @PutMapping("/cap-nhat")
    public ResponseEntity<?> capNhatKhuyenMai(
            @RequestBody KhuyenMaiDTO request) {
        KhuyenMai khuyenMai = khuyenMaiService.findByID(request.getId());
        // Cập nhật thông tin khuyến mãi
        khuyenMai.setSoTienGiam(request.getSoTienGiam());
        khuyenMai.setTenKhuyenMai(request.getTenKhuyenMai());
        khuyenMai.setPhanTramGiam(request.getPhanTramGiam());
        khuyenMai.setNgayBatDau(request.getNgayBatDau());
        khuyenMai.setNgayKetThuc(request.getNgayKetThuc());
        khuyenMai.setTrangThai(request.getTrangThai());
        khuyenMai.setMoTa(request.getMoTa());

        // Cập nhật khuyến mãi và danh sách sản phẩm chi tiết
        try {
            khuyenMaiService.capNhat(khuyenMai, request.getSanPhamChiTietIds());
            return ResponseEntity.ok("Cập nhật khuyến mãi thành công!");
        } catch (DuplicateFieldException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", e.getFieldErrors()));
        }
    }

    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable("id") Integer id){
        khuyenMaiService.xoa(id);
        return "xóa thành công";
    }


}