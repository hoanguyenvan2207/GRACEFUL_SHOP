<template>
  <div class="card chart-container">
    <div class="card-header">
      <h4>Top 5 Áo Dài Bán Chạy</h4>
    </div>
    <div class="card-body px-2 py-3">
      <div class="chart-wrapper">
        <canvas
          ref="combinedChartCanvas"
          id="combinedChart"
          v-show="topProducts.length > 0"
        ></canvas>
      </div>
    </div>

    <!-- Chi tiết sản phẩm được chọn -->
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
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "Top5SanPham",
  data() {
    return {
      chart: null,
      topProducts: [],
      selectedProduct: null,
    };
  },
  async mounted() {
    await this.fetchTopProducts();
  },
  beforeUnmount() {
    if (this.chart) {
      this.chart.destroy();
    }
  },
  methods: {
    formatCurrency(value) {
      return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value);
    },
    async fetchTopProducts() {
      try {
        const response = await ThongKeService.getTop5SanPham();
        this.topProducts = response.data.body || [];
        this.topProducts.sort((a, b) => b.tongTienDaBan - a.tongTienDaBan);

        if (this.topProducts.length > 0) {
          await this.$nextTick();
          this.initChart();
        }
      } catch (error) {
        console.error("Error fetching top products:", error);
        this.$toast.error("Không thể tải dữ liệu top sản phẩm");
      }
    },
    handleChartClick(event) {
      if (!this.chart) return;
      const points = this.chart.getElementsAtEventForMode(event, "nearest", { intersect: true }, true);
      if (points.length) {
        this.selectedProduct = this.topProducts[points[0].index];
      }
    },
    initChart() {
      const canvas = this.$refs.combinedChartCanvas;
      if (!canvas) {
        console.error("Canvas không tồn tại.");
        return;
      }

      const ctx = canvas.getContext("2d");
      if (!ctx) {
        console.error("Không thể lấy context của canvas.");
        return;
      }

      if (this.chart) {
        this.chart.destroy();
      }

      this.chart = new Chart(ctx, {
        type: "bar",
        data: {
          labels: this.topProducts.map((product) => `${product.tenAoDai} (${product.maAoDai})`),
          datasets: [
            {
              label: "Doanh thu (VND)",
              data: this.topProducts.map((product) => product.tongTienDaBan),
              backgroundColor: "rgba(255, 99, 132, 0.7)",
              borderColor: "rgb(255, 99, 132)",
              borderWidth: 1,
              yAxisID: "y-axis-revenue",
              barPercentage: 0.2,
              categoryPercentage: 0.7,
            },
            {
              label: "Số lượng bán",
              data: this.topProducts.map((product) => product.tongSoLuongDaBan),
              type: "line",
              borderColor: "rgb(54, 162, 235)",
              borderWidth: 3,
              yAxisID: "y-axis-quantity",
              pointBackgroundColor: "rgb(54, 162, 235)",
              pointRadius: 5,
              fill: false,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            "y-axis-revenue": {
              type: "linear",
              position: "left",
              ticks: {
                callback: (value) =>
                  new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value),
              },
              title: { display: true, text: "Doanh thu (VND)" },
            },
            "y-axis-quantity": {
              type: "linear",
              position: "right",
              ticks: { stepSize: 1 },
              title: { display: true, text: "Số lượng bán" },
              grid: { drawOnChartArea: false },
            },
          },
          plugins: { legend: { display: true, position: "top" } },
          onClick: this.handleChartClick,
        },
      });
    },
  },
};
</script>

<style scoped>
.card { box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); transition: transform 0.2s; border-radius: 8px; overflow: hidden; }
.card:hover { transform: translateY(-3px); }
.card-header { background-color: #f8f9fa; border-bottom: 1px solid rgba(0, 0, 0, 0.125); padding: 0.75rem 1.25rem; }
.card-header h4 { margin: 0; font-weight: 600; color: #2c3e50; }
.chart-container { width: 100%; margin-bottom: 2rem; }
.chart-wrapper { position: relative; height: 450px; width: 100%; margin: 0 auto; }
canvas { width: 100%; height: 100%; }
</style>