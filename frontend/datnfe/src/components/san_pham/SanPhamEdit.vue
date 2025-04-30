<template>
    <a-spin v-if="loading" :spinning="true" size="large" class="full-screen-spin">
    </a-spin>
    <a-card :disabled="loading" class="container shadow rounded" style="max-width: 60%;">
        <h5 class="text-center mt-2">CẬP NHẬT SẢN PHẨM</h5>
        <a-form :model="form" :rules="rules" @finish="handleSubmit" ref="formRef" layout="vertical" class="row p-3">
            <a-card class="pt-4 shadow">
                <!-- Mã Áo Dài -->
                <a-form-item label="Mã Áo Dài" name="maAoDai">
                    <a-input v-model:value="form.maAoDai" disabled />
                </a-form-item>
                <!-- Tên Áo Dài -->
                <a-form-item label="Tên Áo Dài" name="tenAoDai">
                    <a-textarea v-model:value="form.tenAoDai" placeholder="Nhập tên áo dài"
                        :auto-size="{ minRows: 1, maxRows: 3 }" @keydown="handleKeyDown"
                        @input="formErrors.tenAoDai = ''" />
                </a-form-item>
                <!-- Trạng thái -->
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="form.trangThai" checked-children="Đang kinh doanh"
                        un-checked-children="Ngừng kinh doanh" />
                </a-form-item>
                <!-- Danh sách ảnh -->
                <a-form-item label="Thêm ảnh mới">
                    <!-- Phần radio group để chọn chế độ -->
                    <a-radio-group v-model:value="imageOption" button-style="solid" class="mb-3">
                        <a-radio-button value="upload">Tải ảnh lên</a-radio-button>
                        <a-radio-button value="link">Nhập link ảnh</a-radio-button>
                    </a-radio-group>
                    <!-- Phần nhận dữ liệu của ảnh -->
                    <a-form-item name="anhList" :rules="rules.anhList" class="mt-3">
                        <div v-if="imageOption === 'link'">
                            <a-input-group compact class="d-flex align-items-center">
                                <a-textarea :disabled="form.anhList.length + currentImages.length >= 5"
                                    :auto-size="{ minRows: 1, maxRows: 3 }" v-model:value="newImageLink"
                                    placeholder="Nhập link ảnh" style="width: 85%;" />
                                <a-button
                                    :disabled="!isImageLinkValid(newImageLink) || form.anhList.length + currentImages.length >= 5"
                                    type="primary" @click="addImageLink">
                                    Thêm link
                                </a-button>
                            </a-input-group>
                            <!-- Danh sách link ảnh -->
                            <a-list bordered :data-source="form.anhList" class="mt-2">
                                <template #renderItem="{ item, index }">
                                    <a-list-item>
                                        <a-image :src="item" width="80px" />
                                        <a :href="item" target="_blank" style="margin-left: 15px;">{{ item }}</a>
                                        <a href="#" @click.prevent="removeImageLink(index)"
                                            style="color: red; margin-right: 8px;">[X]</a>
                                    </a-list-item>
                                </template>
                            </a-list>
                        </div>
                        <div v-else>
                            <a-upload :disabled="uploadedFiles.length + currentImages.length >= 5"
                                v-model:file-list="uploadedFiles" :before-upload="handleBeforeUpload"
                                list-type="picture-card" :multiple="true" accept="image/*" @change="handleUploadChange"
                                :custom-request="dummyRequest">
                                <div>
                                    <plus-outlined />
                                    <div style="margin-top: 8px">Tải ảnh lên</div>
                                </div>
                            </a-upload>
                        </div>
                    </a-form-item>
                    <!-- Danh sách ảnh hiện tại -->
                    <div v-if="currentImages.length > 0" class="mt-4">
                        <div class="font-bold mb-2">Ảnh hiện tại:</div>
                        <div class="current-images-container">
                            <div class="image-row">
                                <div v-for="(img, index) in currentImages" :key="index" class="image-item">
                                    <a-image :src="img" alt="product image" style=" max-width: 105px;" />
                                    <a type="text" danger @click="removeCurrentImage(index)" class="delete-button btn">
                                        <delete-outlined />
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                    <!-- Modal hiển thị ảnh -->
                    <a-modal v-model:open="isModalVisible" :footer="null" @cancel="isModalVisible = false" centered
                        :closable="false" class="custom-modal">
                        <div class="relative">
                            <!-- Nút đóng modal -->
                            <div class="modal-close-button" @click="isModalVisible = false">
                                <close-outlined />
                            </div>
                            <!-- Hình ảnh lớn -->
                            <img :src="selectedImage" alt="Large Image" class="large-image" />
                        </div>
                    </a-modal>
                </a-form-item>
                <div class="row mt-5">
                    <!-- Link Youtube -->
                    <a-form-item label="Link YouTube" name="linkYoutube">
                        <a-textarea :auto-size="{ minRows: 2, maxRows: 5 }" v-model:value="form.linkYoutube"
                            placeholder="Nhập link youtube" />
                    </a-form-item>
                    <!-- Mô tả -->
                    <a-form-item label="Mô tả" name="moTa" :rules="rules.moTa" validate-trigger="change,blur">
                        <RichTextEditor v-model:value="form.moTa" @change="handleRichTextChange"
                            @blur="() => formRef.validateFields(['moTa'])" />
                    </a-form-item>
                    <!-- Loại Áo Dài -->
                    <a-form-item label="Loại áo" name="loaiAoDaiId" class="col-6">
                        <a-select v-model:value="form.loaiAoDaiId" placeholder="Chọn loại áo dài">
                            <a-select-option v-for="loai in loaiAoDais" :key="loai.id" :value="loai.id"
                                :disabled="loai.trangThai === false">
                                {{ loai.tenLoaiAoDai }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                    <!-- Chất liệu -->
                    <a-form-item label="Chất liệu" name="chatLieuId" class="col-6">
                        <a-select v-model:value="form.chatLieuId" placeholder="Chọn chất liệu">
                            <a-select-option v-for="chatLieu in chatLieus" :key="chatLieu.id" :value="chatLieu.id"
                                :disabled="chatLieu.trangThai === false">
                                {{ chatLieu.tenChatLieu }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </div>
                <div class="row">
                    <!-- Tà áo -->
                    <a-form-item label="Tà áo" name="taAoId" class="col-6">
                        <a-select v-model:value="form.taAoId" placeholder="Chọn tà áo">
                            <a-select-option v-for="taAo in taAos" :key="taAo.id" :value="taAo.id"
                                :disabled="taAo.trangThai === false">
                                {{ taAo.tenTaAo }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                    <!-- Nhà cung cấp -->
                    <a-form-item label="Nhà cung cấp" name="nhaCungCapId" class="col-6">
                        <a-select v-model:value="form.nhaCungCapId" placeholder="Chọn nhà cung cấp">
                            <a-select-option v-for="ncc in nhaCungCaps" :key="ncc.id" :value="ncc.id"
                                :disabled="ncc.trangThai === false">
                                {{ ncc.tenNhaCungCap }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </div>
            </a-card>
        </a-form>
        <div class="text-end mt-4 p-5">
            <!-- Nút Cập Nhật -->
            <a-button class="mx-2" type="primary" danger @click="handleCancel">
                <i class="bi bi-x-lg"> Huỷ bỏ</i>
            </a-button>
            <a-button type="primary" html-type="submit" @click="handleSubmit">
                <i class="bi bi-floppy"> Lưu thay đổi</i>
            </a-button>
        </div>
    </a-card>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { PlusOutlined, DeleteOutlined, CloseOutlined } from "@ant-design/icons-vue";
import { message, Modal, notification } from "ant-design-vue";
import { getSanPhamById, updateSanPham, uploadImages } from "../san_pham/api";
import { getLoaiAoDaiAll } from "../loai_ao_dai/api";
import { getChatLieuAll } from "../chat_lieu/api";
import { getTaAoAll } from "../ta_ao/api";
import { getNhaCungCapAll } from "../nha_cung_cap/api";
import RichTextEditor from "./RichTextEditor.vue";

const router = useRouter();
const route = useRoute();
const formRef = ref();
const form = ref({
    tenAoDai: "",
    moTa: "",
    trangThai: true,
    loaiAoDaiId: null,
    chatLieuId: null,
    taAoId: null,
    nhaCungCapId: null,
    linkYoutube: "",
    anhList: [] // Dữ liệu dưới dạng mảng
});
const formErrors = ref({});
const loading = ref(false);
const imageOption = ref("upload");
const uploadedFiles = ref([]);
const loaiAoDais = ref([]);
const chatLieus = ref([]);
const taAos = ref([]);
const nhaCungCaps = ref([]);
const currentImages = ref([]);
const newImageLink = ref(""); // Dùng để nhập link mới

// Biến modal
const isModalVisible = ref(false);
const selectedImage = ref("");

const showImage = (img) => {
    selectedImage.value = img;
    isModalVisible.value = true;
};

const addImageLink = () => {

    if (imageOption.value === 'link') {
        uploadedFiles.value = [];
    }

    for (const item of form.value.anhList) {
        if (item === newImageLink.value) {
            message.error('Link ảnh đã tồn tại!');
            newImageLink.value = "";
            return;
        }
    }
    if (newImageLink.value.trim()) {
        form.value.anhList.push(newImageLink.value.trim());
        newImageLink.value = "";
    }
    formRef.value.validateFields(["anhList"]);
};

const isImageLinkValid = (link) => {
    const regex = /^(https?:\/\/.*\.(?:png|jpg|jpeg|gif|bmp))$/i;
    return regex.test(link);
};

watch(imageOption, (newValue) => {
    uploadedFiles.value = [];
    form.value.anhList = [];
    formRef.value.validateFields(["anhList"]);
});

const removeImageLink = (index) => {
    form.value.anhList.splice(index, 1);
    formRef.value.validateFields(["anhList"]);
};

const handleKeyDown = (event) => {
    if (event.key === "Enter") {
        event.preventDefault();
    }
};

onBeforeUnmount(() => {
    uploadedFiles.value.forEach(file => {
        if (file.url && file.url.startsWith('blob:')) {
            URL.revokeObjectURL(file.url);
        }
    });
});

// Lấy thông tin sản phẩm
const fetchSanPham = async () => {
    try {
        const id = route.params.id;
        const response = await getSanPhamById(id);
        if (response) {
            form.value = {
                maAoDai: response.maAoDai,
                tenAoDai: response.tenAoDai,
                moTa: response.moTa,
                trangThai: response.trangThai,
                loaiAoDaiId: response.loaiAoDaiId,
                linkYoutube: response.linkYoutube,
                chatLieuId: response.chatLieuId,
                taAoId: response.taAoId,
                nhaCungCapId: response.nhaCungCapId,
                anhList: [] // Khởi tạo mảng rỗng cho link mới (sẽ không trùng với currentImages)
            };
            currentImages.value = response.anhList || [];
        }
    } catch (error) {
        console.error("Lỗi khi lấy thông tin sản phẩm:", error);
        message.error("Không thể lấy thông tin sản phẩm");
    }
};

const fetchOptions = async () => {
    try {
        loaiAoDais.value = await getLoaiAoDaiAll();
        chatLieus.value = await getChatLieuAll();
        taAos.value = await getTaAoAll();
        nhaCungCaps.value = await getNhaCungCapAll();
    } catch (error) {
        console.error("Lỗi khi lấy danh sách từ DB:", error);
    }
};

onMounted(() => {
    fetchOptions();
    fetchSanPham();
});

const handleRichTextChange = () => {
    formRef.value.validateFields(['moTa'])
}

const rules = {
    tenAoDai: [
        { required: true, message: "Vui lòng nhập tên áo dài!" },
        { type: 'string', min: 3, max: 100, message: 'Tên phải có từ 3 đến 100 ký tự' },
        { type: 'string', pattern: /^(?!\s)(?!.*\s$)[\p{L}\s]+$/u, message: 'Tên áo dài chỉ được chứa chữ cái và khoảng trắng' },
        {
            validator: async (_, value) => {
                if (formErrors.value.tenAoDai) {
                    return Promise.reject(formErrors.value.tenAoDai);
                }
                return Promise.resolve();
            }
        }
    ],
    linkYoutube: [
        {
            validator: async (_, value) => {
                if (value && !/^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/.test(value)) {
                    return Promise.reject('Link YouTube không hợp lệ!');
                }
                if (formErrors.value.linkYoutube) {
                    return Promise.reject(formErrors.value.linkYoutube);
                }
                return Promise.resolve();
            }
        }
    ],
    moTa: [
        {
            required: true,
            message: 'Vui lòng nhập mô tả',
            validator: async (_, value) => {
                const strippedValue = (value || '')
                    .replace(/<[^>]+>/g, '') // Loại bỏ HTML tags
                    .replace(/&nbsp;/g, ' ') // Thay thế &nbsp;
                    .trim()

                if (!strippedValue) {
                    return Promise.reject('Vui lòng nhập mô tả')
                }
                if (formErrors.value.moTa) {
                    return Promise.reject(formErrors.value.moTa)
                }
                return Promise.resolve()
            }
        }
    ],
    loaiAoDaiId: [
        { required: true, message: "Vui lòng chọn loại áo dài!" },
        {
            validator: async (_, value) => {
                if (formErrors.value.loaiAoDaiId) {
                    return Promise.reject(formErrors.value.loaiAoDaiId);
                }
                return Promise.resolve();
            }
        }
    ],
    chatLieuId: [
        { required: true, message: "Vui lòng chọn chất liệu!" },
        {
            validator: async (_, value) => {
                if (formErrors.value.chatLieuId) {
                    return Promise.reject(formErrors.value.chatLieuId);
                }
                return Promise.resolve();
            }
        }
    ],
    taAoId: [
        { required: true, message: "Vui lòng chọn tà áo!" },
        {
            validator: async (_, value) => {
                if (formErrors.value.taAoId) {
                    return Promise.reject(formErrors.value.taAoId);
                }
                return Promise.resolve();
            }
        }
    ],
    nhaCungCapId: [
        { required: true, message: "Vui lòng chọn nhà cung cấp!" },
        {
            validator: async (_, value) => {
                if (formErrors.value.nhaCungCapId) {
                    return Promise.reject(formErrors.value.nhaCungCapId);
                }
                return Promise.resolve();
            }
        }
    ],
    anhList: [
        {
            validator: async () => {
                const total =
                    currentImages.value.length +
                    (imageOption.value === "link" ? form.value.anhList.length : 0) +
                    uploadedFiles.value.length;

                if (total === 0) return Promise.reject("Vui lòng thêm ít nhất 1 ảnh!");
                if (total > 5) return Promise.reject("Chỉ được tối đa 5 ảnh!");
                return Promise.resolve();
            },
            trigger: ["change", "blur"]
        },
        {
            validator: async (_, value) => {
                if (formErrors.value.anhList) {
                    return Promise.reject(formErrors.value.anhList);
                }
                return Promise.resolve();
            }
        }
    ]
};

const handleBeforeUpload = (file) => {
    const isValidType = ["image/jpeg", "image/png"].includes(file.type);
    const isLt2M = file.size / 1024 / 1024 < 2;

    if (!isValidType) {
        message.error("Chỉ chấp nhận ảnh JPG hoặc PNG.");
        return false;
    }

    if (!isLt2M) {
        message.error("Ảnh phải nhỏ hơn 2MB!");
        return false;
    }

    return true;
};

const removeCurrentImage = (index) => {
    Modal.confirm({
        title: "Xác nhận xoá ảnh.",
        content: "Ảnh sẽ bị xoá khỏi sản phẩm khi cập nhật thành công.",
        okText: "Xác nhận",
        okType: "danger",
        cancelText: "Huỷ bỏ",
        onOk() {
            currentImages.value.splice(index, 1);
            message.success('Đã xóa ảnh ảnh sản phẩm!');
            formRef.value.validateFields(["anhList"]);
        }
    });
};

// Thay đổi hàm dummyRequest
const dummyRequest = async ({ file, onProgress, onSuccess, onError }) => {
    if (imageOption.value === "link") {
        uploadedFiles.value = [];
    }
    try {
        // Giả lập tiến trình upload
        onProgress({ percent: 0 });
        await new Promise(resolve => setTimeout(resolve, 500));
        onProgress({ percent: 100 });

        // Thêm file vào danh sách khi hoàn thành
        uploadedFiles.value = [
            ...uploadedFiles.value,
            {
                uid: file.uid,
                name: file.name,
                status: "done",
                url: URL.createObjectURL(file),
                originFileObj: file
            }
        ];
        formRef.value.validateFields(["anhList"]);
        onSuccess();
    } catch (error) {
        onError(error);
    }
};

const handleUploadChange = ({ file, fileList }) => {
    uploadedFiles.value = fileList
        .filter(f => f.status === 'done')
        .map(f => ({
            ...f,
            url: f.url || URL.createObjectURL(f.originFileObj)
        }));
};

const hasFormChanged = async () => {
    const id = route.params.id;
    try {
        const response = await getSanPhamById(id);
        const originalForm = {
            maAoDai: response.maAoDai,
            tenAoDai: response.tenAoDai,
            moTa: response.moTa,
            trangThai: response.trangThai,
            loaiAoDaiId: response.loaiAoDaiId,
            linkYoutube: response.linkYoutube,
            chatLieuId: response.chatLieuId,
            taAoId: response.taAoId,
            nhaCungCapId: response.nhaCungCapId
        };
        const fieldsChanged = Object.keys(originalForm).some(
            key => String(originalForm[key]) !== String(form.value[key])
        );
        const originalImages = response.anhList || [];
        const imagesChangedFromRemovalOrModification =
            JSON.stringify(originalImages) !== JSON.stringify(currentImages.value);
        let newImagesAdded = false;
        if (imageOption.value === "link" && form.value.anhList.length > 0) {
            newImagesAdded = true;
        }
        if (imageOption.value === "upload" && uploadedFiles.value.length > 0) {
            newImagesAdded = true;
        }
        const imagesChanged = imagesChangedFromRemovalOrModification || newImagesAdded;
        return fieldsChanged || imagesChanged;
    } catch (error) {
        console.error("Lỗi khi lấy dữ liệu sản phẩm:", error);
        return false;
    }
};

const handleCancel = async () => {
    const formChanged = await hasFormChanged();
    if (formChanged) {
        Modal.confirm({
            title: "Xác nhận huỷ",
            content: "Bạn có chắc chắn muốn huỷ cập nhật? Tất cả các thay đổi sẽ không được lưu!",
            okText: "Xác nhận",
            cancelText: "Huỷ bỏ",
            onOk() {
                router.push("/san-pham/list/all");
                message.info("Đã huỷ thao tác cập nhật.");
            },
            onCancel() {
                message.info("Tiếp tục chỉnh sửa.");
            }
        });
    } else {
        router.push("/san-pham/list/all");
    }
};

const handleSubmit = async () => {
    if (!(await hasFormChanged())) {
        router.push("/san-pham/list/all");
        return;
    }

    Modal.confirm({
        title: "Xác nhận cập nhật",
        content: "Bạn có chắc chắn muốn cập nhật? Tất cả thay đổi sẽ được lưu!",
        okText: "Xác nhận",
        cancelText: "Huỷ bỏ",
        onOk: async () => {
            try {
                await formRef.value.validate();
                loading.value = true;
                let anhList = [...currentImages.value];

                if (imageOption.value === "link" && form.value.anhList.length > 0) {
                    anhList = [...currentImages.value, ...form.value.anhList];
                }
                if (imageOption.value === "upload" && uploadedFiles.value.length > 0) {
                    const uploadPromises = uploadedFiles.value
                        .filter(f => f.originFileObj)
                        .map(file => uploadImages(file.originFileObj));

                    const uploadResponse = [];
                    for (const promise of uploadPromises) {
                        const result = await promise;
                        uploadResponse.push(result);
                        await new Promise((resolve) => setTimeout(resolve, 500));
                    }

                    anhList = [...currentImages.value, ...uploadResponse];
                }
                const data = { ...form.value, anhList };
                await updateSanPham(route.params.id, data);

                await new Promise(resolve => setTimeout(resolve, 1000));

                notification.success({
                    message: "Thành công",
                    description: "Sản phẩm đã được cập nhật thành công."
                });
                router.push("/san-pham/list/all");
            } catch (error) {
                if (error.response?.data?.errors) {
                    formErrors.value = error.response.data.errors;
                    try {
                        // Validate lại các trường bị lỗi
                        await formRef.value.validate([
                            'tenAoDai',
                            'moTa',
                            'linkYoutube',
                            'loaiAoDaiId',
                            'chatLieuId',
                            'taAoId',
                            'nhaCungCapId',
                            'anhList',
                        ]);
                    } catch (validateError) {
                        message.error("Vui lòng kiểm tra lại thông tin sản phẩm!");
                        console.error("Lỗi xác thực dữ liệu:", validateError);
                    }
                } else {
                    // Hiển thị thông báo lỗi chung
                    message.error(error.message || 'Cập nhật sản phẩm thất bại');
                }
            } finally {
                loading.value = false;
            }
        },
        onCancel() {
            fetchOptions();
            fetchSanPham();
            message.info("Đã hủy thao tác cập nhật.");
        }
    });
};
</script>

<style scoped>
.current-images-container {
    margin-top: 16px;
}

.image-row {
    display: flex;
    flex-wrap: wrap;
}

.image {
    cursor: pointer;
    transition: transform 0.2s ease-in-out;
}

.image:hover {
    transform: scale(1.01);
}

.large-image {
    width: 100%;
    height: auto;
    display: block;
    margin: 0 auto;
    border-radius: 5px;
}

.image-item {
    position: relative;
    width: 20%;
    transition: transform 0.2s ease-in-out;
}

.image {
    width: 100%;
    height: auto;
    border-radius: 4px;
}


.delete-button {
    position: absolute;
    top: 10%;
    right: 17%;
    transform: translateY(-50%);
    background-color: rgba(255, 255, 255, 0.9);
    color: red;
    width: 22px;
    height: 22px;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
}

.delete-button:hover {
    background-color: rgba(255, 220, 220, 0.7);
    scale: 1.1;

}

.close-outlined {
    font-size: 20px;
    color: #000;
}

.full-screen-spin {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.65);
    z-index: 9999;
}
</style>