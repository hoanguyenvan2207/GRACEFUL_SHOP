<template>
  <div class="register-wrapper">
    <div class="register-container">
      <!-- Illustration Section -->
      <div class="illustration-section">
        <img src="https://accounts.haravan.com/img/login_banner.svg" alt="Shopping Illustration" class="illustration-image" />
      </div>


      <!-- Form Section -->
      <div class="form-section">
        <div class="register-card">
          <h1 class="register-title">GRACEFUL - ĐĂNG KÝ</h1>
          <p class="register-subtitle">Vui lòng điền các thông tin bên dưới</p>


          <div class="form-group">
            <label for="hoTen">Họ tên</label>
            <input type="text" id="hoTen" class="form-control" placeholder="Nhập họ tên của bạn" v-model="formData.hoTen"
              :class="{ 'is-invalid': validationErrors.hoTen }" />
            <div class="invalid-feedback d-block" v-if="validationErrors.hoTen">
              {{ validationErrors.hoTen }}
            </div>
          </div>


          <div class="form-group">
            <label for="email">Địa chỉ email</label>
            <input type="email" id="email" class="form-control" placeholder="Vui lòng nhập địa chỉ email của bạn"
              v-model="formData.email" :class="{ 'is-invalid': validationErrors.email }" />
            <div class="invalid-feedback d-block" v-if="validationErrors.email">
              {{ validationErrors.email }}
            </div>
          </div>


          <div class="form-group">
            <label for="soDienThoai">Số điện thoại</label>
            <input type="text" id="soDienThoai" class="form-control" placeholder="Nhập số điện thoại của bạn"
              v-model="formData.soDienThoai" :class="{ 'is-invalid': validationErrors.soDienThoai }" />
            <div class="invalid-feedback d-block" v-if="validationErrors.soDienThoai">
              {{ validationErrors.soDienThoai }}
            </div>
          </div>


          <div class="form-group password-wrapper">
            <label for="matKhau">Mật khẩu</label>
            <div class="password-input-container">
              <input :type="passwordVisible ? 'text' : 'password'" id="matKhau" class="form-control"
                placeholder="Vui lòng nhập mật khẩu của bạn" v-model="formData.matKhau"
                :class="{ 'is-invalid': validationErrors.matKhau }" />
              <span class="toggle-password" @click="togglePassword">
                <i :class="passwordVisible ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
              </span>
            </div>
            <div class="invalid-feedback d-block" v-if="validationErrors.matKhau">
              {{ validationErrors.matKhau }}
            </div>
          </div>


          <div class="form-group password-wrapper">
            <label for="confirmPassword">Xác nhận mật khẩu</label>
            <div class="password-input-container">
              <input :type="confirmPasswordVisible ? 'text' : 'password'" id="confirmPassword" class="form-control"
                placeholder="Vui lòng nhập xác nhận mật khẩu" v-model="formData.confirmPassword"
                :class="{ 'is-invalid': validationErrors.confirmPassword }" />
              <span class="toggle-password" @click="toggleConfirmPassword">
                <i :class="confirmPasswordVisible ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
              </span>
            </div>
            <div class="invalid-feedback d-block" v-if="validationErrors.confirmPassword">
              {{ validationErrors.confirmPassword }}
            </div>
          </div>


          <div class="account-exists">
            Bạn đã có tài khoản? <router-link to="/dang-nhap">Đăng nhập</router-link>
          </div>


          <button class="btn-register" @click="register" :disabled="isLoading">
            <span v-if="isLoading">
              <i class="fas fa-spinner fa-spin"></i> Đang xử lý...
            </span>
            <span v-else>ĐĂNG KÝ</span>
          </button>
        </div>
      </div>
    </div>
    <div class="powered-by">
      Powered by <span class="brand">Graceful</span>
    </div>
  </div>
</template>


<script>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { notification } from "ant-design-vue";
import AuthService from "../../services/AuthService";


