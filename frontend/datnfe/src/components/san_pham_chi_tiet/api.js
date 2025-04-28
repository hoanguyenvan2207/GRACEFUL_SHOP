import axios from "axios";

const API_URL = "/api/san-pham-chi-tiet";


const handleError = (error) => {
    console.error("Lỗi khi gọi API:", error);
    throw error;
};

// Lấy tất cả sản phẩm chi tiết
export const getAllSanPhamChiTiets = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

const instance = axios.create({
    baseURL: "/api", // Thêm dòng này
    paramsSerializer: {
        indexes: null
    }
});
// Filter sản phẩm chi tiết
export const filterSanPhamChiTiets = async (params) => {
    try {
        const response = await instance.get(`${API_URL}/page/filter`, {
            params: params,
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

// Lấy danh sách sản phẩm chi tiết theo ID sản phẩm 
export const getSanPhamChiTietBySanPhamId = async (aoDaiId) => {
    try {
        const response = await axios.get(`${API_URL}/list-by-san-pham?aoDaiId=${aoDaiId}`);

        return response.data;
    } catch (error) {
        return handleError(error);
    }
};


// Lấy danh sách sản phẩm đang hoạt động (page enabled)
// export const getSanPhamChiTietsEnPage = async (keyword = "", page = 0, size = 5) => {
//     try {
//         const response = await axios.get(`${API_URL}/page/enables`, {
//             params: {
//                 keyword,
//                 page,
//                 size,
//             },
//         });
//         return response.data;
//     } catch (error) {
//         return handleError(error);
//     }
// };

// Lấy danh sách sản phẩm đang hoạt động (page enabled)
// export const getSanPhamChiTietsDisPage = async (keyword = "", page = 0, size = 5) => {
//     try {
//         const response = await axios.get(`${API_URL}/page/disables`, {
//             params: {
//                 keyword,
//                 page,
//                 size,
//             },
//         });
//         return response.data;
//     } catch (error) {
//         return handleError(error);
//     }
// };

// Lấy danh sách sản phẩm theo trang và từ khóa tìm kiếm
// export const getSanPhamChiTiets = async (keyword = "", page = 0, size = 5) => {
//     try {
//         const response = await axios.get(`${API_URL}/page/all`, {
//             params: {
//                 keyword,
//                 page,
//                 size,
//             },
//         });
//         return response.data;
//     } catch (error) {
//         return handleError(error);
//     }
// };

// Thêm sản phẩm mới
export const addSanPhamChiTiet = async (sanPhamChiTiet) => {
    try {
        const response = await axios.post(`${API_URL}/add`, sanPhamChiTiet, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

export const importSanPhamChiTiets = async (sanPhamChiTiets) => {
    try {
        const response = await axios.post(`${API_URL}/adds`, sanPhamChiTiets, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

// Cập nhật sản phẩm theo ID
export const updateSanPhamChiTiet = async (id, sanPhamChiTiet) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, sanPhamChiTiet, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};

// Lấy chi tiết sản phẩm theo ID
export const getSanPhamChiTietById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        return handleError(error);
    }
};
