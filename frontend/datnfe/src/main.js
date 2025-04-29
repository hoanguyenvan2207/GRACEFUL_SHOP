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
import i18n from './i18n';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import store from './router/store.js';


const currentLayout = router.currentRoute.value.meta.layout;

if (currentLayout === 'admin') {
  store.dispatch('fetchUserInfo')
    .catch(err => {
      console.log('Chưa có session hợp lệ hoặc lỗi:', err)
    })
    .finally(() => {
      const pinia = createPinia()
      pinia.use(piniaPluginPersistedstate);

      const app = createApp(App);
      app.use(pinia)
      app.use(store);
      app.use(i18n)
      app.use(router)
      app.use(Antd)
      app.mount('#app')
    })
} else {
  const pinia = createPinia()
  pinia.use(piniaPluginPersistedstate);

  const app = createApp(App);
  app.use(pinia)
  app.use(store);
  app.use(i18n)
  app.use(router)
  app.use(Antd)
  app.mount('#app')
}


