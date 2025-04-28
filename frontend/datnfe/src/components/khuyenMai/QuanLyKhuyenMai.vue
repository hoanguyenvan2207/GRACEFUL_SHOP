<template>
  <div class="container p-0">
    <!-- Bootstrap Tabs -->
    <ul class="nav nav-tabs">
      <li class="nav-item">
        <button class="nav-link" :class="{ 'active': activeTab === 'giamgia' }" @click="switchTab('giamgia')">
          Giảm giá
        </button>
      </li>
      <li class="nav-item">
        <button class="nav-link" :class="{ 'active': activeTab === 'khuyenmai' }" @click="switchTab('khuyenmai')">
          Khuyến mãi
        </button>
      </li>
    </ul>

    <!-- Tab content -->
    <div class="tab-content mt-3">
      <div v-show="activeTab === 'giamgia'">
        <GiamGia />
      </div>
      <div v-show="activeTab === 'khuyenmai'">
        <KhuyenMai />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import GiamGia from './GiamGia.vue'
import KhuyenMai from './KhuyenMai.vue'

const route = useRoute()
const router = useRouter()
const activeTab = ref('giamgia') // Giá trị mặc định

// Xử lý khi route thay đổi
watch(() => route.path, (newPath) => {
  if (newPath.includes('/admin/quan-ly-giam-gia') || newPath.includes('/admin/giam-gia')) {
    activeTab.value = 'giamgia'
  } else if (newPath.includes('/admin/khuyen-mai')) {
    activeTab.value = 'khuyenmai'
  }
}, { immediate: true })

// Xử lý chuyển tab
const switchTab = (tabName) => {
  activeTab.value = tabName
  const pathMap = {
    giamgia: '/giam-gia',
    khuyenmai: '/khuyen-mai'
  }
  router.push(pathMap[tabName])
}
</script>

<style scoped>
/* Style Bootstrap tabs theo nhu cầu */
.nav-tabs {
  border-bottom: 2px solid #dee2e6;
}

.nav-link {
  color: #007bff;
  border: 1px solid transparent;
  border-radius: 0.25rem 0.25rem 0 0;
  transition: all 0.3s;
}

.nav-link.active {
  color: #495057;
  background-color: #fff;
  border-color: #dee2e6 #dee2e6 #fff;
}

/* Hiệu ứng hover giống Ant Design */
.nav-link:hover:not(.active) {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}
</style>