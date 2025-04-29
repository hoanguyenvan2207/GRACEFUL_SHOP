import axios from "axios";

const API_URL = "/api/san-pham";

// Lấy danh sách sản phẩm đang hoạt động (list enabled)
export const getSanPhamsEn = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/enables`);
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

export const getSanPhamAll = async () => {
    try {
        const response = await axios.get(`${API_URL}/list/all`);
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

// Thêm sản phẩm mới
export const addSanPham = async (sanPham) => {
    try {
        const response = await axios.post(`${API_URL}/add`, sanPham, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

// Import sản phẩm mới
export const importSanPhams = async (productList) => {
    try {
        const response = await axios.post(`${API_URL}/addSanPhams`, productList, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

// Cập nhật sản phẩm theo ID
export const updateSanPham = async (id, sanPham) => {
    try {
        const response = await axios.put(`${API_URL}/update/${id}`, sanPham, {
            headers: {
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

// Lấy chi tiết sản phẩm theo ID
export const getSanPhamById = async (id) => {
    try {
        const response = await axios.get(`${API_URL}/detail/${id}`);
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

export const getSanPhamByMa = async (ma) => {
    try {
        const response = await axios.get(`${API_URL}/by-ma/${ma}`);
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

export const getTopSell = async () => {
    try {
        const response = await axios.get(`${API_URL}/top-sell`);
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};


// Filter
export const filterSanPhams = async (params) => {
    try {
        const response = await instance.get(`${API_URL}/list/search-and-filter`, {
            params: params,
        });
        return response.data;
    } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        throw error;
    }
};

const instance = axios.create({
    paramsSerializer: {
        indexes: null
    }
});

export const filterProducts = async (params) => {
    try {
        const response = await instance.get(`${API_URL}/list/filter-products`, { params });
        return response.data;
    } catch (error) {
        console.error('Error fetching filter products:', error);
        throw error;
    }
}

const cloudinaryAxios = axios.create({
    baseURL: 'https://api.cloudinary.com/v1_1/defcm50t7',
    headers: {
        'Content-Type': 'multipart/form-data'
    }
});

export const uploadImages = async (file) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        formData.append('upload_preset', 'graceful');

        const response = await cloudinaryAxios.post('/image/upload', formData);
        return response.data.secure_url;
    } catch (error) {
        console.error('Lỗi upload ảnh:', error);
        throw error;
    }
};