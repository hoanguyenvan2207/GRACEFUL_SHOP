<template>
  <a-modal v-model:open="visible" :title="t('addressModal.edit.title')" @cancel="cancel" :footer="null">
    <a-form @submit.prevent="submitAddress" layout="vertical">
      <a-form-item :label="t('addressModal.edit.labels.fullName')" required>
        <a-input v-model:value="address.hoTen" :placeholder="t('addressModal.edit.placeholders.fullName')" disabled />
      </a-form-item>

      <a-form-item :label="t('addressModal.edit.labels.phone')" required>
        <a-input v-model:value="address.soDienThoai" :placeholder="t('addressModal.edit.placeholders.phone')"
          disabled />
      </a-form-item>

      <a-form-item :label="t('addressModal.edit.labels.province')" required>
        <a-select v-model:value="selectedProvince" :placeholder="t('addressModal.edit.placeholders.province')"
          @change="onProvinceChange">
          <a-select-option v-for="(province, idx) in provinces" :key="idx" :value="province.code"
            :label="province.name">
            {{ province.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.edit.labels.district')" required>
        <a-select v-model:value="selectedDistrict" :placeholder="t('addressModal.edit.placeholders.district')"
          @change="onDistrictChange" :disabled="!selectedProvince">
          <a-select-option v-for="(district, idx) in districts" :key="idx" :value="district.code"
            :label="district.name">
            {{ district.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.edit.labels.ward')" required>
        <a-select v-model:value="selectedWard" :placeholder="t('addressModal.edit.placeholders.ward')"
          @change="onWardChange" :disabled="!selectedDistrict">
          <a-select-option v-for="(ward, idx) in wards" :key="idx" :value="ward.code" :label="ward.name">
            {{ ward.name }}
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-form-item :label="t('addressModal.edit.labels.detailAddress')" required>
        <a-input v-model:value="address.duong" :placeholder="t('addressModal.edit.placeholders.detailAddress')" />
      </a-form-item>

      <a-form-item>
        <a-checkbox v-model:checked="address.macDinh">
          {{ t('addressModal.edit.labels.defaultAddress') }}
        </a-checkbox>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit"> {{ t('addressModal.edit.button.save') }} </a-button>
        <a-button style="margin-left: 8px" @click="cancel"> {{ t('addressModal.edit.button.cancel') }} </a-button>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { notification } from "ant-design-vue";
import DiaChiService from "../../../services/DiaChiService";
import AddressService from "../../../services/AddressService";
import AuthService from "../../../services/AuthService";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const props = defineProps({
  addressId: {
    type: Number,
    required: true,
  },
});

const emit = defineEmits(["update-address", "cancel"]);
const visible = ref(true);
const address = ref({
  id: null,
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
    const provincesData = await AddressService.getProvinces();
    provinces.value = provincesData;
    await fetchAddressDetails();
    const customerInfo = await AuthService.getCustomerInfo();
    // console.log("Customer info:", customerInfo);
    if ((!address.value.hoTen || address.value.hoTen === "") && customerInfo.hoTen) {
      address.value.hoTen = customerInfo.hoTen;
    }
    if ((!address.value.soDienThoai || address.value.soDienThoai === "") && customerInfo.soDienThoai) {
      address.value.soDienThoai = customerInfo.soDienThoai;
    }
    console.log("Sau khi gán, address:", address.value);
  } catch (error) {
    console.error("Lỗi tải dữ liệu:", error);
    notification.error({
      message: "Lỗi",
      description: "Không thể tải dữ liệu. Vui lòng thử lại sau.",
    });
  }
});



async function fetchAddressDetails() {
  try {
    const response = await DiaChiService.getDiaChiById(props.addressId);
    const addressData = response.data;

    address.value = {
      id: addressData.id,
      hoTen: addressData.hoTen,
      soDienThoai: addressData.soDienThoai,
      tinhThanhPho: addressData.tinhThanhPho,
      quanHuyen: addressData.quanHuyen,
      xaPhuong: addressData.xaPhuong,
      duong: addressData.duong,
      macDinh: addressData.macDinh,
    };

    const province = provinces.value.find(
      (p) => p.name === addressData.tinhThanhPho
    );
    if (province) {
      selectedProvince.value = province.code;
      await onProvinceChange(province.code);
      const district = districts.value.find(
        (d) => d.name === addressData.quanHuyen
      );
      if (district) {
        selectedDistrict.value = district.code;
        await onDistrictChange(district.code);
        const ward = wards.value.find((w) => w.name === addressData.xaPhuong);
        if (ward) {
          selectedWard.value = ward.code;
        }
      }
    }
  } catch (error) {
    console.error("Lỗi khi lấy thông tin địa chỉ:", error);
    notification.error({
      message: t("addressModal.edit.notification.error.title"),
      description: t("addressModal.edit.notification.error.fetch"),
    });
  }
}

async function onProvinceChange(provinceCode) {
  try {
    const province = provinces.value.find((p) => p.code === provinceCode);
    if (province) {
      address.value.tinhThanhPho = province.name;
    }

    const districtsData = await AddressService.getDistrictsByProvince(provinceCode);
    districts.value = districtsData;

    if (!province) {
      selectedDistrict.value = "";
      address.value.quanHuyen = "";
      wards.value = [];
      selectedWard.value = "";
      address.value.xaPhuong = "";
    }
  } catch (error) {
    console.error("Lỗi tải danh sách quận/huyện:", error);
    notification.error({
      message: t("addressModal.edit.notification.error.title"),
      description: t("addressModal.edit.notification.error.district"),
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

    if (!district) {
      selectedWard.value = "";
      address.value.xaPhuong = "";
    }
  } catch (error) {
    console.error("Lỗi tải danh sách phường/xã:", error);
    notification.error({
      message: t("addressModal.edit.notification.error.title"),
      description: t("addressModal.edit.notification.error.ward"),
    });
  }
}

function onWardChange(wardCode) {
  const ward = wards.value.find((w) => w.code === wardCode);
  if (ward) {
    address.value.xaPhuong = ward.name;
  }
}

function submitAddress() {
  if (
    !address.value.hoTen ||
    !address.value.soDienThoai ||
    !address.value.duong ||
    !address.value.tinhThanhPho ||
    !address.value.quanHuyen ||
    !address.value.xaPhuong
  ) {
    notification.error({
      message: t("addressModal.edit.notification.error.title"),
      description: t("addressModal.edit.notification.error.missing"),
    });
    return;
  }

  DiaChiService.updateDiaChi(address.value.id, address.value)
    .then((response) => {
      notification.success({
        message: t("addressModal.edit.notification.success.title"),
        description: t("addressModal.edit.notification.success.updated"),
      });
      emit("update-address", response.data);
    })
    .catch((error) => {
      console.error(
        "Lỗi khi cập nhật địa chỉ:",
        error.response ? error.response.data : error
      );
      notification.error({
        message: t("addressModal.edit.notification.error.title"),
        description:
          error.response?.data?.message ||
          t("addressModal.edit.notification.error.generic"),
      });
    });
}

function cancel() {
  emit("cancel");
  notification.info({
    message: t("addressModal.edit.notification.info.cancelTitle"),
    description: t("addressModal.edit.notification.info.cancelDesc"),
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