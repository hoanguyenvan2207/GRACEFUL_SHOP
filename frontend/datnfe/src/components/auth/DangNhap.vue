<template>
  <div class="login-wrapper">
    <div class="login-container">
      <!-- Illustration Section -->
      <div class="illustration-section">
        <img src="https://accounts.haravan.com/img/login_banner.svg" alt="Shopping Illustration"
          class="illustration-image" />
      </div>


      <!-- Form Section -->
      <div class="form-section">
        <div class="login-card">
          <h1 class="login-title">GRACEFUL - ĐĂNG NHẬP</h1>
          <p class="login-subtitle">Vui lòng nhập email hoặc số điện thoại và mật khẩu</p>


          <!-- Form đăng nhập -->
          <div class="form-group">
            <label for="identifier">Email hoặc Số điện thoại</label>
            <input type="text" id="identifier" class="form-control"
              placeholder="Vui lòng nhập email hoặc số điện thoại của bạn" v-model="formData.identifier"
              :class="{ 'is-invalid': validationErrors.identifier }" />
            <div class="invalid-feedback" v-if="validationErrors.identifier">
              {{ validationErrors.identifier }}
            </div>
          </div>


          <div class="form-group password-wrapper">
            <label for="matKhau">Mật khẩu</label>
            <div class="password-input-container">
              <input :type="showPassword ? 'text' : 'password'" id="matKhau" class="form-control"
                placeholder="Vui lòng nhập mật khẩu của bạn" v-model="formData.matKhau"
                :class="{ 'is-invalid': validationErrors.matKhau }" />
              <button type="button" class="password-toggle-btn" @click="toggleShowPassword">
                <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
              </button>
            </div>
            <div class="invalid-feedback" v-show="validationErrors.matKhau">
              {{ validationErrors.matKhau }}
            </div>
          </div>


          <div class="forgot-password text-end mb-4">
            <a href="#" @click.prevent="forgotPassword">Quên mật khẩu?</a>
          </div>


          <button class="btn-login" @click="login" :disabled="isLoading">
            <span v-if="isLoading">
              <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
            </span>
            <span v-else>ĐĂNG NHẬP</span>
          </button>


          <div class="no-account">
            Chưa có tài khoản?
            <router-link to="/dang-ki">Tạo một tài khoản</router-link>
          </div>
        </div>
      </div>
    </div>
    <div class="powered-by">
      Powered by <span class="brand">Graceful</span>
    </div>


    <!-- Modal Quên mật khẩu (OTP) -->
    <div class="modal-overlay" v-if="showForgotPasswordModal" @click="closeForgotPasswordModal">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <h2>Quên mật khẩu</h2>
          <button class="close-button" @click="closeForgotPasswordModal">
            ×
          </button>
        </div>
        <div class="modal-body">
          <div v-if="!otpSent">
            <div class="form-group">
              <label for="resetEmail">Địa chỉ email</label>
              <input type="email" id="resetEmail" class="form-control" placeholder="Vui lòng nhập địa chỉ email của bạn"
                v-model="resetEmailData.email" :class="{ 'is-invalid': resetValidationErrors.email }" />
              <div class="invalid-feedback" v-if="resetValidationErrors.email">
                {{ resetValidationErrors.email }}
              </div>
            </div>
            <p class="reset-info">
              Nhập email của bạn và chúng tôi sẽ gửi mã OTP để đặt lại mật khẩu.
            </p>
          </div>
          <div v-else>
            <div class="form-group">
              <label for="otp">Mã OTP</label>
              <input type="text" id="otp" class="form-control" placeholder="Nhập mã OTP" v-model="resetPasswordData.otp"
                :class="{ 'is-invalid': resetPasswordErrors.otp }" />
              <div class="invalid-feedback" v-if="resetPasswordErrors.otp">
                {{ resetPasswordErrors.otp }}
              </div>
            </div>
            <div class="form-group password-wrapper">
              <label for="newPassword">Mật khẩu mới</label>
              <div class="password-input-container">
                <input :type="showNewPassword ? 'text' : 'password'" id="newPassword" class="form-control"
                  placeholder="Nhập mật khẩu mới" v-model="resetPasswordData.matKhau"
                  :class="{ 'is-invalid': resetPasswordErrors.matKhau }" />
                <button type="button" class="password-toggle-btn" @click="toggleShowNewPassword">
                  <i :class="showNewPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div class="invalid-feedback" v-if="resetPasswordErrors.matKhau">
                {{ resetPasswordErrors.matKhau }}
              </div>
            </div>


            <div class="form-group password-wrapper">
              <label for="confirmPassword">Xác nhận mật khẩu</label>
              <div class="password-input-container">
                <input :type="showConfirmPassword ? 'text' : 'password'" id="confirmPassword" class="form-control"
                  placeholder="Nhập lại mật khẩu mới" v-model="resetPasswordData.xacNhanMatKhau"
                  :class="{ 'is-invalid': resetPasswordErrors.xacNhanMatKhau }" />
                <button type="button" class="password-toggle-btn" @click="toggleShowConfirmPassword">
                  <i :class="showConfirmPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
                </button>
              </div>
              <div class="invalid-feedback" v-if="resetPasswordErrors.xacNhanMatKhau">
                {{ resetPasswordErrors.xacNhanMatKhau }}
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="closeForgotPasswordModal">
            {{ otpSent ? "Đóng" : "Hủy" }}
          </button>
          <button v-if="!otpSent" class="btn-reset" @click="sendOTP" :disabled="isResetting">
            <span v-if="isResetting">
              <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
            </span>
            <span v-else>Gửi mã OTP</span>
          </button>
          <button v-else class="btn-reset" @click="resetPassword" :disabled="isResettingPassword">
            <span v-if="isResettingPassword">
              <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
            </span>
            <span v-else>Đặt lại mật khẩu</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
