<template>
  <div class="reset-password-container">
    <div class="title-section">
      <p class="subtitle">
        {{ t("account.resetPassword.subtitle") }}
      </p>
    </div>

    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="new-password">{{ t('account.resetPassword.labels.newPassword') }}</label>
        <div class="password-input-container">
          <input :type="showNewPassword ? 'text' : 'password'" id="new-password" v-model="formData.newPassword"
            :placeholder="t('account.resetPassword.placeholders.newPassword')"
            :class="{ 'is-invalid': validationErrors.newPassword }" />
          <span class="toggle-password" @click="showNewPassword = !showNewPassword">
            <i :class="showNewPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
          </span>
        </div>
        <span class="error-message" v-show="validationErrors.newPassword">
          {{ validationErrors.newPassword }}
        </span>
      </div>

      <div class="form-group">
        <label for="confirm-password">{{ t('account.resetPassword.labels.confirmPassword') }}</label>
        <div class="password-input-container">
          <input :type="showConfirmPassword ? 'text' : 'password'" id="confirm-password"
            v-model="formData.confirmPassword" :placeholder="t('account.resetPassword.placeholders.confirmPassword')"
            :class="{ 'is-invalid': validationErrors.confirmPassword }" />
          <span class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
            <i :class="showConfirmPassword ? 'fa fa-eye-slash' : 'fa fa-eye'"></i>
          </span>
        </div>
        <span class="error-message" v-show="validationErrors.confirmPassword">
          {{ validationErrors.confirmPassword }}
        </span>
      </div>

      <button type="submit" class="submit-btn" :disabled="isLoading">
        {{ isLoading ? t('account.resetPassword.button.processing') : t('account.resetPassword.button.confirm') }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import AuthService from "../../services/AuthService";
import { notification } from "ant-design-vue";
import { useRouter } from "vue-router";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

const router = useRouter();
const isLoading = ref(false);

const formData = reactive({
  newPassword: "",
  confirmPassword: "",
});

const validationErrors = reactive({
  newPassword: "",
  confirmPassword: "",
});

const validateForm = () => {
  let isValid = true;
  validationErrors.newPassword = "";
  validationErrors.confirmPassword = "";

  if (!formData.newPassword.trim()) {
    validationErrors.newPassword = t("account.resetPassword.validation.newPasswordRequired");
    isValid = false;
  } else if (formData.newPassword.length < 8) {
    validationErrors.newPassword = t("account.resetPassword.validation.newPasswordMin");
    isValid = false;
  }

  if (!formData.confirmPassword.trim()) {
    validationErrors.confirmPassword = t("account.resetPassword.validation.confirmPasswordRequired");
    isValid = false;
  } else if (formData.newPassword !== formData.confirmPassword) {
    validationErrors.confirmPassword = t("account.resetPassword.validation.confirmPasswordMismatch");
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  try {
    isLoading.value = true;
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser || !currentUser.email) {
      throw new Error(t("account.resetPassword.notification.error.userNotFound"));
    }
    // Gọi hàm doiMatKhau với email của người dùng hiện tại và mật khẩu mới từ form
    await AuthService.doiMatKhau(currentUser.email, formData.newPassword);

    notification.success({
      message: t("account.resetPassword.notification.success.title"),
      description: t("account.resetPassword.notification.success.description"),
      placement: "topRight",
      duration: 3,
    });

    // Xóa dữ liệu form sau khi đổi mật khẩu thành công
    formData.newPassword = "";
    formData.confirmPassword = "";
  } catch (error) {
    notification.error({
      message: "Lỗi",
      description: error.message || t("account.resetPassword.notification.error.generic"),
      placement: "topRight",
      duration: 5,
    });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.reset-password-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.title-section {
  margin-bottom: 25px;
  text-align: center;
}

.subtitle {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

.form-group {
  margin-bottom: 20px;
  position: relative;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #333;
}

.password-input-container {
  position: relative;
  width: 100%;
}

input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

input.is-invalid {
  border-color: #dc3545;
  background-color: #fff;
}

.error-message {
  display: block;
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
  min-height: 20px;
}

.toggle-password {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #888;
}

.toggle-password:hover {
  color: #333;
}

.submit-btn {
  width: 100%;
  padding: 10px;
  background: #ffcccc;
  border: none;
  border-radius: 4px;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.submit-btn:hover {
  background: #ffb3b3;
}

.submit-btn:disabled {
  background: #f0f0f0;
  cursor: not-allowed;
}
</style>
