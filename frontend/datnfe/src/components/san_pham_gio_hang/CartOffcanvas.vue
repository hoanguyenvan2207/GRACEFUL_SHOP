<!-- CartOffcanvas.vue -->
<template>
    <div v-if="isLoading" class="full-screen-spin">
        <div class="loader"></div>
    </div>
    <div class="offcanvas offcanvas-end" style="width: 550px;" tabindex="-1" id="cartOffcanvas">
        <div class="offcanvas-header bg-dark">
            <h6 class="text-white p-0 m-0">{{ $t('cart.title') }}</h6>
            <button type="button" class="btn-close btn-close-white text-reset" data-bs-dismiss="offcanvas"></button>
        </div>

        <div class="offcanvas-body d-flex flex-column">
            <div v-if="loading" class="text-center my-4">
                <div class="spinner-border text-success"></div>
                <p class="text-muted mt-2">{{ $t('cart.loading') }}</p>
            </div>

            <div v-else-if="cartItems.length === 0" class="text-center my-4">
                <i class="bi bi-cart-x fs-1 text-muted"></i>
                <p class="text-muted mt-2">{{ $t('cart.empty') }}</p>
            </div>

            <div v-else class="flex-grow-1 overflow-auto">
                <div v-for="item in cartItems" :key="item.id" class="border rounded mb-3 p-2">
                    <div class="d-flex align-items-center gap-2">
                        <a :href="item.href" class="text-decoration-none">
                            <img :src="item.anhUrl || 'https://placehold.jp/800x1200.png'" class="rounded"
                                style="width: 100px; height: 150px; object-fit: cover;" alt="Ảnh sản phẩm">
                        </a>
                        <div class="flex-grow-1">
                            <h6>{{ item.maAoDai }} - {{ item.tenAoDai }}</h6>
                            <small class="text-muted">{{ $t('cart.item.code') }} {{ item.maAoDaiChiTiet }}</small> <br>
                            <small>{{ $t('cart.item.classification', { color: item.tenMauSac, size: item.tenKichThuoc })
                                }}</small>
                            <br>
                            <div class="d-flex align-items-center">
                                <small class="me-2">{{ $t('cart.item.quantity') }}</small>
                                <div class="quantity-container d-flex align-items-center">
                                    <button class="btn btn-sm btn-outline-secondary" style="border: none;"
                                        @click="updateQuantity(item, item.soLuong - 1)" :disabled="item.soLuong <= 1">
                                        -
                                    </button>
                                    <input type="number" class="form-control form-control-sm text-center input-quantity"
                                        v-model.number="item.soLuong" @blur="validateQuantity(item)"
                                        style="width: 60px;" min="1" :max="item.tonKho">
                                    <button class="btn btn-sm btn-outline-secondary" style="border: none;"
                                        @click="updateQuantity(item, item.soLuong + 1)"
                                        :disabled="item.soLuong >= item.tonKho">
                                        +
                                    </button>
                                </div>
                                <small class="ms-2" v-if="item.soLuong <= item.tonKho && item.soLuong > 0">
                                    {{ $t('cart.item.stock', { n: item.tonKho }) }}
                                </small>
                                <small class="text-danger ms-1" v-if="item.soLuong > item.tonKho">
                                    {{ $t('cart.item.quantityErrorMax') }}
                                </small>
                                <small class="text-danger ms-1" v-if="item.soLuong <= 0">
                                    {{ $t('cart.item.quantityErrorMin') }}
                                </small>
                            </div>

                            <div class="mt-2">
                                <span class="text-dark fw-bold">{{ formatCurrency(item.giaBan) }}</span>
                                <small v-if="item.giaGoc !== item.giaBan"><del class="text-danger ms-2 small">{{
                                    formatCurrency(item.giaGoc)
                                        }}</del></small>
                            </div>
                        </div>
                        <i class="bi bi-trash text-danger btn-remove" @click="removeItem(item.id)"></i>
                    </div>
                </div>
            </div>

            <div class="border-top pt-3 mt-auto">
                <div class="d-flex justify-content-between mb-3">
                    <h5>{{ $t('cart.total') }}</h5>
                    <h5 class="text-success">{{ formatCurrency(totalAmount) }}</h5>
                </div>
                <button :disabled="cartItems.length === 0 || hasQuantityError" class="btn btn-dark w-100 p-2"
                    @click="checkout">{{
                        $t('cart.checkout') }}</button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, h } from 'vue';
import axios from 'axios';
import { useRoute, useRouter } from 'vue-router';
import { useCartStore } from '../san_pham_gio_hang/cart';
import { getSanPhamChiTietById } from '../san_pham_chi_tiet/api';
import { notification, Modal } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const cartStore = useCartStore();
const route = useRoute();
const router = useRouter();
const cartItems = ref([]);
const loading = ref(true);
const idKhachHang = sessionStorage.getItem('userId');
const isLoading = ref(false);

