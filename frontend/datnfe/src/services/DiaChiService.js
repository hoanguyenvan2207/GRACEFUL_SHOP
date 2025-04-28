import axios from "axios";

const BASE_URL = "/api/admin/dia-chi";

class DiaChiService {
  getAllDiaChi() {
    return axios.get(`${BASE_URL}/hien-thi`, { withCredentials: true });
  }

  getDiaChiById(id) {
    return axios.get(`${BASE_URL}/details/${id}`, { withCredentials: true });
  }

  addDiaChi(diaChi) {
    return axios.post(`${BASE_URL}/add`, diaChi, { withCredentials: true });
  }

  updateDiaChi(id, diaChi) {
    return axios.put(`${BASE_URL}/update/${id}`, diaChi, { withCredentials: true });
  }

  deleteDiaChi(id) {
    return axios.delete(`${BASE_URL}/delete/${id}`, { withCredentials: true });
  }

  setMacDinh(id) {
    return axios.put(`${BASE_URL}/mac-dinh/${id}`, {}, { withCredentials: true });
  }

  getDiaChiByKhachHangId(id) {
    return axios.get(`${BASE_URL}/khach-hang/${id}`, { withCredentials: true });
  }
}

export default new DiaChiService();
