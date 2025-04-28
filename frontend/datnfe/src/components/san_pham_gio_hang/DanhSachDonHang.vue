<template>
  <div class="container py-4 order-container">
    <!-- Phần Tab điều hướng -->
    <div class="py-2">
      <ul class="nav nav-tabs order-tabs">
        <li class="nav-item" v-for="tab in tabs" :key="tab.value">
          <a class="nav-link" :class="{ active: currentTab === tab.value }" href="#"
            @click.prevent="filterOrders(tab.value)">
            {{ tab.label }}
          </a>
        </li>
      </ul>
    </div>

    <!-- Hiển thị danh sách đơn hàng -->
    <div v-if="orders && orders.length > 0" class="order-list">
      <div v-for="order in filteredOrders" :key="order.id" class="order-item card">
        <div class="card-body">
          <div class="order-header d-flex flex-wrap justify-content-between align-items-center">
            <div class="order-info">
              <h2 class="order-code">{{ order.maHoaDon }}</h2>
              <p class="order-date">{{ t('order-list.detail.createdDate') }} {{ formatDate(order.ngayTao) }}</p>
              <p class="order-status-label">
                {{ t('order-list.detail.status') }}
                <span class="order-status" :class="{
                  'order-status-pending': order.trangThai === 'Chờ xác nhận',
                  'order-status-confirmed': order.trangThai === 'Đã xác nhận',
                  'order-status-shipping': order.trangThai === 'Đang giao hàng',
                  'order-status-completed': order.trangThai === 'Đã nhận hàng',
                  'order-status-cancel': order.trangThai === 'Đã hủy đơn'
                }">
                  {{ t(`order-list.tabs.${getTranslatedStatus(order.trangThai)}`) }}
                </span>
              </p>


            </div>
            <!-- Nút "Chi tiết" để mở modal -->
            <div class="order-actions text-end">
              <button class="btn btn-detail" @click.stop="openOrderDetailModal(order)">
                <i class="fas fa-eye"></i> {{ t('order-list.actions.detail') }}
              </button>
              <p class="order-total">
                {{ t('order-list.list.orderTotal') }} <span>{{ formatCurrency(order.tongTien) }}</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="no-orders text-center">
      <p>{{ t('order-list.list.noOrders') }}</p>
    </div>
  </div>

  <!-- Modal chi tiết đơn hàng sử dụng a-modal của Ant Design Vue -->
  <a-modal v-model:open="isModalVisible" style="width: 1100px;" @cancel="handleModalCancel" :footer="null">
    <h5 class="text-center text-uppercase mb-5">{{ t('order-list.detail.title') }}</h5>
    <div class="steps-container" v-if="selectedOrder">
      <ul class="steps">
        <li v-for="(item, index) in steps.map((step, i) => ({ step, i }))
          .filter(item =>
            selectedOrder.trangThai === 'Đã hủy đơn' ||
            stepDefinitions[item.i].value !== 'Đã huỷ đơn')" :key="item.i" :class="{
              active: item.i <= currentStep && selectedOrder.trangThai !== 'Đã hủy đơn',
              canceled: selectedOrder.trangThai === 'Đã hủy đơn' && item.i > currentStep && stepDefinitions[item.i].value !== 'Đã huỷ đơn'
            }">
          <div class="step-circle">
            <template
              v-if="selectedOrder.trangThai === 'Đã hủy đơn' && item.i > currentStep && stepDefinitions[item.i].value !== 'Đã huỷ đơn'">
              ❌
            </template>
            <template v-else>
              {{ item.i + 1 }}
            </template>
          </div>
          <div class="step-title">{{ item.step }}</div>
        </li>
      </ul>
    </div>
    <div class="modal-cancel-button text-center" v-if="selectedOrder && selectedOrder.trangThai === 'Chờ xác nhận'">
      <button class="btn btn-cancel" @click.stop="cancelOrder(selectedOrder.id)">
        <i class="fas fa-trash-alt"></i> {{ t('order-list.detail.cancelOrder') }}
      </button>
    </div>
    <div class="container py-4" v-if="selectedOrder">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <div class="text-center">
          <span class="order-detail-code">{{ $t('order-list.detail.orderCode') }} {{ selectedOrder.maHoaDon }}</span>
        </div>
        <div>
          <span class="order-detail-status text-uppercase">
            {{ selectedOrder.trangThai }}
          </span>
        </div>
      </div>
      <hr />
      <!-- Thông tin địa chỉ nhận hàng -->
      <div class="order-delivery mt-4">
        <h2 class="h5 mb-3">
          <i class="bi bi-geo-alt"></i> {{ t('order-list.detail.deliveryAddressTitle') }}
        </h2>
        <p class="fw-bold">
          {{ selectedOrder.khachHang.hoTen }} - {{ selectedOrder.khachHang.soDienThoai }}
        </p>
        <p>{{ t('order-list.detail.addressLabel') }} {{ selectedOrder.diaChiGiaoHang }}</p>
      </div>
      <hr />
      <!-- Hiển thị danh sách sản phẩm trong đơn hàng -->
      <div class="order-product d-flex justify-content-between align-items-center mb-3 rounded p-3"
        v-for="ct in selectedOrder.chiTietHoaDon" :key="ct.id">
        <div class="d-flex align-items-center">
          <img :src="ct.aoDaiChiTiet.anhUrl || 'https://placehold.jp/800x1200.png'" alt="Product image"
            class="product-image me-4 rounded" />
          <div class="product-info">
            <p><strong>{{ t('order-list.detail.product.code') }}</strong> {{ ct.aoDaiChiTiet.maAoDaiChiTiet }}</p>
            <h2 class="h6 fw-bold">{{ ct.aoDaiChiTiet.aoDai.tenAoDai }}</h2>
            <p class="text-muted mb-1">
              <strong>{{ t('order-list.detail.product.classification') }}</strong>
              {{ t('order-list.detail.product.color') }} {{ ct.aoDaiChiTiet.mauSac.tenMauSac }}, {{
                t('order-list.detail.product.size') }} {{
                ct.aoDaiChiTiet.kichThuoc.ten }}
            </p>
            <p class="mb-0">{{ t('order-list.detail.product.quantity') }} x{{ ct.soLuong }}</p>
          </div>
        </div>
        <div class="d-flex justify-content-end text-end">
          <p v-if="ct.aoDaiChiTiet.giaGoc !== ct.giaBan" class="line-through text-muted me-1">
            <del>{{ formatCurrency(ct.aoDaiChiTiet.giaGoc) }}</del>
          </p>
          <p class="fw-bold text-danger">{{ formatCurrency(ct.aoDaiChiTiet.giaBan) }}</p>
        </div>
      </div>
      <div class="order-summary border-top pt-3">
        <div class="d-flex justify-content-between py-2">
          <span>{{ t('order-list.detail.summary.itemsTotal') }}</span>
          <span>{{ formatCurrency(selectedOrder.tienTruocGiam - selectedOrder.phiGiaoHang) }}</span>
        </div>
        <div class="d-flex justify-content-between py-2">
          <span>{{ t('order-list.detail.summary.shippingFee') }}</span>
          <span>{{ formatCurrency(selectedOrder.phiGiaoHang) }}</span>
        </div>
        <div class="d-flex justify-content-between py-2">
          <span>{{ t('order-list.detail.summary.totalBeforeDiscount') }}</span>
          <span>{{ formatCurrency(selectedOrder.tienTruocGiam) }}</span>
        </div>
        <div class="d-flex justify-content-between py-2">
          <span>{{ t('order-list.detail.summary.voucher') }}</span>
          <span>-{{ formatCurrency(calculateVoucherAmount(selectedOrder)) }}</span>
        </div>
        <div class="d-flex justify-content-between py-2 text-orange fw-bold">
          <span>{{ t('order-list.detail.summary.grandTotal') }}</span>
          <span>{{ formatCurrency(selectedOrder.tongTien) }}</span>
        </div>
        <div class="d-flex justify-content-between py-2 text-muted">
          <span>{{ t('order-list.detail.paymentMethod.label') }}</span>
          <span>
            {{ selectedOrder.phuongThucThanhToanOnline ? t('order-list.detail.paymentMethod.transfer') :
              t('order-list.detail.paymentMethod.cod') }}
          </span>
        </div>
        <div v-if="selectedOrder.ghiChu" class="d-flex justify-content-between py-2">
          <span>{{ t('order-list.detail.orderNote') }}</span>
          <span>{{ selectedOrder.ghiChu }}</span>
        </div>
      </div>
    </div>
    <div v-else>
      <p>{{ t('order-list.detail.noInfo') }}</p>
    </div>
  </a-modal>

  <!-- Modal nhập lý do hủy đơn -->
  <a-modal v-model:open="isCancelModalVisible" :title="t('order-list.cancel.modal.title')"
    @cancel="() => (isCancelModalVisible.value = false)" :footer="null">
    <div class="reason-modal">
      <textarea v-model="cancellationReason" rows="3" :placeholder="t('order-list.cancel.modal.placeholder')"
        class="reason-textarea"></textarea>
      <div class="modal-btn-group">
        <button class="btn-confirm" @click="confirmCancel">{{ t('order-list.cancel.modal.confirm') }}</button>
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { notification, Modal } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();

