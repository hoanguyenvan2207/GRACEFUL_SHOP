import axios from "axios";

const API_URL = "/api/auth";

class AuthService {
  currentUser = null;

  async dangKi(hoTen, email, soDienThoai, matKhau) {
    try {
      const response = await axios.post(
        `${API_URL}/dang-ki`, { hoTen, email, soDienThoai, matKhau }, { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      if (
        error.response && error.response.data && error.response.data.message
      ) {
        if (error.response.data.message === "Email đã tồn tại") {
          throw new Error("Email đã tồn tại");
        } else if (
          error.response.data.message === "Số điện thoại đã tồn tại"
        ) {
          throw new Error("Số điện thoại đã tồn tại");
        }
      }
      throw new Error("Đã xảy ra lỗi khi đăng ký");
    }
  }

  async dangNhap(email, soDienThoai, matKhau) {
    try {
      const payload = { matKhau };
      if (email) {
        payload.email = email;
      }
      if (soDienThoai) {
        payload.soDienThoai = soDienThoai;
      }
      const response = await axios.post(
        `${API_URL}/dang-nhap`,
        payload,
        { withCredentials: true }
      );
      this.currentUser = {
        id: response.data.id,
        email: response.data.email,
        hoTen: response.data.hoTen || ""
      };
      return response.data;
    } catch (error) {
      if (error.response) {
        if (error.response.data.error) {
          throw new Error(error.response.data.error);
        } else if (error.response.data.message) {
          throw new Error(error.response.data.message);
        } else if (error.response.data.errors) {
          const errors = {};
          Object.keys(error.response.data.errors).forEach((key) => {
            errors[key] = error.response.data.errors[key];
          });
          throw { errors };
        }
      }
      throw new Error("Đã xảy ra lỗi khi đăng nhập");
    }
  }


  async getCurrentUserFromSession() {
    try {
      const response = await axios.get(`${API_URL}/customer/hien-tai`, {
        withCredentials: true
      });
      if (response.data && response.data.id) {
        this.currentUser = {
          id: response.data.id,
          email: response.data.email,
          hoTen: response.data.hoTen || ""
        };
        return this.currentUser;
      }
      return null;
    } catch (error) {
      if (error.response && error.response.status === 401) {
        this.currentUser = null;
        return null;
      }
      console.error("Lỗi khi lấy thông tin người dùng từ session:", error);
      this.currentUser = null;
      return null;
    }
  }

  async initialize() {
    try {
      if (!this.currentUser) {
        return await this.getCurrentUserFromSession();
      }
      return this.currentUser;
    } catch (error) {
      console.error("Lỗi khởi tạo:", error);
      this.currentUser = null;
      return null;
    }
  }

  async getCustomerInfo() {
    if (!this.currentUser || !this.currentUser.id) {
      return null;
    }
    try {
      const response = await axios.get(
        `${API_URL}/customer/details/${this.currentUser.id}`,
        { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      console.error("Lỗi khi lấy thông tin khách hàng:", error);
      return null;
    }
  }

  async updateCustomer(id, customerData) {
    try {
      const response = await axios.put(
        `${API_URL}/customer/update/${id}`,
        customerData,
        { withCredentials: true }
      );
      this.currentUser = { ...this.currentUser, ...customerData };
      return response.data;
    } catch (error) {
      if (error.response) {
        const responseData = error.response.data;
        if (responseData.message) {
          throw new Error(responseData.message); // Ném lỗi với thông điệp từ server
        } else if (responseData.error) {
          throw new Error(responseData.error);
        } else if (responseData.errors) {
          const errors = {};
          Object.keys(responseData.errors).forEach((key) => {
            errors[key] = responseData.errors[key];
          });
          throw { errors };
        }
      }
      throw new Error("Đã xảy ra lỗi khi cập nhật thông tin khách hàng");
    }
  }

  async doiMatKhau(email, matKhau) {
    try {
      const response = await axios.post(
        `${API_URL}/doi-mat-khau`,
        { email, matKhau },
        { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      if (error.response) {
        if (error.response.data.error) {
          throw new Error(error.response.data.error);
        } else if (error.response.data.errors) {
          const errors = {};
          Object.keys(error.response.data.errors).forEach((key) => {
            errors[key] = error.response.data.errors[key];
          });
          throw { errors };
        }
      }
      throw new Error("Đã xảy ra lỗi khi đổi mật khẩu");
    }
  }

  async xacThucTokenResetMatKhau(token) {
    try {
      const response = await axios.get(
        `${API_URL}/xac-thuc-reset-token/${token}`,
        { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      throw new Error("Token không hợp lệ hoặc đã hết hạn");
    }
  }

  async quenMatKhau(email) {
    try {
      const response = await axios.post(
        `${API_URL}/customer/quen-mat-khau`,
        { email },
        { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      if (error.response) {
        if (error.response.data.error) {
          throw new Error(error.response.data.error);
        } else if (error.response.data.errors) {
          const errors = {};
          Object.keys(error.response.data.errors).forEach((key) => {
            errors[key] = error.response.data.errors[key];
          });
          throw { errors };
        }
      }
      throw new Error("Đã xảy ra lỗi khi gửi yêu cầu quên mật khẩu");
    }
  }

  async datLaiMatKhau(email, otp, matKhau) {
    try {
      const response = await axios.post(
        `${API_URL}/customer/dat-lai-mat-khau`,
        { email, otp, matKhau },
        { withCredentials: true }
      );
      return response.data;
    } catch (error) {
      if (error.response) {
        if (error.response.data.error) {
          throw new Error(error.response.data.error);
        } else if (error.response.data.errors) {
          const errors = {};
          Object.keys(error.response.data.errors).forEach((key) => {
            errors[key] = error.response.data.errors[key];
          });
          throw { errors };
        }
      }
      throw new Error("Đã xảy ra lỗi khi đặt lại mật khẩu");
    }
  }

  async logout() {
    try {
      const response = await axios.post(
        `${API_URL}/dang-xuat`,
        {},
        { withCredentials: true }
      );
      this.currentUser = null;
      return response.data;
    } catch (error) {
      console.error("Lỗi khi đăng xuất:", error);
    }
  }

  isAuthenticated() {
    return !!this.currentUser;
  }

  getCurrentUser() {
    return this.currentUser;
  }
}

export default new AuthService();
