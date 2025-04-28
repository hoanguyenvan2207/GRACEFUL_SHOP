<template>
  <a-modal v-model:open="visible" :title="t('addressModal.add.title')" @cancel="cancel" :footer="null">
    <a-form @submit.prevent="submitAddress" layout="vertical">
      <a-form-item :label="t('addressModal.add.labels.fullName')" required>
        <a-input v-model:value="address.hoTen" :placeholder="t('addressModal.add.placeholders.fullName')" disabled />
      </a-form-item>

      <a-form-item :label="t('addressModal.add.labels.phone')" required>
        <a-input v-model:value="address.soDienThoai" :placeholder="t('addressModal.add.placeholders.phone')" disabled />
      </a-form-item>

      <a-form-item :label="t('addressModal.add.labels.province')" required>
        <a-select v-model:value="selectedProvince" :placeholder="t('addressModal.add.placeholders.province')"
          @change="onProvinceChange">
          <a-select-option v-for="(province, idx) in provinces" :key="idx" :value="province.code"
            :label="province.name">
            {{ province.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.add.labels.district')" required>
        <a-select v-model:value="selectedDistrict" :placeholder="t('addressModal.add.placeholders.district')"
          @change="onDistrictChange" :disabled="!selectedProvince">
          <a-select-option v-for="(district, idx) in districts" :key="idx" :value="district.code"
            :label="district.name">
            {{ district.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.add.labels.ward')" required>
        <a-select v-model:value="selectedWard" :placeholder="t('addressModal.add.placeholders.ward')"
          @change="onWardChange" :disabled="!selectedDistrict">
          <a-select-option v-for="(ward, idx) in wards" :key="idx" :value="ward.code" :label="ward.name">
            {{ ward.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.add.labels.detailAddress')" required>
        <a-input v-model:value="address.duong" :placeholder="t('addressModal.add.placeholders.detailAddress')" />
      </a-form-item>

      <a-form-item>
        <a-checkbox v-model:checked="address.macDinh">
          {{ t('addressModal.add.labels.defaultAddress') }}
        </a-checkbox>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit">
          {{ t('addressModal.add.button.save') }}
        </a-button>
        <a-button style="margin-left: 8px" @click="cancel">
          {{ t('addressModal.add.button.cancel') }}
        </a-button>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useI18n } from "vue-i18n";
import { notification } from "ant-design-vue";
import DiaChiService from "../../../services/DiaChiService";
import AddressService from "../../../services/AddressService";
import AuthService from "../../../services/AuthService";

const { t } = useI18n();
const emit = defineEmits(["add-address", "cancel"]);
const visible = ref(true);

const address = ref({
  hoTen: "",
  soDienThoai: "",
  tinhThanhPho: "",
  quanHuyen: "",
  xaPhuong: "",
  duong: "",
  macDinh: false,
});

const selectedProvince = ref("");
const selectedDistrict = ref("");
const selectedWard = ref("");

const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);

onMounted(async () => {
  try {
    const data = await AddressService.getProvinces();
    provinces.value = data;
    const customerInfo = await AuthService.getCustomerInfo();
    if (customerInfo) {
      if (!address.value.hoTen && customerInfo.hoTen) {
        address.value.hoTen = customerInfo.hoTen;
      }
      if (!address.value.soDienThoai && customerInfo.soDienThoai) {
        address.value.soDienThoai = customerInfo.soDienThoai;
      }
    }
  } catch (error) {
    console.error("Lỗi tải danh sách tỉnh/thành phố:", error);
    notification.error({
      message: t('addressModal.add.notification.error.title'),
      description: t('addressModal.add.notification.error.province'),
    });
  }
});

async function onProvinceChange(provinceCode) {
  try {
    const province = provinces.value.find((p) => p.code === provinceCode);
    if (province) {
      address.value.tinhThanhPho = province.name;
    }
    const districtsData = await AddressService.getDistrictsByProvince(provinceCode);
    districts.value = districtsData;
    selectedDistrict.value = "";
    address.value.quanHuyen = "";
    wards.value = [];
    selectedWard.value = "";
    address.value.xaPhuong = "";
  } catch (error) {
    console.error("Lỗi tải danh sách quận/huyện:", error);
    notification.error({
      message: t('addressModal.add.notification.error.title'),
      description: t('addressModal.add.notification.error.district'),
    });
  }
}

async function onDistrictChange(districtCode) {
  try {
    const district = districts.value.find((d) => d.code === districtCode);
    if (district) {
      address.value.quanHuyen = district.name;
    }
    const wardsData = await AddressService.getWardsByDistrict(districtCode);
    wards.value = wardsData;
    selectedWard.value = "";
    address.value.xaPhuong = "";
  } catch (error) {
    console.error("Lỗi tải danh sách phường/xã:", error);
    notification.error({
      message: t('addressModal.add.notification.error.title'),
      description: t('addressModal.add.notification.error.ward'),
    });
  }
}

function onWardChange(wardCode) {
  const ward = wards.value.find((w) => w.code === wardCode);
  if (ward) {
    address.value.xaPhuong = ward.name;
  }
}

async function submitAddress() {
  if (
    !address.value.hoTen ||
    !address.value.soDienThoai ||
    !address.value.duong ||
    !address.value.tinhThanhPho ||
    !address.value.quanHuyen ||
    !address.value.xaPhuong
  ) {
    notification.error({
      message: t('addressModal.add.notification.error.title'),
      description: t('addressModal.add.notification.error.missing'),
    });
    return;
  }

  try {
    const response = await DiaChiService.addDiaChi(address.value);
    notification.success({
      message: t('addressModal.add.notification.success.title'),
      description: t('addressModal.add.notification.success.added'),
    });
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser.hoTen || !currentUser.soDienThoai) {
      await AuthService.updateCustomer(currentUser.id, {
        id: currentUser.id,
        hoTen: address.value.hoTen,
        soDienThoai: address.value.soDienThoai
      });
    }
    emit("add-address", response.data);
  } catch (error) {
    console.error("Lỗi khi thêm địa chỉ:", error.response ? error.response.data : error);
    const errorMessage = error.response?.data || t('addressModal.add.notification.error.generic');// Lỗi 2 thông báo ở đây.Không thể thêm địa chỉ. Vui lòng thử lại sau.
    notification.error({
      message: t('addressModal.add.notification.error.title'),
      description: errorMessage === "Địa chỉ đã tồn tại" ? t('addressModal.add.notification.error.exist') : errorMessage
    });
  }
}

function cancel() {
  emit("cancel");
  notification.info({
    message: t('addressModal.add.notification.info.cancelTitle'),
    description: t('addressModal.add.notification.info.cancelDesc')
  });
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}

.ant-select {
  width: 100%;
}

.ant-btn {
  border-radius: 4px;
}

.ant-btn-primary {
  background-color: #1890ff;
}
</style>
