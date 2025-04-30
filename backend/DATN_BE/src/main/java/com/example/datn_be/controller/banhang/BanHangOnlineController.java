package com.example.datn_be.controller.banhang;

import com.example.datn_be.entiy.AoDaiChiTietBH;
import com.example.datn_be.entiy.HoaDonBH;
import com.example.datn_be.entiy.HoaDonChiTietBH;
import com.example.datn_be.entiy.DiaChiBH;
import com.example.datn_be.entiy.GiamGiaBH;
import com.example.datn_be.entiy.KhachHangBH;
import com.example.datn_be.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ban-hang-online")
public class BanHangOnlineController {

    // VNPay cấu hình (môi trường TEST)
    private static final String VNP_Version = "2.1.0";
    private static final String VNP_Command = "pay";
    private static final String VNP_TmnCode = "O33JOH6I";
    private static final String VNP_HashSecret = "8LKU9O0U39ES9OEC1YKZVR92CFBCETXQ";
    private static final String VNP_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    // URL trả về khi VNPay xử lý xong (điều chỉnh theo domain của bạn)
    private static final String VNP_ReturnUrl = "https://graceful56.shop/api/ban-hang-online/vnpay_return";

    @Autowired
    AoDaiChiTietBHRepo aoDaiChiTietBHRepo;

    @Autowired
    KhachHangBHRepo khachHangBHRepo;

    @Autowired
    DiaChiBHRepo diaChiBHRepo;

    @Autowired
    GiamGiaBHRepo giamGiaBHRepo;

    @Autowired
    HoaDonBHRepo hoaDonBHRepo;

    @Autowired
    HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    GioHangChiTietBHRepo gioHangChiTietBHRepo;

    @Autowired
    QuanLyDonHangRepo quanLyDonHangRepo;

    // Map tạm để lưu đơn hàng chưa lưu vào CSDL (cho trường hợp thanh toán online VNPay)
    private static final Map<String, HoaDonBH> pendingOrders = new ConcurrentHashMap<>();

    @GetMapping("/dia-chi-khach-hang")
    public List<DiaChiBH> getCustomerAddresses(@RequestParam("idKhachHang") Integer khachHangId) {
        return diaChiBHRepo.findByKhachHang_IdOrderByDiaChiMacDinhDesc(khachHangId);
    }

    @GetMapping("/hien-thi-giam-gia-online")
    public ResponseEntity<List<GiamGiaBH>> getAllGiamGia() {
        List<GiamGiaBH> giamGiaList = giamGiaBHRepo.getAllGiamGia();
        return new ResponseEntity<>(giamGiaList, HttpStatus.OK);
    }


    @PostMapping("/dia-chi")
    public ResponseEntity<?> saveAddress(@RequestBody DiaChiBH diaChi) {
        // Kiểm tra thông tin khách hàng
        if(diaChi.getKhachHang() == null || diaChi.getKhachHang().getId() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin khách hàng");
        }
        Optional<KhachHangBH> customerOpt = khachHangBHRepo.findById(diaChi.getKhachHang().getId());
        if (!customerOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Khách hàng không tồn tại");
        }
        List<DiaChiBH> existingAddresses = diaChiBHRepo.findByKhachHang_Id(diaChi.getKhachHang().getId());
        for (DiaChiBH existing : existingAddresses) {
            if (existing.getDuong().trim().equalsIgnoreCase(diaChi.getDuong().trim()) &&
                    existing.getXaPhuong().trim().equalsIgnoreCase(diaChi.getXaPhuong().trim()) &&
                    existing.getQuanHuyen().trim().equalsIgnoreCase(diaChi.getQuanHuyen().trim()) &&
                    existing.getTinhThanhPho().trim().equalsIgnoreCase(diaChi.getTinhThanhPho().trim())) {
                return ResponseEntity.badRequest().body("Địa chỉ đã tồn tại");
            }
        }
        if (Boolean.TRUE.equals(diaChi.getDiaChiMacDinh())) {
            List<DiaChiBH> addressesToUpdate = diaChiBHRepo.findByKhachHang_IdOrderByDiaChiMacDinhDesc(diaChi.getKhachHang().getId());
            for (DiaChiBH address : addressesToUpdate) {
                address.setDiaChiMacDinh(false);
            }
            diaChiBHRepo.saveAll(addressesToUpdate);
        }
        diaChi.setKhachHang(customerOpt.get());
        DiaChiBH savedAddress = diaChiBHRepo.save(diaChi);
        return ResponseEntity.ok(savedAddress);
    }

