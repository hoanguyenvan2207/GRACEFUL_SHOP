<template>
  <div class="login-container">
    <div class="login-content">
      <div class="left-panel">
        <img src="https://accounts.haravan.com/img/login_banner.svg" alt="Illustration" class="illustration-image" />
      </div>
      <div class="right-panel">
        <div class="powered-by">
          <span>Powered by</span>
          <img src="https://res.cloudinary.com/defcm50t7/image/upload/v1740129073/logo_graceful_lpd32u.png"
            alt="Haravan Logo" class="haravan-logo" />
        </div>

        <div class="border px-5 pb-5 rounded shadow-sm bg-white" style="margin-top: 100px;">
          <div class="header-info text-center ">
            <h1 class="title">Graceful - Đăng nhập</h1>
            <p class="top-text">
              Xin chào, vui lòng nhập thông tin đăng nhập
            </p>
          </div>

          <div class="form-container">
            <form @submit.prevent="handleLogin">
              <div class="form-group">
                <label for="username">Tên đăng nhập</label>
                <input id="username" type="text" placeholder="Nhập tên đăng nhập" class="form-input"
                  v-model="username" />
              </div>

              <div class="form-group">
                <label for="password">Mật khẩu</label>
                <div class="password-wrapper">
                  <input id="password" :type="showPassword ? 'text' : 'password'" placeholder="●●●●●●●"
                    class="form-input" v-model="password" />
                  <span class="password-toggle" @click="togglePassword">
                    <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                  </span>
                </div>
                <div class="forgot-password">
                  <a href="#" class="forgot-password-btn" @click.prevent="showForgotPasswordForm = true">
                    Quên mật khẩu?
                  </a>
                </div>
              </div>

              <button type="submit" class="btn-login">Đăng nhập</button>
            </form>
          </div>
        </div>
        <div v-if="errorMessage" class="alert alert-danger">
          {{ errorMessage }}
        </div>
        <div v-if="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>
        <div v-if="warningMessage" class="alert alert-warning">
          {{ warningMessage }}
        </div>
      </div>
    </div>
    <a-modal v-model:open="showForgotPasswordForm" title="Quên Mật Khẩu" :footer="null" centered>
      <form @submit.prevent="handleForgotPassword">
        <a-form-item label="Nhập Email của bạn">
          <a-input v-model:value="forgotEmail" type="text" placeholder="Nhập email của bạn" />
        </a-form-item>
        <div style="display: flex; justify-content: flex-end; gap: 10px; margin-top: 16px;">
          <a-button type="primary" html-type="submit" :loading="isSendingForgotPassword">
            <template v-if="!isSendingForgotPassword">Gửi yêu cầu</template>

          </a-button>
          <a-button @click="showForgotPasswordForm = false" danger>
            Quay lại
          </a-button>
        </div>
        <div v-if="message" class="alert" style="margin-top: 16px;">
          {{ message }}
        </div>
      </form>
    </a-modal>
  </div>
</template>

<script>
import axios from 'axios';
import {
  Modal,
  Input,
  Button,
  Form
} from 'ant-design-vue';
import AuthService from '../../services/AuthService';

