<template>
    <div class="col-12 row">
        <div class="col-6 row m-0 p-0">
            <!-- Hàng 1: Lọc theo tên áo dài, màu sắc, kích thước -->
            <a-form-item class="col-1">
                Lọc <i class="bi bi-funnel"></i>
            </a-form-item>

            <!-- Dropdown chọn Tên Áo Dài -->
            <a-form-item class="col-7 m-0">
                <a-select style="width: 100%" placeholder="Chọn tên áo dài" v-model:value="filters.listTenAoDai"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in aoDaiOptions" :key="item.id" :value="item.tenAoDai">
                        {{ item.tenAoDai }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <!-- Dropdown chọn Màu Sắc -->
            <a-form-item class="col-4 m-0">
                <a-select style="width: 100%" placeholder="Chọn màu sắc" v-model:value="filters.listMauSac"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in mauSacOptions" :key="item.id" :value="item.tenMauSac">
                        {{ item.tenMauSac }}
                    </a-select-option>
                </a-select>
            </a-form-item>
        </div>

        <div class="col-6 row m-0 p-0">
            <!-- Dropdown chọn Kích Thước -->
            <a-form-item class="col-3 m-0">
                <a-select style="width: 100%" placeholder="Chọn kích thước" v-model:value="filters.listKichThuoc"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in kichThuocOptions" :key="item.id" :value="item.tenKichThuoc">
                        {{ item.tenKichThuoc }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item class="col-4">
                <a-select style="width: 100%;" placeholder="Sắp xếp" v-model:value="filters.sort"
                    @change="onFilterChange" allowClear>
                    <a-select-option value="ngayTao_ASC">Cũ nhất</a-select-option>
                    <a-select-option value="ngayTao_DESC">Mới nhất</a-select-option>
                    <a-select-option value="soLuong_ASC">Số lượng: Tăng dần</a-select-option>
                    <a-select-option value="soLuong_DESC">Số lượng: Giảm dần</a-select-option>
                    <a-select-option value="giaBan_ASC">Giá bán: Tăng dần</a-select-option>
                    <a-select-option value="giaBan_DESC">Giá bán: Giảm dần</a-select-option>
                </a-select>
            </a-form-item>

            <!-- Dropdown chọn Trạng Thái -->
            <a-form-item class="col-4">
                <a-select style="width: 100%;" placeholder="Chọn trạng thái" v-model:value="filters.trangThai"
                    @change="onFilterChange" allowClear>
                    <a-select-option value="true">Đang kinh doanh</a-select-option>
                    <a-select-option value="false">Ngừng kinh doanh</a-select-option>
                </a-select>
            </a-form-item>

            <!-- Nút Refresh -->
            <a-form-item class="col-1">
                <a-button type="primary" ghost @click="onRefresh" class="d-flex align-items-center gap-1"
                    style="font-size: 20px; font-weight: bold; border: 1px solid #d9d9d9; color: lightskyblue;">
                    <i class="bi bi-arrow-clockwise"></i>
                </a-button>
            </a-form-item>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
// Giả sử bạn có các hàm API sau để lấy dữ liệu option cho các dropdown
import { getSanPhamAll } from "../san_pham/api";
import { getMauSacsAll } from "../mau_sac/api";
import { getKichThuocsAll } from "../kich_thuoc/api";

const emit = defineEmits(["update-filter"]);

const filters = ref({
    listTenAoDai: [],
    listMauSac: [],
    listKichThuoc: [],
    trangThai: "true",
    sort: "ngayTao_DESC",
});

// Các options cho dropdown, thay đổi tên biến tùy theo API của bạn
const aoDaiOptions = ref([]);
const mauSacOptions = ref([]);
const kichThuocOptions = ref([]);

const fetchOptions = async () => {
    try {
        const [aoDaiData, mauSacData, kichThuocData] = await Promise.all([
            getSanPhamAll(),
            getMauSacsAll(),
            getKichThuocsAll(),
        ]);
        aoDaiOptions.value = aoDaiData;
        mauSacOptions.value = mauSacData;
        kichThuocOptions.value = kichThuocData;
    } catch (error) {
        console.error("Lỗi khi lấy dữ liệu option:", error);
    }
};

const onFilterChange = () => {
    emit("update-filter", { ...filters.value });
};

const onRefresh = () => {
    filters.value = {
        listTenAoDai: [],
        listMauSac: [],
        listKichThuoc: [],
        trangThai: null,
        sort: null,
    };
    emit("update-filter", { ...filters.value });
};

onMounted(() => {
    fetchOptions();
    emit("update-filter", { ...filters.value });
});
</script>