import { reactive, ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import { notification } from "ant-design-vue";
import AuthService from "../../services/AuthService";
import { isLoggedIn, userEmail } from "./authUse";
import store from "../../router/store";


export default {
  name: "KhachHangLogin",
  setup() {
    const router = useRouter();
    const route = useRoute();


    const formData = reactive({
      identifier: "",
      matKhau: "",
    });
    const validationErrors = reactive({
      identifier: "",
      matKhau: "",
    });
    const isLoading = ref(false);
    const showPassword = ref(false);


    const showForgotPasswordModal = ref(false);
    const otpSent = ref(false);
    const resetEmailData = reactive({
      email: "",
    });
    const resetValidationErrors = reactive({
      email: "",
    });
    const isResetting = ref(false);


    const resetPasswordData = reactive({
      otp: "",
      matKhau: "",
      xacNhanMatKhau: "",
    });
    const resetPasswordErrors = reactive({
      otp: "",
      matKhau: "",
      xacNhanMatKhau: "",
    });
    const isResettingPassword = ref(false);
    const showNewPassword = ref(false);
    const showConfirmPassword = ref(false);


    const validateForm = () => {
      let isValid = true;
      validationErrors.identifier = "";
      validationErrors.matKhau = "";


      if (!formData.identifier.trim()) {
        validationErrors.identifier = "Email hoặc số điện thoại không được để trống";
        isValid = false;
      } else {
        if (formData.identifier.includes("@")) {
          if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.identifier)) {
            validationErrors.identifier = "Email không hợp lệ";
            isValid = false;
          }
        } else {
          if (!/^(0\d{9,10})$/.test(formData.identifier)) {
            validationErrors.identifier = "Số điện thoại không hợp lệ";
            isValid = false;
          }
        }
      }


      if (!formData.matKhau) {
        validationErrors.matKhau = "Mật khẩu không được để trống";
        isValid = false;
      } else if (formData.matKhau.length < 8) {
        validationErrors.matKhau = "Mật khẩu phải có ít nhất 8 ký tự";
        isValid = false;
      }


      return isValid;
    };


    const validateResetEmail = () => {
      resetValidationErrors.email = "";
      let isValid = true;


      if (!resetEmailData.email.trim()) {
        resetValidationErrors.email = "Email không được để trống";
        isValid = false;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(resetEmailData.email)) {
        resetValidationErrors.email = "Email không hợp lệ";
        isValid = false;
      }


      return isValid;
    };


    const validateResetPassword = () => {
      resetPasswordErrors.otp = "";
      resetPasswordErrors.matKhau = "";
      resetPasswordErrors.xacNhanMatKhau = "";
      let isValid = true;


      if (!resetPasswordData.otp.trim()) {
        resetPasswordErrors.otp = "Mã OTP không được để trống";
        isValid = false;
      }


      if (!resetPasswordData.matKhau) {
        resetPasswordErrors.matKhau = "Mật khẩu mới không được để trống";
        isValid = false;
      } else if (resetPasswordData.matKhau.length < 8) {
        resetPasswordErrors.matKhau = "Mật khẩu phải có ít nhất 8 ký tự";
        isValid = false;
      }


      if (!resetPasswordData.xacNhanMatKhau) {
        resetPasswordErrors.xacNhanMatKhau = "Vui lòng xác nhận mật khẩu";
        isValid = false;
      } else if (resetPasswordData.matKhau !== resetPasswordData.xacNhanMatKhau) {
        resetPasswordErrors.xacNhanMatKhau = "Mật khẩu xác nhận không khớp";
        isValid = false;
      }


      return isValid;
    };


    const sendOTP = async () => {
      if (!validateResetEmail()) return;


      isResetting.value = true;
      try {
        await AuthService.quenMatKhau(resetEmailData.email);
        otpSent.value = true;
        notification.success({
          message: "Thành công",
          description: "Mã OTP đã được gửi đến email của bạn!",
          placement: "topRight",
          duration: 5,
        });
      } catch (error) {
        console.error("Lỗi gửi OTP:", error);
        if (error.errors) {
          resetValidationErrors.email = error.errors.email || "";
        } else if (error.message) {
          notification.error({
            message: "Lỗi",
            description: error.message,
            placement: "topRight",
            duration: 5,
          });
        }
      } finally {
        isResetting.value = false;
      }
    };


    const resetPassword = async () => {
      if (!validateResetPassword()) return;


      isResettingPassword.value = true;
      try {
        await AuthService.datLaiMatKhau(
          resetEmailData.email,
          resetPasswordData.otp,
          resetPasswordData.matKhau
        );
        notification.success({
          message: "Thành công",
          description: "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.",
          placement: "topRight",
          duration: 5,
        });
        closeForgotPasswordModal();
        router.push("/dang-nhap");
      } catch (error) {
        if (error.errors) {
          resetPasswordErrors.otp = error.errors.otp || "";
          resetPasswordErrors.matKhau = error.errors.matKhau || "";
        } else if (error.message) {
          notification.error({
            message: "Lỗi",
            description: error.message,
            placement: "topRight",
            duration: 5,
          });
        }
      } finally {
        isResettingPassword.value = false;
      }
    };


    const login = async () => {
      if (!validateForm()) return;

      isLoading.value = true;
      try {
        let response;
        await store.dispatch('logout');
        sessionStorage.removeItem("user");

        if (formData.identifier.includes("@")) {
          response = await AuthService.dangNhap(formData.identifier, undefined, formData.matKhau);
        } else {
          response = await AuthService.dangNhap(undefined, formData.identifier, formData.matKhau);
        }

        sessionStorage.setItem("customerInfo", JSON.stringify(response));
        sessionStorage.setItem("userId", response.id);

        isLoggedIn.value = true;
        userEmail.value = response.email;

        notification.success({
          message: "Thành công",
          description: "Đăng nhập thành công!",
          placement: "topRight",
          duration: 3,
        });

        const redirectPath = route.query.redirect;

        if (redirectPath && redirectPath !== '/dang-ki' && redirectPath !== '/thanh-toan') {
          router.push(redirectPath);
        } else {
          router.push('/');
        }

      } catch (error) {
        let errorMsg = "";
        if (error.response && error.response.data) {
          console.log("Error response data:", error.response.data);
          errorMsg =
            error.response.data.message ||
            error.response.data.error ||
            error.response.data;
        } else {
          errorMsg = error.message;
        }
        if (typeof errorMsg !== "string") {
          errorMsg = JSON.stringify(errorMsg);
        }
        if (
          errorMsg.includes("Email không đúng") ||
          errorMsg.includes("Số điện thoại không đúng")
        ) {
          validationErrors.identifier = errorMsg;
        } else if (errorMsg.includes("Mật khẩu không đúng")) {
          validationErrors.matKhau = errorMsg;
        } else {
          notification.error({
            message: "Lỗi",
            description: errorMsg,
            placement: "topRight",
            duration: 5,
          });
        }
      } finally {
        isLoading.value = false;
      }
    };

    const toggleShowPassword = () => {
      showPassword.value = !showPassword.value;
    };


    const toggleShowNewPassword = () => {
      showNewPassword.value = !showNewPassword.value;
    };


    const toggleShowConfirmPassword = () => {
      showConfirmPassword.value = !showConfirmPassword.value;
    };


    const forgotPassword = () => {
      if (formData.identifier && formData.identifier.includes("@")) {
        resetEmailData.email = formData.identifier;
      } else {
        resetEmailData.email = "";
      }
      showForgotPasswordModal.value = true;
    };


    const closeForgotPasswordModal = () => {
      showForgotPasswordModal.value = false;
      resetEmailData.email = "";
      otpSent.value = false;
      resetPasswordData.otp = "";
      resetPasswordData.matKhau = "";
      resetPasswordData.xacNhanMatKhau = "";
      resetValidationErrors.email = "";
      resetPasswordErrors.otp = "";
      resetPasswordErrors.matKhau = "";
      resetPasswordErrors.xacNhanMatKhau = "";
    };


    return {
      formData,
      validationErrors,
      isLoading,
      showPassword,
      login,
      toggleShowPassword,
      forgotPassword,
      showForgotPasswordModal,
      resetEmailData,
      resetValidationErrors,
      isResetting,
      sendOTP,
      otpSent,
      resetPasswordData,
      resetPasswordErrors,
      isResettingPassword,
      showNewPassword,
      toggleShowNewPassword,
      resetPassword,
      closeForgotPasswordModal,
      showConfirmPassword,
      toggleShowConfirmPassword,
    };
  },
};
</script>


