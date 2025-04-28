<template>
  <div class="main-container">
    <h2 class="page-title">Danh sách nhân viên</h2>
    <div class="table-container">
      <div class="action-buttons">
        <router-link to="/nhan-vien/add" class="btn btn-success">
          <i class="bi bi-plus"></i>Thêm nhân viên
        </router-link>
        <button @click="exportExcel" class="btn btn-success">
          <i class="bi bi-file-excel"></i> Xuất Excel
        </button>
        <div class="loc-trangthai">
          <div class="input-group">
            <span class="input-group-text">
              <i class="bi bi-funnel"></i>
            </span>
            <select v-model="statusFilter" class="form-select" @change="searchNhanVien">
              <option value="">Tất cả</option>
              <option value="true">Đang làm</option>
              <option value="false">Đã nghỉ làm</option>
            </select>
          </div>
        </div>
        <div class="search-control">
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-search"></i></span>
            <input type="text" v-model="searchTerm" class="form-control" @input="searchNhanVien"
              placeholder="Tìm kiếm nhân viên">
          </div>
        </div>
      </div>
      <table class="table table-hover">
        <thead>
          <tr>
            <th>#</th>
            <th>Mã Nhân Viên</th>
            <th>Họ tên</th>
            <th>Giới Tính</th>
            <th>Ngày Sinh</th>
            <th>Email</th>
            <th>Số Điện Thoại</th>
            <th>Địa Chỉ</th>
            <th>Trạng Thái</th>
            <th>Chức Vụ</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="nv in nhanviens" :key="nv.id" @click="detailNhanVien(nv.id)" :class="{ 'empty-row': nv._empty }"
            :style="{ cursor: nv._empty ? 'default' : 'pointer', visibility: nv._empty ? 'hidden' : 'visible' }">
            <td>{{ nv.id }}</td>
            <td>{{ nv.maNhanVien }}</td>
            <td>{{ nv.hoVaTen }}</td>
            <td>{{ nv.gioiTinh ? 'Nam' : 'Nữ' }}</td>
            <td>{{ formatDate(nv.ngaySinh) }}</td>
            <td>{{ nv.email }}</td>
            <td>{{ nv.soDienThoai }}</td>
            <td>{{ nv.diaChi }}</td>
            <td @click.stop>
              <a-switch :checked="nv.trangThai" checkedChildren="Đang làm" unCheckedChildren="Đã nghỉ làm" @click.stop
                @change="checked => handleToggle(nv, checked)" />
            </td>
            <td>{{ nv.vaiTro?.tenVaiTro }}</td>
            <td>
              <div class="action-icons">
                <button @click.stop="openUpdateModal(nv.id)" class="btn btn-sm btn-outline-warning" title="Sửa">
                  <i class="bi bi-pencil"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div>
      <Detailnhanvien :show="showDetailModal" :employeeId="selectedEmployeeId" @close="showDetailModal = false" />
    </div>
    <div class="pagination-container">
      <div class="entries-control">
        Hiện
        <select v-model="size" @change="fetchNhanViens()" class="form-select form-select-sm">
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="25">25</option>
          <option value="50">50</option>
        </select>
        dòng mục
      </div>
      <div class="pagination">
        <button @click="changePage(0)" :disabled="totalItems === 0 || currentPage === 0">First</button>
        <button @click="changePage(currentPage - 1)" :disabled="totalItems === 0 || currentPage === 0">Prev</button>
        <button v-for="page in totalPagesArray" :key="page" @click="changePage(page)"
          :class="{ active: page === currentPage }">
          {{ page + 1 }}
        </button>
        <button @click="changePage(currentPage + 1)"
          :disabled="totalItems === 0 || currentPage === totalPages - 1">Next</button>
        <button @click="changePage(totalPages - 1)"
          :disabled="totalItems === 0 || currentPage === totalPages - 1">Last</button>
      </div>
      <div class="page-info">
        Hiển thị {{ displayedCurrentPage }}/{{ displayedTotalPages }} trang
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { Switch, notification, Modal } from 'ant-design-vue';
import Detailnhanvien from './detailnhanvien.vue';



