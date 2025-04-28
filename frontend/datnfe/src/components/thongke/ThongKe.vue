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
          <div class="col-md-4 mb-3">
            <div class="p-3 bg-primary text-white rounded">
              <h6>Tổng Hóa Đơn</h6>
              <h4>{{ totalRevenue.tongHoaDon }}</h4>
            </div>
          </div>
          <div class="col-md-4 mb-3">
            <div class="p-3 bg-success text-white rounded">
              <h6>Tổng Doanh Thu</h6>
              <h4>{{ formatCurrency(totalRevenue.tongDoanhThu) }}</h4>
            </div>
          </div>
          <div class="col-md-4 mb-3">
            <div class="p-3 bg-info text-white rounded">
              <h6>Tổng Sản Phẩm</h6>
              <h4>{{ totalRevenue.tongSanPhamBanDuoc }}</h4>
            </div>
          </div>
        </div>
        <div v-else class="text-center py-4">
          <p class="text-muted">Không có dữ liệu doanh thu cho ngày đã chọn</p>
        </div>
        <canvas v-if="dateRangeRevenue.length" ref="dateRangeRevenueCanvas" height="100"></canvas>
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
        <canvas ref="monthlyRevenueCanvas" height="100"></canvas>
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
        <div class="chart-container">
          <canvas ref="yearlyRevenueCanvas" style="width: 100%; height: 350px;"></canvas>
          <div v-if="!yearlyRevenue.length && !isYearlyLoading" class="no-data-overlay">
            Không có dữ liệu
          </div>
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
        <canvas v-else ref="pieChartCanvas" class="mx-auto" style="max-width: 300px; max-height: 300px;"></canvas>
      </div>
    </div>

    <!-- Top 5 Áo Dài Bán Chạy -->
    <div class="card mt-4">
      <div class="card-header">
        <h4>Top 5 Áo Dài Bán Chạy</h4>
      </div>
      <div class="card-body chart-container">
        <canvas ref="topProductsCanvas" v-show="topProducts.length > 0" style="width: 100%; height: 350px;"></canvas>
      </div>
      <div v-if="selectedProduct" class="mt-4">
        <div class="card">
          <div class="card-header">
            <h5>Chi tiết sản phẩm</h5>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="col-md-6">
                <p><strong>Mã áo dài:</strong> {{ selectedProduct.maAoDai }}</p>
                <p><strong>Tên áo dài:</strong> {{ selectedProduct.tenAoDai }}</p>
              </div>
              <div class="col-md-6">
                <p><strong>Số lượng đã bán:</strong> {{ selectedProduct.tongSoLuongDaBan }}</p>
                <p><strong>Doanh thu:</strong> {{ formatCurrency(selectedProduct.tongTienDaBan) }}</p>
              </div>
            </div>
          </div>
        </div>
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
    await Promise.all([
      this.fetchMonthlyRevenue(),
      this.fetchYearlyRevenue(),
      this.fetchDateRangeRevenue(),
      this.fetchLoaiAoDaiData(),
      this.fetchTopProducts(),
      this.fetchDoanhThuHomNay()
    ]);
  },
  beforeUnmount() {
    [
      this.dailyRevenueChart,
      this.monthlyRevenueChart,
      this.yearlyRevenueChart,
      this.dateRangeRevenueChart,
      this.pieChart,
      this.topProductsChart
    ].forEach(c => c && c.destroy());
  },
  methods: {
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
      } catch (e) { console.error(e); }
    },
    async fetchMonthlyRevenue() {
      try {
        const resp = await ThongKeService.getDoanhThuTheoThang(this.selectedMonth, this.selectedYear);
        this.monthlyRevenue = resp.data.body;
        this.$nextTick(() => {
          this.monthlyRevenueChart && this.monthlyRevenueChart.destroy();
          this.initMonthlyRevenueChart();
        });
      } catch (e) { console.error(e); }
    },
    initMonthlyRevenueChart() {
      const ctx = this.$refs.monthlyRevenueCanvas.getContext("2d");
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
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, title: { display: true, text: 'Doanh thu (VND)' }, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', beginAtZero: true, grid: { drawOnChartArea: false } }
          }
        }
      });
    },
    async fetchYearlyRevenue() {
      this.isYearlyLoading = true;
      try {
        const resp = await ThongKeService.getDoanhThuTheoNam(this.selectedYearForYearly);
        this.yearlyRevenue = resp.data.body || [];
        this.$nextTick(() => {
          this.yearlyRevenueChart && this.yearlyRevenueChart.destroy();
          this.yearlyRevenue.length && this.initYearlyRevenueChart();
        });
      } catch (e) {
        console.error(e);
      } finally {
        this.isYearlyLoading = false;
      }
    },
    initYearlyRevenueChart() {
      const ctx = this.$refs.yearlyRevenueCanvas.getContext("2d");
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
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });
    },
    async fetchDateRangeRevenue() {
      if (this.isFetching) return;
      this.isFetching = true;
      try {
        const resp = await ThongKeService.getDoanhThuTheoKhoangThoiGian(this.startDate, this.endDate);
        this.dateRangeRevenue = resp.data.body || [];
        this.$nextTick(() => {
          this.dateRangeRevenueChart && this.dateRangeRevenueChart.destroy();
          this.dateRangeRevenue.length && this.initDateRangeRevenueChart();
        });
      } catch (e) {
        console.error(e);
      } finally {
        this.isFetching = false;
      }
    },
    initDateRangeRevenueChart() {
      const canvas = this.$refs.dateRangeRevenueCanvas;
      if (!canvas) return;
      const ctx = canvas.getContext("2d");
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
          scales: {
            x: { grid: { display: false } },
            y: { beginAtZero: true, ticks: { callback: v => this.formatCurrency(v) } },
            'y-count': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });
    },
    async fetchLoaiAoDaiData() {
      try {
        const resp = await ThongKeService.getLoaiAoDaiBanNhieu();
        this.loaiAoDaiData = resp.data.body || [];
        this.$nextTick(() => {
          this.pieChart && this.pieChart.destroy();
          this.loaiAoDaiData.length && this.initPieChart();
        });
      } catch (e) {
        console.error(e);
      }
    },
    initPieChart() {
      const ctx = this.$refs.pieChartCanvas.getContext("2d");
      this.pieChart = new Chart(ctx, {
        type: 'pie',
        data: {
          labels: this.loaiAoDaiData.map(i => i.tenLoaiAoDai),
          datasets: [{ data: this.loaiAoDaiData.map(i => i.phanTram) }]
        },
        options: { responsive: true }
      });
    },
    async fetchTopProducts() {
      try {
        const resp = await ThongKeService.getTop5SanPham();
        this.topProducts = resp.data.body || [];
        this.topProducts.sort((a, b) => b.tongTienDaBan - a.tongTienDaBan);
        this.$nextTick(() => {
          this.topProductsChart && this.topProductsChart.destroy();
          this.topProducts.length && this.initTopProductsChart();
        });
      } catch (e) {
        console.error(e);
      }
    },
    initTopProductsChart() {
      const ctx = this.$refs.topProductsCanvas.getContext("2d");
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
          scales: {
            'y-rev': { position: 'left', ticks: { callback: v => this.formatCurrency(v) } },
            'y-qty': { position: 'right', grid: { drawOnChartArea: false } }
          }
        }
      });
    }
  },
  watch: {
    selectedMonth: 'fetchMonthlyRevenue',
    selectedYear: 'fetchMonthlyRevenue',
    selectedYearForYearly: 'fetchYearlyRevenue',
    startDate: 'fetchDateRangeRevenue',
    endDate: 'fetchDateRangeRevenue'
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
