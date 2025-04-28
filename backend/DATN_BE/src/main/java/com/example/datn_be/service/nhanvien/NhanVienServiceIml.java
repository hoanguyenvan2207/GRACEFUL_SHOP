package com.example.datn_be.service.nhanvien;


import com.example.datn_be.dto.nhanvien.NhanVienRequest;
import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.List;


@Service
public class NhanVienServiceIml implements NhanVienService {

@Autowired
private final NhanVienRepo nhanVienRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public NhanVienServiceIml(NhanVienRepo nhanVienRepo) {
        this.nhanVienRepo = nhanVienRepo;
    }

    public boolean existsByEmail(String email) {
        return nhanVienRepo.existsByEmail(email);
    }
    @Override
    public ResponseEntity<?> getAllNhanVien(Boolean status, String keyword, Pageable pageable) {
        Page<nhanvien> nhanViens;
        if (status != null) {
            nhanViens = nhanVienRepo.findByKeywordWithPagination(keyword, status, pageable);
        } else {
            nhanViens = nhanVienRepo.findByKeywordWithPagination(keyword,null,pageable);
        }
        if (nhanViens.isEmpty()) {
            return new ResponseEntity<>("Không tìm thấy nhân viên nào!", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(nhanViens, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> addNhanVien(NhanVienRequest nhanVienRequest) {
        if (nhanVienRepo.existsByEmail(nhanVienRequest.getEmail())) {
            return new ResponseEntity<>("Email đã tồn tại!", HttpStatus.BAD_REQUEST);
        }

        if (nhanVienRepo.existsByTenDangNhap(nhanVienRequest.getTenDangNhap())) {
            return new ResponseEntity<>("Tên đăng nhập đã tồn tại!", HttpStatus.BAD_REQUEST);
        }
        nhanvien nv=new nhanvien();
        BeanUtils.copyProperties(nhanVienRequest, nv);
        String username = nhanVienRequest.getTenDangNhap();
        String matkhautamthoi = (nhanVienRequest.getMatKhau() == null || nhanVienRequest.getMatKhau().isEmpty())
                ? generateTempPassword()
                : nhanVienRequest.getMatKhau();
        nv.setMatKhau(passwordEncoder.encode(matkhautamthoi));
        nv.setTenDangNhap(username);
        nv.setMat_khau_tam_thoi(0);
        sendEmail(nv.getEmail(), nv.getHoVaTen(), username, matkhautamthoi);

        return new ResponseEntity<>(nhanVienRepo.save(nv), HttpStatus.OK);

    }

    @Override
    public nhanvien updateThongTin(Integer id, NhanVienRequest nhanVienRequest) {
        nhanvien nv = nhanVienRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        nv.setSoDienThoai(nhanVienRequest.getSoDienThoai());
        nv.setEmail(nhanVienRequest.getEmail());
        nv.setDiaChi(nhanVienRequest.getDiaChi());

        return nhanVienRepo.save(nv);
    }


    private String generateTempPassword() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    SecureRandom random = new SecureRandom();
    StringBuilder password = new StringBuilder();

    for (int i = 0; i < 8; i++) {
        password.append(chars.charAt(random.nextInt(chars.length())));
    }

    return password.toString();
}
    // Phương thức gửi email
    private void sendEmail(String email, String hoTen, String username, String mat_khau_tam_thoi) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Thông tin tài khoản nhân viên");
            helper.setText("<h3>Xin chào " + hoTen + ",</h3>"
                    + "<p>Bạn đã được đăng ký tài khoản trong hệ thống.</p>"
                    + "<p><b>Tên đăng nhập:</b> " + username + "</p>"
                    + "<p><b>Mật khẩu tạm thời:</b> " + mat_khau_tam_thoi + "</p>"
                    + "<p>Vui lòng đổi mật khẩu sau khi đăng nhập.</p>"
                    + "<br><p>Trân trọng,</p>"
                    + "<p>Hệ thống Quản lý Nhân viên</p>", true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public ResponseEntity<?> updateNhanVien(Integer id, NhanVienRequest nhanVienRequest) {
        return nhanVienRepo.findById(id)
                .map(nhanvien -> {
                    nhanvien.setMaNhanVien(nhanVienRequest.getMaNhanVien());
                    nhanvien.setTenDangNhap(nhanVienRequest.getTenDangNhap());
                    nhanvien.setHoVaTen(nhanVienRequest.getHoVaTen());
                    nhanvien.setGioiTinh(nhanVienRequest.getGioiTinh());
                    nhanvien.setNgaySinh(nhanVienRequest.getNgaySinh());
                    nhanvien.setMatKhau(nhanVienRequest.getMatKhau());
                    nhanvien.setEmail(nhanVienRequest.getEmail());
                    nhanvien.setSoDienThoai(nhanVienRequest.getSoDienThoai());
                    nhanvien.setDiaChi(nhanVienRequest.getDiaChi());
                    nhanvien.setTrangThai(nhanVienRequest.getTrangThai());
                    nhanvien.setVaiTro(nhanVienRequest.getVaiTro());
                    return new ResponseEntity<>(nhanVienRepo.save(nhanvien), HttpStatus.OK);

                })
                .orElseThrow(() -> new NullPointerException("nhân viên not found!"));
    }
    @Override
    public ResponseEntity<?> getNhanVienById(Integer id) {
        return nhanVienRepo.findById(id)
                .map(nhanvien -> {
                    return new ResponseEntity<>(nhanvien, HttpStatus.OK);
                })
                .orElseThrow(() -> new NullPointerException("nhân viên not found!"));
    }

    @Override
    public List<nhanvien> getAllNhanViens() {
        return nhanVienRepo.findAll();
    }

}
