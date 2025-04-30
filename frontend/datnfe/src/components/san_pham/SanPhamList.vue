<template>
  <h2 class="text-center mt-3">DANH SÁCH SẢN PHẨM</h2>
  <a-layout class="container mt-2 mb-5 rounded">
    <!-- Tìm kiếm và nút thêm sản phẩm -->
    <div class="d-flex justify-content-between mt-4 mb-3">
      <a-input-search v-model:value="keyword" loading enter-button placeholder="Tìm kiếm sản phẩm"
        @input="onKeywordChange" style="width: 30%" allowClear />
      <div class="d-flex align-items-center">
        <!-- Thêm nút nhập sản phẩm từ Excel -->
        <a-button v-if="isAdmin" type="primary" ghost
          style="background-color: green; color: white; border-color: #28a745;" @click="showImportModal">
          <i class="bi bi-file-earmark-arrow-down"> Import Excel</i>
        </a-button>

        <!-- Modal Import sản phẩm -->
        <a-modal v-model:open="isImportModalVisible" title="Import sản phẩm" :footer="null" style="width: 400px;">
          <p>
            Tải template:
            <a style="font-style: italic;"
              href="https://docs.google.com/spreadsheets/d/1ruWXIvRc1eWOoTqZnnR-4wWD71_IF4HTiBEn5ZXpj7M/export?format=xlsx"
              download="template_import_san_pham.xlsx">
              Click để tải template <i class="bi bi-download"></i>
            </a>
          </p>
          <p>Chọn file Excel để import sản phẩm:</p>
          <a-button type="primary" @click="triggerFileInput">
            Chọn file Excel
          </a-button>
          <!-- File input được giữ ẩn, dùng để xử lý upload -->
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
            <a-radio value="selected">Xuất các sản phẩm được chọn</a-radio>
            <a-radio value="all">Xuất tất cả sản phẩm</a-radio>
            <a-radio value="en">Xuất tất cả sản phẩm đang kinh doanh</a-radio>
            <a-radio value="dis">Xuất tất cả sản phẩm ngừng kinh doanh</a-radio>
          </a-radio-group>
        </a-modal>

        <a-button v-if="isAdmin" type="primary" @click="showFormAdd">
          <i class="bi bi-plus-lg"> Thêm sản phẩm</i>
        </a-button>
      </div>
    </div>

    <!-- Bộ lọc sản phẩm (sử dụng component ProductFilter) -->
    <ProductFilter @update-filter="handleFilter" />

    <!-- Bảng sản phẩm -->
    <a-table :dataSource="sanPhams" :columns="columns" :pagination="false" bordered :row-selection="rowSelection"
      rowKey="id" :custom-row="customRow">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'trangThai'">
          <a-tag :color="record.trangThai ? 'green' : 'red'">
            {{ record.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh" }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'ngayTao'">
          {{ formatDateTime(record.ngayTao) }}
        </template>
        <template v-else-if="column.key === 'actions'">
          <div class="d-flex justify-content-between">
            <a-button type="link" @click="showFormUpdate(record.id)" class="text-warning border edit-button">
              <i class="bi bi-pencil-square"></i>
            </a-button>
            <!-- <a-button type="link" @click="showDrawer(record)" class="text-info border detail-button">
              <i class="bi bi-info-square"></i>
            </a-button> -->
          </div>
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
        <a-pagination v-model:current="page" :total="totalPages * size" :pageSize="size" @change="fetchSanPhams"
          show-less-items :showSizeChanger="false" />
        <a-select v-model:value="size" @change="handlePageSizeChange" class="page-size-selector" style="width: 120px">
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

    <!-- Drawer hiển thị thông tin chi tiết -->
    <a-drawer v-model:open="visible" @close="onClose" title=" THÔNG TIN CHI TIẾT ÁO DÀI" width="50%" :footer="null">
      <a-card>
        <ul class="product-details">
          <li><strong>Mã Áo Dài:</strong> <span>{{ selectedSanPham.maAoDai }}</span></li>
          <li><strong>Tên Áo Dài:</strong> <span>{{ selectedSanPham.tenAoDai }}</span></li>
          <li><strong>Loại Áo Dài:</strong> <span>{{ selectedSanPham.tenLoaiAoDai }}</span></li>
          <li><strong>Chất Liệu:</strong> <span>{{ selectedSanPham.tenChatLieu }}</span></li>
          <li><strong>Tà Áo:</strong> <span>{{ selectedSanPham.tenTaAo }}</span></li>
          <li><strong>Nhà Cung Cấp:</strong> <span>{{ selectedSanPham.tenNhaCungCap }}</span></li>
          <li>
            <strong>Link YouTube:</strong>
            <span class="youtube-link">
              <a :href="selectedSanPham.linkYoutube" target="_blank">
                {{ selectedSanPham.linkYoutube }}
              </a>
            </span>
          </li>
          <li><strong>Mô Tả:</strong> <span v-html="selectedSanPham.moTa" class="product-description"></span></li>
          <li>
            <strong>Trạng Thái:</strong>
            <span>
              <a-tag :color="selectedSanPham.trangThai ? 'green' : 'red'" style="width: auto;">
                {{ selectedSanPham.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh" }}
              </a-tag>
            </span>
          </li>
          <li><strong>Ngày Tạo:</strong> <span>{{ formatDateTime(selectedSanPham.ngayTao) }}</span></li>
          <li>
            <strong>Ảnh Sản Phẩm:</strong>
            <hr>
            <div class="image-list">
              <a-image v-for="(anh, index) in selectedSanPham.anhList" :key="index"
                :src="anh || 'https://placehold.jp/800x1200.png'" alt="Ảnh sản phẩm" />
            </div>
          </li>
          <li>
            <strong class="mb-3">Các Biến Thể:</strong>
            <div class="variants-container mb-2">
              <div v-for="(variant, index) in productVariants" :key="index" class="variant-item">
                <a-card hoverable style="width: 100% ; height: 100%" size="small">
                  <template #cover>
                    <img :alt="variant.tenAoDai" :src="variant.anhUrl || 'https://placehold.jp/800x1200.png'" />
                  </template>
                  <a-card-meta>
                    <template #title>
                      {{ variant.maAoDaiChiTiet }}
                    </template>
                    <template #description>
                      Màu {{ variant.tenMauSac }} -
                      Size {{ variant.tenKichThuoc }}
                    </template>
                  </a-card-meta>
                </a-card>
              </div>
              <div class="variant-item">
                <a :href="`/san-pham-chi-tiet/add`" style=" text-decoration: none">
                  <a-card hoverable style="width: 100% ; height: 100%" size="small">
                    <template #cover>
                      <img alt="https://placehold.co/800x1200?text=Hello+World"
                        src="https://placehold.co/800x1200?text=Thêm+biến+thể" />
                    </template>
                    <a-card-meta>
                      <template #description>
                        Thêm biến thể
                      </template>
                    </a-card-meta>
                  </a-card>
                </a>
              </div>
            </div>
          </li>
        </ul>
      </a-card>
    </a-drawer>
  </a-layout>
  <div> </div>
</template>

<script setup>
import { ref, onMounted, computed, h, nextTick } from "vue";
import { importSanPhams, filterSanPhams, getSanPhamAll } from "./api";
import { getChatLieuByName } from "../chat_lieu/api";
import { getTaAoByName } from "../ta_ao/api";
import { getLoaiAoDaiByName } from "../loai_ao_dai/api";
import { getNhaCungCapByName } from "../nha_cung_cap/api";
import { getSanPhamChiTietBySanPhamId } from "../san_pham_chi_tiet/api";
import { Modal, notification } from "ant-design-vue";
import { useRouter, useRoute } from "vue-router";
import { utils, writeFile } from "xlsx";
import * as XLSX from "xlsx";
import ProductFilter from "./SanPhamFillter.vue";
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
  return store.state.roles.includes('ROLE_QUANLY');
});

// Các biến reactive và đối tượng cần thiết
const sanPhams = ref([]);
const totalPages = ref(0);
const totalElements = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const visible = ref(false);
const selectedSanPham = ref({});
const productVariants = ref([]);
const currentFilters = ref({});

const isExportModalVisible = ref(false);
const exportOption = ref('selected');

const router = useRouter();
const route = useRoute();

const isImportModalVisible = ref(false);
const showImportModal = () => {
  isImportModalVisible.value = true;
};

// Hàm xử lý filter
const handleFilter = (filters) => {
  currentFilters.value = filters;
  page.value = 1;
  fetchSanPhams();
};

// Import sản phẩm từ Excel
const fileInput = ref(null);
const triggerFileInput = () => fileInput.value.click();

const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  event.target.value = "";

  nextTick(() => {
    isImportModalVisible.value = false;
  });

  const reader = new FileReader();
  reader.onload = async (e) => {
    const data = new Uint8Array(e.target.result);
    const workbook = XLSX.read(data, { type: "array" });
    const sheetName = workbook.SheetNames[0];
    const sheet = workbook.Sheets[sheetName];
    const jsonData = XLSX.utils.sheet_to_json(sheet);

    const requiredHeaders = [
      "Tên Áo Dài",
      "Loại Áo Dài",
      "Chất Liệu",
      "Nhà Cung Cấp",
      "Tà Áo",
      "Trạng Thái",
      "Link Youtube",
      "Mô Tả",
      "Hình Ảnh (URL)"
    ];

    const actualHeaders = Object.keys(jsonData[0] || {});

    // Kiểm tra xem các header yêu cầu có đầy đủ không
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
    const nameCount = {};
    jsonData.forEach((row, index) => {
      // Lấy tên áo dài và xử lý trim (nếu không có dữ liệu, dùng chuỗi rỗng)
      const tenAoDai = row["Tên Áo Dài"]
        ? row["Tên Áo Dài"].toString().replace(/\r?\n/g, " ").trim()
        : "";
      if (tenAoDai) {
        nameCount[tenAoDai] = (nameCount[tenAoDai] || 0) + 1;
      }
    });

    // Duyệt qua đối tượng đếm và thêm lỗi nếu có tên xuất hiện > 1 lần
    for (const name in nameCount) {
      if (nameCount[name] > 1) {
        errors.push(`Tên áo dài "${name}" xuất hiện ${nameCount[name]} lần trong file.\n Vui lòng kiểm tra lại.`);
      }
    }

    // Nếu có lỗi trùng lặp, báo lỗi và dừng xử lý
    if (errors.length > 0) {
      Modal.error({
        title: "Lỗi Validation",
        content: h(
          "div",
          { style: "white-space: pre-line; color: red;" },
          errors.join("\n")
        ),
      });
      return;
    }
    const sanPhamsImport = await Promise.all(
      jsonData.map(async (row, index) => {
        const rowIndex = index + 2;
        // Lấy dữ liệu theo tên từ file Excel
        const tenAoDai = row["Tên Áo Dài"]
          ? row["Tên Áo Dài"].toString().replace(/\r?\n/g, " ").trim()
          : "";
        const loaiAoDaiName = row["Loại Áo Dài"]
          ? (typeof row["Loại Áo Dài"] === 'string'
            ? row["Loại Áo Dài"].replace(/\r?\n/g, " ").trim()
            : row["Loại Áo Dài"].toString().replace(/\r?\n/g, " ").trim())
          : "";

        const chatLieuName = row["Chất Liệu"]
          ? (typeof row["Chất Liệu"] === 'string'
            ? row["Chất Liệu"].replace(/\r?\n/g, " ").trim()
            : row["Chất Liệu"].toString().replace(/\r?\n/g, " ").trim())
          : "";

        const nhaCungCapName = row["Nhà Cung Cấp"]
          ? (typeof row["Nhà Cung Cấp"] === 'string'
            ? row["Nhà Cung Cấp"].replace(/\r?\n/g, " ").trim()
            : row["Nhà Cung Cấp"].toString().replace(/\r?\n/g, " ").trim())
          : "";

        const taAoName = row["Tà Áo"]
          ? (typeof row["Tà Áo"] === 'string'
            ? row["Tà Áo"].replace(/\r?\n/g, " ").trim()
            : row["Tà Áo"].toString().replace(/\r?\n/g, " ").trim())
          : "";
        let trangThai;
        if (row["Trạng Thái"]) {
          const trangThaiText = row["Trạng Thái"].toString().replace(/\r?\n/g, " ").trim();
          if (trangThaiText === "Đang kinh doanh") {
            trangThai = true;
          } else if (trangThaiText === "Ngừng kinh doanh") {
            trangThai = false;
          } else {
            errors.push(`Dòng ${rowIndex}: Giá trị của Trạng Thái không hợp lệ (chỉ cho phép "Đang kinh doanh" hoặc "Ngừng kinh doanh").`);
          }
        } else {
          errors.push(`Dòng ${rowIndex}: Trạng Thái không được để trống.`);
        }
        const linkYoutube = row["Link Youtube"]
          ? row["Link Youtube"].replace(/\r?\n/g, " ").trim()
          : "";
        const moTa = row["Mô Tả"]
          ? row["Mô Tả"].replace(/\r?\n/g, " ").trim()
          : "";
        const anhList = row["Hình Ảnh (URL)"]
          ? row["Hình Ảnh (URL)"].split(",").map((url) =>
            url.replace(/\r?\n/g, " ").trim()
          )
          : [];

        // Validate từng trường theo thứ tự: nếu trống thì báo lỗi và bỏ qua các kiểm tra khác
        if (!tenAoDai) {
          errors.push(`Dòng ${rowIndex}: Tên áo dài không được để trống.`);
        } else if (tenAoDai.trim().length > 100) {
          errors.push(`Dòng ${rowIndex}: Tên áo dài không vượt quá 100 ký tự.`);
        } else if (tenAoDai.trim().length < 3) {
          errors.push(`Dòng ${rowIndex}: Tên áo dài không được ít hơn 3 ký tự.`);
        } else if (!/^[\p{L}\s]+$/u.test(tenAoDai.trim())) {
          errors.push(`Dòng ${rowIndex}: Tên áo dài chỉ được chứa chữ cái khoảng trắng.`);
        }

        if (!loaiAoDaiName) {
          errors.push(`Dòng ${rowIndex}: Loại áo dài không được để trống.`);
        } else {
          const loaiAoDaiData = await getLoaiAoDaiByName(loaiAoDaiName);
          if (!loaiAoDaiData) {
            errors.push(`Dòng ${rowIndex}: Không tìm thấy loại áo dài với tên "${loaiAoDaiName}".`);
          }
        }

        if (!chatLieuName) {
          errors.push(`Dòng ${rowIndex}: Chất liệu không được để trống.`);
        } else {
          const chatLieuData = await getChatLieuByName(chatLieuName);
          if (!chatLieuData) {
            errors.push(`Dòng ${rowIndex}: Không tìm thấy chất liệu với tên "${chatLieuName}".`);
          }
        }

        if (!nhaCungCapName) {
          errors.push(`Dòng ${rowIndex}: Nhà cung cấp không được để trống.`);
        } else {
          const nhaCungCapData = await getNhaCungCapByName(nhaCungCapName);
          if (!nhaCungCapData) {
            errors.push(`Dòng ${rowIndex}: Không tìm thấy nhà cung cấp với tên "${nhaCungCapName}".`);
          }
        }

        if (!taAoName) {
          errors.push(`Dòng ${rowIndex}: Tà áo không được để trống.`);
        } else {
          const taAoData = await getTaAoByName(taAoName);
          if (!taAoData) {
            errors.push(`Dòng ${rowIndex}: Không tìm thấy tà áo với tên "${taAoName}".`);
          }
        }

        if (linkYoutube && !/^https?:\/\/(www\.)?youtube\.com\/watch\?v=[\w-]+$/.test(linkYoutube.trim())) {
          errors.push(`Dòng ${rowIndex}: Link YouTube không hợp lệ.`);
        }

        if (!moTa) {
          errors.push(`Dòng ${rowIndex}: Mô tả không được để trống.`);
        }

        if (!anhList || anhList.length === 0) {
          errors.push(`Dòng ${rowIndex}: Danh sách hình ảnh không được để trống.`);
        } else if (anhList.length > 5) {
          errors.push(`Dòng ${rowIndex}: Danh sách hình ảnh không được vượt quá 5 ảnh.`);
        } else {
          anhList.forEach((anh, index) => {
            if (!anh || anh.trim() === "") {
              errors.push(`Dòng ${rowIndex}: Hình ảnh ${index + 1} không được để trống.`);
            } else if (!/^https?:\/\/.+\.(jpg|jpeg|png|gif)$/.test(anh.trim())) {
              errors.push(`Dòng ${rowIndex}: Hình ảnh ${index + 1} không hợp lệ.`);
            }
          });
        }

        // Trả về đối tượng sản phẩm (nếu một số trường không đạt, kết quả có thể chứa giá trị null)
        return {
          tenAoDai,
          // Nếu tên không trống và API trả về dữ liệu thì sử dụng id, ngược lại là null
          loaiAoDaiId: loaiAoDaiName ? (await getLoaiAoDaiByName(loaiAoDaiName))?.id : null,
          chatLieuId: chatLieuName ? (await getChatLieuByName(chatLieuName))?.id : null,
          nhaCungCapId: nhaCungCapName ? (await getNhaCungCapByName(nhaCungCapName))?.id : null,
          taAoId: taAoName ? (await getTaAoByName(taAoName))?.id : null,
          trangThai,
          linkYoutube,
          moTa,
          anhList,
        };
      })
    );

    if (errors.length > 0) {
      Modal.error({
        title: "Lỗi Validation",
        content: h(
          "div",
          { style: "white-space: pre-line; color: red;" },
          errors.join("\n")
        ),
      });
      return;
    }

    Modal.confirm({
      title: `Xác nhận`,
      content: `Bạn có chắc chắn muốn import ${sanPhamsImport.length} sản phẩm không?`,
      okText: "Xác nhận",
      cancelText: "Hủy bỏ",
      onOk: async () => {
        try {
          await importSanPhams(sanPhamsImport);
          Modal.success({ title: "Thành công", content: "Import sản phẩm thành công!" });
          fetchSanPhams();
        } catch (error) {
          if (error.response && error.response.data && error.response.data.errors.tenAoDai) {
            Modal.error({ title: "Lỗi", content: error.response.data.errors.tenAoDai });
          } else {
            Modal.error({ title: "Lỗi", content: "Có lỗi khi import sản phẩm!" });
          }
        }
      },
    });
  };
  reader.readAsArrayBuffer(file);
};

// Xử lý Export Excel
const handleExportOk = async () => {
  await exportExcel(exportOption.value);
  isExportModalVisible.value = false;
};

const handleExportCancel = () => {
  isExportModalVisible.value = false;
};

const showExportModal = () => {
  isExportModalVisible.value = true;
};

const exportExcel = async (option) => {
  let productsToExport = [];

  try {
    if (option === 'selected') {
      if (selectedRowKeys.value.length === 0) {
        Modal.error({
          title: 'Lỗi',
          content: 'Vui lòng chọn ít nhất một sản phẩm để xuất Excel',
        });
        return;
      }
      productsToExport = sanPhams.value.filter(product =>
        selectedRowKeys.value.includes(product.id)
      );
    } else if (option === 'all') {
      productsToExport = await getSanPhamAll();
    } else if (option === 'en') {
      const allProducts = await getSanPhamAll();
      productsToExport = allProducts.filter(product => product.trangThai === true);
    } else if (option === 'dis') {
      const allProducts = await getSanPhamAll();
      productsToExport = allProducts.filter(product => product.trangThai === false);
    }

    const data = productsToExport.map(product => ({
      "Mã Áo Dài": product.maAoDai,
      "Tên Áo Dài": product.tenAoDai,
      "Loại Áo Dài": product.tenAoDai,
      "Chất Liệu": product.tenChatLieu,
      "Tà Áo": product.tenTaAo,
      "Nhà Cung Cấp": product.tenNhaCungCap,
      "Link YouTube": product.linkYoutube,
      "Trạng Thái": product.trangThai ? "Đang kinh doanh" : "Ngừng kinh doanh",
      "Ngày Tạo": formatDateTime(product.ngayTao),
      "Mô Tả": product.moTa,
      "Link ảnh Sản Phẩm": product.anhList.join("\n")
    }));

    const worksheet = utils.json_to_sheet(data);
    worksheet['!cols'] = [
      { wch: 10 },
      { wch: 25 },
      { wch: 15 },
      { wch: 15 },
      { wch: 15 },
      { wch: 15 },
      { wch: 30 },
      { wch: 18 },
      { wch: 25 },
      { wch: 40 },
      { wch: 100 }
    ];

    Modal.confirm({
      title: 'Xác nhận',
      content: `Bạn có chắc chắn muốn export ${productsToExport.length} sản phẩm không?`,
      okText: 'Xác nhận',
      cancelText: 'Hủy bỏ',
      onOk: () => {
        const workbook = utils.book_new();
        utils.book_append_sheet(workbook, worksheet, 'Danh sách sản phẩm');
        writeFile(workbook, 'danh_sach_san_pham_export.xlsx');
        notification.success({
          message: 'Xuất Excel thành công',
          description: `Đã xuất ${productsToExport.length} sản phẩm.`,
        });
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

// Xử lý chọn sản phẩm trong table
const selectedRowKeys = ref([]);
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys) => {
    selectedRowKeys.value = keys;
  },
}));

// Các hàm điều hướng
const showFormAdd = () => {
  router.push("/san-pham/add");
};
const showFormUpdate = (id) => {
  router.push(`/san-pham/edit/${id}`);
};

const handlePageSizeChange = (newSize) => {
  size.value = newSize;
  fetchSanPhams(1);
};

const columns = [
  { title: "#", dataIndex: "index", key: "index" },
  { title: "Mã", dataIndex: "maAoDai", key: "maAoDai" },
  { title: "Tên Áo Dài", dataIndex: "tenAoDai", key: "tenAoDai", width: "25%" },
  { title: "Loại Áo Dài", dataIndex: "tenLoaiAoDai", key: "tenLoaiAoDai" },
  { title: "Chất Liệu", dataIndex: "tenChatLieu", key: "tenChatLieu" },
  { title: "Tà Áo", dataIndex: "tenTaAo", key: "tenTaAo" },
  { title: "Nhà cung cấp", dataIndex: "tenNhaCungCap", key: "tenNhaCungCap" },
  { title: "Trạng Thái", key: "trangThai" },
  { title: "Actions", key: "actions" },
];

let debounceTimer = null;
const onKeywordChange = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(() => {
    fetchSanPhams(1);
  }, 500);
};

// Hàm fetch sản phẩm kết hợp keyword và các bộ lọc (currentFilters)
const fetchSanPhams = async (pageNumber = 1) => {
  page.value = pageNumber;
  try {
    const params = {
      keyword: keyword.value.trim() || "",
      tenLoaiAoDai: currentFilters.value.tenLoaiAoDai || "",
      tenNhaCungCap: currentFilters.value.tenNhaCungCap || "",
      tenTaAo: currentFilters.value.tenTaAo || "",
      tenChatLieu: currentFilters.value.tenChatLieu || "",
      trangThai: currentFilters.value.trangThai !== null ? currentFilters.value.trangThai : "",
      sortDate: currentFilters.value.sortDate !== null ? currentFilters.value.sortDate : "",
      page: page.value - 1,
      size: size.value,
    };

    const response = await filterSanPhams(params);
    const data = response; // axios trả về response.data

    if (data && data.content) {
      sanPhams.value = data.content.map((item, index) => ({
        ...item,
        index: index + 1 + (page.value - 1) * size.value,
      }));
      totalPages.value = data.page.totalPages;
      totalElements.value = data.page.totalElements;
    } else {
      sanPhams.value = [];
      totalPages.value = 0;
      totalElements.value = 0;
    }
  } catch (error) {
    console.error("Lỗi khi gọi API:", error);
  }
};

const formatDateTime = (dateString) => {
  const date = new Date(dateString);
  const time = date.toLocaleTimeString("en-EN", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
  const day = date.toLocaleDateString("vi-VN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
  return `${time}, ngày ${day}`;
};

const customRow = (record) => {
  return {
    onClick: async () => {
      try {
        selectedSanPham.value = record;
        const variants = await getSanPhamChiTietBySanPhamId(record.id);
        productVariants.value = variants;
        if (!productVariants.anhUrl || productVariants.anhUrl === '' || productVariants.anhUrl === null || productVariants.anhUrl === undefined) {
          productVariants.anhUrl = 'https://placehold.jp/800x1200.png';
        }
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

onMounted(() => {
  keyword.value = route.query.keyword || "";
  page.value = Number(route.query.page) || 1;
  size.value = Number(route.query.size) || 5;
  fetchSanPhams(page.value);
});
</script>



<style scoped>
:deep(.ant-table-tbody > tr > td) {
  cursor: pointer;
}

:deep(.product-description img) {
  max-width: 50%;
  height: auto;
  width: 700px;
  display: block;
  margin: 10px auto;
  border-radius: 8px;
}

.product-details li {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.product-details li strong {
  width: 150px;
  flex-shrink: 0;
  white-space: nowrap;
}

.product-details li span {
  display: inline-block;
  width: calc(100% - 150px);
  word-wrap: break-word;
  word-break: break-word;
}

.image-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  justify-content: center;
  align-items: center;
}

.ant-image-mask {
  border-radius: 4px;
}

.variants-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1%;
}

.variant-item {
  width: 19%;
  margin-bottom: 15px;
}

.ant-card-meta-description {
  font-size: 12px;
  text-align: center;
}

.ant-card-meta-title {
  text-align: center;
}

.product-details li .youtube-link {
  display: inline-block;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: calc(100% - 150px);
}

.product-details li .youtube-link a {
  word-wrap: break-word;
  word-break: break-all;
}

.edit-button:hover {
  background-color: rgba(255, 193, 7, 0.2);
  /* Màu vàng nhạt khi hover */
  border-color: #ffc107 !important;
  /* Giữ màu border vàng */
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