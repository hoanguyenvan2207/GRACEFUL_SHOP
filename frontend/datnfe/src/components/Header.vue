<template>
  <div class="app-layout">
    <aside :class="['menu-sidebar', { collapsed }]">
      <a-button type="text" class="collapse-btn" @click="collapsed = !collapsed">
        <i :class="collapsed ? 'fas fa-angle-right' : 'fas fa-angle-left'" />
      </a-button>

      <div class="logo-wrapper">
        <img src="https://res.cloudinary.com/defcm50t7/image/upload/v1740129073/logo_graceful_lpd32u.png"
          :style="{ width: collapsed ? '60px' : '200px' }" alt="Logo" />
      </div>

      <div class="menu-scroll">
        <a-menu v-model:openKeys="openKeys" v-model:selectedKeys="selectedKeys" :inline-collapsed="collapsed"
          :items="computedItems" mode="inline" @click="onMenuClick" />
      </div>

      <div class="user-info">
        <a-avatar :size="collapsed ? 40 : 50" class="user-avatar">
          <template #icon><i class="bi bi-person" /></template>
        </a-avatar>
        <div v-if="!collapsed" class="greeting">
          {{ timeGreeting }}<br /><strong>{{ userName }}</strong>
        </div>
      </div>
    </aside>

    <main class="content-area">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';
import { h } from 'vue';

// trạng thái collapsed
const collapsed = ref(false);

// router / vuex
const store = useStore();
const isAdmin = computed(() => store.state.roles.includes('ROLE_QUANLY'));
const router = useRouter();
const route = useRoute();

// danh sách menu tĩnh
const menuData = [
  { label: 'Bán hàng', key: 'ban-hang', icon: () => h('i', { class: 'fas fa-store-alt' }), path: '/ban-hang' },
  { label: 'Quản lý hóa đơn', key: 'hoa-don', icon: () => h('i', { class: 'fas fa-file-invoice' }), path: '/hoa-don/hien-thi' },
  { label: 'Quản lý đơn hàng', key: 'don-hang', icon: () => h('i', { class: 'fa-solid fa-truck-fast' }), path: '/quan-ly-don-hang' },
  {
    label: 'Quản lý sản phẩm', key: 'san-pham', icon: () => h('i', { class: 'fa-solid fa-shirt' }), children: [
      { label: 'Sản phẩm', key: 'san-pham-san-pham', path: '/san-pham/list/all' },
      { label: 'Biến thể sản phẩm', key: 'san-pham-variant', path: '/san-pham-chi-tiet/list/all' },
      {
        label: 'Quản lý thuộc tính', key: 'thuoc-tinh', icon: () => h('i', { class: 'fa-solid fa-palette' }), children: [
          { label: 'Chất liệu', key: 'thuoc-tinh-chat-lieu', path: '/chat-lieu/list/all' },
          { label: 'Loại áo dài', key: 'thuoc-tinh-loai-ao-dai', path: '/loai-ao-dai/list/all' },
          { label: 'Tà áo', key: 'thuoc-tinh-ta-ao', path: '/ta-ao/list/all' },
          { label: 'Nhà cung cấp', key: 'thuoc-tinh-ncc', path: '/nha-cung-cap/list/all' },
          { label: 'Màu sắc', key: 'thuoc-tinh-mau-sac', path: '/mau-sac/list/all' },
          { label: 'Kích thước', key: 'thuoc-tinh-kich-thuoc', path: '/kich-thuoc/list/all' },
        ]
      }
    ]
  },
  { label: 'Quản lý giảm giá', key: 'giam-gia', icon: () => h('i', { class: 'fas fa-percent' }), path: '/quan-ly-giam-gia' },
  { label: 'Khách hàng', key: 'khach-hang', icon: () => h('i', { class: 'fas fa-users' }), path: '/khach-hang/list' },
  { label: 'Quản lý nhân viên', key: 'nhan-vien', icon: () => h('i', { class: 'fas fa-user-tie' }), path: '/nhan-vien/list' },
  { label: 'Thống kê', key: 'thong-ke', icon: () => h('i', { class: 'bi bi-bar-chart-fill' }), path: '/thong-ke' },
  { label: 'Tài khoản', key: 'tai-khoan', icon: () => h('i', { class: 'fas fa-user-circle' }), path: '/staff' },
];

// build các map để tra cứu nhanh
const keyToItem = new Map();
const ancestorsMap = new Map();
const pathToKey = new Map();

function buildMaps(list, parents = []) {
  list.forEach(item => {
    keyToItem.set(item.key, item);
    ancestorsMap.set(item.key, parents);
    if (item.path) pathToKey.set(item.path, item.key);
    if (item.children) {
      buildMaps(item.children, [...parents, item.key]);
    }
  });
}
buildMaps(menuData);

// filter theo quyền
const computedItems = computed(() =>
  menuData.filter(i => !(i.key === 'nhan-vien' && !isAdmin.value))
);

// trạng thái menu
const selectedKeys = ref([]);
const openKeys = ref([]);

// khi click menu
function onMenuClick({ key }) {
  const item = keyToItem.get(key);
  if (item.path) {
    router.push(item.path);
    selectedKeys.value = [key];
  }
  if (item.children) {
    openKeys.value = [key];
  } else {
    openKeys.value = ancestorsMap.get(key) || [];
  }
}

// đồng bộ khi route thay đổi
watch(() => route.path, p => {
  const k = pathToKey.get(p);
  selectedKeys.value = k ? [k] : [];
  openKeys.value = k ? ancestorsMap.get(k) : [];
}, { immediate: true });

// user-info
const userName = ref('');
onMounted(() => {
  userName.value = (sessionStorage.getItem('hoVaTen') || 'KHÁCH').toUpperCase();
});

const timeGreeting = computed(() => {
  const hour = parseInt(
    new Intl.DateTimeFormat('vi-VN', {
      hour: 'numeric', hour12: false, timeZone: 'Asia/Ho_Chi_Minh'
    }).format(new Date()),
    10
  );
  if (hour < 12) return 'Xin chào buổi sáng!';
  if (hour < 18) return 'Xin chào buổi chiều!';
  return 'Xin chào buổi tối!';
});
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
}

.menu-sidebar {
  display: flex;
  flex-direction: column;
  height: 100vh;
  position: relative;
  background: #ffffff;
  transition: width 0.2s ease-in-out;
}

.menu-scroll {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.menu-sidebar .ant-menu {
  border-right: none !important;
  box-shadow: none !important;
}

:deep(.menu-scroll::-webkit-scrollbar) {
  display: none;
}

.logo-wrapper {
  margin-top: 10px;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
}

.collapse-btn {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 10;
  padding: 4px;
  font-size: 18px;
  color: #000;
}

.collapse-btn:hover {
  background: rgba(0, 0, 0, 0.05);
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
  margin-top: 10px;
}

.user-avatar {
  margin-bottom: 8px;
}

.greeting {
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  line-height: 1.2;
  color: #333;
}

.content-area {
  flex: 1;
  padding: 10px;
  background: #f0f2f5;
  overflow-y: auto;
  transition: width 0.2s ease-in-out;
}
</style>