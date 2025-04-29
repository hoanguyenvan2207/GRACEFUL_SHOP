<template>
  <div class="modal-container">
    <div v-if="warningMessage" class="alert alert-warning">
      {{ warningMessage }}
    </div>
    <div class="modal-header">
      <h5 class="modal-title">Thông tin - {{ user.hoVaTen }}</h5>
    </div>
    <div class="modal-body">
      <div class="row">
        <div class="col-3">
          <ul class="nav flex-column nav-pills">
            <li class="nav-item">
              <a class="nav-link active" data-bs-toggle="pill" href="#chi-tiet">
                <i class="bi bi-info-circle"></i> Chi tiết
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" data-bs-toggle="pill" href="#doi-mat-khau">
                <i class="bi bi-key"></i> Đổi mật khẩu
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" data-bs-toggle="pill" href="#chi-tiet" @click.prevent="confirmLogout">
                <i class="bi bi-box-arrow-right"></i> Đăng xuất
              </a>
            </li>
            <li class="nav-item">
              <a class="nav-link" data-bs-toggle="pill" href="#chi-tiet" @click.prevent="showConfirmResetModal">
                <i class="bi bi-arrow-clockwise"></i> Reset mật khẩu
              </a>
            </li>
          </ul>
        </div>
        <div class="col-9 tab-content">
          <div id="chi-tiet" class="tab-pane fade show active">
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">Tên nhân viên:</label>
                <input type="text" v-model="user.hoVaTen" class="form-control" readonly />
              </div>
              <div class="col-md-6">
                <label class="form-label">Số điện thoại:</label>
                <input type="text" v-model="user.soDienThoai" class="form-control" @input="validateField('soDienThoai')"
                  placeholder="Nhập số điện thoại">
                <div class="error-wrapper">
                  <p v-if="errors.soDienThoai" class="error-message">{{ errors.soDienThoai }}</p>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Chức vụ:</label>
                <input type="text" v-model="user.vaiTro" class="form-control" readonly />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Mã:</label>
                <input type="text" v-model="user.maNhanVien" class="form-control" readonly />
              </div>
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Tên tài khoản:</label>
                <input type="text" v-model="user.tenDangNhap" class="form-control" readonly />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Email:</label>
                <input type="text" v-model="user.email" class="form-control" @input="validateEmail()"
                  placeholder="Nhập email">
                <div class="error-wrapper">
                  <p v-if="errors.email" class="error-message">{{ errors.email }}</p>
                </div>
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">Địa chỉ:</label>
                <input type="text" v-model="user.diaChi" class="form-control" @input="validateField('diaChi')"
                  placeholder="Nhập địa chỉ">
                <div class="error-wrapper">
                  <p v-if="errors.diaChi" class="error-message">{{ errors.diaChi }}</p>
                </div>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Ngày sinh:</label>
                <input type="text" v-model="user.ngaySinh" class="form-control" readonly />
              </div>
            </div>
            <div class="row mb-3">
              <div class="col-md-6">
                <label class="form-label">Trạng thái:</label>
                <div class="d-flex align-items-center gap-2">
                  <span :class="['badge', (user.trangThai === 'Đang làm việc') ? 'bg-success' : 'bg-danger']">
                    {{ user.trangThai }}
                  </span>
                </div>
              </div>
            </div>
            <div class="d-flex justify-content-end mt-4">
              <button type="button" class="btn btn-success btn-custom" @click="updateNhanVien">
                Lưu thay đổi
              </button>
            </div>
          </div>
          <div id="doi-mat-khau" class="tab-pane fade">
            <div class="mb-3">
              <label class="form-label">Mật khẩu cũ:</label>
              <div class="password-input-group">
                <input :type="isOldPasswordVisible ? 'text' : 'password'" v-model="user.oldPassword"
                  class="form-control" placeholder="Nhập mật khẩu cũ" />
                <span class="password-toggle" @click="togglePasswordVisibility('oldPassword')">
                  <i :class="isOldPasswordVisible ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
                </span>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Mật khẩu mới:</label>
              <div class="password-input-group">
                <input :type="isPasswordVisible ? 'text' : 'password'" v-model="user.password" class="form-control"
                  @input="validatePassword" placeholder="Mật khẩu mới" />
                <span class="password-toggle" @click="togglePasswordVisibility('password')">
                  <i :class="isPasswordVisible ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
                </span>
              </div>
              <div class="error-wrapper">
                <p v-if="passwordError" class="error-message">{{ passwordError }}</p>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Xác nhận mật khẩu mới:</label>
              <div class="password-input-group">
                <input :type="isConfirmPasswordVisible ? 'text' : 'password'" v-model="user.confirmPassword"
                  class="form-control" placeholder="Nhập lại mật khẩu" />
                <span class="password-toggle" @click="togglePasswordVisibility('confirmPassword')">
                  <i :class="isConfirmPasswordVisible ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
                </span>
              </div>
            </div>
            <div class="d-flex justify-content-end">
              <button type="button" class="btn btn-primary btn-custom" @click="changePassword">
                Lưu
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import axios from "axios";
import { notification, Modal, Switch } from 'ant-design-vue';
axios.defaults.baseURL = "http://localhost:8080";




