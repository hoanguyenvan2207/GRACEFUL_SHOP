<template>
  <div id="app">
    <component :is="layoutComponent" />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import AdminLayout from './layouts/AdminLayout.vue';
import CustomerLayout from './layouts/CustomerLayout.vue';
import PaymentLayout from './components/san_pham_gio_hang/ThanhToan.vue';
import Layout from './layouts/Layout.vue';
import { onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const route = useRoute();

const layoutComponent = computed(() => {
  const layout = route.meta?.layout;
  if (layout === 'payment') {
    return PaymentLayout;
  }
  if (layout === 'admin') {
    return AdminLayout;
  }
  if (layout === 'customer') {
    return CustomerLayout;
  }
  return Layout;
});

axios.defaults.baseURL = '/api';
axios.defaults.withCredentials = true;

function checkAuth() {
  axios.get('/api/auth/check', { withCredentials: true })
    .then(res => {
      if (!res.data.authenticated) throw new Error();
    })
    .catch(err => {
      if (err.response?.status === 401) {
        sessionStorage.clear();
        message.warning('Phiên đã hết hạn hoặc server không phản hồi.');
        const layout = route.meta?.layout;
        if (layout === 'customer' || layout === 'payment') {
          router.push("/dang-nhap");
        } else {
          router.push("/admin");
        }
      }
    });
}

function onVisibilityChange() {
  const { requiresAuth, layout } = router.currentRoute.value.meta;
  const hasSession = layout === 'admin'
    ? !!sessionStorage.getItem('user')
    : !!sessionStorage.getItem('customerInfo');

  if (document.visibilityState === 'visible' && requiresAuth && hasSession) {
    checkAuth();
  }
}

onMounted(() => {
  onVisibilityChange();
  document.addEventListener('visibilitychange', onVisibilityChange);
});


</script>
