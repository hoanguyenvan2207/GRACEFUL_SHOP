<template>
  <div class="container mt-4">
    <h4>Danh sách đơn hàng online</h4>

    <div class="search-timkiem">
      <input type="text" v-model="searchQuery" placeholder="Nhập mã HD hoặc SDT KH" />
      <button @click="timKiemDonHang">Tìm kiếm</button>

      <label for="trangThai">Trạng thái: </label>
      <select v-model="trangThai" @change="timKiemDonHangTheoTrangThai">
        <option value="">Tất cả</option>
        <option value="Chưa thanh toán">Chưa thanh toán</option>
        <option value="Chờ xác nhận">Chờ xác nhận</option>
        <option value="Đã xác nhận">Đã xác nhận</option>
        <option value="Đang giao hàng">Đang giao hàng</option>
        <option value="Đã nhận hàng">Đã nhận hàng</option>
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

      <button class="xuat-excel" @click="exportExcel">
        <i class="fas fa-file-excel"></i> Xuất Excel
      </button>
    </div>

    <div class="table-container">
      <table class="table table-bordered table-hover">
        <thead>
          <tr>
            <th>#</th>
            <th>Mã hóa đơn</th>
            <th>Tên khách hàng</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ giao</th>
            <th>Ngày tạo</th>
            <th>Trạng thái</th>
            <th>Chi tiết</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(donHang, index) in donHangs" :key="donHang.id">
            <td>{{ page * size + index + 1 }}</td>
            <td>
              <input type="checkbox" :value="donHang.id" v-model="selectedOrderIds" />
              {{ donHang.maHoaDon || '-' }}
            </td>
            <td>{{ donHang.khachHang?.hoTen || '-' }}</td>
            <td>{{ donHang.khachHang?.soDienThoai || '-' }}</td>
            <td>{{ donHang.diaChiGiaoHang || '-' }}</td>
            <td>
              {{ donHang.ngayTao ? new Date(donHang.ngayTao).toLocaleString('vi-VN') : '-' }}
            </td>
            <td>{{ donHang.trangThai || '-' }}</td>
            <td>
              <i class="fas fa-eye eye-icon" @click.stop="hienThiDonHangChiTiet(donHang.id)"></i>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="phanTrang">
      <button :disabled="donHangs.length === 0 || page === 0" @click="diToiTrangDau">First</button>
      <button :disabled="donHangs.length === 0 || page === 0" @click="diToiTrangTruoc">Previ</button>
      <div class="phanTrang-info">
        <span>{{ totalPages === 0 ? 0 : page + 1 }} / {{ totalPages }}</span>
      </div>
      <button :disabled="donHangs.length === 0 || page >= totalPages - 1" @click="diToiTrangSau">Next</button>
      <button :disabled="donHangs.length === 0 || page >= totalPages - 1" @click="diToiTrangCuoi">Last</button>
    </div>

    <div class="modal-overlay" v-if="modalOpen" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeModal">&times;</button>

        <div class="steps-container" v-if="selectedDonHang">
          <ul class="steps" :class="{ dimmed: isCancelled }">
            <li v-for="(step, index) in displaySteps" :key="index"
              :class="{ active: !isCancelled && index <= currentStep }">
              <div class="step-circle">{{ index + 1 }}</div>
              <div class="step-title">{{ step }}</div>

              <div v-if="!isCancelled && index === currentStep && index < displaySteps.length - 1"
                class="next-button-container">
                <button class="rounded-pill btn btn-primary btn-sm" style="font-size: 12px;" @click="updateStatus">
                  {{ currentStep === 0 ? 'Xác nhận đơn' : 'Xác nhận' }}
                </button>
              </div>
            </li>
          </ul>
        </div>

        <div class="cancel-order-container text-center" v-if="selectedDonHang && canCancel">
          <button class="btn btn-danger rounded-pill" style="font-size: 12px;" @click="cancelOrder">
            Hủy đơn
          </button>
        </div>

        <div v-if="selectedDonHang" class="mt-4">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <span class="modal-title-detail">MÃ ĐƠN HÀNG: {{ selectedDonHang.maHoaDon }}</span>
            <span class="text-uppercase" style="font-weight: 600;">{{ selectedDonHang.trangThai }}</span>
          </div>
          <div v-if="donHangChiTiets.length > 0" class="product-section">
            <div class="product-cards">
              <div class="product-card" v-for="ct in donHangChiTiets" :key="ct.id">
                <div class="image-wrapper">
                  <img :src="ct.aoDaiChiTiet.anhUrl" class="product-image" alt="Product Image" />
                </div>
                <div class="product-description">
                  <p><strong>Mã sản phẩm : {{ ct.aoDaiChiTiet.maAoDaiChiTiet }}</strong></p>
                  <p>
                    <strong>Tên sản phẩm :</strong>
                    {{ ct.aoDaiChiTiet.aoDai.tenAoDai }}
                  </p>
                  <p>
                    <strong>Phân loại :</strong>
                    {{ ct.aoDaiChiTiet.aoDai.taAo.ten }} -
                    Màu {{ ct.aoDaiChiTiet.mauSac.tenMauSac }} -
                    Size {{ ct.aoDaiChiTiet.kichThuoc.ten }} -
                    Chất liệu {{ ct.aoDaiChiTiet.aoDai.chatLieu.ten }}
                  </p>
                  <p><strong>Số lượng:</strong> {{ ct.soLuong }} </p>
                  <p v-if="ct.aoDaiChiTiet.giaGoc !== ct.giaBan"><strong>Giá gốc: </strong><del class="text-danger">{{
                    formatCurrency(ct.aoDaiChiTiet.giaGoc)
                      }}</del></p>
                  <p><strong>Đơn giá: </strong> {{ formatCurrency(ct.giaBan) }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="payment-summary">
            <h5 class="section-title">Tóm tắt thanh toán</h5>
            <div class="payment-row">
              <span class="label">Hình Thức Mua Hàng:</span>
              <span class="value">
                {{ selectedDonHang.hinhThucMuaHang != null ? (selectedDonHang.hinhThucMuaHang ? 'Online' : 'Tại quầy') :
                  '-' }}
              </span>
            </div>
            <div class="payment-row">
              <span class="label">Tổng tiền hàng:</span>
              <span class="value">{{ formatCurrency(tinhTongTienHang) }}</span>
            </div>
            <div class="payment-row">
              <span class="label">Phí Ship:</span>
              <span class="value">{{ formatCurrency(selectedDonHang.phiGiaoHang) }}</span>
            </div>
            <div class="payment-row">
              <span class="label">Tiền Trước Giảm:</span>
              <span class="value">{{ formatCurrency(selectedDonHang.tienTruocGiam) }}</span>
            </div>
            <div class="payment-row">
              <span class="label">Mã Giảm Giá:</span>
              <span class="value">{{ selectedDonHang.giamGia?.maGiamGia || '-' }}</span>
            </div>
            <div class="payment-row">
              <span class="label">Giảm giá được:</span>
              <span class="value">{{ formatCurrency(tinhGiamGia) }}</span>
            </div>
            <div class="payment-row">
              <span class="label">Tổng Tiền:</span>
              <span class="value">{{ formatCurrency(selectedDonHang.tongTien) }}</span>
            </div>
            <div class="payment-row full">
              <span class="label">Ghi Chú:</span>
              <span class="value">{{ selectedDonHang.ghiChu ? selectedDonHang.ghiChu : 'Không có' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal-overlay" v-if="showCancelReasonModal" @click="closeCancelReasonModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeCancelReasonModal">&times;</button>
        <h5>Nhập lý do hủy đơn</h5>
        <div class="reason-modal">
          <textarea v-model="cancellationReason" rows="3" placeholder="Nhập lý do hủy đơn..."
            class="reason-textarea"></textarea>
          <div class="modal-btn-group">
            <button class="btn-confirm" @click="confirmCancelOrder">Xác nhận</button>
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
            Xuất excel các đơn hàng được chọn
          </label>
          <label>
            <input type="radio" v-model="exportOption" value="searched" />
            Xuất excel các đơn hàng được tìm kiếm
          </label>
          <label>
            <input type="radio" v-model="exportOption" value="all" />
            Xuất excel tất cả các đơn hàng
          </label>
        </div>
        <div style="text-align: right; margin-top: 20px;">
          <button class="btn-next" @click="confirmExport">Xác nhận</button>
          <button class="btn-next" style="background-color: #dc3545; margin-left: 10px;"
            @click="closeExportModal">Hủy</button>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
import axios from "axios";
import { notification, Modal } from "ant-design-vue";

export default {
  data() {
    return {
      donHangs: [],
      donHangChiTiets: [],
      searchQuery: "",
      trangThai: "",
      fromDate: "",
      toDate: "",
      page: 0,
      size: 7,
      totalPages: 0,
      modalOpen: false,
      selectedDonHang: null,
      selectedOrderIds: [],
      steps: [
        "Chờ xác nhận",
        "Đã xác nhận",
        "Đang giao hàng",
        "Đã nhận hàng",
        "Đã hủy đơn"
      ],
      preCancelStatus: "",
      showCancelReasonModal: false,
      cancellationReason: "",
      showExportModal: false,
      exportOption: "selected"
    };
  },
  computed: {
    currentStep() {
      if (!this.selectedDonHang) return 0;
      const index = this.steps.indexOf(this.selectedDonHang.trangThai);
      return index >= 0 ? index : 0;
    },
    displaySteps() {
      if (this.isCancelled) {
        return [this.preCancelStatus, "Đã hủy đơn"];
      }
      // bình thường thì hiển thị tới "Đã nhận hàng"
      return this.steps.slice(0, this.steps.length - 1);
    },
    isCancelled() {
      return this.selectedDonHang && this.selectedDonHang.trangThai === "Đã hủy đơn";
    },
    canCancel() {
      return (
        this.selectedDonHang &&
        this.selectedDonHang.trangThai !== "Đã hủy đơn" &&
        this.selectedDonHang.trangThai !== "Đã nhận hàng"
      );
    },
    isDateCleared() {
      return (
        (!this.fromDate || this.fromDate.trim() === "") &&
        (!this.toDate || this.toDate.trim() === "")
      );
    },
    tinhTongTienHang() {
      if (!this.donHangChiTiets || this.donHangChiTiets.length === 0) return 0;
      return this.donHangChiTiets.reduce((acc, item) => {
        return acc + (item.soLuong * item.giaBan);
      }, 0);
    },
    tinhGiamGia() {
      if (!this.selectedDonHang || !this.selectedDonHang.giamGia) return 0;

      const discount = this.selectedDonHang.giamGia;
      const tienTruocGiam = Number(this.selectedDonHang.tienTruocGiam) || 0;

      let giamGiaDuoc = 0;
      if (discount.loaiGiamGia === 0) {
        // Giảm theo tiền
        giamGiaDuoc = discount.giaTriGiam;
      } else if (discount.loaiGiamGia === 1) {
        // Giảm theo phần trăm
        giamGiaDuoc = tienTruocGiam * (discount.giaTriGiam / 100);
        // So sánh với mức giảm tối đa nếu có
        if (discount.toiDaGiamGia && giamGiaDuoc > discount.toiDaGiamGia) {
          giamGiaDuoc = discount.toiDaGiamGia;
        }
      }
      return giamGiaDuoc;
    }
  },
  created() {
    this.hienThiDonHang();
  },
  watch: {
    isDateCleared(newVal) {
      if (newVal) {
        this.hienThiDonHang();
      }
    }
  },
  methods: {
    async showConfirm(message) {
      return new Promise((resolve) => {
        Modal.confirm({
          title: "Xác nhận",
          content: message,
          onOk() {
            resolve(true);
          },
          onCancel() {
            resolve(false);
          }
        });
      });
    },

    async updateStatus() {
      if (!this.selectedDonHang) return;
      const currentIndex = this.currentStep;
      if (currentIndex >= this.steps.length - 1) {
        notification.warning({
          message: "Thông báo",
          description: "Đơn hàng đã đạt trạng thái cuối cùng"
        });
        return;
      }
      const newStatus = this.steps[currentIndex + 1];
      const confirmed = await this.showConfirm(`Bạn có chắc chắn muốn chuyển trạng thái thành "${newStatus}"?`);
      if (!confirmed) return;
      try {
        const response = await axios.patch(`/api/quan-ly-don-hang/cap-nhat-trang-thai/${this.selectedDonHang.id}`, null, {
          params: { trangThai: newStatus }
        });
        notification.success({
          message: "Thành công",
          description: response.data
        });
        this.selectedDonHang.trangThai = newStatus;
        this.refreshDonHangs();
      } catch (error) {
        const errorMsg = error.response && error.response.data
          ? error.response.data
          : "Cập nhật trạng thái thất bại";
        notification.error({
          message: "Lỗi",
          description: errorMsg
        });
      }
    },

    cancelOrder() {
      if (!this.selectedDonHang) return;
      this.preCancelStatus = this.selectedDonHang.trangThai;
      this.cancellationReason = "";
      this.showCancelReasonModal = true;
    },

    closeCancelReasonModal() {
      this.showCancelReasonModal = false;
    },

    async confirmCancelOrder() {
      if (!this.cancellationReason.trim()) {
        notification.warning({
          message: "Chú ý",
          description: "Vui lòng nhập lý do hủy đơn."
        });
        return;
      }
      const confirmed = await this.showConfirm("Bạn có chắc chắn muốn hủy đơn hàng này?");
      if (!confirmed) return;
      try {
        const response = await axios.patch(
          "/api/quan-ly-don-hang/huy-don",
          [this.selectedDonHang.id],
          { params: { lyDoHuy: this.cancellationReason } }
        );
        notification.success({
          message: "Thành công",
          description: response.data
        });
        this.selectedDonHang.trangThai = "Đã hủy đơn";
        this.refreshDonHangs();
        this.closeCancelReasonModal();
        this.closeModal();
      } catch (error) {
        const errorMsg = error.response && error.response.data
          ? error.response.data
          : "Hủy đơn thất bại";
        notification.error({
          message: "Lỗi",
          description: errorMsg
        });
      }
    },

    refreshDonHangs() {
      axios
        .get("/api/quan-ly-don-hang/hien-thi-phan-trang", {
          params: {
            page: this.page,
            size: this.size,
            keyword: this.searchQuery,
            trangThai: this.trangThai,
            fromDate: this.fromDate,
            toDate: this.toDate,
          },
        })
        .then((response) => {
          this.donHangs = response.data.content;
          this.totalPages = response.data.page?.totalPages || response.data.totalPages;
        })
        .catch((error) => console.error(error));
    },

    hienThiDonHangChiTiet(idDonHang) {
      this.selectedDonHang = this.donHangs.find((dh) => dh.id === idDonHang);
      axios
        .get("/api/quan-ly-don-hang/chi-tiet", {
          params: { idHoaDon: idDonHang },
        })
        .then((response) => {
          this.donHangChiTiets = response.data;
          this.modalOpen = true;
        })
        .catch((error) =>
          console.error("Lỗi khi hiển thị chi tiết đơn hàng:", error)
        );
    },

    closeModal() {
      this.modalOpen = false;
    },

    timKiemDonHang() {
      this.modalOpen = false;
      this.page = 0;
      this.fromDate = "";
      this.toDate = "";
      this.trangThai = "";
      this.selectedOrderIds = [];
      this.hienThiDonHang();
    },

    timKiemDonHangTheoTrangThai() {
      this.modalOpen = false;
      this.page = 0;
      this.searchQuery = "";
      this.selectedOrderIds = [];
      this.hienThiDonHang();
    },

    timKiemDonHangTheoNgay() {
      this.modalOpen = false;
      this.page = 0;
      this.searchQuery = "";
      this.selectedOrderIds = [];
      this.hienThiDonHang();
    },

    hienThiDonHang() {
      this.modalOpen = false;
      const params = {
        page: this.page,
        size: this.size,
        keyword: this.searchQuery,
        trangThai: this.trangThai,
        fromDate: this.fromDate && this.fromDate.trim() !== "" ? this.fromDate : null,
        toDate: this.toDate && this.toDate.trim() !== "" ? this.toDate : null,
      };
      axios
        .get("/api/quan-ly-don-hang/hien-thi-phan-trang", { params })
        .then((response) => {
          this.donHangs = response.data.content;
          this.totalPages = this.donHangs.length === 0 ? 0 : (response.data.page?.totalPages || response.data.totalPages);
          this.donHangChiTiets = [];
        })
        .catch((error) => {
          console.error(error);
          let errorMessage = "Có lỗi xảy ra khi tải danh sách đơn hàng.";
          if (error.response && error.response.data) {
            errorMessage = error.response.data;
          }
          notification.error({
            message: "Lỗi",
            description: errorMessage,
          });
        });
    },

    diToiTrangDau() {
      this.modalOpen = false;
      this.page = 0;
      this.hienThiDonHang();
    },
    diToiTrangTruoc() {
      if (this.page > 0) {
        this.modalOpen = false;
        this.page--;
        this.hienThiDonHang();
      }
    },
    diToiTrangSau() {
      if (this.page < this.totalPages - 1) {
        this.modalOpen = false;
        this.page++;
        this.hienThiDonHang();
      }
    },
    diToiTrangCuoi() {
      this.modalOpen = false;
      this.page = this.totalPages - 1;
      this.hienThiDonHang();
    },
    formatCurrency(value) {
      if (value == null || isNaN(value)) return "-";
      return Number(value).toLocaleString("vi-VN") + " vnđ";
    },
    // Hàm xuất Excel mở modal lựa chọn
    exportExcel() {
      this.showExportModal = true;
    },
    closeExportModal() {
      this.showExportModal = false;
    },
    async confirmExport() {
      // Đóng modal khi xác nhận
      this.showExportModal = false;

      let ordersForExport = [];

      try {
        if (this.exportOption === "selected") {
          if (this.selectedOrderIds.length === 0) {
            notification.warning({
              message: "Chưa chọn đơn hàng",
              description: "Vui lòng chọn ít nhất một đơn hàng để xuất Excel."
            });
            return;
          }
          // Lấy đơn hàng được chọn trên trang hiện tại
          ordersForExport = this.donHangs.filter((dh) =>
            this.selectedOrderIds.includes(dh.id)
          );
        } else if (this.exportOption === "searched") {
          // Lấy đơn hàng theo bộ lọc tìm kiếm (phân trang)
          const initialParams = {
            keyword: this.searchQuery,
            trangThai: this.trangThai,
            fromDate: this.fromDate && this.fromDate.trim() !== "" ? this.fromDate : null,
            toDate: this.toDate && this.toDate.trim() !== "" ? this.toDate : null,
            page: 0,
            size: this.size
          };
          const initialResponse = await axios.get("/api/quan-ly-don-hang/hien-thi-phan-trang", { params: initialParams });
          let allOrders = initialResponse.data.content;
          const totalPages = initialResponse.data.page?.totalPages || initialResponse.data.totalPages;
          const promises = [];
          for (let i = 1; i < totalPages; i++) {
            const params = { ...initialParams, page: i };
            promises.push(axios.get("/api/quan-ly-don-hang/hien-thi-phan-trang", { params }));
          }
          const responses = await Promise.all(promises);
          responses.forEach(resp => {
            allOrders = allOrders.concat(resp.data.content);
          });
          ordersForExport = allOrders;
          if (ordersForExport.length === 0) {
            notification.warning({
              message: "Không có đơn hàng",
              description: "Không tìm thấy đơn hàng nào theo tiêu chí tìm kiếm."
            });
            return;
          }
        } else if (this.exportOption === "all") {
          // Lấy tất cả đơn hàng (không áp dụng bộ lọc)
          const initialParams = {
            page: 0,
            size: this.size
          };
          const initialResponse = await axios.get("/api/quan-ly-don-hang/hien-thi-phan-trang", { params: initialParams });
          let allOrders = initialResponse.data.content;
          const totalPages = initialResponse.data.page?.totalPages || initialResponse.data.totalPages;
          const promises = [];
          for (let i = 1; i < totalPages; i++) {
            const params = { page: i, size: this.size };
            promises.push(axios.get("/api/quan-ly-don-hang/hien-thi-phan-trang", { params }));
          }
          const responses = await Promise.all(promises);
          responses.forEach(resp => {
            allOrders = allOrders.concat(resp.data.content);
          });
          ordersForExport = allOrders;
          if (ordersForExport.length === 0) {
            notification.warning({
              message: "Không có đơn hàng",
              description: "Danh sách đơn hàng trống."
            });
            return;
          }
        }

        // Tạo dữ liệu sheet cho đơn hàng
        const orderData = ordersForExport.map((dh) => ({
          "Mã HD": dh.maHoaDon || "-",
          "Tên KH": dh.khachHang?.hoTen || "-",
          "SDT KH": dh.khachHang?.soDienThoai || "-",
          "Địa chỉ giao": dh.diaChiGiaoHang || "-",
          "Ngày tạo": dh.ngayTao ? new Date(dh.ngayTao).toLocaleString("vi-VN") : "-",
          "Trạng thái": dh.trangThai || "-"
        }));

        // Lấy chi tiết cho từng đơn hàng
        const detailPromises = ordersForExport.map((order) =>
          axios
            .get("/api/quan-ly-don-hang/chi-tiet", { params: { idHoaDon: order.id } })
            .then((response) => {
              return response.data.map((detail) => ({
                "Mã HD": order.maHoaDon,
                "Mã SPCT": detail.aoDaiChiTiet.maAoDaiChiTiet,
                "Tên sản phẩm": detail.aoDaiChiTiet.aoDai.tenAoDai,
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
        const wsOrders = XLSX.utils.json_to_sheet(orderData);
        const wsDetails = XLSX.utils.json_to_sheet(allDetails);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, wsOrders, "DonHang");
        XLSX.utils.book_append_sheet(workbook, wsDetails, "ChiTietDonHang");
        const wbout = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
        const blob = new Blob([wbout], { type: "application/octet-stream" });
        saveAs(blob, "DonHang.xlsx");

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
/* Container chung */
.container {
  max-width: 1260px;
  font-family: 'Roboto', sans-serif;
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

/* Bảng danh sách đơn hàng */
.table-container {
  max-height: 385px;
  overflow-y: auto;
  overflow-x: auto;
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
  margin-right: 5px;
  font-weight: 500;
  color: #555;
}

.search-timkiem input {
  padding: 8px;
  font-size: 13px;
  width: 160px;
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

/* Nút Xuất Excel & Hủy đơn */
.xuat-excel,
.huy-don {
  margin-left: auto;
  padding: 8px 16px;
  background-color: #28a745;
  color: #fff;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.xuat-excel:hover,
.huy-don:hover {
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
  width: 1100px;
  max-width: 90%;
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

.modal-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  color: #007bff;
  margin-bottom: 20px;
}

/* Step Indicator */
.steps-container {
  margin-bottom: 20px;
  padding: 0 10px;
}

.steps {
  display: flex;
  justify-content: space-between;
  padding: 0;
  margin: 0;
  list-style: none;
}

.steps li {
  text-align: center;
  position: relative;
  flex: 1;
}

.steps li::after {
  content: "";
  position: absolute;
  top: 15px;
  right: -50%;
  width: 100%;
  height: 2px;
  background: #e9ecef;
  z-index: -1;
}

.steps li:last-child::after {
  display: none;
}

.step-circle {
  width: 30px;
  height: 30px;
  line-height: 30px;
  border-radius: 50%;
  background: #e9ecef;
  margin: 0 auto;
  color: #6c757d;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.steps li.active .step-circle {
  background: #007bff;
  color: #fff;
}

.step-title {
  margin-top: 4px;
  font-size: 12px;
  color: #6c757d;
}

.steps li.active .step-title {
  color: #007bff;
}

.next-button-container {
  text-align: center;
  margin-top: 20px;
}

.btn-next:hover {
  background-color: #0056b3;
}

/* Order Info & Payment Summary */
.order-info {
  display: none;
}

/* Payment Summary: sử dụng nền trắng và giao diện tinh tế */
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
  max-width: 1100px;
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
  margin-left: 20px;
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
  width: 100px;
  height: auto;
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

.modal-title-detail {
  text-align: center;
  align-items: center;
  font-weight: 600;
  color: #333;
}

.dimmed {
  opacity: 0.5;
  pointer-events: none;
}

/* Áp dụng chung cho cả 2 nút */
.modal-content button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

/* Modal Reason Cancellation */
.reason-modal {
  padding: 20px;
  text-align: center;
}

.reason-textarea {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  resize: vertical;
  font-size: 0.95rem;
}

.modal-btn-group {
  display: flex;
  justify-content: flex-end;
}

.btn-confirm {
  background: #27ae60;
  border: none;
  color: #fff;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.btn-confirm:hover {
  background: #1e8449;
}
</style>
