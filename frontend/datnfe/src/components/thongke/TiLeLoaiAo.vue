<template>
  <div class="card mt-4">
    <div class="card-header">
      <h4>Tỉ Lệ Loại Áo Dài Bán Chạy</h4>
    </div>
    <div class="card-body chart-container">
      <div v-if="loaiAoDaiData.length === 0" class="text-center py-4">
        <p class="text-muted">Không có dữ liệu về loại áo dài</p>
      </div>
      <canvas v-else ref="pieChartCanvas" id="pieChart"></canvas>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import ThongKeService from "../../services/ThongKeService";

export default {
  name: "TiLeLoaiAo",
  data() {
    return {
      pieChart: null,
      loaiAoDaiData: [],
    };
  },
  async mounted() {
    await this.fetchLoaiAoDaiData();
  },
  beforeUnmount() {
    if (this.pieChart) {
      this.pieChart.destroy();
    }
  },
  methods: {
    async fetchLoaiAoDaiData() {
      try {
        const response = await ThongKeService.getLoaiAoDaiBanNhieu();
        this.loaiAoDaiData = response.data.body || [];
        if (this.loaiAoDaiData.length > 0) {
          await this.$nextTick();
          this.initPieChart();
        }
      } catch (error) {
        console.error("Error fetching loai ao dai data:", error);
        this.$toast.error("Không thể tải dữ liệu thống kê loại áo dài");
        this.loaiAoDaiData = [];
      }
    },
    initPieChart() {
      const canvas = this.$refs.pieChartCanvas;
      if (!canvas) {
        console.error("Canvas không tồn tại.");
        return;
      }

      const ctx = canvas.getContext("2d");
      if (!ctx) {
        console.error("Không thể lấy context của canvas.");
        return;
      }

      if (this.pieChart) {
        this.pieChart.destroy();
      }

      this.pieChart = new Chart(ctx, {
        type: "pie",
        data: {
          labels: this.loaiAoDaiData.map((item) => item.tenLoaiAoDai),
          datasets: [
            {
              data: this.loaiAoDaiData.map((item) => item.phanTram),
              backgroundColor: [
                "rgb(0, 128, 0)",
                "rgb(128, 0, 128)",
                "rgb(255, 140, 0)",
                "rgb(0, 191, 255)",
                "rgb(220, 20, 60)",
                "rgb(34, 139, 34)",
                "rgb(255, 215, 0)",
                "rgb(30, 144, 255)",
                "rgb(139, 69, 19)",
                "rgb(255, 20, 147)",
              ].slice(0, this.loaiAoDaiData.length),
              borderWidth: 1,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          layout: { padding: 20 },
          plugins: {
            legend: {
              position: "right",
              labels: { padding: 10, usePointStyle: true, font: { size: 14 } },
            },
            tooltip: {
              callbacks: {
                label: (context) => {
                  const label = context.label || "";
                  const value = context.raw || 0;
                  const soLuong = this.loaiAoDaiData[context.dataIndex].soLuong;
                  return `${label}: ${value}% (${soLuong} sản phẩm)`;
                },
              },
            },
            title: {
              display: false,
              text: "Phân Bố Loại Áo Dài",
              font: { size: 16, weight: "bold" },
            },
          },
        },
      });
    },
  },
};
</script>

<style scoped>
.card { box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); transition: transform 0.2s; width: 100%; }
.card:hover { transform: translateY(-5px); }
.chart-container { position: relative; height: 500px; width: 100%; margin: 0 auto; }
canvas#pieChart { width: 100% !important; height: 100% !important; }
.card-header { background-color: #f8f9fa; border-bottom: 1px solid rgba(0, 0, 0, 0.125); }
.text-muted { color: #6c757d; font-style: italic; }
</style>