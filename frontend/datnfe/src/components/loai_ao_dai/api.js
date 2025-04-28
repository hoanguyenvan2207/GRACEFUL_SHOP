import axios from "axios";

const API_URL = "/api/loai-san-pham";

const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

export const getLoaiAoDaisEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        handleError(error);
    }
};

export const getLoaiAoDaiAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getLoaiAoDaiByName = async (ten) => {
    try {
        const response = await axios.get(`${API_URL}/by-ten/${ten}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getLoaiAoDais = async (keyword = "", page = 0, size = 5, trangThai = "") => {
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

export const addLoaiAoDai = async (loaiAoDai) => {
    try {
        const response = await axios.post(`${API_URL}/add`, loaiAoDai, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const updateLoaiAoDai = async (id, loaiAoDai) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, loaiAoDai, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getLoaiAoDaiById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};