const orders = ref([]);
const isModalVisible = ref(false);
const selectedOrder = ref(null);
const currentTab = ref('all');
const idKhachHang = sessionStorage.getItem('userId');

const getTranslatedStatus = (status) => {
  const statusMap = {
    'Chờ xác nhận': 'pending',
    'Đã xác nhận': 'confirmed',
    'Đang giao hàng': 'shipping',
    'Đã nhận hàng': 'completed',
    'Đã thanh toán': 'paid',
    'Đã hủy đơn': 'cancelled'
  };
  return statusMap[status] || '';
};


const tabs = computed(() => [
  { value: 'all', label: t('order-list.tabs.all') },
  { value: 'pending', label: t('order-list.tabs.pending') },
  { value: 'confirmed', label: t('order-list.tabs.confirmed') },
  { value: 'shipping', label: t('order-list.tabs.shipping') },
  { value: 'completed', label: t('order-list.tabs.completed') },
  { value: 'cancelled', label: t('order-list.tabs.cancelled') }
]);

const stepDefinitions = [
  { value: 'Chờ xác nhận', key: 'order-list.detail.step.pending' },
  { value: 'Đã xác nhận', key: 'order-list.detail.step.confirmed' },
  { value: 'Đang giao hàng', key: 'order-list.detail.step.shipping' },
  { value: 'Đã nhận hàng', key: 'order-list.detail.step.completed' },
  { value: 'Đã huỷ đơn', key: 'order-list.detail.step.cancelled' }
];

