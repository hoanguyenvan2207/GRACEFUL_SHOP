<template>
    <div class="container mt-5" v-if="product">
        <div v-if="isLoading" class="full-screen-spin">
            <div class="loader"></div>
        </div>
        <div class="d-flex justify-content-between">
            <!-- Carousel Column -->
            <div id="carouselExampleIndicators" class="carousel slide carousel-fade col-lg-4 p-0">
                <div class="carousel-indicators">
                    <button v-for="(img, index) in sliderImages" :key="index" type="button"
                        :data-bs-target="'#carouselExampleIndicators'" :data-bs-slide-to="index"
                        :class="{ active: index === activeImageIndex }" @click="setActiveImage(index)"></button>
                </div>
                <div class="carousel-inner">
                    <div v-for="(img, index) in sliderImages" :key="index" class="carousel-item p-0"
                        :class="{ active: index === activeImageIndex }" :style="{ transition: 'opacity 0.5s ease' }">
                        <img :src="img" class="d-block w-100 rounded"
                            style="width: 500px; height: auto; object-fit: cover;" alt="Product Image" />
                    </div>
                </div>
                <button class="carousel-control-prev" type="button" data-bs-target="#carouselExampleIndicators"
                    data-bs-slide="prev" @click="prevImage">
                    <span class="carousel-control-prev-icon"></span>
                    <span class="visually-hidden">Previous</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#carouselExampleIndicators"
                    data-bs-slide="next" @click="nextImage">
                    <span class="carousel-control-next-icon"></span>
                    <span class="visually-hidden">Next</span>
                </button>
            </div>

            <!-- Thumbnail Images -->
            <div class="col-lg-1 d-flex flex-column align-items-center" :style="{
                padding: '0',
                marginRight: '50px',
                maxHeight: thumbnailImages.length > 5 ? '650px' : 'auto',
                overflowY: thumbnailImages.length > 5 ? 'auto' : 'visible'
            }">
                <img v-for="(img, index) in thumbnailImages" :key="index" :src="img" class="thumbnail rounded"
                    style="width: 85px; height: auto; margin-bottom: 5px; cursor: pointer;"
                    @click="setActiveImageFromThumbnail(img)" />

            </div>

            <!-- Product Details -->
            <div class="col-lg-7 p-2">
                <h4>{{ product.maAoDai }} - {{ product.tenAoDai.toUpperCase() }}</h4>
                <p class="fw-bold">{{ $t('product.code') }} {{ displayProductCode }}</p>
                <p class="text-success" v-if="product.trangThai">{{ $t('product.inStock') }}</p>
                <hr />
                <div class="d-flex justify-content-between w-100">
                    <div style="width: 70%;">
                        <div class="d-flex align-items-center">
                            <h4>{{ $t('product.price') }} {{ displayPrice }}</h4>
                            <del v-if="displayPrice !== displayPriceIm && selectedVariant"
                                class="text-danger mx-3 mb-2">{{
                                    displayPriceIm }}</del>
                        </div>
                        <!-- Chọn màu sắc -->
                        <div class="mt-3">
                            <p class="fw-bold">{{ $t('product.color') }}</p>
                            <div>
                                <button v-for="color in availableColors" :key="color.id" class="btn me-2 color-btn"
                                    :class="{
                                        enabled: selectedColor === color.id,
                                        disabled: disabledColors[color.id]
                                    }" @click="selectColor(color.id)" :disabled="disabledColors[color.id]">
                                    {{ color.tenMauSac }}
                                </button>
                            </div>
                        </div>
                        <!-- Chọn kích thước -->
                        <div class="mt-3">
                            <p class="fw-bold">{{ $t('product.size') }}</p>
                            <div>
                                <button v-for="size in availableSizes" :key="size.id" class="btn me-2 size-btn" :class="{
                                    enabled: selectedSize === size.id,
                                    disabled: disabledSizes[size.id]
                                }" @click="selectSize(size.id)" :disabled="disabledSizes[size.id]">
                                    {{ size.tenKichThuoc }}
                                </button>
                            </div>
                        </div>
                        <!-- Input số lượng đặt hàng -->
                        <div class="mt-3">
                            <p class="fw-bold">{{ $t('product.quantity') }}</p>
                            <div class="d-flex align-items-center gap-3 w-100">
                                <div class="quantity-container mt-1 border">
                                    <button class="btn btn-secondary btn-sm" @click="decreaseQuantity"
                                        :disabled="orderQuantity <= 1">
                                        <i class="bi bi-dash-lg"></i>
                                    </button>
                                    <input type="number" class="quantity-input" v-model.number="orderQuantity" min="1"
                                        :max="maxQuantity" style="width: 70px;" @blur="checkQuantity" />
                                    <button class="btn btn-secondary btn-sm" @click="increaseQuantity"
                                        :disabled="orderQuantity >= maxQuantity">
                                        <i class="bi bi-plus-lg"></i>
                                    </button>
                                </div>
                                <small class="text-danger ms-1" v-if="orderQuantity > maxQuantity && selectedVariant">
                                    {{ $t('product.quantityErrorMax') }}
                                </small>
                                <small v-if="orderQuantity <= 0" class="text-danger ms-1">
                                    {{ $t('product.quantityErrorMin') }}
                                </small>
                            </div>
                        </div>
                        <small class="text-muted">{{ $t('product.stock', { n: maxQuantity }) }}</small>
                        <!-- Thêm vào giỏ và Mua ngay -->
                        <div class="mt-4 d-flex gap-3" style="width: 95%;">
                            <button class="btn btn-secondary1 flex-grow-1" style="width: 40%;"
                                :disabled="!selectedVariant" @click="addToCart">
                                <i class="bi bi-cart-plus"></i> {{ $t('product.addToCart') }}
                            </button>
                            <button class="btn btn-success1 flex-grow-1" style="width: 40%;"
                                :disabled="!selectedVariant" @click="buyNow">
                                <i class="bi bi-credit-card"></i> {{ $t('product.buyNow') }}
                            </button>
                        </div>
                    </div>
                    <div style="width: 30%; border-left: 1px solid #ccc; padding: 0 10px;">
                        <h6>{{ $t('product.details') }}</h6>
                        <p>{{ $t('product.material') }} {{ product.tenChatLieu }}</p>
                        <p>{{ $t('product.supplier') }} {{ product.tenNhaCungCap }}</p>
                        <p>{{ $t('product.aoDaiType') }} {{ product.tenLoaiAoDai }}</p>
                        <p>{{ $t('product.sleeve') }} {{ product.tenTaAo }}</p>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="product.moTa">
            <hr>
            <h4>{{ $t('product.description') }}</h4>
            <p v-html="product.moTa" class="product-description"></p>
        </div>
        <div v-if="youtubeEmbedUrl" class="mt-5">
            <h3 class="mt-5">{{ $t('product.video') }}</h3>
            <hr />
            <div v-if="youtubeEmbedUrl" class="text-center">
                <iframe width="1000px" height="562px" :src="youtubeEmbedUrl" frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                    allowfullscreen></iframe>
            </div>
        </div>
    </div>
    <div v-else class="container mt-4">
        <p>{{ $t('product.loading') }}</p>
    </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onBeforeUnmount, h } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getSanPhamById } from "../san_pham/api";
