<template>
    <h2 class="text-center mt-3">DANH SÁCH BIẾN THỂ</h2>
    <a-layout class="container mt-2 mb-5 rounded">
        <!-- Tìm kiếm và nút thêm -->
        <div class="d-flex justify-content-between mt-4 mb-3">
            <a-input-search v-model:value="keyword" loading enter-button allowClear
                placeholder="Tìm kiếm sản phẩm chi tiết" @input="onKeywordChange" style="width: 30%" />
            <div class="d-flex align-items-center">
                <a-button v-if="isAdmin" type="primary" ghost
                    style="background-color: green; color: white; border-color: #28a745;" @click="showImportModal">
                    <i class="bi bi-file-earmark-arrow-down"> Import Excel</i>
                </a-button>

                <!-- Modal Import sản phẩm -->
                <a-modal v-model:open="isImportModalVisible" title="Import sản phẩm" :footer="null"
                    style="width: 400px;">
                    <p>
                        Tải template:
                        <a style="font-style: italic;"
                            href="https://docs.google.com/spreadsheets/d/1pd3JllLJ1w-B3tsFPwBCRowEv9rBqBfpy5t2Jqo9qD8/export?format=xlsx"
                            download="template_import_bien_the_san_pham.xlsx">
                            Click để tải template <i class="bi bi-download"></i>
                        </a>
                    </p>
                    <p>Chọn file Excel để import sản phẩm:</p>
                    <a-button type="primary" @click="triggerFileInput">
                        Chọn file Excel
                    </a-button>
                    <input type="file" ref="fileInput" @change="handleFileUpload" accept=".xlsx, .xls" hidden />
                    <div style="text-align: right; margin-top: 10px;">
                        <a-button @click="isImportModalVisible = false">Đóng</a-button>
                    </div>
                </a-modal>

                <!-- Nút Export Excel -->
                <a-button type="primary" ghost style="background-color: green; color: white; border-color: #28a745;"
                    @click="showExportModal" class="mx-2">
                    <i class="bi bi-file-earmark-arrow-up"> Export Excel</i>
                </a-button>

                <!-- Modal chọn option Export Excel -->
                <a-modal v-model:open="isExportModalVisible" title="Chọn tùy chọn xuất Excel" @ok="handleExportOk"
                    @cancel="handleExportCancel" okText="Xác nhận" cancelText="Hủy bỏ" style="width: 400px;">
                    <a-radio-group v-model:value="exportOption" class="d-flex flex-column">
                        <a-radio value="selected">Xuất các biến thể được chọn</a-radio>
                        <a-radio value="all">Xuất tất cả biến thể</a-radio>
                        <a-radio value="en">Xuất tất cả biến thể đang kinh doanh</a-radio>
                        <a-radio value="dis">Xuất tất cả biến thể ngừng kinh doanh</a-radio>
                    </a-radio-group>
                </a-modal>

                <!-- Download QR -->
                <a-button type="primary" @click="showQRModal" class="mx-2">
                    <i class="bi bi-qr-code-scan"> Download QR</i>
                </a-button>

                <!-- Modal Export QR -->
                <a-modal v-model:open="isQRModalVisible" title="Xuất QR Code" @ok="handleQROk" @cancel="handleQRCancel"
                    okText="Xác nhận" cancelText="Hủy" style="width: 400px;">
                    <a-radio-group v-model:value="qrExportOption" class="d-flex flex-column">
                        <a-radio value="selected">Các biến thể được chọn</a-radio>
                        <a-radio value="all">Tất cả biến thể</a-radio>
                        <a-radio value="en">Biến thể đang kinh doanh</a-radio>
                        <a-radio value="dis">Biến thể ngừng kinh doanh</a-radio>
                    </a-radio-group>
                    <template #footer>
                        <a-button key="back" @click="handleQRCancel">Hủy</a-button>
                        <a-button key="submit" type="primary" :loading="qrLoading" @click="handleQROk">Xác
                            nhận</a-button>
                    </template>
                </a-modal>

                <a-button v-if="isAdmin" type="primary" @click="showFormAdd" class="mr-2">
                    <i class="bi bi-plus-lg"> Thêm biến thể</i>
                </a-button>
            </div>
        </div>

        <!-- Lọc -->
        <FilterSanPhamChiTiet @update-filter="handleUpdateFilter" />

        <!-- Bảng sản phẩm chi tiết -->
        <a-table :dataSource="sanPhamChiTiets" :columns="columns" :pagination="false" bordered
            :row-selection="rowSelection" rowKey="id" :custom-row="customRow">
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'maKhuyenMai'">
                    <a-tag :color="record.maKhuyenMai
                        ? (isPromotionActive(record.ngayBatDau, record.ngayKetThuc, record.trangThaiKhuyenMai) ? 'green' : 'gold')
                        : 'red'">
                        {{ record.maKhuyenMai || 'Không có' }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'aoDai'">
                    <div class="ao-dai-cell">
                        <a-tooltip :title="`${record.maAoDai} - ${record.tenAoDai}`">
                            <div class="truncate-text">
                                {{ record.maAoDai }} - {{ record.tenAoDai }}
                            </div>
                        </a-tooltip>
                    </div>
                </template>
                <template v-else-if="column.key === 'trangThai'">
                    <a-tag :color="record.trangThai ? 'green' : 'red'">
                        {{ record.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh" }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'anhUrl'">
                    <a-image :width="50" :src="record.anhUrl" />
                </template>
                <template v-else-if="column.key === 'ngayTao'">
                    {{ formatDateTime(record.ngayTao) }}
                </template>
                <template v-else-if="column.key === 'giaGoc' || column.key === 'giaBan'">
                    {{ formatCurrency(record[column.key]) }}
                </template>
                <template v-else-if="column.key === 'actions'">
                    <a-button type="link" @click="showFormUpdate(record.id)" class="text-warning border edit-button">
                        <i class="bi bi-pencil-square"></i>
                    </a-button>
                </template>
                <template v-else>
                    <a-tooltip :title="record[column.key]" placement="topLeft">
                        <span>{{ record[column.key] }}</span>
                    </a-tooltip>
                </template>
            </template>
        </a-table>

        <!-- Phân trang -->
        <div class="d-flex align-items-center mt-4 gap-3 mb-3" v-if="totalElements > 0">
            <div class="d-flex justify-content-center flex-grow-1 gap-4">
                <a-pagination class="text-align-center" v-model:current="page" :total="totalPages * size"
                    :pageSize="size" @change="fetchSanPhamChiTiets" show-less-items :showSizeChanger="false" />

                <a-select v-model:value="size" @change="handlePageSizeChange" style="width: 120px">
                    <a-select-option :value="5">5 / page</a-select-option>
                    <a-select-option :value="10">10 / page</a-select-option>
                    <a-select-option :value="25">25 / page</a-select-option>
                    <a-select-option :value="50">50 / page</a-select-option>
                    <a-select-option :value="100">100 / page</a-select-option>
                </a-select>
            </div>
            <div class="text-end mx-3" style="width: auto;">
                <i class="bi bi-list"> Total: {{ totalElements }}</i>
            </div>
        </div>

        <!-- Drawer chi tiết -->
        <a-drawer v-model:open="visible" @close="onClose" title="THÔNG TIN CHI TIẾT BIẾN THỂ" width="50%"
            :footer="null">
            <a-card>
                <ul class="product-details row">
                    <div class="col-7">
                        <li><strong>Mã SPCT:</strong> <span>{{ selectedItem.maAoDaiChiTiet }}</span></li>
                        <li><strong>Áo dài:</strong> <span>{{ selectedItem.tenAoDai }} ({{ selectedItem.maAoDai
                                }})</span>
                        </li>
                        <li><strong>Màu sắc:</strong> <span>{{ selectedItem.tenMauSac }}</span></li>
                        <li><strong>Kích thước:</strong> <span>{{ selectedItem.tenKichThuoc }}</span></li>
                        <li><strong>Giá bán:</strong> <span>{{ formatCurrency(selectedItem.giaGoc) }}</span></li>
                        <li><strong>Giá khuyến mãi:</strong> <span>{{ formatCurrency(selectedItem.giaBan) }}</span></li>
                        <li>
                            <strong>Khuyến mãi:</strong>
                            <span>
                                <a-tag
                                    :color="selectedItem.maKhuyenMai ? (isPromotionActive(selectedItem.ngayBatDau, selectedItem.ngayKetThuc, selectedItem.trangThaiKhuyenMai) ? 'green' : 'red') : 'default'">
                                    {{
                                        selectedItem.maKhuyenMai
                                            ? selectedItem.maKhuyenMai + " - " + selectedItem.tenKhuyenMai + " - " +
                                            (isPromotionActive(selectedItem.ngayBatDau, selectedItem.ngayKetThuc,
                                                selectedItem.trangThaiKhuyenMai) ? "Có hiệu lực" : "Hết hiệu lực")
                                            : "Không có"
                                    }}
                                </a-tag>
                            </span>
                        </li>
                        <li
                            v-if="selectedItem.maKhuyenMai && isPromotionActive(selectedItem.ngayBatDau, selectedItem.ngayKetThuc, selectedItem.trangThaiKhuyenMai)">
                            <strong>Số tiền giảm:</strong>
                            <span>
                                {{ selectedItem.soTienGiam ? formatCurrency(-selectedItem.soTienGiam) :
                                    (selectedItem.phanTramGiam ? selectedItem.phanTramGiam + '%' : '') }}
                            </span>
                        </li>
                        <li><strong>Số lượng:</strong> <span>{{ selectedItem.soLuong }}</span></li>
                        <li><strong>Trạng thái:</strong>
                            <span>
                                <a-tag :color="selectedItem.trangThai ? 'green' : 'red'">
                                    {{ selectedItem.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh" }}
                                </a-tag>
                            </span>
                        </li>
                        <li><strong>Ngày tạo:</strong> <span>{{ formatDateTime(selectedItem.ngayTao) }}</span></li>
                    </div>
                    <li class="col-5 flex-column">
                        <strong>Ảnh sản phẩm:</strong>
                        <a-image :src="selectedItem.anhUrl || 'https://placehold.jp/800x1200.png'"
                            style="border-radius: 4px;" />
                    </li>
                </ul>
            </a-card>
        </a-drawer>
    </a-layout>
    <div> </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, h } from "vue";
import { importSanPhamChiTiets, getAllSanPhamChiTiets, filterSanPhamChiTiets } from "./api";
import { getSanPhamByMa, getSanPhamById } from "../san_pham/api";
import { getKichThuocByName, getKichThuocById } from "../kich_thuoc/api";
import { getMauSacByName, getMauSacById } from "../mau_sac/api";
import { useRouter, useRoute } from "vue-router";
import * as XLSX from "xlsx";
import { Modal, notification } from "ant-design-vue";
import { utils, writeFile } from "xlsx";
import QRCode from 'qrcode';
import FileSaver from 'file-saver';
import JSZip from 'jszip';
import FilterSanPhamChiTiet from "./FilterSanPhamChiTiet.vue";
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
    return store.state.roles.includes('ROLE_QUANLY');
});
// Khai báo các biến cần thiết
const router = useRouter();
const route = useRoute();
const totalElements = ref(0);
const sanPhamChiTiets = ref([]);
const totalPages = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const visible = ref(false);
const selectedItem = ref({});
const isQRModalVisible = ref(false);
const qrExportOption = ref('selected');
const qrLoading = ref(false);
const isExportModalVisible = ref(false);
const exportOption = ref('selected');
const selectedRowKeys = ref([]);
const { saveAs } = FileSaver;
const currentFilters = ref({});

const handleUpdateFilter = (filters) => {
    currentFilters.value = filters;
    console.log(currentFilters.value);

    page.value = 1;
    fetchSanPhamChiTiets();
};

const rowSelection = computed(() => ({
    selectedRowKeys: selectedRowKeys.value,
    onChange: (keys) => {
        selectedRowKeys.value = keys;
    },
}));

const isImportModalVisible = ref(false);
const fileInput = ref(null);

const showImportModal = () => {
    isImportModalVisible.value = true;
};

const triggerFileInput = () => {
    fileInput.value.click();
};

const isPromotionActive = (startDate, endDate, promotionStatus) => {
    if (!promotionStatus) return false;
    if (!startDate || !endDate) return false;

    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());

    const s = new Date(startDate);
    const start = new Date(s.getFullYear(), s.getMonth(), s.getDate());

    const e = new Date(endDate);
    const end = new Date(e.getFullYear(), e.getMonth(), e.getDate());

    return today >= start && today <= end;
};

const exportQR = async () => {
    try {
        qrLoading.value = true;
        let spctList = [];

        // Lấy danh sách sản phẩm (giữ nguyên logic lọc)
        if (qrExportOption.value === 'selected') {
            if (selectedRowKeys.value.length === 0) {
                Modal.error({ title: 'Lỗi', content: 'Vui lòng chọn ít nhất một sản phẩm' });
                return;
            }
            spctList = sanPhamChiTiets.value.filter(item => selectedRowKeys.value.includes(item.id));
        } else {
            const response = await getAllSanPhamChiTiets();
            spctList = response;

            if (qrExportOption.value === 'en') {
                spctList = spctList.filter(item => item.trangThai);
            } else if (qrExportOption.value === 'dis') {
                spctList = spctList.filter(item => !item.trangThai);
            }
        }

        if (spctList.length === 0) {
            Modal.error({ title: 'Lỗi', content: 'Không có dữ liệu để xuất' });
            return;
        }

        const zip = new JSZip();

        for (const spct of spctList) {
            const canvas = document.createElement('canvas');
            await QRCode.toCanvas(canvas, spct.maAoDaiChiTiet, {
                width: 300,
                margin: 2,
                color: {
                    dark: '#000000',
                    light: '#ffffff'
                }
            });
            const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/png'));

            zip.file(`QR_${spct.maAoDaiChiTiet}.png`, blob);
        }

        Modal.confirm({
            title: 'Xác nhận',
            content: `Bạn có chắc chắn muốn xuất ${spctList.length} mã QR không?`,
            okText: 'Xác nhận',
            cancelText: 'Hủy bỏ',
            onOk: async () => {
                const content = await zip.generateAsync({ type: "blob" });

                saveAs(content, `QR_Codes_${new Date().toISOString().slice(0, 10)}.zip`);

                notification.success({
                    message: 'Xuất QR thành công',
                    description: `Đã tạo ${spctList.length} QR code`,
                });
            }
        })
    } catch (error) {
        console.error('Lỗi tạo QR:', error);
        Modal.error({ title: 'Lỗi', content: 'Xuất QR thất bại: ' + error.message });
    } finally {
        selectedRowKeys.value = [];
        qrLoading.value = false;
        isQRModalVisible.value = false;
    }
};

// Xử lý modal QR
const showQRModal = () => {
    isQRModalVisible.value = true;
};

const handleQROk = () => {
    exportQR();
};

const handleQRCancel = () => {
    isQRModalVisible.value = false;
};

const exportExcel = async (option) => {
    let spctToExport = [];

    try {
        if (option === 'selected') {
            if (selectedRowKeys.value.length === 0) {
                Modal.error({
                    title: 'Lỗi',
                    content: 'Vui lòng chọn ít nhất một sản phẩm chi tiết để xuất Excel',
                });
                return;
            }
            spctToExport = sanPhamChiTiets.value.filter(spct =>
                selectedRowKeys.value.includes(spct.id)
            );
        } else if (option === 'all') {
            spctToExport = await getAllSanPhamChiTiets();
        } else if (option === 'en') {
            const response = await getAllSanPhamChiTiets();
            spctToExport = response.filter(spct => spct.trangThai);
        } else if (option === 'dis') {
            const response = await getAllSanPhamChiTiets();
            spctToExport = response.filter(spct => !spct.trangThai);
        }

        if (spctToExport.length === 0) {
            Modal.error({ title: 'Lỗi', content: 'Không có dữ liệu để xuất' });
            return;
        }

        // Transform data cho Excel
        const data = spctToExport.map(spct => ({
            "Mã SPCT": spct.maAoDaiChiTiet,
            "Mã áo dài": spct.maAoDai,
            "Giá bán": spct.giaGoc,
            "Giá khuyến mãi": spct.giaBan,
            "Khuyến mãi": spct.maKhuyenMai ? spct.maKhuyenMai : "Không có",
            "Số lượng": spct.soLuong,
            "Màu sắc": spct.tenMauSac,
            "Kích thước": spct.tenKichThuoc,
            "Trạng thái": spct.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh",
            "Ngày tạo": formatDateTime(spct.ngayTao),
            "Ảnh URL": spct.anhUrl

        }));

        // Tạo worksheet
        const worksheet = utils.json_to_sheet(data);
        worksheet['!cols'] = [
            { wch: 15 },
            { wch: 10 },
            { wch: 12 },
            { wch: 12 },
            { wch: 15 },
            { wch: 10 },
            { wch: 12 },
            { wch: 15 },
            { wch: 20 },
            { wch: 30 },
            { wch: 100 }

        ];

        Modal.confirm({
            title: 'Xác nhận',
            content: `Bạn có chắc chắn muốn xuất ${spctToExport.length} sản phẩm chi tiết không?`,
            okText: 'Xác nhận',
            cancelText: 'Hủy bỏ',
            onOk() {
                const workbook = utils.book_new();
                utils.book_append_sheet(workbook, worksheet, 'Danh sách SPCT');
                writeFile(workbook, 'danh_sach_san_pham_chi_tiet.xlsx');

                notification.success({
                    message: 'Xuất Excel thành công',
                    description: `Đã xuất ${spctToExport.length} sản phẩm chi tiết`,
                });
                selectedRowKeys.value = [];
            }
        })
    } catch (error) {
        console.error('Lỗi xuất Excel:', error);
        Modal.error({
            title: 'Lỗi',
            content: 'Xuất file Excel thất bại!',
        });
    } finally {
        selectedRowKeys.value = [];
    }
};

// Xử lý modal Export
const showExportModal = () => {
    isExportModalVisible.value = true;
};

const handleExportOk = () => {
    exportExcel(exportOption.value);
    isExportModalVisible.value = false;
};

const handleExportCancel = () => {
    isExportModalVisible.value = false;
};


const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // Reset input file để cho phép chọn lại file nếu cần
    event.target.value = "";

    nextTick(() => {
        isImportModalVisible.value = false;
    });

    const reader = new FileReader();
    reader.onload = async (e) => {
        try {
            const data = new Uint8Array(e.target.result);
            const workbook = XLSX.read(data, { type: "array" });
            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];
            const jsonData = XLSX.utils.sheet_to_json(sheet);

            const requiredHeaders = [
                "Mã Áo dài",
                "Giá bán",
                "Số lượng",
                "Trạng thái",
                "Ảnh URL",
                "Tên màu sắc",
                "Tên kích thước"
            ];
            const actualHeaders = Object.keys(jsonData[0] || {});
            const missingHeaders = requiredHeaders.filter(header => !actualHeaders.includes(header));
            if (missingHeaders.length > 0) {
                Modal.error({
                    title: "Lỗi định dạng file",
                    content: `Template không hợp lệ. Vui lòng kiểm tra lại các header sau: ${missingHeaders.join(", ")}`,
                });
                return;
            }

            if (!jsonData || jsonData.length === 0) {
                Modal.error({ title: "Lỗi", content: "File Excel không có dữ liệu!" });
                return;
            }

            const errors = [];
            const duplicateCheck = new Set();
            const existingItems = new Set();

            try {
                const existingSPCTs = await getAllSanPhamChiTiets();
                existingSPCTs.forEach(item => {
                    // Tạo key để kiểm tra trùng lặp: aoDaiId_mauSacId_kichThuocId
                    existingItems.add(`${item.aoDaiId}_${item.mauSacId}_${item.kichThuocId}`);
                });
            } catch (error) {
                console.error("Không thể lấy dữ liệu sản phẩm hiện có:", error);
            }

            // Map từng dòng dữ liệu và chuyển đổi qua cấu trúc backend cần
            const spctDataPromises = jsonData.map(async (row, index) => {
                const rowIndex = index + 2; // Vì dòng đầu tiên là header
                const rowErrors = []; // Lỗi riêng cho mỗi dòng

                // Kiểm tra và chuẩn hóa dữ liệu
                let maAoDai = row["Mã Áo dài"] ? row["Mã Áo dài"].toString().trim() : "";
                let giaGoc = (row["Giá bán"] !== undefined && row["Giá bán"] !== null)
                    ? row["Giá bán"].toString().trim()
                    : "";
                let soLuong = (row["Số lượng"] !== undefined && row["Số lượng"] !== null)
                    ? row["Số lượng"].toString().trim()
                    : "";
                let trangThai = row["Trạng thái"] ? row["Trạng thái"].toString().trim() : "";
                let anhUrl = row["Ảnh URL"] ? row["Ảnh URL"].toString().trim() : "";
                let tenMauSac = row["Tên màu sắc"] ? row["Tên màu sắc"].toString().trim() : "";
                let tenKichThuoc = row["Tên kích thước"] ? row["Tên kích thước"].toString().trim() : "";

                // --- VALIDATE THIẾU DỮ LIỆU ---
                if (!maAoDai) rowErrors.push(`Mã áo dài không được để trống`);
                if (giaGoc === "") rowErrors.push(`Giá bán không được để trống`);
                if (soLuong === "") rowErrors.push(`Số lượng không được để trống`);
                if (!trangThai) rowErrors.push(`Trạng thái không được để trống`);
                if (!anhUrl) rowErrors.push(`Ảnh URL không được để trống`);
                if (!tenMauSac) rowErrors.push(`Tên màu sắc không được để trống`);
                if (!tenKichThuoc) rowErrors.push(`Tên kích thước không được để trống`);

                // Nếu có lỗi thiếu dữ liệu, thêm vào danh sách lỗi và bỏ qua các bước tiếp theo
                if (rowErrors.length > 0) {
                    errors.push(`Dòng ${rowIndex}: ${rowErrors.join("; ")}`);
                    return null;
                }

                // --- VALIDATE DỮ LIỆU HỢP LỆ ---
                // Validate số
                if (isNaN(giaGoc)) {
                    errors.push(`Dòng ${rowIndex}: Giá bán '${giaGoc}' phải là số`);
                    return null;
                }
                if (Number(giaGoc) <= 0) {
                    errors.push(`Dòng ${rowIndex}: Giá bán không được là số âm hoặc bằng 0`);
                    return null;
                }

                if (isNaN(soLuong)) {
                    errors.push(`Dòng ${rowIndex}: Số lượng '${soLuong}' phải là số`);
                    return null;
                }
                if (Number(soLuong) < 0) {
                    errors.push(`Dòng ${rowIndex}: Số lượng không được là số âm`);
                    return null;
                }

                // Validate trạng thái
                if (trangThai !== "Đang kinh doanh" && trangThai !== "Ngừng kinh doanh") {
                    errors.push(`Dòng ${rowIndex}: Trạng thái '${trangThai}' không hợp lệ. Chỉ chấp nhận 'Đang kinh doanh' hoặc 'Ngừng kinh doanh'`);
                    return null;
                }

                // --- VALIDATE URL ẢNH ---

                if (!/^https?:\/\/.+\.(jpg|jpeg|png|gif)$/.test(anhUrl)) {
                    errors.push(`Dòng ${rowIndex}: Link ảnh không hợp lệ`);
                    return null;
                }

                // --- KIỂM TRA THAM CHIẾU TỒN TẠI ---
                // Kiểm tra tồn tại áo dài
                const sanPhamData = await getSanPhamByMa(maAoDai);
                if (!sanPhamData) {
                    errors.push(`Dòng ${rowIndex}: Không tìm thấy sản phẩm có mã: ${maAoDai}`);
                    return null;
                }

                // Kiểm tra tồn tại màu sắc
                const mauSacData = await getMauSacByName(tenMauSac);
                if (!mauSacData) {
                    errors.push(`Dòng ${rowIndex}: Không tìm thấy màu sắc với tên: ${tenMauSac}`);
                    return null;
                }

                // Kiểm tra tồn tại kích thước
                const kichThuocData = await getKichThuocByName(tenKichThuoc);
                if (!kichThuocData) {
                    errors.push(`Dòng ${rowIndex}: Không tìm thấy kích thước với tên: ${tenKichThuoc}`);
                    return null;
                }

                // --- KIỂM TRA TRÙNG LẶP TRONG FILE EXCEL ---
                const aoDaiId = sanPhamData.id;
                const mauSacId = mauSacData.id;
                const kichThuocId = kichThuocData.id;
                const uniqueKey = `${aoDaiId}_${mauSacId}_${kichThuocId}`;

                if (duplicateCheck.has(uniqueKey)) {
                    errors.push(`Dòng ${rowIndex}: Sản phẩm có mã '${maAoDai}' với màu '${tenMauSac}' và kích thước '${tenKichThuoc}' đã trùng lặp trong file`);
                    return null;
                }
                duplicateCheck.add(uniqueKey);

                // --- KIỂM TRA TRÙNG LẶP VỚI DỮ LIỆU HIỆN CÓ ---
                if (existingItems.has(uniqueKey)) {
                    errors.push(`Dòng ${rowIndex}: Sản phẩm có mã '${maAoDai}' với màu '${tenMauSac}' và kích thước '${tenKichThuoc}' đã tồn tại trong hệ thống`);
                    return null;
                }

                // --- CHUYỂN ĐỔI GIÁ TRỊ TRẠNG THÁI ---
                const trangThaiValue = trangThai === "Đang kinh doanh" ? true : false;

                // --- CHUYỂN ĐỔI DỮ LIỆU THEO CẤU TRÚC BACKEND ---
                return {
                    aoDaiId: aoDaiId,
                    giaGoc: Number(giaGoc),
                    soLuong: Number(soLuong),
                    trangThai: trangThaiValue,
                    anhUrl: anhUrl,
                    mauSacId: mauSacId,
                    kichThuocId: kichThuocId
                };
            });

            // Chờ tất cả các promise hoàn tất
            const spctListWithNulls = await Promise.all(spctDataPromises);
            // Lọc bỏ các giá trị null (dòng có lỗi)
            const spctList = spctListWithNulls.filter(item => item !== null);

            if (errors.length > 0) {
                Modal.error({
                    title: "Lỗi Validation",
                    content: h("div", { style: "white-space: pre-line; color: red; max-height: 500px; overflow-y: auto;" }, errors.join("\n"))
                });
                return;
            }

            if (spctList.length === 0) {
                Modal.error({
                    title: "Lỗi",
                    content: "Không có dữ liệu hợp lệ để import!"
                });
                return;
            }

            Modal.confirm({
                title: "Xác nhận",
                content: `Bạn có chắc chắn muốn import ${spctList.length} sản phẩm chi tiết không?`,
                okText: "Xác nhận",
                cancelText: "Hủy bỏ",
                onOk: async () => {
                    try {
                        const response = await importSanPhamChiTiets(spctList);
                        if (response) {
                            Modal.success({
                                title: "Import thành công",
                                content: `Đã import thành công ${spctList.length} sản phẩm chi tiết`
                            });
                            fetchSanPhamChiTiets();
                        }
                    } catch (error) {
                        console.error("Chi tiết lỗi:", error.response?.data);

                        // Xử lý lỗi từ server với thông báo chi tiết hơn
                        let errorContent = "Lỗi không xác định khi import";

                        if (error.response?.data) {
                            if (error.response.data.errors?.duplicate) {
                                errorContent = error.response.data.errors.duplicate;
                            } else if (error.response.data.message) {
                                errorContent = error.response.data.message;
                            } else if (typeof error.response.data === 'string') {
                                errorContent = error.response.data;
                            }
                        }

                        Modal.error({
                            title: "Lỗi Import",
                            content: errorContent
                        });
                    }
                }
            })
        } catch (error) {
            console.error("Lỗi import:", error);
            Modal.error({
                title: "Lỗi",
                content: "Import thất bại do lỗi hệ thống! Vui lòng kiểm tra định dạng file và thử lại."
            });
        }
    };
    reader.readAsArrayBuffer(file);
};

