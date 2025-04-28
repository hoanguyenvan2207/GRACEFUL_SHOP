import axios from "axios";

const API_URL = "/api/chat-lieu";

// Hàm xử lý lỗi chung
const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

export const getChatLieusEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getChatLieuAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getChatLieus = async (keyword = "", page = 0, size = 5, trangThai = "") => {
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

export const getChatLieuByName = async (ten) => {
    try {
        const response = await axios.get(`${API_URL}/by-ten/${ten}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
}


export const addChatLieu = async (chatLieu) => {
    try {
        const response = await axios.post(`${API_URL}/add`, chatLieu, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const updateChatLieu = async (id, chatLieu) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, chatLieu, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const getChatLieuById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};
