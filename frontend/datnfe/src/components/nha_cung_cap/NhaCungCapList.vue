<template>
    <a-layout class="container mt-5 rounded">
        <h3 class="text-center mb-4">DANH SÁCH NHÀ CUNG CẤP</h3>
        <div class="flex justify-between mb-3">
            <div>
                <a-input-search v-model:value="keyword" loading enter-button placeholder="Tìm kiếm nhà cung cấp"
                    @input="onKeywordChange" style="width: 300px" />
                <a-select v-model:value="trangThai" style="width: 150px" class="ms-3" @change="onTrangThaiChange">
                    <a-select-option value="">Tất cả</a-select-option>
                    <a-select-option value="true">Đang hoạt động</a-select-option>
                    <a-select-option value="false">Ngừng hoạt động</a-select-option>
                </a-select>
            </div>
            <div>
                <a-button v-if="isAdmin" type="primary" @click="showFormAdd">
                    <i class="bi bi-plus-lg"> Thêm mới nhà cung cấp</i>
                </a-button>
            </div>
        </div>
        <a-table :dataSource="nhaCungCaps" :columns="columns" :pagination="false" bordered>
            <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'trangThai'">
                    <a-tag :color="record.trangThai ? 'green' : 'red'">
                        {{ record.trangThai ? "Đang hoạt động" : "Ngừng hoạt động" }}
                    </a-tag>
                </template>
                <template v-else-if="column.key === 'ngayTao'">
                    {{ formatDateTime(record.ngayTao) }}
                </template>
                <template v-else-if="column.key === 'actions'">
                    <a-button :disabled="!isAdmin" type="link" @click="showFormUpdate(record)" class="text-warning">
                        <edit-outlined />
                    </a-button>
                </template>
                <template v-else>
                    {{ record[column.key] }}
                </template>
            </template>
        </a-table>
        <div class="pagination-container">
            <div class="pagination-wrapper">
                <a-pagination class="pagination" v-model:current="page" :total="totalPages * size" :pageSize="size"
                    @change="fetchNhaCungCaps" show-less-items />
                <a-select v-model:value="size" @change="handlePageSizeChange" class="page-size-selector"
                    style="width: 120px">
                    <a-select-option :value="5">5 / trang</a-select-option>
                    <a-select-option :value="10">10 / trang</a-select-option>
                    <a-select-option :value="15">15 / trang</a-select-option>
                </a-select>
            </div>
        </div>
        <a-modal v-model:open="visible" :footer="null" @cancel="onClose"
            :title="isEditMode ? 'Cập nhật nhà cung cấp' : 'Thêm nhà cung cấp'" width="540px">
            <a-form :model="selectedNhaCungCap" :rules="formRules" ref="formRef" @finish="onSubmit" layout="vertical">
                <a-form-item label="Tên Nhà Cung Cấp" name="tenNhaCungCap">
                    <a-input v-model:value="selectedNhaCungCap.tenNhaCungCap" @input="formErrors.tenNhaCungCap = ''" />
                </a-form-item>
                <a-form-item label="Địa chỉ" name="diaChi">
                    <a-input v-model:value="selectedNhaCungCap.diaChi" @input="formErrors.diaChi = ''" />
                </a-form-item>
                <a-form-item label="Số điện thoại" name="soDienThoai">
                    <a-input v-model:value="selectedNhaCungCap.soDienThoai" @input="formErrors.soDienThoai = ''" />
                </a-form-item>
                <a-form-item label="Email" name="email">
                    <a-input v-model:value="selectedNhaCungCap.email" @input="formErrors.email = ''" />
                </a-form-item>
                <a-form-item label="Mô tả" name="moTa">
                    <a-input v-model:value="selectedNhaCungCap.moTa" @input="formErrors.moTa = ''" />
                </a-form-item>
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="selectedNhaCungCap.trangThai" checked-children="Đang hoạt động"
                        un-checked-children="Ngừng hoạt động" />
                </a-form-item>
                <a-form-item class="ant-form-item-button">
                    <a-button @click="onClose" type="primary" danger class="mx-2">
                        <i class="bi bi-x-lg"> Huỷ bỏ</i>
                    </a-button>
                    <a-button type="primary" html-type="submit">
                        <i class="bi bi-floppy"> {{ isEditMode ? ' Cập nhật' : ' Thêm mới' }}</i>
                    </a-button>
                </a-form-item>
            </a-form>
        </a-modal>
    </a-layout>
</template>


<script setup>
import { ref, onMounted, computed, h } from "vue";
import { getNhaCungCaps, addNhaCungCap, updateNhaCungCap, getNhaCungCapById } from "./api";
import { message, Modal, notification } from "ant-design-vue";
import { EyeOutlined, EditOutlined } from '@ant-design/icons-vue';
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
    return store.state.roles.includes('ROLE_QUANLY');
});

