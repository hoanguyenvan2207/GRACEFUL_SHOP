package com.example.datn_be.controller.nhanvien;

import com.example.datn_be.dto.nhanvien.NhanVienDTO;
import com.example.datn_be.dto.nhanvien.NhanVienRequest;
import com.example.datn_be.entiy.NhanVien.nhanvien;
import com.example.datn_be.entiy.NhanVien.VaiTro;
import com.example.datn_be.repository.NhanVien.NhanVienRepo;
import com.example.datn_be.repository.VaiTroRepo;
import com.example.datn_be.service.nhanvien.NhanVienServiceIml;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;


@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/nhan-vien")
public class nhanviencontroller {
  @Autowired
  private NhanVienRepo nhanvienRepo;
  @Autowired
  private VaiTroRepo vaiTroRepo;
  @Autowired
  private  NhanVienServiceIml nhanvienServiceIml;
    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhone(@RequestParam String phone) {
        boolean exists = nhanvienRepo.existsBySoDienThoai(phone);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = nhanvienServiceIml.existsByEmail(email);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }
@GetMapping("/account")
    public List<NhanVienDTO> getAllNhanVien() {
        return nhanvienRepo.getAllNhanVienaccount();
    }


    @GetMapping("/hien-thi-nhan-vien")
public ResponseEntity<?> getAllNhanVien(
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        @RequestParam(required = false) Boolean status
) {
    Pageable pageable = PageRequest.of(page, size);
    return nhanvienServiceIml.getAllNhanVien(status, keyword, pageable);
}
@GetMapping("/show-vai-tro")
    public List<VaiTro> getvaitro() {
    return vaiTroRepo.findAll();

}
    @PostMapping("/add")
    public ResponseEntity<?> addNhanVien(@Valid @RequestBody NhanVienRequest nhanVienRequest){
        return nhanvienServiceIml.addNhanVien(nhanVienRequest);
    }
@GetMapping("/update/{id}")
public ResponseEntity<nhanvien> update2(@PathVariable Integer id) {
    Optional<nhanvien> nhanVien = nhanvienRepo.findById(id);

    if (nhanVien.isPresent()) {
        return ResponseEntity.ok(nhanVien.get());
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        boolean exists = nhanvienRepo.existsByTenDangNhap(username);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNhanVien(@PathVariable Integer id, @RequestBody NhanVienRequest nhanVienRequest){
        return nhanvienServiceIml.updateNhanVien(id, nhanVienRequest);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteNhanVien(@PathVariable Integer id) {
        Optional<nhanvien> optionalNhanVien = nhanvienRepo.findById(id);
        if (optionalNhanVien.isPresent()) {
            nhanvien nhanVien = optionalNhanVien.get();
            nhanVien.setTrangThai(false);
            nhanvienRepo.save(nhanVien);
            return "trạng thái đã chuyển sang 0";
        }
        return "Không tìm thấy nhân viên với id: " + id;
    }
    @GetMapping("/details/{id}")
    public ResponseEntity<?> detailsNhanVien(@PathVariable Integer id){
        return nhanvienServiceIml.getNhanVienById(id);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<byte[]> exportEmployeesToExcel() throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Nhân viên");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Mã nhân viên");
        headerRow.createCell(2).setCellValue("Họ tên");
        headerRow.createCell(3).setCellValue("Giới tính");
        headerRow.createCell(4).setCellValue("Ngày Sinh");
        headerRow.createCell(5).setCellValue("Email");
        headerRow.createCell(6).setCellValue("Số điện thoai");
        headerRow.createCell(7).setCellValue("Địa chỉ");
        headerRow.createCell(8).setCellValue("Trạng Thái");
        headerRow.createCell(9).setCellValue("Chức vụ");
        List<nhanvien> employees = nhanvienServiceIml.getAllNhanViens();
        int rowIdx = 1;
        for (nhanvien emp : employees) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(emp.getId());
            row.createCell(1).setCellValue(emp.getMaNhanVien());
            row.createCell(2).setCellValue(emp.getHoVaTen());
            row.createCell(3).setCellValue(emp.getGioiTinh() == 1  ? "Nam" : "Nữ");

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("dd/MM/yyyy")
            );

            Cell cell = row.createCell(4);

            if (emp.getNgaySinh() != null) {
                Instant instant = emp.getNgaySinh()
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant();
                Date date = Date.from(instant);
                cell.setCellValue(date);      // Ghi vào cell dưới dạng Date
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellValue("");
            }
            row.createCell(5).setCellValue(emp.getEmail());
            row.createCell(6).setCellValue(emp.getSoDienThoai());
            row.createCell(7).setCellValue(emp.getDiaChi());
            row.createCell(8).setCellValue(emp.getTrangThai() ? "Đang Làm" : "Đã Nghỉ Làm");
            row.createCell(9).setCellValue(emp.getVaiTro().getTenVaiTro());
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        byte[] excelBytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "NhanVien.xlsx");
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @PutMapping("/cap-nhat-thong-tin/{id}")
    public ResponseEntity<?> capNhatThongTin(@PathVariable Integer id, @RequestBody NhanVienRequest request) {
        try {
            nhanvien updated = nhanvienServiceIml.updateThongTin(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật thông tin: " + e.getMessage());
        }
    }


}