import { useCartStore } from "../san_pham_gio_hang/cart";
import axios from "axios";
import { message, notification } from "ant-design-vue";
import { useI18n } from "vue-i18n";

const isLoading = ref(false);
const { t } = useI18n();
const route = useRoute();
const router = useRouter();
const productId = route.params?.id;
const product = ref(null);
const activeImageIndex = ref(0);
const orderQuantity = ref(1);
const sliderTimer = ref(null);
const selectedColor = ref(null);
const selectedSize = ref(null);
const availableColors = ref([]);
const availableSizes = ref([]);
const cartStore = useCartStore();
const idKhachHang = sessionStorage.getItem('userId');

function formatCurrency(value) {
    const num = typeof value === "number" ? value : parseFloat(value);
    if (isNaN(num)) return "";
    return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND"
    }).format(num);
}


const youtubeEmbedUrl = computed(() => {
    if (!product.value || !product.value.linkYoutube) return "";
    const regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#&?]*).*/;
    const match = product.value.linkYoutube.match(regExp);
    const videoId = match && match[7].length === 11 ? match[7] : null;
    return videoId ? `https://www.youtube.com/embed/${videoId}` : "";
});

const selectedVariant = computed(() => {
    if (!product.value || !product.value.sanPhamChiTietList) return null;
    return product.value.sanPhamChiTietList.find(
        (spct) =>
            spct.trangThai === true &&
            spct.soLuong > 0 &&
            spct.mauSacId === selectedColor.value &&
            spct.kichThuocId === selectedSize.value
    );
});

