package com.example.datn_be.validator;

import com.example.datn_be.dto.khachhang.resquest.KhachHangRequest;
import com.example.datn_be.repository.KhachHangRepo;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class KhachHangValidator implements Validator {

    private final KhachHangRepo khachHangRepo;

    public KhachHangValidator(KhachHangRepo khachHangRepo) {
        this.khachHangRepo = khachHangRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return KhachHangRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        KhachHangRequest request = (KhachHangRequest) target;

        // Nếu là thêm mới (không có ID)
        if (request.getId() == null) {
            if (khachHangRepo.existsByEmail(request.getEmail())) {
                errors.rejectValue("email", "error.email", "Email đã tồn tại");
            }
            if (khachHangRepo.existsBySoDienThoai(request.getSoDienThoai())) {
                errors.rejectValue("soDienThoai", "error.soDienThoai", "Số điện thoại đã tồn tại");
            }
        }
        // Nếu là cập nhật (có ID)
        else {
            // Kiểm tra email trùng, loại trừ email của chính khách hàng đang cập nhật
            if (khachHangRepo.existsByEmailAndIdNot(request.getEmail(), request.getId())) {
                errors.rejectValue("email", "error.email", "Email đã tồn tại");
            }
            // Kiểm tra số điện thoại trùng, loại trừ số điện thoại của chính khách hàng đang cập nhật
            if (khachHangRepo.existsBySoDienThoaiAndIdNot(request.getSoDienThoai(), request.getId())) {
                errors.rejectValue("soDienThoai", "error.soDienThoai", "Số điện thoại đã tồn tại");
            }
        }
    }
}
