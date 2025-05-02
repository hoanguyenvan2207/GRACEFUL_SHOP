<template>
  <a-layout>
    <!-- Tổng quan -->
    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h4>Tổng quan</h4>
      </div>
      <div v-if="doanhThuTrongNgay" class="card-body">
        <div class="row">
          <div class="col-md-3 mb-3">
            <div class="p-2 border border-primary-subtle border-2 rounded">
              <h6>Doanh Thu Hôm Nay</h6>
              <h4>{{ formatCurrency(doanhThuTrongNgay.tongDoanhThu) }}</h4>
            </div>
          </div>
          <div class="col-md-3 mb-3">
            <div class="p-2 border border-success-subtle border-2 rounded">
              <h6>Doanh Thu Trực Tiếp Hôm Nay</h6>
              <h4>{{ formatCurrency(doanhThuTrongNgay.doanhThuOffline) }}</h4>
            </div>
          </div>
          <div class="col-md-3 mb-3">
            <div class="p-2 border border-info-subtle border-2 rounded">
              <h6>Doanh Thu Trực Tuyến Hôm Nay</h6>
              <h4>{{ formatCurrency(doanhThuTrongNgay.doanhThuOnline) }}</h4>
            </div>
          </div>
          <div class="col-md-3 mb-3">
            <div class="p-2 border border-secondary-subtle border-2 rounded">
              <h6>Tổng Sản Phẩm</h6>
              <h4>{{ doanhThuTrongNgay.tongSanPham }}</h4>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-success-subtle border-2 rounded">
              <h6>Tổng Số Hóa Đơn</h6>
              <h4>{{ doanhThuTrongNgay.soHoaDon }}</h4>
            </div>
          </div>
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-primary-subtle border-2 rounded">
              <h6>Hoá Đơn Trực Tiếp</h6>
              <h4>{{ doanhThuTrongNgay.tongDonOffline }}</h4>
            </div>
          </div>
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-info-subtle border-2 rounded">
              <h6>Đơn Hàng Trực Tuyến</h6>
              <h4>{{ doanhThuTrongNgay.tongDonOnline }}</h4>
            </div>
          </div>
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-warning-subtle border-2 rounded">
              <h6>Đơn Chưa Xác Nhận</h6>
              <h4>{{ doanhThuTrongNgay.donChuaXacNhan }}</h4>
            </div>
          </div>
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-success-subtle border-2 rounded">
              <h6>Đơn Giao Thành Công</h6>
              <h4>{{ doanhThuTrongNgay.donDaThanhToan }}</h4>
            </div>
          </div>
          <div class="col-md-2 mb-3">
            <div class="p-2 border border-danger-subtle border-2 rounded">
              <h6>Đơn Đã Huỷ</h6>
              <h4>{{ doanhThuTrongNgay.donHuy }}</h4>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Doanh Thu Theo Ngày -->
    <div class="card mb-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h4>Doanh Thu Theo Ngày</h4>
        <div class="d-flex gap-2 align-items-center">
          Từ:
          <input type="date" class="form-control" @change="fetchDateRangeRevenue" style="width: 200px"
            v-model="startDate" />
          Đến:
          <input type="date" class="form-control" @change="fetchDateRangeRevenue" style="width: 200px"
            v-model="endDate" />
        </div>
      </div>
      <div class="card-body">
        <div v-if="dateRangeRevenue.length" class="row">
          <!-- các ô tổng giữ nguyên -->
        </div>
        <div v-else class="text-center py-4">
          <p class="text-muted">Không có dữ liệu doanh thu cho ngày đã chọn</p>
        </div>
        <div v-if="dateRangeRevenue.length" class="chart-container">
          <canvas ref="dateRangeRevenueCanvas" height="100"></canvas>
        </div>
      </div>
    </div>

    <!-- Doanh Thu Theo Tháng -->
    <div class="card mt-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h4>Doanh Thu Theo Tháng</h4>
        <div class="d-flex gap-2">
          <select class="form-select" style="width: 130px" v-model="selectedMonth">
            <option v-for="month in 12" :key="month" :value="month">Tháng {{ month }}</option>
          </select>
          <select class="form-select" style="width: 130px" v-model="selectedYear">
            <option v-for="year in years" :key="year" :value="year">Năm {{ year }}</option>
          </select>
        </div>
      </div>
      <div class="card-body">
        <div v-if="monthlyRevenue.length" class="chart-container">
          <canvas ref="monthlyRevenueCanvas" height="100"></canvas>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-muted">Không có dữ liệu doanh thu cho tháng đã chọn</p>
        </div>
      </div>
    </div>

    <!-- Thống kê doanh thu theo năm -->
    <div class="card mt-4">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h4 class="m-0">Thống kê doanh thu theo năm</h4>
        <div class="d-flex align-items-center gap-2">
          <select class="form-select" style="width: 120px" v-model="selectedYearForYearly">
            <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
          </select>
          <div v-if="isYearlyLoading" class="spinner-border spinner-border-sm text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
        </div>
      </div>
      <div class="card-body">
        <div v-if="yearlyRevenue.length" class="chart-container">
          <canvas ref="yearlyRevenueCanvas" style="width: 100%; height: 350px;"></canvas>
        </div>
        <div v-if="!yearlyRevenue.length && !isYearlyLoading" class="no-data-overlay">
          Không có dữ liệu
        </div>
      </div>
    </div>

    <!-- Tỉ Lệ Loại Áo Dài Bán Chạy -->
    <div class="card mt-4">
      <div class="card-header">
        <h4>Tỉ Lệ Loại Áo Dài Bán Chạy</h4>
      </div>
      <div class="card-body chart-container">
        <div v-if="loaiAoDaiData.length === 0" class="text-center py-4">
          <p class="text-muted">Không có dữ liệu về loại áo dài</p>
        </div>
        <div v-else class="chart-container">
          <canvas ref="pieChartCanvas" class="mx-auto" style="max-width: 300px; max-height: 300px;"></canvas>
        </div>
      </div>
    </div>

    <!-- Top 5 Áo Dài Bán Chạy -->
    <div class="card mt-4">
      <div class="card-header">
        <h4>Top 5 Áo Dài Bán Chạy</h4>
      </div>
      <div class="card-body chart-container">
        <div v-if="topProducts.length > 0" class="chart-container">
          <canvas ref="topProductsCanvas" style="width: 100%; height: 350px;"></canvas>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-muted">Không có dữ liệu về top sản phẩm</p>
        </div>
      </div>
      <div v-if="selectedProduct" class="mt-4">
        <!-- chi tiết sản phẩm giữ nguyên -->
      </div>
    </div>
  </a-layout>
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "ThongKe",
  data() {
    return {
      doanhThuTrongNgay: null,
      dailyRevenue: null,
      dailyRevenueChart: null,
      selectedMonth: new Date().getMonth() + 1,
      selectedYear: new Date().getFullYear(),
      monthlyRevenue: [],
      monthlyRevenueChart: null,
      years: [2024, 2025, 2026],
      selectedYearForYearly: new Date().getFullYear(),
      yearlyRevenue: [],
      yearlyRevenueChart: null,
      availableYears: Array.from({ length: 3 }, (_, i) => new Date().getFullYear() - i),
      isYearlyLoading: false,
      dateRangeRevenue: [],
      dateRangeRevenueChart: null,
      startDate: new Date(new Date().setDate(new Date().getDate() - 7)).toISOString().split("T")[0],
      endDate: new Date().toISOString().split("T")[0],
      isFetching: false,
      loaiAoDaiData: [],
      pieChart: null,
      topProducts: [],
      topProductsChart: null,
      selectedProduct: null,
      // Theo dõi trạng thái của biểu đồ
      chartsInitialized: {
        monthly: false,
        yearly: false,
        dateRange: false,
        pie: false,
        topProducts: false
      }
    };
  },
  computed: {
    sortedDateRangeRevenue() {
      return [...this.dateRangeRevenue].sort((a, b) => new Date(a.ngay) - new Date(b.ngay));
    },
    totalRevenue() {
      return this.dateRangeRevenue.reduce((tot, item) => ({
        tongDoanhThu: tot.tongDoanhThu + (item.tongDoanhThu || 0),
        tongHoaDon: tot.tongHoaDon + (item.tongHoaDon || 0),
        tongSanPhamBanDuoc: tot.tongSanPhamBanDuoc + (item.tongSanPhamBanDuoc || 0),
      }), { tongDoanhThu: 0, tongHoaDon: 0, tongSanPhamBanDuoc: 0 });
    },
  },
  async mounted() {
    this.$nextTick(() => {
      this.loadAllData();
    });
  },
  beforeUnmount() {
    window.location.reload();
    this.destroyAllCharts();
  },
  methods: {
    // Hủy tất cả các biểu đồ trước khi component bị hủy
    destroyAllCharts() {
      if (this.dailyRevenueChart) {
        this.dailyRevenueChart.destroy();
        this.dailyRevenueChart = null;
      }
      if (this.monthlyRevenueChart) {
        this.monthlyRevenueChart.destroy();
        this.monthlyRevenueChart = null;
      }
      if (this.yearlyRevenueChart) {
        this.yearlyRevenueChart.destroy();
        this.yearlyRevenueChart = null;
      }
      if (this.dateRangeRevenueChart) {
        this.dateRangeRevenueChart.destroy();
        this.dateRangeRevenueChart = null;
      }
      if (this.pieChart) {
        this.pieChart.destroy();
        this.pieChart = null;
      }
      if (this.topProductsChart) {
        this.topProductsChart.destroy();
        this.topProductsChart = null;
      }

      // Reset trạng thái biểu đồ
      this.chartsInitialized = {
        monthly: false,
        yearly: false,
        dateRange: false,
        pie: false,
        topProducts: false
      };
    },

    // Tải tất cả dữ liệu
    async loadAllData() {
      try {
        await Promise.all([
          this.fetchMonthlyRevenue(),
          this.fetchYearlyRevenue(),
          this.fetchDateRangeRevenue(),
          this.fetchLoaiAoDaiData(),
          this.fetchTopProducts(),
          this.fetchDoanhThuHomNay()
        ]);
      } catch (error) {
        console.error("Lỗi khi tải dữ liệu:", error);
      }
    },

    formatCurrency(val) {
      return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(val || 0);
    },
    formatDate(str) {
      return new Date(str).toLocaleDateString("vi-VN", { day: "2-digit", month: "2-digit", year: "numeric" });
    },
    async fetchDoanhThuHomNay() {
      try {
        const resp = await ThongKeService.getDoanhThuTheoNgay();
        this.doanhThuTrongNgay = resp.data.body || null;
      } catch (e) {
        console.error("Lỗi khi tải doanh thu hôm nay:", e);
      }
    },
    async fetchMonthlyRevenue() {
      try {
        const resp = await ThongKeService.getDoanhThuTheoThang(this.selectedMonth, this.selectedYear);
        this.monthlyRevenue = resp.data.body || [];
        this.$nextTick(() => {
          this.initMonthlyRevenueChart();
        });
      } catch (e) {
        console.error("Lỗi khi tải doanh thu theo tháng:", e);
      }
    },
    initMonthlyRevenueChart() {
      const canvasElement = this.$refs.monthlyRevenueCanvas;
      // Kiểm tra xem canvas có tồn tại không
      if (!canvasElement) return;

      // Nếu biểu đồ đã tồn tại, hủy nó trước
      if (this.monthlyRevenueChart) {
        this.monthlyRevenueChart.destroy();
        this.monthlyRevenueChart = null;
      }

      // Kiểm tra dữ liệu có tồn tại không
      if (!this.monthlyRevenue.length) return;

      const ctx = canvasElement.getContext("2d");
      const sorted = [...this.monthlyRevenue].sort((a, b) => a.ngay - b.ngay);

      this.monthlyRevenueChart = new Chart(ctx, {
        type: "line",
        data: {
          labels: sorted.map(i => i.ngay),
          datasets: [
            { label: "Tổng Doanh Thu Ngày", data: sorted.map(i => i.tongDoanhThuNgay), tension: 0.4 },
            { label: "Tổng Số Hóa Đơn", data: sorted.map(i => i.tongSoHoaDon), yAxisID: "y-count", tension: 0.4 },
            { label: "Tổng Sản Phẩm", data: sorted.map(i => i.tongSanPham), yAxisID: "y-count", tension: 0.4 }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, title: { display: true, text: 'Doanh thu (VND)' }, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', beginAtZero: true, grid: { drawOnChartArea: false } }
          }
        }
      });

      this.chartsInitialized.monthly = true;
    },
    async fetchYearlyRevenue() {
      this.isYearlyLoading = true;
      try {
        const resp = await ThongKeService.getDoanhThuTheoNam(this.selectedYearForYearly);
        this.yearlyRevenue = resp.data.body || [];
        this.$nextTick(() => {
          this.initYearlyRevenueChart();
        });
      } catch (e) {
        console.error("Lỗi khi tải doanh thu theo năm:", e);
      } finally {
        this.isYearlyLoading = false;
      }
    },
    initYearlyRevenueChart() {
      const canvasElement = this.$refs.yearlyRevenueCanvas;
      // Kiểm tra xem canvas có tồn tại không
      if (!canvasElement) return;

      // Nếu biểu đồ đã tồn tại, hủy nó trước
      if (this.yearlyRevenueChart) {
        this.yearlyRevenueChart.destroy();
        this.yearlyRevenueChart = null;
      }

      // Kiểm tra dữ liệu có tồn tại không
      if (!this.yearlyRevenue.length) return;

      const ctx = canvasElement.getContext("2d");
      const months = Array.from({ length: 12 }, (_, i) => ({ tongDoanhThuThang: 0, tongSoHoaDon: 0, tongSanPham: 0 }));
      this.yearlyRevenue.forEach(i => { months[i.thang - 1] = i; });

      this.yearlyRevenueChart = new Chart(ctx, {
        type: "line",
        data: {
          labels: months.map((_, i) => `Tháng ${i + 1}`),
          datasets: [
            { label: 'Tổng Doanh Thu', data: months.map(m => m.tongDoanhThuThang), tension: 0.4 },
            { label: 'Số Hóa Đơn', data: months.map(m => m.tongSoHoaDon), yAxisID: 'y-count', tension: 0.4 },
            { label: 'Tổng Sản Phẩm', data: months.map(m => m.tongSanPham), yAxisID: 'y-count', tension: 0.4 }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });

      this.chartsInitialized.yearly = true;
    },
    async fetchDateRangeRevenue() {
      if (this.isFetching) return;
      this.isFetching = true;
      try {
        const resp = await ThongKeService.getDoanhThuTheoKhoangThoiGian(this.startDate, this.endDate);
        this.dateRangeRevenue = resp.data.body || [];
        this.$nextTick(() => {
          this.initDateRangeRevenueChart();
        });
      } catch (e) {
        console.error("Lỗi khi tải doanh thu theo khoảng thời gian:", e);
      } finally {
        this.isFetching = false;
      }
    },
    initDateRangeRevenueChart() {
      const canvasElement = this.$refs.dateRangeRevenueCanvas;
      // Kiểm tra xem canvas có tồn tại không
      if (!canvasElement) return;

      // Nếu biểu đồ đã tồn tại, hủy nó trước
      if (this.dateRangeRevenueChart) {
        this.dateRangeRevenueChart.destroy();
        this.dateRangeRevenueChart = null;
      }

      // Kiểm tra dữ liệu có tồn tại không
      if (!this.dateRangeRevenue.length) return;

      const ctx = canvasElement.getContext("2d");
      const sorted = [...this.dateRangeRevenue].sort((a, b) => new Date(a.ngay) - new Date(b.ngay));

      this.dateRangeRevenueChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: sorted.map(i => this.formatDate(i.ngay)),
          datasets: [
            { label: 'Tổng Doanh Thu', data: sorted.map(i => i.tongDoanhThu), fill: true, tension: 0.4 },
            { label: 'Số Hóa Đơn', data: sorted.map(i => i.tongHoaDon), yAxisID: 'y-count', tension: 0.4 },
            { label: 'Tổng Sản Phẩm', data: sorted.map(i => i.tongSanPhamBanDuoc), yAxisID: 'y-count', tension: 0.4 }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });

      this.chartsInitialized.dateRange = true;
    },
    async fetchLoaiAoDaiData() {
      try {
        const resp = await ThongKeService.getLoaiAoDaiBanNhieu();
        this.loaiAoDaiData = resp.data.body || [];
        this.$nextTick(() => {
          this.initPieChart();
        });
      } catch (e) {
        console.error("Lỗi khi tải dữ liệu loại áo dài:", e);
      }
    },
    initPieChart() {
      const canvasElement = this.$refs.pieChartCanvas;
      // Kiểm tra xem canvas có tồn tại không
      if (!canvasElement) return;

      // Nếu biểu đồ đã tồn tại, hủy nó trước
      if (this.pieChart) {
        this.pieChart.destroy();
        this.pieChart = null;
      }

      // Kiểm tra dữ liệu có tồn tại không
      if (!this.loaiAoDaiData.length) return;

      const ctx = canvasElement.getContext("2d");

      this.pieChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: this.loaiAoDaiData.map(i => i.tenLoaiAoDai),
          datasets: [{
            data: this.loaiAoDaiData.map(i => i.phanTram),
            backgroundColor: [
              'rgba(255, 99, 132, 0.7)',
              'rgba(54, 162, 235, 0.7)',
              'rgba(255, 206, 86, 0.7)',
              'rgba(75, 192, 192, 0.7)',
              'rgba(153, 102, 255, 0.7)',
              'rgba(255, 159, 64, 0.7)'
            ]
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false
        }
      });

      this.chartsInitialized.pie = true;
    },
    async fetchTopProducts() {
      try {
        const resp = await ThongKeService.getTop5SanPham();
        this.topProducts = resp.data.body || [];
        this.topProducts.sort((a, b) => b.tongTienDaBan - a.tongTienDaBan);
        this.$nextTick(() => {
          this.initTopProductsChart();
        });
      } catch (e) {
        console.error("Lỗi khi tải dữ liệu top sản phẩm:", e);
      }
    },
    initTopProductsChart() {
      const canvasElement = this.$refs.topProductsCanvas;
      // Kiểm tra xem canvas có tồn tại không
      if (!canvasElement) return;

      // Nếu biểu đồ đã tồn tại, hủy nó trước
      if (this.topProductsChart) {
        this.topProductsChart.destroy();
        this.topProductsChart = null;
      }

      // Kiểm tra dữ liệu có tồn tại không
      if (!this.topProducts.length) return;

      const ctx = canvasElement.getContext("2d");

      this.topProductsChart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: this.topProducts.map(p => `${p.tenAoDai} (${p.maAoDai})`),
          datasets: [
            { label: 'Doanh thu', data: this.topProducts.map(p => p.tongTienDaBan), yAxisID: 'y-rev' },
            { label: 'Số lượng', data: this.topProducts.map(p => p.tongSoLuongDaBan), type: 'line', yAxisID: 'y-qty' }
          ]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            'y-rev': { position: 'left', ticks: { callback: v => this.formatCurrency(v) } },
            'y-qty': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });

      this.chartsInitialized.topProducts = true;
    }
  },
  watch: {
    selectedMonth() {
      this.fetchMonthlyRevenue();
    },
    selectedYear() {
      this.fetchMonthlyRevenue();
    },
    selectedYearForYearly() {
      this.fetchYearlyRevenue();
    },
  }
};
</script>

<style scoped>
.chart-container {
  position: relative;
  width: 100%;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.no-data-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  color: #6c757d;
  font-style: italic;
}

.chart-container canvas {
  width: 100% !important;
  height: 100% !important;
}
</style>
