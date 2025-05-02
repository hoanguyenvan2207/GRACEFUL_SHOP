<template>
    <Carousel />
    <div class="container">
        <!-- Container hiển thị top 5 sản phẩm trên 1 hàng -->
        <div class="mt-5">
            <h6 class="fw-bold text-dark text-muted mb-0">{{ $t('home.title') }}</h6>
            <div class="row g-3 mt-1
            row-cols-2
            row-cols-md-3
            row-cols-lg-4
            row-cols-xl-5">
                <div v-for="product in products" :key="product.id" class="col">
                    <div class="card h-100 product-items position-relative" style="cursor: pointer;"
                        @click="redirectTo(product.href)">
                        <div class="img-container position-relative overflow-hidden" style="aspect-ratio: 2/3;">
                            <img :src="product.imageSrc || 'https://placehold.jp/800x1200.png'" :alt="product.tenAoDai"
                                class="card-img-top img-fluid rounded h-100" style="object-fit: cover;">
                            <i class="bi bi-eye hover-icon"></i>
                        </div>
                        <div class="card-body d-flex flex-column justify-content-between">
                            <div class="row align-items-center p-0">
                                <div class="d-flex justify-content-start text-warning col-7">
                                    <i class="bi bi-star-fill me-1"></i>
                                    <i class="bi bi-star-fill me-1"></i>
                                    <i class="bi bi-star-fill me-1"></i>
                                    <i class="bi bi-star-fill me-1"></i>
                                    <i class="bi bi-star-fill me-1"></i>
                                </div>
                                <button class="btn btn-add rounded-pill col-3" @click.stop="addToCart(product)">
                                    <i class="fa-solid fa-cart-plus"></i>
                                </button>
                            </div>
                            <div>
                                <h6 class="card-title fs-6 text-dark">
                                    {{ product.maAoDai }} - {{ product.tenAoDai }}
                                </h6>
                            </div>
                            <div class="d-flex justify-content-start align-items-center">
                                <p class="fw-bold text-dark mb-0 h5">
                                    {{ formatCurrency(product.giaBan) }}
                                </p>
                                <small v-if="product.giaGoc !== product.giaBan" class="text-danger ms-2">
                                    <del>{{ formatCurrency(product.giaGoc) }}</del>
                                </small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <img class="w-100 h-100 mt-5" src="https://lamia.com.vn/storage/banner-sale/mega-sale-ao-dai-1920x15503.jpg"
            alt="Banner">
        <img class="w-100 h-100 mt-5"
            src="https://lamia.com.vn/storage/banner-sale/ao-dai-sale-ngay-quoc-te-lao-dong.jpg" alt="Banner">
    </div>
</template>

<script setup>
import { ref, onMounted, h } from 'vue';
import axios from 'axios';
import Carousel from './Carousel.vue';
import { getTopSell } from './san_pham/api';
import { notification } from 'ant-design-vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import { useCartStore } from "./san_pham_gio_hang/cart";

const router = useRouter();
const cartStore = useCartStore();
const { t } = useI18n();
const products = ref([]);
const idKhachHang = sessionStorage.getItem('userId');

const fetchProducts = async () => {
    try {
        const data = await getTopSell();

        const productArray = data.content ? data.content : data;

        const filteredProducts = productArray.filter(product => {
            const details = product.sanPhamChiTietList;
            if (!details || details.length === 0) return false;
            return details.some(detail => detail.trangThai === true && detail.soLuong > 0);
        });

        products.value = filteredProducts.slice(0, 5).map(product => {
            const imageSrc = product.anhList && product.anhList.length > 0 ? product.anhList[0] : '';
            const giaBan = product.sanPhamChiTietList?.reduce((min, detail) =>
                Math.min(min, detail.giaBan), product.sanPhamChiTietList?.[0]?.giaBan || 0);
            const giaGoc = product.sanPhamChiTietList?.reduce((max, detail) =>
                Math.max(max, detail.giaGoc), product.sanPhamChiTietList?.[0]?.giaGoc || 0);

            return {
                href: `/san-pham/${product.id}`,
                imageSrc,
                giaBan,
                giaGoc,
                tenAoDai: product.tenAoDai,
                id: product.id,
                maAoDai: product.maAoDai,
                tenLoaiAoDai: product.tenLoaiAoDai,
                tenChatLieu: product.tenChatLieu,
                tenTaAo: product.tenTaAo,
                tenNhaCungCap: product.tenNhaCungCap,
                sanPhamChiTietList: product.sanPhamChiTietList
            };
        });
    } catch (error) {
        console.error("Lỗi khi lấy sản phẩm bán chạy:", error);
    }
};

