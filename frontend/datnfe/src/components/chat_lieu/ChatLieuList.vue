<template>
    <a-layout class="container mt-5 rounded p-3">
        <h3 class="text-center mb-4 mt-3">DANH SÁCH CHẤT LIỆU</h3>
        <!-- Tìm kiếm và nút thêm chất liệu -->
        <div class="d-flex justify-content-between mb-3">
            <div>
                <a-input-search v-model:value="keyword" loading enter-button placeholder="Tìm kiếm chất liệu"
                    @input="onKeywordChange" style="width: 300px" />
                <a-select v-model:value="trangThai" style="width: 150px" class="ms-3" @change="onTrangThaiChange">
                    <a-select-option value="">Tất cả</a-select-option>
                    <a-select-option value="true">Đang hoạt động</a-select-option>
                    <a-select-option value="false">Ngừng hoạt động</a-select-option>
                </a-select>

            </div>

            <div>
                <a-button v-if="isAdmin" type="primary" @click="showFormAdd">
                    <i class="bi bi-plus-lg"> Thêm mới chất liệu</i>
                </a-button>
            </div>
        </div>
        <!-- Bảng chất liệu -->
        <a-table :dataSource="chatLieus" :columns="columns" :pagination="false" bordered>
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
        <!-- Phân trang -->
        <div class="d-flex justify-content-center my-4">
            <div class="d-flex items-center gap-4">
                <a-pagination class="pagination" v-model:current="page" :total="totalPages * size" :pageSize="size"
                    @change="fetchChatLieus" show-less-items />
                <a-select v-model:value="size" @change="handlePageSizeChange" :width="120" style="width: 120px">
                    <a-select-option :value="5">5 / trang</a-select-option>
                    <a-select-option :value="10">10 / trang</a-select-option>
                    <a-select-option :value="15">15 / trang</a-select-option>
                </a-select>
            </div>
        </div>
        <!-- Drawer hiển thị thông tin chi tiết và form -->
        <a-modal v-model:open="visible" :footer="null" @cancel="handleModalClose" :forceRender="true"
            :title="isEditMode ? 'Cập nhật chất liệu' : 'Thêm chất liệu mới'" width=" 540px">
            <a-form :model="selectedChatLieu" :rules="rules" ref="formRef" @finish="onSubmit" layout="vertical">
                <a-form-item label="Tên chất liệu" name="tenChatLieu">
                    <a-input v-model:value="selectedChatLieu.tenChatLieu"
                        @input="() => clearBackendError('tenChatLieu')" />
                </a-form-item>
                <a-form-item label="Mô tả" name="moTa">
                    <a-input v-model:value="selectedChatLieu.moTa" @input="() => clearBackendError('moTa')" />
                </a-form-item>
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="selectedChatLieu.trangThai" checked-children="Đang hoạt động"
                        un-checked-children="Ngừng hoạt động" />
                </a-form-item>
                <a-form-item class="d-flex justify-content-end">
                    <a-space>
                        <a-button type="primary" danger @click="handleCancel"><i class="bi bi-x-lg"> Huỷ
                                bỏ</i></a-button>
                        <a-button type="primary" html-type="submit" :loading="loading">
                            <i class="bi bi-floppy"> {{ isEditMode ? ' Cập nhật' : ' Thêm mới' }}</i>
                        </a-button>
                    </a-space>
                </a-form-item>
            </a-form>
        </a-modal>
    </a-layout>
</template>

<script setup>
import { ref, onMounted, computed, h, nextTick } from "vue";
import { getChatLieus, addChatLieu, updateChatLieu, getChatLieuById } from "./api";
import { message, Modal, notification } from "ant-design-vue";
import { EditOutlined } from '@ant-design/icons-vue';
import { useStore } from 'vuex';

const store = useStore();
const isAdmin = computed(() => {
    return store.state.roles.includes('ROLE_QUANLY');
});


// Khai báo các biến trạng thái và dữ liệu
const chatLieus = ref([]);
const totalPages = ref(0);
const page = ref(1);
const size = ref(5);
const keyword = ref("");
const trangThai = ref("");
const visible = ref(false);
const isEditMode = ref(false);
const formRef = ref(null);
const loading = ref(false);
const backendErrors = ref({});


const selectedChatLieu = ref({
    tenChatLieu: "",
    moTa: "",
    trangThai: true
});

const handleCancel = () => {
    formRef.value.resetFields();
    backendErrors.value = {};
    visible.value = false;
};

const handleModalClose = () => {
    formRef.value.resetFields();
    backendErrors.value = {};
    visible.value = false;
};

// Hàm xóa lỗi backend cho field cụ thể
const clearBackendError = (fieldName) => {
    if (backendErrors.value[fieldName]) {
        delete backendErrors.value[fieldName];
        formRef.value?.validateFields([fieldName]);
    }
};

