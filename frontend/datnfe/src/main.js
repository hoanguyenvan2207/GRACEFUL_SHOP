// datnfe/src/main.js
if (typeof global === 'undefined') {
  window.global = window;
}
import { createApp } from 'vue';
import App from './App.vue';
import router from "./router/router.js";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";
import { createPinia } from 'pinia';
import i18n from './i18n/index.js';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import store from './router/store.js';
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.withCredentials = true

const currentLayout = router.currentRoute.value.meta.layout;

if (currentLayout === 'admin') {
  store.dispatch('fetchUserInfo')
    .catch(err => {
      // nếu không fetch được (chưa login), không sao
      console.log('Chưa có session hợp lệ hoặc lỗi:', err)
    })
    .finally(() => {
      const pinia = createPinia()
      const app = createApp(App);
      pinia.use(piniaPluginPersistedstate);
      app.use(pinia)
      app.use(router)
      app.use(Antd)
      app.use(i18n)
      app.use(store);
      app.mount('#app')
    })
} else {
  const pinia = createPinia()
  const app = createApp(App);
  pinia.use(piniaPluginPersistedstate);
  app.use(pinia)
  app.use(router)
  app.use(Antd)
  app.use(i18n)
  app.use(store);
  app.mount('#app')
}


