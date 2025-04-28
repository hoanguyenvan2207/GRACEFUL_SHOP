package com.example.datn_be.repository;

import com.example.datn_be.dto.thongke.response.DoanhThuTheoKhoangThoiGianRes;
import com.example.datn_be.dto.thongke.response.DoanhThuTheoNamResponse;
import com.example.datn_be.dto.thongke.response.DoanhThuTheoThangResponse;
import com.example.datn_be.dto.thongke.response.DoanhThuTrongNgayResponse;
import com.example.datn_be.dto.thongke.response.KetQuaThongKeTop5Response;
import com.example.datn_be.dto.thongke.response.LoaiAoDaiBanNhieuResponse;
import com.example.datn_be.entiy.khach_hang.AoDaiTk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ThongKeRepo extends JpaRepository<AoDaiTk, Integer> {
    @Query(value = """
                    SELECT TOP 5
                        ad.ma_ao_dai as maAoDai,
                    	ad.ten_ao_dai as tenAoDai,
                        SUM(hdct.so_luong) as tongSoLuongDaBan,
                        SUM(hdct.so_luong * hdct.don_gia) as tongTienDaBan
                    FROM ao_dai ad
                    JOIN ao_dai_chi_tiet adct ON ad.id = adct.id_ao_dai
                    JOIN hoa_don_chi_tiet hdct ON adct.id = hdct.id_ao_dai_chi_tiet
                    JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
                    WHERE hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng')
                    GROUP BY ad.ma_ao_dai, ad.ten_ao_dai
                    ORDER BY tongSoLuongDaBan DESC
            """, nativeQuery = true)
    List<KetQuaThongKeTop5Response> getTop5AoDaiBanChay();

    @Query(value = """
                   SELECT
                         CAST(hd.ngay_tao AS DATE) as ngay,
                         COUNT(DISTINCT hd.id) as so_hoa_don,
                         SUM(hd.tong_tien) - SUM(hd.phi_giao_hang) as tong_doanh_thu,
                         SUM(ct.tong_so_luong) as tong_san_pham
                         FROM hoa_don hd
                             LEFT JOIN (
                                  SELECT id_hoa_don, SUM(so_luong) as tong_so_luong
                                  FROM hoa_don_chi_tiet
                                  GROUP BY id_hoa_don
                                        ) ct ON hd.id = ct.id_hoa_don
                         WHERE hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng')
                         AND CAST(hd.ngay_tao AS DATE) = :ngay
                         GROUP BY CAST(hd.ngay_tao AS DATE)
            """, nativeQuery = true)
    List<DoanhThuTrongNgayResponse> getDoanhThuTrongNgay(LocalDate ngay);

    @Query(value = """ 
               SELECT 
                    COUNT(CASE WHEN hd.hinh_thuc_mua_hang = 1 THEN 1 END) as tongDonOnline, 
                    COUNT(CASE WHEN hd.hinh_thuc_mua_hang = 1 AND hd.trang_thai = N'Chờ xác nhận' THEN 1 END) as donChuaXacNhan, 
                    COUNT(CASE WHEN hd.hinh_thuc_mua_hang = 1 AND hd.trang_thai = N'Đã hủy đơn' THEN 1 END) as donHuy, 
                    COUNT(CASE WHEN hd.hinh_thuc_mua_hang = 1 AND hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') THEN 1 END) as donDaThanhToan, 
                    COUNT(CASE WHEN hd.hinh_thuc_mua_hang = 0 THEN 1 END) as tongDonOffline, 
                    ISNULL(SUM(CASE WHEN hd.hinh_thuc_mua_hang = 1 AND hd.trang_thai = N'Đã nhận hàng'  THEN hd.tong_tien - hd.phi_giao_hang ELSE 0 END), 0) as doanhThuOnline, 
                    ISNULL(SUM(CASE WHEN hd.hinh_thuc_mua_hang = 0 AND hd.trang_thai = N'Đã thanh toán' THEN hd.tong_tien ELSE 0 END), 0) as doanhThuOffline, 
                    COUNT(DISTINCT hd.id) AS soHoaDon, 
                    ISNULL(SUM(CASE WHEN hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') THEN hd.tong_tien - hd.phi_giao_hang ELSE 0 END), 0) as tongDoanhThu, 
                    ISNULL(SUM(CASE WHEN hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') THEN ct.so_luong ELSE 0 END), 0) AS tongSanPham 
               FROM hoa_don hd 
               LEFT JOIN (
                    SELECT id_hoa_don, SUM(so_luong) as so_luong
                    FROM hoa_don_chi_tiet 
                    GROUP BY id_hoa_don 
               ) ct ON hd.id = ct.id_hoa_don 
               WHERE CAST(hd.ngay_tao AS DATE) = :homNay 
        """, nativeQuery = true)
    DoanhThuTrongNgayResponse getDoanhThuTheoNgay(LocalDate homNay);

    @Query(value = """
                SELECT
                        DAY(hd.ngay_tao) as ngay,
                        COUNT(DISTINCT hd.id) as tongSoHoaDon,
                        CAST(SUM(hd.tong_tien) - SUM(hd.phi_giao_hang) AS DECIMAL(20,2)) as tongDoanhThuNgay,
                        COALESCE(SUM(ct.tong_so_luong), 0) as tongSanPham
                FROM hoa_don hd
                    LEFT JOIN (
                        SELECT id_hoa_don, SUM(so_luong) as tong_so_luong
                        FROM hoa_don_chi_tiet
                        GROUP BY id_hoa_don
                    ) ct ON hd.id = ct.id_hoa_don
                WHERE hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng')
                        AND MONTH(hd.ngay_tao) = :thang
                        AND YEAR(hd.ngay_tao) = :nam
                GROUP BY DAY(hd.ngay_tao), MONTH(hd.ngay_tao), YEAR(hd.ngay_tao)
                ORDER BY DAY(hd.ngay_tao) ASC
            """, nativeQuery = true)
    List<DoanhThuTheoThangResponse> getDoanhThuTheoThang(Integer thang, Integer nam);

    @Query(value = """
                SELECT
                       MONTH(hd.ngay_tao) as thang,
                       COUNT(DISTINCT hd.id) as tongSoHoaDon,
                       CAST(SUM(hd.tong_tien) - SUM(hd.phi_giao_hang) AS DECIMAL(20,2)) as tongDoanhThuThang,
                       COALESCE(SUM(ct.tong_so_luong), 0) as tongSanPham
                   FROM hoa_don hd
                   LEFT JOIN (
                       SELECT id_hoa_don, SUM(so_luong) as tong_so_luong
                       FROM hoa_don_chi_tiet
                       GROUP BY id_hoa_don
                   ) ct ON hd.id = ct.id_hoa_don
                   WHERE hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng')
                     AND YEAR(hd.ngay_tao) = :nam
                   GROUP BY MONTH(hd.ngay_tao)
                   ORDER BY thang ASC
            """, nativeQuery = true)
    List<DoanhThuTheoNamResponse> getDoanhThuTheoNam(Integer nam);


    @Query(value = """
         WITH DateRange AS (
             SELECT TOP (DATEDIFF(DAY, :startDate, :endDate) + 1)
                    DATEADD(DAY, ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) - 1, :startDate) AS ngay
             FROM master.dbo.spt_values
         )
         SELECT
             dr.ngay AS ngay,
             COUNT(DISTINCT hd.id) AS tongHoaDon,
             ISNULL(SUM(CASE WHEN hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') THEN hd.tong_tien - hd.phi_giao_hang ELSE 0 END), 0) AS tongDoanhThu,
             ISNULL(SUM(CASE WHEN hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng') THEN ct.so_luong ELSE 0 END), 0) AS tongSanPhamBanDuoc
         FROM DateRange dr
         LEFT JOIN hoa_don hd ON CAST(hd.ngay_tao AS DATE) = dr.ngay
         LEFT JOIN (
             SELECT id_hoa_don, SUM(so_luong) as so_luong
             FROM hoa_don_chi_tiet
             GROUP BY id_hoa_don
         ) ct ON hd.id = ct.id_hoa_don
         GROUP BY dr.ngay
         ORDER BY dr.ngay DESC;
""", nativeQuery = true)
    List<DoanhThuTheoKhoangThoiGianRes> getDoanhThuTheoKhoangThoiGian(LocalDate startDate, LocalDate endDate);

    @Query(value = """
            SELECT
                    lad.ten_loai_ao_dai AS tenLoaiAoDai,
                    SUM(hdct.so_luong) AS soLuong,
                    CAST(
                        SUM(hdct.so_luong) * 100.0\s
                        / SUM(SUM(hdct.so_luong)) OVER()\s
                        AS DECIMAL(10,2)
                    ) AS phanTram
                FROM loai_ao_dai lad
                JOIN ao_dai ad ON lad.id = ad.id_loai_ao_dai
                JOIN ao_dai_chi_tiet adct ON ad.id = adct.id_ao_dai
                JOIN hoa_don_chi_tiet hdct ON adct.id = hdct.id_ao_dai_chi_tiet
                JOIN hoa_don hd ON hd.id = hdct.id_hoa_don
                  AND hd.trang_thai IN (N'Đã thanh toán', N'Đã nhận hàng')
                GROUP BY lad.ten_loai_ao_dai
                ORDER BY SUM(hdct.so_luong) DESC
            """, nativeQuery = true)
    List<LoaiAoDaiBanNhieuResponse> getLoaiAoDaiBanNhieu();
}
