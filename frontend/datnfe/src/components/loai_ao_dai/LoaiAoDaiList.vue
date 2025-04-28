<template>
    <a-layout class="container mt-5 rounded">
        <h3 class="text-center mb-4">DANH SÁCH LOẠI ÁO</h3>
        <div class="flex justify-between mb-3">
            <div>
                <a-input-search v-model:value="keyword" loading enter-button placeholder="Tìm kiếm loại áo dài"
                    @input="onKeywordChange" style="width: 300px" />
                <a-select v-model:value="trangThai" style="width: 150px" class="ms-3" @change="onTrangThaiChange">
                    <a-select-option value="">Tất cả</a-select-option>
                    <a-select-option value="true">Đang hoạt động</a-select-option>
                    <a-select-option value="false">Ngừng hoạt động</a-select-option>
                </a-select>
            </div>
            <div>
                <a-button v-if="isAdmin" type="primary" @click="showFormAdd">
                    <i class="bi bi-plus-lg"> Thêm mới loại áo dài</i>
                </a-button>
            </div>
        </div>
        <a-table :dataSource="loaiAoDais" :columns="columns" :pagination="false" bordered>
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
                    @change="fetchLoaiAoDais" show-less-items />
                <a-select v-model:value="size" @change="handlePageSizeChange" class="page-size-selector"
                    style="width: 120px">
                    <a-select-option :value="5">5 / trang</a-select-option>
                    <a-select-option :value="10">10 / trang</a-select-option>
                    <a-select-option :value="15">15 / trang</a-select-option>
                </a-select>
            </div>
        </div>
        <a-modal v-model:open="visible" :footer="null" @cancel="onClose"
            :title="isEditMode ? 'Cập nhật loại áo dài' : 'Thêm loại áo dài'" width="540px">
            <!-- Sử dụng ref formRef để gọi các phương thức validate, resetFields -->
            <a-form :model="selectedLoaiAoDai" :rules="formRules" ref="formRef" @finish="onSubmit" layout="vertical">
                <a-form-item label="Tên Loại Áo Dài" name="tenLoaiAoDai">
                    <a-input v-model:value="selectedLoaiAoDai.tenLoaiAoDai" @input="formErrors.tenLoaiAoDai = ''" />
                </a-form-item>
                <a-form-item label="Mô tả" name="moTa">
                    <a-input v-model:value="selectedLoaiAoDai.moTa" @input="formErrors.moTa = ''" />
                </a-form-item>
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="selectedLoaiAoDai.trangThai" checked-children="Đang hoạt động"
                        un-checked-children="Ngừng hoạt động" />
                </a-form-item>
                <a-form-item class="ant-form-item-button">
                    <a-button type="primary" class="mx-2" danger @click="onClose"><i class="bi bi-x-lg"> Huỷ
                            bỏ</i></a-button>
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
import { getLoaiAoDais, addLoaiAoDai, updateLoaiAoDai, getLoaiAoDaiById } from "./api";
import { message, Modal, notification } from "ant-design-vue";
import { EditOutlined, SmileOutlined } from '@ant-design/icons-vue';
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
    return store.state.roles.includes('ROLE_QUANLY');
});

const loaiAoDais = ref([]);
const totalPages = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const trangThai = ref("");
const visible = ref(false);
const isEditMode = ref(false);
const selectedLoaiAoDai = ref({ tenLoaiAoDai: "", moTa: "", trangThai: true });
const loading = ref(false);
const formRef = ref(null);
const formErrors = ref({});

