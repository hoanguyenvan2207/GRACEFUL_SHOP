<template>
  <Carousel />
  <div class="bg-white">
    <div class="container">
      <div class="row mt-5">
        <!-- Bộ lọc -->
        <div class="col-12 col-md-3 col-lg-2">
          <div class="card w-100 shadow mt-md-5 mt-3">
            <div class="card-header">
              <i class="bi bi-funnel">{{ $t('filter.title') }}</i>
            </div>
            <div class="card-body">
              <!-- Lọc theo loại áo dài -->
              <a-select v-model:value="filterParams.listLoaiAoDai" mode="multiple" allowClear
                :placeholder="$t('filter.aoDaiType')" :max-tag-count="1"
                :options="loaiAoDaiOptions.map(option => ({ value: option, label: option }))"
                @change="handleFilterChange" class="w-100" />
              <!-- Lọc theo chất liệu -->
              <a-select v-model:value="filterParams.listChatLieu" mode="multiple" allowClear
                :placeholder="$t('filter.material')" :max-tag-count="2"
                :options="chatLieuOptions.map(option => ({ value: option, label: option }))"
                @change="handleFilterChange" class="w-100 mt-3" />
              <!-- Lọc theo tà áo -->
              <a-select v-model:value="filterParams.listTaAo" mode="multiple" allowClear
                :placeholder="$t('filter.sleeve')" :max-tag-count="1"
                :options="taAoOptions.map(option => ({ value: option, label: option }))" @change="handleFilterChange"
                class="w-100 mt-3" />
              <!-- Lọc theo nhà cung cấp -->
              <a-select v-model:value="filterParams.listNhaCungCap" mode="multiple" allowClear
                :placeholder="$t('filter.supplier')" :max-tag-count="1"
                :options="nhaCungCapOptions.map(option => ({ value: option, label: option }))"
                @change="handleFilterChange" class="w-100 mt-3" />
              <!-- Lọc theo màu sắc -->
              <a-select v-model:value="filterParams.listMauSac" mode="multiple" allowClear
                :placeholder="$t('filter.color')" :max-tag-count="1"
                :options="mauSacOptions.map(option => ({ value: option, label: option }))" @change="handleFilterChange"
                class="w-100 mt-3" />
              <!-- Lọc theo kích thước -->
              <a-select v-model:value="filterParams.listKichThuoc" mode="multiple" allowClear
                :placeholder="$t('filter.size')" :max-tag-count="2"
                :options="kichThuocOptions.map(option => ({ value: option, label: option }))"
                @change="handleFilterChange" class="w-100 mt-3" />
            </div>
          </div>
          <!-- Giữ nguyên phần lọc giá -->
          <div class="card mb-3 mt-4 shadow">
            <div class="card-header">{{ $t('filter.price') }}</div>
            <div class="card-body">
              <div class="row g-2">
                <div class="col-12">
                  <a-input-number v-model:value="filterParams.minPrice" :placeholder="$t('filter.priceFrom')" :min="0"
                    class="w-100" @change="handleFilterChange"
                    :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="value => value.replace(/\$\s?|(,*)/g, '')" />
                </div>
                <div class="col-12">
                  <a-input-number v-model:value="filterParams.maxPrice" :placeholder="$t('filter.priceTo')"
                    class="w-100" @change="handleFilterChange"
                    :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="value => value.replace(/\$\s?|(,*)/g, '')" />
                </div>
              </div>
            </div>
          </div>
          <button v-if="hasActiveFilters" class="btn btn-danger btn-sm text-center" @click="handleClearFilters"
            style="margin-left: 20%;">
            <i class="bi bi-eraser me-2"></i>{{ $t('filter.clear') }}
          </button>
        </div>
        <!-- Danh sách sản phẩm -->
        <div class="col-12 col-md-9 col-lg-10">
          <div class="row">
            <!-- Tìm kiếm -->
            <div class="col-12 d-flex flex-wrap gap-2 align-items-center mb-3">
              <h6 class="fw-bold text-dark text-muted mb-0 flex-grow-1">{{ $t('products.title') }}</h6>
              <div class="flex-grow-1" style="min-width: 200px;">
                <a-input :placeholder="$t('products.search')" v-model:value="filterParams.keyword"
                  @input="handleFilterChange" class="rounded-pill" style="width: 100%;" />
              </div>
              <!-- Sắp xếp -->
              <div class="d-flex align-items-center gap-2 flex-grow-1">
                <label class="form-label mb-0" style="width: 100px;">{{ $t('products.sort.label')
                }}</label>
                <a-select v-model:value="sortBy" @change="handleSortChange" class="rounded-pill-select"
                  :placeholder="$t('products.sort.default')" style="width: 100%;">
                  <a-select-option value="">{{ $t('products.sort.default') }}</a-select-option>
                  <a-select-option value="price_asc">{{ $t('products.sort.priceAsc')
                  }}</a-select-option>
                  <a-select-option value="price_desc">{{ $t('products.sort.priceDesc')
                  }}</a-select-option>
                  <a-select-option value="date_desc">{{ $t('products.sort.dateDesc')
                  }}</a-select-option>
                  <a-select-option value="date_asc">{{ $t('products.sort.dateAsc')
                  }}</a-select-option>
                </a-select>
              </div>
            </div>

            <div v-for="product in products" class="col-6 col-sm-4 col-md-4 col-lg-3 p-2 mb-4">
              <!-- Thêm sự kiện click vào toàn bộ card -->
              <div class="card h-100 product-items position-relative" style="cursor: pointer;"
                @click="redirectTo(product.href)">
                <div class="img-container" style="position: relative; aspect-ratio: 2/3; overflow: hidden;">
                  <img :src="product.imageSrc || 'https://placehold.jp/800x1200.png'" :alt="product.tenAoDai"
                    class="card-img-top img-fluid rounded h-100" style="object-fit: cover;">
                  <!-- Icon mắt nằm bên trong container ảnh -->
                  <i class="bi bi-eye hover-icon"></i>
                </div>
                <div class="card-body d-flex flex-column justify-content-between">
                  <div class="d-flex justify-content-between align-items-center p-0">
                    <div class="d-flex justify-content-start text-warning ">
                      <i class="bi bi-star-fill me-1"></i>
                      <i class="bi bi-star-fill me-1"></i>
                      <i class="bi bi-star-fill me-1"></i>
                      <i class="bi bi-star-fill me-1"></i>
                      <i class="bi bi-star-fill me-1"></i>
                    </div>
                    <button class="btn btn-add rounded-pill" @click.stop="addToCart(product)">
                      <i class="fa-solid fa-cart-plus"></i>
                    </button>
                    <!-- <div class="text-danger col-2 text-end"><i class="bi bi-heart"></i></div> -->
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
          <p class="text-muted fw-bold text-end">{{ $t('products.total', { n: page.totalElements }) }}</p>
          <!-- Phân trang -->
          <nav v-if="page.totalPages > 1" class="pagination-container">
            <ul class="pagination">
              <!-- Nút "Trước" -->
              <li class="page-item" :class="{ disabled: filterParams.page === 0 }">
                <a href="#" class="page-link" @click.prevent="changePage(filterParams.page - 1)">
                  <i class="fa-solid fa-chevron-left"></i>
                </a>
              </li>
              <!-- Hiển thị các trang -->
              <li v-for="(pageItem, index) in pagesToShow" :key="index" class="page-item"
                :class="{ active: pageItem === (filterParams.page + 1) }">
                <template v-if="pageItem === '...'">
                  <span class="page-link dots">{{ pageItem }}</span>
                </template>
                <template v-else>
                  <a href="#" class="page-link" @click.prevent="changePage(pageItem - 1)">
                    {{ pageItem }}
                  </a>
                </template>
              </li>
              <!-- Nút "Sau" -->
              <li class="page-item" :class="{ disabled: filterParams.page === page.totalPages - 1 }">
                <a href="#" class="page-link" @click.prevent="changePage(filterParams.page + 1)">
                  <i class="fa-solid fa-angle-right"></i>
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, h } from 'vue';
import { filterProducts } from '../san_pham/api';
import { Modal, notification } from 'ant-design-vue';
import Carousel from '../Carousel.vue';
import { useCartStore } from "../san_pham_gio_hang/cart";
import axios from 'axios';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

