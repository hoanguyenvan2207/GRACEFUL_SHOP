<template>
  <div class="card mt-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h4>Doanh Thu Theo Khoảng Thời Gian</h4>
      <div class="d-flex gap-2">
        <input type="date" class="form-control" v-model="startDate" @change="fetchDateRangeRevenue"/>
        <input type="date" class="form-control" v-model="endDate" @change="fetchDateRangeRevenue"/>
      </div>
    </div>
    <div class="card-body">
      <div v-if="dateRangeRevenue.length === 0" class="text-center py-4">
        <p class="text-muted">Không có dữ liệu cho khoảng thời gian đã chọn</p>
      </div>
      <canvas v-else ref="dateRangeRevenueChartCanvas" id="dateRangeRevenueChart" height="100"></canvas>

      <!-- Bảng thống kê chi tiết -->
      <div v-if="dateRangeRevenue.length > 0" class="mt-4 table-responsive">
        <h5 class="mb-3">Chi tiết doanh thu theo ngày</h5>
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th class="text-center">Ngày</th>
              <th class="text-center">Tổng doanh thu</th>
              <th class="text-center">Số hóa đơn</th>
              <th class="text-center">Tổng sản phẩm bán được</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in sortedDateRangeRevenue" :key="index">
              <td>{{ formatDate(item.ngay) }}</td>
              <td>{{ formatCurrency(item.tongDoanhThu) }}</td>
              <td class="text-center">{{ item.tongHoaDon }}</td>
              <td class="text-center">{{ item.tongSanPhamBanDuoc }}</td>
            </tr>
            <tr class="table-success fw-bold">
              <td>Tổng cộng</td>
              <td>{{ formatCurrency(totalRevenue.tongDoanhThu) }}</td>
              <td class="text-center">{{ totalRevenue.tongHoaDon }}</td>
              <td class="text-center">{{ totalRevenue.tongSanPhamBanDuoc }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "DoanhThuTheoThoiGian",
  data() {
    return {
      dateRangeRevenueChart: null,
      dateRangeRevenue: [],
      startDate: new Date().toISOString().split("T")[0],
      endDate: new Date().toISOString().split("T")[0],
      isFetching: false,
    };
  },
  computed: {
    sortedDateRangeRevenue() {
      return [...this.dateRangeRevenue].sort(
        (a, b) => new Date(a.ngay) - new Date(b.ngay)
      );
    },
    totalRevenue() {
      if (this.dateRangeRevenue.length === 0) {
        return { tongDoanhThu: 0, tongHoaDon: 0, tongSanPhamBanDuoc: 0 };
      }
      return this.dateRangeRevenue.reduce(
        (total, item) => ({
          tongDoanhThu: total.tongDoanhThu + (item.tongDoanhThu || 0),
          tongHoaDon: total.tongHoaDon + (item.tongHoaDon || 0),
          tongSanPhamBanDuoc:
            total.tongSanPhamBanDuoc + (item.tongSanPhamBanDuoc || 0),
        }),
        { tongDoanhThu: 0, tongHoaDon: 0, tongSanPhamBanDuoc: 0 }
      );
    },
  },
  async mounted() {
    const today = new Date();
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(today.getDate() - 7);

    this.startDate = sevenDaysAgo.toISOString().split("T")[0];
    this.endDate = today.toISOString().split("T")[0];

    await this.fetchDateRangeRevenue();
  },
  beforeUnmount() {
    if (this.dateRangeRevenueChart) {
      this.dateRangeRevenueChart.destroy();
    }
  },
  methods: {
    formatCurrency(value) {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value || 0);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
      });
    },
    async fetchDateRangeRevenue() {
      if (this.isFetching) return; // Ngăn chặn fetch đồng thời
      this.isFetching = true;
      try {
        const response = await ThongKeService.getDoanhThuTheoKhoangThoiGian(
          this.startDate,
          this.endDate
        );
        this.dateRangeRevenue = response.data.body || [];

        if (this.dateRangeRevenue.length > 0) {
          await this.$nextTick();
          this.initDateRangeRevenueChart();
        }
      } catch (error) {
        console.error("Lỗi khi tải dữ liệu doanh thu:", error);
        this.$toast?.error("Không thể tải dữ liệu doanh thu");
      } finally {
        this.isFetching = false;
      }
    },
    initDateRangeRevenueChart() {
      const canvas = this.$refs.dateRangeRevenueChartCanvas;
      if (!canvas) {
        console.error("Canvas không tồn tại.");
        return;
      }

      const existingChart = Chart.getChart(canvas);
      if (existingChart) {
        existingChart.destroy();
      }

      const ctx = canvas.getContext("2d");
      if (!ctx) {
        console.error("Không thể lấy context của canvas.");
        return;
      }

      const sortedData = this.sortedDateRangeRevenue;

      this.dateRangeRevenueChart = new Chart(ctx, {
        type: "line",
        data: {
          labels: sortedData.map((item) => this.formatDate(item.ngay)),
          datasets: [
            {
              label: "Tổng Doanh Thu",
              data: sortedData.map((item) => item.tongDoanhThu),
              borderColor: "rgb(40, 167, 69)",
              backgroundColor: "rgba(40, 167, 69, 0.1)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(40, 167, 69)",
              fill: true,
            },
            {
              label: "Số Hóa Đơn",
              data: sortedData.map((item) => item.tongHoaDon),
              borderColor: "rgb(220, 53, 69)",
              backgroundColor: "rgba(220, 53, 69, 0.1)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(220, 53, 69)",
              borderDash: [5, 5],
              yAxisID: "y-axis-count",
              fill: false,
            },
            {
              label: "Tổng Sản Phẩm Bán Được",
              data: sortedData.map((item) => item.tongSanPhamBanDuoc),
              borderColor: "rgb(23, 162, 184)",
              backgroundColor: "rgba(23, 162, 184, 0.1)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(23, 162, 184)",
              borderDash: [5, 5],
              yAxisID: "y-axis-count",
              fill: false,
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: "top",
              labels: { usePointStyle: true, padding: 15, font: { size: 12 } },
            },
            tooltip: {
              backgroundColor: "rgba(0, 0, 0, 0.8)",
              padding: 10,
              titleFont: { size: 14 },
              bodyFont: { size: 13 },
              callbacks: {
                label: (context) => {
                  if (context.dataset.label === "Tổng Doanh Thu") {
                    return context.dataset.label + ": " + this.formatCurrency(context.raw);
                  }
                  return context.dataset.label + ": " + context.raw;
                },
              },
            },
          },
          scales: {
            x: { grid: { display: false }, ticks: { maxRotation: 45, minRotation: 45 } },
            y: {
              beginAtZero: true,
              ticks: { callback: (value) => this.formatCurrency(value) },
              title: { display: true, text: "Doanh thu (VNĐ)", font: { weight: "bold" } },
            },
            "y-axis-count": {
              type: "linear",
              position: "right",
              title: { display: true, text: "Số lượng", font: { weight: "bold" } },
              ticks: { stepSize: 1, precision: 0 },
              grid: { drawOnChartArea: false },
            },
          },
          interaction: { intersect: false, mode: "index" },
          animation: { duration: 1000, easing: "easeOutQuart" },
          elements: { point: { radius: 4, hoverRadius: 6 } },
        },
      });
    },
  },
  watch: {
    startDate() { this.fetchDateRangeRevenue(); },
    endDate() { this.fetchDateRangeRevenue(); },
  },
};
</script>

<style scoped>
.card { box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); border-radius: 8px; transition: transform 0.2s;height: 100%; }
.card:hover { transform: translateY(-5px); }
.card-header { background-color: #f8f9fa; border-bottom: 1px solid rgba(0, 0, 0, 0.125); }
.form-control { border-radius: 4px; border: 1px solid #ced4da; padding: 0.375rem 0.75rem; }
.form-control:focus { border-color: #80bdff; outline: 0; box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25); }
.table th { background-color: #f1f8ff; }
.table-hover tbody tr:hover { background-color: #f5f5f5; }
</style>