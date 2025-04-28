import axios from "axios";
const getAllKhachHangAPI = "/api/admin/khach-hang/hien-thi";
const getAllDiaChisAPI = "/api/admin/dia-chi/hien-thi";
const getKhachHangByIdAPI = "/api/admin/khach-hang/details";
const updateKhachHangAPI = "/api/admin/khach-hang/update";
const addKhachHangAPI = "/api/admin/khach-hang/add";
class KhachHangService {
  getAllKhachHang(params) {
    return axios.get(getAllKhachHangAPI, {
      params: {
        keyword: params.keyword || "",
        page: params.page || 0,
        size: params.size || 5,
      },
    });
  }
  getAllDiaChi() {
    return axios.get(getAllDiaChisAPI);
  }

  getKhachHangById(id) {
    return axios.get(`${getKhachHangByIdAPI}/${id}`);
  }

  updateKhachHang(id, khachHang) {
    return axios.put(`${updateKhachHangAPI}/${id}`, khachHang);
  }

  addKhachHang(khachHang) {
    return axios.post(`${addKhachHangAPI}`, khachHang);
  }
}

export default new KhachHangService();
