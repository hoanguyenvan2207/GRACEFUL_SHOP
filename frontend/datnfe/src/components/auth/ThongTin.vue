<template>
  <div class="account-info-container">
    <div class="sidebar">
      <div class="user-info">
        <div class="avatar">
          <div class="avatar-placeholder">
            <span>{{ userDisplayName }}</span>
          </div>
        </div>
        <div>{{ t('account.sidebar.emailLabel') }} {{ userEmail }}</div>
      </div>
      <nav class="navigation">
        <div class="d-flex flex-column gap-2 mt-2">
          <button class="btn btn-dark" :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">
            {{ t('account.sidebar.accountInfo') }}
          </button>
          <button class="btn btn-dark" :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">
            {{ t('account.sidebar.changePassword') }}
          </button>
          <button class="btn btn-dark" :class="{ active: activeTab === 'address' }" @click="activeTab = 'address'">
            {{ t('account.sidebar.addressBook') }}
          </button>
        </div>
        <!-- Nút Quay lại trang home -->
        <div class="mt-2">
          <button class="btn btn-dark w-100" @click="goHome">
            {{ t('account.sidebar.goBack') }}
          </button>
        </div>
      </nav>
    </div>

    <div class="main-content">
      <!-- Thông tin tài khoản -->
      <div v-if="activeTab === 'info'">
        <h2 class="title">{{ t('account.info.title') }}</h2>
        <form @submit.prevent="handleSubmit" class="account-form">
          <div class="form-group">
            <label>{{ t('account.info.gender') }}</label>
            <div class="radio-group">
              <label class="radio">
                <input type="radio" v-model="formData.gioiTinh" :value="true" />
                {{ t('account.info.male') }}
              </label>
              <label class="radio">
                <input type="radio" v-model="formData.gioiTinh" :value="false" />
                {{ t('account.info.female') }}
              </label>
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('account.info.fullName') }} <span class="required">*</span></label>
            <input type="text" v-model="formData.hoTen" :placeholder="t('account.info.fullNamePlaceholder')"
              :class="{ 'is-invalid': validationErrors.hoTen }" />
            <div class="invalid-feedback" v-if="validationErrors.hoTen">
              {{ validationErrors.hoTen }}
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('account.info.phone') }} <span class="required">*</span></label>
            <input type="tel" v-model="formData.soDienThoai" :placeholder="t('account.info.phonePlaceholder')"
              :class="{ 'is-invalid': validationErrors.soDienThoai }" />
            <div class="invalid-feedback" v-if="validationErrors.soDienThoai">
              {{ validationErrors.soDienThoai }}
            </div>
          </div>

          <div class="form-group">
            <label>{{ t('account.info.email') }}</label>
            <input type="email" v-model="formData.email" disabled />
          </div>

          <div class="form-group">
            <label>{{ t('account.info.birthday') }}</label>
            <input type="date" lang="en-CA"
              :value="formData.ngaySinh ? new Date(formData.ngaySinh).toISOString().substr(0, 10) : ''"
              @input="formData.ngaySinh = $event.target.value" :class="{ 'is-invalid': validationErrors.ngaySinh }" />
            <div class="invalid-feedback" v-if="validationErrors.ngaySinh">
              {{ validationErrors.ngaySinh }}
            </div>
          </div>

          <button type="submit" class="submit-btn">{{ t('account.info.submit') }}</button>
        </form>
      </div>

      <!-- Đổi mật khẩu -->
      <div v-else-if="activeTab === 'password'">
        <h2 class="title">{{ t('account.resetPassword.title') }}</h2>
        <DoiMk />
      </div>

      <!-- Sổ địa chỉ -->
      <div v-else-if="activeTab === 'address'">
        <h2 class="title">{{ t('account.addressBook.title') }}</h2>
        <SoDiaChi />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import AuthService from "../../services/AuthService";
import { notification, Modal } from "ant-design-vue";
import DoiMk from "../auth/DoiMk.vue";
import SoDiaChi from "../auth/SoDiaChi.vue";
import { isLoggedIn, userEmail } from "./authUse";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const router = useRouter();
const formData = ref({
  gioiTinh: true,
  hoTen: "",
  soDienThoai: "",
  email: "",
  ngaySinh: "",
});
const showConfirm = (message) => {
  return new Promise((resolve) => {
    Modal.confirm({
      title: t("account.info.confirmUpdateTitle"),
      content: message,
      okText: "Ok",
      cancelText: "Cancel",
      onOk() {
        resolve(true);
      },
      onCancel() {
        resolve(false);
      },
    });
  });
};


const validationErrors = ref({
  hoTen: "",
  soDienThoai: "",
  ngaySinh: ""
});

const activeTab = ref("info");

const userDisplayName = computed(() => {
  if (formData.value.hoTen) {
    return formData.value.hoTen.split(" ").slice(-1)[0] || "User";
  }
  return "User";
});

onMounted(async () => {
  const user = await AuthService.initialize();
  if (!user) {
    router.push("/dang-nhap");
    return;
  }
  userEmail.value = user.email;
  formData.value.email = user.email;
  if (user.hoTen) {
    formData.value.hoTen = user.hoTen;
  }
  if (user.id) {
    const customerInfo = await AuthService.getCustomerInfo();
    if (customerInfo) {
      formData.value = {
        hoTen: customerInfo.hoTen || user.hoTen || "",
        gioiTinh: customerInfo.gioiTinh !== undefined ? customerInfo.gioiTinh : true,
        soDienThoai: customerInfo.soDienThoai || "",
        email: customerInfo.email || user.email,
        ngaySinh: customerInfo.ngaySinh || "",
      };
    }
  }
});

