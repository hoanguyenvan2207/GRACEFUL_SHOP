<template>
  <div class="edit-container">
    <a-card title="Chỉnh sửa thông tin khách hàng" class="edit-card">
      <a-form
        :model="formState"
        @finish="updateKhachHang"
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

            <a-col :span="12">
              <a-form-item label="Giới tính" name="gioiTinh">
                <a-radio-group v-model:value="formState.gioiTinh">
                  <a-radio value="true">Nam</a-radio>
                  <a-radio value="false">Nữ</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <!-- Thông tin địa chỉ với 2 tab -->
        <div class="section-card">
          <h4 class="section-title">Thông tin địa chỉ</h4>
          <a-tabs default-active-key="1">
            <!-- Tab chỉnh sửa địa chỉ -->
            <a-tab-pane key="1" tab="Chỉnh sửa địa chỉ">
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
            </a-tab-pane>

            <!-- Tab thêm địa chỉ mới -->
            <a-tab-pane key="2" tab="Thêm địa chỉ mới">
              <a-row :gutter="16">
                <a-col :span="12">
                  <a-form-item label="Tỉnh/Thành phố" name="newAddress.tinhThanhPho">
                    <a-select
                      v-model:value="newAddress.tinhThanhPho"
                      placeholder="Chọn tỉnh/thành phố"
                      @change="handleProvinceChangeNew"
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
                  <a-form-item label="Quận/Huyện" name="newAddress.quanHuyen">
                    <a-select
                      v-model:value="newAddress.quanHuyen"
                      placeholder="Chọn quận/huyện"
                      @change="handleDistrictChangeNew"
                      :loading="loadingDistrictsNew"
                      :disabled="!newAddress.tinhThanhPho"
                    >
                      <a-select-option
                        v-for="district in newDistricts"
                        :key="district.code"
                        :value="district.name"
                      >
                        {{ district.name }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>

                <a-col :span="12">
                  <a-form-item label="Phường/Xã" name="newAddress.xaPhuong">
                    <a-select
                      v-model:value="newAddress.xaPhuong"
                      placeholder="Chọn phường/xã"
                      :loading="loadingWardsNew"
                      :disabled="!newAddress.quanHuyen"
                    >
                      <a-select-option
                        v-for="ward in newWards"
                        :key="ward.code"
                        :value="ward.name"
                      >
                        {{ ward.name }}
                      </a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>

                <a-col :span="12">
                  <a-form-item label="Số nhà, đường" name="newAddress.duong">
                    <a-input v-model:value="newAddress.duong" />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item name="macDinhNew">
                <a-checkbox v-model:checked="newAddress.macDinh">
                  Đặt làm địa chỉ mặc định
                </a-checkbox>
              </a-form-item>
            </a-tab-pane>
          </a-tabs>
        </div>

        <!-- Nút thao tác -->
        <div class="form-actions">
          <a-space>
            <a-button @click="cancelEdit">
              <template #icon><CloseOutlined /></template>
              Hủy
            </a-button>
            <a-button type="primary" html-type="submit">
              <template #icon><SaveOutlined /></template>
              Cập nhật
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
import { useRouter, useRoute } from "vue-router";
import AddressService from "../../services/AddressService";
import KhachHangService from "../../services/KhachHangService";
import { CloseOutlined, SaveOutlined } from "@ant-design/icons-vue";
import dayjs from "dayjs";

const router = useRouter();
const route = useRoute();

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
  diaChi: {
    id: null,
    tinhThanhPho: "",
    quanHuyen: "",
    xaPhuong: "",
    duong: "",
    macDinh: false,
  },
});

const newAddress = reactive({
  tinhThanhPho: "",
  quanHuyen: "",
  xaPhuong: "",
  duong: "",
  macDinh: false,
});
const newDistricts = ref([]);
const newWards = ref([]);
const loadingDistrictsNew = ref(false);
const loadingWardsNew = ref(false);

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

    const id = route.params.id;
    if (id) {
      const response = await KhachHangService.getKhachHangById(id);
      const khachHang = response.data;

      formState.ngaySinh = khachHang?.ngaySinh ? dayjs(khachHang.ngaySinh) : null;
      formState.hoTen = khachHang?.hoTen ?? "";
      formState.email = khachHang?.email ?? "";
      formState.soDienThoai = khachHang?.soDienThoai ?? "";
      formState.gioiTinh = (khachHang?.gioiTinh ?? true).toString();

      if (khachHang?.diaChis && khachHang.diaChis.length > 0) {
        const diaChi = khachHang.diaChis.find((dc) => dc.macDinh) || khachHang.diaChis[0];
        formState.diaChi = {
          id: diaChi?.id ?? null,
          tinhThanhPho: diaChi?.tinhThanhPho ?? "",
          quanHuyen: diaChi?.quanHuyen ?? "",
          xaPhuong: diaChi?.xaPhuong ?? "",
          duong: diaChi?.duong ?? "",
          macDinh: diaChi?.macDinh ?? false,
        };

        if (diaChi?.tinhThanhPho) {
          await handleProvinceChange(diaChi.tinhThanhPho);
          if (diaChi?.quanHuyen) {
            setTimeout(() => {
              formState.diaChi.xaPhuong = diaChi?.xaPhuong ?? "";
            }, 300);
          }
        }
      }
    }
  } catch (error) {
    console.error("Lỗi:", error);
    message.error("Không thể tải thông tin khách hàng");
  } finally {
    loadingProvinces.value = false;
  }
});

