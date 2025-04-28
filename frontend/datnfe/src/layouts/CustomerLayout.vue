<!-- layouts/CustomerLayout.vue -->
<template>
    <MenuCus />
    <FloatingChat />
    <!-- <ChatFloatButton :customerId="customerId" /> -->
    <div class="customer-layout">
        <router-view />
    </div>
    <Footer />
</template>

<script setup>
import MenuCus from '../components/MenuCus.vue'
import Footer from '../components/Footer.vue'
import FloatingChat from '../components/chat/ChatGPTFloatBtn.vue';
import ChatFloatButton from '../components/chat/ChatFloatButton.vue';
// const props = defineProps({
//     customerId: {
//         type: Number,
//         required: false,
//         default: () => {
//             const idKhachHang = sessionStorage.getItem('userId')
//             return idKhachHang ? Number(idKhachHang) : null
//         }
//     }
// })
</script>
