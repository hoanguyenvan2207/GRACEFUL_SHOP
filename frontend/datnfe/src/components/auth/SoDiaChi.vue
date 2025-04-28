<template>
  <div class="address-book">
    <div class="address-book-header">
      <a-button type="primary" @click="showAddForm = true">
        {{ t('account.addressBook.addNew') }}
      </a-button>
    </div>

    <div v-if="loading" class="loading-container">
      <a-spin :tip="t('account.addressBook.loading')" />
    </div>

    <a-empty v-else-if="addresses.length === 0" :description="t('account.addressBook.emptyDescription')" />

    <div v-else v-for="(addr, index) in addresses" :key="addr.id" class="address-item">
      <div class="address-info">
        <strong>{{ addr.hoTen }}</strong> ({{ addr.soDienThoai }})
        <div>{{ t('account.addressBook.addressInfo.streetLabel') }}: {{ addr.duong }}</div>
        <div>
          {{ addr.xaPhuong }}, {{ addr.quanHuyen }}, {{ addr.tinhThanhPho }}
        </div>
        <span v-if="addr.macDinh" class="default-badge">{{ t('account.addressBook.addressInfo.defaultBadge')
        }}</span>
      </div>

      <div class="address-actions">
        <a-button type="link" @click="editAddress(addr.id)">{{ t('account.addressBook.actions.update')
        }}</a-button>
        <a-button type="link" v-if="!addr.macDinh" @click="setDefault(addr.id)">
          {{ t('account.addressBook.actions.setDefault') }}
        </a-button>
        <a-popconfirm :title="t('account.addressBook.deleteConfirm.title')"
          :ok-text="t('account.addressBook.deleteConfirm.okText')"
          :cancel-text="t('account.addressBook.deleteConfirm.cancelText')" @confirm="removeAddress(addr.id)">
          <a-button type="link" danger>{{ t('account.addressBook.actions.delete') }}</a-button>
        </a-popconfirm>
      </div>
    </div>

    <AddDiaChi v-if="showAddForm" @add-address="handleAddAddress" @cancel="cancelAdd" />

    <EditDiaChi v-if="showEditForm" :address-id="selectedAddressId" @update-address="handleUpdateAddress"
      @cancel="cancelEdit" />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { notification } from "ant-design-vue";
import AuthService from "../../services/AuthService";
import AddDiaChi from "./diachi/AddDiaChi.vue";
import EditDiaChi from "./diachi/EditDiaChi.vue";
import DiaChiService from "../../services/DiaChiService";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const addresses = ref([]);
const loading = ref(true);
const showAddForm = ref(false);
const showEditForm = ref(false);
const selectedAddressId = ref(null);

onMounted(async () => {
  await fetchUserAddresses();
});

async function fetchUserAddresses() {
  try {
    loading.value = true;
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser || !currentUser.id) {
      notification.error({
        message: "Lỗi",
        description: "Bạn cần đăng nhập để xem danh sách địa chỉ.",
      });
      loading.value = false;
      return;
    }
    const khachHangId = currentUser.id;
    const response = await DiaChiService.getDiaChiByKhachHangId(khachHangId);
    addresses.value = response.data;
    if (addresses.value.length > 0) {
      console.log("Danh sách địa chỉ đã được tải:", addresses.value);
    }
  } catch (error) {
    console.error("Lỗi khi tải danh sách địa chỉ:", error);
    notification.error({
      message: t('account.addressBook.notification.error.title'),
      description:
        "Không thể tải danh sách địa chỉ của bạn. Vui lòng thử lại sau.",
    });
  } finally {
    loading.value = false;
  }
}

async function handleAddAddress(newAddr) {
  try {
    loading.value = true;
    await fetchUserAddresses();
    showAddForm.value = false;
    notification.success({
      message: "Thành công",
      description: "Địa chỉ mới đã được thêm vào danh sách của bạn.",
    });
  } catch (error) {
    console.error("Lỗi sau khi thêm địa chỉ:", error);
    notification.error({
      message: "Lỗi",
      description:
        "Đã thêm địa chỉ nhưng không thể cập nhật danh sách. Vui lòng tải lại trang.",
    });
  } finally {
    loading.value = false;
  }
}

function cancelAdd() {
  showAddForm.value = false;
}

async function removeAddress(id) {
  try {
    loading.value = true;
    await DiaChiService.deleteDiaChi(id);
    await fetchUserAddresses();
    notification.success({
      message: t('account.addressBook.notification.success.title'),
      description: t('account.addressBook.notification.success.description'),
    });
  } catch (error) {
    console.error("Lỗi khi xóa địa chỉ:", error);
    const errorMessage = error.response?.data || "Không thể xóa địa chỉ. Vui lòng thử lại sau.";
    notification.error({
      message: "Lỗi",
      description: errorMessage,
    });
  } finally {
    loading.value = false;
  }
}

function editAddress(id) {
  selectedAddressId.value = id;
  showEditForm.value = true;
}

async function handleUpdateAddress() {
  try {
    loading.value = true;
    await fetchUserAddresses();
    showEditForm.value = false;
    selectedAddressId.value = null;
  } catch (error) {
    console.error("Lỗi sau khi cập nhật địa chỉ:", error);
    notification.error({
      message: "Lỗi",
      description:
        "Đã cập nhật địa chỉ nhưng không thể cập nhật danh sách. Vui lòng tải lại trang.",
    });
  } finally {
    loading.value = false;
  }
}

function cancelEdit() {
  showEditForm.value = false;
  selectedAddressId.value = null;
}

async function setDefault(id) {
  try {
    loading.value = true;
    await DiaChiService.setMacDinh(id);
    await fetchUserAddresses();
    notification.success({
      message: "Thành công",
      description: "Đã thiết lập địa chỉ mặc định.",
    });
  } catch (error) {
    console.error("Lỗi khi thiết lập địa chỉ mặc định:", error);
    notification.error({
      message: "Lỗi",
      description: "Không thể thiết lập địa chỉ mặc định. Vui lòng thử lại sau.",
    });
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.address-book {
  background-color: #fff;
  padding: 1.5rem;
  border-radius: 5px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-height: 90vh;
  overflow-y: auto;
}

.address-book h2 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.5rem;
}

.address-book-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 2rem 0;
}

.address-item {
  display: flex;
  justify-content: space-between;
  border: 1px solid #eee;
  border-radius: 5px;
  padding: 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.address-item:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 5px rgba(24, 144, 255, 0.1);
}

.address-info {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  flex: 1;
}

.default-badge {
  display: inline-block;
  margin-top: 0.5rem;
  color: #1890ff;
  font-weight: bold;
  background-color: #e6f7ff;
  padding: 2px 8px;
  border-radius: 2px;
  font-size: 0.85rem;
}

.address-actions {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: flex-end;
}
</style>
