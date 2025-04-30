<template>
  <div class="top-bar-sapo">
    <div class="search-sapo">
      <input type="text" v-model="keyWord" placeholder="Tìm kiếm áo dài..." class="product-search-input"
        @focus="onFocusAoDai" @blur="onBlurAoDai" />
      <button class="btn btn-primary btn-qr-sapo" @click="openQrScanner">
        <i class="fas fa-qrcode"></i> QR
      </button>

      <div v-if="danhSachAoDai.length > 0" class="product-dropdown" :class="{ 'max-h-60': danhSachAoDai.length > 2 }">
        <div v-for="aoDaiCT in danhSachAoDai" :key="aoDaiCT.id" class="product-item" @click="themVaoGio(aoDaiCT)">
          <img :src="aoDaiCT.anhUrl || 'default-placeholder.png'" alt="Ảnh áo dài" class="product-image" />
          <div class="product-details">
            <div class="product-title">
              {{ aoDaiCT.maAoDaiChiTiet }} - {{ aoDaiCT.aoDai.tenAoDai }} ({{ aoDaiCT.aoDai.taAo.ten }})
            </div>
            <div class="product-info">
              <span>Màu: {{ aoDaiCT.mauSac.tenMauSac }} -</span>
              <span>Kích thước: {{ aoDaiCT.kichThuoc.ten }} -</span>
              <span>Chất liệu: {{ aoDaiCT.aoDai.chatLieu.ten }}</span>
            </div>
            <div class="product-quantity">
              Số lượng còn: {{ aoDaiCT.soLuong }}
            </div>
            <div class="product-price">
              <small v-if="aoDaiCT.giaGoc !== aoDaiCT.giaBan">
                <del class="text-danger">
                  Giá gốc:
                  {{ aoDaiCT.giaGoc.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) }}
                </del>
              </small>
              <p v-if="aoDaiCT.giaGoc !== aoDaiCT.giaBan">
                Giá khuyến mãi:
                {{ aoDaiCT.giaBan.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) }}
              </p>
              <p v-else>
                Giá bán:
                {{ aoDaiCT.giaGoc.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) }}
              </p>
            </div>

          </div>
          <button class="add-to-cart-btn">
            <i class="fas fa-shopping-cart"></i> Thêm
          </button>
        </div>
      </div>
      <div v-else-if="keyWord.trim() !== ''" class="product-dropdown">
        <div class="no-results">
          Không có kết quả
        </div>
      </div>

      <div class="invoice-sapo">
        <select v-model="activeTab" @change="chonHoaDon(activeTab)" class="invoice-dropdown">
          <option disabled value="">Chọn hóa đơn</option>
          <option v-for="hoaDon in danhSachHoaDon" :key="hoaDon.id" :value="hoaDon.id">
            Đơn {{ hoaDon.stt }} - {{ hoaDon.maHoaDon }}
          </option>
        </select>

        <button @click="taoHoaDonMoi" class="add-invoice-btn">
          <i class="fa-solid fa-plus"></i>
        </button>

        <button @click="deleteInvoice(activeTab)" class="delete-invoice-btn">
          <i class="fas fa-times"></i>
        </button>
      </div>
    </div>

  </div>

  <div class="main-content">
    <div class="cacDs">
      <div class="spcthd">
        <div class="table-spcthd-wrapper">
          <table class="table table-bordered table-hover">
            <thead v-if="danhSachHoaDonChiTiet.length > 0">
              <tr>
                <th style="width: 20px;">#</th>
                <th>Mã SPCT</th>
                <th>Tên áo dài</th>
                <th>Màu sắc</th>
                <th>Kích thước</th>
                <th>Số lượng</th>
                <th>Giá bán</th>
                <th>Giá khuyến mãi</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(spctthd, index) in danhSachHoaDonChiTiet" :key="spctthd.id">
                <td>{{ index + 1 }}</td>
                <td>{{ spctthd.aoDaiChiTiet.maAoDaiChiTiet }}</td>
                <td>{{ spctthd.aoDaiChiTiet.aoDai.tenAoDai }}</td>
                <td>{{ spctthd.aoDaiChiTiet.mauSac.tenMauSac }}</td>
                <td>{{ spctthd.aoDaiChiTiet.kichThuoc.ten }}</td>
                <td>
                  <input type="number" :value="spctthd.soLuong" min="1"
                    @blur="capNhatSoLuong(spctthd, $event.target.value)" />
                </td>
                <td>
                  <del v-if="spctthd.aoDaiChiTiet.giaGoc !== spctthd.giaBan" class="text-danger">
                    {{ formatCurrency(spctthd.aoDaiChiTiet.giaGoc) }}
                  </del>
                  <span v-else>
                    {{ formatCurrency(spctthd.aoDaiChiTiet.giaGoc) }}
                  </span>
                </td>
                <td>
                  <span v-if="spctthd.aoDaiChiTiet.giaGoc === spctthd.giaBan">
                    Không có
                  </span>
                  <span v-else>
                    {{ formatCurrency(spctthd.giaBan) }}
                  </span>
                </td>
                <td>
                  <button class="btn-delete" @click="xoaSanPham(spctthd)">
                    <i class="fas fa-trash-alt"></i>
                  </button>
                </td>
              </tr>
              <tr v-if="danhSachHoaDonChiTiet.length === 0"
                style="height: 312px; font-size: 20px; font-family: Montserrat;">
                <td colspan="9" style="vertical-align: middle; text-align: center;">
                  <img
                    src="https://png.pngtree.com/png-clipart/20241022/original/pngtree-empty-cardboard-box-3d-heap-column-container-photo-png-image_16457318.png"
                    alt="Chưa có sản phẩm" style="width: 250px; height: auto; display: block; margin: 0 auto 10px;" />
                  Chưa có sản phẩm
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="showQrScanner" class="qr-scanner-modal">
        <div class="scanner-container">
          <div id="loading-message" v-if="loadingMessage">{{ loadingMessage }}</div>
          <div v-if="error" class="error-message">{{ error }}</div>
          <canvas id="qr-canvas"></canvas>
          <button class="btn btn-secondary close-button" @click="closeQrScanner">
            X
          </button>
        </div>
      </div>
    </div>

    <div class="banhang">
      <form @submit.prevent="themKhachHang" class="customer-form">
        <div class="form-group">
          <label>Tên khách hàng:</label>
          <input v-model="tenKhachHang" type="text" />
          <span v-if="errors.tenKhachHang" class="error">
            {{ errors.tenKhachHang }}
          </span>
        </div>
        <div class="form-group">
          <label>Số điện thoại:</label>
          <input v-model="soDienThoai" type="text" @keypress.enter="timKhachHangTheoSDT" />
          <span v-if="errors.soDienThoai" class="error">
            {{ errors.soDienThoai }}
          </span>
        </div>
        <div class="form-group">
          <button type="button" @click="chonKhachLe" class="btn-customer">
            <i class="fas fa-user"></i> <span>Khách lẻ</span>
          </button>
          <button type="submit" class="btn-add-customer">
            <i class="fas fa-user-plus"></i> <span>Thêm</span>
          </button>
        </div>
      </form>
      <div class="form-banhang">
        <form>
          <div class="form-group">
            <label>Giảm giá:</label>
            <button type="button" class="btn-discount" @click="openDiscountModal">
              <span v-if="selectedVoucher">
                {{ selectedVoucher.maGiamGia }} -
                <template v-if="selectedVoucher.loaiGiamGia === 0">
                  Giảm: {{ formatCurrency(selectedVoucher.giaTriGiam) }}
                </template>
                <template v-else>
                  Giảm: {{ selectedVoucher.giaTriGiam }}%
                </template>
                <span class="clear-voucher" @click.stop="clearVoucher">×</span>
              </span>
              <span v-else>Chọn voucher giảm giá</span>
            </button>
          </div>
          <div class="form-group payment-method-group" style="max-width: 350px; width: 100%;">
            <label style="margin-bottom: 0;">Phương thức:</label>
            <div class="payment-method" style="width: 100%; justify-content: space-around;">
              <label>
                <input type="radio" name="phuong-thuc-thanh-toan" value="1" v-model="selectedPaymentMethod" />
                Chuyển khoản
              </label>
              <label>
                <input type="radio" name="phuong-thuc-thanh-toan" value="0" v-model="selectedPaymentMethod" />
                Tiền mặt
              </label>
            </div>
          </div>
          <div class="form-group">
            <label>Tên nhân viên:</label>
            <input type="text" :value="hoVaTen" readonly />
          </div>
          <div class="form-group">
            <label>Tiền trước giảm:</label>
            <input type="text" :value="formatCurrency(tienTruocKhiGiam)" readonly />
          </div>
          <div class="form-group">
            <label>Voucher giảm:</label>
            <input type="text" :value="displayVoucherDiscount" readonly />
          </div>
          <div class="form-group">
            <label>Tổng tiền:</label>
            <input type="text" :value="formatCurrency(finalTotal)" readonly />
          </div>
          <div class="btn-group">
            <button type="button" @click="thanhToan" class="btn-payment">
              <i class="fas fa-money-check-alt"></i>
              <span> Thanh toán hóa đơn</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>

  <div v-if="showDiscountModal" class="modal-overlay">
    <div class="modal-content">
      <h3>Chọn Voucher Giảm Giá</h3>
      <ul class="voucher-list">
        <li v-for="voucher in danhSachGiamGia" :key="voucher.id"
          :class="{ disabled: tienTruocKhiGiam < Number(voucher.giaTriToiThieu) }" @click="selectDiscount(voucher)">
          <div class="voucher-item">
            <span class="voucher-code">{{ voucher.maGiamGia }}</span>
            <span class="voucher-value">
              -
              <template v-if="voucher.loaiGiamGia === 0">
                {{ formatCurrency(voucher.giaTriGiam) }}
              </template>
              <template v-else>
                {{ voucher.giaTriGiam }}%
              </template>
            </span>
          </div>
          <div class="voucher-condition">
            (Tối thiểu: {{ formatCurrency(voucher.giaTriToiThieu) }})
          </div>
          <div v-if="voucher.loaiGiamGia === 1" class="voucher-condition">
            (Giảm tối đa: {{ formatCurrency(voucher.toiDaGiamGia) }})
          </div>
        </li>
      </ul>
      <div class="modal-actions">
        <button @click="closeDiscountModal" class="btn-cancel">Hủy</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import jsQR from "jsqr";
