<template>
  <div class="container mt-4">
    <!-- Phần Tab -->
    <div class="tabs">
      <button :class="{ active: activeTab === 'offline' }" @click="switchTab('offline')">Tại quầy</button>
      <button :class="{ active: activeTab === 'online' }" @click="switchTab('online')">Online</button>
    </div>

    <div class="search-timkiem">
      <input type="text" v-model="searchQuery" placeholder="Nhập mã HD hoặc SDT KH" />
      <button @click="timKiemHoaDon">Tìm kiếm</button>

      <label for="trangThai">Trạng thái: </label>
      <select v-model="trangThai" @change="timKiemHoaDonTheoTrangThai">
        <option v-for="option in statusOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>

      <div class="date-search">
        <div>
          <label for="fromDate">Từ :</label>
          <input type="date" v-model="fromDate" />
        </div>
        <div>
          <label for="toDate">Đến :</label>
          <input type="date" v-model="toDate" />
          <button @click="timKiemDonHangTheoNgay">Tìm kiếm</button>
        </div>
      </div>

      <!-- Nút xuất Excel giờ đây chỉ mở modal lựa chọn -->
      <button class="xuat-excel" @click="openExportModal">
        <i class="fas fa-file-excel"></i> Xuất Excel
      </button>
    </div>

    <div class="table-container">
      <table class="table table-bordered table-hover">
        <thead>
          <tr>
            <th>#</th>
            <th>Mã Hóa đơn</th>
            <th>Mã nhân viên</th>
            <th>Tên nhân viên</th>
            <th>Tên khách hàng</th>
            <th>Số điện thoại</th>
            <th>Ngày Tạo</th>
            <th>Trạng Thái</th>
            <th>Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(hoaDon, index) in hoaDons" :key="hoaDon.id">
            <td>{{ page * size + index + 1 }}</td>
            <td>
              <input type="checkbox" :value="hoaDon.id" v-model="selectedHoaDonIds" />
              {{ hoaDon.maHoaDon || '-' }}
            </td>
            <td>{{ hoaDon.nhanVien?.maNhanVien || '-' }}</td>
            <td>{{ hoaDon.nhanVien?.hoVaTen || '-' }}</td>
            <td>{{ hoaDon.khachHang?.hoTen || '-' }}</td>
            <td>{{ hoaDon.khachHang?.soDienThoai || '-' }}</td>
            <td>
              {{ hoaDon.ngayTao ? new Date(hoaDon.ngayTao).toLocaleString("vi-VN") : '-' }}
            </td>
            <td>{{ hoaDon.trangThai || '-' }}</td>
            <td>
              <i class="fas fa-eye eye-icon" @click="hienThiHoaDonChiTiet(hoaDon.id)"></i>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div class="phanTrang">
      <button :disabled="page === 0 || totalPages === 0" @click="diToiTrangDau">First</button>
      <button :disabled="page === 0 || totalPages === 0" @click="diToiTrangTruoc">Previ</button>
      <div class="phanTrang-info">
        <span>{{ totalPages === 0 ? 0 : page + 1 }} / {{ totalPages }}</span>
      </div>
      <button :disabled="page >= totalPages - 1 || totalPages === 0" @click="diToiTrangSau">Next</button>
      <button :disabled="page >= totalPages - 1 || totalPages === 0" @click="diToiTrangCuoi">Last</button>
    </div>

    <!-- Modal hiển thị chi tiết hóa đơn -->
    <div class="modal-overlay" v-if="modalVisible" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeModal">&times;</button>
        <h5 class="modal-title">Chi tiết hóa đơn: {{ selectedHoaDon?.maHoaDon }}</h5>

        <!-- Thông tin khách hàng -->
        <div class="order-info">
          <div class="info-row">
            <span class="label">Mã khách hàng:</span>
            <span class="value">{{ selectedHoaDon.khachHang?.maKhachHang || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">Tên khách hàng:</span>
            <span class="value">{{ selectedHoaDon.khachHang?.hoTen || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="label">Số điện thoại:</span>
            <span class="value">{{ selectedHoaDon.khachHang?.soDienThoai || '-' }}</span>
          </div>
          <div v-if="selectedHoaDon.diaChiGiaoHang" class="info-row full">
            <span class="label">Địa Chỉ Giao:</span>
            <span class="value">{{ selectedHoaDon.diaChiGiaoHang || '-' }}</span>
          </div>
        </div>

        <!-- Danh sách sản phẩm trong hóa đơn -->
        <div v-if="hoaDonChiTiets.length > 0" class="product-section">
          <h5 class="section-title">Sản phẩm trong hóa đơn</h5>
          <div class="product-cards">
            <div class="product-card" v-for="ct in hoaDonChiTiets" :key="ct.id">
              <div class="product-description">
                <p><strong>Mã sản phẩm:</strong> {{ ct.aoDaiChiTiet.maAoDaiChiTiet }}</p>
                <p>
                  <strong>Tên sản phẩm:</strong>
                  {{ ct.aoDaiChiTiet.aoDai.tenAoDai }}

                </p>
                <p>
                  <strong>Phân loại:</strong>
                  {{ ct.aoDaiChiTiet.aoDai.taAo.ten }} -
                  Màu {{ ct.aoDaiChiTiet.mauSac.tenMauSac }} -
                  Size {{ ct.aoDaiChiTiet.kichThuoc.ten }} -
                  Chất liệu {{ ct.aoDaiChiTiet.aoDai.chatLieu.ten }}
                </p>
                <p><strong>Số lượng:</strong> {{ ct.soLuong }}</p>
                <p v-if="ct.aoDaiChiTiet.giaGoc !== ct.giaBan">
                  <strong>Giá gốc:</strong>
                  <span class="text-danger"><del>{{ formatCurrency(ct.aoDaiChiTiet.giaGoc) }}</del></span>
                </p>
                <p>
                  <strong>Đơn giá:</strong>
                  <span class="price">{{ formatCurrency(ct.giaBan) }}</span>
                </p>
              </div>
              <div class="image-wrapper">
                <img :src="ct.aoDaiChiTiet.anhUrl" class="product-image" alt="Product Image" />
              </div>
            </div>
          </div>
        </div>

        <!-- Phần tóm tắt thanh toán -->
        <div class="payment-summary">
          <h5 class="section-title">Tóm tắt thanh toán</h5>
          <div class="payment-row">
            <span class="label">Hình thức mua:</span>
            <span class="value">
              {{ selectedHoaDon.hinhThucMuaHang != null
                ? (selectedHoaDon.hinhThucMuaHang ? 'Online' : 'Tại quầy')
                : '-' }}
            </span>
          </div>

          <!-- Nếu Tại quầy -->
          <div class="payment-row" v-if="selectedHoaDon.hinhThucMuaHang === false">
            <span class="label">Phương thức thanh toán:</span>
            <span class="value">
              <!-- Giả sử bạn lưu mã thanh toán tại quầy bằng biến phuongThucThanhToan -->
              {{ selectedHoaDon.phuongThucThanhToan != null
                ? (selectedHoaDon.phuongThucThanhToan
                  ? 'Chuyển khoản' : 'Tiền mặt') : '-' }}
            </span>
          </div>

          <!-- Nếu Online -->
          <div class="payment-row" v-else-if="selectedHoaDon.hinhThucMuaHang === true">
            <span class="label">Phương thức thanh toán:</span>
            <span class="value">
              <!-- Giả sử bạn lưu mã thanh toán online bằng biến phuongThucThanhToanOnline -->
              {{ selectedHoaDon.phuongThucThanhToanOnline != null
                ? (selectedHoaDon.phuongThucThanhToanOnline
                  ? 'Thanh toán qua VNPay'
                  : 'Thanh toán khi nhận hàng(COD)')
                : '-' }}
            </span>
          </div>

          <!-- Ngược lại (khi chưa chọn hình thức mua) -->
          <div class="payment-row" v-else>
            <span class="label">Phương thức thanh toán:</span>
            <span class="value">-</span>
          </div>

          <div class="payment-row">
            <span class="label">Tổng tiền hàng:</span>
            <span class="value">{{ formatCurrency(totalTienHang) }}</span>
          </div>
          <div class="payment-row" v-if="activeTab === 'online'">
            <span class="label">Phí ship:</span>
            <span class="value">{{ formatCurrency(selectedHoaDon.phiGiaoHang) }}</span>
          </div>
          <div class="payment-row">
            <span class="label">Tiền trước giảm:</span>
            <span class="value">{{ formatCurrency(selectedHoaDon.tienTruocGiam) }}</span>
          </div>
          <div class="payment-row">
            <span class="label">Mã giảm giá:</span>
            <span class="value">
              {{ selectedHoaDon.giamGia ? selectedHoaDon.giamGia.maGiamGia : '-' }}
            </span>
          </div>
          <div class="payment-row">
            <span class="label">Giảm giá được:</span>
            <span class="value">{{ formatCurrency(discountAmount) }}</span>
          </div>
          <div class="payment-row total">
            <span class="label">Tổng tiền:</span>
            <span class="value">{{ formatCurrency(selectedHoaDon.tongTien) }}</span>
          </div>
        </div>

      </div>
    </div>

    <!-- Modal xuất Excel -->
    <div class="modal-overlay" v-if="showExportModal" @click="closeExportModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeExportModal">&times;</button>
        <h5 style="text-align: center; margin-bottom: 20px;">Chọn kiểu xuất Excel</h5>
        <div class="export-option" style="display: flex; flex-direction: column; gap: 10px;">
          <label>
            <input type="radio" v-model="exportOption" value="selected" />
            Xuất excel hóa đơn được chọn
          </label>
          <label>
            <input type="radio" v-model="exportOption" value="searched" />
            Xuất excel hóa đơn được tìm kiếm
          </label>
          <label>
            <input type="radio" v-model="exportOption" value="all" />
            Xuất excel tất cả hóa đơn
          </label>
        </div>
        <div style="text-align: right; margin-top: 20px;">
          <button class="btn-export confirm" @click="confirmExport">Xác nhận</button>
          <button class="btn-export cancel" @click="closeExportModal">Hủy</button>
        </div>

      </div>
    </div>

  </div>
</template>

<script>
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import axios from "axios";
import { notification } from "ant-design-vue";

export default {
  data() {
    return {
      hoaDons: [],
      hoaDonChiTiets: [],
      searchQuery: "",
      trangThai: "",
      fromDate: "",
      toDate: "",
      page: 0,
      size: 5,
      totalPages: 0,
      modalVisible: false,
      selectedHoaDon: null,
      selectedHoaDonIds: [],
      activeTab: "offline",
      showExportModal: false,
      exportOption: "selected"
    };
  },
  computed: {
    statusOptions() {
      if (this.activeTab === "offline") {
        return [
          { value: "", label: "Tất cả" },
          { value: "Chưa thanh toán", label: "Chưa thanh toán" },
          { value: "Đã thanh toán", label: "Đã thanh toán" }
        ];
      } else {
        return [
          { value: "", label: "Tất cả" },
          { value: "Chờ xác nhận", label: "Chờ xác nhận" },
          { value: "Đã xác nhận", label: "Đã xác nhận" },
          { value: "Đang giao hàng", label: "Đang giao hàng" },
          { value: "Đã nhận hàng", label: "Đã nhận hàng" },
          { value: "Đã hủy đơn", label: "Đã hủy đơn" }
        ];
      }
    },
    discountAmount() {
      if (!this.selectedHoaDon || !this.selectedHoaDon.giamGia) {
        return 0;
      }
      const discount = this.selectedHoaDon.giamGia;
      const preDiscount = this.selectedHoaDon.tienTruocGiam || 0;
      let discountValue = 0;
      if (discount.loaiGiamGia === 0) {
        discountValue = discount.giaTriGiam;
      } else if (discount.loaiGiamGia === 1) {
        discountValue = (preDiscount * discount.giaTriGiam) / 100;
      }
      if (discount.toiDaGiamGia != null && discountValue > discount.toiDaGiamGia) {
        discountValue = discount.toiDaGiamGia;
      }
      return discountValue;
    },
    totalTienHang() {
      if (!this.hoaDonChiTiets || this.hoaDonChiTiets.length === 0) {
        return 0;
      }
      return this.hoaDonChiTiets.reduce((total, ct) => {
        const tien = (ct.giaBan || 0) * (ct.soLuong || 0);
        return total + tien;
      }, 0);
    }
  },
  created() {
    this.hienThiHoaDon();
  },
  methods: {
    formatCurrency(value) {
      if (value == null || isNaN(value)) return "-";
      return Number(value).toLocaleString("vi-VN") + " vnđ";
    },
    switchTab(tab) {
      this.activeTab = tab;
      this.page = 0;
      this.searchQuery = "";
      this.trangThai = "";
      this.fromDate = "";
      this.toDate = "";
      this.hienThiHoaDon();
    },
    getSearchParams() {
      return {
        page: this.page,
        size: this.size,
        searchQuery: this.searchQuery || null,
        trangThai: this.trangThai || null,
        fromDate: this.fromDate || null,
        toDate: this.toDate || null,
        hinhThucMuaHang: this.activeTab === "online" ? true : false
      };
    },
    hienThiHoaDon() {
      this.modalVisible = false;
      axios
        .get("/api/hoa-don/hien-thi-phan-trang", {
          params: this.getSearchParams()
        })
        .then((response) => {
          this.hoaDons = response.data.content;
          this.totalPages = this.hoaDons.length === 0 ? 0 : (response.data.page?.totalPages || response.data.totalPages);
          this.hoaDonChiTiets = [];
        })
        .catch((error) => {
          console.error(error);
          let errorMessage = "Có lỗi xảy ra khi tải danh sách hóa đơn.";
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification.error({
            message: "Lỗi",
            description: errorMessage
          });
        });
    },
    hienThiHoaDonChiTiet(idHoaDon) {
      this.selectedHoaDon = this.hoaDons.find((hd) => hd.id === idHoaDon);
      axios
        .get("/api/hoa-don/chi-tiet", { params: { idHoaDon } })
        .then((response) => {
          this.hoaDonChiTiets = response.data;
          this.modalVisible = true;
        })
        .catch((error) => {
          console.error("Lỗi khi hiển thị chi tiết hóa đơn:", error);
        });
    },
    closeModal() {
      this.modalVisible = false;
    },
    timKiemHoaDon() {
      this.modalVisible = false;
      this.page = 0;
      this.trangThai = "";
      this.fromDate = "";
      this.toDate = "";
      this.hienThiHoaDon();
    },
    timKiemHoaDonTheoTrangThai() {
      this.modalVisible = false;
      this.page = 0;
      this.searchQuery = "";
      this.hienThiHoaDon();
    },
    timKiemDonHangTheoNgay() {
      this.modalVisible = false;
      this.page = 0;
      this.searchQuery = "";
      this.hienThiHoaDon();
    },
    diToiTrangDau() {
      this.modalVisible = false;
      this.page = 0;
      this.hienThiHoaDon();
    },
    diToiTrangTruoc() {
      if (this.page > 0) {
        this.modalVisible = false;
        this.page--;
        this.hienThiHoaDon();
      }
    },
    diToiTrangSau() {
      if (this.page < this.totalPages - 1) {
        this.modalVisible = false;
        this.page++;
        this.hienThiHoaDon();
      }
    },
    diToiTrangCuoi() {
      this.modalVisible = false;
      this.page = this.totalPages - 1;
      this.hienThiHoaDon();
    },
    // Xuất Excel: thay vì xuất ngay, mở modal lựa chọn
    openExportModal() {
      this.showExportModal = true;
    },
    closeExportModal() {
      this.showExportModal = false;
    },
    async confirmExport() {
      this.showExportModal = false;
      let invoicesForExport = [];
      try {
        if (this.exportOption === "selected") {
          if (this.selectedHoaDonIds.length === 0) {
            notification.warning({
              message: "Chưa chọn hóa đơn",
              description: "Vui lòng chọn ít nhất một hóa đơn để xuất Excel."
            });
            return;
          }
          invoicesForExport = this.hoaDons.filter((hd) =>
            this.selectedHoaDonIds.includes(hd.id)
          );
        } else if (this.exportOption === "searched") {
          // Lấy hóa đơn theo bộ lọc hiện tại (thu thập dữ liệu từ tất cả các trang)
          const initialParams = this.getSearchParams();
          initialParams.page = 0;
          const initialResponse = await axios.get("/api/hoa-don/hien-thi-phan-trang", { params: initialParams });
          let allInvoices = initialResponse.data.content;
          const totalPages = initialResponse.data.page?.totalPages || initialResponse.data.totalPages;
          const promises = [];
          for (let i = 1; i < totalPages; i++) {
            const params = { ...initialParams, page: i };
            promises.push(axios.get("/api/hoa-don/hien-thi-phan-trang", { params }));
          }
          const responses = await Promise.all(promises);
          responses.forEach(resp => {
            allInvoices = allInvoices.concat(resp.data.content);
          });
          invoicesForExport = allInvoices;
          if (invoicesForExport.length === 0) {
            notification.warning({
              message: "Không có hóa đơn",
              description: "Không tìm thấy hóa đơn nào theo tiêu chí tìm kiếm."
            });
            return;
          }
        } else if (this.exportOption === "all") {
          // Lấy tất cả hóa đơn dựa theo tab hiện tại (offline/online)
          const initialParams = {
            page: 0,
            size: this.size,
            hinhThucMuaHang: this.activeTab === "online" ? true : false
          };
          const initialResponse = await axios.get("/api/hoa-don/hien-thi-phan-trang", { params: initialParams });
          let allInvoices = initialResponse.data.content;
          const totalPages = initialResponse.data.page?.totalPages || initialResponse.data.totalPages;
          const promises = [];
          for (let i = 1; i < totalPages; i++) {
            const params = { page: i, size: this.size, hinhThucMuaHang: this.activeTab === "online" ? true : false };
            promises.push(axios.get("/api/hoa-don/hien-thi-phan-trang", { params }));
          }
          const responses = await Promise.all(promises);
          responses.forEach(resp => {
            allInvoices = allInvoices.concat(resp.data.content);
          });
          invoicesForExport = allInvoices;
          if (invoicesForExport.length === 0) {
            notification.warning({
              message: "Không có hóa đơn",
              description: "Danh sách hóa đơn trống."
            });
            return;
          }
        }

        // Tạo dữ liệu cho sheet hóa đơn
        const invoiceData = invoicesForExport.map((hd) => ({
          "Mã HD": hd.maHoaDon || "-",
          "Mã NV": hd.nhanVien?.maNhanVien || "-",
          "Tên NV": hd.nhanVien?.hoVaTen || "-",
          "Tên KH": hd.khachHang?.hoTen || "-",
          "SDT KH": hd.khachHang?.soDienThoai || "-",
          "Ngày Tạo": hd.ngayTao ? new Date(hd.ngayTao).toLocaleString("vi-VN") : "-",
          "Trạng Thái": hd.trangThai || "-"
        }));

        // Lấy chi tiết cho từng hóa đơn
        const detailPromises = invoicesForExport.map((invoice) =>
          axios
            .get("/api/hoa-don/chi-tiet", { params: { idHoaDon: invoice.id } })
            .then((response) => {
              return response.data.map((detail) => ({
                "Mã hóa đơn": invoice.maHoaDon,
                "Mã HDCT": detail.maHDCT,
                "Tên Áo Dài": detail.aoDaiChiTiet.aoDai.tenAoDai,
                "Màu sắc": detail.aoDaiChiTiet.mauSac.tenMauSac,
                "Kích thước": detail.aoDaiChiTiet.kichThuoc.ten,
                "Chất liệu": detail.aoDaiChiTiet.aoDai.chatLieu.ten,
                "Số lượng": detail.soLuong,
                "Đơn giá": detail.giaBan
              }));
            })
        );

        const detailsArray = await Promise.all(detailPromises);
        const allDetails = [].concat(...detailsArray);

        // Tạo workbook Excel sử dụng XLSX
        const wsInvoices = XLSX.utils.json_to_sheet(invoiceData);
        const wsDetails = XLSX.utils.json_to_sheet(allDetails);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, wsInvoices, "HoaDon");
        XLSX.utils.book_append_sheet(workbook, wsDetails, "ChiTietHoaDon");
        const wbout = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
        const blob = new Blob([wbout], { type: "application/octet-stream" });
        saveAs(blob, "HoaDon.xlsx");
      } catch (error) {
        console.error("Lỗi khi xuất Excel:", error);
        notification.error({
          message: "Lỗi",
          description: "Có lỗi xảy ra khi xuất file Excel."
        });
      }
    }
  }
};
</script>

<style scoped>
/* Các style chung không thay đổi */
.container {
  max-width: 1260px;
  font-family: "Roboto", sans-serif;
  color: #333;
  margin: 0 auto;
  padding: 10px;
}

/* Tabs */
.tabs {
  display: flex;
  margin-bottom: 16px;
}

.tabs button {
  padding: 10px 18px;
  margin-right: 8px;
  border: none;
  border-bottom: 2px solid transparent;
  background: #f9f9f9;
  cursor: pointer;
  transition: border-color 0.3s;
  font-size: 14px;
}

.tabs button.active {
  border-color: #007bff;
  font-weight: bold;
}

/* Bảng danh sách hóa đơn */
.table-container {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e3e3e3;
}

.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

th,
td {
  text-align: center;
  padding: 12px 8px;
  border-bottom: 1px solid #e3e3e3;
  font-size: 13px;
}

th {
  background-color: #fafafa;
  position: sticky;
  top: 0;
  z-index: 2;
  font-weight: 600;
  border-bottom: 2px solid #ddd;
}

/* Phần tìm kiếm */
.search-timkiem {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px 15px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.search-timkiem label {
  font-size: 13px;
  font-weight: 500;
  color: #555;
}

.search-timkiem input {
  padding: 8px;
  font-size: 13px;
  width: 180px;
  border: 1px solid #ccc;
  border-radius: 4px;
  transition: border-color 0.3s;
}

.search-timkiem input:focus {
  outline: none;
  border-color: #007bff;
}

.search-timkiem button {
  padding: 8px 16px;
  font-size: 14px;
  background-color: #007bff;
  color: #fff;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.search-timkiem button:hover {
  background-color: #1ace0d;
}

.search-timkiem select {
  padding: 8px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #ccc;
}

/* Nút Xuất Excel */
.xuat-excel {
  margin-left: auto;
  padding: 8px 16px;
  background-color: #28a745;
  color: #fff;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.xuat-excel:hover {
  background-color: #218838;
}

/* Phân trang */
.phanTrang {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
}

.phanTrang button,
.phanTrang-info span {
  padding: 8px 14px;
  font-size: 14px;
  border: 1px solid #007bff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.phanTrang button {
  background-color: #fff;
  color: #007bff;
}

.phanTrang button:hover,
.phanTrang-info span:hover {
  background-color: #007bff;
  color: #fff;
}

.phanTrang button:disabled {
  background-color: #f0f0f0;
  color: #b0b0b0;
  border-color: #d0d0d0;
  cursor: not-allowed;
}

.phanTrang-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #333;
}

.phanTrang-info span {
  background-color: #f9f9f9;
  color: #007bff;
}

/* Icon con mắt */
.eye-icon {
  font-size: 18px;
  cursor: pointer;
  color: #007bff;
  transition: color 0.3s;
}

.eye-icon:hover {
  color: #0056b3;
}

/* Date Search */
.date-search {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
  background: #fefefe;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.date-search>div {
  display: flex;
  align-items: center;
}

.date-search label {
  display: inline-block;
  width: 35px;
  font-weight: 500;
  color: #555;
  font-size: 13px;
}

.date-search input[type="date"] {
  padding: 6px;
  font-size: 13px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.date-search>div:nth-child(2) button {
  margin-left: 10px;
  padding: 6px 12px;
  font-size: 14px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.date-search>div:nth-child(2) button:hover {
  background-color: #0056b3;
}

/* Modal Overlay & Content */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.modal-content {
  background: #fff;
  width: 900px;
  max-width: 95%;
  max-height: 80vh;
  padding: 20px 30px;
  border-radius: 10px;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }

  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-content .close-btn {
  background: transparent;
  border: none;
  font-size: 30px;
  cursor: pointer;
  position: absolute;
  top: 10px;
  right: 15px;
  color: #888;
  transition: color 0.3s;
}

.modal-content .close-btn:hover {
  color: #333;
}

/* Modal title */
.modal-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  color: #007bff;
  margin-bottom: 20px;
}

/* Order Info */
.order-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin: 10px 0;
  padding: 12px;
  background: #f7f7f7;
  border-radius: 8px;
  font-size: 14px;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.info-row.full {
  grid-column: span 2;
}

.info-row .label {
  width: 140px;
  color: #555;
  font-weight: 500;
}

.info-row .value {
  flex: 1;
  color: #333;
}

/* Payment Summary */
.payment-summary {
  background-color: #ffffff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.payment-summary h5 {
  text-align: center;
  margin-bottom: 20px;
  color: #007bff;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 2px solid #007bff;
  padding-bottom: 10px;
}

.payment-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 16px;
  border-bottom: 1px solid #eaeaea;
}

.payment-row:last-child {
  border-bottom: none;
}

.payment-row .label {
  font-weight: bold;
  color: #333;
}

.payment-row .value {
  color: #333;
}

.payment-row.total {
  font-size: 18px;
  color: #007bff;
  font-weight: bold;
  border-top: 2px solid #007bff;
  padding-top: 10px;
  margin-top: 10px;
}

/* Product Section */
.product-section {
  margin-top: 20px;
  padding: 12px;
  background: #fafafa;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.section-title {
  margin-bottom: 12px;
  text-align: left;
  color: #007bff;
  font-size: 18px;
  font-weight: bold;
  padding-left: 10px;
}

/* Sản phẩm */
.product-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;
}

.product-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16px;
  width: 100%;
  max-width: 900px;
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-2px);
}

.product-description {
  flex: 1;
}

.product-description p {
  margin: 4px 0;
  font-size: 15px;
  line-height: 1.4;
}

.product-description strong {
  color: #555;
}

.image-wrapper {
  width: 150px;
  height: 150px;
  overflow: hidden;
  border-radius: 8px;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.price {
  color: #007bff;
  font-weight: bold;
}

/* Export Modal */
.export-option label {
  font-size: 14px;
  color: #333;
}

/* Export Modal Buttons */
.btn-export {
  padding: 8px 16px;
  font-size: 14px;
  color: #fff;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
  border-radius: 8px;
  /* Bo viền tròn */
}

/* Nút xác nhận (mặc định màu xanh) */
.btn-export.confirm {
  background-color: #007bff;
}

.btn-export.confirm:hover {
  background-color: #0056b3;
}

/* Nút hủy (mặc định màu đỏ) */
.btn-export.cancel {
  background-color: #dc3545;
  margin-left: 10px;
}

.btn-export.cancel:hover {
  background-color: #c82333;
}
</style>