<style scoped>
.login-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #e6edff;
  padding: 20px;
  position: relative;
}


.login-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  background-color: transparent;
  gap: 50px;
  /* Added gap to increase spacing between illustration and form */
}


.illustration-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}


.illustration-image {
  width: 100%;
  max-width: 400px;
  height: auto;
}


.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}


.powered-by {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 12px;
  color: #666;
}


.powered-by .brand {
  color: #ff69b4;
  font-weight: 500;
}


.login-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 350px;
  width: 100%;
}


.login-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 8px;
}


.login-subtitle {
  font-size: 13px;
  color: #666;
  text-align: center;
  margin-bottom: 25px;
}


.form-group {
  margin-bottom: 15px;
}


label {
  font-weight: 500;
  margin-bottom: 6px;
  display: block;
  color: #333;
}


.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  transition: border-color 0.3s;
}


.form-control:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 0.25rem rgba(0, 123, 255, 0.25);
}


.form-control.is-invalid {
  border-color: #dc3545;
}


.invalid-feedback {
  display: block;
  width: 100%;
  color: #dc3545;
  font-size: 11px;
  margin-top: 4px;
}


.password-wrapper {
  position: relative;
}


.password-input-container {
  position: relative;
}


.password-toggle-btn {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
}


.password-toggle-btn:hover {
  color: #495057;
}