const displayPrice = computed(() => {
    if (selectedVariant.value) return formatCurrency(selectedVariant.value.giaBan);
    if (product.value?.sanPhamChiTietList?.length) {
        const defaultVariant = product.value.sanPhamChiTietList.find(
            (spct) => spct.trangThai === true
        );
        return defaultVariant ? formatCurrency(defaultVariant.giaBan) : "";
    }
    return "";
});

const setActiveImageFromThumbnail = (imgUrl) => {
    clearTimeout(sliderTimer.value);
    const index = sliderImages.value.findIndex(url => url === imgUrl);
    if (index !== -1) {
        activeImageIndex.value = index;
    }
    startSliderTimer();
};


const displayPriceIm = computed(() => {
    if (selectedVariant.value) return formatCurrency(selectedVariant.value.giaGoc);
    if (product.value?.sanPhamChiTietList?.length) {
        const defaultVariant = product.value.sanPhamChiTietList.find(
            (spct) => spct.trangThai === true
        );
        return defaultVariant ? formatCurrency(defaultVariant.giaGoc) : "";
    }
    return "";
});

const displayProductCode = computed(() => {
    if (selectedVariant.value) return selectedVariant.value.maAoDaiChiTiet;
    return "";
});

const maxQuantity = computed(() => {
    if (!selectedVariant.value) return t("product.stockError");
    return selectedVariant.value ? selectedVariant.value.soLuong : 0;
});

const thumbnailImages = computed(() => {
    const imgs = []
    // 1. Ảnh chung của sản phẩm
    if (product.value?.anhList?.length) {
        product.value.anhList
            .filter(url => url?.trim())
            .forEach(url => imgs.push(url))
    }

    // 2. Ảnh biến thể, mỗi màu chỉ lấy 1 ảnh
    const seenColors = new Set()
    if (product.value?.sanPhamChiTietList?.length) {
        product.value.sanPhamChiTietList.forEach(spct => {

            // chỉ xét biến thể đang hoạt động và còn hàng
            if (spct.trangThai && spct.soLuong > 0 && spct.anhUrl?.trim()) {
                const color = spct.tenMauSac  // hoặc spct.color, spct.maMau tuỳ tên trường
                if (!seenColors.has(color)) {
                    seenColors.add(color)
                    imgs.push(spct.anhUrl)
                }
            }
        })
    }

    return imgs
})

const sliderImages = computed(() => {
    // giữ lại cơ chế đẩy ảnh variant được chọn lên đầu
    let imgs = thumbnailImages.value.slice()
    if (selectedVariant.value?.anhUrl) {
        imgs = imgs.filter(u => u !== selectedVariant.value.anhUrl)
        imgs.unshift(selectedVariant.value.anhUrl)
    }
    return imgs
})



const setActiveImage = (index) => {
    clearTimeout(sliderTimer.value);
    activeImageIndex.value = index;
};

const prevImage = () => {
    clearTimeout(sliderTimer.value);
    activeImageIndex.value =
        activeImageIndex.value > 0 ? activeImageIndex.value - 1 : sliderImages.value.length - 1;
};

const nextImage = () => {
    clearTimeout(sliderTimer.value);
    activeImageIndex.value =
        activeImageIndex.value < sliderImages.value.length - 1 ? activeImageIndex.value + 1 : 0;
};

const startSliderTimer = () => {
    sliderTimer.value = setTimeout(() => {
        nextImage();
        startSliderTimer();
    }, 5000);
};

const selectColor = (colorId) => {
    selectedColor.value = selectedColor.value === colorId ? null : colorId;
    selectSize(null);
};

const selectSize = (sizeId) => {
    selectedSize.value = selectedSize.value === sizeId ? null : sizeId;
};

const disabledColors = computed(() => {
    const result = {};
    if (!selectedSize.value || !product.value) return result;
    availableColors.value.forEach((color) => {
        const exists = product.value.sanPhamChiTietList.some(
            (spct) =>
                spct.trangThai === true &&
                spct.soLuong > 0 &&
                spct.kichThuocId === selectedSize.value &&
                spct.mauSacId === color.id
        );
        result[color.id] = !exists;
    });
    return result;
});

const disabledSizes = computed(() => {
    const result = {};
    if (!selectedColor.value || !product.value) return result;
    availableSizes.value.forEach((size) => {
        // Tìm tất cả các variant có màu đã chọn và kích thước tương ứng
        const matchingVariants = product.value.sanPhamChiTietList.filter(
            (spct) =>
                spct.trangThai === true &&
                spct.soLuong > 0 &&
                spct.mauSacId === selectedColor.value &&
                spct.kichThuocId === size.id
        );
        // Tính tổng số lượng của các variant này
        const totalQuantity = matchingVariants.reduce(
            (sum, variant) => sum + Number(variant.soLuong),
            0
        );
        // Nếu tổng số lượng <= 0 thì disable kích thước đó
        result[size.id] = totalQuantity <= 0;
    });
    return result;
});