const nhaCungCaps = ref([]);
const totalPages = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const trangThai = ref("");
const visible = ref(false);
const isEditMode = ref(false);
const selectedNhaCungCap = ref({ tenNhaCungCap: "", diaChi: "", soDienThoai: "", email: "", moTa: "", trangThai: true });
const loading = ref(false);
const formRef = ref(null);

const formErrors = ref({});

const formRules = computed(() => ({
    tenNhaCungCap: [
        { required: true, message: 'Vui lòng nhập tên nhà cung cấp' },
        { min: 3, max: 100, message: 'Tên nhà cung cấp phải có từ 3 đến 100 ký tự' },
        { pattern: /^(?!\s)(?!.*\s$)[\p{L}\s]+$/u, message: 'Tên nhà cung cấp chỉ được chứa chữ cái và khoảng trắng' },
        {
            // Validator chỉ bắt lỗi từ backend
            validator: async (_, value) => {
                if (formErrors.value.tenNhaCungCap) {
                    return Promise.reject(formErrors.value.tenNhaCungCap);
                }
                return Promise.resolve();
            }
        }
    ],
    diaChi: [
        { required: true, message: 'Vui lòng nhập địa chỉ' },
        { min: 3, max: 100, message: 'Địa chỉ phải có từ 3 đến 100 ký tự' },
        {
            validator: async (_, value) => {
                if (formErrors.value.diaChi) {
                    return Promise.reject(formErrors.value.diaChi);
                }
                return Promise.resolve();
            }
        }
    ],
    soDienThoai: [
        { required: true, message: 'Vui lòng nhập số điện thoại' },
        { pattern: /^(0[0-9]{9,10})$/, message: 'Số điện thoại không đúng định dạng' },
        {
            validator: async (_, value) => {
                if (formErrors.value.soDienThoai) {
                    return Promise.reject(formErrors.value.soDienThoai);
                }
                return Promise.resolve();
            }
        }
    ],
    email: [
        { required: true, message: 'Vui lòng nhập email' },
        { type: 'email', message: 'Email không đúng định dạng' },
        {
            validator: async (_, value) => {
                if (formErrors.value.email) {
                    return Promise.reject(formErrors.value.email);
                }
                return Promise.resolve();
            }
        }
    ],
    moTa: [
        { required: true, message: 'Vui lòng nhập mô tả' },
        { min: 5, max: 500, message: 'Mô tả phải có từ 5 đến 500 ký tự' },
        {
            validator: async (_, value) => {
                if (formErrors.value.moTa) {
                    return Promise.reject(formErrors.value.moTa);
                }
                return Promise.resolve();
            }
        }
    ],
    trangThai: [
        { required: true, message: 'Vui lòng chọn trạng thái' }
    ]
}));

const onTrangThaiChange = (value) => {
    trangThai.value = value;
    fetchNhaCungCaps(1);
};

// Methods xử lý dữ liệu
const fetchNhaCungCaps = async (pageNumber = 1) => {
    page.value = pageNumber;
    try {
        const response = await getNhaCungCaps(keyword.value, page.value - 1, size.value, trangThai.value);
        if (response && response.content) {
            nhaCungCaps.value = response.content.map((item, index) => ({
                ...item,
                index: index + 1 + (page.value - 1) * size.value,
            }));
            totalPages.value = response.page.totalPages;
        } else {
            nhaCungCaps.value = [];
        }
    } catch (error) {
        nhaCungCaps.value = [];
        message.error('Lỗi khi tải dữ liệu');
    }
};

let debounceTimer = null;
const onKeywordChange = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchNhaCungCaps(1);
    }, 300);
};

const formatDateTime = (dateString) => {
    const date = new Date(dateString);
    const options = {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        second: "2-digit",
        hour12: true,
    };
    return date.toLocaleString("en-VI", options);
};

const showFormAdd = () => {
    selectedNhaCungCap.value = { tenNhaCungCap: "", diaChi: "", soDienThoai: "", email: "", moTa: "", trangThai: true };
    isEditMode.value = false;
    visible.value = true;
};

const showFormUpdate = async (record) => {
    try {
        const response = await getNhaCungCapById(record.id);
        if (response) {
            selectedNhaCungCap.value = { ...response };
            isEditMode.value = true;
            visible.value = true;
        } else {
            message.error("Không tìm thấy nhà cung cấp!");
        }
    } catch (error) {
        message.error('Lỗi tải dữ liệu nhà cung cấp');
    }
};