const handleProvinceChange = async (provinceName) => {
  try {
    loadingDistricts.value = true;
    formState.diaChi.tinhThanhPho = provinceName;
    const province = provinces.value.find((p) => p.name === provinceName);
    if (province) {
      districts.value = await AddressService.getDistrictsByProvince(province.code.toString());
    }
  } catch (error) {
    console.error("Lỗi load quận/huyện:", error);
    message.error("Không thể tải danh sách quận/huyện");
  } finally {
    loadingDistricts.value = false;
  }
};

const handleDistrictChange = async (value) => {
  try {
    loadingWards.value = true;
    formState.diaChi.quanHuyen = value;
    const district = districts.value.find((d) => d.name === value);
    if (district) {
      const wardsData = await AddressService.getWardsByDistrict(district.code);
      wards.value = wardsData;
    }
  } catch (error) {
    console.error("Lỗi load phường/xã:", error);
    message.error("Không thể tải danh sách phường/xã");
  } finally {
    loadingWards.value = false;
  }
};

const handleProvinceChangeNew = async (provinceName) => {
  try {
    loadingDistrictsNew.value = true;
    newAddress.tinhThanhPho = provinceName;
    const province = provinces.value.find((p) => p.name === provinceName);
    if (province) {
      newDistricts.value = await AddressService.getDistrictsByProvince(province.code.toString());
    }
  } catch (error) {
    console.error("Lỗi load quận/huyện:", error);
    message.error("Không thể tải danh sách quận/huyện");
  } finally {
    loadingDistrictsNew.value = false;
  }
};

const handleDistrictChangeNew = async (value) => {
  try {
    loadingWardsNew.value = true;
    newAddress.quanHuyen = value;
    const district = newDistricts.value.find((d) => d.name === value);
    if (district) {
      const wardsData = await AddressService.getWardsByDistrict(district.code);
      newWards.value = wardsData;
    }
  } catch (error) {
    console.error("Lỗi load phường/xã:", error);
    message.error("Không thể tải danh sách phường/xã");
  } finally {
    loadingWardsNew.value = false;
  }
};

const updateKhachHang = async (values) => {
  try {
    const addresses = [];

    if (formState.diaChi.tinhThanhPho || formState.diaChi.duong) {
      addresses.push({
        id: formState.diaChi.id,
        duong: formState.diaChi.duong,
        xaPhuong: formState.diaChi.xaPhuong,
        quanHuyen: formState.diaChi.quanHuyen,
        tinhThanhPho: formState.diaChi.tinhThanhPho,
        macDinh: formState.diaChi.macDinh || false,
      });
    }

    if (newAddress.tinhThanhPho || newAddress.duong) {
      addresses.push({
        duong: newAddress.duong,
        xaPhuong: newAddress.xaPhuong,
        quanHuyen: newAddress.quanHuyen,
        tinhThanhPho: newAddress.tinhThanhPho,
        macDinh: newAddress.macDinh || false,
      });
    }

    const submitData = {
      ...values,
      id: route.params.id,
      ngaySinh: values.ngaySinh ? values.ngaySinh.format("YYYY-MM-DD") : null,
      gioiTinh: values.gioiTinh === "true",
      diaChis: addresses,
    };

    await KhachHangService.updateKhachHang(route.params.id, submitData);
    message.success("Cập nhật khách hàng thành công!");
    setTimeout(() => {
      router.push("/khach-hang/list");
    }, 1500);
  } catch (error) {
    let errorMsg = "Có lỗi xảy ra khi cập nhật khách hàng!";
    if (error.response && error.response.data) {
      const errorData = error.response.data;
      if (errorData.email) {
        errorMsg = errorData.email; // Ví dụ: "Email đã tồn tại"
      } else if (errorData.soDienThoai) {
        errorMsg = errorData.soDienThoai; // Ví dụ: "Số điện thoại đã tồn tại"
      }
    }
    notification.error({
      message: "Lỗi cập nhật khách hàng",
      description: errorMsg,
    });
    console.error(error);
  }
};

const onFinishFailed = (errorInfo) => {
  console.log("Failed:", errorInfo);
  message.error("Vui lòng điền đầy đủ thông tin bắt buộc!");
};

const cancelEdit = () => {
  router.push("/khach-hang/list");
};
</script>

<style scoped>
.edit-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.edit-card {
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