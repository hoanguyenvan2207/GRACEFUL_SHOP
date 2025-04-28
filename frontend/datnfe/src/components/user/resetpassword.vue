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

        <div class="header-info">
          <h1 class="title ">Đổi mật khẩu</h1>
          <p class="subtitle">Nhập mật khẩu mới cho tài khoản của bạn</p>
        </div>


        <div class="form-container">
          <form @submit.prevent="submitNewPassword">
            <div class="form-group">
              <label for="newPassword">Mật khẩu mới <span class="required">*</span></label>
              <div class="password-wrapper">
                <input :type="showPassword1 ? 'text' : 'password'" id="newPassword" v-model="newPassword"
                  placeholder="Ít nhất 6 ký tự, gồm hoa, thường, số, đặc biệt" class="form-input" required />
                <span class="password-toggle" @click="togglePassword('new')">
                  <i :class="showPassword1 ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
                </span>
              </div>
              <p v-if="newPassword && !isPasswordValid" class="error-message">
                Mật khẩu phải ≥ 6 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt.
              </p>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Xác nhận mật khẩu <span class="required">*</span></label>
              <div class="password-wrapper">
                <input :type="showPassword2 ? 'text' : 'password'" id="confirmPassword" v-model="confirmPassword"
                  placeholder="Nhập lại mật khẩu" class="form-input" required />
                <span class="password-toggle" @click="togglePassword('confirm')">
                  <i :class="showPassword2 ? 'fas fa-eye' : 'fas fa-eye-slash'"></i>
                </span>
              </div>
              <p v-if="confirmPassword && !passwordsMatch" class="error-message">
                Mật khẩu không khớp.
              </p>
            </div>

            <div class="button-group">
              <button type="submit" class="btn-login" :disabled="!canSubmit">
                {{ isSubmitting ? 'Đang xử lý...' : 'Đồng ý' }}
              </button>
              <button type="button" class="btn-cancel" @click="cancel">
                Huỷ bỏ
              </button>
            </div>

            <p v-if="errorMessage" class="alert alert-danger mt-3">
              {{ errorMessage }}
            </p>
          </form>
        </div>
      </div>
    </div>

  </div>
</template>


<script>
import axios from 'axios';
import { notification } from 'ant-design-vue';
axios.defaults.baseURL = "http://localhost:8080";
import {
  Modal,
  Input,
  Button,
  Form
} from 'ant-design-vue';


export default {
  components: {
    'a-modal': Modal,
    'a-input': Input,
    'a-button': Button,
    'a-form-item': Form.Item,
  },
  data() {
    return {
      newPassword: '',
      confirmPassword: '',
      token: this.$route.query.token,
      isSubmitting: false,
      errorMessage: '',
      showPassword1: false,
      showPassword2: false,
    };
  },
  computed: {
    isPasswordValid() {
      const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{6,}$/;
      return regex.test(this.newPassword);
    },
    passwordsMatch() {
      return this.newPassword === this.confirmPassword;
    },
    canSubmit() {
      return this.isPasswordValid && this.passwordsMatch && !this.isSubmitting;
    }
  },
  methods: {
    async submitNewPassword() {
      if (!this.canSubmit) {
        notification.warning({ message: 'Cảnh báo', description: 'Vui lòng kiểm tra lại thông tin.' });
        return;
      }
      this.isSubmitting = true;
      this.errorMessage = '';

      try {
        const response = await axios.post('/api/reset-password', {
          token: this.token,
          newPassword: this.newPassword
        });
        notification.success({ message: 'Thành công!', description: response.data || 'Đã đổi mật khẩu thành công!' });
        this.$router.push('/admin');
      } catch (error) {
        const desc = error.response?.data || 'Lỗi kết nối máy chủ. Vui lòng thử lại sau.';
        notification.error({ message: 'Lỗi', description: desc });
        this.errorMessage = desc;
      } finally {
        this.isSubmitting = false;
      }
    },
    togglePassword(field) {
      if (field === 'new') this.showPassword1 = !this.showPassword1;
      else this.showPassword2 = !this.showPassword2;
    },
    cancel() {
      this.$router.push('/admin');
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

@media (min-width: 992px) {
  .left-panel {
    display: block;
  }
}


.error-message {
  color: #dc2626;
  font-size: 13px;
  margin-top: 5px;
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
  text-align: center;
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
  flex: none;
  padding: 8px 24px;
  background-color: #2680eb;
  color: #fff;
  border: none;
  border-radius: 2px;
  font-weight: 600;
  cursor: pointer;
  width: 50%;
}

.btn-cancel {
  flex: none;
  padding: 8px 24px;
  border: none;
  border-radius: 2px;
  font-weight: 600;
  cursor: pointer;
  width: 50%;
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
  gap: 10px;
  margin-top: 20px;
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