const onSubmit = async () => {
    // Sử dụng Modal.confirm để xác nhận lưu
    Modal.confirm({
        title: 'Xác nhận lưu nhà cung cấp',
        content: 'Bạn có chắc chắn muốn lưu nhà cung cấp này không?',
        okText: 'Xác nhận',
        cancelText: 'Huỷ bỏ',
        async onOk() {
            loading.value = true;
            // Reset lỗi backend trước khi submit
            formErrors.value = {};
            try {
                // Validate form
                await formRef.value.validate();

                const payload = {
                    ...selectedNhaCungCap.value,
                    tenNhaCungCap: selectedNhaCungCap.value.tenNhaCungCap.replace(/\s+/g, " ").trim(),
                    diaChi: selectedNhaCungCap.value.diaChi.replace(/\s+/g, " ").trim(),
                    soDienThoai: selectedNhaCungCap.value.soDienThoai.replace(/\s+/g, " ").trim(),
                    email: selectedNhaCungCap.value.email.replace(/\s+/g, " ").trim(),
                    moTa: selectedNhaCungCap.value.moTa.replace(/\s+/g, " ").trim()
                };

                let response;
                if (isEditMode.value) {
                    response = await updateNhaCungCap(
                        payload.id, { ...payload }
                    );
                } else {
                    response = await addNhaCungCap({ ...payload });
                }
                if (!response) {
                    message.error(isEditMode.value ? 'Cập nhật thất bại!' : 'Thêm mới thất bại!');
                    return;
                }
                // Hiển thị notification thành công
                notification.success({
                    message: 'Thành công',
                    description: isEditMode.value
                        ? 'Cập nhật nhà cung cấp thành công!'
                        : 'Thêm mới nhà cung cấp thành công!'
                });
                onClose();
                await fetchNhaCungCaps(page.value);
            } catch (error) {
                if (error.response?.data?.errors) {
                    // Cập nhật lỗi từ backend vào formErrors
                    formErrors.value = error.response.data.errors;
                    // Validate lại các trường để hiển thị lỗi trên form
                    await formRef.value.validate(['tenNhaCungCap', 'diaChi', 'soDienThoai', 'email', 'moTa']).catch(() => { });
                } else {
                    message.error(error.message || 'Có lỗi xảy ra');
                }
                // Đóng modal confirm nếu có lỗi
                Modal.destroyAll();
                throw error;
            } finally {
                loading.value = false;
            }
        },
        onCancel() {
            message.info('Đã huỷ thao tác lưu');
        }
    });
};

const onClose = () => {
    visible.value = false;
    isEditMode.value = false;
    formErrors.value = {};
    selectedNhaCungCap.value = { tenNhaCungCap: "", diaChi: "", soDienThoai: "", email: "", trangThai: true };
    if (formRef.value) {
        formRef.value.resetFields();
    }
};

const handlePageSizeChange = (newSize) => {
    size.value = newSize;
    fetchNhaCungCaps(1);
};

const columns = [
    { title: "#", dataIndex: "index", key: "index" },
    { title: "Tên Nhà Cung Cấp", dataIndex: "tenNhaCungCap", key: "tenNhaCungCap" },
    { title: "Địa Chỉ", dataIndex: "diaChi", key: "diaChi" },
    { title: "Số Điện Thoại", dataIndex: "soDienThoai", key: "soDienThoai" },
    { title: "Email", dataIndex: "email", key: "email" },
    { title: "Trạng Thái", key: "trangThai" },
    { title: "Mô Tả", dataIndex: "moTa", key: "moTa" },
    { title: "Ngày Tạo", key: "ngayTao" },
    { title: "Actions", key: "actions" },
];

onMounted(() => {
    fetchNhaCungCaps();
});
</script>


<style scoped>
.container {
    padding: 24px;
}

.text-center {
    text-align: center;
}

.mb-3 {
    margin-bottom: 16px;
}

.mt-5 {
    margin-top: 32px;
}

.flex {
    display: flex;
}

.justify-between {
    justify-content: space-between;
}

.mr-2 {
    margin-right: 8px;
}

.pagination-container {
    display: flex;
    justify-content: center;
    margin-top: 16px;
}

.pagination-wrapper {
    display: flex;
    align-items: center;
    gap: 16px;
}

.page-size-selector {
    width: 120px;
}

.ant-form-item {
    margin-bottom: 16px;
}

.ant-form-item-label {
    font-weight: bold;
    text-align: left;
}

.ant-form-item-control {
    display: flex;
    flex-direction: column;
}

.ant-form-item-control-input {
    margin-bottom: 10px;
}

.ant-form-item-button {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
}
</style>
