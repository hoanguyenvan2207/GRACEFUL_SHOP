// AddressSevice.js
import axios from 'axios';

const API_URL = 'https://provinces.open-api.vn/api'; 

const axiosInstance = axios.create({
  baseURL: API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

class AddressService {
  async getProvinces() {
    try {
      const response = await axiosInstance.get('/');  
      return response.data;
    } catch (error) {
      console.error('Lỗi khi lấy danh sách tỉnh/thành:', error);
      if (error.code === 'ERR_BAD_REQUEST') {
        throw new Error('Không thể truy cập API. Vui lòng thử lại sau.');
      }
      throw error;
    }
  }

  async getDistrictsByProvince(provinceCode) {
    try {
      const response = await axiosInstance.get(`/p/${provinceCode}?depth=2`);
      return response.data.districts;
    } catch (error) {
      console.error('Lỗi khi lấy danh sách quận/huyện:', error);
      throw error;
    }
  }

  async getWardsByDistrict(districtCode) {
    try {
      const response = await axiosInstance.get(`/d/${districtCode}?depth=2`);
      return response.data.wards;
    } catch (error) {
      console.error('Lỗi khi lấy danh sách phường/xã:', error);
      throw error;
    }
  }
}

export default new AddressService();