<template>
    <a-layout class="container mt-5 rounded">
        <h3 class="text-center mb-4">DANH SÁCH MÀU SẮC</h3>
        <div class="flex justify-between mb-3">
            <div>
                <a-input-search v-model:value="keyword" loading enter-button placeholder="Tìm kiếm màu sắc"
                    @input="onKeywordChange" style="width: 300px" />
                <a-select v-model:value="trangThai" style="width: 150px" class="ms-3" @change="onTrangThaiChange">
                    <a-select-option value="">Tất cả</a-select-option>
                    <a-select-option value="true">Đang hoạt động</a-select-option>
                    <a-select-option value="false">Ngừng hoạt động</a-select-option>
                </a-select>
            </div>
            <div>
                <a-button v-if="isAdmin" type="primary" @click="showFormAdd">
                    <i class="bi bi-plus-lg"> Thêm mới màu sắc</i>
                </a-button>
            </div>
        </div>
        <a-table :dataSource="mauSacs" :columns="columns" :pagination="false" bordered>
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
                    @change="fetchMauSacs" show-less-items />
                <a-select v-model:value="size" @change="handlePageSizeChange" class="page-size-selector"
                    style="width: 120px">
                    <a-select-option :value="5">5 / trang</a-select-option>
                    <a-select-option :value="10">10 / trang</a-select-option>
                    <a-select-option :value="15">15 / trang</a-select-option>
                </a-select>
            </div>
        </div>
        <a-modal v-model:open="visible" :footer="null" @cancel="onClose"
            :title="isEditMode ? 'Cập nhật màu sắc' : 'Thêm màu sắc'" width="540px">
            <a-form :model="selectedMauSac" :rules="formRules" ref="formRef" @finish="onSubmit" layout="vertical">
                <a-form-item label="Tên Màu Sắc" name="tenMauSac">
                    <a-input v-model:value="selectedMauSac.tenMauSac" @input="formErrors.tenMauSac = ''" />
                </a-form-item>
                <a-form-item label="Mô tả" name="moTa">
                    <a-input v-model:value="selectedMauSac.moTa" @input="formErrors.moTa = ''" />
                </a-form-item>
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="selectedMauSac.trangThai" checked-children="Đang kinh doanh"
                        un-checked-children="Ngừng kinh doanh" />
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
import { getMauSacs, addMauSac, updateMauSac, getMauSacById } from "./api";
import { message, notification, Modal } from "ant-design-vue";
import { EditOutlined } from "@ant-design/icons-vue";
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
    return store.state.roles.includes('ROLE_QUANLY');
});

const mauSacs = ref([]);
const totalPages = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const trangThai = ref("");
const visible = ref(false);
const isEditMode = ref(false);
const selectedMauSac = ref({ tenMauSac: "", moTa: "", trangThai: true });
const loading = ref(false);
const formRef = ref(null);

const formErrors = ref({});

// Computed formRules: các rule cơ bản và rule custom để bắt lỗi backend
const formRules = computed(() => ({
    tenMauSac: [
        { required: true, message: 'Vui lòng nhập tên màu sắc' },
        { pattern: /^(?!\s)(?!.*\s$)[\p{L}\s]+$/u, message: 'Tên màu sắc chỉ được chứa chữ cái và khoảng trắng' },
        {
            // Validator này chỉ bắt lỗi từ backend
            validator: async (_, value) => {
                if (formErrors.value.tenMauSac) {
                    return Promise.reject(formErrors.value.tenMauSac);
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
    fetchMauSacs(1);
}
// Methods for fetching and handling data
const fetchMauSacs = async (pageNumber = 1) => {
    page.value = pageNumber;
    try {
        const response = await getMauSacs(keyword.value, page.value - 1, size.value, trangThai.value);
        if (response && response.content) {
            mauSacs.value = response.content.map((item, index) => ({
                ...item,
                index: index + 1 + (page.value - 1) * size.value,
            }));
            totalPages.value = response.page.totalPages;
        } else {
            mauSacs.value = [];
        }
    } catch (error) {
        mauSacs.value = [];
        message.error('Lỗi khi tải dữ liệu');
    }
};

let debounceTimer = null;
const onKeywordChange = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchMauSacs(1);
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
    selectedMauSac.value = { tenMauSac: "", moTa: "", trangThai: true };
    isEditMode.value = false;
    visible.value = true;
};

const showFormUpdate = async (record) => {
    try {
        const response = await getMauSacById(record.id);
        if (response) {
            selectedMauSac.value = { ...response };
            isEditMode.value = true;
            visible.value = true;
        } else {
            message.error("Không tìm thấy màu sắc!");
        }
    } catch (error) {
        message.error('Lỗi tải dữ liệu màu sắc');
    }
};

const onSubmit = async () => {
    Modal.confirm({
        title: 'Xác nhận lưu màu sắc',
        content: 'Bạn có chắc chắn muốn lưu màu sắc này không?',
        okText: 'Xác nhận',
        cancelText: 'Huỷ bỏ',
        async onOk() {
            loading.value = true;
            // Reset lỗi từ backend trước khi submit
            formErrors.value = {};
            try {
                // Validate form
                await formRef.value.validate();

                const payload = {
                    ...selectedMauSac.value,
                    tenMauSac: selectedMauSac.value.tenMauSac.replace(/\s+/g, " ").trim(),
                    moTa: selectedMauSac.value.moTa.replace(/\s+/g, " ").trim()
                };

                console.log(payload);


                let response;
                if (isEditMode.value) {
                    response = await updateMauSac(payload.id, { ...payload });
                } else {
                    response = await addMauSac({ ...payload });

                }
                if (!response) {
                    message.error(isEditMode.value ? 'Cập nhật thất bại!' : 'Thêm mới thất bại!');
                    return;
                }
                // Hiển thị notification khi lưu thành công
                notification.success({
                    message: 'Thành công',
                    description: isEditMode.value
                        ? 'Cập nhật màu sắc thành công!'
                        : 'Thêm mới màu sắc thành công!'
                });
                onClose();
                await fetchMauSacs(page.value);
            } catch (error) {
                if (error.response?.data?.errors) {
                    // Cập nhật lỗi từ backend vào formErrors
                    formErrors.value = error.response.data.errors;
                    // Validate lại các trường để hiển thị lỗi
                    await formRef.value.validate(['tenMauSac', 'moTa']).catch(() => { });
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
    selectedMauSac.value = { tenMauSac: "", moTa: "", trangThai: true };
    if (formRef.value) {
        formRef.value.resetFields();
    }
};

const handlePageSizeChange = (newSize) => {
    size.value = newSize;
    fetchMauSacs(1);
};

const columns = [
    { title: "#", dataIndex: "index", key: "index" },
    { title: "Tên Màu Sắc", dataIndex: "tenMauSac", key: "tenMauSac" },
    { title: "Mô Tả", dataIndex: "moTa", key: "moTa" },
    { title: "Trạng Thái", key: "trangThai" },
    { title: "Ngày Tạo", key: "ngayTao" },
    { title: "Actions", key: "actions", scopedSlots: { customRender: "actions" } },
];

onMounted(() => {
    fetchMauSacs(1);
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
