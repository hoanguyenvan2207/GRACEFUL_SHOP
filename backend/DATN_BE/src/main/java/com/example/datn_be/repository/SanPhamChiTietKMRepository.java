package com.example.datn_be.repository;
import com.example.datn_be.dto.khuyenmai.SPCTDTO;
import com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO;
import com.example.datn_be.entiy.giam_gia.SanPhamChiTietKM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SanPhamChiTietKMRepository extends JpaRepository<SanPhamChiTietKM, Integer> {

    @Query("""
    SELECT new com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO
        (
        spct.id,
        spct.maAoDaiChiTiet,
        spct.aoDai.tenAoDai,
        spct.giaGoc,
        spct.khuyenMai.id,
        spct.kichThuoc.ten,
        spct.mauSac.tenMauSac
        )FROM SanPhamChiTietKM spct 
            """)
    Page<SanPhamChiTietKhuyenMaiDTO> getListSPCT(Pageable pageable);

    @Query(value = "SELECT spct.id, spct.ma_ao_dai_chi_tiet, sp.ten_ao_dai, kt.ten_kich_thuoc, ms.ten_mau_sac " +
            "FROM ao_dai_chi_tiet spct " +
            "LEFT JOIN ao_dai sp ON spct.id_ao_dai = sp.id " +
            "LEFT JOIN kich_thuoc kt ON spct.id_kich_thuoc = kt.id " +
            "LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id " +
            "WHERE spct.ma_ao_dai_chi_tiet LIKE %:keyq% " +
            "OR sp.ten_ao_dai LIKE %:keyq%", nativeQuery = true)
    Page<SPCTDTO> searchSpctByKey(@Param("keyq") String keyq, Pageable pageable);



    @Query(value = """
    SELECT spct.id FROM SanPhamChiTietKM spct 
    WHERE spct.id IN :sanPhamIds AND spct.khuyenMai IS NOT NULL
            """)
    List<Integer> timSanPhamTrongChuongTrinhKhuyenMai(@Param("sanPhamIds") List<Integer> sanPhamIds);

    @Query("""
    SELECT new com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO(
        spct.id,
        spct.maAoDaiChiTiet,
        spct.aoDai.tenAoDai,
        spct.giaGoc,
        spct.khuyenMai.id,
        spct.kichThuoc.ten,
        spct.mauSac.tenMauSac
    )
    FROM SanPhamChiTietKM spct WHERE spct.khuyenMai.id = ?1
""")
    List<SanPhamChiTietKhuyenMaiDTO> getListSanbyIdKhuyenMai(Integer id);
    List<SanPhamChiTietKM> findByKhuyenMaiId(Integer id);
    @Query("SELECT new com.example.datn_be.dto.khuyenmai.SanPhamChiTietKhuyenMaiDTO" +
            "(spct.id, " +
            "spct.maAoDaiChiTiet," +
            "spct.aoDai.tenAoDai, " +
            "spct.giaGoc," +
            "spct.khuyenMai.id, " +
            "spct.kichThuoc.ten," +
            "spct.mauSac.tenMauSac)" +


            "FROM SanPhamChiTietKM spct WHERE spct.khuyenMai.id = :khuyenMaiId")
    List<SanPhamChiTietKhuyenMaiDTO> findDetailsByKhuyenMaiId(@Param("khuyenMaiId") Integer khuyenMaiId);

}