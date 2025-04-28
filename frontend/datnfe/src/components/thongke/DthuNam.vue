<template>
  <div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
      <h2 class="text-lg font-bold m-0">Thống kê doanh thu theo năm</h2>
      <div class="d-flex align-items-center gap-2">
        <select
          class="form-select"
          style="width: 120px"
          v-model="selectedYear"
          :disabled="isLoading"
        >
          <option v-for="year in availableYears" :key="year" :value="year">{{ year }}</option>
        </select>
        <div v-if="isLoading" class="spinner-border spinner-border-sm text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
      </div>
    </div>
    <div class="card-body">
      <div class="chart-wrapper">
        <canvas ref="yearlyRevenueChartCanvas"></canvas>
        <div v-if="showNoData && !isLoading" class="no-data-overlay">Không có dữ liệu</div>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';
import ThongKeService from '../../services/ThongKeService';

export default {
  name: 'DthuNam',
  data() {
    return {
      selectedYear: new Date().getFullYear(),
      availableYears: Array.from({ length: 3 }, (_, i) => new Date().getFullYear() - i),
      yearlyRevenueChart: null,
      showNoData: false,
      isLoading: false,
    };
  },
  mounted() { this.fetchYearlyRevenue(); },
  beforeUnmount() { this.yearlyRevenueChart && this.yearlyRevenueChart.destroy(); },
  methods: {
    async fetchYearlyRevenue() {
      this.isLoading = true;
      try {
        const { data } = await ThongKeService.getDoanhThuTheoNam(this.selectedYear);
        const monthsData = Array(12).fill({ tongDoanhThuThang:0, tongSoHoaDon:0, tongSanPham:0 });
        (data.body || []).forEach(item => { monthsData[item.thang-1] = item; });
        if (!this.yearlyRevenueChart) this.initChart(monthsData);
        else {
          this.yearlyRevenueChart.data.datasets[0].data = monthsData.map(m => m.tongDoanhThuThang);
          this.yearlyRevenueChart.data.datasets[1].data = monthsData.map(m => m.tongSoHoaDon);
          this.yearlyRevenueChart.data.datasets[2].data = monthsData.map(m => m.tongSanPham);
          this.yearlyRevenueChart.update();
        }
        this.showNoData = !(data.body && data.body.length);
      } catch (e) { console.error(e); }
      this.isLoading = false;
    },
    initChart(monthsData) {
      const ctx = this.$refs.yearlyRevenueChartCanvas.getContext('2d');
      this.yearlyRevenueChart = new Chart(ctx, {
        type:'line',
        data: {
          labels: ['Tháng 1','Tháng 2','Tháng 3','Tháng 4','Tháng 5','Tháng 6','Tháng 7','Tháng 8','Tháng 9','Tháng 10','Tháng 11','Tháng 12'],
          datasets: [
            { label:'Tổng Doanh Thu', data: monthsData.map(m=>m.tongDoanhThuThang), borderColor:'rgb(40,167,69)', tension:0.4 },
            { label:'Số Hóa Đơn', data: monthsData.map(m=>m.tongSoHoaDon), borderColor:'rgb(220,53,69)', borderDash:[5,5], yAxisID:'y-count', tension:0.4 },
            { label:'Tổng Sản Phẩm', data: monthsData.map(m=>m.tongSanPham), borderColor:'rgb(23,162,184)', borderDash:[5,5], yAxisID:'y-count', tension:0.4 },
          ]
        },
        options:{ responsive:true, maintainAspectRatio:false, scales:{ x:{grid:{display:false}}, y:{beginAtZero:true,title:{display:true,text:'Doanh thu (VND)'},ticks:{callback:v=>new Intl.NumberFormat('vi-VN',{style:'currency',currency:'VND'}).format(v)}}, 'y-count':{position:'right',beginAtZero:true,title:{display:true,text:'Số lượng'},grid:{drawOnChartArea:false},ticks:{stepSize:1}} }
      });
    }
  }
};
</script>

<style scoped>
.chart-wrapper { position: relative; width:100%; height:400px; }
.no-data-overlay { position:absolute; top:0; left:0; width:100%; height:100%; display:flex; align-items:center; justify-content:center; background:rgba(255,255,255,0.8); color:#6c757d; font-style:italic; }
</style>