const columns = [
    { title: "#", dataIndex: "index", key: "index" },
    { title: "Mã SPCT", dataIndex: "maAoDaiChiTiet", key: "maAoDaiChiTiet" },
    {
        title: "Áo Dài",
        key: "aoDai",
        customRender: (record) => `${record.maAoDai} - ${record.tenAoDai}`,
    },
    { title: "Màu sắc", dataIndex: "tenMauSac", key: "tenMauSac" },
    { title: "Kích thước", dataIndex: "tenKichThuoc", key: "tenKichThuoc" },
    { title: "Giá bán", dataIndex: "giaGoc", key: "giaGoc" },
    { title: "Giá khuyến mãi", dataIndex: "giaBan", key: "giaBan" },
    { title: "Khuyến mãi", dataIndex: "maKhuyenMai", key: "maKhuyenMai" },
    { title: "Số lượng", dataIndex: "soLuong", key: "soLuong" },
    { title: "Trạng thái", key: "trangThai" },
    { title: "Actions", key: "actions" },
];

const fetchSanPhamChiTiets = async (pageNumber = 1) => {
    page.value = pageNumber;
    router.replace({
        path: route.path,
        query: {
            keyword: keyword.value,
            page: page.value,
            size: size.value,
        },
    });
    try {
        const params = {
            keyword: keyword.value,
            ...currentFilters.value,
            page: page.value - 1,
            size: size.value,
        };

        const response = await filterSanPhamChiTiets(params, page.value - 1, size.value);
        if (response?.content) {
            sanPhamChiTiets.value = response.content.map((item, index) => ({
                ...item,
                index: index + 1 + (page.value - 1) * size.value,
            }));
            totalPages.value = response.page.totalPages;

            totalElements.value = response.page.totalElements;
        }
    } catch (error) {
        console.error("Lỗi khi tải dữ liệu:", error);
    }
};

