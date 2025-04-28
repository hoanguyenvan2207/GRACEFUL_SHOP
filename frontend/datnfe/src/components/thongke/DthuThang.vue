<template>
  <div class="card mt-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h4>Doanh Thu Theo Tháng</h4>
      <div class="d-flex gap-2">
        <select class="form-select" style="width: 100px" v-model="selectedMonth">
          <option v-for="month in 12" :key="month" :value="month">
            Tháng {{ month }}
          </option>
        </select>
        <select class="form-select" style="width: 120px" v-model="selectedYear">
          <option v-for="year in years" :key="year" :value="year">
            Năm {{ year }}
          </option>
        </select>
      </div>
    </div>
    <div class="card-body">
      <!-- Nếu không có dữ liệu, hiển thị thông báo -->
      <div
        v-if="isEmptyData"
        class="text-center text-muted"
        style="padding: 50px 0; font-size: 1.2rem;"
      >
        Không có dữ liệu
      </div>
      <!-- Ngược lại, hiển thị biểu đồ -->
      <canvas v-else ref="monthlyRevenueCanvas" height="100"></canvas>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "DthuThang",
  data() {
    return {
      monthlyRevenueChart: null,
      selectedMonth: new Date().getMonth() + 1,
      selectedYear: new Date().getFullYear(),
      monthlyRevenue: [],
      years: [2024, 2025, 2026],
    };
  },
  computed: {
    // Kiểm tra xem có dữ liệu hay không
    isEmptyData() {
      return this.monthlyRevenue.length === 0;
    },
  },
  async mounted() {
    await this.fetchMonthlyRevenue();
  },
  beforeUnmount() {
    this.destroyChart();
  },
  watch: {
    selectedMonth() {
      this.fetchMonthlyRevenue();
    },
    selectedYear() {
      this.fetchMonthlyRevenue();
    },
  },
  methods: {
    formatCurrency(value) {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value);
    },

    async fetchMonthlyRevenue() {
      try {
        const response = await ThongKeService.getDoanhThuTheoThang(
          this.selectedMonth,
          this.selectedYear
        );
        this.monthlyRevenue = response.data.body;
        // Phá hủy chart cũ trước khi vẽ lại
        this.destroyChart();
        this.$nextTick(() => {
          // Chỉ khởi tạo chart khi có dữ liệu
          if (!this.isEmptyData) {
            this.initMonthlyRevenueChart();
          }
        });
      } catch (error) {
        console.error("Error fetching monthly revenue:", error);
        this.$toast.error("Không thể tải dữ liệu doanh thu theo tháng");
      }
    },

    initMonthlyRevenueChart() {
      const canvas = this.$refs.monthlyRevenueCanvas;
      if (!canvas) return;

      const ctx = canvas.getContext("2d");
      if (!ctx) return;

      const sortedData = [...this.monthlyRevenue].sort((a, b) => a.ngay - b.ngay);

      this.monthlyRevenueChart = new Chart(ctx, {
        type: "line",
        data: {
          labels: sortedData.map((item) => `Ngày ${item.ngay}`),
          datasets: [
            {
              label: "Tổng Doanh Thu Ngày",
              data: sortedData.map((item) => item.tongDoanhThuNgay),
              borderColor: "rgb(40, 167, 69)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(40, 167, 69)",
            },
            {
              label: "Tổng Số Hóa Đơn",
              data: sortedData.map((item) => item.tongSoHoaDon),
              borderColor: "rgb(220, 53, 69)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(220, 53, 69)",
              borderDash: [5, 5],
              yAxisID: "y-axis-count",
            },
            {
              label: "Tổng Sản Phẩm",
              data: sortedData.map((item) => item.tongSanPham),
              borderColor: "rgb(23, 162, 184)",
              borderWidth: 2,
              tension: 0.4,
              pointBackgroundColor: "rgb(23, 162, 184)",
              borderDash: [5, 5],
              yAxisID: "y-axis-count",
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: "top",
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  if (context.dataset.label === "Tổng Doanh Thu Ngày") {
                    return (
                      context.dataset.label +
                      ": " +
                      this.formatCurrency(context.raw)
                    );
                  } else {
                    return context.dataset.label + ": " + context.raw;
                  }
                },
              },
            },
          },
          scales: {
            x: { grid: { display: false } },
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: "Doanh thu (VND)",
                font: { weight: "bold" },
              },
              ticks: { callback: (value) => this.formatCurrency(value) },
            },
            "y-axis-count": {
              type: "linear",
              position: "right",
              beginAtZero: true,
              title: {
                display: true,
                text: "Số lượng",
                font: { weight: "bold" },
              },
              ticks: { stepSize: 1, precision: 0 },
              grid: { drawOnChartArea: false },
            },
          },
          interaction: { intersect: false, mode: "index" },
        },
      });
    },

    destroyChart() {
      if (this.monthlyRevenueChart) {
        this.monthlyRevenueChart.destroy();
        this.monthlyRevenueChart = null;
      }
    },
  },
};
</script>

<style scoped>
.card {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  height: 100%;
}

.card:hover {
  transform: translateY(-5px);
}

.form-select {
  font-size: 16px;
  padding: 8px 12px;
  height: 40px;
}

.form-control {
  border-radius: 4px;
  border: 1px solid #ced4da;
  padding: 0.375rem 0.75rem;
}

.form-control:focus {
  border-color: #80bdff;
  outline: 0;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
}

/* Styles cho thông báo không có dữ liệu */
.text-muted {
  color: #6c757d;
}
</style>