const increaseQuantity = () => {
    if (orderQuantity.value < maxQuantity.value) orderQuantity.value++;
};

const decreaseQuantity = () => {
    if (orderQuantity.value > 1) orderQuantity.value--;
};

const checkQuantity = () => {
    if (!orderQuantity.value || orderQuantity.value < 1) orderQuantity.value = 1;
    if (orderQuantity.value > maxQuantity.value) orderQuantity.value = maxQuantity.value;
};

const addToCart = async () => {

    if (!idKhachHang) {
        router.push('/dang-nhap');
        return;
    }

    if (!selectedVariant.value) return;
    const variant = selectedVariant.value;
    const newCartItem = {
        idSanPhamChiTiet: variant.id,
        soLuong: orderQuantity.value,
        // idKhachHang: 1 // ID khách hàng tạm thời
        idKhachHang: Number(sessionStorage.getItem('userId'))
    };

    // Kiểm tra nếu số lượng trong giỏ đã đạt tới số lượng tồn kho của spct
    const existingCartItem = cartStore.cartItems.find(
        (item) => Number(item.idSanPhamChiTiet) === Number(variant.id)
    );
    if (existingCartItem && existingCartItem.soLuong >= Number(variant.soLuong)) {
        notification.error({
            message: t("cart.add.message"),
            description: t("cart.add.errorQuantity")
        });
        return;
    }

    if (existingCartItem && (existingCartItem.soLuong + orderQuantity.value) > Number(variant.soLuong)) {
        notification.error({
            message: t("cart.add.message"),
            description: t("cart.add.errorQuantity")
        });
        return;
    }

    try {
        if (typeof cartStore.addItem === "function") {
            const addItem = cartStore.addItem(newCartItem);
            if (addItem.soLuong !== newCartItem.soLuong) {
                const response = await axios.put(`/api/gio_hang/${addItem.id}`, {
                    soLuong: addItem.soLuong
                });
                cartStore.updateItemQuantity(addItem.id, response.data.soLuong);
            } else {
                const response = await axios.post("/api/gio_hang", newCartItem);
                cartStore.updateItemWithBackendData(newCartItem.idSanPhamChiTiet, response.data);
            }
        } else {
            console.error("cartStore.addItem không được định nghĩa");
        }
        openNotification();
    } catch (error) {
        console.error("Lỗi thêm sản phẩm vào giỏ hàng:", error);
        notification.error({
            message: t("cart.add.message"),
            description: t("cart.add.error")
        });
    }
};

const openNotification = () => {
    const key = `openNotification${Date.now()}`;
    notification.success({
        key,
        message: t("cart.add.notification.success.message"),
        description: h("div", {}, [
            h("div", {}, t("cart.add.notification.success.description")),
            h("div", { style: { marginTop: "10px", color: "#f44336" } }, t("cart.add.notification.success.viewCart"))
        ]),
        style: {
            cursor: "pointer"
        },
        onClick: () => {
            cartStore.setCartVisibility(true);
            notification.close(key);
        }
    });
};

const buyNow = () => {
    isLoading.value = true;
    if (!idKhachHang) {
        router.push('/dang-nhap');
        return;
    }
    setTimeout(() => {
        if (!selectedVariant.value) return;
        const variant = selectedVariant.value;
        variant.soLuong = orderQuantity.value;
        cartStore.setCheckoutProducts([variant]);
        router.push("/thanh-toan");
    }, 500);
};