    @DeleteMapping("/xoa-dia-chi/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable("id") Integer id) {
        Optional<DiaChiBH> addressOpt = diaChiBHRepo.findById(id);
        if (!addressOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Địa chỉ không tồn tại");
        }
        diaChiBHRepo.delete(addressOpt.get());
        return ResponseEntity.ok("Xóa địa chỉ thành công");
    }

    @PutMapping("/dia-chi/{id}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable("id") Integer addressId) {
        Optional<DiaChiBH> addressOpt = diaChiBHRepo.findById(addressId);
        if (!addressOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Địa chỉ không tồn tại");
        }
        DiaChiBH addressToSetDefault = addressOpt.get();
        Integer customerId = addressToSetDefault.getKhachHang().getId();
        List<DiaChiBH> existingAddresses = diaChiBHRepo.findByKhachHang_IdOrderByDiaChiMacDinhDesc(customerId);
        for (DiaChiBH addr : existingAddresses) {
            addr.setDiaChiMacDinh(false);
        }
        diaChiBHRepo.saveAll(existingAddresses);
        addressToSetDefault.setDiaChiMacDinh(true);
        DiaChiBH updatedAddress = diaChiBHRepo.save(addressToSetDefault);
        return ResponseEntity.ok(updatedAddress);
    }


    @PostMapping("/dat-hang")
    @Transactional
    public ResponseEntity<?> datHang(@RequestBody HoaDonBH hoaDon, HttpServletRequest request) {
        if (hoaDon.getKhachHang() == null || hoaDon.getKhachHang().getId() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin khách hàng");
        }
        Optional<KhachHangBH> khachHangOpt = khachHangBHRepo.findById(hoaDon.getKhachHang().getId());
        if (!khachHangOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Khách hàng không tồn tại");
        }
        hoaDon.setKhachHang(khachHangOpt.get());
        GiamGiaBH voucher = null;
        if (hoaDon.getGiamGia() != null && hoaDon.getGiamGia().getId() != null) {
            Optional<GiamGiaBH> voucherOpt = giamGiaBHRepo.findById(hoaDon.getGiamGia().getId());
            if (!voucherOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Voucher không tồn tại");
            }
            voucher = voucherOpt.get();
        }
        double totalOrderValue = 0.0;
        if (hoaDon.getChiTietHoaDon() == null || hoaDon.getChiTietHoaDon().isEmpty()) {
            return ResponseEntity.badRequest().body("Không có chi tiết hóa đơn");
        }
        for (HoaDonChiTietBH ct : hoaDon.getChiTietHoaDon()) {
            if (ct.getAoDaiChiTiet() == null || ct.getAoDaiChiTiet().getId() == null) {
                return ResponseEntity.badRequest().body("Thiếu thông tin sản phẩm chi tiết");
            }
            Optional<AoDaiChiTietBH> spOpt = aoDaiChiTietBHRepo.findById(ct.getAoDaiChiTiet().getId());
            if (!spOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Sản phẩm chi tiết không tồn tại với id: " + ct.getAoDaiChiTiet().getId());
            }
            AoDaiChiTietBH sp = spOpt.get();
            if (sp.getSoLuong() < ct.getSoLuong()) {
                return ResponseEntity.badRequest().body("Sản phẩm " + sp.getMaAoDaiChiTiet() + " không đủ số lượng");
            }
            totalOrderValue += sp.getGiaBan() * ct.getSoLuong();
            ct.setAoDaiChiTiet(sp);
            ct.setHoaDon(hoaDon);
        }
        double shippingFee = hoaDon.getPhiGiaoHang() != null ? hoaDon.getPhiGiaoHang() : 0.0;
        double tienTruocGiam = totalOrderValue + shippingFee;
        double discount = 0.0;
        if (voucher != null && tienTruocGiam >= voucher.getGiaTriToiThieu()) {
            // 1. Giảm cố định
            if (voucher.getLoaiGiamGia() == 0) {
                discount = voucher.getGiaTriGiam();
            }
            // 2. Giảm theo % với cap tối đa
            else if (voucher.getLoaiGiamGia() == 1) {
                discount = tienTruocGiam * voucher.getGiaTriGiam() / 100.0;
                Double maxCap = voucher.getToiDaGiamGia();
                if (maxCap != null && discount > maxCap) {
                    discount = maxCap;
                }
            }
            hoaDon.setGiamGia(voucher);
        } else {
            // không đủ điều kiện hoặc không có voucher
            hoaDon.setGiamGia(null);
        }

        // 3. Tính tổng tiền thực tế
        double finalTotal = tienTruocGiam - discount;
        hoaDon.setTongTien(finalTotal);
        hoaDon.setPhuongThucThanhToan(null);
        hoaDon.setHinhThucMuaHang(true);
        hoaDon.setTienTruocGiam(tienTruocGiam);
        hoaDon.setNgayTao(new Date());

        // Nếu khách hàng chọn thanh toán online VNPay, lưu tạm vào pendingOrders
        if (hoaDon.getPhuongThucThanhToanOnline() != null && hoaDon.getPhuongThucThanhToanOnline()) {
            // Tạo mã giao dịch độc nhất (txnRef) và gán làm mã đơn hàng
            String txnRef = UUID.randomUUID().toString();
            hoaDon.setMaHoaDon(txnRef);
            // Lưu tạm đơn hàng vào bộ nhớ
            pendingOrders.put(txnRef, hoaDon);
            // Tạo URL thanh toán VNPay
            String vnpayUrl = generateVnpayPaymentUrl(txnRef, hoaDon.getTongTien(), request);
            return ResponseEntity.ok(vnpayUrl);
        } else {
            // Nếu thanh toán COD thì lưu đơn hàng trực tiếp
            hoaDon.setTrangThai("Chờ xác nhận");
            HoaDonBH savedHoaDon = hoaDonBHRepo.save(hoaDon);
            return ResponseEntity.ok(savedHoaDon);
        }
    }


