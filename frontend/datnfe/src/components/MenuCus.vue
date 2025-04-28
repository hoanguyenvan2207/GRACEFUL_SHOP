<script setup lang="ts">
import CartIcon from './san_pham_gio_hang/CartIcon.vue';
import CartOffcanvas from './san_pham_gio_hang/CartOffcanvas.vue';
import { ref, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useI18n } from 'vue-i18n';
import enFlag from '/assets/flag-en.png';
import viFlag from '/assets/flag-vi.png';
import AuthService from "../services/AuthService";
import { isLoggedIn, userEmail } from "./auth/authUse";

const currentLanguage = ref<'vi' | 'en'>(localStorage.getItem('language') as 'vi' | 'en' || 'vi');

const { t, locale } = useI18n();
locale.value = currentLanguage.value;

const switchLanguage = (lang: 'en' | 'vi') => {
  currentLanguage.value = lang;
  locale.value = lang;
  localStorage.setItem('language', lang);
};

const router = useRouter();
const route = useRoute();
const isLoading = ref(false);

const getUserInfo = async () => {
  const currentUser = await AuthService.initialize();
  console.log("Thông tin khách hàng:", currentUser);
  if (currentUser && currentUser.email) {
    userEmail.value = currentUser.email;
    // Lưu id khách hàng vào sessionStorage
    sessionStorage.setItem('user', currentUser.id);
    isLoggedIn.value = true;
  } else {
    userEmail.value = "";
    sessionStorage.removeItem('userId');
    isLoggedIn.value = false;
  }
};


const goToThongTin = () => {
  router.push("/thong-tin");
};

const goToLogin = () => {
  router.push({
    path: '/dang-nhap',
    query: { redirect: route.fullPath }
  });
};

const logout = async () => {
  isLoading.value = true;
  setTimeout(async () => {
    try {

      await AuthService.logout()
      userEmail.value = ''
      isLoggedIn.value = false
      sessionStorage.clear()

      const sensitive = ['/danh-sach-don-hang', '/thanh-toan', '/thong-tin']
      if (sensitive.includes(route.path)) {
        router.push('/')
      } else {
        router.go(0)
      }
    } catch (e) {
      console.error('Logout error', e)
    } finally {
      isLoading.value = false
    }
  }, 1000)
};


onMounted(() => {
  getUserInfo();
});

watch(isLoggedIn, (newValue) => {
  console.log("Trạng thái đăng nhập trong MenuCus:", newValue);
});
</script>

<template>
  <header class="border-bottom fixed-top bg-white" style="padding: 0 7%;">
    <div v-if="isLoading" class="full-screen-spin">
      <div class="loader"></div>
    </div>
    <div class="row align-items-center py-3">
      <div class="col-4">
        <nav class="d-flex">
          <router-link to="/" class="text-dark me-3 text-decoration-none" :class="{ active: route.path === '/' }" exact>
            {{ $t('header.home') }}
          </router-link>

          <router-link to="/danh-sach-san-pham" class="text-dark me-3 text-decoration-none"
            :class="{ active: route.path.startsWith('/danh-sach-san-pham') }">
            {{ $t('header.products') }}
          </router-link>
        </nav>

      </div>
      <div class="col-4 text-center">
        <a href="http://localhost:5173/">
          <img style="width: 160px; height: auto;"
            src="https://res.cloudinary.com/defcm50t7/image/upload/v1740129073/logo_graceful_lpd32u.png" alt="Logo" />
        </a>
      </div>
      <div class="col-4 d-flex justify-content-end align-items-center">

        <div class="dropdown me-4">
          <router-link to="/danh-sach-don-hang" class="text-dark text-decoration-none">
            <i class="fa-solid fa-list-check dsdh" style="font-size: 20px"></i>
          </router-link>
        </div>

        <div>
          <CartIcon />
          <CartOffcanvas />
        </div>

        <div class="dropdown ms-4">
          <a href="#" class="text-dark text-decoration-none" data-bs-toggle="dropdown">
            <i class="fas fa-user dsdh" style="font-size: 20px"></i>
          </a>
          <ul class="dropdown-menu">
            <template v-if="isLoggedIn">
              <li>
                <a class="dropdown-item" href="#" @click.prevent="goToThongTin">
                  <i class="fas fa-user-circle"></i> {{ t('header.viewInfo') }}
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#" @click.prevent="logout">
                  <i class="fas fa-sign-out-alt"></i> {{ t('header.logout') }}
                </a>
              </li>
            </template>
            <li v-else>
              <a class="dropdown-item" href="#" @click.prevent="goToLogin">
                <i class="fas fa-sign-in-alt"></i> {{ t('header.login') }}
              </a>
            </li>
          </ul>
        </div>

        <!-- Dropdown ngôn ngữ -->
        <div class="dropdown ms-3">
          <button class="btn btn-light dropdown-toggle bg-white" style="width: 130px; border: none;" type="button"
            data-bs-toggle="dropdown" aria-expanded="false">
            <img :src="currentLanguage === 'en' ? enFlag : viFlag" alt="Language"
              style="width: 20px; height: auto; margin-right: 5px;" />
            <span>{{ currentLanguage === 'en' ? 'English' : 'Tiếng Việt' }}</span>
          </button>
          <ul class="dropdown-menu rounded">
            <li>
              <a class="dropdown-item" href="#" @click.prevent="switchLanguage('en')">
                <img :src="enFlag" alt="English" style="width: 20px; height: auto; margin-right: 5px;" />
                {{ $t('language.en') }}
              </a>
            </li>
            <li>
              <a class="dropdown-item" href="#" @click.prevent="switchLanguage('vi')">
                <img :src="viFlag" alt="Tiếng Việt" style="width: 20px; height: auto; margin-right: 5px;" />
                {{ $t('language.vi') }}
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </header>
  <div class="bg-light py-2 container" style="margin-top: 97px;">
    <div class="container row text-center align-items-center">
      <div class="col border-end border-secondary">
        {{ $t('header.freeShipping') }}
      </div>
      <div class="col border-end border-secondary">
        {{ $t('header.warranty') }}
      </div>
      <div class="col">
        {{ $t('header.membership') }}
      </div>
    </div>
  </div>
</template>

<style scoped>
nav a {
  position: relative;
  text-decoration: none;
  font-weight: 500;
  transition: letter-spacing 0.3s ease;
}

nav a::after {
  content: "";
  position: absolute;
  left: 0;
  bottom: -2px;
  height: 3px;
  width: 0;
  background-color: black;
  transition: width 0.3s ease;
}

.dsdh {
  transition: all 0.3s ease;
}

.dsdh:hover {
  transform: scale(1.1);
}

nav a:hover::after {
  width: 100%;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
  color: #007bff;
}

nav a.active::after {
  width: 100%;
}

nav a.active::after {
  background-color: #000;
  height: 3px;
}

.dropdown-divider {
  margin: 0;
}

.full-screen-spin {
  position: fixed;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.65);
  z-index: 9999;
}

/* HTML: <div class="loader"></div> */
.loader {
  width: 50px;
  aspect-ratio: 1;
  display: grid;
  border: 4px solid #0000;
  border-radius: 50%;
  border-right-color: #000000;
  animation: l15 0.8s infinite linear;
}

.loader::before,
.loader::after {
  content: "";
  grid-area: 1/1;
  margin: 2px;
  border: inherit;
  border-radius: 50%;
  animation: l15 1.3s infinite;
}

.loader::after {
  margin: 8px;
  animation-duration: 2s;
}

@keyframes l15 {
  100% {
    transform: rotate(1turn)
  }
}
</style>