const { t } = useI18n();
const router = useRouter();
const products = ref([]);
const loaiAoDaiOptions = ref([]);
const chatLieuOptions = ref([]);
const taAoOptions = ref([]);
const nhaCungCapOptions = ref([]);
const mauSacOptions = ref([]);
const kichThuocOptions = ref([]);
const sortBy = ref('date_desc');
const idKhachHang = sessionStorage.getItem('userId');

const cartStore = useCartStore();

const page = reactive({
  totalElements: 0,
  totalPages: 0,
  number: 0,
  size: 8
});

const filterParams = reactive({
  listLoaiAoDai: [],
  listChatLieu: [],
  listTaAo: [],
  listNhaCungCap: [],
  listMauSac: [],
  listKichThuoc: [],
  minPrice: 0,
  maxPrice: null,
  keyword: '',
  sortPrice: '',
  sortDate: 'desc',
  page: 0,
  size: 8
});

const redirectTo = (href) => {
  window.location.href = href;
}

const formatCurrency = (value) => {
  if (value == null || isNaN(value)) return '';
  return value.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
};

const handleFilterChange = () => {
  filterParams.page = 0;
};

const handleSortChange = () => {
  switch (sortBy.value) {
    case 'price_asc':
      filterParams.sortPrice = 'asc';
      filterParams.sortDate = '';
      break;
    case 'price_desc':
      filterParams.sortPrice = 'desc';
      filterParams.sortDate = '';
      break;
    case 'date_desc':
      filterParams.sortDate = 'desc';
      filterParams.sortPrice = '';
      break;
    case 'date_asc':
      filterParams.sortDate = 'asc';
      filterParams.sortPrice = '';
      break;
    default:
      filterParams.sortPrice = '';
      filterParams.sortDate = '';
  }
  handleFilterChange();
};