const openNotification = () => {
    const key = `openNotification${Date.now()}`;
    notification.success({
        key,
        message: t('cart.add.notification.success.message'),
        description: h('div', {}, [
            h('div', {}, t('cart.add.notification.success.description')),
            h('div', { style: { marginTop: '10px', color: '#f44336' } }, t('cart.add.notification.success.viewCart'))
        ]),
        style: {
            cursor: 'pointer'
        },
        onClick: () => {
            cartStore.setCartVisibility(true);
            notification.close(key);
        }
    });
};

onMounted(() => {
    fetchProducts();
});

// Phương thức chuyển hướng (có thể sử dụng router.push nếu có cấu hình vue-router)
const redirectTo = (href) => {
    window.location.href = href || '#';
};

// Phương thức thêm sản phẩm vào giỏ hàng
const addToCart = async (product) => {
    if (!idKhachHang) {
        router.push('/dang-nhap');
        return;
    }
    const variant = product.sanPhamChiTietList.find(item =>
        item.trangThai === true && item.soLuong > 0
    );
    const newCartItem = {
        idSanPhamChiTiet: variant.id,
        soLuong: 1,
        idKhachHang: Number(idKhachHang)
    };

    // Kiểm tra nếu số lượng trong giỏ đã đạt tới số lượng tồn kho của spct
    const existingCartItem = cartStore.cartItems.find(item =>
        Number(item.idSanPhamChiTiet) === Number(variant.id)
    );
    if (existingCartItem && existingCartItem.soLuong >= Number(variant.soLuong)) {
        notification.error({
            message: 'Sản phẩm hết hàng',
            description: 'Số lượng sản phẩm trong giỏ đã đạt tối đa theo số lượng tồn kho.'
        });
        return; // Ngừng thêm sản phẩm vào giỏ
    }

    try {
        if (typeof cartStore.addItem === "function") {
            const addItem = cartStore.addItem(newCartItem);

            if (addItem.soLuong !== newCartItem.soLuong) {
                const response = await axios.put(`/api/gio_hang/${addItem.id}`, { soLuong: addItem.soLuong });
                cartStore.updateItemQuantity(addItem.id, response.data.soLuong);
            } else {
                const response = await axios.post('/api/gio_hang', newCartItem);

                cartStore.updateItemWithBackendData(newCartItem.idSanPhamChiTiet, response.data);
            }
        } else {
            console.error("cartStore.addItem không được định nghĩa");
        }

        openNotification();
    } catch (error) {
        console.error("Lỗi thêm sản phẩm vào giỏ hàng:", error);
        notification.error({
            message: t('cart.add.message'),
            description: t('cart.add.error')
        });
    }
};

// Hàm định dạng tiền tệ
const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};
</script>

<style scoped>
.product-items {
    transition: all 0.2s ease-in-out;
}

.product-items:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.hover-icon {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translateY(100%) translateX(-50%);
    font-size: 2rem;
    color: rgba(255, 255, 255, 0.9);
    opacity: 0;
    transition: all 0.3s ease;
}

.hover-icon:hover {
    color: rgb(0, 0, 0);
    font-size: 2.1rem;
}

.product-items:hover .hover-icon {
    opacity: 1;
    transform: translate(-50%, -50%);
}

.img-container {
    position: relative;
    overflow: hidden;
}

.img-container img {
    transition: filter 0.3s ease;
}

.product-items:hover .img-container img {
    filter: brightness(80%);
}

.btn-add {
    z-index: 3;
    transition: transform 0.3s ease;
    font-weight: bold;
    padding: 0;
    background-color: #f4c93b;
    color: white;
}

.btn-add:active,
.btn-add:focus {
    outline: none !important;
    box-shadow: none !important;
    border: none !important;
}

.btn-add:hover {
    transform: scale(1.08);
    background-color: #e3c010;
}
</style>
