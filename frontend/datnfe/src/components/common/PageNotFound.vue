<template>
    <a-result status="404" title="404"
        sub-title="Xin lỗi, trang bạn truy cập không tồn tại. Vui lòng quay lại hoặc liên hệ quản trị viên.">
        <template #extra>
            <a-button type="primary" @click="goBack">Quay lại</a-button>
        </template>
    </a-result>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
function goBack() {
    if (window.history.length > 1) {
        router.back()
    } else {
        router.push('/admin')
    }
}
</script>