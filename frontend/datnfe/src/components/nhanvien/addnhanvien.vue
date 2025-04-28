<template>
  <div>
    <div class="modal-overlay" @click.self="closeModal">
      <div class="modal-container">
        <div class="modal-header">
          <h2 class="page-title">Thêm mới nhân viên</h2>
        </div>
        <div class="modal-body">
          <form @submit.prevent="confirmSubmit" class="add-form">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Họ Tên<span class="required">*</span></label>
                <input type="text" v-model="nhanVien.hoVaTen" class="form-control" @input="validateField('hoVaTen')"
                  placeholder="Vui lòng nhập họ và tên">
                <div class="error-wrapper">
                  <p v-if="errors.hoVaTen" class="error-message">{{ errors.hoVaTen }}</p>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Giới Tính<span class="required">*</span></label>
                <div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="gioiTinh" id="male" value="1"
                      v-model="nhanVien.gioiTinh" @change="validateField('gioiTinh')" />
                    <label class="form-check-label" for="male">Nam</label>
                  </div>
                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="gioiTinh" id="female" value="0"
                      v-model="nhanVien.gioiTinh" @change="validateField('gioiTinh')" />
                    <label class="form-check-label" for="female">Nữ</label>
                  </div>
                </div>
                <div class="error-wrapper">
                  <p v-if="errors.gioiTinh" class="error-message">{{ errors.gioiTinh }}</p>
                </div>
              </div>

            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Tên Đăng Nhập<span class="required">*</span></label>
                <input type="text" v-model="nhanVien.tenDangNhap" class="form-control"
                  @input="validateField('tenDangNhap')" placeholder="Vui lòng nhập tên đăng nhập">
                <div class="error-wrapper">
                  <p v-if="errors.tenDangNhap" class="error-message">{{ errors.tenDangNhap }}</p>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Ngày Sinh<span class="required">*</span></label>
                <input type="date" v-model="nhanVien.ngaySinh" class="form-control" @input="validateField('ngaySinh')">
                <div class="error-wrapper">
                  <p v-if="errors.ngaySinh" class="error-message">{{ errors.ngaySinh }}</p>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Email<span class="required">*</span></label>
                <input type="email" v-model="nhanVien.email" class="form-control"
                  @input="nhanVien.email = nhanVien.email.replace(/^\s+|\s+$/g, ''); validateEmail()"
                  placeholder="Vui lòng nhập email">
                <div class="error-wrapper">
                  <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Số Điện Thoại<span class="required">*</span></label>
                <input type="text" v-model="nhanVien.soDienThoai" class="form-control"
                  @input="validateField('soDienThoai')" placeholder="Vui lòng nhập số điện thoại">
                <div class="error-wrapper">
                  <p v-if="errors.soDienThoai" class="error-message">{{ errors.soDienThoai }}</p>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Địa Chỉ<span class="required">*</span></label>
                <input type="text" v-model="nhanVien.diaChi" class="form-control" @input="validateField('diaChi')"
                  placeholder="Vui lòng nhập địa chỉ">
                <div class="error-wrapper">
                  <p v-if="errors.diaChi" class="error-message">{{ errors.diaChi }}</p>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Chức Vụ<span class="required">*</span></label>
                <select v-model="nhanVien.vaiTro.id" class="form-select" @change="validateField('vaiTro')">
                  <option v-if="nhanVien.vaiTro.id === null" :value="null" disabled>Chọn chức vụ</option>
                  <option v-for="vaiTro in vaiTros" :key="vaiTro.id" :value="vaiTro.id">
                    {{ vaiTro.tenVaiTro }}
                  </option>
                </select>
                <div class="error-wrapper">
                  <p v-if="errors.vaiTro" class="error-message">{{ errors.vaiTro }}</p>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Trạng Thái<span class="required">*</span></label>
                <select v-model="nhanVien.trangThai" disabled class="form-select" @change="validateField('trangThai')">
                  <option value="true">Đang làm</option>
                  <option value="false">Nghỉ làm</option>
                </select>
              </div>
            </div>
            <div class="text-center mt-3">
              <button type="submit" class="btn btn-primary me-2" :disabled="isLoading">
                <span v-if="!isLoading">Lưu</span>
                <span v-else>
                  <span class="spinner"></span> Đang gửi...
                </span>
              </button>
              <button type="button" @click="closeModal" class="btn btn-secondary ms-2">Quay lại</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import axios from 'axios';
