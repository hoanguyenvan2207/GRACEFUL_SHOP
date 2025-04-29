<template>
  <div class="update-employee-page">
    <div class="modal-header">
      <h2 class="page-title">Cập nhật nhân viên</h2>
    </div>
    <form @submit.prevent="updateNhanVien" class="update-form">
      <div class="row">
        <div class="col-md-6">
          <label class="form-label">Mã Nhân Viên<span class="required">*</span></label>
          <input type="text" v-model="nhanVien.maNhanVien" class="form-control" readonly>
        </div>
        <div class="col-md-6 mb-3">
          <label class="form-label">Giới Tính<span class="required">*</span></label>
          <div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="gioiTinh" id="male" value="1"
                v-model="nhanVien.gioiTinh" @change="validateField('gioiTinh')" disabled />
              <label class="form-check-label" for="male">Nam</label>
            </div>
            <div class="form-check form-check-inline">
              <input class="form-check-input" type="radio" name="gioiTinh" id="female" value="0"
                v-model="nhanVien.gioiTinh" @change="validateField('gioiTinh')" disabled />
              <label class="form-check-label" for="female">Nữ</label>
            </div>
          </div>
          <div class="error-wrapper">
            <p v-if="errors.gioiTinh" class="error-message">{{ errors.gioiTinh }}</p>
          </div>
        </div>

      </div>

      <div class="row">
        <div class="col-md-6 mb-2">
          <label class="form-label">Họ Tên<span class="required">*</span></label>
          <input type="text" v-model="nhanVien.hoVaTen" class="form-control" @input="validateField('hoVaTen')"
            placeholder="Nhập họ và tên" readonly>
          <div class="error-wrapper">
            <p v-if="errors.hoVaTen" class="error-message">{{ errors.hoVaTen }}</p>
          </div>
        </div>
        <div class="col-md-6 mb-2">
          <label class="form-label">Tên Đăng Nhập<span class="required">*</span></label>
          <input type="text" v-model="nhanVien.tenDangNhap" class="form-control" @input="validateField('tenDangNhap')"
            placeholder="Nhập tên đăng nhập" readonly>
          <div class="error-wrapper">
            <p v-if="errors.tenDangNhap" class="error-message">{{ errors.tenDangNhap }}</p>
          </div>
        </div>
      </div>

      <div class="row">

        <div class="col-md-6 mb-2">
          <label class="form-label">Ngày Sinh<span class="required">*</span></label>
          <input type="date" v-model="nhanVien.ngaySinh" class="form-control" @input="validateField('ngaySinh')"
            readonly>
          <div class="error-wrapper">
            <p v-if="errors.ngaySinh" class="error-message">{{ errors.ngaySinh }}</p>
          </div>
        </div>
        <div class="col-md-6 mb-2">
          <label class="form-label">Email<span class="required">*</span></label>
          <input type="email" v-model="nhanVien.email" class="form-control" @input="validateEmail()"
            placeholder="Nhập email" readonly>
          <div class="error-wrapper">
            <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
          </div>
        </div>
      </div>

      <div class="row">

        <div class="col-md-6 mb-2">
          <label class="form-label">Số Điện Thoại<span class="required">*</span></label>
          <input type="text" v-model="nhanVien.soDienThoai" class="form-control" @input="validateField('soDienThoai')"
            placeholder="Nhập số điện thoại" readonly>
          <div class="error-wrapper">
            <p v-if="errors.soDienThoai" class="error-message">{{ errors.soDienThoai }}</p>
          </div>
        </div>
        <div class="col-md-6 mb-2">
          <label class="form-label">Địa Chỉ<span class="required">*</span></label>
          <input type="text" v-model="nhanVien.diaChi" class="form-control" @input="validateField('diaChi')"
            placeholder="Nhập địa chỉ" readonly>
          <div class="error-wrapper">
            <p v-if="errors.diaChi" class="error-message">{{ errors.diaChi }}</p>
          </div>
        </div>
      </div>

      <div class="row">

        <div class="col-md-6 mb-2">
          <label class="form-label">Chức Vụ<span class="required">*</span></label>
          <select v-model="nhanVien.vaiTro.id" class="form-select" @change="validateField('vaiTro')">
            <option :value="null" disabled>Chọn chức vụ</option>
            <option v-for="role in vaiTros" :key="role.id" :value="role.id">
              {{ role.tenVaiTro }}
            </option>
          </select>
          <div class="error-wrapper">
            <p v-if="errors.vaiTro" class="error-message">{{ errors.vaiTro }}</p>
          </div>
        </div>
        <div class="col-md-6 d-flex align-items-center">
          <label class="form-label me-3">Trạng Thái<span class="required">*</span></label>
          <a-switch :checked="nhanVien.trangThai" checkedChildren="Đang làm" unCheckedChildren="Nghỉ làm"
            @change="val => { nhanVien.trangThai = val; validateField('trangThai'); }" />
          <div class="error-wrapper ms-3">
            <p v-if="errors.trangThai" class="error-message">{{ errors.trangThai }}</p>
          </div>
        </div>
      </div>
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <button type="button" @click="goBack" class="btn btn-secondary">Quay lại</button>
      </div>
    </form>
  </div>