const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    const time = date.toLocaleTimeString('en-EN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
    });
    const day = date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
    });
    return `${time}, ngày ${day}`;
};

const formatCurrency = (value) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
    }).format(value);
};

const showDrawer = (record) => {
    selectedItem.value = record;
    visible.value = true;
};

const customRow = (record) => {
    return {
        onClick: async () => {
            try {
                selectedItem.value = record;
                visible.value = true;
            } catch (error) {
                console.error("Lỗi khi lấy thông tin sản phẩm:", error);
            }
        },
    };
};

const onClose = () => {
    visible.value = false;
};

const showFormAdd = () => {
    router.push("/san-pham-chi-tiet/add");
};

const showFormUpdate = (id) => {
    router.push(`/san-pham-chi-tiet/edit/${id}`);
};

const handlePageSizeChange = (newSize) => {
    size.value = newSize;
    fetchSanPhamChiTiets(1);
};

let debounceTimer = null;
const onKeywordChange = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchSanPhamChiTiets(1);
    }, 300);
};

onMounted(() => {
    fetchSanPhamChiTiets();
});
</script>


<style scoped>
:deep(.ant-table-tbody > tr > td) {
    cursor: pointer;
}

.product-details li {
    display: flex;
    margin-bottom: 10px;
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;
}

.product-details li strong {
    width: 120px;
    flex-shrink: 0;
}

.product-details li span {
    flex: 1;
    word-break: break-word;
}

.edit-button:hover {
    background-color: rgba(255, 193, 7, 0.2);
    border-color: #ffc107 !important;
}

.detail-button:hover {
    background-color: rgba(0, 173, 181, 0.2);
    border-color: #00adb5 !important;
}

.edit-button,
.detail-button {
    transition: background-color 0.3s ease, border-color 0.3s ease;
}

.edit-button i,
.detail-button i {
    transition: color 0.3s ease;
}

.edit-button:hover i {
    color: #ffc107;
}

.detail-button:hover i {
    color: #00adb5;
}
</style>