import { Modal, notification } from 'ant-design-vue';


export default {
  data() {
    return {
      showModal: false,
      nhanVien: {
        hoVaTen: '',
        tenDangNhap: '',
        gioiTinh: null,
        ngaySinh: '',
        email: '',
        soDienThoai: '',
        diaChi: '',
        vaiTro: { id: null },
        trangThai: true,
      },
      vaiTros: [],
      errors: {},
      isLoading: false,
    };
  },
  created() {
    this.fetchVaiTros();
  },
  methods: {
    confirmSubmit() {
      Modal.confirm({
        title: 'Xác nhận thêm nhân viên mới',
        content: 'Bạn có chắc chắn muốn thêm nhân viên này không?',
        okText: 'Xác nhận',
        cancelText: 'Hủy',
        onOk: () => {
          this.addNhanVien();
        }
      });
    }
    ,
    openModal() {
      this.showModal = true;
      document.body.style.overflow = 'hidden';
    },
    closeModal() {
      this.$emit('close');
      document.body.style.overflow = 'auto';
      this.$router.push('/nhan-vien/list');
      this.resetForm();
    },
    resetForm() {
      this.nhanVien = {
        hoVaTen: '',
        tenDangNhap: '',
        gioiTinh: null,
        ngaySinh: '',
        email: '',
        soDienThoai: '',
        diaChi: '',
        vaiTro: { id: null },
        trangThai: null,
      };
      this.errors = {};
    },
    validateEmail() {
      this.nhanVien.email = this.nhanVien.email.trim();
      const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;

      if (!this.nhanVien.email) {
        this.errors.email = "Email không được để trống";
      } else if (/\s/.test(this.nhanVien.email)) {
        this.errors.email = "Email không được chứa khoảng trắng!";
      } else if (!emailPattern.test(this.nhanVien.email)) {
        this.errors.email = "Email phải chứa @";
      } else {
        this.errors.email = "";
      }
    },
    checkUsername() {
      axios.get(`/api/admin/nhan-vien/check-username?username=${this.nhanVien.tenDangNhap}`)
        .then(response => {
          if (response.data.exists) {
            notification.error({
              message: 'Lỗi',
              description: 'Tên đăng nhập đã tồn tại! Vui lòng nhập tên đăng nhập khác.'
            });
            this.isLoading = false;
          } else {
            this.submitNhanVien();
          }
        })
        .catch(error => {
          console.error("Lỗi kiểm tra tên đăng nhập:", error);
          notification.error({
            message: 'Lỗi',
            description: 'Lỗi hệ thống khi kiểm tra tên đăng nhập!'
          });
          this.isLoading = false;
        });
    }
    ,
    validateField(field) {
      this.errors = { ...this.errors };
      if (field === 'hoVaTen') {
        this.nhanVien.hoVaTen = this.nhanVien.hoVaTen.trimStart();
        if (!this.nhanVien.hoVaTen.trim()) {
          this.errors.hoVaTen = 'Họ và Tên không được để trống hoặc chỉ chứa khoảng trắng!';
        } else if (!/^[\p{L}\s]+$/u.test(this.nhanVien.hoVaTen)) {
          this.errors.hoVaTen = 'Họ và Tên không được chứa số và ký tự đặc biệt!';
        } else {
          this.errors.hoVaTen = '';
        }
      }
      if (field === 'diaChi') {
        this.nhanVien.diaChi = this.nhanVien.diaChi.trimStart();
        if (!this.nhanVien.diaChi.trim()) {
          this.errors.diaChi = 'Địa chỉ không được để trống hoặc chỉ chứa khoảng trắng!';
        } else {
          const specialCharsRegex = /[@#\$%]/;
          if (specialCharsRegex.test(this.nhanVien.diaChi)) {
            this.errors.diaChi = 'Địa chỉ không được chứa các ký tự @, #, $, %!';
          } else {
            this.errors.diaChi = '';
          }
        }
      }
      if (field === 'tenDangNhap') {
        this.nhanVien.tenDangNhap = this.nhanVien.tenDangNhap.trimStart();
        if (!this.nhanVien.tenDangNhap.trim()) {
          this.errors.tenDangNhap = 'Tên đăng nhập không được để trống hoặc chỉ chứa khoảng trắng!';
        } else {
          this.errors.tenDangNhap = '';
        }
      }
      if (field === 'soDienThoai') {

        this.nhanVien.soDienThoai = this.nhanVien.soDienThoai.trim();
        if (!this.nhanVien.soDienThoai) {
          this.errors.soDienThoai = "Số điện thoại không được để trống!";
          return;
        }
        if (/\s/.test(this.nhanVien.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không được chứa khoảng trắng!";
          return;
        }
        if (/[^0-9]/.test(this.nhanVien.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không được chứa ký tự đặc biệt!";
          return;
        }
        if (this.nhanVien.soDienThoai.length !== 10) {
          this.errors.soDienThoai = "Số điện thoại phải có đúng 10 số!";
          return;
        }
        const phoneRegex = /^(0(3[2-9]|5[2-9]|7[0-9]|8[1-9]|9[0-9]))\d{7}$/;
        if (!phoneRegex.test(this.nhanVien.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không hợp lệ! (Phải thuộc đầu số hợp lệ của Việt Nam)";
          return;
        }
        this.errors.soDienThoai = "";
      }
      if (field === 'ngaySinh') {
        if (!this.nhanVien.ngaySinh) {
          this.errors.ngaySinh = "Vui lòng chọn ngày sinh!";
        } else {
          const birthDate = new Date(this.nhanVien.ngaySinh);
          const today = new Date();
          let age = today.getFullYear() - birthDate.getFullYear();
          const monthDifference = today.getMonth() - birthDate.getMonth();
          if (monthDifference < 0 || (monthDifference === 0 && today.getDate() < birthDate.getDate())) {
            age--;
          }
          if (age < 18) {
            this.errors.ngaySinh = "Nhân viên phải đủ 18 tuổi!";
          } else {
            this.errors.ngaySinh = "";
          }
        }
      }
      if (field === 'gioiTinh' && (this.nhanVien.gioiTinh === null || this.nhanVien.gioiTinh === "")) {
        this.errors.gioiTinh = "Vui lòng chọn giới tính!";
      } else if (field === 'gioiTinh') {
        this.errors.gioiTinh = "";
      }
      if (field === 'vaiTro' && (!this.nhanVien.vaiTro.id || this.nhanVien.vaiTro.id === "")) {
        this.errors.vaiTro = "Vui lòng chọn chức vụ!";
      } else if (field === 'vaiTro') {
        this.errors.vaiTro = "";
      }
      if (field === 'trangThai' && (this.nhanVien.trangThai === null || this.nhanVien.trangThai === "")) {
        this.errors.trangThai = "Vui lòng chọn trạng thái!";
      } else if (field === 'trangThai') {
        this.errors.trangThai = "";
      }
    },
    fetchVaiTros() {
      axios.get('/api/admin/nhan-vien/show-vai-tro', {
        withCredentials: true
      })
        .then(response => (this.vaiTros = response.data))
        .catch(error => console.error(error));
    },
    addNhanVien() {
      this.validateField('hoVaTen');
      this.validateField('soDienThoai');
      this.validateField('ngaySinh');
      this.validateField('tenDangNhap');
      this.validateField('diaChi');
      this.validateEmail();
      this.validateField('gioiTinh');
      this.validateField('vaiTro');
      this.validateField('trangThai');

      if (Object.values(this.errors).some(error => error !== "")) {
        return;
      }

      this.isLoading = true;

      // Kiểm tra số điện thoại
      axios.get(`/api/admin/nhan-vien/check-phone?phone=${this.nhanVien.soDienThoai}`)
        .then(response => {
          if (response.data.exists) {
            notification.error({
              message: 'Lỗi',
              description: 'Số điện thoại đã tồn tại! Vui lòng nhập số điện thoại khác.'
            });
            this.isLoading = false;
          } else {
            // Kiểm tra email
            axios.get(`/api/admin/nhan-vien/check-email?email=${this.nhanVien.email}`)
              .then(response => {
                if (response.data.exists) {
                  notification.error({
                    message: 'Lỗi',
                    description: 'Email đã tồn tại! Vui lòng nhập email khác.'
                  });
                  this.isLoading = false;
                } else {
                  // Kiểm tra tên đăng nhập
                  this.checkUsername();
                }
              })
              .catch(error => {
                console.error("Lỗi kiểm tra email:", error);
                notification.error({
                  message: 'Lỗi',
                  description: 'Lỗi hệ thống khi kiểm tra email!'
                });
                this.isLoading = false;
              });
          }
        })
        .catch(error => {
          console.error("Lỗi kiểm tra số điện thoại:", error);
          notification.error({
            message: 'Lỗi',
            description: 'Lỗi hệ thống khi kiểm tra số điện thoại!'
          });
          this.isLoading = false;
        });
    },

    submitNhanVien() {
      this.nhanVien.id = null;
      axios.post('/api/admin/nhan-vien/add', this.nhanVien)
        .then(response => {
          notification.success({
            message: 'Thành công!',
            description: 'Nhân viên đã được thêm thành công!'
          });
          setTimeout(() => {
            this.isLoading = false;
            this.closeModal();
          }, 3000);
        })
        .catch(error => {
          console.error("Lỗi thêm nhân viên:", error);
          let errorMsg = "Đã có lỗi xảy ra.";
          if (error.response && error.response.data && error.response.data.message) {
            errorMsg = error.response.data.message;
          }
          notification.error({
            message: 'Lỗi',
            description: errorMsg,
          });
          this.isLoading = false;
        });
    },
  },
};
</script>

<style scoped>
.required {
  color: red;
  margin-left: 3px;
}

.modal-container {
  background-color: rgb(243, 243, 243);
  border-radius: 8px;
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  margin: 10px auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.modal-body {
  padding: 2rem 3rem;
}

.add-form {
  width: 100%;
  margin: 0 auto;
}

.row {
  margin-bottom: 1rem;
}

.form-label {
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.form-control,
.form-select {
  padding: 0.4rem 0.6rem;
  height: calc(2.5rem + 2.5px);
  width: 100%;
}

.modal-header {
  padding: 1rem 2rem;
  border-bottom: 1px solid #cbd0d6;
}

.modal-header h2 {
  font-size: 1.5rem;
  margin: 0;
}

.modal-close {
  border: none;
  outline: none;
  background-color: transparent;
  font-size: 20px;
  cursor: pointer;
}

.modal-close:hover {
  color: red;
}

.page-title {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  text-align: center;
}

.btn.btn-primary.me-2,
.btn.btn-secondary.ms-2 {
  padding: 0.4rem 1rem;
  font-size: 0.9rem;
}

.spinner {
  display: inline-block;
  width: 1rem;
  height: 1rem;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 6px;
  vertical-align: middle;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

.form-check-input[type="radio"] {
  width: 1.2rem;
  height: 1.2rem;
  margin-right: 0.4rem;
}

.form-check-label {
  font-size: 1rem;
  margin-right: 1rem;
}

.error-wrapper {
  min-height: 20px;
}

.error-message {
  color: red;
  font-size: 14px;
  margin: 0;
}
</style>
