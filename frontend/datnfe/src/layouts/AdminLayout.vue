<!-- layouts/AdminLayout.vue -->
<template>
    <div class="admin-layout">
        <Header />
        <div class="admin-content">
        </div>
    </div>
</template>

<script>
import Header from '../components/Header.vue';

export default {
    name: 'AdminLayout',
    components: {
        Header,
    },
};
</script>
<style scoped>
.admin-content {
    margin-left: 240px;
}
</style>