.btn-login {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}


.btn-login:hover {
  background-color: #0056b3;
}


.btn-login:disabled {
  background-color: #7caae6;
  cursor: not-allowed;
}


.no-account {
  margin-top: 15px;
  text-align: center;
  font-size: 13px;
}


.no-account a {
  color: #007bff;
  text-decoration: none;
}


.no-account a:hover {
  text-decoration: underline;
}


.forgot-password a {
  color: #007bff;
  text-decoration: none;
  font-size: 13px;
}


.forgot-password a:hover {
  text-decoration: underline;
}


.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}


.modal-container {
  background-color: white;
  width: 90%;
  max-width: 400px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}


.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}


.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}


.close-button {
  background: none;
  border: none;
  font-size: 20px;
  line-height: 1;
  color: #666;
  cursor: pointer;
}


.modal-body {
  padding: 20px;
}


.reset-info {
  font-size: 13px;
  color: #666;
  margin-top: 15px;
}


.modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 15px 20px;
  border-top: 1px solid #eee;
  gap: 10px;
}


.btn-cancel {
  padding: 8px 12px;
  background-color: #f8f9fa;
  border: 1px solid #ced4da;
  border-radius: 4px;
  color: #495057;
  cursor: pointer;
  transition: background-color 0.3s;
}


.btn-cancel:hover {
  background-color: #e9ecef;
}


.btn-reset {
  padding: 8px 12px;
  background-color: #007bff;
  border: none;
  border-radius: 4px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}


.btn-reset:hover {
  background-color: #0056b3;
}


.btn-reset:disabled {
  background-color: #7caae6;
  cursor: not-allowed;
}
</style>
