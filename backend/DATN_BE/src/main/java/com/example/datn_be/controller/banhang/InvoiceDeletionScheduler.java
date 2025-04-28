//package com.example.datn_be.controller.banhang;
//
//import com.example.datn_be.entiy.AoDaiChiTietBH;
//import com.example.datn_be.entiy.HoaDonBH;
//import com.example.datn_be.entiy.HoaDonChiTietBH;
//import com.example.datn_be.repository.AoDaiChiTietBHRepo;
//import com.example.datn_be.repository.HoaDonBHRepo;
//import com.example.datn_be.repository.HoaDonChiTietRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//
//@Component
//public class InvoiceDeletionScheduler {
//
//    @Autowired
//    private HoaDonBHRepo hoaDonRepo;
//
//    @Autowired
//    private HoaDonChiTietRepo hoaDonChiTietRepo;
//
//    @Autowired
//    private AoDaiChiTietBHRepo aoDaiChiTietBHRepo;
//
//    @Scheduled(fixedDelay = 10000)
//    @Transactional
//    public void deleteUnpaidInvoices() {
//        Date now = new Date();
//        List<HoaDonBH> unpaidInvoices = hoaDonRepo.findByTrangThai("Chưa thanh toán");
//        for (HoaDonBH invoice : unpaidInvoices) {
//            long diffMillis = now.getTime() - invoice.getNgayTao().getTime();
//            long diffMinutes = diffMillis / (60 * 1000);
//            if (diffMinutes >= 1) {
//                List<HoaDonChiTietBH> invoiceDetails = hoaDonChiTietRepo.findByHoaDonId(invoice.getId());
//                if (invoiceDetails != null && !invoiceDetails.isEmpty()) {
//                    for (HoaDonChiTietBH detail : invoiceDetails) {
//                        // Cập nhật lại số lượng sản phẩm khi xóa HDCT
//                        AoDaiChiTietBH product = detail.getAoDaiChiTiet();
//                        product.setSoLuong(product.getSoLuong() + detail.getSoLuong());
//                        aoDaiChiTietBHRepo.save(product);
//                    }
//                    hoaDonChiTietRepo.deleteAll(invoiceDetails);
//                }
//                hoaDonRepo.delete(invoice);
//            }
//        }
//    }
//}