export default {
  name: "RegistrationForm",
  data() {
    return {
      user: {
        idNhanVien: '',
        maNhanVien: "",
        hoVaTen: "",
        soDienThoai: "",
        diaChi: "",
        email: "",
        trangThai: "",
        oldPassword: "",
        password: "",
        confirmPassword: ""
      },
      isPasswordVisible: false,
      isOldPasswordVisible: false,
      isConfirmPasswordVisible: false,
      passwordError: "",
      warningMessage: "",
      errors: {},
      isLoading: false,
      originalPhone: "",
      originalEmail: "",
    };
  },
  created() {
    this.warningMessage = sessionStorage.getItem("warningMessage") || "";
    const storedUser = sessionStorage.getItem("user");
    if (storedUser) {
      this.user = JSON.parse(storedUser);
      if (!this.user.trangThai) {
        this.user.trangThai = "Đang làm việc";
      }
    }
  },


  methods: {
    validateField(field) {
      if (field === 'soDienThoai') {

        this.user.soDienThoai = this.user.soDienThoai.trim();
        if (!this.user.soDienThoai) {
          this.errors.soDienThoai = "Số điện thoại không được để trống!";
          return;
        }
        if (/\s/.test(this.user.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không được chứa khoảng trắng!";
          return;
        }
        if (/[^0-9]/.test(this.user.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không được chứa ký tự đặc biệt!";
          return;
        }
        if (this.user.soDienThoai.length !== 10) {
          this.errors.soDienThoai = "Số điện thoại phải có đúng 10 số!";
          return;
        }
        const phoneRegex = /^(0(3[2-9]|5[2-9]|7[0-9]|8[1-9]|9[0-9]))\d{7}$/;
        if (!phoneRegex.test(this.user.soDienThoai)) {
          this.errors.soDienThoai = "Số điện thoại không hợp lệ! (Phải thuộc đầu số hợp lệ của Việt Nam)";
          return;
        }
        this.errors.soDienThoai = "";
      }
      if (field === 'diaChi') {
        this.user.diaChi = this.user.diaChi.trimStart();
        if (!this.user.diaChi.trim()) {
          this.errors.diaChi = 'Địa chỉ không được để trống hoặc chỉ chứa khoảng trắng!';
        } else {
          const specialCharsRegex = /[@#\$%]/;
          if (specialCharsRegex.test(this.user.diaChi)) {
            this.errors.diaChi = 'Địa chỉ không được chứa các ký tự @, #, $, %!';
          } else {
            this.errors.diaChi = '';
          }
        }
      }
    },
    validateEmail() {
      this.user.email = this.user.email.trim();
      const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;


      if (!this.user.email) {
        this.errors.email = "Email không được để trống";
      } else if (/\s/.test(this.user.email)) {
        this.errors.email = "Email không được chứa khoảng trắng!";
      } else if (!emailPattern.test(this.user.email)) {
        this.errors.email = "Email phải chứa @";
      } else {
        this.errors.email = "";
      }
    },
    updateNhanVien() {
      this.validateField("soDienThoai");
      this.validateField("diaChi");
      this.validateEmail();


      if (this.errors.soDienThoai || this.errors.email || this.errors.diaChi) {
        return;
      }


      axios.put(`/api/nhan-vien/cap-nhat-thong-tin/${this.user.idNhanVien}`, {
        soDienThoai: this.user.soDienThoai,
        email: this.user.email,
        diaChi: this.user.diaChi
      })
        .then((response) => {
          notification.success({
            message: "Cập nhật thành công",
            description: "Thông tin nhân viên đã được lưu."
          });
        })
        .catch((error) => {
          notification.error({
            message: "Cập nhật thất bại",
            description: error.response?.data || "Đã xảy ra lỗi khi cập nhật."
          });
        });
    }
    ,
    logout() {
      sessionStorage.removeItem("user");
      sessionStorage.removeItem("hoVaTen");
      sessionStorage.removeItem("idNhanVien");
      sessionStorage.removeItem("roles");
      notification.success({
        message: "Thành công!",
        description: "Bạn đã đăng xuất thành công.",
        duration: 3
      });
      this.$router.push("/admin");
    }
    ,
    togglePasswordVisibility(field) {
      if (field === "password") {
        this.isPasswordVisible = !this.isPasswordVisible;
      } else if (field === "oldPassword") {
        this.isOldPasswordVisible = !this.isOldPasswordVisible;
      } else if (field === "confirmPassword") {
        this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
      }
    },
    validatePassword() {
      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
      if (!this.user.password) {
        this.passwordError = "";
      } else if (!passwordRegex.test(this.user.password)) {
        this.passwordError = "Mật khẩu phải ≥ 6 ký tự, có chữ hoa, chữ thường, số và ký tự đặc biệt!";
      } else {
        this.passwordError = "";
      }
    },
    async changePassword() {
      if (!this.user.oldPassword || !this.user.password || !this.user.confirmPassword) {
        notification.error({
          message: "Lỗi!",
          description: "Vui lòng nhập đủ Mật khẩu cũ, Mật khẩu mới và Xác nhận mật khẩu.",
          duration: 3
        });
        return;
      }
      if (this.user.password !== this.user.confirmPassword) {
        notification.error({
          message: "Lỗi!",
          description: "Mật khẩu xác nhận không khớp!"
        });
        return;
      }
      try {
        Modal.confirm({
          title: "Xác nhận đổi mật khẩu",
          content: "Bạn có chắc chắn muốn đổi mật khẩu?",
          okText: "Đồng ý",
          cancelText: "Hủy",
          onOk: async () => {
            await axios.post("/api/auth/change-password", {
              username: this.user.tenDangNhap,
              oldPassword: this.user.oldPassword,
              newPassword: this.user.password
            });
            notification.success({
              message: "Thành công!",
              description: "Đổi mật khẩu thành công!",
              duration: 3
            });
            sessionStorage.removeItem("warningMessage");
            this.warningMessage = "";
            this.user.oldPassword = "";
            this.user.password = "";
            this.user.confirmPassword = "";
            sessionStorage.clear();
            this.$router.push("/admin");
          }
        })
      } catch (error) {
        console.error("Lỗi khi đổi mật khẩu:", error);
        notification.error({
          message: "Lỗi!",
          description: "Đổi mật khẩu thất bại, vui lòng kiểm tra lại mật khẩu cũ!",
          duration: 3
        });
      }
    },
    confirmLogout() {
      Modal.confirm({
        title: "Xác nhận đăng xuất",
        content: "Bạn có chắc chắn muốn đăng xuất?",
        okText: "Đồng ý",
        cancelText: "Hủy",
        onOk: () => {
          this.logout();
        }
      });
    },
    showConfirmResetModal() {
      if (!this.user.tenDangNhap) {
        notification.error({
          message: "Lỗi!",
          description: "Không tìm thấy tài khoản để đặt lại mật khẩu."
        });
        return;
      }
      Modal.confirm({
        title: "Đặt lại mật khẩu",
        content: "Bạn có chắc chắn muốn đặt lại mật khẩu về mặc định?",
        okText: "Xác nhận",
        cancelText: "Hủy",
        onOk: () => {
          this.confirmResetPassword();
        }
      });
    },
    async confirmResetPassword() {
      try {
        await axios.post("/api/auth/reset-password", {
          username: this.user.tenDangNhap
        });
        notification.success({
          message: "Thành công!",
          description: "Mật khẩu đã được đặt lại về mặc định!"
        });
      } catch (error) {
        console.error("Lỗi khi đặt lại mật khẩu:", error);
        notification.error({
          message: "Lỗi!",
          description: "Đặt lại mật khẩu thất bại, vui lòng thử lại!"
        });
      }
    }
  }
};
</script>


<style scoped>
.modal-header,
.modal-footer {
  padding: 10px 15px;
  border-bottom: 1px solid #ddd;
}


.modal-footer {
  border-top: none;
  display: flex;
  justify-content: space-between;
}


.modal-title {
  margin: 0;
}

.password-input-group {
  position: relative;
  width: 100%;
}

.password-input-group input {
  width: 100%;
  padding-right: 35px;
}

.password-toggle {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
  color: #666;
}

.password-toggle:hover {
  color: #333;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 5px;
}

.modal-container {
  border: 1px solid #ccc;
  background: #fff;
  border-radius: 6px;
  width: 95%;
  min-height: 70vh;
  margin: 0;
  display: flex;
  flex-direction: column;
  position: relative;
}

.modal-body {
  padding: 10px;
}

.row {
  margin: 0;
  width: 100%;
}

.col-3,
.col-9 {
  padding: 15px;
}

.nav-pills {
  border-right: 1px solid #dee2e6;
  padding-right: 15px;
  height: 100%;
}

.btn-custom {
  border-radius: 20px;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
}


.btn-custom:hover {
  background-color: #0b5ed7;
  border-color: #0a58ca;
}

.error-wrapper {
  min-height: 20px;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 5px;
}
</style>