const steps = computed(() => stepDefinitions.map(step => t(step.key)));

const currentStep = computed(() => {
  if (!selectedOrder.value) return 0;
  if (selectedOrder.value.trangThai === "Đã hủy đơn") return -1;
  return stepDefinitions.findIndex(step => step.value === selectedOrder.value.trangThai);
});

const isCancelModalVisible = ref(false);
const cancellationReason = ref("");

const fetchOrders = async () => {
  try {
    const response = await axios.get('/api/ban-hang-online/don-hang', {
      params: { idKhachHang }
    });
    orders.value = response.data;
  } catch (error) {
    console.error("Error fetching orders:", error);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString('vi-VN');
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
};

const openOrderDetailModal = (order) => {
  selectedOrder.value = order;
  isModalVisible.value = true;
};

const handleModalCancel = () => {
  isModalVisible.value = false;
};

function calculateVoucherAmount(order) {
  return order.tienTruocGiam && order.tongTien
    ? (order.tienTruocGiam - order.tongTien)
    : 0;
}

const filteredOrders = computed(() => {
  if (currentTab.value === 'all') {
    return orders.value;
  } else if (currentTab.value === 'pending') {
    return orders.value.filter((o) => o.trangThai === 'Chờ xác nhận');
  } else if (currentTab.value === 'confirmed') {
    return orders.value.filter((o) => o.trangThai === 'Đã xác nhận');
  } else if (currentTab.value === 'shipping') {
    return orders.value.filter((o) => o.trangThai === 'Đang giao hàng');
  } else if (currentTab.value === 'completed') {
    return orders.value.filter((o) => o.trangThai === 'Đã nhận hàng');
  }
  else if (currentTab.value === 'cancelled') {
    return orders.value.filter((o) => o.trangThai === 'Đã hủy đơn');
  }
  return orders.value;
});

function filterOrders(tab) {
  currentTab.value = tab;
}

const cancelOrder = (orderId) => {
  const order = orders.value.find(o => o.id === orderId);
  if (!order || order.trangThai !== "Chờ xác nhận") {
    notification.warning({
      message: t('order-list.cancel.notAllowed.title'),
      description: t('order-list.cancel.notAllowed.description')
    });
    return;
  }
  cancellationReason.value = "";
  isCancelModalVisible.value = true;
};

const confirmCancel = async () => {
  if (!cancellationReason.value.trim()) {
    notification.warning({
      message: t('order-list.cancel.warning.title'),
      description: t('order-list.cancel.warning.description')
    });
    return;
  }

  Modal.confirm({
    title: t('order-list.cancel.confirm.title'),
    content: t('order-list.cancel.confirm.content'),
    async onOk() {
      try {
        await axios.patch("/api/ban-hang-online/huy-don-khach-hang", [selectedOrder.value.id], {
          params: { lyDoHuy: cancellationReason.value }
        });
        notification.success({
          message: t('order-list.cancel.success.title'),
          description: t('order-list.cancel.success.description')
        });
        if (selectedOrder.value) {
          selectedOrder.value.trangThai = "Đã hủy đơn";
        }
        await fetchOrders();
        isCancelModalVisible.value = false;
        isModalVisible.value = false;
      } catch (error) {
        const errorMsg = error.response && error.response.data
          ? error.response.data
          : t('order-list.cancel.error.description');
        notification.error({
          message: t('order-list.cancel.error.title'),
          description: errorMsg,
        });
      }
    }
  });
};

onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.order-container {
  max-width: 1260px;
  margin: 0 auto;
  background: #fefefe;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.05);
}

.order-tabs {
  border-bottom: none;
}

.order-tabs .nav-link {
  font-weight: 500;
  color: #555;
  border: none;
  background: transparent;
  transition: all 0.3s ease;
  margin-right: 10px;
  padding: 8px 16px;
  border-radius: 20px;
}

.order-tabs .nav-link.active {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: #fff;
}

.order-tabs .nav-link:hover {
  background: rgba(37, 117, 252, 0.1);
  color: #2575fc;
}

.order-list {
  margin-top: 20px;
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.order-item.card {
  border: none;
  border-radius: 15px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background: #fff;
}

.order-item.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 20px;
}

.order-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  flex: 1;
}

