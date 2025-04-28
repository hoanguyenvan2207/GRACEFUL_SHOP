package com.example.datn_be.dto.khuyenmai;
import com.example.datn_be.entiy.giam_gia.SanPhamChiTietKM;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SPCTDTO {
    private  Integer id;
    private String maAoDaiChiTiet;
    private String tenAoDai;
    private String kichThuoc;
    private String mauSac;
}
