package com.example.datn_be.controller.banhang;

import com.example.datn_be.entiy.*;
import com.example.datn_be.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ban-hang")
public class BanHangController {

    @Autowired
    AoDaiChiTietBHRepo aoDaiChiTietBHRepo;

    @Autowired
    HoaDonBHRepo hoaDonRepo;

    @Autowired
    HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    KhachHangBHRepo khachHangBHRepo;

    @Autowired
    GiamGiaBHRepo giamGiaBHRepo;

    @Autowired
    NhanVienRepository nhanVienRepository;

    @GetMapping("/ao-dai-chi-tiet/hien-thi")
    public List<AoDaiChiTietBH> hienThiAoDaiChiTiet(@RequestParam(required = false) String keyWord) {
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            return aoDaiChiTietBHRepo.timKiemSanPhamTheoKeyWord(keyWord.trim());
        }
        return aoDaiChiTietBHRepo.findAll();
    }

    @GetMapping("/ds-hoa-don-chua-thanh-toan")
    public List<HoaDonBH> hienThiDSHoaDonChuaThanhToan(){
        return hoaDonRepo.findByTrangThai("Chưa thanh toán");
    }

    @GetMapping("/tim-kiem-hoa-don-chua-thanh-toan")
    public List<HoaDonBH> timKiemHoaDonChuaThanhToan(@RequestParam(required = false) String tuKhoa) {
        return hoaDonRepo.timKiemHoaDonTheoTuKhoa(tuKhoa);
    }

    @GetMapping("/hoa-don-chi-tiet")
    public List<HoaDonChiTietBH> getHoaDonChiTietByHoaDonId(@RequestParam("idHoaDon") Integer idHoaDon) {
        return hoaDonChiTietRepo.findByHoaDonId(idHoaDon);
    }

    @PostMapping("/them-khach-hang")
    public ResponseEntity<?> themKhachHang(@RequestBody KhachHangBH khachHang) {
        try {
            if (khachHang.getHoTen() == null || khachHang.getHoTen().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên khách hàng không được để trống.");
            }
            if (khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Số điện thoại không được để trống.");
            }
            if (!khachHang.getHoTen().matches("^[\\p{L} ]+$")) {
                return ResponseEntity.badRequest().body("Tên khách hàng không hợp lệ. Không được chứa số.");
            }
            if (!khachHang.getSoDienThoai().matches("^0\\d{9}$")) {
                return ResponseEntity.badRequest().body("Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0.");
            }
            Optional<KhachHangBH> existingKhachHang = khachHangBHRepo.findBySoDienThoai(khachHang.getSoDienThoai());
            if (existingKhachHang.isPresent()) {
                return ResponseEntity.badRequest().body("Số điện thoại đã tồn tại.");
            }
            khachHangBHRepo.insertKhachHang(khachHang.getHoTen(), khachHang.getSoDienThoai());
            return ResponseEntity.ok("Thêm khách hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm khách hàng: " + e.getMessage());
        }
    }

    @GetMapping("/tim-khach-hang")
    public ResponseEntity<?> timKhachHang(@RequestParam String soDienThoai) {
        Optional<KhachHangBH> khachHang = khachHangBHRepo.findBySoDienThoai(soDienThoai);
        if (khachHang.isPresent()) {
            return ResponseEntity.ok(khachHang.get());
        } else {
            return ResponseEntity.badRequest().body("Số điện thoại này chưa có tài khoản.");
        }
    }

    @GetMapping("/hien-thi-giam-gia")
    public ResponseEntity<List<GiamGiaBH>> getAllGiamGia() {
    // Lấy các voucher có trạng thái = true và số lượng > 0
    List<GiamGiaBH> giamGiaList = giamGiaBHRepo.getAllGiamGia();

    return new ResponseEntity<>(giamGiaList, HttpStatus.OK);
    }

    @PostMapping("/thanh-toan")
    public ResponseEntity<?> thanhToan(@RequestBody HoaDonBH hoaDonInput, HttpServletRequest request) {
        // 1. Lấy hóa đơn từ CSDL theo id
        Optional<HoaDonBH> optHoaDon = hoaDonRepo.findById(hoaDonInput.getId());
        if (!optHoaDon.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn không tồn tại.");
        }
        HoaDonBH hoaDon = optHoaDon.get();

        if ("Đã thanh toán".equalsIgnoreCase(hoaDon.getTrangThai())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn đã được thanh toán.");
        }

        List<HoaDonChiTietBH> dsChiTiet = hoaDonChiTietRepo.findByHoaDonId(hoaDon.getId());
        if (dsChiTiet == null || dsChiTiet.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn chưa có sản phẩm!");
        }

        double total = 0;
        for (HoaDonChiTietBH ct : dsChiTiet) {
            total += ct.getSoLuong() * ct.getGiaBan();
        }
        hoaDon.setTienTruocGiam(total);
        double discount = 0;
        GiamGiaBH appliedVoucher = null;
        if (hoaDonInput.getGiamGia() != null && hoaDonInput.getGiamGia().getId() != null) {
            Optional<GiamGiaBH> optVoucher = giamGiaBHRepo.findById(hoaDonInput.getGiamGia().getId());
            if (optVoucher.isPresent()) {
                GiamGiaBH voucher = optVoucher.get();
                if (total >= voucher.getGiaTriToiThieu()) {
                    if (voucher.getLoaiGiamGia() != null && voucher.getLoaiGiamGia() == 0) {
                        discount = voucher.getGiaTriGiam();
                    } else if (voucher.getLoaiGiamGia() != null && voucher.getLoaiGiamGia() == 1) {
                        double calculated = total * (voucher.getGiaTriGiam() / 100.0);
                        discount = Math.min(calculated, voucher.getToiDaGiamGia());
                    }
                    if (voucher.getSoLuong() > 0) {
                        voucher.setSoLuong(voucher.getSoLuong() - 1);
                        giamGiaBHRepo.save(voucher);
                        appliedVoucher = voucher;
                    } else {
                        discount = 0;
                    }
                }
            }
        }
        double finalTotal = total - discount;
        if (hoaDonInput.getPhuongThucThanhToan() != null) {
            hoaDon.setPhuongThucThanhToan(hoaDonInput.getPhuongThucThanhToan());
        }
        hoaDon.setHinhThucMuaHang(false);
        if (hoaDonInput.getKhachHang() == null || hoaDonInput.getKhachHang().getId() == null) {
            String soDienThoai = hoaDonInput.getKhachHang() != null
                    ? hoaDonInput.getKhachHang().getSoDienThoai()
                    : null;
            if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Thông tin khách hàng chưa được cung cấp.");
            }
            Optional<KhachHangBH> optKhachHang = khachHangBHRepo.findBySoDienThoai(soDienThoai);
            if (!optKhachHang.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Khách hàng không tồn tại.");
            }
            hoaDon.setKhachHang(optKhachHang.get());
        } else {
            hoaDon.setKhachHang(hoaDonInput.getKhachHang());
        }
        if (hoaDonInput.getNhanVien() == null || hoaDonInput.getNhanVien().getId() == null) {
            return ResponseEntity.badRequest().body("Thông tin nhân viên chưa được cung cấp.");
        } else {
            Optional<NhanVienBH> optNhanVien = nhanVienRepository.findById(hoaDonInput.getNhanVien().getId());
            if (!optNhanVien.isPresent()) {
                return ResponseEntity.badRequest().body("Nhân viên không tồn tại.");
            }
            hoaDon.setNhanVien(optNhanVien.get());
        }
        if (hoaDonInput.getPhuongThucThanhToan() != null && hoaDonInput.getPhuongThucThanhToan()) {
            try {
                // Tạo mã giao dịch
                String txnRef = "HD" + hoaDon.getId() + "-" + System.currentTimeMillis();

                // Tạo URL VietQR trực tiếp
                String vietQrUrl = generateVietQRUrl(finalTotal, txnRef);

                // Tạo QR code từ URL VietQR
                String qrCodeBase64 = getQRCodeBase64FromUrl(vietQrUrl);

                // Lưu hóa đơn
                hoaDon.setGiamGia(appliedVoucher);
                hoaDon.setTongTien(finalTotal);
                hoaDon.setTrangThai("Chưa thanh toán");
                hoaDonRepo.save(hoaDon);

                // Trả về thông tin
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Vui lòng quét mã QR để thanh toán.");
                response.put("qrCode", qrCodeBase64);
                response.put("tongTien", finalTotal);
                response.put("txnRef", txnRef);

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi khi tạo mã QR: " + e.getMessage());
            }
        } else {
            // Thanh toán tiền mặt - giữ nguyên như cũ
            hoaDon.setGiamGia(appliedVoucher);
            hoaDon.setTongTien(finalTotal);
            hoaDon.setTrangThai("Đã thanh toán");
            hoaDonRepo.save(hoaDon);
            return ResponseEntity.ok("Thanh toán thành công. Tổng tiền cuối cùng: " + finalTotal);
        }
    }

    private String generateVietQRUrl(double amount, String txnRef) {
        // Thông tin tài khoản ngân hàng của shop
        String bankId = "970436";         // Mã ngân hàng
        String accountNo = "1017095564";  // Số tài khoản
        String accountName = "GRACEFUL Việt Nam";

        try {
            // Xây dựng nội dung chuyển khoản
            String content = "Thanh toan don hang " + txnRef;

            // Format số tiền không có phần thập phân
            long formattedAmount = (long) amount;

            // Tạo URL VietQR theo chuẩn
            String vietQrUrl = "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-" +
                    "compact2" + ".png?amount=" + formattedAmount +
                    "&addInfo=" + URLEncoder.encode(content, StandardCharsets.UTF_8.toString()) +
                    "&accountName=" + URLEncoder.encode(accountName, StandardCharsets.UTF_8.toString());

            return vietQrUrl;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo mã VietQR: " + e.getMessage());
        }
    }

    private String getQRCodeBase64FromUrl(String url) {
        try {
            // Tải hình ảnh từ URL
            URL imageUrl = new URL(url);
            BufferedImage image = ImageIO.read(imageUrl);

            // Chuyển đổi BufferedImage thành Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo mã QR từ URL: " + e.getMessage());
        }
    }

    @PostMapping("/xac-nhan-thanh-toan")
    public ResponseEntity<?> xacNhanThanhToan(@RequestBody Map<String, Integer> request) {
        Integer hoaDonId = request.get("id");
        if (hoaDonId == null) {
            return ResponseEntity.badRequest().body("ID hóa đơn không được cung cấp");
        }

        Optional<HoaDonBH> optHoaDon = hoaDonRepo.findById(hoaDonId);
        if (!optHoaDon.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Hóa đơn không tồn tại.");
        }

        HoaDonBH hoaDon = optHoaDon.get();
        if ("Đã thanh toán".equalsIgnoreCase(hoaDon.getTrangThai())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn đã được thanh toán.");
        }

        hoaDon.setTrangThai("Đã thanh toán");
        hoaDonRepo.save(hoaDon);

        return ResponseEntity.ok("Xác nhận thanh toán thành công");
    }


    @PostMapping("/tao-hoa-don-moi")
    public ResponseEntity<?> taoHoaDonMoi() {
        List<HoaDonBH> dsHoaDonChuaThanhToan = hoaDonRepo.findByTrangThai("Chưa thanh toán");
        if (dsHoaDonChuaThanhToan.size() >= 15) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Không thể tạo thêm hóa đơn mới. Giới hạn tối đa 15 hóa đơn chưa thanh toán đã được đạt.");
        }

        try {
            HoaDonBH hoaDonMoi = new HoaDonBH();
            hoaDonMoi.setPhuongThucThanhToan(null);
            hoaDonMoi.setTongTien(0.0);
            hoaDonMoi.setGhiChu("");
            hoaDonMoi.setPhiGiaoHang(0.0);
            hoaDonMoi.setHinhThucMuaHang(false);
            hoaDonMoi.setDiaChiGiaoHang("");
            hoaDonMoi.setNgayTao(new Date());
            hoaDonMoi.setTrangThai("Chưa thanh toán");
            hoaDonMoi.setKhachHang(null);
            hoaDonMoi.setNhanVien(null);
            hoaDonMoi.setGiamGia(null);
            HoaDonBH saved = hoaDonRepo.save(hoaDonMoi);
            hoaDonRepo.flush();
            HoaDonBH refreshed = hoaDonRepo.findById(saved.getId()).orElse(saved);
            return ResponseEntity.ok(refreshed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tạo hóa đơn mới: " + e.getMessage());
        }
    }

    @PostMapping("/them-san-pham-vao-hoa-don")
    public ResponseEntity<?> themSanPhamVaoHoaDon(@RequestBody HoaDonChiTietBH hoaDonChiTietBHInput) {
        if (hoaDonChiTietBHInput.getHoaDon() == null || hoaDonChiTietBHInput.getHoaDon().getId() == null) {
            return ResponseEntity.badRequest().body("Hóa đơn không được để trống.");
        }
        Integer hoaDonId = hoaDonChiTietBHInput.getHoaDon().getId();
        Optional<HoaDonBH> optHoaDon = hoaDonRepo.findById(hoaDonId);
        if (!optHoaDon.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hóa đơn không tồn tại.");
        }
        HoaDonBH hoaDon = optHoaDon.get();
        if (!"Chưa thanh toán".equalsIgnoreCase(hoaDon.getTrangThai())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể thêm sản phẩm vào hóa đơn đã thanh toán.");
        }

        if (hoaDonChiTietBHInput.getAoDaiChiTiet() == null || hoaDonChiTietBHInput.getAoDaiChiTiet().getId() == null) {
            return ResponseEntity.badRequest().body("Sản phẩm không được để trống.");
        }
        Integer productId = hoaDonChiTietBHInput.getAoDaiChiTiet().getId();
        Optional<AoDaiChiTietBH> optProduct = aoDaiChiTietBHRepo.findById(productId);
        if (!optProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sản phẩm không tồn tại.");
        }
        AoDaiChiTietBH product = optProduct.get();
        if (product.getSoLuong() == null || product.getSoLuong() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sản phẩm đã hết hàng.");
        }

        Optional<HoaDonChiTietBH> optExisting = hoaDonChiTietRepo.findByHoaDon_IdAndAoDaiChiTiet_Id(hoaDonId, productId);
        if (optExisting.isPresent()) {
            HoaDonChiTietBH existingChiTiet = optExisting.get();
            existingChiTiet.setSoLuong(existingChiTiet.getSoLuong() + 1);
            hoaDonChiTietRepo.save(existingChiTiet);
        } else {
            hoaDonChiTietBHInput.setSoLuong(1);
            hoaDonChiTietBHInput.setGiaBan(product.getGiaBan());
            hoaDonChiTietBHInput.setMaHDCT("HDCT" + System.currentTimeMillis());
            hoaDonChiTietBHInput.setHoaDon(hoaDon);
            hoaDonChiTietRepo.save(hoaDonChiTietBHInput);
        }

        product.setSoLuong(product.getSoLuong() - 1);
        aoDaiChiTietBHRepo.save(product);

        return ResponseEntity.ok("Thêm sản phẩm thành công.");
    }

    @DeleteMapping("/xoa-san-pham-vao-hoa-don/{id}")
    public ResponseEntity<?> xoaSanPhamVaoHoaDon(@PathVariable("id") Integer hoaDonChiTietId) {
        Optional<HoaDonChiTietBH> optHDCT = hoaDonChiTietRepo.findById(hoaDonChiTietId);
        if (!optHDCT.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Sản phẩm trong hóa đơn không tồn tại.");
        }
        HoaDonChiTietBH hdct = optHDCT.get();
        AoDaiChiTietBH product = hdct.getAoDaiChiTiet();
        int soLuongHDCT = hdct.getSoLuong() != null ? hdct.getSoLuong() : 0;
        product.setSoLuong(product.getSoLuong() + soLuongHDCT);
        aoDaiChiTietBHRepo.save(product);
        hoaDonChiTietRepo.delete(hdct);

        return ResponseEntity.ok("Xóa sản phẩm thành công.");
    }

    @DeleteMapping("/xoa-hoa-don/{id}")
    public ResponseEntity<?> xoaHoaDon(@PathVariable("id") Integer hoaDonId) {
        // Lấy hóa đơn theo id
        Optional<HoaDonBH> optHoaDon = hoaDonRepo.findById(hoaDonId);
        if (!optHoaDon.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn không tồn tại.");
        }
        HoaDonBH hoaDon = optHoaDon.get();

        List<HoaDonChiTietBH> dsChiTiet = hoaDonChiTietRepo.findByHoaDonId(hoaDonId);
        if (dsChiTiet != null && !dsChiTiet.isEmpty()) {
            for (HoaDonChiTietBH ct : dsChiTiet) {
                AoDaiChiTietBH product = ct.getAoDaiChiTiet();
                product.setSoLuong(product.getSoLuong() + ct.getSoLuong());
                aoDaiChiTietBHRepo.save(product);
            }
            hoaDonChiTietRepo.deleteAll(dsChiTiet);
        }
        hoaDonRepo.delete(hoaDon);

        return ResponseEntity.ok("Xóa hóa đơn thành công.");
    }

    @PutMapping("/cap-nhat-so-luong")
    public ResponseEntity<?> capNhatSoLuong(@RequestBody HoaDonChiTietBH hdctInput) {
        if (hdctInput.getId() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Id của hóa đơn chi tiết không được để trống.", "currentQuantity", 0)
            );
        }

        Optional<HoaDonChiTietBH> optHDCT = hoaDonChiTietRepo.findById(hdctInput.getId());
        if (!optHDCT.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Hóa đơn chi tiết không tồn tại.", "currentQuantity", 0));
        }

        HoaDonChiTietBH existingHDCT = optHDCT.get();
        int oldQuantity = existingHDCT.getSoLuong() != null ? existingHDCT.getSoLuong() : 0;
        int newQuantity = hdctInput.getSoLuong() != null ? hdctInput.getSoLuong() : 0;

        if (newQuantity == oldQuantity) {
            return ResponseEntity.ok(Map.of("message", "Số lượng không thay đổi.", "currentQuantity", oldQuantity));
        }

        int diff = newQuantity - oldQuantity;
        AoDaiChiTietBH product = existingHDCT.getAoDaiChiTiet();

        // Cập nhật số lượng tồn kho của sản phẩm
        if (diff > 0) {
            if (product.getSoLuong() == null || product.getSoLuong() < diff) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Sản phẩm không đủ số lượng trong kho để tăng số lượng.", "currentQuantity", oldQuantity));
            }
            product.setSoLuong(product.getSoLuong() - diff);
        } else {
            product.setSoLuong(product.getSoLuong() + (-diff));
        }

        // Cập nhật số lượng mới cho chi tiết hóa đơn
        existingHDCT.setSoLuong(newQuantity);
        hoaDonChiTietRepo.save(existingHDCT);
        aoDaiChiTietBHRepo.save(product);

        // Tính lại tổng tiền của hóa đơn sau thay đổi số lượng
        HoaDonBH invoice = existingHDCT.getHoaDon();
        List<HoaDonChiTietBH> dsChiTiet = hoaDonChiTietRepo.findByHoaDonId(invoice.getId());
        double totalBeforeDiscount = 0;
        for (HoaDonChiTietBH ct : dsChiTiet) {
            totalBeforeDiscount += ct.getSoLuong() * ct.getGiaBan();
        }
        invoice.setTienTruocGiam(totalBeforeDiscount);

        // Tính số tiền giảm nếu hóa đơn có áp dụng voucher
        double discount = 0;
        if (invoice.getGiamGia() != null && invoice.getGiamGia().getId() != null) {
            GiamGiaBH voucher = invoice.getGiamGia();
            if (totalBeforeDiscount >= voucher.getGiaTriToiThieu()) {
                if (voucher.getLoaiGiamGia() != null && voucher.getLoaiGiamGia() == 0) {
                    discount = voucher.getGiaTriGiam();
                } else if (voucher.getLoaiGiamGia() != null && voucher.getLoaiGiamGia() == 1) {
                    double calculated = totalBeforeDiscount * (voucher.getGiaTriGiam() / 100.0);
                    discount = Math.min(calculated, voucher.getToiDaGiamGia());
                }
            }
        }

        double finalTotal = totalBeforeDiscount - discount;
        invoice.setTongTien(finalTotal);
        hoaDonRepo.save(invoice);

        return ResponseEntity.ok(Map.of("message", "Cập nhật số lượng thành công.", "currentQuantity", newQuantity));
    }

    @PostMapping("/cap-nhat-khach-hang-hoa-don")
    public ResponseEntity<?> capNhatKhachHangHoaDon(@RequestBody HoaDonBH hoaDonInput) {
        if (hoaDonInput.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Vui lòng chọn hóa đơn.");
        }
        Optional<HoaDonBH> optHoaDon = hoaDonRepo.findById(hoaDonInput.getId());
        if (!optHoaDon.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Hóa đơn không tồn tại.");
        }
        HoaDonBH hoaDon = optHoaDon.get();
        if (hoaDonInput.getKhachHang() == null
                || hoaDonInput.getKhachHang().getSoDienThoai() == null
                || hoaDonInput.getKhachHang().getSoDienThoai().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Thông tin khách hàng chưa được cung cấp.");
        }
        Optional<KhachHangBH> optKhachHang = khachHangBHRepo.findBySoDienThoai(hoaDonInput.getKhachHang().getSoDienThoai());
        if (!optKhachHang.isPresent()) {
            return ResponseEntity.badRequest().body("Khách hàng không tồn tại.");
        }
        hoaDon.setKhachHang(optKhachHang.get());
        hoaDonRepo.save(hoaDon);
        return ResponseEntity.ok("Cập nhật khách hàng cho hóa đơn thành công.");
    }
}