<!-- ThanhToan.vue -->
<template>
  <!-- Header -->
  <div v-if="!orderData" class="py-3 bg-light">
    <div class="container d-flex align-items-center">
      <a href="/">
        <img src="https://res.cloudinary.com/defcm50t7/image/upload/v1740128891/logo_graceful-removebg_rusxhe.png"
          alt="Graceful logo" height="auto" width="160px" style="object-fit: cover;" />
      </a>
      <div class="border-start border-2 border-dark-300 mx-4" style="height: 30px;"></div>
      <span class="text-orange-500 fs-5">{{ t('checkout.header.title') }}</span>

    </div>
  </div>

  <div class="container mt-2" style="width: 80%;">
    <div v-if="isLoading" class="full-screen-spin">
      <div class="loader"></div>
    </div>
    <div v-if="!orderSuccess" class="row p-3 gx-5">
      <div class="col-7">
        <div class="rounded border p-2 row align-items-center mb-3">
          <div class="col-10">
            <p><i class="bi bi-geo-alt"></i> {{ t('checkout.shippingAddress') }}</p>
            <template v-if="currentAddress">
              <h6 class="p-0">
                {{ currentAddress.khachHang.hoTen }} - ({{ currentAddress.khachHang.soDienThoai }})
              </h6>
              <p>
                {{ t('checkout.address') }}: {{ currentAddress.duong }}, {{ currentAddress.xaPhuong }},
                {{ currentAddress.quanHuyen }}, {{ currentAddress.tinhThanhPho }}
              </p>
            </template>
            <template v-else>
              <p>{{ t('checkout.pleaseSelectAddress') }}</p>
            </template>
          </div>
          <div class="col-2 text-primary" style="cursor: pointer" @click="openModal">
            {{ t('checkout.change') }} <i class="bi bi-chevron-right"></i>
          </div>
        </div>

        <div class="border rounded p-2 align-items-center row">
          <p><i class="bi bi-inboxes-fill"></i> {{ t('checkout.productInfo') }}</p>
          <div v-if="checkoutProducts && checkoutProducts.length > 0">
            <div v-for="item in checkoutProducts" :key="item.id"
              class="p-2 border rounded d-flex align-items-center mb-3">
              <div class="me-3 rounded">
                <img :src="item.anhUrl || 'https://placehold.jp/800x1200.png'" alt="Ảnh sản phẩm" width="60"
                  class="rounded" />
              </div>
              <div class="flex-grow-1 d-flex justify-content-between align-items-center">
                <div>
                  <h6>{{ item.maAoDaiChiTiet }} - {{ item.tenAoDai }}</h6>
                  <p class="m-0">
                    {{ t('checkout.product.classification') }}: {{ t('checkout.product.color') }}: {{ item.tenMauSac }},
                    {{ t('checkout.product.size') }}: {{
                      item.tenKichThuoc
                    }}
                  </p>
                  <p>{{ t('checkout.product.quantity') }}: x{{ item.soLuong }}</p>
                </div>
                <div class="d-flex gap-1 align-items-center">
                  <h6 class="text-muted" v-if="item.giaGoc !== item.giaBan"><del>{{ formatCurrency(item.giaGoc)
                      }}</del></h6>
                  <h6 class="text-danger">{{ formatCurrency(item.giaBan) }}</h6>
                </div>
              </div>
            </div>
          </div>
          <div v-else-if="buyNowProduct" class="p-2 border rounded d-flex align-items-center mb-3">
            <div class="me-3 rounded">
              <img :src="buyNowProduct.anhUrl || 'https://placehold.jp/800x1200.png'" alt="Ảnh sản phẩm" width="60"
                class="rounded" />
            </div>
            <div class="flex-grow-1 d-flex justify-content-between align-items-center">
              <div>
                <h6>{{ buyNowProduct.maAoDaiChiTiet }} - {{ buyNowProduct.tenAoDai }}</h6>
                <p class="text-muted mb-1">
                  <strong>{{ t('checkout.product.classification') }}:</strong>
                  {{ t('checkout.product.color') }}: {{ buyNowProduct.aoDaiChiTiet?.mauSac?.tenMauSac }},
                  {{ t('checkout.product.size') }}: {{ buyNowProduct.aoDaiChiTiet?.kichThuoc?.ten }}
                </p>
                <p>{{ t('checkout.product.quantity') }}: x{{ buyNowProduct.orderQuantity }}</p>
              </div>
              <div>
                <h6 v-if="buyNowProduct.giaGoc !== buyNowProduct.giaBan" class="text-muted"><del>{{
                  formatCurrency(buyNowProduct.giaGoc) }}</del></h6>
                <h6 class="text-danger">{{ formatCurrency(buyNowProduct.giaBan) }}</h6>
              </div>
            </div>
          </div>
          <div v-else>
            <p>{{ t('checkout.noProducts') }}</p>
          </div>
        </div>
      </div>

      <div class="col-5">
        <div class="border rounded p-2 row align-items-center">
          <p><i class="bi bi-credit-card-fill"></i> {{ t('checkout.paymentInfo') }}</p>
          <div class="d-flex align-items-center justify-content-between mb-3">
            <p>{{ t('checkout.discount') }}</p>
            <p class="text-primary" style="cursor: pointer" @click="openVoucherModal">
              <span v-if="!selectedGiamGiaId">{{ t('checkout.chooseDiscount') }} <i
                  class="bi bi-chevron-right"></i></span>
              <span v-else>{{ getVoucherLabel(selectedGiamGiaId) }}</span>
            </p>
          </div>
          <div class="d-flex mt-2 align-items-center justify-content-between">
            <p>{{ t('checkout.subtotal') }}</p>
            <p>{{ formatCurrency(productTotal) }}</p>
          </div>
          <div class="d-flex mt-2 align-items-center justify-content-between">
            <p>{{ t('checkout.shippingFee') }}</p>
            <p>{{ formatCurrency(shippingFee) }}</p>
          </div>
          <div class="d-flex align-items-center justify-content-between">
            <p>{{ t('checkout.totalBeforeDiscount') }}</p>
            <p>{{ formatCurrency(totalBeforeDiscount) }}</p>
          </div>
          <div class="d-flex align-items-center justify-content-between">
            <p>{{ t('checkout.voucherDiscount') }}</p>
            <p>-{{ formatCurrency(discount) }}</p>
          </div>
          <div class="d-flex align-items-center justify-content-between">
            <p>{{ t('checkout.total') }}</p>
            <p>{{ formatCurrency(totalAfterDiscount) }}</p>
          </div>
          <div class="d-flex align-items-center justify-content-between">
            <h5>{{ t('checkout.totalPayment') }}</h5>
            <h5 class="text-danger">{{ formatCurrency(totalPayment) }}</h5>
          </div>
          <label for="ghi-chu" class="form-label">{{ t('checkout.note') }}</label>
          <input type="text" class="form-control" name="ghi-chu" :placeholder="t('checkout.enterNote')" id="ghi-chu"
            v-model="note" />
          <div class="border rounded p-2 mt-3">
            <p><i class="bi bi-credit-card"></i> {{ t('checkout.paymentMethod') }}</p>
            <div class="list-group">
              <label class="list-group-item d-flex align-items-center">
                <input class="form-check-input me-3" type="radio" name="paymentMethod" value="cod"
                  v-model="paymentMethod" />
                <img src="https://hstatic.net/0/0/global/design/seller/image/payment/cod.svg?v=6" alt="COD"
                  class="me-3" />
                <span>{{ t('checkout.methods.cod') }}</span>
              </label>
              <label class="list-group-item d-flex align-items-center">
                <input class="form-check-input me-3" type="radio" name="paymentMethod" value="vnpay"
                  v-model="paymentMethod" />
                <img src="https://hstatic.net/0/0/global/design/seller/image/payment/vnpay_new.svg?v=6" alt=""
                  class="me-3" width="40" />
                <div class="d-flex flex-column flex-sm-row align-items-start align-items-sm-center">
                  <span class="me-3">{{ t('checkout.methods.vnpay') }}</span>
                </div>
              </label>
            </div>
            <div class="mt-3 py-5">
              <button class="btn btn-dark w-100" @click="handlePlaceOrder">{{ t('checkout.PlaceOrder') }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Màn hình thành công -->
    <div v-if="orderSuccess && orderData" class="result-wrapper">
      <a-result status="success" class="mt-0" :title="t('checkout.placeOrder.success.orderSuccess')"
        :sub-title="`${t('order-list.detail.orderCode')} ${orderData.maHoaDon}`">
        <template #extra>
          <h6 class="mb-3">{{ t('order-list.detail.title') }}</h6>
          <div class="mt-4 mb-4">
            <a-button type="primary" @click="goToOrders" class="me-2">
              {{ $t('checkout.placeOrder.result.viewAllOrders') }}
            </a-button>
            <a-button @click="goHome">
              {{ $t('checkout.placeOrder.result.continueShopping') }}
            </a-button>
          </div>
          <!-- Thông tin giao hàng -->
          <div class="order-delivery mb-4 text-start border rounded p-3">
            <h4 class="h5 mb-2">
              <i class="bi bi-geo-alt"></i> {{ t('order-list.detail.deliveryAddressTitle') }}
            </h4>
            <p class="fw-bold">
              {{ orderData.khachHang.hoTen }} — {{ orderData.khachHang.soDienThoai }}
            </p>
            <p>{{ t('order-list.detail.addressLabel') }} {{ orderData.diaChiGiaoHang }}</p>
          </div>

          <a-divider />

          <!-- Danh sách sản phẩm -->
          <div class="order-product d-flex justify-content-between align-items-center mb-3 p-3 rounded border"
            v-for="ct in orderData.chiTietHoaDon" :key="ct.id">
            <div class="d-flex align-items-center">
              <img :src="ct.aoDaiChiTiet.anhUrl" alt="Hình sản phẩm" class="product-image me-4 rounded"
                style="width:80px; height:auto;" />
              <div class="text-start">
                <p><strong>{{ t('order-list.detail.product.code') }}</strong> {{ ct.aoDaiChiTiet.maAoDaiChiTiet }}</p>
                <h6 class="fw-bold mb-1">{{ ct.aoDaiChiTiet.aoDai.tenAoDai }}</h6>
                <p class="text-muted mb-1">
                  <strong>{{ t('order-list.detail.product.color') }}</strong> {{ ct.aoDaiChiTiet.mauSac.tenMauSac }},
                  <strong>{{
                    t('order-list.detail.product.size') }}</strong> {{ ct.aoDaiChiTiet.kichThuoc.ten }}
                </p>
                <p class="mb-0"><strong>{{ t('order-list.detail.product.quantity') }} </strong> x{{ ct.soLuong }}</p>
              </div>
            </div>
            <div class="text-end d-flex">
              <p v-if="ct.aoDaiChiTiet.giaGoc !== ct.giaBan" class="me-1 text-muted">
                <del class="me-">{{ formatCurrency(ct.aoDaiChiTiet.giaGoc) }}</del>
              </p>
              <p class="fw-bold mb-0">{{ formatCurrency(ct.giaBan * ct.soLuong) }}</p>
            </div>
          </div>

          <a-divider />

          <!-- Tổng hợp thanh toán -->
          <div class="order-summary border p-3 rounded">
            <div class="d-flex justify-content-between py-1">
              <span>{{ t('order-list.detail.summary.itemsTotal') }}</span>
              <span>{{ formatCurrency(orderData.tienTruocGiam - orderData.phiGiaoHang) }}</span>
            </div>
            <div class="d-flex justify-content-between py-1">
              <span>{{ t('order-list.detail.summary.shippingFee') }}</span>
              <span>{{ formatCurrency(orderData.phiGiaoHang) }}</span>
            </div>
            <div class="d-flex justify-content-between py-1">
              <span>{{ t('order-list.detail.summary.totalBeforeDiscount') }}</span>
              <span>{{ formatCurrency(orderData.tienTruocGiam) }}</span>
            </div>
            <div class="d-flex justify-content-between py-1">
              <span>{{ t('order-list.detail.summary.voucher') }}</span>
              <span>-{{ formatCurrency(orderData.tongTien - orderData.tienTruocGiam) }}</span>
            </div>
            <div class="d-flex justify-content-between py-2 fw-bold fs-5">
              <span>{{ t('order-list.detail.summary.grandTotal') }}</span>
              <span>{{ formatCurrency(orderData.tongTien) }}</span>
            </div>
            <div class="d-flex justify-content-between py-1 text-muted">
              <span>{{ t('order-list.detail.paymentMethod.label') }}</span>
              <span>
                {{ orderData.phuongThucThanhToanOnline ? t('order-list.detail.paymentMethod.transfer') :
                  t('order-list.detail.paymentMethod.cod') }}
              </span>
            </div>
            <div v-if="orderData.ghiChu" class="d-flex justify-content-between py-1">
              <span>{{ t('order-list.detail.orderNote') }}</span>
              <span>{{ orderData.ghiChu }}</span>
            </div>
          </div>
        </template>
      </a-result>
    </div>
  </div>

  <div class="modal fade" tabindex="-1" :class="{ show: showVoucherModal }"
    :style="showVoucherModal ? 'display: block;' : 'display: none;'" role="dialog" aria-labelledby="voucherModalLabel"
    aria-modal="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="voucherModalLabel">{{ t('checkout.voucherModal.title') }}</h5>
          <button type="button" class="btn-close" @click="closeVoucherModal"></button>
        </div>
        <div class="modal-body" style="cursor: pointer;">
          <div v-if="danhSachGiamGia && danhSachGiamGia.length" class="voucher-grid">
            <div v-for="voucher in danhSachGiamGia" :key="voucher.id"
              :class="['voucher-card', { disabled: totalBeforeDiscount < voucher.giaTriToiThieu }]"
              @click="totalBeforeDiscount >= voucher.giaTriToiThieu && selectVoucher(voucher.id)">
              <div class="card voucher-card-content">
                <div class="voucher-inner">
                  <h6 class="voucher-code">{{ voucher.maGiamGia }}</h6>
                  <p class="voucher-detail">
                    <template v-if="voucher.loaiGiamGia === 0">
                      {{ t('checkout.voucherModal.discount') }} {{ formatCurrency(voucher.giaTriGiam) }}
                    </template>
                    <template v-else-if="voucher.loaiGiamGia === 1">
                      {{ t('checkout.voucherModal.discount') }} {{ voucher.giaTriGiam }}%<br />
                      {{ t('checkout.voucherModal.maxDiscount') }} {{ formatCurrency(voucher.toiDaGiamGia) }}
                    </template>
                  </p>
                  <p class="voucher-min text-muted">
                    {{ t('checkout.voucherModal.minOrder') }} {{ formatCurrency(voucher.giaTriToiThieu) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
          <div v-else>
            <p>{{ t('checkout.voucherModal.noVouchers') }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="closeVoucherModal">{{ t('checkout.voucherModal.close')
          }}</button>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showModal" class="modal d-block" style="z-index: 1050;" tabindex="-1">
    <div class="modal-dialog modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">{{ t('checkout.addressModal.title') }}</h5>
          <button type="button" class="btn-close" @click="closeModal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label>
              <input type="radio" value="existing" v-model="addressOption" />
              {{ t('checkout.addressModal.useExisting') }}
            </label>
            <label class="ms-3">
              <input type="radio" value="new" v-model="addressOption" />
              {{ t('checkout.addressModal.addNew') }}
            </label>
          </div>
          <div v-if="addressOption === 'existing'">
            <h6>{{ t('checkout.addressModal.existingAddress') }}:</h6>
            <ul class="list-group">
              <li v-for="(address, index) in addresses" :key="address.id"
                class="list-group-item d-flex justify-content-between align-items-center"
                :class="{ active: selectedExistingAddress === address }" style="cursor: pointer"
                @click="selectExistingAddress(address)">
                <span>
                  {{ address.duong }}, {{ address.xaPhuong }}, {{ address.quanHuyen }}, {{ address.tinhThanhPho }}
                </span>
                <button type="button" class="btn btn-danger btn-sm" @click.stop="confirmDelete(address)">
                  <i class="bi bi-trash"></i>
                </button>
              </li>
            </ul>
            <div class="mt-2">
              <input type="checkbox" class="me-1" id="setDefault" v-model="setSelectedAsDefault" />
              <label for="setDefault">{{ t('checkout.addressModal.setDefault') }}</label>
            </div>
          </div>
          <div v-else>
            <h6>{{ t('checkout.addressModal.addNew') }}:</h6>
            <div class="mb-3">
              <label for="province" class="form-label">{{ t('checkout.addressModal.province') }}:</label>
              <select id="province" class="form-select" v-model="selectedProvince" @change="onProvinceChange">
                <option value="">{{ t('checkout.addressModal.selectProvince') }}</option>
                <option v-for="province in provinces" :key="province.code" :value="province">
                  {{ province.name }}
                </option>
              </select>
            </div>
            <div class="mb-3" v-if="districts.length">
              <label for="district" class="form-label">{{ t('checkout.addressModal.district') }}:</label>
              <select id="district" class="form-select" v-model="selectedDistrict" @change="onDistrictChange">
                <option value="">{{ t('checkout.addressModal.selectDistrict') }}</option>
                <option v-for="district in districts" :key="district.code" :value="district">
                  {{ district.name }}
                </option>
              </select>
            </div>
            <div class="mb-3" v-if="wards.length">
              <label for="ward" class="form-label">{{ t('checkout.addressModal.ward') }}:</label>
              <select id="ward" class="form-select" v-model="selectedWard">
                <option value="">{{ t('checkout.addressModal.selectWard') }}</option>
                <option v-for="ward in wards" :key="ward.code" :value="ward">
                  {{ ward.name }}
                </option>
              </select>
            </div>
            <div class="mb-3" v-if="selectedProvince && selectedDistrict && selectedWard">
              <label for="duong" class="form-label">{{ t('checkout.addressModal.street') }}</label>
              <input type="text" id="duong" class="form-control" v-model="newAddress.duong" />
            </div>
            <div class="mb-3">
              <input type="checkbox" id="defaultAddress" v-model="defaultAddress" />
              <label for="defaultAddress">{{ t('checkout.addressModal.setDefault') }}</label>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('checkout.addressModal.close')
          }}</button>
          <button type="button" class="btn btn-primary" @click="saveAddress">{{ t('checkout.addressModal.save')
          }}</button>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showModal" class="modal-backdrop fade show"></div>
</template>

<script setup>
import { ref, onMounted, watch, computed, h, nextTick } from 'vue';
import axios from 'axios';
import AddressService from "../../services/AddressService";
import { useCartStore } from "../san_pham_gio_hang/cart";
import axiosGHTK from '../san_pham_gio_hang/axiosGHTK';
import { getSanPhamById } from "../san_pham/api";
import { notification, Modal } from 'ant-design-vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
const { t } = useI18n();

const route = useRoute();
const router = useRouter();

const isLoading = ref(false);
const orderSuccess = ref(false);
const orderData = ref(null);

// Store
const cartStore = useCartStore();
const buyNowProduct = computed(() => cartStore.buyNowProduct || null);
const checkoutProducts = computed(() => cartStore.checkoutProducts || []);

// Địa chỉ giao hàng
const currentAddress = ref(null);
const addresses = ref([]);
const showModal = ref(false);
const addressOption = ref("existing");
const selectedExistingAddress = ref(null);
const setSelectedAsDefault = ref(false);

// Địa chỉ mới
const newAddress = ref({ duong: "" });
const provinces = ref([]);
const districts = ref([]);
const wards = ref([]);
const selectedProvince = ref(null);
const selectedDistrict = ref(null);
const selectedWard = ref(null);
const defaultAddress = ref(false);

// Sản phẩm (cho mua ngay)
const product = ref({});

// Voucher
const danhSachGiamGia = ref([]);
const selectedGiamGiaId = ref("");
// Trạng thái hiển thị modal voucher
const showVoucherModal = ref(false);

// Phí ship
const shopAddress = "Huyện Đông Anh, Thành phố Hà Nội";
const shopProvince = "Thành phố Hà Nội";
const shopDistrict = "Huyện Đông Anh";
const orderWeight = 2000;
const orderValue = 0;
const transportMethod = "road";
const baseFee = 15000;
const shippingFee = ref(baseFee);

async function calculateGHTKFee() {
  if (!currentAddress.value) return baseFee;
  const orderData = {
    pick_address: shopAddress,
    pick_province: shopProvince,
    pick_district: shopDistrict,
    address: currentAddress.value.duong || "",
    province: currentAddress.value.tinhThanhPho,
    district: currentAddress.value.quanHuyen,
    weight: orderWeight,
    value: orderValue,
    transport: transportMethod
  };
  try {
    const response = await axiosGHTK.post("/ghtk/services/shipment/fee", orderData, {
      headers: {
        "Content-Type": "application/json",
        "Token": "18Yxeo5hEmOu7tae44LuQZZheZ3sHpFmL0An50q"
      }
    });
    console.log("GHTK fee response:", response.data);
    const fee = response.data.fee && response.data.fee.fee ? response.data.fee.fee : baseFee;
    return fee;
  } catch (error) {
    console.error("Lỗi khi tính phí ship GHTK:", error);
    return baseFee;
  }
}

watch(currentAddress, async (newAddr) => {
  if (newAddr) {
    const fee = await calculateGHTKFee();
    shippingFee.value = fee;
    console.log("Calculated shipping fee from GHTK:", shippingFee.value);
  }
});

const formatCurrency = (value) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

const paymentMethod = ref("cod");
const note = ref("");

// Tiền hàng chưa có ship
const productTotal = computed(() => {
  let total = 0;
  if (checkoutProducts.value && checkoutProducts.value.length > 0) {
    checkoutProducts.value.forEach(item => {
      total += (item.giaBan || 0) * (item.soLuong || 0);
    });
  } else if (buyNowProduct.value) {
    total += (buyNowProduct.value.giaBan || 0) * (buyNowProduct.value.orderQuantity || 0);
  }
  return total;
});

// Tổng tiền trước và sau khi giảm
const totalBeforeDiscount = computed(() => {
  return productTotal.value === 0 ? 0 : productTotal.value + shippingFee.value;
});
// const discount = computed(() => {
//   let disc = 0;
//   if (selectedGiamGiaId.value) {
//     const voucher = (danhSachGiamGia.value || []).find(v => v.id == selectedGiamGiaId.value);
//     if (voucher && totalBeforeDiscount.value >= voucher.giaTriToiThieu) {
//       disc = voucher.loaiGiamGia === 0
//         ? voucher.giaTriGiam
//         : totalBeforeDiscount.value * voucher.giaTriGiam / 100;
//       if (disc > voucher.toiDaGiamGia) {
//         disc = voucher.toiDaGiamGia;
//       }
//     }
//   }
//   return disc;
// });

const discount = computed(() => {
  // Mặc định không có giảm giá
  let disc = 0;

  // Nếu chưa chọn voucher thì thôi
  if (!selectedGiamGiaId.value) {
    return 0;
  }

  // Tìm voucher
  const voucher = (danhSachGiamGia.value || [])
    .find(v => v.id === selectedGiamGiaId.value);

  if (!voucher) {
    return 0;
  }

  const total = totalBeforeDiscount.value;

  // Chỉ áp voucher khi đạt điều kiện tối thiểu
  if (total < voucher.giaTriToiThieu) {
    return 0;
  }

  // Loại 0 = cố định, 1 = % 
  if (voucher.loaiGiamGia === 0) {
    // giảm giá cố định
    disc = voucher.giaTriGiam;
  }
  else if (voucher.loaiGiamGia === 1) {
    // giảm theo %
    disc = total * voucher.giaTriGiam / 100;

    // nếu vượt mức tối đa thì giới hạn lại
    if (voucher.toiDaGiamGia != null && disc > voucher.toiDaGiamGia) {
      disc = voucher.toiDaGiamGia;
    }
  }

  return disc;
});

const totalAfterDiscount = computed(() => totalBeforeDiscount.value - discount.value);
const totalPayment = computed(() => totalAfterDiscount.value);

// mở modal voucher
const openVoucherModal = () => {
  showVoucherModal.value = true;
};

// đóng modal voucher
const closeVoucherModal = () => {
  showVoucherModal.value = false;
};

// Hàm chọn voucher
const selectVoucher = (voucherId) => {
  selectedGiamGiaId.value = voucherId;
  closeVoucherModal();
};

// Hàm hiển thị thông tin voucher đã chọn
const getVoucherLabel = (voucherId) => {
  const voucher = danhSachGiamGia.value.find(v => v.id == voucherId);
  if (voucher) {
    return voucher.maGiamGia + ' - ' + (voucher.loaiGiamGia === 0 ? `${t('checkout.voucherModal.discount')} ${formatCurrency(voucher.giaTriGiam)}` : `${t('checkout.voucherModal.discount')} ${voucher.giaTriGiam}%`);
  }
  return "Chọn";
};

function goToOrders() {
  isLoading.value = true;

  setTimeout(() => {
    router.push('/danh-sach-don-hang');
    isLoading.value = false;
  }, 500);
}

function goHome() {
  isLoading.value = true;

  setTimeout(() => {
    router.push('/danh-sach-san-pham');
    isLoading.value = false;
  }, 500);
}

// Hàm đặt hàng 
const handlePlaceOrder = async () => {
  if (!currentAddress.value) {
    notification.warning({
      message: t('checkout.placeOrder.warning.title'),
      description: t('checkout.placeOrder.warning.description'),
    });
    return;
  }

  Modal.confirm({
    title: t('checkout.placeOrder.confirm.title'),
    content: t('checkout.placeOrder.confirm.content'),
    async onOk() {
      isLoading.value = true;
      let orderItems = [];

      if (checkoutProducts.value && checkoutProducts.value.length > 0) {
        checkoutProducts.value.forEach(item => {
          const productDetailId = item.idSanPhamChiTiet || item.id;
          if (!productDetailId) {
            console.error("Sản phẩm chi tiết không có id:", item);
            return;
          }
          orderItems.push({
            aoDaiChiTiet: { id: productDetailId },
            soLuong: item.soLuong,
            giaBan: item.giaBan
          });
        });
      } else if (buyNowProduct.value) {
        const productDetailId = buyNowProduct.value.idSanPhamChiTiet || buyNowProduct.value.id;
        if (!productDetailId) {
          notification.error({
            message: t('checkout.placeOrder.error.noProductDetailInfo.title'),
            description: t('checkout.placeOrder.error.noProductDetailInfo.description'),
          });
          return;
        }
        orderItems.push({
          aoDaiChiTiet: { id: productDetailId },
          soLuong: buyNowProduct.value.orderQuantity,
          giaBan: buyNowProduct.value.giaBan
        });
      } else {
        notification.error({
          message: t('checkout.placeOrder.error.noProducts.title'),
          description: t('checkout.placeOrder.error.noProducts.description'),
        });
        return;
      }

      if (orderItems.length === 0) {
        notification.error({
          message: t('checkout.placeOrder.error.invalidProducts.title'),
          description: t('checkout.placeOrder.error.invalidProducts.description'),
        });
        return;
      }

      const deliveryAddress = `${currentAddress.value.duong}, ${currentAddress.value.xaPhuong}, ${currentAddress.value.quanHuyen}, ${currentAddress.value.tinhThanhPho}`;
      let voucherObj = selectedGiamGiaId.value ? { id: selectedGiamGiaId.value } : null;
      const isVnpay = paymentMethod.value === "vnpay";

      const orderPayload = {
        phuongThucThanhToan: isVnpay,
        phuongThucThanhToanOnline: isVnpay,
        ghiChu: note.value,
        phiGiaoHang: shippingFee.value,
        diaChiGiaoHang: deliveryAddress,
        khachHang: { id: idKhachHang },
        giamGia: voucherObj,
        chiTietHoaDon: orderItems
      };

      console.log("Order payload:", orderPayload);

      try {
        const response = await axios.post("/api/ban-hang-online/dat-hang", orderPayload);
        console.log("Order response:", response.data);

        orderData.value = response.data;
        // Nếu vnpay thì redirect ngay
        if (isVnpay) {
          window.location.replace(response.data);
          return;
        }

        if (!isVnpay) {
          setTimeout(async () => {
            isLoading.value = false;
            orderSuccess.value = true;

            if (checkoutProducts.value?.length) {
              for (const item of checkoutProducts.value) {
                try {
                  await axios.delete(`/api/gio_hang/${item.id}`);
                } catch (err) {
                  console.error("Lỗi xóa SPCT:", err);
                }
              }
              cartStore.setCheckoutProducts([]);
            } else if (buyNowProduct.value) {
              cartStore.clearBuyNowProduct();
              window.location.reload();
            }
          }, 3000);
        }
      } catch (error) {
        console.error("Error placing order:", error);
        notification.error({
          message: t('checkout.placeOrder.error.orderPlacement.title'),
          description: t('checkout.placeOrder.error.orderPlacement.description'),
        });
      }
    }
  });
};

const idKhachHang = sessionStorage.getItem('userId');
// Hàm lấy danh sách địa chỉ
const fetchAddresses = async () => {
  try {
    const response = await axios.get(`/api/ban-hang-online/dia-chi-khach-hang?idKhachHang=${idKhachHang}`);
    addresses.value = response.data;
    if (addresses.value.length > 0) {
      currentAddress.value = addresses.value[0];
      selectedExistingAddress.value = addresses.value[0];
    }
  } catch (error) {
    console.error("Error fetching addresses:", error);
  }
};

const fetchProvinces = async () => {
  try {
    provinces.value = await AddressService.getProvinces();
  } catch (error) {
    console.error("Error fetching provinces:", error);
  }
};

const onProvinceChange = async () => {
  if (selectedProvince.value && selectedProvince.value.code) {
    try {
      districts.value = await AddressService.getDistrictsByProvince(selectedProvince.value.code);
      selectedDistrict.value = null;
      wards.value = [];
      selectedWard.value = null;
    } catch (error) {
      console.error("Error fetching districts:", error);
    }
  }
};

const onDistrictChange = async () => {
  if (selectedDistrict.value && selectedDistrict.value.code) {
    try {
      wards.value = await AddressService.getWardsByDistrict(selectedDistrict.value.code);
      selectedWard.value = null;
    } catch (error) {
      console.error("Error fetching wards:", error);
    }
  }
};

const openModal = async () => {
  showModal.value = true;
  if (provinces.value.length === 0) {
    await fetchProvinces();
  }
};

const selectExistingAddress = (address) => {
  selectedExistingAddress.value = address;
};

const resetForm = () => {
  newAddress.value = { duong: "" };
  selectedProvince.value = null;
  selectedDistrict.value = null;
  selectedWard.value = null;
  defaultAddress.value = false;
  setSelectedAsDefault.value = false;
  addressOption.value = "existing";
  districts.value = [];
  wards.value = [];
};

const saveAddress = async () => {
  if (addressOption.value === "existing") {
    if (selectedExistingAddress.value) {
      currentAddress.value = selectedExistingAddress.value;
      if (setSelectedAsDefault.value && !selectedExistingAddress.value.diaChiMacDinh) {
        try {
          await axios.put(`/api/ban-hang-online/dia-chi/${selectedExistingAddress.value.id}/default`);
          await fetchAddresses();
        } catch (error) {
          console.error("Error updating default address", error);
          notification.error({
            message: "Lỗi",
            description: "Lỗi khi cập nhật địa chỉ mặc định. Vui lòng thử lại.",
          });
        }
      }
    }
  } else {
    if (!newAddress.value.duong || !selectedProvince.value || !selectedDistrict.value || !selectedWard.value) {
      notification.warning({
        message: "Cảnh báo",
        description: "Vui lòng điền đầy đủ thông tin địa chỉ mới.",
      });
      return;
    }
    const newAddressData = {
      duong: newAddress.value.duong.trim().toLowerCase(),
      xaPhuong: selectedWard.value.name.trim().toLowerCase(),
      quanHuyen: selectedDistrict.value.name.trim().toLowerCase(),
      tinhThanhPho: selectedProvince.value.name.trim().toLowerCase(),
    };
    const duplicate = addresses.value.find(
      (a) =>
        a.duong.trim().toLowerCase() === newAddressData.duong &&
        a.xaPhuong.trim().toLowerCase() === newAddressData.xaPhuong &&
        a.quanHuyen.trim().toLowerCase() === newAddressData.quanHuyen &&
        a.tinhThanhPho.trim().toLowerCase() === newAddressData.tinhThanhPho
    );
    if (duplicate) {
      notification.warning({
        message: "Cảnh báo",
        description: "Địa chỉ này đã tồn tại",
      });
      return;
    }
    const constructedAddress = {
      duong: newAddress.value.duong,
      xaPhuong: selectedWard.value.name,
      quanHuyen: selectedDistrict.value.name,
      tinhThanhPho: selectedProvince.value.name,
      diaChiMacDinh: defaultAddress.value,
      khachHang: { id: idKhachHang },
    };
    try {
      const response = await axios.post("/api/ban-hang-online/dia-chi", constructedAddress);
      currentAddress.value = response.data;
      addresses.value.push(response.data);
    } catch (error) {
      console.error("Error saving new address:", error);
      notification.error({
        message: "Lỗi",
        description: "Lỗi khi lưu địa chỉ mới. Vui lòng thử lại.",
      });
      return;
    }
  }
  resetForm();
  closeModal();
};

const closeModal = () => {
  resetForm();
  showModal.value = false;
};

const confirmDelete = (address) => {
  Modal.confirm({
    title: "Xác nhận xóa",
    content: "Bạn có muốn xóa địa chỉ này không?",
    getContainer: () => document.body,
    zIndex: 1051,
    async onOk() {
      try {
        await axios.delete(`/api/ban-hang-online/xoa-dia-chi/${address.id}`);
        addresses.value = addresses.value.filter((a) => a.id !== address.id);
        if (selectedExistingAddress.value && selectedExistingAddress.value.id === address.id) {
          selectedExistingAddress.value = addresses.value.length > 0 ? addresses.value[0] : null;
        }
        if (currentAddress.value && currentAddress.value.id === address.id) {
          currentAddress.value = addresses.value.length > 0 ? addresses.value[0] : null;
        }
      } catch (error) {
        console.error("Error deleting address:", error);
        notification.error({
          message: "Lỗi",
          description: "Lỗi khi xóa địa chỉ. Vui lòng thử lại.",
        });
      }
    },
  });
};

onMounted(async () => {
  await fetchAddresses();
  const productId = 1;
  product.value = await getSanPhamById(productId);
  try {
    const response = await axios.get("/api/ban-hang-online/hien-thi-giam-gia-online");
    danhSachGiamGia.value = response.data;
  } catch (error) {
    console.error("Error fetching vouchers:", error);
  }

  if (route.query.success === 'true') {
    try {
      isLoading.value = true;
      const { orderId } = route.query;

      if (orderId) {
        // First fetch the order data
        const { data } = await axios.get(
          `/api/hoa-don/detail?idHoaDon=${orderId}`
        );
        orderData.value = data;

        // Wait 1 second before deleting cart items
        setTimeout(async () => {
          if (checkoutProducts.value && checkoutProducts.value.length > 0) {
            for (const item of checkoutProducts.value) {
              try {
                await axios.delete(`/api/api/gio_hang/${item.id}`);
              } catch (error) {
                console.error("Error deleting cart item:", error);
              }
            }
            cartStore.setCheckoutProducts([]);
          } else if (buyNowProduct.value) {
            cartStore.clearBuyNowProduct();
            window.location.reload();
          }

          orderSuccess.value = true;
          setTimeout(() => {
            isLoading.value = false;
          }, 1000);
        }, 2000);
      }
    } catch (err) {
      notification.error({
        title: "Không lấy được thông tin đơn",
        content: "Vui lòng thử lại hoặc liên hệ quản trị viên."
      });
      isLoading.value = false;
    } finally {
      router.replace({ path: route.path, query: {} });
    }
  } else if (route.query.success === 'false') {
    Modal.warning({
      title: "Giao dịch không thành công",
      content: "Thanh toán VNPay đã bị hủy hoặc thất bại. Vui lòng thử lại hoặc kiểm tra giỏ hàng của bạn.",
      onOk: () => {
        router.push('/thanh-toan');
      }
    });
    router.replace({ path: route.path, query: {} });
  }
});
</script>

<style scoped>
.modal {
  background: rgba(0, 0, 0, 0.5);
  z-index: 1050;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1040;
}

.modal-voucher {
  width: 1000px;
}

.voucher-grid {
  display: flex;
  flex-wrap: wrap;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.voucher-card.disabled {
  opacity: 0.5;
  pointer-events: none;
}

.voucher-card .card {
  border: 1px solid #eee;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  transition: all 0.3s ease;
  background-color: rgb(255, 255, 255);
}

.voucher-card .card:hover {
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.15);
  transform: translateY(-4px);
  background-color: rgba(144, 133, 133, 0.1);
}

.voucher-inner {
  text-align: center;
}

.voucher-detail {
  font-size: 1rem;
  margin: 0;
}

.voucher-min {
  font-size: 0.9rem;
  margin-top: 8px;
}

.voucher-selection {
  cursor: pointer;
  color: blue;
  font-size: 1.2rem;
  font-weight: bold;
  text-decoration: underline;
}

.backhome {
  margin-left: 800px;

}

.backhome span {
  font-size: 20px;
}

.full-screen-spin {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.65);
  z-index: 9999;
}

/* HTML: <div class="loader"></div> */
.loader {
  width: 50px;
  aspect-ratio: 1;
  display: grid;
  border: 4px solid #0000;
  border-radius: 50%;
  border-right-color: #000000;
  animation: l15 0.8s infinite linear;
}

.loader::before,
.loader::after {
  content: "";
  grid-area: 1/1;
  margin: 2px;
  border: inherit;
  border-radius: 50%;
  animation: l15 1.3s infinite;
}

.loader::after {
  margin: 8px;
  animation-duration: 2s;
}

@keyframes l15 {
  100% {
    transform: rotate(1turn)
  }
}

.checkout-container {
  position: relative;
}

.result-wrapper {
  padding: 40px;
  text-align: center;
}
</style>
