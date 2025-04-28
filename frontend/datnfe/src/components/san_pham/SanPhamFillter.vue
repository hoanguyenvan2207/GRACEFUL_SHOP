<template>
    <div class="col-12 row m-0">
        <!-- Dropdown Loại Sản Phẩm -->
        <div class="col-6 row m-0 p-0">
            <a-form-item class="col-1">
                Lọc<i class="bi bi-funnel"></i>
            </a-form-item>

            <a-form-item class="col-4">
                <a-select style="width: 100%" placeholder="Chọn loại áo đài" v-model:value="filters.tenLoaiAoDai"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in loaiSanPhamOptions" :key="item.id" :value="item.tenLoaiAoDai">
                        {{ item.tenLoaiAoDai }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <!-- Dropdown Nhà Cung Cấp -->
            <a-form-item class="col-4">
                <a-select style="width: 100%;" placeholder="Chọn nhà cung cấp" v-model:value="filters.tenNhaCungCap"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in nhaCungCapOptions" :key="item.id" :value="item.tenNhaCungCap">
                        {{ item.tenNhaCungCap }}
                    </a-select-option>
                </a-select>
            </a-form-item>
            <!-- Dropdown Tà Áo -->
            <a-form-item class="col-3">
                <a-select style="width: 100%;" placeholder="Chọn tà áo" v-model:value="filters.tenTaAo"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in taAoOptions" :key="item.id" :value="item.tenTaAo">
                        {{ item.tenTaAo }}
                    </a-select-option>
                </a-select>
            </a-form-item>
        </div>

        <div class="col-6 row p-0">

            <!-- Dropdown Chất Liệu -->
            <a-form-item class="col-4">
                <a-select style="width: 100%;" placeholder="Chọn chất liệu" v-model:value="filters.tenChatLieu"
                    @change="onFilterChange" allowClear mode="multiple" :max-tag-count="0">
                    <a-select-option v-for="item in chatLieuOptions" :key="item.id" :value="item.tenChatLieu">
                        {{ item.tenChatLieu }}
                    </a-select-option>
                </a-select>
            </a-form-item>

            <a-form-item class="col-4 ">
                <a-select style="width: 100%;" placeholder="Sắp xếp theo ngày" v-model:value="filters.sortDate"
                    @change="onFilterChange" allowClear>
                    <a-select-option value="ASC">Cũ nhất</a-select-option>
                    <a-select-option value="DESC">Mới nhất</a-select-option>
                </a-select>
            </a-form-item>

            <!-- Dropdown Trạng Thái -->
            <a-form-item class="col-3">
                <a-select style="width: 100%;" placeholder="Chọn trạng thái" v-model:value="filters.trangThai"
                    @change="onFilterChange" allowClear>
                    <a-select-option value="true">Đang kinh doanh</a-select-option>
                    <a-select-option value="false">Ngừng kinh doanh</a-select-option>
                </a-select>
            </a-form-item>

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
import { getChatLieuAll } from "../chat_lieu/api";
import { getLoaiAoDaiAll } from "../loai_ao_dai/api";
import { getNhaCungCapAll } from "../nha_cung_cap/api";
import { getTaAoAll } from "../ta_ao/api";

const emit = defineEmits(["update-filter"]);

const filters = ref({
    tenLoaiAoDai: [],
    tenNhaCungCap: [],
    tenTaAo: [],
    tenChatLieu: [],
    trangThai: "true",
    sortDate: "DESC",
});

const loaiSanPhamOptions = ref([]);
const nhaCungCapOptions = ref([]);
const taAoOptions = ref([]);
const chatLieuOptions = ref([]);

const fetchOptions = async () => {
    try {
        const [loaiData, nhaData, taData, chatLieuData] = await Promise.all([
            getLoaiAoDaiAll(),
            getNhaCungCapAll(),
            getTaAoAll(),
            getChatLieuAll(),
        ]);

        loaiSanPhamOptions.value = loaiData;
        nhaCungCapOptions.value = nhaData;
        taAoOptions.value = taData;
        chatLieuOptions.value = chatLieuData;
    } catch (error) {
        console.error("Lỗi khi lấy dữ liệu option:", error);
    }
};

const onFilterChange = () => {
    emit("update-filter", {
        ...filters.value
    });

};

const onRefresh = () => {
    filters.value = {
        tenLoaiAoDai: [],
        tenNhaCungCap: [],
        tenTaAo: [],
        tenChatLieu: [],
        trangThai: null,
        sortDate: null,
    };
    emit("update-filter", { ...filters.value });

};

onMounted(() => {
    fetchOptions();
    emit("update-filter", { ...filters.value });
});
</script>
