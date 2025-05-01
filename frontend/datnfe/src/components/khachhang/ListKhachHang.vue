<template>
  <div class="main-content">
    <div class="customer-container">
      <div class="header-section">
        <h2 class="page-title">Danh sách khách hàng</h2>
        <button @click="$router.push('/khach-hang/add')" class="add-button">
          <i class="bi bi-plus-circle me-2"></i>Thêm khách hàng
        </button>
      </div>

      <div class="search-section">
        <div class="search-wrapper">
          <i class="bi bi-search search-icon"></i>
          <input v-model="searchTerm" @keyup="handleSearch" class="search-input"
            placeholder="Tìm kiếm theo mã, tên, email, số điện thoại" />
        </div>
      </div>

      <div class="table-pagination-wrapper">
        <div class="table-responsive">
          <table class="customer-table">
            <thead>
              <tr>
                <th>STT</th>
                <th>Mã KH</th>
                <th>Họ và tên</th>
                <th>Giới tính</th>
                <th>Ngày sinh</th>
                <th>Email</th>
                <th>SDT</th>
                <th>Địa chỉ mặc định</th>
                <th>Ngày tạo</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(kh, index) in khachHangs" :key="kh.id">
                <td>{{ currentPage * pageSize + index + 1 }}</td>
                <td>{{ kh.maKhachHang }}</td>
                <td class="fw-medium">{{ kh.hoTen }}</td>
                <td>
                  <span :class="kh.gioiTinh ? 'male-badge' : 'female-badge'">
                    {{ kh.gioiTinh ? "Nam" : "Nữ" }}
                  </span>
                </td>
                <td>{{ kh.ngaySinh }}</td>
                <td>{{ kh.email }}</td>
                <td>{{ kh.soDienThoai }}</td>
                <td class="address-cell">
                  {{ formatDefaultAddress(kh.diaChis) }}
                </td>
                <td>{{ kh.ngayTao }}</td>
                <td>
                  <div class="action-buttons">
                    <button @click="editKhachHang(kh.id)" class="edit-btn" title="Chỉnh sửa">
                      <i class="bi bi-pen"></i>
                    </button>
                    <button @click="openDrawerForKhachHang(kh.id)" class="view-btn" title="Xem chi tiết">
                      <i class="bi bi-eye"></i>
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="khachHangs.length === 0">
                <td colspan="10" class="text-center">Kết quả tìm kiếm "Không có"</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination-section">
          <nav aria-label="Page navigation">
            <ul class="pagination">
              <li :class="['page-item', { disabled: currentPage === 0 }]">
                <button class="page-btn" @click="diToiTrangDau">
                  <i class="bi bi-chevron-double-left"></i>
                </button>
              </li>
              <li :class="['page-item', { disabled: currentPage === 0 }]">
                <button class="page-btn" @click="diToiTrangTruoc">
                  <i class="bi bi-chevron-left"></i>
                </button>
              </li>
              <li class="page-item active">
                <span class="page-info">
                  <template v-if="totalPages > 0">
                    {{ currentPage + 1 }} / {{ totalPages }}
                  </template>
                  <template v-else>
                    0 / 0
                  </template>
                </span>
              </li>
              <li :class="[
                'page-item',
                { disabled: currentPage >= totalPages - 1 },
              ]">
                <button class="page-btn" @click="diToiTrangSau">
                  <i class="bi bi-chevron-right"></i>
                </button>
              </li>
              <li :class="[
                'page-item',
                { disabled: currentPage >= totalPages - 1 },
              ]">
                <button class="page-btn" @click="diToiTrangCuoi">
                  <i class="bi bi-chevron-double-right"></i>
                </button>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
    <!-- Drawer Component -->
    <KhachHangDrawer :is-open="isDrawerOpen" ref="khachHangDrawer" @close="closeDrawer" />
  </div>
</template>

<script>
import KhachHangService from "../../services/KhachHangService";
import KhachHangDrawer from "./KhachHangDrawer.vue";

