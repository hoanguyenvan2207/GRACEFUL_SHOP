// ../san_pham_gio_hang/cart.js
import { defineStore } from 'pinia';

export const useCartStore = defineStore('cart', {
    state: () => ({
        cartItems: [],  // phải có property này
        cartVisible: false,
        buyNowProduct: null,
        checkoutProducts: []   // state để lưu sản phẩm "mua ngay"
    }),
    actions: {
        setCartVisibility(visible) {
            this.cartVisible = visible;
        },
        toggleCart() {
            this.cartVisible = !this.cartVisible;
        },
        setCartItems(items) {
            this.cartItems = items;
        },
        addItem(item) {
            const index = this.cartItems.findIndex(cartItem =>
                Number(cartItem.idSanPhamChiTiet) === Number(item.idSanPhamChiTiet)
            );
            if (index !== -1) {
                this.cartItems[index].soLuong = Number(this.cartItems[index].soLuong) + Number(item.soLuong);
                return this.cartItems[index];
            } else {
                this.cartItems.push(item);
                return item;
            }
        },
        updateItemQuantity(itemId, newQuantity) {
            const index = this.cartItems.findIndex(item => item.id === itemId);
            if (index !== -1) {
                this.cartItems[index].soLuong = newQuantity;
            }
        },
        updateItemWithBackendData(oldIdSanPhamChiTiet, newItemData) {
            const index = this.cartItems.findIndex(item =>
                item.idSanPhamChiTiet === oldIdSanPhamChiTiet && !item.id
            );
            if (index !== -1) {
                this.cartItems[index] = newItemData;
            }
        },
        removeItem(itemId) {
            this.cartItems = this.cartItems.filter(i => i.id !== itemId);
        },
        // Action để lưu sản phẩm mua ngay
        setBuyNowProduct(product) {
            this.buyNowProduct = product;
        },
        // Action để xóa thông tin sản phẩm mua ngay (nếu cần)
        clearBuyNowProduct() {
            this.buyNowProduct = null;
        },
        // Action để lưu danh sách sản phẩm cần thanh toán
        setCheckoutProducts(products) {
            this.checkoutProducts = products;
        }
    },
    persist: true
});