const hasQuantityError = computed(() => {
    return cartItems.value.some(item => item.soLuong <= 0 || item.soLuong > item.tonKho);
});

const totalAmount = computed(() => {
    return (cartItems.value || []).reduce((sum, item) => {
        return sum + (item.giaBan * item.soLuong);
    }, 0);
});

const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(value);
};


const loadCart = async () => {
    if (!idKhachHang || idKhachHang === "null" || idKhachHang === "undefined") {
        cartItems.value = [];
        cartStore.setCartItems([]);
        loading.value = false;
        return;
    }
    try {

        loading.value = true;
        const response = await axios.get(`/api/gio_hang/khach_hang/${idKhachHang}`);
        const data = response.data;

        cartItems.value = await Promise.all(data.map(async (item) => {
            const variant = await getSanPhamChiTietById(item.idSanPhamChiTiet);

            return {
                id: item.id,
                idSanPhamChiTiet: item.idSanPhamChiTiet,
                maAoDai: variant.maAoDai,
                maAoDaiChiTiet: variant.maAoDaiChiTiet,
                tenAoDai: variant.tenAoDai,
                anhUrl: variant.anhUrl,
                tenMauSac: variant.tenMauSac,
                tenKichThuoc: variant.tenKichThuoc,
                giaBan: variant.giaBan,
                giaGoc: variant.giaGoc,
                tonKho: variant.soLuong,
                soLuong: item.soLuong,
                href: `/san-pham/${variant.aoDaiId}`
            };
        }));
        cartStore.setCartItems(data); // Cập nhật store nếu cần
    } catch (error) {
        console.error('Lỗi tải giỏ hàng:', error);
        cartItems.value = [];
    } finally {
        loading.value = false;
    }
};

const validateQuantity = (item) => {
    if (isNaN(item.soLuong) || item.soLuong < 1) {
        item.soLuong = 1;
    } else if (item.soLuong > item.tonKho) {
        item.soLuong = item.tonKho;
    }
    updateQuantity(item, item.soLuong);
};

const updateQuantity = async (item, newQuantity) => {
    try {
        newQuantity = Math.max(1, newQuantity);
        await axios.put(`/api/gio_hang/${item.id}`, { soLuong: newQuantity });
        item.soLuong = newQuantity;
        if (typeof cartStore.updateItemQuantity === 'function') {
            cartStore.updateItemQuantity(item.id, newQuantity);
            cartStore.setCheckoutProducts(cartItems.value);
        }
    } catch (error) {
        console.error('Lỗi cập nhật số lượng:', error);
    }
};

const removeItem = (id) => {
    Modal.confirm({
        title: t('cart.remove.title'),           // Dịch tiêu đề
        content: t('cart.remove.content'),       // Dịch nội dung
        okText: t('cart.remove.okText'),         // Dịch nút xác nhận
        cancelText: t('cart.remove.cancelText'), // Dịch nút hủy
        getContainer: () => document.body,
        zIndex: 1100,
        onOk: async () => {
            try {
                await axios.delete(`/api/gio_hang/${id}`);
                await loadCart();
                openNotificationSuccess();
                cartStore.setCheckoutProducts(cartItems.value);
            } catch (error) {
                console.error('Lỗi xóa sản phẩm:', error);
            }
        }
    });
};

const openNotificationSuccess = () => {
    notification.success({
        message: t('cart.notification.success.message'),
        description: t('cart.notification.success.description')
    });
};

let offcanvasInstance = null;
const handleHide = () => {
    cartStore.setCartVisibility(false);
};

onMounted(() => {
    const offcanvasEl = document.getElementById('cartOffcanvas');
    offcanvasInstance = new bootstrap.Offcanvas(offcanvasEl);
    offcanvasEl.addEventListener('hidden.bs.offcanvas', handleHide);
    loadCart();
});

watch(() => cartStore.cartVisible, (newVal) => {
    if (offcanvasInstance) {
        newVal ? offcanvasInstance.show() : offcanvasInstance.hide();
        loadCart();
    }
});

// Hàm checkout chuyển hướng sang trang thanh toán
const checkout = () => {
    isLoading.value = true;
    setTimeout(() => {
        isLoading.value = false;
        if (cartItems.value.length > 0 && !hasQuantityError.value) {
            cartStore.setCheckoutProducts(cartItems.value);
            offcanvasInstance.hide();
            router.push("/thanh-toan");
        }
    }, 1000);
};
</script>

<style scoped>
.input-quantity {
    text-align: center;
    border: none;
    outline: none;
}

.quantity-container {
    border: 1px solid #ccc;
    width: 110px;
    border-radius: 4px;
    overflow: hidden;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

.btn-remove {
    cursor: pointer;
    transition: transform 0.2s ease;
    font-size: 20px;
}

.btn-remove:hover {
    transform: scale(1.2);
}

.full-screen-spin {
    position: fixed;
    top: 0;
    right: 0;
    width: 550px;
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
</style>