export default {
  components: { KhachHangDrawer },
  data() {
    return {
      khachHangs: [],
      searchTerm: "",
      currentPage: 0,
      totalPages: 0,
      pageSize: 5,
      searchTimeout: null,
      isDrawerOpen: false,
    };
  },
  created() {
    this.getAllKhachHangs();
  },
  methods: {
    getAllKhachHangs() {
      const searchParams = {
        keyword: this.searchTerm,
        page: this.currentPage,
        size: this.pageSize,
      };
      KhachHangService.getAllKhachHang(searchParams)
        .then((response) => {
          this.khachHangs = response.data.content.sort((a, b) => {
            return new Date(b.ngayTao) - new Date(a.ngayTao);
          });
          if (
            response.data.page &&
            response.data.page.totalPages !== undefined
          ) {
            this.totalPages = response.data.page.totalPages;
          } else if (response.data.totalPages !== undefined) {
            this.totalPages = response.data.totalPages;
          } else {
            this.totalPages = 1;
          }
        })
        .catch((error) => console.error(error));
    },
    editKhachHang(id) {
      this.$router.push(`/khach-hang/update/${id}`);
    },
    handleSearch() {
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout);
      }
      this.searchTimeout = setTimeout(() => {
        this.currentPage = 0;
        this.getAllKhachHangs();
      }, 300);
    },
    changePage(page) {
      if (page >= 0 && page < this.totalPages) {
        this.currentPage = page;
        this.getAllKhachHangs();
      }
    },
    diToiTrangDau() {
      this.currentPage = 0;
      this.getAllKhachHangs();
    },
    diToiTrangTruoc() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.getAllKhachHangs();
      }
    },
    diToiTrangSau() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.getAllKhachHangs();
      }
    },
    diToiTrangCuoi() {
      this.currentPage = this.totalPages - 1;
      this.getAllKhachHangs();
    },
    openDrawerForKhachHang(id) {
      this.isDrawerOpen = true;
      this.$nextTick(() => {
        KhachHangService.getKhachHangById(id)
          .then((response) => {
            console.log("API Response:", response.data);
            this.$refs.khachHangDrawer.khachHang = response.data;
            this.$refs.khachHangDrawer.khachHang.diaChi_duong =
              response.data.duong || "Không có thông tin";
            this.$refs.khachHangDrawer.khachHang.diaChi_xaPhuong =
              response.data.xaPhuong || "Không có thông tin";
            this.$refs.khachHangDrawer.khachHang.diaChi_quan =
              response.data.quanHuyen || "Không có thông tin";
            this.$refs.khachHangDrawer.khachHang.diaChi_thanhPho =
              response.data.tinhThanhPho || "Không có thông tin";
          })
          .catch((error) => {
            console.error(error);
            alert("Không thể lấy dữ liệu khách hàng!");
          });
      });
    },
    closeDrawer() {
      this.isDrawerOpen = false;
    },
    formatDefaultAddress(diaChis) {
      if (!diaChis || !Array.isArray(diaChis) || diaChis.length === 0) {
        return "Không có địa chỉ";
      }
      const defaultAddr = diaChis.find((dc) => dc.macDinh);
      const otherAddresses = diaChis.filter((dc) => !dc.macDinh);
      const formatAddress = (addr) =>
        `${addr.duong}, ${addr.xaPhuong}, ${addr.quanHuyen}, ${addr.tinhThanhPho}`;
      let addresses = [];
      if (defaultAddr) {
        addresses.push(formatAddress(defaultAddr) + " (Mặc định)");
      }
      if (otherAddresses.length > 0) {
        addresses = addresses.concat(otherAddresses.map(formatAddress));
      }
      return addresses.join(" | ");
    },
  },
};
</script>

<style scoped>
.main-content {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.customer-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  color: #2c3e50;
  margin: 0;
  font-weight: 600;
}

.add-button {
  background-color: #0d6efd;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.add-button:hover {
  background-color: #0b5ed7;
  transform: translateY(-1px);
}

.search-section {
  margin-bottom: 24px;
}

.search-wrapper {
  position: relative;
  max-width: 600px;
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #6c757d;
}

.search-input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #0d6efd;
  box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.25);
}

.table-pagination-wrapper {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  min-height: 400px;
}

.table-responsive {
  flex-grow: 1;
  overflow-x: auto;
}

.customer-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  margin-bottom: 24px;
}

.customer-table th,
.customer-table td {
  padding: 8px 12px;
  text-align: center;
  border-bottom: 1px solid #dee2e6;
  font-size: 14px;
}

.customer-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #495057;
}

.customer-table tbody tr:hover {
  background-color: #f8f9fa;
}

.male-badge,
.female-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.male-badge {
  background-color: #e3f2fd;
  color: #0d6efd;
}

.female-badge {
  background-color: #fff0f6;
  color: #e83e8c;
}

.address-cell {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.edit-btn,
.view-btn {
  border: none;
  padding: 6px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.edit-btn {
  background-color: #ffc107;
  color: #000;
}

.view-btn {
  background-color: #198754;
  color: white;
}

.edit-btn:hover,
.view-btn:hover {
  transform: translateY(-1px);
  filter: brightness(110%);
}

.pagination-section {
  margin-top: auto;
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.pagination {
  display: flex;
  gap: 8px;
  align-items: center;
  margin: 0;
  padding: 0;
  list-style: none;
}

.page-btn {
  background-color: white;
  border: 1px solid #dee2e6;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background-color: #e9ecef;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  background-color: #0d6efd;
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  display: inline-block;
}
</style>