const formRules = computed(() => ({
    tenLoaiAoDai: [
        { required: true, message: 'Vui lòng nhập tên loại áo dài' },
        { min: 3, max: 100, message: 'Tên loại áo dài phải có từ 3 đến 100 ký tự' },
        { pattern: /^(?!\s)(?!.*\s$)[\p{L}\s]+$/u, message: 'Tên loại áo dài chỉ được chứa chữ cái và khoảng trắng' },
        {
            // Validator này chỉ kiểm tra lỗi từ backend
            validator: async (_, value) => {
                if (formErrors.value.tenLoaiAoDai) {
                    return Promise.reject(formErrors.value.tenLoaiAoDai);
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
    fetchLoaiAoDais(1);
}

const fetchLoaiAoDais = async (pageNumber = 1) => {
    page.value = pageNumber;
    try {
        const response = await getLoaiAoDais(keyword.value, page.value - 1, size.value, trangThai.value);
        if (response && response.content) {
            loaiAoDais.value = response.content.map((item, index) => ({
                ...item,
                index: index + 1 + (page.value - 1) * size.value,
            }));
            totalPages.value = response.page.totalPages;
        } else {
            loaiAoDais.value = [];
        }
    } catch (error) {
        loaiAoDais.value = [];
        message.error('Lỗi khi tải dữ liệu');
    }
};

let debounceTimer = null;
const onKeywordChange = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchLoaiAoDais(1);
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
    selectedLoaiAoDai.value = { tenLoaiAoDai: "", moTa: "", trangThai: true };
    isEditMode.value = false;
    visible.value = true;
};

const showFormUpdate = async (record) => {
    try {
        const response = await getLoaiAoDaiById(record.id);
        if (response) {
            selectedLoaiAoDai.value = { ...response };
            isEditMode.value = true;
            visible.value = true;
        } else {
            message.error("Không tìm thấy loại áo dài!");
        }
    } catch (error) {
        message.error('Lỗi tải dữ liệu loại áo dài');
    }
};

const onSubmit = async () => {
    // Sử dụng Modal.confirm để xác nhận lưu dữ liệu
    Modal.confirm({
        title: 'Xác nhận lưu dữ liệu',
        content: 'Bạn có chắc chắn muốn lưu loại áo dài này không?',
        okText: 'Xác nhận',
        cancelText: 'Huỷ bỏ',
        async onOk() {
            loading.value = true;
            // Reset lỗi backend trước khi submit
            formErrors.value = {};
            try {
                await formRef.value.validate();

                const payload = {
                    ...selectedLoaiAoDai.value,
                    tenLoaiAoDai: selectedLoaiAoDai.value.tenLoaiAoDai.replace(/\s+/g, " ").trim(),
                    moTa: selectedLoaiAoDai.value.moTa.replace(/\s+/g, " ").trim()
                };

                let response;
                if (isEditMode.value) {
                    response = await updateLoaiAoDai(payload.id, { ...payload });
                } else {
                    response = await addLoaiAoDai({ ...payload });
                }
                if (!response) {
                    message.error(isEditMode.value ? 'Cập nhật thất bại!' : 'Thêm mới thất bại!');
                    return;
                }
                // Hiển thị notification thành công
                notification.success({
                    message: 'Thành công',
                    description: isEditMode.value ? 'Cập nhật thành công!' : 'Thêm mới thành công!',
                });
                onClose();
                await fetchLoaiAoDais(page.value);
            } catch (error) {
                if (error.response?.data?.errors) {
                    formErrors.value = error.response.data.errors;
                    // Validate lại các trường để hiển thị lỗi từ backend
                    await formRef.value.validate(['tenLoaiAoDai', 'moTa']).catch(() => { });
                } else {
                    message.error(error.message || 'Có lỗi xảy ra');
                }
                // Đóng modal confirm khi có lỗi
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
    selectedLoaiAoDai.value = { tenLoaiAoDai: "", moTa: "", trangThai: true };
    if (formRef.value) {
        formRef.value.resetFields();
    }
};

const handlePageSizeChange = (newSize) => {
    size.value = newSize;
    fetchLoaiAoDais(1);
};

const columns = [
    { title: "#", dataIndex: "index", key: "index" },
    { title: "Tên Loại Áo Dài", dataIndex: "tenLoaiAoDai", key: "tenLoaiAoDai" },
    { title: "Mô Tả", dataIndex: "moTa", key: "moTa" },
    { title: "Trạng Thái", key: "trangThai" },
    { title: "Ngày Tạo", key: "ngayTao" },
    { title: "Actions", key: "actions", scopedSlots: { customRender: "actions" } },
];

onMounted(() => {
    fetchLoaiAoDais(1);
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