const validateForm = () => {
  let isValid = true;
  validationErrors.value = {
    hoTen: "",
    soDienThoai: "",
    ngaySinh: ""
  };

  // Kiểm tra họ tên
  if (!formData.value.hoTen.trim()) {
    validationErrors.value.hoTen = t("account.validation.nameRequired");
    isValid = false;
  } else if (/[0-9]/.test(formData.value.hoTen)) {
    validationErrors.value.hoTen = t("account.validation.nameNoNumbers");
    isValid = false;
  } else if (/[!@#$%^&*(),.?":{}|<>]/.test(formData.value.hoTen)) {
    validationErrors.value.hoTen = t("account.validation.nameNoSpecial");
    isValid = false;
  }

  // Kiểm tra số điện thoại
  if (!formData.value.soDienThoai.trim()) {
    validationErrors.value.soDienThoai = t("account.validation.phoneRequired");
    isValid = false;
  } else if (!/^[0-9]{10}$/.test(formData.value.soDienThoai)) {
    validationErrors.value.soDienThoai = t("account.validation.phoneDigits");
    isValid = false;
  } else if (!/^(03|05|07|08|09)/.test(formData.value.soDienThoai)) {
    validationErrors.value.soDienThoai =
      t("account.validation.phoneValidPrefix");
    isValid = false;
  }

  // Kiểm tra ngày sinh: đảm bảo ngày sinh nhỏ hơn ngày hiện tại
  if (formData.value.ngaySinh) {
    const ngaySinhDate = new Date(formData.value.ngaySinh);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    if (ngaySinhDate >= today) {
      validationErrors.value.ngaySinh = t("account.validation.birthdayPast");
      isValid = false;
    }
  } else {
    validationErrors.value.ngaySinh = t("account.validation.birthdayRequired");
    isValid = false;
  }

  return isValid;
};

const handleSubmit = async () => {
  const confirmed = await showConfirm(t("account.info.confirmUpdateContent"));
  if (!confirmed) return;

  if (!validateForm()) return;

  try {
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser?.id) {
      console.error("Không tìm thấy thông tin người dùng!");
      return;
    }

    const updateData = {
      id: currentUser.id,
      hoTen: formData.value.hoTen,
      gioiTinh: formData.value.gioiTinh,
      ngaySinh: formData.value.ngaySinh,
      email: formData.value.email,
      soDienThoai: formData.value.soDienThoai,
    };

    await AuthService.updateCustomer(currentUser.id, updateData);

    notification.success({
      message: t("account.notification.updateSuccessTitle"),
      description: t("account.notification.updateSuccessDescription"),
      placement: "topRight",
      duration: 3,
    });
  } catch (error) {
    // Kiểm tra nếu lỗi là một object chứa errors
    if (error.errors && error.errors.soDienThoai) {
      validationErrors.value.soDienThoai = error.errors.soDienThoai;
    }
    // Kiểm tra nếu lỗi là một Error object với message
    else if (error.message) {
      if (error.message.includes("Số điện thoại")) {
        validationErrors.value.soDienThoai = error.message;
      } else {
        notification.error({
          message: t("account.notification.error"),
          description: error.message,
          placement: "topRight",
          duration: 3,
        });
      }
    }
    // Trường hợp lỗi không xác định
    else {
      console.error("Lỗi không xác định:", error);
      notification.error({
        message: "Lỗi",
        description: "Đã xảy ra lỗi không xác định khi cập nhật thông tin",
        placement: "topRight",
        duration: 3,
      });
    }
  }
};



const goHome = () => {
  router.push("/");
};
</script>


<style scoped>
.account-info-container {
  display: flex;
  gap: 4rem;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  background-color: rgb(228, 225, 228);
}

.sidebar {
  width: 280px;
  flex-shrink: 0;
  padding: 1.5rem;
  background-color: #f0f0f0;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.user-info {
  text-align: center;
  margin-bottom: 1.5rem;
}

.avatar {
  width: 100px;
  height: 80px;
  margin: 0 auto 1rem;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background: #e9ecef;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  font-size: 1rem;
}

.navigation {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.btn.active {
  background-color: #8e9799;
}

.main-content {
  flex: 1;
  padding: 1.5rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.title {
  margin-bottom: 2rem;
  font-size: 1.5rem;
}

.account-form {
  max-width: 600px;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}

.radio-group {
  display: flex;
  gap: 2rem;
}

.radio {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

input[type="text"],
input[type="tel"],
input[type="email"],
input[type="date"] {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}

input[disabled] {
  background: #f8f9fa;
}

.submit-btn {
  width: 100%;
  padding: 0.75rem;
  background: #007bff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: white;
  font-weight: 500;
}

.submit-btn:hover {
  background: #0069d9;
}

.required {
  color: #dc3545;
}

.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  display: block;
  width: 100%;
  color: #dc3545;
  font-size: 12px;
  margin-top: 5px;
}
</style>