import { notification, Modal, Spin, message } from "ant-design-vue";
import { h } from "vue";

export default {
  data() {
    return {
      tenKhachHang: "",
      soDienThoai: "",
      errors: {},
      keyWord: "",
      danhSachAoDai: [],
      danhSachHoaDon: [],
      danhSachHoaDonChiTiet: [],
      danhSachGiamGia: [],
      hoVaTen: "",
      activeTab: "",
      selectedGiamGiaId: "",
      allHoaDon: [],
      selectedPaymentMethod: "1",
      originalHoaDonChiTiet: "",
      originalCustomer: { ten: "", sdt: "" },
      showQrScanner: false,
      showDiscountModal: false,
      camera: "auto",
      error: "",
      video: null,
      canvasElement: null,
      canvas: null,
      scanning: false,
      loadingMessage: "Đang khởi tạo camera...",
      scanningInProgress: false,
    };
  },
  computed: {
    selectedVoucher() {
      return (
        this.danhSachGiamGia.find(
          (v) => v.id === Number(this.selectedGiamGiaId)
        ) || null
      );
    },
    displayVoucherDiscount() {
      if (!this.selectedVoucher) return "0 đ";
      if (this.tienTruocKhiGiam < Number(this.selectedVoucher.giaTriToiThieu))
        return "0 đ";
      if (this.selectedVoucher.loaiGiamGia === 0) {
        return this.formatCurrency(this.selectedVoucher.giaTriGiam);
      } else if (this.selectedVoucher.loaiGiamGia === 1) {
        const discountCalculated =
          this.tienTruocKhiGiam *
          (Number(this.selectedVoucher.giaTriGiam) / 100);
        const discountAmount = Math.min(
          discountCalculated,
          Number(this.selectedVoucher.toiDaGiamGia)
        );
        return this.formatCurrency(discountAmount);
      }
      return "0 đ";
    },
    tienTruocKhiGiam() {
      return this.danhSachHoaDonChiTiet.reduce((acc, item) => {
        const soLuong = Number(item.soLuong) || 0;
        const giaBan = Number(item.giaBan) || 0;
        return acc + soLuong * giaBan;
      }, 0);
    },
    discountAmount() {
      if (!this.selectedGiamGiaId) return 0;
      const voucher = this.danhSachGiamGia.find(
        (g) => g.id === Number(this.selectedGiamGiaId)
      );
      if (!voucher) return 0;
      if (this.tienTruocKhiGiam < Number(voucher.giaTriToiThieu)) return 0;
      if (voucher.loaiGiamGia === 0) {
        return Number(voucher.giaTriGiam);
      } else if (voucher.loaiGiamGia === 1) {
        const calculated =
          this.tienTruocKhiGiam *
          (Number(voucher.giaTriGiam) / 100);
        return Math.min(calculated, Number(voucher.toiDaGiamGia));
      }
      return 0;
    },
    finalTotal() {
      return this.tienTruocKhiGiam - this.discountAmount;
    },
  },
  watch: {
    async keyWord(newKeyword) {
      if (newKeyword.trim() !== "") {
        try {
          const response = await axios.get(
            "/api/ban-hang/ao-dai-chi-tiet/hien-thi",
            { params: { keyWord: newKeyword.trim() } }
          );
          this.danhSachAoDai = response.data.filter(
            (item) => item.soLuong > 0
          );

        } catch (error) {
          console.error("Lỗi khi lấy dữ liệu áo dài:", error);
        }
      }
    },
    // Watcher cho tổng tiền để kiểm tra điều kiện áp dụng voucher
    tienTruocKhiGiam(newVal) {
      if (this.selectedVoucher && newVal < Number(this.selectedVoucher.giaTriToiThieu)) {
        this.clearVoucher();
      }
    },
  },
  methods: {
    async onFocusAoDai() {
      if (!this.keyWord.trim()) {
        try {
          const response = await axios.get(
            "/api/ban-hang/ao-dai-chi-tiet/hien-thi"
          );
          this.danhSachAoDai = response.data.filter(
            (item) => item.soLuong > 0
          );
        } catch (error) {
          console.error("Lỗi khi lấy danh sách áo dài:", error);
        }
      }
    },
    onBlurAoDai() {
      if (!this.keyWord.trim()) {
        setTimeout(() => {
          this.danhSachAoDai = [];
        }, 200);
      }
    },
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
          },
        });
      });
    },
    openQrScanner() {
      this.showQrScanner = true;
      this.$nextTick(() => {
        this.startScanner();
      });
    },
    closeQrScanner() {
      this.showQrScanner = false;
      if (this.video && this.video.srcObject) {
        const tracks = this.video.srcObject.getTracks();
        tracks.forEach((track) => track.stop());
      }
      this.scanning = false;
    },
    async startScanner() {
      try {
        this.video = document.createElement("video");
        this.canvasElement = document.getElementById("qr-canvas");
        this.canvas = this.canvasElement.getContext("2d");
        const stream = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: "environment" },
        });
        this.video.srcObject = stream;
        this.video.setAttribute("playsinline", true);
        this.video.play();
        this.scanning = true;
        requestAnimationFrame(this.tick);
      } catch (error) {
        console.error("Error starting scanner:", error);
        this.error =
          "Không thể khởi tạo camera. Vui lòng kiểm tra quyền truy cập.";
      }
    },
    drawLine(begin, end, color) {
      this.canvas.beginPath();
      this.canvas.moveTo(begin.x, begin.y);
      this.canvas.lineTo(end.x, end.y);
      this.canvas.lineWidth = 4;
      this.canvas.strokeStyle = color;
      this.canvas.stroke();
    },
    tick() {
      if (!this.scanning) return;
      if (this.video.readyState === this.video.HAVE_ENOUGH_DATA) {
        this.loadingMessage = "";
        this.canvasElement.height = this.video.videoHeight;
        this.canvasElement.width = this.video.videoWidth;
        this.canvas.drawImage(
          this.video,
          0,
          0,
          this.canvasElement.width,
          this.canvasElement.height
        );
        const imageData = this.canvas.getImageData(
          0,
          0,
          this.canvasElement.width,
          this.canvasElement.height
        );
        const code = jsQR(imageData.data, imageData.width, imageData.height, {
          inversionAttempts: "dontInvert",
        });
        if (code) {
          this.drawLine(
            code.location.topLeftCorner,
            code.location.topRightCorner,
            "#FF3B58"
          );
          this.drawLine(
            code.location.topRightCorner,
            code.location.bottomRightCorner,
            "#FF3B58"
          );
          this.drawLine(
            code.location.bottomRightCorner,
            code.location.bottomLeftCorner,
            "#FF3B58"
          );
          this.drawLine(
            code.location.bottomLeftCorner,
            code.location.topLeftCorner,
            "#FF3B58"
          );
          console.log("Found QR code:", code.data);
          this.handleQrCode(code.data);
          return;
        }
      }
      requestAnimationFrame(this.tick);
    },
    async handleQrCode(qrData) {
      try {
        const productCode = qrData.trim().toUpperCase();
        console.log("Processing QR code:", productCode);
        const response = await axios.get(
          "/api/ban-hang/ao-dai-chi-tiet/hien-thi",
          {
            params: { keyWord: productCode },
          }
        );
        if (response.data && response.data.length > 0) {
          const matchedProduct = response.data.find(
            (product) => product.maAoDaiChiTiet === productCode
          );
          if (matchedProduct) {
            const exists = this.danhSachHoaDonChiTiet.some(
              (item) =>
                item.aoDaiChiTiet &&
                item.aoDaiChiTiet.maAoDaiChiTiet === productCode
            );
            if (exists) {
              const confirmed = await this.showConfirm(
                "Sản phẩm này đã có trong hóa đơn, bạn có muốn thêm lần nữa không?"
              );
              if (!confirmed) {
                this.scanning = true;
                requestAnimationFrame(this.tick);
                return;
              }
            }
            await this.themVaoGio(matchedProduct);
            this.scanning = true;
            requestAnimationFrame(this.tick);
          } else {
            notification.error({
              message: "Lỗi",
              description: `Không tìm thấy sản phẩm chính xác với mã: ${productCode}`,
            });
            this.scanning = true;
            requestAnimationFrame(this.tick);
          }
        } else {
          notification.error({
            message: "Lỗi",
            description: `Không tìm thấy sản phẩm với mã: ${productCode}`,
          });
          this.scanning = true;
          requestAnimationFrame(this.tick);
        }
      } catch (error) {
        console.error("Lỗi xử lý mã QR:", error);
        notification.error({
          message: "Lỗi",
          description: "Có lỗi xảy ra khi xử lý mã QR!",
        });
        this.scanning = true;
        requestAnimationFrame(this.tick);
      }
    },
    formatCurrency(value) {
      return Number(value).toLocaleString("vi-VN", {
        style: "currency",
        currency: "VND",
      });
    },
    async chonHoaDon(newId) {
      this.activeTab = newId;
      await this.fetchHoaDonChuaThanhToan();
      await this.getHoaDonChiTiet(newId);
      this.selectedGiamGiaId = "";
      const selectedInvoice = this.danhSachHoaDon.find((hd) => hd.id === newId);
      if (selectedInvoice && selectedInvoice.khachHang && selectedInvoice.khachHang.hoTen) {
        this.tenKhachHang = selectedInvoice.khachHang.hoTen;
        this.soDienThoai = selectedInvoice.khachHang.soDienThoai;
      } else {
        this.tenKhachHang = "";
        this.soDienThoai = "";
      }
    },
    async deleteInvoice(invoiceId) {
      if (!invoiceId) {
        notification.error({
          message: "Thất bại!",
          description: "Vui lòng chọn hóa đơn muốn xóa!",
        });
        return;
      }
      const confirmed = await this.showConfirm(
        "Bạn có muốn xóa hóa đơn này không?"
      );
      if (!confirmed) return;
      try {
        const response = await axios.delete(
          "/api/ban-hang/xoa-hoa-don/" + invoiceId
        );
        notification.success({
          message: "Thành công!",
          description: response.data,
        });
        await this.fetchHoaDonChuaThanhToan();
        await this.fetchDanhSachAoDai();
        this.selectedGiamGiaId = "";
        if (this.activeTab === invoiceId) {
          this.activeTab = "";
          this.danhSachHoaDonChiTiet = [];
          this.tenKhachHang = "";
          this.soDienThoai = "";
        }
      } catch (error) {
        console.error("Lỗi xóa hóa đơn:", error);
        if (error.response && error.response.data) {
          notification.error({ message: "Lỗi", description: error.response.data });
        }
      }
    },
    async updateInvoiceCustomer() {
      if (!this.activeTab) {
        notification.error({
          message: "Lỗi",
          description: "Chưa chọn hóa đơn.",
        });
        return;
      }
      try {
        const payload = {
          id: this.activeTab,
          khachHang: { soDienThoai: this.soDienThoai },
        };
        const response = await axios.post(
          "/api/ban-hang/cap-nhat-khach-hang-hoa-don",
          payload
        );
        notification.success({
          message: "Thành công",
          description: response.data,
        });
      } catch (error) {
        notification.error({
          message: "Lỗi",
          description:
            error.response?.data || "Cập nhật khách hàng thất bại!",
        });
      }
    },
    async fetchDanhSachAoDai() {
      const keyword = this.keyWord.trim();
      if (!keyword) return;
      try {
        const response = await axios.get(
          "/api/ban-hang/ao-dai-chi-tiet/hien-thi",
          {
            params: { keyWord: keyword },
          }
        );
        this.danhSachAoDai = response.data.filter(
          (item) => item.soLuong > 0
        );
      } catch (error) {
        console.error("Lỗi khi lấy danh sách áo dài:", error);
        alert(
          error.response && error.response.data
            ? error.response.data
            : "Lỗi khi lấy danh sách áo dài."
        );
      }
    },
    async themVaoGio(aoDaiCT) {
      if (!this.activeTab) {
        notification.error({
          message: "Lỗi",
          description: "Chưa chọn hóa đơn.",
        });
        return;
      }
      const requestData = {
        hoaDon: { id: this.activeTab },
        aoDaiChiTiet: { id: aoDaiCT.id },
      };
      try {
        const response = await axios.post(
          "/api/ban-hang/them-san-pham-vao-hoa-don",
          requestData
        );
        notification.success({
          message: "Thành công!",
          description: response.data,
        });
        await this.getHoaDonChiTiet(this.activeTab);
        if (this.keyWord.trim() !== "") {
          await this.fetchDanhSachAoDai();
        }
      } catch (error) {
        console.error("Lỗi thêm sản phẩm vào hóa đơn:", error);
        notification.error({
          message: "Lỗi",
          description:
            error.response && error.response.data
              ? error.response.data
              : "Thêm sản phẩm thất bại!",
        });
      }
    },
    async xoaSanPham(hdct) {
      const confirmed = await this.showConfirm(
        "Bạn có muốn xóa sản phẩm này khỏi hóa đơn không?"
      );
      if (!confirmed) return;
      try {
        const response = await axios.delete(
          `/api/ban-hang/xoa-san-pham-vao-hoa-don/${hdct.id}`
        );
        notification.success({
          message: "Thành công!",
          description: response.data,
        });
        await this.getHoaDonChiTiet(this.activeTab);
        await this.fetchDanhSachAoDai();
      } catch (error) {
        if (error.response && error.response.data) {
          notification.error({ message: "Lỗi", description: error.response.data });
        }
      }
    },
    async getHoaDonChiTiet(idHoaDon) {
      try {
        const response = await axios.get(
          "/api/ban-hang/hoa-don-chi-tiet",
          {
            params: { idHoaDon },
          }
        );
        this.danhSachHoaDonChiTiet = response.data;
        const hoaDon = this.danhSachHoaDon.find((hd) => hd.id === idHoaDon);
        if (hoaDon && hoaDon.khachHang) {
          this.tenKhachHang = hoaDon.khachHang.hoTen;
          this.soDienThoai = hoaDon.khachHang.soDienThoai;
        }
        this.originalHoaDonChiTiet = JSON.stringify(
          this.danhSachHoaDonChiTiet
        );
      } catch (error) {
      }
    },
    async xoaTatCaSanPhamMoiTrongHoaDon() {
      let originalList = [];
      try {
        originalList = JSON.parse(this.originalHoaDonChiTiet || "[]");
      } catch (e) {
        originalList = [];
      }
      const originalIds = originalList.map((item) => item.id);
      const unsavedHDCT = this.danhSachHoaDonChiTiet.filter(
        (item) => !originalIds.includes(item.id)
      );
      if (unsavedHDCT && unsavedHDCT.length > 0) {
        for (let hdct of unsavedHDCT) {
          try {
            await axios.delete(
              `/api/ban-hang/xoa-san-pham-vao-hoa-don/${hdct.id}`
            );
          } catch (err) {
            console.error("Lỗi khi xóa HDCT với id:", hdct.id, err);
          }
        }
        await this.getHoaDonChiTiet(this.activeTab);
      }
    },
    async processThanhToan() {
      const hoaDonThanhToan = {
        id: this.activeTab,
        phuongThucThanhToan: this.selectedPaymentMethod === "1",
        giamGia: this.selectedGiamGiaId ? { id: Number(this.selectedGiamGiaId) } : null,
        khachHang: { soDienThoai: this.soDienThoai },
        nhanVien: { id: Number(sessionStorage.getItem("idNhanVien")) },
      };

      // Hiển thị modal ngay lập tức với QR tạm thời
      const qrModal = this.showQRCodeModal(
        "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjEwMCIgeT0iMTAwIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMTYiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiNjY2MiPkxvYWRpbmcgUVIgQ29kZTwvdGV4dD48L3N2Zz4=",
        this.tongTien,
        "Đang tạo..."
      );

      try {
        const response = await axios.post(
          "/api/ban-hang/thanh-toan",
          hoaDonThanhToan
        );

        if (response.data.qrCode) {
          // Cập nhật modal với QR thật và dữ liệu mới
          this.updateQRModal(
            qrModal,
            response.data.qrCode,
            response.data.tongTien,
            response.data.txnRef
          );
        } else {
          qrModal.destroy();
          notification.success({
            message: "Thành công!",
            description: response.data,
          });
          this.resetForm();
        }
      } catch (error) {
        qrModal.destroy();
        if (error.response?.data) {
          notification.error({ message: "Lỗi", description: error.response.data });
        }
      }
    },

    showQRCodeModal(qrCodeBase64, amount, txnRef) {
      // Sử dụng biến tham chiếu để cập nhật giá trị
      const modalData = {
        qrCodeBase64,
        amount,
        txnRef,
        confirmedAmount: amount // Lưu trữ số tiền để sử dụng khi xác nhận
      };

      const modal = Modal.confirm({
        title: "Thanh toán chuyển khoản",
        width: 370,
        content: () => this.renderQRModalContent(modalData, true),
        okText: "Đã thanh toán",
        cancelText: "Hủy",
        onOk: async () => {
          try {
            await axios.post("/api/ban-hang/xac-nhan-thanh-toan", {
              id: this.activeTab
            });
            notification.success({
              message: "Thành công!",
              description: `Thanh toán chuyển khoản thành công. Tổng tiền: ${modalData.confirmedAmount?.toLocaleString("vi-VN") || '0'
                } VND`,
            });
            this.resetForm();
          } catch (error) {
            notification.error({
              message: "Lỗi",
              description: error.response?.data || "Không thể xác nhận thanh toán",
            });
            return Promise.reject();
          }
        },
      });

      // Lưu reference để cập nhật sau
      modal._modalData = modalData;
      return modal;
    },

    updateQRModal(modal, qrCodeBase64, amount, txnRef) {
      // Cập nhật cả dữ liệu modal và số tiền xác nhận
      modal._modalData.qrCodeBase64 = qrCodeBase64;
      modal._modalData.amount = amount;
      modal._modalData.txnRef = txnRef;
      modal._modalData.confirmedAmount = amount; // Quan trọng: cập nhật số tiền cho xác nhận

      modal.update({
        content: () => this.renderQRModalContent(modal._modalData, false)
      });
    },

    renderQRModalContent(modalData, isLoading) {
      const { qrCodeBase64, amount, txnRef } = modalData;
      return h(
        "div",
        {
          style: {
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            paddingRight: "30px",
          },
        },
        [
          h(
            "p",
            { style: { margin: "8px 0 0" } },
            isLoading ? "Đang tạo mã QR..." : "Vui lòng quét mã QR để thanh toán."
          ),
          h("div", {
            style: {
              position: "relative",
              width: "350px",
              height: "350px",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginBottom: "16px",
            }
          }, [
            h("img", {
              src: qrCodeBase64,
              alt: "QR Code",
              style: {
                width: "100%",
                height: "100%",
                objectFit: "contain",
                opacity: isLoading ? 0.7 : 1,
                filter: isLoading ? "blur(2px)" : "none",
                transition: "all 0.3s ease",
              },
            }),
            isLoading && h("div", {
              style: {
                position: "absolute",
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }
            }, [
              h(Spin, { size: "large" })
            ])
          ]),
          h("p", { style: { margin: "8px 0" } }, `Mã giao dịch: ${txnRef || 'Đang tạo...'}`),
        ]
      );
    },

    resetForm() {
      this.fetchHoaDonChuaThanhToan();
      this.fetchDanhSachAoDai();
      this.selectedGiamGiaId = "";
      this.selectedPaymentMethod = "1";
      this.danhSachHoaDonChiTiet = [];
      this.tenKhachHang = "";
      this.soDienThoai = "";
      this.activeTab = "";
    },
    async thanhToan() {
      if (!this.activeTab) {
        notification.error({
          message: "Lỗi",
          description: "Vui lòng chọn hóa đơn cần thanh toán.",
        });
        return;
      }

      if (this.selectedPaymentMethod === "1") {
        // For bank transfer, directly call process method
        // The QR code will be shown in the response handling
        await this.processThanhToan();
      } else if (this.selectedPaymentMethod === "0") {
        // Cash payment confirmation
        Modal.confirm({
          title: "Xác nhận thanh toán tiền mặt",
          content: "Bạn có chắc chắn muốn thanh toán tiền mặt không?",
          okText: "Xác nhận",
          cancelText: "Hủy",
          onOk: async () => {
            await this.processThanhToan();
          },
        });
      }
    },
    async fetchDanhSachGiamGia() {
      try {
        const response = await axios.get(
          "/api/ban-hang/hien-thi-giam-gia"
        );
        this.danhSachGiamGia = response.data;
      } catch (error) {
        console.error("Lỗi khi lấy danh sách giảm giá:", error);
      }
    },
    // Các hàm liên quan đến khách hàng
    async timKhachHangTheoSDT(event) {
      event.preventDefault();
      if (!this.soDienThoai.trim()) {
        this.errors.soDienThoai = "Vui lòng nhập số điện thoại.";
        return;
      }
      try {
        const response = await axios.get(
          `/api/ban-hang/tim-khach-hang?soDienThoai=${this.soDienThoai}`
        );
        this.tenKhachHang = response.data.hoTen;
        this.errors = {};
        this.updateInvoiceCustomer();
      } catch (error) {
        this.tenKhachHang = "";
        this.errors.soDienThoai =
          error.response?.data || "Số điện thoại này chưa có tài khoản.";
      }
    },
    async themKhachHang() {
      this.errors = {};
      if (!this.tenKhachHang.trim()) {
        this.errors.tenKhachHang = "Tên khách hàng không được để trống.";
      } else if (!/^[\p{L} ]+$/u.test(this.tenKhachHang)) {
        this.errors.tenKhachHang =
          "Tên khách hàng không hợp lệ. Không được chứa số.";
      }
      if (!this.soDienThoai.trim()) {
        this.errors.soDienThoai = "Số điện thoại không được để trống.";
      } else if (!/^0\d{9}$/.test(this.soDienThoai)) {
        this.errors.soDienThoai =
          "Số điện thoại phải có 10 chữ số và bắt đầu bằng số 0.";
      }
      if (Object.keys(this.errors).length > 0) return;
      try {
        const response = await axios.post(
          "/api/ban-hang/them-khach-hang",
          {
            hoTen: this.tenKhachHang,
            soDienThoai: this.soDienThoai,
          }
        );
        notification.success({
          message: "Thành công!",
          description: response.data,
        });
        this.updateInvoiceCustomer();
      } catch (error) {
        if (error.response && error.response.data) {
          this.errors.soDienThoai = error.response.data;
        }
      }
    },
    async fetchHoaDonChuaThanhToan() {
      try {
        const response = await axios.get(
          "/api/ban-hang/ds-hoa-don-chua-thanh-toan"
        );
        this.allHoaDon = response.data.map((hoaDon, index) => {
          return { ...hoaDon, stt: index + 1 };
        });
        this.danhSachHoaDon = this.allHoaDon;
      } catch (error) {
        console.error("Lỗi khi lấy danh sách hóa đơn chưa thanh toán:", error);
      }
    },
    chonKhachLe() {
      this.tenKhachHang = "Khách lẻ";
      this.soDienThoai = "0000000000";
      this.errors = {};
      this.updateInvoiceCustomer();
    },
    async taoHoaDonMoi() {
      try {
        const response = await axios.post(
          "/api/ban-hang/tao-hoa-don-moi"
        );
        const newInvoice = response.data;
        await this.fetchHoaDonChuaThanhToan();
        this.activeTab = newInvoice.id;
        this.danhSachHoaDonChiTiet = [];
        this.selectedGiamGiaId = "";
        this.selectedPaymentMethod = "1";
        this.tenKhachHang = "";
        this.soDienThoai = "";
        notification.success({
          message: "Thành công!",
          description: "Tạo hóa đơn mới thành công!",
        });
      } catch (error) {
        if (error.response && error.response.data) {
          notification.error({ message: "Lỗi", description: error.response.data });
        }
        console.error("Lỗi tạo hóa đơn mới:", error);
      }
    },
    async capNhatSoLuong(hdct, newValue) {
      const newQuantity = parseInt(newValue);
      if (isNaN(newQuantity) || newQuantity < 1) {
        notification.error({
          message: "Lỗi",
          description: "Số lượng phải là số nguyên lớn hơn 0.",
        });
        await this.getHoaDonChiTiet(this.activeTab);
        return;
      }
      if (newQuantity === hdct.soLuong) return;
      const updatedHDCT = { id: hdct.id, soLuong: newQuantity };
      try {
        const response = await axios.put(
          "/api/ban-hang/cap-nhat-so-luong",
          updatedHDCT
        );
        notification.success({
          message: "Thành công!",
          description:
            response.data.message || "Cập nhật số lượng thành công.",
        });
        await this.getHoaDonChiTiet(this.activeTab);
        if (this.keyWord.trim() !== "") {
          await this.fetchDanhSachAoDai();
        }
      } catch (error) {
        notification.error({
          message: "Lỗi",
          description:
            error.response && error.response.data
              ? error.response.data.error
              : "Cập nhật số lượng thất bại!",
        });
        await this.getHoaDonChiTiet(this.activeTab);
        if (this.keyWord.trim() !== "") {
          await this.fetchDanhSachAoDai();
        }
      }
    },
    // Các hàm xử lý cho modal giảm giá
    openDiscountModal() {
      this.showDiscountModal = true;
    },
    closeDiscountModal() {
      this.showDiscountModal = false;
    },
    selectDiscount(voucher) {
      // Kiểm tra điều kiện tối thiểu áp dụng
      if (this.tienTruocKhiGiam < Number(voucher.giaTriToiThieu)) {
        return; // Không cho chọn nếu không đủ điều kiện
      }
      this.selectedGiamGiaId = voucher.id;
      this.closeDiscountModal();
    },
    clearVoucher() {
      this.selectedGiamGiaId = "";
    },
  },
  mounted() {
    this.fetchHoaDonChuaThanhToan();
    this.fetchDanhSachGiamGia();
    this.hoVaTen = sessionStorage.getItem("hoVaTen");
  },
  watch: {
    activeTab() {
      this.getHoaDonChiTiet(this.activeTab);
    },
  }
};
</script>

