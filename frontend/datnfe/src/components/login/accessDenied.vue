<template>
  <a-result status="403" title="403"
    sub-title="Bạn không có quyền truy cập trang này. Vui lòng quay lại hoặc liên hệ quản trị viên.">
    <template #extra>
      <a-button type="primary" @click="goBack">
        Quay lại
      </a-button>
    </template>
  </a-result>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const savedUser = sessionStorage.getItem('user')
function goBack() {
  if (window.history.length > 1 && savedUser) {
    router.back()
  } else {
    router.push('/admin')
  }
}
</script>