// Các quy tắc validate (rules)
const rules = computed(() => ({
    tenChatLieu: [
        { required: true, message: 'Tên chất liệu không được để trống.' },
        { pattern: /^(?!\s)(?!.*\s$)[\p{L}\s]+$/u, message: 'Tên chỉ được chứa chữ cái và khoảng trắng' },
        { min: 3, message: 'Tên phải có ít nhất 3 ký tự' },
        { max: 100, message: 'Tên không được quá 100 ký tự' },
        {
            validator: async (_, value) => {
                if (backendErrors.value.tenChatLieu) {
                    return Promise.reject(backendErrors.value.tenChatLieu);
                }
                return Promise.resolve();
            }
        }
    ],
    moTa: [
        { required: true, message: 'Mô tả không được để trống.' },
        { min: 5, message: 'Mô tả phải có ít nhất 5 ký tự' },
        { max: 500, message: 'Mô tả không được quá 500 ký tự' },
        {
            validator: async (_, value) => {
                if (backendErrors.value.moTa) {
                    return Promise.reject(backendErrors.value.moTa);
                }
                return Promise.resolve();
            }
        }
    ]
}));

const onClose = () => {
    visible.value = false;
    isEditMode.value = false;

    selectedChatLieu.value = {
        tenChatLieu: "",
        moTa: "",
        trangThai: true
    };

    backendErrors.value = {};
};

// Hiển thị form thêm mới
const showFormAdd = () => {
    nextTick();
    selectedChatLieu.value = { tenChatLieu: "", moTa: "", trangThai: true };
    isEditMode.value = false;
    visible.value = true;
};

// Hiển thị form cập nhật với dữ liệu lấy từ API theo id của record
const showFormUpdate = async (record) => {
    try {
        const response = await getChatLieuById(record.id);
        if (response) {
            nextTick();
            selectedChatLieu.value = { ...response };
            isEditMode.value = true;
            visible.value = true;
        } else {
            message.error("Không tìm thấy chất liệu!");
        }
    } catch (error) {
        message.error('Lỗi tải dữ liệu chất liệu');
    }
};

const handlePageSizeChange = (newSize) => {
    size.value = newSize;
    fetchChatLieus(1);
};

const onTrangThaiChange = (value) => {
    trangThai.value = value;
    fetchChatLieus(1);
};


const columns = [
    { title: "#", dataIndex: "index", key: "index" },
    { title: "Tên Chất Liệu", dataIndex: "tenChatLieu", key: "tenChatLieu" },
    { title: "Mô Tả", dataIndex: "moTa", key: "moTa" },
    { title: "Trạng Thái", key: "trangThai" },
    { title: "Ngày Tạo", key: "ngayTao" },
    { title: "Actions", key: "actions" },
];

let debounceTimer = null;
const onKeywordChange = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
        fetchChatLieus(1);
    }, 300);
};

const fetchChatLieus = async (pageNumber = 1) => {
    // Ép về số và kiểm tra hợp lệ
    const parsedPage = Number(pageNumber);
    if (isNaN(parsedPage)) {
        console.error("Giá trị page không hợp lệ:", pageNumber);
        return;
    }
    page.value = parsedPage;
    const pageIndex = parsedPage - 1; // API nhận trang bắt đầu từ 0

    try {
        const response = await getChatLieus(keyword.value, pageIndex, size.value, trangThai.value);
        if (response && response.content) {
            chatLieus.value = response.content.map((item, index) => ({
                ...item,
                index: index + 1 + (parsedPage - 1) * size.value,
            }));
            totalPages.value = response.page.totalPages;
        } else {
            chatLieus.value = [];
        }
    } catch (error) {
        chatLieus.value = [];
        console.error("Lỗi khi tải danh sách chất liệu:", error);
    }
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

const openNotificationSuccess = (isEditMode) => {
    notification.success({
        message: isEditMode ? 'Cập nhật thành công!' : 'Thêm mới thành công!',
        description: (isEditMode ? 'Cập nhật' : 'Thêm mới') + ' chất liệu thành công!',

    });
};

const openNotificationFail = (isEditMode) => {
    notification.error({
        message: isEditMode ? 'Cập nhật thất bại!' : 'Thêm mới thất bại!',
        description: (isEditMode ? 'Cập nhật' : 'Thêm mới') + ' chất liệu thất bại!',

    });
};

const onSubmit = async () => {
    if (loading.value || !formRef.value) return;
    loading.value = true;

    Modal.confirm({
        title: 'Xác nhận hành động',
        content: 'Bạn có chắc chắn muốn ' + (isEditMode.value ? 'cập nhật' : 'thêm mới') + ' chất liệu này không?',
        okText: 'Xác nhận',
        cancelText: 'Hủy bỏ',
        onOk: async () => {
            try {
                await formRef.value.validate();

                const payload = {
                    ...selectedChatLieu.value,
                    tenChatLieu: selectedChatLieu.value.tenChatLieu.replace(/\s+/g, " ").trim(),
                    moTa: selectedChatLieu.value.moTa.replace(/\s+/g, " ").trim()
                };

                const response = isEditMode.value
                    ? await updateChatLieu(payload.id, { ...payload })
                    : await addChatLieu({ ...payload });

                if (response) {
                    openNotificationSuccess(isEditMode.value);
                    onClose();
                    await fetchChatLieus(page.value);
                }
            } catch (error) {
                if (error.response && error.response.data && error.response.data.errors) {
                    const errorObj = {};
                    error.response.data.errors.forEach(err => {
                        errorObj[err.field] = err.message;
                    });
                    backendErrors.value = errorObj;
                    formRef.value.validateFields();
                } else {
                    openNotificationFail(isEditMode.value);
                }
            } finally {
                loading.value = false;
            }
        },
        onCancel: () => {
            loading.value = false;
            message.info('Hành động đã bị hủy.');
        },
    });
};

onMounted(() => {
    fetchChatLieus(1);
});
</script>