<style scoped>
/* Thanh trên cùng chiếm full width, nền vàng. */
.top-bar-sapo {
  width: 99%;
  background-color: rgb(59, 242, 255);
  display: flex;
  padding: 10px 20px;
  box-sizing: border-box;
}

/* Phần tìm kiếm áo dài (search-sapo) */
.search-sapo {
  position: relative;
  display: flex;
  align-items: center;
  gap: 5px;
  width: 850px;
}

.product-search-input {
  width: 400px;
  padding: 10px 15px;
  border: 1px solid #ccc;
  border-radius: 25px;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.product-search-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
  outline: none;
}

.btn-qr-sapo {
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 9px 10px;
  font-size: 13px;
  width: 70px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-qr-sapo:hover {
  background-color: #0056b3;
}

/* Khu vực chọn hóa đơn (invoice-sapo) */
.invoice-sapo {
  display: flex;
  align-items: center;
  gap: 5px;
}

.invoice-dropdown {
  margin-left: 95px;
  padding: 10px 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  background-color: #fff;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.invoice-dropdown:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
  outline: none;
}

.add-invoice-btn,
.delete-invoice-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.add-invoice-btn {
  background-color: #4CAF50;
}

.add-invoice-btn:hover {
  background-color: #43A047;
  transform: translateY(-2px);
}

.delete-invoice-btn {
  background-color: #F44336;
}

.delete-invoice-btn:hover {
  background-color: #E53935;
  transform: translateY(-2px);
}

/* Khu vực chính (2 cột) */
.main-content {
  display: flex;
  align-items: flex-start;
  margin-top: 20px;
  padding: 0 20px;
  box-sizing: border-box;
  font-family: "Roboto", sans-serif;
  color: #333;
}

/* CỘT TRÁI: Hóa đơn & danh sách sản phẩm */
.cacDs {
  width: 850px;
}

.spcthd {
  max-width: 810px;
  margin-top: 10px;
  font-size: 12px;
}

.table-spcthd-wrapper {
  height: 330px;
  overflow-y: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}

.table-spcthd-wrapper th {
  background-color: #fdfefe;
  position: sticky;
  top: 0;
  z-index: 1;
}

/* Dropdown tìm kiếm sản phẩm */
.product-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  width: 70%;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-height: 340px;
  overflow-y: auto;
  z-index: 9999;
  padding: 5px 0;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s;
}