export default {
  name: "Register",
  setup() {
    const router = useRouter();


    const formData = reactive({
      hoTen: "",
      email: "",
      soDienThoai: "",
      matKhau: "",
      confirmPassword: ""
    });


    const validationErrors = reactive({
      hoTen: "",
      email: "",
      soDienThoai: "",
      matKhau: "",
      confirmPassword: ""
    });


    const isLoading = ref(false);
    const passwordVisible = ref(false);
    const confirmPasswordVisible = ref(false);


    const validateForm = () => {
      let isValid = true;


      validationErrors.hoTen = "";
      validationErrors.email = "";
      validationErrors.soDienThoai = "";
      validationErrors.matKhau = "";
      validationErrors.confirmPassword = "";


      if (!formData.hoTen.trim()) {
        validationErrors.hoTen = "Họ tên không được để trống";
        isValid = false;
      } else if (!/^[\p{L}\s]+$/u.test(formData.hoTen)) {
        validationErrors.hoTen = "Họ tên chỉ được chứa chữ cái";
        isValid = false;
      }


      if (!formData.email.trim()) {
        validationErrors.email = "Email không được để trống";
        isValid = false;
      } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
        validationErrors.email = "Email không hợp lệ";
        isValid = false;
      }


      if (!formData.soDienThoai.trim()) {
        validationErrors.soDienThoai = "Số điện thoại không được để trống";
        isValid = false;
      } else if (
        !/^(0|\+84)(3[2-9]|5[2689]|7[06-9]|8[1-9]|9[0-9])[0-9]{7}$/.test(
          formData.soDienThoai
        )
      ) {
        validationErrors.soDienThoai =
          "Số điện thoại không hợp lệ. Phải là số điện thoại di động Việt Nam (10 số) với các đầu số: 03x, 05x, 07x, 08x, 09x";
        isValid = false;
      }


      if (!formData.matKhau.trim()) {
        validationErrors.matKhau = "Mật khẩu không được để trống";
        isValid = false;
      } else if (formData.matKhau.length < 8) {
        validationErrors.matKhau = "Mật khẩu phải có ít nhất 8 ký tự";
        isValid = false;
      } else if (!/[a-z]/.test(formData.matKhau)) {
        validationErrors.matKhau = "Mật khẩu phải có ít nhất 1 chữ in thường";
        isValid = false;
      } else if (!/[A-Z]/.test(formData.matKhau)) {
        validationErrors.matKhau = "Mật khẩu phải có ít nhất 1 chữ in hoa";
        isValid = false;
      } else if (!/[!@#$%^&*(),.?\":{}|<>]/.test(formData.matKhau)) {
        validationErrors.matKhau = "Mật khẩu phải có ít nhất 1 ký tự đặc biệt";
        isValid = false;
      }


      if (!formData.confirmPassword.trim()) {
        validationErrors.confirmPassword = "Vui lòng xác nhận mật khẩu";
        isValid = false;
      } else if (formData.confirmPassword !== formData.matKhau) {
        validationErrors.confirmPassword = "Xác nhận mật khẩu không khớp";
        isValid = false;
      }


      return isValid;
    };


    const register = async () => {
      validationErrors.hoTen = "";
      validationErrors.email = "";
      validationErrors.soDienThoai = "";
      validationErrors.matKhau = "";
      validationErrors.confirmPassword = "";


      if (!validateForm()) {
        return;
      }


      isLoading.value = true;
      try {
        await AuthService.dangKi(
          formData.hoTen,
          formData.email,
          formData.soDienThoai,
          formData.matKhau
        );
        notification.success({
          message: "Đăng ký thành công!",
          description: "Bạn đã đăng ký thành công. Vui lòng đăng nhập."
        });
        router.push("/dang-nhap");
      } catch (error) {
        if (error.message === "Email đã tồn tại") {
          validationErrors.email = "Email đã tồn tại. Vui lòng sử dụng email khác.";
        } else if (error.message === "Số điện thoại đã tồn tại") {
          validationErrors.soDienThoai = "Số điện thoại đã tồn tại. Vui lòng sử dụng số điện thoại khác.";
        } else {
          console.error("Register error:", error);
          notification.error({
            message: "Lỗi",
            description: "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại sau."
          });
        }
      } finally {
        isLoading.value = false;
      }
    };


    const togglePassword = () => {
      passwordVisible.value = !passwordVisible.value;
    };


    const toggleConfirmPassword = () => {
      confirmPasswordVisible.value = !confirmPasswordVisible.value;
    };


    return {
      formData,
      validationErrors,
      isLoading,
      register,
      passwordVisible,
      confirmPasswordVisible,
      togglePassword,
      toggleConfirmPassword
    };
  }
};
</script>


<style scoped>
.register-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #e6edff;
  padding: 20px;
  position: relative;
}


.register-container {
  display: flex;
  max-width: 900px;
  width: 100%;
  background-color: transparent;
  gap: 50px; /* Added gap to increase spacing between illustration and form */
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


.register-card {
  background-color: white;
  border-radius: 10px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 350px;
  width: 100%;
}


.register-title {
  font-size: 22px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 8px;
}


.register-subtitle {
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


.toggle-password {
  position: absolute;
  top: 50%;
  right: 10px;
  transform: translateY(-50%);
  cursor: pointer;
  color: #666;
}


.toggle-password i {
  font-size: 1rem;
}


.toggle-password:hover i {
  color: #333;
}


.account-exists {
  text-align: center;
  margin: 15px 0;
  font-size: 13px;
}


.account-exists a {
  color: #007bff;
  text-decoration: none;
}


.account-exists a:hover {
  text-decoration: underline;
}


.btn-register {
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


.btn-register:hover {
  background-color: #0056b3;
}


.btn-register:disabled {
  background-color: #7caae6;
  cursor: not-allowed;
}
</style>

