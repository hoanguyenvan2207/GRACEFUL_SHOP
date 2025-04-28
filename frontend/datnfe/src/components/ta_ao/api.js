import axios from "axios";

const API_URL = "/api/ta-ao";


const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

export const getTaAosEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        handleError(error);
    }
};


export const getTaAoAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getTaAos = async (keyword = "", page = 0, size = 5, trangThai = "") => {
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

export const getTaAoByName = async (ten) => {
    try {
        const response = await axios.get(`${API_URL}/by-ten/${ten}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const addTaAo = async (taAo) => {
    try {
        const response = await axios.post(`${API_URL}/add`, taAo, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const updateTaAo = async (id, taAo) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, taAo, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getTaAoById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};