const changePage = (newPage) => {
  if (newPage >= 0 && newPage < page.totalPages) {
    filterParams.page = newPage;
  }
};

const pagesToShow = computed(() => {
  const total = page.totalPages;
  const current = filterParams.page + 1;
  const delta = 1;
  const range = [];
  const rangeWithDots = [];
  let l;

  if (total <= 5) {
    for (let i = 1; i <= total; i++) {
      range.push(i);
    }
  } else {
    for (let i = 1; i <= total; i++) {
      if (
        i === 1 ||
        i === total ||
        (i >= current - delta && i <= current + delta)
      ) {
        range.push(i);
      }
    }
  }
  for (const i of range) {
    if (l) {
      if (i - l === 2) {
        rangeWithDots.push(l + 1);
      } else if (i - l > 2) {
        rangeWithDots.push('...');
      }
    }
    rangeWithDots.push(i);
    l = i;
  }
  return rangeWithDots;
});

const fetchProducts = async () => {
  try {
    const params = {
      ...filterParams,
      listLoaiAoDai: filterParams.listLoaiAoDai?.length ? filterParams.listLoaiAoDai : null,
      listChatLieu: filterParams.listChatLieu?.length ? filterParams.listChatLieu : null,
      listTaAo: filterParams.listTaAo?.length ? filterParams.listTaAo : null,
      listNhaCungCap: filterParams.listNhaCungCap?.length ? filterParams.listNhaCungCap : null,
      listMauSac: filterParams.listMauSac?.length ? filterParams.listMauSac : null,
      listKichThuoc: filterParams.listKichThuoc?.length ? filterParams.listKichThuoc : null,
      minPrice: filterParams.minPrice || 0,
      maxPrice: filterParams.maxPrice || null
    };

    // Xóa các param không có giá trị
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '') {
        delete params[key];
      }
    });

    const response = await filterProducts(params);

    // Lọc sản phẩm: chỉ lấy sản phẩm có danh sách chi tiết và có ít nhất 1 sản phẩm chi tiết có trạng thái true
    const filteredProducts = response.content.filter(product => {
      const details = product.sanPhamChiTietList;
      if (!details || details.length === 0) return false;
      // Giả sử mỗi chi tiết có thuộc tính `trangThai` (boolean)
      return details.some(detail => detail.trangThai === true);
    });

    products.value = filteredProducts.map(product => {
      const imageSrc = product.anhList?.[0];
      const giaBan = product.sanPhamChiTietList?.reduce((min, detail) =>
        Math.min(min, detail.giaBan), product.sanPhamChiTietList?.[0]?.giaBan || 0);
      const giaGoc = product.sanPhamChiTietList?.reduce((min, detail) =>
        Math.max(min, detail.giaGoc), product.sanPhamChiTietList?.[0]?.giaGoc || 0);

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

    // Cập nhật các tùy chọn lọc
    const allProducts = await filterProducts({ size: 10000 }); // Load tất cả sản phẩm
    loaiAoDaiOptions.value = [...new Set(allProducts.content.map(p => p.tenLoaiAoDai))].filter(Boolean);
    chatLieuOptions.value = [...new Set(allProducts.content.map(p => p.tenChatLieu))].filter(Boolean);
    taAoOptions.value = [...new Set(allProducts.content.map(p => p.tenTaAo))].filter(Boolean);
    nhaCungCapOptions.value = [...new Set(allProducts.content.map(p => p.tenNhaCungCap))].filter(Boolean);
    mauSacOptions.value = [...new Set(allProducts.content.flatMap(p =>
      p.sanPhamChiTietList?.map(d => d.tenMauSac) || []))].filter(Boolean);
    kichThuocOptions.value = [...new Set(allProducts.content.flatMap(p =>
      p.sanPhamChiTietList?.map(d => d.tenKichThuoc) || []))].filter(Boolean);

    page.totalElements = response.page.totalElements;
    page.totalPages = response.page.totalPages;
    page.number = response.page.number;
    page.size = response.page.size;
  } catch (error) {
    console.error("Error fetching products:", error);
  }
};

