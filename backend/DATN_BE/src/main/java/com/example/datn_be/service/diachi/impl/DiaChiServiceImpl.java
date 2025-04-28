package com.example.datn_be.service.diachi.impl;

import com.example.datn_be.dto.khachhang.response.DiaChiResponse;
import com.example.datn_be.dto.khachhang.resquest.DiaChiRequest;
import com.example.datn_be.entiy.khach_hang.DiaChiTk;
import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import com.example.datn_be.repository.DiaChiRepo;
import com.example.datn_be.repository.KhachHangRepo;
import com.example.datn_be.security.jwt.JwtUtil;
import com.example.datn_be.service.diachi.DiaChiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiaChiServiceImpl implements DiaChiService {

    @Autowired
    private final DiaChiRepo diaChiRepo;

    @Autowired
    private final KhachHangRepo khachHangRepo;

    @Autowired
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<?> getAllDiaChi() {
        return new ResponseEntity<>(diaChiRepo.getAllDiaChi(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addDiaChi(DiaChiRequest diaChiRequest) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = (String) request.getSession().getAttribute("JWT_TOKEN");
            if(token == null) {
                return new ResponseEntity<>("Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED);
            }
            Integer customerId = jwtUtil.getCustomerIdFromToken(token);
            Optional<KhachHangTk> khachHangOptional = khachHangRepo.findById(customerId);
            if (!khachHangOptional.isPresent()) {
                return new ResponseEntity<>("Khách hàng không tồn tại", HttpStatus.BAD_REQUEST);
            }
            KhachHangTk khachHang = khachHangOptional.get();
            List<DiaChiTk> existingAddresses = diaChiRepo.findByKhachHang_IdAndDuongAndXaPhuongAndQuanHuyenAndTinhThanhPho(
                    customerId, diaChiRequest.getDuong(), diaChiRequest.getXaPhuong(),
                    diaChiRequest.getQuanHuyen(), diaChiRequest.getTinhThanhPho());
            if (!existingAddresses.isEmpty()) {
                return new ResponseEntity<>("Địa chỉ đã tồn tại", HttpStatus.BAD_REQUEST);
            }
            DiaChiTk diaChi = new DiaChiTk();
            diaChi.setDuong(diaChiRequest.getDuong());
            diaChi.setXaPhuong(diaChiRequest.getXaPhuong());
            diaChi.setQuanHuyen(diaChiRequest.getQuanHuyen());
            diaChi.setTinhThanhPho(diaChiRequest.getTinhThanhPho());
            diaChi.setMacDinh(diaChiRequest.getMacDinh());
            diaChi.setKhachHang(khachHang);

            if (Boolean.TRUE.equals(diaChiRequest.getMacDinh())) {
                updateDefaultAddresses(khachHang.getId());
            }

            DiaChiTk savedDiaChi = diaChiRepo.save(diaChi);
            return new ResponseEntity<>(savedDiaChi, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thêm địa chỉ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> updateDiaChi(Integer id, DiaChiRequest diaChiRequest) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = (String) request.getSession().getAttribute("JWT_TOKEN");
            if (token == null) {
                return new ResponseEntity<>("Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED);
            }
            Integer currentCustomerId = jwtUtil.getCustomerIdFromToken(token);

            Optional<DiaChiTk> diaChiOptional = diaChiRepo.findById(id);
            if (diaChiOptional.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy địa chỉ với ID: " + id, HttpStatus.NOT_FOUND);
            }
            DiaChiTk diaChi = diaChiOptional.get();

            if (!diaChi.getKhachHang().getId().equals(currentCustomerId)) {
                return new ResponseEntity<>("Bạn không có quyền cập nhật địa chỉ này", HttpStatus.FORBIDDEN);
            }

            List<DiaChiTk> existingAddresses = diaChiRepo.findByKhachHang_IdAndDuongAndXaPhuongAndQuanHuyenAndTinhThanhPhoAndIdNot(
                    diaChi.getKhachHang().getId(), diaChiRequest.getDuong(), diaChiRequest.getXaPhuong(),
                    diaChiRequest.getQuanHuyen(), diaChiRequest.getTinhThanhPho(), id);
            if (!existingAddresses.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Địa chỉ đã tồn tại");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            diaChi.setDuong(diaChiRequest.getDuong());
            diaChi.setXaPhuong(diaChiRequest.getXaPhuong());
            diaChi.setQuanHuyen(diaChiRequest.getQuanHuyen());
            diaChi.setTinhThanhPho(diaChiRequest.getTinhThanhPho());

            if (Boolean.TRUE.equals(diaChiRequest.getMacDinh()) && Boolean.FALSE.equals(diaChi.getMacDinh())) {
                updateDefaultAddresses(diaChi.getKhachHang().getId());
            }

            diaChi.setMacDinh(diaChiRequest.getMacDinh());

            DiaChiTk updatedDiaChi = diaChiRepo.save(diaChi);
            return new ResponseEntity<>(updatedDiaChi, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi cập nhật địa chỉ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getDiaChiById(Integer id) {
        try {
            Optional<DiaChiTk> diaChiOptional = diaChiRepo.findById(id);
            if (diaChiOptional.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy địa chỉ với ID: " + id, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(diaChiOptional.get(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi lấy thông tin địa chỉ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> getDiaChiByKhachHangId(Integer khachHangId) {
        try {
            List<DiaChiResponse> diaChiList = diaChiRepo.findByKhachHangId(khachHangId);
            return new ResponseEntity<>(diaChiList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi lấy danh sách địa chỉ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteDiaChiById(Integer id) {
        try {
            Optional<DiaChiTk> diaChiOptional = diaChiRepo.findById(id);
            if (diaChiOptional.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy địa chỉ với ID: " + id, HttpStatus.NOT_FOUND);
            }
            DiaChiTk diaChi = diaChiOptional.get();
            if (Boolean.TRUE.equals(diaChi.getMacDinh())) {
                return new ResponseEntity<>("Không thể xóa địa chỉ mặc định", HttpStatus.BAD_REQUEST);
            }
            Integer khachHangId= diaChi.getKhachHang().getId();
            diaChiRepo.deleteById(id,khachHangId);
            return new ResponseEntity<>("Đã xóa địa chỉ thành công", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi xóa địa chỉ: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> setDefaultAddress(Integer id) {
        try {
            Optional<DiaChiTk> diaChiOptional = diaChiRepo.findById(id);
            if (diaChiOptional.isEmpty()) {
                return new ResponseEntity<>("Không tìm thấy địa chỉ với ID: " + id, HttpStatus.NOT_FOUND);
            }
            DiaChiTk diaChi = diaChiOptional.get();
            Integer khachHangId = diaChi.getKhachHang().getId();
            updateDefaultAddresses(khachHangId);
            diaChi.setMacDinh(true);
            diaChiRepo.save(diaChi);
            List<DiaChiResponse> diaChiResponses = diaChiRepo.findByKhachHangId(khachHangId);
            DiaChiResponse defaultDiaChiResponse = diaChiResponses.stream()
                    .filter(dc -> dc.getId().equals(id))
                    .findFirst()
                    .orElse(null);

            return new ResponseEntity<>(defaultDiaChiResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Lỗi khi thiết lập địa chỉ mặc định: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void updateDefaultAddresses(Integer khachHangId) {
        List<DiaChiTk> addresses = diaChiRepo.findAllById(
                diaChiRepo.findByKhachHangId(khachHangId).stream()
                        .map(DiaChiResponse::getId)
                        .toList()
        );
        for (DiaChiTk address : addresses) {
            if (Boolean.TRUE.equals(address.getMacDinh())) {
                address.setMacDinh(false);
                diaChiRepo.save(address);
            }
        }
    }
}