export default {
  props: ['statusFilter'],
  components: { Detailnhanvien, 'a-switch': Switch },
  watch: {
    statusFilter() {
      console.log("Status Filter changed to:", this.statusFilter);
      this.fetchNhanViens();
    }
  },
  data() {
    return {
      showDetailModal: false,
      selectedEmployeeId: "",
      nhanviens: [],
      searchTerm: '',
      currentPage: 0,
      statusFilter: '',
      totalPages: 0,
      size: 5,
      totalItems: 0
    };
  },
  computed: {
    totalPagesArray() {
      if (this.totalItems === 0) {
        return [];
      }
      const range = [];
      const delta = 2;
      for (let i = Math.max(0, this.currentPage - delta); i <= Math.min(this.totalPages - 1, this.currentPage + delta); i++) {
        range.push(i);
      }
      return range;
    },
    displayedCurrentPage() {
      return this.totalItems > 0 ? this.currentPage + 1 : 0;
    },
    displayedTotalPages() {
      return this.totalItems > 0 ? this.totalPages : 0;
    }
  },
  created() {
    this.fetchNhanViens();
  },
  methods: {
    handleToggle(nv, checked) {
      this.selectedEmployeeId = nv.id;
      if (checked) {
        Modal.confirm({
          title: 'Bạn có chắc chắn muốn kích hoạt lại nhân viên này không?',
          okText: 'Xác nhận',
          cancelText: 'Hủy',
          onOk: () => this.activateEmployee()
        });
      } else {
        Modal.confirm({
          title: 'Bạn có chắc chắn muốn ngừng hoạt động nhân viên này không?',
          okText: 'Xác nhận',
          cancelText: 'Hủy',
          onOk: () => this.confirmDelete()
        });
      }
    },
    activateEmployee() {
      axios.get(`/api/admin/nhan-vien/update/${this.selectedEmployeeId}`)
        .then(response => {
          const nhanVien = response.data;
          nhanVien.trangThai = true;
          return axios.put(`/api/admin/nhan-vien/update/${this.selectedEmployeeId}`, nhanVien);
        })
        .then(() => {
          notification.success({
            message: 'Thành công',
            description: 'Đã kích hoạt lại nhân viên thành công! Trạng thái nhân viên đã được cập nhật'
          }); setTimeout(() => {
            this.isLoading = false;
            this.closeModal();
          }, 3000);
          this.fetchNhanViens();
        })
        .catch(error => {
          console.error(error);
          notification.error({
            message: 'Lỗi',
            description: 'Kích hoạt lại nhân viên thất bại! Đã xảy ra lỗi khi cập nhật trạng thái'
          }); setTimeout(() => {
            this.isLoading = false;
            this.closeModal();
          }, 3000);
        });
    },


    exportExcel() {
      axios.get('/api/admin/nhan-vien/export-excel', { responseType: 'blob' })
        .then(response => {
          const blob = new Blob([response.data], {
            type: response.headers['content-type'] || 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
          });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'NhanVien.xlsx');
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        })
        .catch(error => {
          console.error("Có lỗi khi xuất Excel:", error);
        });
    },
    formatDate(date) {
      if (!date) return '';
      return new Date(date).toLocaleDateString('vi-VN');
    },
    openUpdateModal(id) {
      this.$router.push(`/nhan-vien/update/${id}`);
    },
    detailNhanVien(id) {
      console.log('Button clicked, ID:', id);
      this.selectedEmployeeId = id;
      this.showDetailModal = true;
      console.log('Modal state:', this.showDetailModal);
    },
    async fetchNhanViens() {
      try {
        const params = new URLSearchParams({
          keyword: this.searchTerm,
          status: this.statusFilter,
          page: this.currentPage,
          size: this.size
        });
        const response = await axios.get(`/api/admin/nhan-vien/hien-thi-nhan-vien?${params}`
        );
        this.nhanviens = response.data.content;
        this.totalPages = response.data.page.totalPages;
        this.totalItems = response.data.totalElements;
        if (this.nhanviens.length < this.size) {
          const emptyRows = this.size - this.nhanviens.length;
          for (let i = 0; i < emptyRows; i++) {
            this.nhanviens.push({
              id: null,
              maNhanVien: "",
              hoVaTen: "",
              gioiTinh: null,
              ngaySinh: null,
              email: "",
              soDienThoai: "",
              diaChi: "",
              trangThai: null,
              vaiTro: { tenVaiTro: "" },
              _empty: true
            });
          }
        }
      } catch (error) {
        console.error('Error fetching employees:', error);
      }
    },


    searchNhanVien() {
      this.currentPage = 0;
      this.fetchNhanViens();
    },

    changePage(page) {
      if (this.totalItems === 0) return;
      if (page >= 0 && page < this.totalPages) {
        this.currentPage = page;
        this.fetchNhanViens();
      }
    }
    ,
    confirmDelete() {
      axios.delete(`/api/admin/nhan-vien/delete/${this.selectedEmployeeId}`)
        .then(() => {
          notification.success({
            message: 'Thành công',
            description: 'Đã ngừng hoạt động nhân viên thành công! Trạng thái nhân viên đã được cập nhật'
          }); setTimeout(() => {
            this.isLoading = false;
            this.closeModal();
          }, 3000);
          this.fetchNhanViens();
        })
        .catch(error => {
          console.error(error);
          notification.error({
            message: 'Lỗi',
            description: 'Ngừng hoạt động nhân viên thất bại! Đã xảy ra lỗi khi cập nhật trạng thái'
          });
        });
    }
  }
};
</script>

