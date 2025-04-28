<template>
  <div class="add-customer">
    <a-card title="Thêm mới khách hàng" class="form-card">
      <a-form
        :model="formState"
        @finish="handleSubmit"
        @finishFailed="onFinishFailed"
        layout="vertical"
        validate-first
      >
        <!-- Thông tin cá nhân -->
        <div class="section-card">
          <h4 class="section-title">Thông tin cá nhân</h4>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item
                label="Họ và tên"
                name="hoTen"
                :rules="[
                  { required: true, message: 'Vui lòng nhập họ tên!' },
                  { validator: validateHoTen }
                ]"
              >
                <a-input v-model:value="formState.hoTen" />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="Giới tính" name="gioiTinh">
                <a-radio-group v-model:value="formState.gioiTinh">
                  <a-radio value="true">Nam</a-radio>
                  <a-radio value="false">Nữ</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item
                label="Ngày sinh"
                name="ngaySinh"
                :rules="[
                  { required: true, message: 'Vui lòng chọn ngày sinh!' },
                  { validator: validateBirthDate }
                ]"
              >
                <a-date-picker
                  v-model:value="formState.ngaySinh"
                  format="YYYY-MM-DD"
                  class="w-full"
                />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item
                label="Email"
                name="email"
                :rules="[
                  { required: true, message: 'Vui lòng nhập email!' },
                  { type: 'email', message: 'Email không hợp lệ!' }
                ]"
              >
                <a-input v-model:value="formState.email" />
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item
                label="Số điện thoại"
                name="soDienThoai"
                :rules="[
                  { required: true, message: 'Vui lòng nhập số điện thoại!' },
                  {
                    pattern: /^(03[2-9]|05[6,8,9]|07[0,6-9]|08[1-6,8,9]|09[0-9])\d{7}$/,
                    message: 'Số điện thoại không hợp lệ!'
                  }
                ]"
              >
                <a-input v-model:value="formState.soDienThoai" />
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- Thông tin địa chỉ -->
        <div class="form-section">
          <h4 class="section-title">Thông tin địa chỉ</h4>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="Tỉnh/Thành phố" name="diaChi.tinhThanhPho">
                <a-select
                  v-model:value="formState.diaChi.tinhThanhPho"
                  placeholder="Chọn tỉnh/thành phố"
                  @change="handleProvinceChange"
                  :loading="loadingProvinces"
                >
                  <a-select-option
                    v-for="province in provinces"
                    :key="province.code"
                    :value="province.name"
                  >
                    {{ province.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="Quận/Huyện" name="diaChi.quanHuyen">
                <a-select
                  v-model:value="formState.diaChi.quanHuyen"
                  placeholder="Chọn quận/huyện"
                  @change="handleDistrictChange"
                  :loading="loadingDistricts"
                  :disabled="!formState.diaChi.tinhThanhPho"
                >
                  <a-select-option
                    v-for="district in districts"
                    :key="district.code"
                    :value="district.name"
                  >
                    {{ district.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="Phường/Xã" name="diaChi.xaPhuong">
                <a-select
                  v-model:value="formState.diaChi.xaPhuong"
                  placeholder="Chọn phường/xã"
                  :loading="loadingWards"
                  :disabled="!formState.diaChi.quanHuyen"
                >
                  <a-select-option
                    v-for="ward in wards"
                    :key="ward.code"
                    :value="ward.name"
                  >
                    {{ ward.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>

            <a-col :span="12">
              <a-form-item label="Số nhà, đường" name="diaChi.duong">
                <a-input v-model:value="formState.diaChi.duong" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item name="macDinh">
            <a-checkbox v-model:checked="formState.diaChi.macDinh">
              Đặt làm địa chỉ mặc định
            </a-checkbox>
          </a-form-item>
        </div>

        <!-- Buttons -->
        <div class="form-actions">
          <a-space>
            <a-button @click="$router.push('/khach-hang/list')">
              <template #icon><CloseOutlined /></template>
              Hủy
            </a-button>
            <a-button type="primary" html-type="submit">
              <template #icon><SaveOutlined /></template>
              Lưu
            </a-button>
          </a-space>
        </div>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { message, notification } from "ant-design-vue";
import AddressService from "../../services/AddressService";
import KhachHangService from "../../services/KhachHangService";
import { CloseOutlined, SaveOutlined } from "@ant-design/icons-vue";

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);
const loadingProvinces = ref(false);
const loadingDistricts = ref(false);
const loadingWards = ref(false);

const formState = reactive({
  hoTen: "",
  gioiTinh: "true",
  ngaySinh: "",
  email: "",
  soDienThoai: "",
  ngayTao: new Date().toISOString().split("T")[0],
  diaChi: {
    tinhThanhPho: "",
    quanHuyen: "",
    xaPhuong: "",
    duong: "",
    macDinh: false,
  },
});

const validateHoTen = (_, value) => {
  if (!value) {
    return Promise.resolve();
  }
  if (value.length > 100) {
    return Promise.reject("Họ tên không được quá 100 ký tự!");
  }
  const regex = /^[\p{L}\s]+$/u;
  if (!regex.test(value)) {
    return Promise.reject("Họ tên chỉ được chứa chữ cái!");
  }
  return Promise.resolve();
};

const validateBirthDate = (_, value) => {
  if (!value) {
    return Promise.resolve();
  }
  const today = new Date();
  const birthDate = new Date(value.format("YYYY-MM-DD"));
  if (birthDate > today) {
    return Promise.reject("Ngày sinh phải là ngày trong quá khứ!");
  }
  return Promise.resolve();
};

onMounted(async () => {
  try {
    loadingProvinces.value = true;
    provinces.value = await AddressService.getProvinces();
  } catch (error) {
    message.error("Không thể tải danh sách tỉnh/thành phố. Vui lòng thử lại sau.");
    console.error("Lỗi chi tiết:", error);
  } finally {
    loadingProvinces.value = false;
  }
});

const handleProvinceChange = async (value) => {
  if (!value) {
    formState.diaChi.tinhThanhPho = undefined;
    formState.diaChi.quanHuyen = undefined;
    formState.diaChi.xaPhuong = undefined;
    return;
  }
  try {
    loadingDistricts.value = true;
    formState.diaChi.tinhThanhPho = value;
    const province = provinces.value.find((p) => p.name === value);
    if (province) {
      districts.value = await AddressService.getDistrictsByProvince(province.code);
    }
  } catch (error) {
    message.error("Không thể tải danh sách quận/huyện");
    console.error(error);
  } finally {
    loadingDistricts.value = false;
  }
};

const handleDistrictChange = async (value) => {
  formState.diaChi.quanHuyen = value;
  formState.diaChi.xaPhuong = undefined;
  wards.value = [];
  if (!value) return;
  try {
    loadingWards.value = true;
    const district = districts.value.find((d) => d.name === value);
    if (district) {
      wards.value = await AddressService.getWardsByDistrict(district.code);
    }
  } catch (error) {
    message.error("Không thể tải danh sách phường/xã");
    console.error(error);
  } finally {
    loadingWards.value = false;
  }
};

const handleSubmit = async (values) => {
  try {
    if (
      !formState.diaChi.tinhThanhPho ||
      !formState.diaChi.quanHuyen ||
      !formState.diaChi.xaPhuong
    ) {
      message.error("Vui lòng nhập đầy đủ thông tin địa chỉ!");
      return;
    }
    const submitData = {
      ...values,
      ngaySinh: values.ngaySinh ? values.ngaySinh.format("YYYY-MM-DD") : null,
      gioiTinh: values.gioiTinh === "true",
      ngayTao: formState.ngayTao,
      diaChis: [
        {
          duong: formState.diaChi.duong,
          xaPhuong: formState.diaChi.xaPhuong,
          quanHuyen: formState.diaChi.quanHuyen,
          tinhThanhPho: formState.diaChi.tinhThanhPho,
          macDinh: formState.diaChi.macDinh || false,
        },
      ],
    };
    await KhachHangService.addKhachHang(submitData);
    message.success("Thêm khách hàng thành công!");
    setTimeout(() => {
      window.location.href = "/khach-hang/list";
    }, 1500);
  } catch (error) {
    let errorMsg = "Có lỗi xảy ra khi thêm khách hàng!";
    if (error.response && error.response.data) {
      const errorData = error.response.data;
      if (errorData.email) {
        errorMsg = errorData.email; // Ví dụ: "Email đã tồn tại"
      } else if (errorData.soDienThoai) {
        errorMsg = errorData.soDienThoai; // Ví dụ: "Số điện thoại đã tồn tại"
      }
    }
    notification.error({
      message: "Lỗi thêm khách hàng",
      description: errorMsg,
    });
    console.error(error);
  }
};

const onFinishFailed = (errorInfo) => {
  console.log("Failed:", errorInfo);
  message.error("Vui lòng điền đầy đủ thông tin bắt buộc!");
};
</script>

<style scoped>
.add-customer {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.form-card {
  max-width: 1200px;
  margin: 0 auto;
}

.section-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 24px;
  color: #1f2937;
}

.form-actions {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-input),
:deep(.ant-select-selector),
:deep(.ant-picker) {
  border-radius: 6px;
}

.w-full {
  width: 100%;
}
</style>