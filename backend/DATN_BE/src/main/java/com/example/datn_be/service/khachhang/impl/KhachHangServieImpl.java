package com.example.datn_be.service.khachhang.impl;

import com.example.datn_be.dto.khachhang.response.DiaChiEditResponse;
import com.example.datn_be.dto.khachhang.response.KhachHangResponse;
import com.example.datn_be.dto.khachhang.resquest.DiaChiEditRequest;
import com.example.datn_be.dto.khachhang.resquest.KhachHangRequest;
import com.example.datn_be.entiy.khach_hang.DiaChiTk;
import com.example.datn_be.entiy.khach_hang.KhachHangTk;
import com.example.datn_be.exception.KhachHangException;
import com.example.datn_be.repository.KhachHangRepo;
import com.example.datn_be.service.khachhang.KhachHangService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class KhachHangServieImpl implements KhachHangService {

    @Autowired
    private final KhachHangRepo khachHangRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<?> getAllKhachHang(String keyword, Pageable pageable) {
        Page<KhachHangResponse> page = khachHangRepo.getAllKhachHang(keyword, pageable);
        Page<KhachHangRequest> resultPage= page.map(khachHang->{
            KhachHangRequest khachHangRequest = new KhachHangRequest();
            khachHangRequest.setId(khachHang.getId());
            khachHangRequest.setMaKhachHang(khachHang.getMaKhachHang());
            khachHangRequest.setHoTen(khachHang.getHoTen());
            khachHangRequest.setGioiTinh(khachHang.getGioiTinh());
            khachHangRequest.setNgaySinh(khachHang.getNgaySinh());
            khachHangRequest.setEmail(khachHang.getEmail());
            khachHangRequest.setSoDienThoai(khachHang.getSoDienThoai());
            khachHangRequest.setNgayTao(khachHang.getNgayTao());
            try {
                List<DiaChiEditRequest> list = objectMapper.readValue(
                        khachHang.getDiaChis(),new TypeReference<List<DiaChiEditRequest>>(){}
                );
                khachHangRequest.setDiaChis(list);
            } catch (Exception e) {
                khachHangRequest.setDiaChis(Collections.emptyList());
            }
            return khachHangRequest;
        });
        return new ResponseEntity<>(resultPage, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addKhachHang(KhachHangRequest khachHangRequest) {
        if (khachHangRepo.existsByEmail(khachHangRequest.getEmail())) {
            throw new KhachHangException("Email đã tồn tại trong hệ thống.");
        }

        if (khachHangRepo.existsBySoDienThoai(khachHangRequest.getSoDienThoai())) {
            throw new KhachHangException("Số điện thoại đã tồn tại trong hệ thống.");
        }
        KhachHangTk khachHang = new KhachHangTk();
        BeanUtils.copyProperties(khachHangRequest, khachHang);
        if (khachHangRequest.getDiaChis() != null && !khachHangRequest.getDiaChis().isEmpty()) {
            List<DiaChiTk> diaChis = khachHangRequest.getDiaChis().stream()
                    .map(diaChiRequest -> {
                        DiaChiTk diaChi = new DiaChiTk();
                        diaChi.setDuong(diaChiRequest.getDuong());
                        diaChi.setXaPhuong(diaChiRequest.getXaPhuong());
                        diaChi.setQuanHuyen(diaChiRequest.getQuanHuyen());
                        diaChi.setTinhThanhPho(diaChiRequest.getTinhThanhPho());
                        diaChi.setMacDinh(diaChiRequest.getMacDinh());
                        diaChi.setKhachHang(khachHang);
                        return diaChi;
                    })
                    .collect(Collectors.toList());

            khachHang.setDiaChis(diaChis);
        }
        return new ResponseEntity<>(khachHangRepo.save(khachHang), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateKhachHang(Integer id, KhachHangRequest khachHangRequest) {
        try {
            KhachHangTk khachHang = khachHangRepo.findById(id)
                    .orElseThrow(() -> new KhachHangException("Khách hàng không tồn tại với ID: " + id));

            if (khachHangRepo.existsByEmailAndIdNot(khachHangRequest.getEmail(), id)) {
                throw new KhachHangException("Email đã tồn tại trong hệ thống.");
            }
            if (khachHangRepo.existsBySoDienThoaiAndIdNot(khachHangRequest.getSoDienThoai(), id)) {
                throw new KhachHangException("Số điện thoại đã tồn tại trong hệ thống.");
            }
            khachHang.setHoTen(khachHangRequest.getHoTen());
            khachHang.setGioiTinh(khachHangRequest.getGioiTinh());
            khachHang.setNgaySinh(khachHangRequest.getNgaySinh());
            khachHang.setEmail(khachHangRequest.getEmail());
            khachHang.setSoDienThoai(khachHangRequest.getSoDienThoai());

            List<DiaChiTk> currentAddresses = khachHang.getDiaChis();

            Map<Integer, DiaChiTk> addressMap = currentAddresses.stream()
                    .filter(address -> address.getId() != null)
                    .collect(Collectors.toMap(DiaChiTk::getId, address -> address));

            Set<Integer> requestIds = new HashSet<>();

            for (DiaChiEditRequest req : khachHangRequest.getDiaChis()) {
                if (req.getId() != null) {
                    requestIds.add(req.getId());
                    DiaChiTk existingAddress = addressMap.get(req.getId());
                    if (existingAddress == null) {
                        throw new KhachHangException("Địa chỉ không tồn tại với id: " + req.getId());
                    }
                    for (DiaChiTk other : currentAddresses) {
                        if (!other.getId().equals(existingAddress.getId()) && isDuplicate(other, req)) {
                            throw new KhachHangException("Địa chỉ chỉnh sửa bị trùng");
                        }
                    }
                    existingAddress.setDuong(req.getDuong());
                    existingAddress.setXaPhuong(req.getXaPhuong());
                    existingAddress.setQuanHuyen(req.getQuanHuyen());
                    existingAddress.setTinhThanhPho(req.getTinhThanhPho());
                    existingAddress.setMacDinh(req.getMacDinh());
                } else {
                    DiaChiTk newAddress = new DiaChiTk();
                    newAddress.setDuong(req.getDuong());
                    newAddress.setXaPhuong(req.getXaPhuong());
                    newAddress.setQuanHuyen(req.getQuanHuyen());
                    newAddress.setTinhThanhPho(req.getTinhThanhPho());
                    newAddress.setMacDinh(req.getMacDinh());
                    newAddress.setKhachHang(khachHang);

                    for (DiaChiTk other : currentAddresses) {
                        if (isDuplicate(other, newAddress)) {
                            throw new KhachHangException("Địa chỉ mới bị trùng với địa chỉ đã có");
                        }
                    }
                    currentAddresses.add(newAddress);
                }
            }

            currentAddresses.removeIf(address -> address.getId() != null && !requestIds.contains(address.getId()));

            return new ResponseEntity<>(khachHangRepo.save(khachHang), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    Collections.singletonMap("error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public ResponseEntity<?> getKhachHangById(Integer id) {
        return khachHangRepo.findById(id)
                .map(khachHang -> {
                    return new ResponseEntity<>(khachHang, HttpStatus.OK);
                })
                .orElseThrow(() -> new NullPointerException("Không tìm thấy Khách Hàng"+id));
    }

    private boolean isDuplicate(DiaChiTk a, DiaChiEditRequest b) {
        return a.getDuong().equalsIgnoreCase(b.getDuong()) &&
                a.getXaPhuong().equalsIgnoreCase(b.getXaPhuong()) &&
                a.getQuanHuyen().equalsIgnoreCase(b.getQuanHuyen()) &&
                a.getTinhThanhPho().equalsIgnoreCase(b.getTinhThanhPho());
    }
    private boolean isDuplicate(DiaChiTk a, DiaChiTk b) {
        return a.getDuong().equalsIgnoreCase(b.getDuong()) &&
                a.getXaPhuong().equalsIgnoreCase(b.getXaPhuong()) &&
                a.getQuanHuyen().equalsIgnoreCase(b.getQuanHuyen()) &&
                a.getTinhThanhPho().equalsIgnoreCase(b.getTinhThanhPho());
    }
}