    // Hàm tạo URL thanh toán VNPay sử dụng txnRef và tổng tiền đơn hàng
    private String generateVnpayPaymentUrl(String txnRef, double tongTien, HttpServletRequest request) {
        try {
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", VNP_Version);
            vnpParams.put("vnp_Command", VNP_Command);
            vnpParams.put("vnp_TmnCode", VNP_TmnCode);
            vnpParams.put("vnp_Amount", String.valueOf((long) (tongTien * 100)));
            String createDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            vnpParams.put("vnp_CreateDate", createDate);
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_IpAddr", "127.0.0.1");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + txnRef);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_ReturnUrl", VNP_ReturnUrl);
            vnpParams.put("vnp_TxnRef", txnRef);

            StringBuilder hashData = new StringBuilder();
            StringJoiner queryJoiner = new StringJoiner("&");
            boolean first = true;
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                if (!first) {
                    hashData.append("&");
                } else {
                    first = false;
                }
                hashData.append(encodedKey).append("=").append(encodedValue);
                queryJoiner.add(encodedKey + "=" + encodedValue);
            }
            String secureHash = hmacSHA512(VNP_HashSecret, hashData.toString()).toUpperCase();
            queryJoiner.add("vnp_SecureHash=" + secureHash);
            queryJoiner.add("vnp_SecureHashType=" + URLEncoder.encode("HmacSHA512", StandardCharsets.UTF_8.toString()));
            return VNP_Url + "?" + queryJoiner.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating VNPay URL: " + e.getMessage());
        }
    }

    // Hàm tính HMAC SHA512
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hash.append('0');
                hash.append(hex);
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // Endpoint callback khi khách hàng được chuyển hướng từ VNPay
    @GetMapping("/vnpay_return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> vnpParams = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            vnpParams.put(entry.getKey(), request.getParameter(entry.getKey()));
        }
        String responseCode = vnpParams.get("vnp_ResponseCode");
        String txnRef = vnpParams.get("vnp_TxnRef");

        HoaDonBH pendingOrder = pendingOrders.get(txnRef);
        if (pendingOrder != null && "00".equals(responseCode)) {
            // Cập nhật số lượng cho từng sản phẩm
            for (HoaDonChiTietBH ct : pendingOrder.getChiTietHoaDon()) {
                AoDaiChiTietBH product = ct.getAoDaiChiTiet();
                // Giảm số lượng sản phẩm theo số lượng đặt hàng
                int newQuantity = product.getSoLuong() - ct.getSoLuong();
                product.setSoLuong(newQuantity);
                aoDaiChiTietBHRepo.save(product);
            }
            // Nếu có voucher được chọn thì giảm số lượng voucher đi 1
            if (pendingOrder.getGiamGia() != null) {
                GiamGiaBH voucher = pendingOrder.getGiamGia();
                int newVoucherQty = voucher.getSoLuong() - 1;
                voucher.setSoLuong(newVoucherQty);
                giamGiaBHRepo.save(voucher);
            }
            // Cập nhật trạng thái đơn hàng thành "Đã xác nhận"
            pendingOrder.setTrangThai("Đã xác nhận");
            hoaDonBHRepo.save(pendingOrder);
            pendingOrders.remove(txnRef);
            String callback = String.format(
                    "https://graceful56.shop/thanh-toan?success=true&orderId=%d",
                    pendingOrder.getId()
            );
            response.sendRedirect(callback);
        } else {
            pendingOrders.remove(txnRef);
            response.sendRedirect("https://graceful56.shop/thanh-toan?success=false");
        }
    }

    // Endpoint IPN từ VNPay (server-to-server)
    @GetMapping("/vnpay_ipn")
    public ResponseEntity<?> vnpayIpn(HttpServletRequest request) {
        try {
            Map<String, String> vnpParams = new TreeMap<>();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry.getKey().startsWith("vnp_")) {
                    vnpParams.put(entry.getKey(), request.getParameter(entry.getKey()));
                }
            }
            String vnpSecureHash = vnpParams.remove("vnp_SecureHash");
            vnpParams.remove("vnp_SecureHashType");

            StringJoiner joiner = new StringJoiner("&");
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                joiner.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            }
            String hashData = joiner.toString();
            String computedHash = hmacSHA512(VNP_HashSecret, hashData).toUpperCase();
            if (!computedHash.equals(vnpSecureHash)) {
                return ResponseEntity.badRequest().body("Chu ky khong hop le");
            }
            String responseCode = vnpParams.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                return ResponseEntity.ok("GD Thanh cong");
            } else {
                return ResponseEntity.ok("GD Khong thanh cong");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/don-hang")
    public ResponseEntity<?> getDonHangByKhachHang(@RequestParam("idKhachHang") Integer khachHangId) {
        List<HoaDonBH> orders = quanLyDonHangRepo.findByKhachHang_IdAndHinhThucMuaHangOrderByNgayTaoDesc(khachHangId, true);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PatchMapping("/huy-don-khach-hang")
    public ResponseEntity<?> huyDonKhachHang(
            @RequestBody List<Integer> orderIds,
            @RequestParam("lyDoHuy") String lyDoHuy) {
        if (orderIds == null || orderIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng chọn đơn hàng cần hủy");
        }

        try {
            List<String> canceledOrderCodes = new ArrayList<>();
            List<HoaDonBH> ordersToCancel = new ArrayList<>();

            for (Integer id : orderIds) {
                Optional<HoaDonBH> opt = quanLyDonHangRepo.findById(id);
                if (opt.isPresent()) {
                    HoaDonBH donHang = opt.get();
                    if ("Đã hủy đơn".equals(donHang.getTrangThai())) {
                        canceledOrderCodes.add(donHang.getMaHoaDon());
                    } else {
                        ordersToCancel.add(donHang);
                    }
                }
            }

            if (!canceledOrderCodes.isEmpty()) {
                String msg = "Đơn hàng " +
                        String.join(", ", canceledOrderCodes) +
                        " đã bị hủy, không thể hủy lại.";
                return ResponseEntity.badRequest().body(msg);
            }

            for (HoaDonBH donHang : ordersToCancel) {
                donHang.setTrangThai("Đã hủy đơn");
                if (lyDoHuy != null && !lyDoHuy.trim().isEmpty()) {
                    donHang.setGhiChu(lyDoHuy);
                }
                quanLyDonHangRepo.save(donHang);
            }

            return ResponseEntity.ok("Đã hủy đơn thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi hủy đơn hàng: " + e.getMessage());
        }
    }
    
}
