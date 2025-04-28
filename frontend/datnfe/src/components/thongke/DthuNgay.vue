<template>
  <div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h4>Doanh Thu Theo Ngày</h4>
      <input
        type="date"
        class="form-control"
        style="width: 200px"
        v-model="selectedDate"
        @change="fetchDailyRevenue"
      />
    </div>
    <div class="card-body">
      <div v-if="dailyRevenue" class="row">
        <div class="col-md-4 mb-3">
          <div class="p-3 bg-primary text-white rounded">
            <h6>Số Hóa Đơn</h6>
            <h4>{{ dailyRevenue.soHoaDon }}</h4>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="p-3 bg-success text-white rounded">
            <h6>Tổng Doanh Thu</h6>
            <h4>{{ formatCurrency(dailyRevenue.tongDoanhThu) }}</h4>
          </div>
        </div>
        <div class="col-md-4 mb-3">
          <div class="p-3 bg-info text-white rounded">
            <h6>Tổng Sản Phẩm</h6>
            <h4>{{ dailyRevenue.tongSanPham }}</h4>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-4">
        <p class="text-muted">Không có dữ liệu doanh thu cho ngày đã chọn</p>
      </div>

      <canvas
        v-if="dailyRevenue"
        ref="dailyRevenueCanvas"
        id="dailyRevenueChart"
        height="100"
      ></canvas>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "DthuNgay",
  data() {
    return {
      dailyRevenue: null,
      selectedDate: new Date().toISOString().split("T")[0],
      dailyRevenueChart: null,
    };
  },
  async mounted() {
    await this.fetchDailyRevenue();
  },
  beforeUnmount() {
    if (this.dailyRevenueChart) {
      this.dailyRevenueChart.destroy();
    }
  },
  watch: {
    selectedDate() {
      this.fetchDailyRevenue();
    },
  },
  methods: {
    formatCurrency(value) {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value);
    },

    async fetchDailyRevenue() {
      try {
        const response = await ThongKeService.getDoanhThuTrongNgay(
          this.selectedDate
        );
        this.dailyRevenue = response.data.body[0] || null;
        await this.$nextTick();
        this.destroyDailyRevenueChart();
        if (this.dailyRevenue) {
          setTimeout(() => {
            this.initDailyRevenueChart();
          }, 100);
        }
      } catch (error) {
        console.error("Error fetching daily revenue:", error);
        this.$toast.error("Không thể tải dữ liệu doanh thu trong ngày");
      }
    },

    destroyDailyRevenueChart() {
      if (this.dailyRevenueChart) {
        console.log("Đang hủy biểu đồ cũ với ID:", this.dailyRevenueChart.id);
        this.dailyRevenueChart.destroy();
        this.dailyRevenueChart = null;
      } else {
        // console.log("Không có biểu đồ nào để hủy");
      }
    },

    initDailyRevenueChart() {
      if (!this.dailyRevenue) return;

      const canvas = this.$refs.dailyRevenueCanvas;
      if (!canvas) {
        console.error("Canvas element not found in DthuNgay.vue");
        return;
      }

      const ctx = canvas.getContext("2d");
      if (!ctx) {
        console.error("Không lấy được context của canvas");
        return;
      }

      this.dailyRevenueChart = new Chart(ctx, {
        type: "bar",
        data: {
          labels: [this.dailyRevenue.ngay],
          datasets: [
            {
              label: "Tổng Doanh Thu",
              data: [this.dailyRevenue.tongDoanhThu],
              backgroundColor: "rgba(40, 167, 69, 0.6)",
              borderColor: "rgb(40, 167, 69)",
              borderWidth: 2,
              yAxisID: "yLeft",
              barThickness: 40,
            },
            {
              label: "Số Hóa Đơn",
              data: [this.dailyRevenue.soHoaDon],
              backgroundColor: "rgba(0, 123, 255, 0.6)",
              borderColor: "rgb(0, 123, 255)",
              borderWidth: 2,
              yAxisID: "yRight",
              barThickness: 40,
            },
            {
              label: "Tổng Sản Phẩm",
              data: [this.dailyRevenue.tongSanPham],
              backgroundColor: "rgba(23, 162, 184, 0.6)",
              borderColor: "rgb(23, 162, 184)",
              borderWidth: 2,
              yAxisID: "yRight",
              barThickness: 40,
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
                  if (context.dataset.label === "Tổng Doanh Thu") {
                    return (
                      context.dataset.label +
                      ": " +
                      this.formatCurrency(context.raw)
                    );
                  }
                  return context.dataset.label + ": " + context.raw;
                },
              },
            },
          },
          // Cấu hình trục
          scales: {
            x: {
              type: "category",
              categoryPercentage: 0.5,
              barPercentage: 0.8,
            },
            yLeft: {
              type: "linear",
              display: true,
              position: "left",
              beginAtZero: true,
              suggestedMax: 6000000,
              title: {
                display: true,
                text: "Tổng Doanh Thu (VND)",
              },
            },
            yRight: {
              type: "linear",
              display: true,
              position: "right",
              beginAtZero: true,
              suggestedMax: 20,
              title: {
                display: true,
                text: "Số lượng",
              },
              grid: {
                drawOnChartArea: false,
              },
            },
          },
        },
      });
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
</style>
