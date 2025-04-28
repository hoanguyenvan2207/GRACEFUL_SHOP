package com.example.datn_be.service.khuyenmai;
import com.example.datn_be.dto.khuyenmai.KhuyenMaiDTO;
import com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO;
import com.example.datn_be.entiy.giam_gia.KhuyenMai;
import com.example.datn_be.entiy.giam_gia.SanPhamChiTietKM;
import com.example.datn_be.exception.DuplicateFieldException;
import com.example.datn_be.repository.KhuyenMaiRepository;
import com.example.datn_be.repository.SanPhamChiTietKMRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {
    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    SanPhamChiTietKMRepository sanPhamChiTietRepository;
    public Page<KhuyenMai>seachByKeyq(String keyq, Pageable pageable, int pageSize, int validity, Boolean status , LocalDate startDate, LocalDate endDate) {
        return khuyenMaiRepository.searchByKey(keyq,status,validity,pageable,startDate,endDate,pageSize);
    }

    public Page<KhuyenMaiDTO> getAll(Pageable pageable){
        Page<KhuyenMai> pageResult = khuyenMaiRepository.getListKM(pageable);

        return pageResult.map(km -> {
            List<Integer> sanPhamIds = km.getSanPhamChiTietList().stream()
                    .map(SanPhamChiTietKM::getId)
                    .collect(Collectors.toList());

            return new KhuyenMaiDTO(
                    km.getId(),
                    km.getMaKhuyenMai(),
                    km.getTenKhuyenMai(),
                    km.getSoTienGiam(),
                    km.getPhanTramGiam(),
                    km.getNgayBatDau(),
                    km.getNgayKetThuc(),
                    km.getTrangThai(),
                    km.getMoTa(),
                    sanPhamIds // Chỉ lấy danh sách ID, không phải danh sách đối tượng
            );
        });
    }


    @Transactional
    public void capNhat( KhuyenMai khuyenMai, List<Integer> sanPhamChiTietIds) {

        if (khuyenMaiRepository.existsByTenKhuyenMaiAndIdNot(
                khuyenMai.getTenKhuyenMai(), khuyenMai.getId())) {
            throw new DuplicateFieldException(
                    Map.of("tenKhuyenMai", "Tên khuyến mãi đã tồn tại")
            );
        }
        // Cập nhật thông tin khuyến mãi
        KhuyenMai savedKhuyenMai = khuyenMaiRepository.save(khuyenMai);

        // Lấy tất cả sản phẩm hiện đang có trong khuyến mãi
        List<SanPhamChiTietKM> danhSachSanPhamHienTai = sanPhamChiTietRepository.findByKhuyenMaiId(khuyenMai.getId());
        LocalDate now = LocalDate.now();

        // Thiết lập thời gian bắt đầu và kết thúc (kết thúc tính đến hết ngày)
        LocalDate batDau = khuyenMai.getNgayBatDau();
        LocalDate ketThuc = khuyenMai.getNgayKetThuc();

        // Loại bỏ sản phẩm không có trong danh sách mới, set khuyến mãi về null
        for (SanPhamChiTietKM spct : danhSachSanPhamHienTai) {
            if (!sanPhamChiTietIds.contains(spct.getId())) {
                spct.setKhuyenMai(null);              // Xóa khuyến mãi khỏi sản phẩm
                spct.setGiaBan(spct.getGiaGoc());     // Trả về giá gốc
            }
        }

        // Cập nhật thông tin cho các sản phẩm trong danh sách mới
        List<SanPhamChiTietKM> danhSachSanPhamMoi = sanPhamChiTietRepository.findAllById(sanPhamChiTietIds);
        for (SanPhamChiTietKM spct : danhSachSanPhamMoi) {
            spct.setKhuyenMai(khuyenMai);

            // Kiểm tra nếu ngày hiện tại nằm trong khoảng thời gian khuyến mãi (bao gồm cả ngày kết thúc)
            if (!now.isBefore(batDau) && !now.isAfter(ketThuc) && Boolean.TRUE.equals(khuyenMai.getTrangThai())) {
                // Khuyến mãi theo phần trăm
                if (khuyenMai.getPhanTramGiam() != null && khuyenMai.getPhanTramGiam() > 0) {
                    spct.setGiaBan(spct.getGiaGoc() * (1 - (khuyenMai.getPhanTramGiam() / 100.0)));
                }
                // Khuyến mãi theo số tiền
                else if (khuyenMai.getSoTienGiam() != null && khuyenMai.getSoTienGiam() > 0) {
                    double giaBanMoi = spct.getGiaGoc() - khuyenMai.getSoTienGiam();
                    spct.setGiaBan(Math.max(giaBanMoi, 0));
                }
            } else {
                spct.setGiaBan(spct.getGiaGoc());  // Đưa giá về giá gốc nếu không nằm trong thời gian khuyến mãi
            }
        }

        sanPhamChiTietRepository.saveAll(danhSachSanPhamHienTai);
        sanPhamChiTietRepository.saveAll(danhSachSanPhamMoi);
    }




    public void xoa(Integer id){
        khuyenMaiRepository.deleteById(id);
    };
    @Transactional
    public void themKhuyenMai(@Valid KhuyenMai khuyenMai, List<Integer> sanPhamChiTietIds) {
        if(khuyenMaiRepository.existsKhuyenMaiByTenKhuyenMai(khuyenMai.getTenKhuyenMai())) {
            throw new DuplicateFieldException(Map.of("tenKhuyenMai", "Tên khuyến mãi đã tồn tại"));
        }
        khuyenMai.setNgayTao(LocalDateTime.now());
        KhuyenMai savedKhuyenMai = khuyenMaiRepository.save(khuyenMai);

        if (sanPhamChiTietIds == null || sanPhamChiTietIds.isEmpty()) {
            return; // Không cập nhật sản phẩm nào
        }

        List<SanPhamChiTietKM> danhSachSanPham = sanPhamChiTietRepository.findAllById(sanPhamChiTietIds);
        LocalDate now = LocalDate.now();

        // Thiết lập thời gian bắt đầu và kết thúc (tính đến hết ngày kết thúc)
        LocalDate batDau = khuyenMai.getNgayBatDau();
        LocalDate ketThuc = khuyenMai.getNgayKetThuc();

        for (SanPhamChiTietKM spct : danhSachSanPham) {
            spct.setKhuyenMai(savedKhuyenMai);

            // Kiểm tra nếu khuyến mãi đang trong thời gian hoạt động và đang được bật
            if (!now.isBefore(batDau) && !now.isAfter(ketThuc) && Boolean.TRUE.equals(khuyenMai.getTrangThai())) {
                // Nếu khuyến mãi theo phần trăm
                if (savedKhuyenMai.getPhanTramGiam() != null && savedKhuyenMai.getPhanTramGiam() > 0) {
                    spct.setGiaBan(spct.getGiaGoc() * (1 - (savedKhuyenMai.getPhanTramGiam() / 100.0)));
                }
                // Nếu khuyến mãi theo số tiền giảm
                else if (savedKhuyenMai.getSoTienGiam() != null && savedKhuyenMai.getSoTienGiam() > 0) {
                    double giaBanMoi = spct.getGiaGoc() - savedKhuyenMai.getSoTienGiam();
                    spct.setGiaBan(Math.max(giaBanMoi, 0)); // Đảm bảo giá bán không âm
                }
            } else {
                spct.setGiaBan(spct.getGiaGoc()); // Giữ nguyên giá gốc nếu không nằm trong thời gian khuyến mãi
            }
        }

        sanPhamChiTietRepository.saveAll(danhSachSanPham);
    }



    public KhuyenMai findByID(Integer id) {
        return khuyenMaiRepository.findById(id).orElse(null);
    }


    public KhuyenMaiService(KhuyenMaiRepository khuyenMaiRepository, SanPhamChiTietKMRepository sanPhamChiTietRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
    }

    @Scheduled(cron = "0 * * * * ?") // Chạy mỗi phút tại giây 0

    @Transactional
    public void capNhatKhuyenMaiTuDong() {
        LocalDate now = LocalDate.now();
        List<KhuyenMai> danhSachKhuyenMai = khuyenMaiRepository.findAll();

        for (KhuyenMai km : danhSachKhuyenMai) {
            // Kiểm tra nếu ngày kết thúc khuyến mãi đã qua (bao gồm cả ngày và giờ)
            if (km.getNgayKetThuc().isBefore(now)) {
                // Cập nhật tất cả sản phẩm liên quan về giá gốc
                List<SanPhamChiTietKM> danhSachSanPham = sanPhamChiTietRepository.findByKhuyenMaiId(km.getId());
                for (SanPhamChiTietKM spct : danhSachSanPham) {
                    spct.setGiaBan(spct.getGiaGoc()); // Đưa giá về giá gốc
                }
                sanPhamChiTietRepository.saveAll(danhSachSanPham);
            }
        }
    }



}