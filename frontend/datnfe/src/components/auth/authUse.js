import { ref } from "vue";
import AuthService from "../../services/AuthService";

export const isLoggedIn = ref(false);
export const userEmail = ref("");


export const checkAuthStatus = async () => {
  try {
    const user = await AuthService.initialize();
    if (user) {
      isLoggedIn.value = true;
      userEmail.value = user.email || "";
      return true;
    } else {
      isLoggedIn.value = false;
      userEmail.value = "";
      return false;
    }
  } catch (error) {
    console.error("Lỗi kiểm tra trạng thái đăng nhập:", error);
    isLoggedIn.value = false;
    userEmail.value = "";
    return false;
  }
};