</template>

<script>
import axios from 'axios';
import { notification, Modal, Switch } from 'ant-design-vue';


export default {
  components: {
    'a-switch': Switch
  },
  name: 'UpdateNhanVien',
  data() {
    return {
      nhanVien: {
        id: '',
        maNhanVien: '',
        hoVaTen: '',
        tenDangNhap: '',
        matKhau: '',
        gioiTinh: null,
        ngaySinh: '',
        email: '',
        soDienThoai: '',
        diaChi: '',
        vaiTro: { id: null },
        trangThai: null,
      },
      originalEmail: '',
      originalPhone: '',
      vaiTros: [],
      errors: {},
    };
  },
  created() {
    this.fetchEmployeeData();
    this.fetchVaiTros();
  },
  methods: {
    fetchEmployeeData() {
      const id = this.$route.params.id;
      const storedNhanVien = localStorage.getItem(`nhanVien_${id}`);
      if (storedNhanVien) {
        this.nhanVien = JSON.parse(storedNhanVien);
        return;
      }
      axios.get(`/api/nhan-vien/update/${id}`)
        .then(response => {
          this.nhanVien = response.data;
          this.originalEmail = this.nhanVien.email;
          this.originalPhone = this.nhanVien.soDienThoai;
          sessionStorage.setItem(`nhanVien_${id}`, JSON.stringify(this.nhanVien));
        })
        .catch(error => console.error("Lỗi khi lấy dữ liệu nhân viên:", error));
    },
    fetchVaiTros() {
      axios.get('/api/nhan-vien/show-vai-tro')
        .then(response => {
          this.vaiTros = response.data;
        })
        .catch(error => console.error("Lỗi khi lấy vai trò:", error));
    },
    validateField(field) {
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
    updateNhanVien() {
      this.validateField('vaiTro');
      this.validateField('trangThai');

      if (Object.values(this.errors).some(error => error !== "")) {
        return;
      }

      Modal.confirm({
        title: 'Xác nhận cập nhật',
        content: 'Bạn có chắc chắn muốn cập nhật thông tin nhân viên này?',
        okText: 'Xác nhận',
        cancelText: 'Hủy',
        onOk: () => {
          axios.put(`/api/nhan-vien/update/${this.nhanVien.id}`, this.nhanVien)
            .then(() => {
              sessionStorage.setItem(`nhanVien_${this.nhanVien.id}`, JSON.stringify(this.nhanVien));
              setTimeout(() => {
                this.$router.push('/nhan-vien/list');
                notification.success({
                  message: 'Thành công!',
                  description: 'Thông tin nhân viên đã được cập nhật!'
                });
              }, 3000);

            })
            .catch(error => {
              console.error("Lỗi khi cập nhật nhân viên:", error);
              notification.error({
                message: 'Lỗi',
                description: 'Có lỗi khi cập nhật thông tin nhân viên!'
              });
            });
        }
      });
    }
    ,
    // updateNhanVien() {
    //   this.validateField('vaiTro');
    //   this.validateField('trangThai');

    //   if (Object.values(this.errors).some(error => error !== "")) {
    //     return;
    //   }
    //       axios.put(`/api/nhan-vien/update/${this.nhanVien.id}`, this.nhanVien)
    //         .then(() => {
    //           notification.success({
    //             message: 'Thành công!',
    //             description: 'Thông tin nhân viên đã được cập nhật!'
    //           });
    //           sessionStorage.setItem(`nhanVien_${this.nhanVien.id}`, JSON.stringify(this.nhanVien)); // Cập nhật dữ liệu
    //           setTimeout(() => {
    //             this.$router.push('/nhan-vien/list');
    //           }, 3000);
    //         })
    //         .catch(error => {
    //           console.error("Lỗi khi cập nhật nhân viên:", error);
    //           notification.error({
    //             message: 'Lỗi',
    //             description: 'Có lỗi khi cập nhật thông tin nhân viên!'
    //           });
    //         });
    // },
    goBack() {
      this.$router.push('/nhan-vien/list');
    },
  }
};
</script>

<style scoped>
.update-employee-page {
  max-width: 1200px;
  margin: 1rem auto;
  padding: 2rem;
  background: #ecebeb;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  text-align: center;
}

.update-form {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.row {
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
}

.col-md-6 {
  width: 48%;
}

.form-label {
  font-weight: 500;
  margin-bottom: 0.25rem;
  display: block;
}

.form-control,
.form-select {
  padding: 0.5rem 0.75rem;
  height: calc(2.2rem + 2px);
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.9rem;
  box-sizing: border-box;
}

.required {
  color: red;
  margin-left: 3px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 1rem;
}

.btn {
  padding: 0.5rem 1.5rem;
  font-size: 1rem;
  text-align: center;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 1rem 2rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-header h2 {
  flex: 1;
  text-align: center;
  margin: 0;
}

.modal-close {
  position: absolute;
  right: 1rem;
  top: 1rem;
  border: none;
  background: transparent;
  font-size: 1.25rem;
  cursor: pointer;
}

.modal-close:hover {
  color: red;
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