onMounted(async () => {
    try {
        const data = await getSanPhamById(productId);
        product.value = data;

        // Nếu chưa có anhList, sử dụng placeholder
        if (!product.value.anhList || !Array.isArray(product.value.anhList) || product.value.anhList.length === 0) {
            product.value.anhList = ["https://placehold.jp/800x1200.png"];
        }

        // Tạo availableColors và availableSizes từ sanPhamChiTietList
        if (product.value.sanPhamChiTietList && Array.isArray(product.value.sanPhamChiTietList)) {
            const colorMap = new Map();
            const sizeMap = new Map();
            product.value.sanPhamChiTietList.forEach((spct) => {
                if (spct.trangThai === true && spct.soLuong > 0) {
                    if (spct.mauSacId && spct.tenMauSac && !colorMap.has(spct.mauSacId)) {
                        colorMap.set(spct.mauSacId, {
                            id: spct.mauSacId,
                            tenMauSac: spct.tenMauSac
                        });
                    }
                    if (spct.kichThuocId && spct.tenKichThuoc && !sizeMap.has(spct.kichThuocId)) {
                        sizeMap.set(spct.kichThuocId, {
                            id: spct.kichThuocId,
                            tenKichThuoc: spct.tenKichThuoc
                        });
                    }
                }
            });
            availableColors.value = Array.from(colorMap.values());
            availableSizes.value = Array.from(sizeMap.values());

            startSliderTimer();
        }
    } catch (error) {
        console.error("Error fetching product details:", error);
    }
});

watch(selectedVariant, (newVal) => {
    if (newVal) {
        activeImageIndex.value = 0;
        clearTimeout(sliderTimer.value);
    }
});

onBeforeUnmount(() => {
    clearTimeout(sliderTimer.value);
});
</script>

<style scoped>
:deep(.product-description img) {
    max-width: 100%;
    height: auto;
    width: 700px;
    display: block;
    margin: 10px auto;
    border-radius: 8px;
}

.carousel-item {
    transition: opacity 0.5s ease !important;
}

.carousel-item.active {
    opacity: 1 !important;
}

.carousel-item:not(.active) {
    opacity: 0 !important;
    display: block !important;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
}

.thumbnail {
    max-width: 100%;
    cursor: pointer;
    border: 2px solid transparent;
}

.thumbnail:hover {
    border: 2px solid #007bff;
}

/* CSS cho nút màu sắc và kích thước */
.color-btn,
.size-btn {
    display: inline-block;
    border: 1px solid black;
    background-color: white;
    color: black;
    text-align: center;
    cursor: pointer;
    padding: 2px 10px;
    border-top-left-radius: 10px;
    border-bottom-right-radius: 10px;
    border-top-right-radius: 0px;
    border-bottom-left-radius: 0px;
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

.color-btn.enabled,
.size-btn.enabled {
    background-color: #000;
    color: #fff;
    position: relative;
    border: 2px solid rgb(255, 255, 255);
}

.color-btn.disabled,
.size-btn.disabled {
    position: relative;
    background-color: #949494;
}

.color-btn.enabled::after,
.size-btn.enabled::after {
    content: "\1F5F8";
    position: absolute;
    color: #006919;
    font-size: 30px;
    font-weight: bold;
    top: 60%;
    left: 100%;
    transform: translate(-50%, -50%);
}

.color-btn:not(.disabled):hover,
.size-btn:not(.disabled):hover {
    background-color: #cbcbcb;
}

/* Focus: hiệu ứng đổ bóng nhẹ khi focus */
.color-btn:not(.disabled):focus,
.size-btn:not(.disabled):focus {
    outline: none;
}

.quantity-container {
    display: flex;
    align-items: center;
    border-top-left-radius: 20px;
    border-bottom-right-radius: 20px;
    border-top-right-radius: 0px;
    border-bottom-left-radius: 0px;
}

.btn-secondary {
    border-top-left-radius: 10px;
    border-bottom-right-radius: 10px;
    border-top-right-radius: 0px;
    border-bottom-left-radius: 0px;
}

.quantity-input {
    text-align: center;
    border: none;
    outline: none;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

/* Tùy chỉnh nút mua  */
.btn-secondary1 {
    background-color: #ffffff;
    border-color: #000000;
    padding: 10px 20px;
    font-weight: bold;
    border-top-left-radius: 20px;
    border-bottom-right-radius: 20px;
    border-top-right-radius: 0px;
    border-bottom-left-radius: 0px;
}

.btn-success1 {
    background-color: #090b09;
    border-color: #ffffff;
    color: white;
    padding: 10px 20px;
    font-weight: bold;
    border-top-left-radius: 20px;
    border-bottom-right-radius: 20px;
    border-top-right-radius: 0px;
    border-bottom-left-radius: 0px;
}

.btn-secondary1:hover {
    background-color: #cbcbcb;
    border-color: #ffffff;
}

.btn-success1:hover {
    background-color: #4a4a4a;
    border-color: #000000;
}

.btn:disabled {
    opacity: 0.65;
    cursor: not-allowed;
}

p {
    margin: 0;
}

.full-screen-spin {
    position: fixed;
    top: 0;
    right: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.65);
    z-index: 9999;
}
</style>