export default {
  components: {
    'a-modal': Modal,
    'a-input': Input,
    'a-button': Button,
    'a-form-item': Form.Item,
  },
  data() {
    return {
      username: '',
      password: '',
      errorMessage: '',
      successMessage: '',
      warningMessage: '',
      showPassword: false,
      showForgotPasswordForm: false,
      forgotEmail: '',
      message: '',
      messageType: '',
      isSendingForgotPassword: false,
      language: 'vi'
    };
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    async handleLogin() {
      // Reset messages
      this.errorMessage = '';
      this.successMessage = '';
      this.warningMessage = '';

      // Validate inputs
      if (!this.username.trim() || !this.password.trim()) {
        this.errorMessage = 'Vui lòng nhập đầy đủ thông tin đăng nhập';
        return;
      }

      const usernameRegex = /^[A-Za-z0-9]+$/;
      if (!usernameRegex.test(this.username)) {
        this.errorMessage = 'Tên đăng nhập chỉ được chứa chữ cái và số';
        return;
      }

      try {
        await AuthService.logout();
        sessionStorage.removeItem("customerInfo");
        sessionStorage.removeItem("userId");
        const response = await axios.post('/api/auth/login', {
          tenDangNhap: this.username,
          matKhau: this.password
        },
          { withCredentials: true }
        );
        sessionStorage.setItem("user", JSON.stringify(response.data));
        const { hoVaTen, vaiTro, mat_khau_tam_thoi, idNhanVien } = response.data;
        this.successMessage = `Chúc mừng ${hoVaTen} đã đăng nhập thành công với vai trò ${vaiTro}`;

        if (mat_khau_tam_thoi === 0) {
          this.warningMessage = "Bạn đang sử dụng mật khẩu tạm thời, vui lòng đổi mật khẩu mạnh!";
          sessionStorage.setItem("warningMessage", this.warningMessage);
        } else {
          sessionStorage.removeItem("warningMessage");
        }

        sessionStorage.setItem('hoVaTen', hoVaTen);
        sessionStorage.setItem('idNhanVien', idNhanVien);
        if (this.$store) {
          await this.$store.dispatch('fetchUserInfo', { username: response.data.tenDangNhap });
        }

        setTimeout(() => {
          this.$emit('login-success');
          this.$router.push('/thong-ke');
        }, 1000);

      } catch (error) {
        if (error.response) {
          const msg = error.response.data;
          if (msg.includes("Tài khoản này đã nghỉ việc")) {
            this.errorMessage = "Tài khoản đã bị khóa. Vui lòng liên hệ quản trị viên!";
          }
          else if (msg.includes("Tên đăng nhập không tồn tại") ||
            msg.includes("Mật khẩu không chính xác")) {
            this.errorMessage = "Tên đăng nhập hoặc mật khẩu không chính xác!";
          }
          else {
            this.errorMessage = msg;
          }
        } else {
          this.errorMessage = "Lỗi kết nối máy chủ. Vui lòng thử lại sau.";
        }
      }
    },
    async handleForgotPassword() {
      this.message = '';
      this.messageType = '';

      if (!this.forgotEmail.trim()) {
        this.message = 'Vui lòng nhập email của bạn';
        this.messageType = 'alert-danger';
        return;
      }

      const strictEmailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      if (!strictEmailRegex.test(this.forgotEmail)) {
        this.message = 'Email không hợp lệ (không chứa khoảng trắng hay ký tự đặc biệt)';
        this.messageType = 'alert-danger';
        return;
      }

      this.isSendingForgotPassword = true;

      try {
        const response = await axios.post(
          '/api/forgot-password',
          { email: this.forgotEmail }
        );

        this.message = response.data;
        this.messageType = 'alert-success';
        this.forgotEmail = '';

        setTimeout(() => {
          this.showForgotPasswordForm = false;
          this.message = '';
        }, 3000);
      } catch (error) {
        this.message = error.response?.data || 'Có lỗi xảy ra, vui lòng thử lại sau.';
        this.messageType = 'alert-danger';
      } finally {
        this.isSendingForgotPassword = false;
      }
    },
    redirectToRegister() {
      if (this.$router) {
        this.$router.push('/register');
      }
    },
    setLanguage(lang) {
      this.language = lang;
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background-color: #f5f5f5;
  font-family: 'Arial', sans-serif;
}

.login-content {
  display: flex;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

@media (min-width: 992px) {
  .left-panel {
    display: block;
  }
}

.left-panel {
  flex: 1;
  background-color: rgb(234, 240, 250);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 200px;
}


.illustration-image {
  width: 60%;
  height: auto;
  object-fit: contain;
}


.right-panel {
  flex: 1;
  padding: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  position: relative;
  overflow-y: auto;
}

.powered-by {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #777;
}

.haravan-logo {
  height: 24px;
  width: auto;
}

.header-info {
  margin-top: 60px;
  margin-bottom: 30px;
}

.title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}

.form-container {
  max-width: 400px;
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #555;
}

.form-input {
  width: 100%;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 12px;
  color: #333;
  font-size: 14px;
}

.form-input:focus {
  outline: none;
  border-color: #2680eb;
  box-shadow: 0 0 0 2px rgba(38, 128, 235, 0.2);
}

.form-input::placeholder {
  color: #aaa;
}

.password-wrapper {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #666;
  font-size: 16px;
}

.forgot-password {
  text-align: right;
  margin-top: 8px;
}

.forgot-password-btn {
  color: #2680eb;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
}

.forgot-password-btn:hover {
  text-decoration: underline;
}

.btn-login {
  background-color: #2680eb;
  color: white;
  width: 100%;
  padding: 12px;
  margin-top: 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  font-size: 16px;
  transition: background-color 0.2s;
}

.btn-login:hover {
  background-color: #1a73e8;
}

.separator {
  display: flex;
  align-items: center;
  text-align: center;
  margin: 24px 0;
}

.separator::before,
.separator::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #ddd;
}

.separator span {
  padding: 0 10px;
  color: #777;
  font-size: 14px;
}

.btn-google {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  color: #555;
  transition: background-color 0.2s;
}

.btn-google:hover {
  background-color: #f8f8f8;
}

.google-icon {
  width: 18px;
  height: 18px;
  margin-right: 10px;
}

.register-link {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #555;
}

.register-link a {
  color: #2680eb;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.language-selector {
  display: flex;
  justify-content: center;
  margin-top: 40px;
  gap: 12px;
}

.lang-btn {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 14px;
  color: #555;
}

.lang-btn:hover {
  color: #2680eb;
}

.lang-icon {
  width: 16px;
  height: 12px;
  margin-right: 6px;
}

.privacy-notice {
  margin-top: 20px;
  font-size: 12px;
  color: #777;
  text-align: center;
}

.privacy-notice a {
  color: #2680eb;
  text-decoration: none;
}

.privacy-notice a:hover {
  text-decoration: underline;
}

.alert {
  padding: 12px;
  border-radius: 4px;
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}

.alert-danger {
  background: rgba(220, 38, 38, 0.1);
  color: #dc2626;
  border: 1px solid rgba(220, 38, 38, 0.2);
}

.alert-success {
  background: rgba(22, 163, 74, 0.1);
  color: #16a34a;
  border: 1px solid rgba(22, 163, 74, 0.2);
}

.alert-warning {
  background: rgba(234, 179, 8, 0.1);
  color: #ca8a04;
  border: 1px solid rgba(234, 179, 8, 0.2);
}

/* Modal styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 24px;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}

.modal-content h2 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.modal-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-top: 8px;
  font-size: 14px;
}

.button-group {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-submit,
.btn-close {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  text-align: center;
  font-size: 14px;
}

.btn-submit {
  background: #2680eb;
  color: white;
}

.btn-submit:hover {
  background: #1a73e8;
}

.btn-submit:disabled {
  background: #94bff3;
  cursor: not-allowed;
}

.btn-close {
  background: #f3f4f6;
  color: #333;
  border: 1px solid #ddd;
}

.btn-close:hover {
  background: #e5e7eb;
}

@media (max-width: 768px) {
  .right-panel {
    padding: 24px;
  }

  .header-info {
    margin-top: 40px;
  }

  .form-container {
    max-width: 100%;
  }

  .button-group {
    flex-direction: column;
  }
}
</style>