.order-code {
  font-size: 1.3rem;
  color: #333;
  margin-bottom: 6px;
}

.order-date {
  font-size: 0.9rem;
  color: #888;
  margin-bottom: 8px;
}

.order-status-label {
  font-size: 1rem;
  font-weight: 500;
}

.order-status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
}

.order-status-pending {
  color: #f39c12;
}

.order-status-confirmed {
  color: #2980b9;
}

.order-status-shipping {
  color: #8e44ad;
}

.order-status-completed {
  color: #27ae60;
}

.order-status-cancel {
  color: #e74c3c;
}

.order-actions {
  text-align: right;
}

.btn-detail {
  background: linear-gradient(135deg, #2575fc 0%, #6a11cb 100%);
  border: none;
  color: #fff;
  padding: 8px 18px;
  border-radius: 20px;
  font-weight: 500;
  transition: background 0.3s ease;
  margin-bottom: 8px;
}

.btn-detail:hover {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
}

.order-total {
  font-size: 1.1rem;
  color: #e74c3c;
  font-weight: bold;
}

/* Modal & Step Indicator */
.steps-container {
  margin-bottom: 20px;
  padding: 0 10px;
}

.steps {
  display: flex;
  justify-content: space-between;
  padding: 0;
  margin: 0;
  list-style: none;
}

.steps li {
  text-align: center;
  position: relative;
  flex: 1;
}

.steps li::after {
  content: "";
  position: absolute;
  top: 15px;
  right: -50%;
  width: 100%;
  height: 2px;
  background: #e9ecef;
  z-index: -1;
}

.steps li:last-child::after {
  display: none;
}

.step-circle {
  width: 30px;
  height: 30px;
  line-height: 30px;
  border-radius: 50%;
  background: #e9ecef;
  margin: 0 auto;
  color: #555;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.steps li.active .step-circle {
  background: #2575fc;
  color: #fff;
}

.step-title {
  margin-top: 4px;
  font-size: 12px;
  color: #777;
}

.steps li.active .step-title {
  color: #2575fc;
}

.dimmed {
  opacity: 0.5;
  pointer-events: none;
}

.modal-cancel-button {
  margin: 15px 0;
}

.btn-cancel {
  background: #e74c3c;
  border: none;
  color: #fff;
  padding: 8px 18px;
  border-radius: 20px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.btn-cancel:hover {
  background: #c0392b;
}

/* Order Detail (Modal) */
.order-detail-code {
  font-weight: 600;
  color: #333;
}

.order-detail-status {
  font-weight: 600;
}

.order-delivery h2 {
  font-size: 1.1rem;
  color: #333;
}

.order-product {
  background: #fafafa;
  border-radius: 10px;
  padding: 15px;
}

.product-image {
  width: 100px;
  height: auto;
  border-radius: 8px;
  object-fit: cover;
}

/* Order Summary */
.order-summary span {
  font-size: 0.95rem;
}

.reason-modal {
  padding: 20px;
  text-align: center;
}

.reason-textarea {
  width: 100%;
  padding: 12px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
  resize: vertical;
  font-size: 0.95rem;
}

.modal-btn-group {
  display: flex;
  justify-content: flex-end;
}

.btn-confirm {
  background: #27ae60;
  border: none;
  color: #fff;
  padding: 10px 20px;
  border-radius: 20px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.btn-confirm:hover {
  background: #1e8449;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .order-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .order-actions {
    margin-top: 10px;
    text-align: left;
  }
}
</style>
