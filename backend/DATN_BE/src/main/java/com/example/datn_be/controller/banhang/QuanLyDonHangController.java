package com.example.datn_be.controller.banhang;

import com.example.datn_be.entiy.GiamGiaBH;
import com.example.datn_be.entiy.HoaDonBH;
import com.example.datn_be.entiy.HoaDonChiTietBH;
import com.example.datn_be.repository.AoDaiChiTietBHRepo;
import com.example.datn_be.repository.GiamGiaBHRepo;
import com.example.datn_be.repository.HoaDonChiTietRepo;
import com.example.datn_be.repository.QuanLyDonHangRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/quan-ly-don-hang")
public class QuanLyDonHangController {

    @Autowired
    QuanLyDonHangRepo quanLyDonHangRepo;

    @Autowired
    HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    AoDaiChiTietBHRepo aoDaiChiTietBHRepo;

    @Autowired
    GiamGiaBHRepo giamGiaBHRepo;

    @GetMapping("/hien-thi-phan-trang")
    public ResponseEntity<?> QuanLyDonHangPhanTrang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate) {
        Pageable pageable = PageRequest.of(page, 7, Sort.by("ngayTao").descending());
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (keyword != null && !keyword.trim().isEmpty()) {
                return ResponseEntity.ok(
                        quanLyDonHangRepo.searchOnlineOrdersByKeyword(keyword, pageable)
                );
            }

            boolean hasDate = fromDate != null && !fromDate.trim().isEmpty()
                    && toDate != null && !toDate.trim().isEmpty();

            if (hasDate) {
                Date startDate = sdf.parse(fromDate);
                Date endDate = sdf.parse(toDate);
                if (startDate.after(endDate)) {
                    return ResponseEntity.badRequest()
                            .body("Ngày bắt đầu không được lớn hơn ngày kết thúc.");
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                endDate = cal.getTime();

                if (trangThai != null && !trangThai.trim().isEmpty()) {
                    return ResponseEntity.ok(
                            quanLyDonHangRepo.findByHinhThucMuaHangAndTrangThaiAndNgayTaoBetween(
                                    true, trangThai, startDate, endDate, pageable)
                    );
                } else {
                    // Chỉ lọc theo ngày
                    return ResponseEntity.ok(
                            quanLyDonHangRepo.findByHinhThucMuaHangAndNgayTaoBetween(
                                    true, startDate, endDate, pageable)
                    );
                }
            }

            if (trangThai != null && !trangThai.trim().isEmpty()) {
                return ResponseEntity.ok(
                        quanLyDonHangRepo.findByHinhThucMuaHangAndTrangThai(true, trangThai, pageable)
                );
            }
            return ResponseEntity.ok(
                    quanLyDonHangRepo.findByHinhThucMuaHang(true, pageable)
            );
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Định dạng ngày không hợp lệ.");
        }
    }



    @GetMapping("/chi-tiet")
    public java.util.List<HoaDonChiTietBH> getHoaDonChiTietByHoaDonId(@RequestParam("idHoaDon") Integer idHoaDon) {
        return hoaDonChiTietRepo.findByHoaDonId(idHoaDon);
    }

    @Transactional
    @PatchMapping("/cap-nhat-trang-thai/{id}")
    public ResponseEntity<?> capNhatTrangThaiDonHang(
            @PathVariable Integer id,
            @RequestParam String trangThai) {
        try {
            Optional<HoaDonBH> optionalDonHang = quanLyDonHangRepo.findById(id);
            if (!optionalDonHang.isPresent()) {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng");
            }
            HoaDonBH donHang = optionalDonHang.get();
            List<String> steps = Arrays.asList("Chờ xác nhận", "Đã xác nhận", "Đang giao hàng", "Đã nhận hàng");

            int currentIndex = steps.indexOf(donHang.getTrangThai());
            if (currentIndex == -1) {
                return ResponseEntity.badRequest().body("Trạng thái hiện tại của đơn hàng không hợp lệ");
            }
            if (currentIndex >= steps.size() - 1) {
                return ResponseEntity.badRequest().body("Đơn hàng đã đạt trạng thái cuối cùng");
            }

            String allowedNextStatus = steps.get(currentIndex + 1);
            if (!allowedNextStatus.equals(trangThai)) {
                return ResponseEntity.badRequest().body("Chỉ cho phép chuyển sang trạng thái kế tiếp");
            }

            if ("Chờ xác nhận".equals(donHang.getTrangThai()) && "Đã xác nhận".equals(trangThai)) {
                List<HoaDonChiTietBH> chiTietList = hoaDonChiTietRepo.findByHoaDonId(donHang.getId());
                // Kiểm tra tồn kho cho từng sản phẩm
                for (HoaDonChiTietBH chiTiet : chiTietList) {
                    int soLuongMua = chiTiet.getSoLuong();
                    int sanPhamId = chiTiet.getAoDaiChiTiet().getId();
                    int checkStock = aoDaiChiTietBHRepo.kiemTraSoLuong(sanPhamId);
                    if (checkStock < soLuongMua) {
                        return ResponseEntity.badRequest().body("Không đủ hàng trong kho cho sản phẩm ID: " + sanPhamId);
                    }
                }
                for (HoaDonChiTietBH chiTiet : chiTietList) {
                    int soLuongMua = chiTiet.getSoLuong();
                    int sanPhamId = chiTiet.getAoDaiChiTiet().getId();
                    aoDaiChiTietBHRepo.truSoLuong(sanPhamId, soLuongMua);
                }

                if (donHang.getGiamGia() != null) {
                    GiamGiaBH voucher = donHang.getGiamGia();
                    if (voucher.getSoLuong() > 0) {
                        voucher.setSoLuong(voucher.getSoLuong() - 1);
                        giamGiaBHRepo.save(voucher);
                    }
                }
            }

            int updatedRows = quanLyDonHangRepo.updateTrangThai(id, trangThai);
            if (updatedRows > 0) {
                return ResponseEntity.ok("Cập nhật trạng thái thành công");
            } else {
                return ResponseEntity.badRequest().body("Cập nhật trạng thái thất bại");
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @PatchMapping("/huy-don")
    public ResponseEntity<?> huyDon(
            @RequestBody List<Integer> orderIds,
            @RequestParam(required = false) String lyDoHuy) {
        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn đơn hàng cần hủy");
        }

        try {
            List<String> canceledOrderCodes = new ArrayList<>();
            List<HoaDonBH> ordersToCancel = new ArrayList<>();

            for (Integer id : orderIds) {
                Optional<HoaDonBH> optionalDonHang = quanLyDonHangRepo.findById(id);
                if (optionalDonHang.isPresent()) {
                    HoaDonBH donHang = optionalDonHang.get();
                    if ("Đã hủy đơn".equals(donHang.getTrangThai())) {
                        canceledOrderCodes.add(donHang.getMaHoaDon());
                    } else {
                        ordersToCancel.add(donHang);
                    }
                }
            }

            if (!canceledOrderCodes.isEmpty()) {
                String msg = "Đơn hàng "
                        + String.join(", ", canceledOrderCodes)
                        + " đã bị hủy, không thể hủy lại.";
                return ResponseEntity.badRequest().body(msg);
            }

            for (HoaDonBH donHang : ordersToCancel) {
                String originalStatus = donHang.getTrangThai();
                donHang.setTrangThai("Đã hủy đơn");
                if (lyDoHuy != null && !lyDoHuy.trim().isEmpty()) {
                    donHang.setGhiChu(lyDoHuy);
                }
                quanLyDonHangRepo.save(donHang);
                if (!"Chờ xác nhận".equals(originalStatus)) {
                    List<HoaDonChiTietBH> chiTietList = hoaDonChiTietRepo.findByHoaDonId(donHang.getId());
                    for (HoaDonChiTietBH ct : chiTietList) {
                        aoDaiChiTietBHRepo.congLaiSoLuong(ct.getAoDaiChiTiet().getId(), ct.getSoLuong());
                    }
                    if (donHang.getGiamGia() != null) {
                        GiamGiaBH voucher = donHang.getGiamGia();
                        voucher.setSoLuong(voucher.getSoLuong() + 1);
                        giamGiaBHRepo.save(voucher);
                    }
                }
            }
            return ResponseEntity.ok("Đã hủy đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }

}