<style scoped>
.table-container {
  background-color: #f2f2f2;
  border-radius: 5px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
  min-height: 400px;
  position: relative;
}

.main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #fff;
  padding: 20px;
  position: relative;
  padding-bottom: 60px;
  height: 85vh;
}

.page-title {
  font-size: 3rem;
  margin-bottom: 20px;
  text-align: center;
}

.action-buttons {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.action-buttons .btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  font-size: 0.875rem;
}

.controls-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.entries-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.entries-control select {
  width: 70px;
  display: inline-block;
}

.search-control {
  margin-left: auto;
}

.search-control input {
  width: 200px;
}

.table {
  margin-bottom: 20px;
  min-height: 300px;
  width: 100%;
  border-collapse: collapse;
  background-color: #f9f9f9;
  height: 90px;
  table-layout: fixed;
}

.table thead th {
  border: 1px solid #ddd;
  white-space: nowrap;
  border: none;
  border-bottom: 1px solid #ddd;
}

.table tbody {
  min-height: 300px;
  display: table-row-group;
}

.table tbody td {
  height: 60px;
  max-height: 60px;
  vertical-align: middle;
  border: 1px solid #ddd;
  padding: 8px 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  border: none;
  border-bottom: 1px solid #ddd;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.action-icons {
  display: flex;
  gap: 5px;
}

.pagination-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-top: 20px;
  margin-top: auto;
  position: relative;
  bottom: 0;
  width: 100%;
  margin-bottom: 0;
  flex-shrink: 0;
  padding: 10px 20px;
  background: #fff;
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1);
}

.pagination {
  display: flex;
  gap: 5px;
}

.pagination button {
  padding: 5px 10px;
  border: 1px solid #dee2e6;
  background: #fff;
  cursor: pointer;
}

.pagination button.active {
  background: #007bff;
  color: #fff;
  border-color: #007bff;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.page-info {
  font-size: 0.875rem;
}

.loc-trangthai {
  display: inline-flex !important;
  align-items: center;
  margin: 0 !important;
  height: 100%;
}

.loc-trangthai .form-control {
  height: 31px;
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  margin-left: 8px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.custom-select {
  border-radius: 0.25rem;
  border: 1px solid #ced4da;
  background-color: #fff;
  min-width: 120px;
}

.custom-select {
  position: relative;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
  padding-right: 25px !important;
}

.select-wrapper {
  position: relative;
  display: inline-block;
}

.select-wrapper::after {
  content: '';
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  pointer-events: none;
}

.select-wrapper .bi-chevron-down {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}

.toggle-switch {
  position: relative;
  display: inline-block;
  vertical-align: middle;
  margin-right: 10px;
}

.toggle-switch input {
  display: none;
}

.toggle-switch label {
  display: block;
  width: 44px;
  height: 22px;
  border-radius: 11px;
  background-color: #e0e0e0;
  cursor: pointer;
  position: relative;
  transition: background-color 0.3s;
}

.toggle-switch label::after {
  content: '';
  position: absolute;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background-color: white;
  top: 2px;
  left: 2px;
  transition: left 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toggle-switch input:checked+label {
  background-color: #4CAF50;
}

.toggle-switch input:checked+label::after {
  left: 24px;
}

.table thead th:nth-child(1) {
  width: 5%;
}

.table thead th:nth-child(2) {
  width: 12%;
}

.table thead th:nth-child(3) {
  width: 15%;
}

.table thead th:nth-child(4) {
  width: 7%;
}

.table thead th:nth-child(5) {
  width: 10%;
}

.table thead th:nth-child(6) {
  width: 23%;
}

.table thead th:nth-child(7) {
  width: 12%;
}

.table thead th:nth-child(8) {
  width: 10%;
}

.table thead th:nth-child(9) {
  width: 12%;
}

.table thead th:nth-child(10) {
  width: 10%;
}

.table thead th:nth-child(11) {
  width: 6%;
}

.empty-row td {
  border: none !important;
  height: 0;
  padding: 0;
}
</style>