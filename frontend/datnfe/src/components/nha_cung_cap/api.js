import axios from "axios";

const API_URL = "/api/nha-cung-cap";

const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

export const getNhaCungCapsEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        handleError(error);
    }
};

export const getNhaCungCapAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        handleError(error);
    }
}

export const getNhaCungCapByName = async (ten) => {
    try {
        const response = await axios.get(`${API_URL}/by-ten/${ten}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
}

export const getNhaCungCaps = async (keyword = "", page = 0, size = 5, trangThai = "") => {
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

export const addNhaCungCap = async (nhaCungCap) => {
    try {
        const response = await axios.post(`${API_URL}/add`, nhaCungCap, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const updateNhaCungCap = async (id, nhaCungCap) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, nhaCungCap, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getNhaCungCapById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};