const handleClearFilters = () => {
  Modal.confirm({
    title: 'Xác nhận',
    content: 'Bạn có chắc chắn muốn xóa tất cả bộ lọc?',
    okText: 'Đồng ý',
    cancelText: 'Hủy',
    onOk() {
      // Reset logic
      filterParams.listLoaiAoDai = []
      filterParams.listChatLieu = []
      filterParams.listTaAo = []
      filterParams.listNhaCungCap = []
      filterParams.listMauSac = []
      filterParams.listKichThuoc = []
      filterParams.minPrice = 0
      filterParams.maxPrice = null
      filterParams.keyword = ''
      filterParams.sortPrice = ''
      filterParams.sortDate = ''
      filterParams.page = 0
      sortBy.value = ''
      fetchProducts()
    }
  })
}

const hasActiveFilters = computed(() => {
  return [
    filterParams.listLoaiAoDai.length,
    filterParams.listChatLieu.length,
    filterParams.listTaAo.length,
    filterParams.listNhaCungCap.length,
    filterParams.listMauSac.length,
    filterParams.listKichThuoc.length,
  ].some(value => !!value)
})


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
    // idKhachHang: 1
    idKhachHang: Number(sessionStorage.getItem('userId'))
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

watch(filterParams, fetchProducts, { deep: true });

onMounted(() => {
  fetchProducts();
});
</script>

<style scoped>
:deep(.ant-input-affix-wrapper) {
  border-radius: 50rem !important;
}

:deep(.rounded-pill-select .ant-select-selector) {
  border-radius: 50rem !important;
}

.product-items {
  transition: all 0.2s ease-in-out;
}

.product-items:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.product-link {
  height: 100%;
  text-decoration: none;
  color: inherit;
  display: block;
  position: relative;
  z-index: 1;
}

.btn-add {
  z-index: 3;
  transition: transform 0.3s ease;
  font-weight: bold;
  padding: 0 20px;
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

.pagination-container {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.pagination {
  display: flex;
  list-style: none;
  padding: 0;
  margin: 0;
}

.page-item {
  margin: 0 4px;
}

.page-link {
  display: block;
  font-size: 16px;
  padding: 4px 12px;
  text-decoration: none;
  border-radius: 0.25rem;
  border: 1px solid #000;
  color: #000;
  background-color: #fff;
  transition: background-color 0.3s ease, color 0.3s ease;
  cursor: pointer;
  border-top-left-radius: 10px;
  border-bottom-right-radius: 10px;
  border-top-right-radius: 0px;
  border-bottom-left-radius: 0px;
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

.page-link:hover {
  color: #fff;
  background-color: #000;
}

.page-item.active .page-link {
  color: #fff;
  background-color: #000;
}

.page-link:focus,
.page-link:active {
  outline: none;
  box-shadow: none;
}

.page-item.disabled .page-link {
  pointer-events: none;
  color: #999;
  background-color: #fff;
  border-color: #999;
}

.page-link.dots {
  cursor: default;
}

@media (max-width: 768px) {
  .product-items .card-title {
    font-size: 14px;
  }

  .product-items .btn-add {
    width: 30px;
    height: 30px;
    padding: 0;
  }

  .ant-select-selector {
    max-width: 100% !important;
  }
}

@media (max-width: 576px) {
  .card-header {
    font-size: 14px;
  }

  .ant-input-number,
  .ant-select {
    font-size: 14px !important;
  }

  .btn-clear-filters {
    margin-left: 0 !important;
    width: 100%;
  }
}
</style>