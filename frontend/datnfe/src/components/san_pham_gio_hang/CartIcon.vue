<template>

    <i class="fas fa-shopping-cart text-dark position-relative shopping-cart-icon" style="font-size: 20px;"
        @click="openCart">
        <span v-if="itemCount > 0" class="position-absolute translate-middle badge rounded-pill bg-danger"
            style="font-size: 10px;">
            {{ itemCount }}
        </span>
    </i>

</template>

<script setup>
import { computed } from 'vue'
import { useCartStore } from '../san_pham_gio_hang/cart'
import { storeToRefs } from 'pinia'

const cartStore = useCartStore()
const { cartItems } = storeToRefs(cartStore)

const itemCount = computed(() => {
    return cartItems.value && Array.isArray(cartItems.value)
        ? cartItems.value.length
        : 0;
});


const openCart = () => {
    cartStore.toggleCart()
}
</script>

<style scoped>
.shopping-cart-icon {
    cursor: pointer;
    transition: transform 0.2s ease;
}

.shopping-cart-icon:hover {
    transform: scale(1.1);
}
</style>