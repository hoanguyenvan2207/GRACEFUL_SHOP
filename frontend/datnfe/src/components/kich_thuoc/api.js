import axios from "axios";

const API_URL = "/api/kich-thuoc";

// Hàm xử lý lỗi chung
const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

export const getKichThuocsEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getKichThuocsAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getKichThuocs = async (keyword = "", page = 0, size = 5, trangThai = "") => {
    try {
        const params = { keyword, page, size };
        if (trangThai === "true" || trangThai === "false") {
            params.trangThai = trangThai === "true";
        }
        const response = await axios.get(`${API_URL}/page/all`, { params });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getKichThuocByName = async (ten) => {
    try {
        const response = await axios.get(`${API_URL}/by-ten/${ten}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
}

export const addKichThuoc = async (kichThuoc) => {
    try {
        const response = await axios.post(`${API_URL}/add`, kichThuoc, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const updateKichThuoc = async (id, kichThuoc) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, kichThuoc, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getKichThuocById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};