.product-item:last-child {
  border-bottom: none;
}

.product-item:hover {
  background-color: #f9f9f9;
}

.product-image {
  width: 90px;
  height: auto;
  margin-right: 10px;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-title {
  font-weight: bold;
  margin-bottom: 5px;
  font-size: 16px;
}

.product-info {
  font-size: 13px;
  color: #555;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.product-quantity {
  font-size: 13px;
  color: #555;
  margin-top: 5px;
}

.product-price {
  font-size: 14px;
  color: #007bff;
  margin-top: 5px;
  font-weight: bold;
}

.add-to-cart-btn {
  background-color: #28a745;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s;
}

.add-to-cart-btn:hover {
  background-color: #218838;
}

/* CỘT PHẢI: Thông tin khách hàng & Thanh toán */
.banhang {
  width: 400px;
  background: #fff;
  border-radius: 10px;
  margin-top: 10px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid #e6e6e6;
  font-family: "Roboto", sans-serif;
  color: #333;
  padding: 20px 15px;
}

.customer-form .form-group,
.form-banhang .form-group {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 15px;
}

.customer-form label,
.form-banhang label {
  font-weight: 600;
  margin: 0 10px 0 0;
  font-size: 14px;
  color: #555;
  width: 120px;
}

.customer-form input[type="text"],
.form-banhang input[type="text"] {
  flex: 1;
  padding: 6px 8px;
  border: none;
  border-bottom: 1px solid #ccc;
  font-size: 14px;
  line-height: 1.2;
  transition: border-color 0.3s;
}

.customer-form input[type="text"]:focus,
.form-banhang input[type="text"]:focus {
  border-bottom: 1px solid #007bff;
  outline: none;
}

.customer-form select,
.form-banhang select {
  flex: 1;
  padding: 6px 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  line-height: 1.2;
  transition: border-color 0.3s;
}

.customer-form select:focus,
.form-banhang select:focus {
  border-color: #007bff;
  outline: none;
}

.customer-form {
  margin-bottom: 20px;
}

.btn-customer {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  margin-left: 140px;
  background: linear-gradient(90deg, #007bff, #0056b3);
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.3s, transform 0.3s;
}

.btn-customer:hover {
  background: linear-gradient(90deg, #007bff, #0056b3);
  transform: scale(1.03);
}

.btn-add-customer {
  display: block;
  margin: 10px auto;
  padding: 6px 17px;
  background: linear-gradient(90deg, #007bff, #0056b3);
  color: #fff;
  border: none;
  border-radius: 5px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

.btn-add-customer:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.btn-group {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.btn-payment {
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  width: 180px;
  font-size: 14px;
  margin-left: 90px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background-color 0.3s, transform 0.3s;
}

.btn-payment:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.form-group .error {
  flex-basis: 100%;
  margin-left: 130px;
  margin-top: 5px;
  font-size: 12px;
  color: #d9534f;
}

/* Modal Quét QR */
.qr-scanner-modal {
  position: fixed;
  top: 10%;
  left: 50%;
  transform: translateX(-50%);
  background: #fff;
  z-index: 1000;
  padding: 10px;
  border: 1px solid #ccc;
}

.scanner-container {
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

#qr-canvas {
  width: 300px;
  height: 300px;
}

/* Input số lượng */
input[type="number"] {
  width: 70px !important;
  min-width: 10px !important;
  max-width: 60px !important;
  padding: 3px !important;
  border: 1px solid #ccc !important;
  border-radius: 4px !important;
  text-align: center !important;
  box-sizing: border-box !important;
  display: inline-block !important;
  flex: none !important;
  -webkit-appearance: none !important;
  -moz-appearance: textfield !important;
}

.btn-delete {
  margin-left: 30px;
  background-color: #F44336;
  border: none;
  border-radius: 50%;
  width: 25px;
  color: #fff;
  font-size: 15px;
}

.btn-delete:hover {
  background-color: #D32F2F;
  transform: scale(1.1);
}

.payment-method {
  padding: 12px 0;
  margin-left: 80px;
}

/* Style cho nút chọn giảm giá mới */
.btn-discount {
  background-color: #fff;
  border: 1px solid #ddd;
  padding: 10px 85px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  width: 100%;
  text-align: left;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s, border-color 0.3s;
  position: relative;
  font-size: 14px;
}

.btn-discount:hover {
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
  border-color: #007bff;
}

.clear-voucher {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-weight: bold;
  color: #ff5722;
  cursor: pointer;
  font-size: 26px;
}

/* Modal chọn giảm giá */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: #fff;
  padding: 20px 25px;
  width: 400px;
  border-radius: 10px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.modal-content h3 {
  margin-top: 0;
  font-size: 20px;
  margin-bottom: 15px;
}

.voucher-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 250px;
  overflow-y: auto;
}

.voucher-list li {
  padding: 12px 15px;
  border-bottom: 1px solid #f1f1f1;
  cursor: pointer;
  transition: background 0.3s;
  text-align: left;
}

.voucher-list li:hover {
  background: #f9f9f9;
}

.voucher-list li.disabled {
  opacity: 0.5;
  pointer-events: none;
}

.voucher-item {
  font-weight: 600;
  font-size: 15px;
}

.voucher-code {
  color: #007bff;
}

.voucher-value {
  color: #ff5722;
  margin-left: 5px;
}

.voucher-condition {
  font-size: 13px;
  color: #777;
  margin-top: 5px;
}

.modal-actions {
  text-align: right;
  margin-top: 15px;
}

.btn-cancel {
  background-color: #F44336;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-cancel:hover {
  background-color: #D32F2F;
}

.product-dropdown .no-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  color: #757575;
  font-size: 14px;
}

.product-dropdown .no-results::before {
  content: "🔍";
  font-size: 32px;
  margin-bottom: 8px;
}

.product-dropdown .no-results span {
  display: block;
  margin-top: 4px;
  color: #999;
  font-size: 12px;
}
</style>
