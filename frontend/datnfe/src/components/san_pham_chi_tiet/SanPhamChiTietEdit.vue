<template>
    <a-spin v-if="submitting" :spinning="true" size="large" class="full-screen-spin">
    </a-spin>
    <a-layout :disabled="submitting" class="container rounded" style="width: 60%">
        <h5 class="text-center mb-4 mt-4">CHỈNH SỬA BIẾN THỂ SẢN PHẨM</h5>
        <a-card class="shadow px-5 mx-5 p pt-5 mt-3">
            <a-form :model="formState" :rules="rules" @finish="handleSubmit" layout="vertical" ref="formRef">
                <a-form-item label="Sản phẩm" name="aoDaiId" required>
                    <a-input v-model:value="productName" disabled />
                </a-form-item>

                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="formState.trangThai" checked-children="Đang kinh doanh"
                        un-checked-children="Ngừng kinh doanh" />
                </a-form-item>

                <div class="row">
                    <a-form-item class="col-6" label="Màu sắc" name="mauSacId" required>
                        <a-select v-model:value="formState.mauSacId" placeholder="Chọn màu sắc" disabled>
                            <a-select-option v-for="color in colors" :key="color.id" :value="color.id">
                                {{ color.tenMauSac }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>

                    <a-form-item class="col-6" label="Kích thước" name="kichThuocId" required>
                        <a-select v-model:value="formState.kichThuocId" placeholder="Chọn kích thước" disabled>
                            <a-select-option v-for="size in sizes" :key="size.id" :value="size.id">
                                {{ size.tenKichThuoc }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </div>

                <div class="row">
                    <div class="col-6">
                        <a-form-item label="Giá bán" name="giaGoc" :rules="rules.giaGoc">
                            <a-input-number v-model:value="formState.giaGoc" class="w-100"
                                :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')">
                                <template #addonAfter>₫</template>
                            </a-input-number>
                        </a-form-item>
                    </div>
                    <div class="col-6">
                        <a-form-item label="Số lượng" name="soLuong" :rules="rules.soLuong">
                            <a-input-number v-model:value="formState.soLuong" class="w-100">
                            </a-input-number>
                        </a-form-item>
                    </div>
                </div>

                <!-- Image Upload Options -->
                <a-form-item label="Ảnh sản phẩm">
                    <a-radio-group v-model:value="imageOption" @change="handleImageOptionChange" button-style="solid">
                        <a-radio-button value="upload">Tải ảnh lên</a-radio-button>
                        <a-radio-button value="link">Nhập link ảnh</a-radio-button>

                    </a-radio-group>
                </a-form-item>

                <!-- Image URL or Upload Section -->
                <a-form-item name="anhUrl" :rules="rules.anhUrl">
                    <div v-if="imageOption === 'upload'">
                        <a-upload :disabled="!!currentImageUrl" v-model:file-list="uploadedFiles"
                            list-type="picture-card" :multiple="false" accept="image/*" :max-count="1"
                            :before-upload="handleBeforeUpload" @preview="handlePreview" :custom-request="dummyRequest"
                            @remove="handleRemoveFile">
                            <div v-if="uploadedFiles.length === 0">
                                <plus-outlined />
                                <div style="margin-top: 8px">Tải ảnh lên</div>
                            </div>
                        </a-upload>
                        <!-- Modal hiển thị ảnh -->
                        <a-modal v-model:open="previewVisible" title="Xem trước ảnh" class="mt-3" :footer="null"
                            @cancel="handleCancelPreview">
                            <img alt="Preview Image" style="width: 100%"
                                :src="previewImage || 'https://placehold.jp/800x1200.png'" />
                        </a-modal>
                    </div>
                    <div v-else>
                        <a-textarea :disabled="!!currentImageUrl" v-model:value="formState.anhUrl"
                            :auto-size="{ minRows: 2, maxRows: 5 }" placeholder="Nhập URL ảnh" />
                        <a-image :src="formState.anhUrl" :width="100" class=" mt-3 rounded"></a-image>
                    </div>
                </a-form-item>

                <!-- Display Current Image and Option to Remove -->
                <a-form-item label="Ảnh hiện tại">
                    <a-form-item class="d-flex align-items-center justify-content-start">
                        <a-image v-if="currentImageUrl" :src="currentImageUrl"
                            style="max-width: 100px; object-fit: contain; position: relative;" />
                        <a v-if="currentImageUrl" type="link" @click="removeCurrentImage" class="delete-button btn">
                            <DeleteOutlined />
                        </a>
                    </a-form-item>
                </a-form-item>
                <div class="d-flex justify-content-end my-5 py-3">
                    <button type="button" class="mx-2 btn btn-danger" @click="handleCancel"><i class="bi bi-x-lg"> Huỷ
                            bỏ</i>
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-floppy"> Lưu thay đổi</i>
                    </button>
                </div>
            </a-form>
        </a-card>
    </a-layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Modal, notification } from 'ant-design-vue';
import { getSanPhamChiTietById, updateSanPhamChiTiet } from './api';
import { getMauSacsAll } from '../mau_sac/api';
import { getKichThuocsAll } from '../kich_thuoc/api';
import { PlusOutlined, DeleteOutlined } from '@ant-design/icons-vue';
import { uploadImages } from '../san_pham/api';

const route = useRoute();
const router = useRouter();
const submitting = ref(false);
const productName = ref('');
const colors = ref([]);
const sizes = ref([]);
const imageOption = ref('upload');
const uploadedFiles = ref([]);
const formRef = ref(null);
const currentImageUrl = ref('');
const previewVisible = ref(false);
const previewImage = ref('');
const formState = reactive({});

const rules = {
    mauSacId: [{ required: true, message: 'Vui lòng chọn màu sắc' }],
    kichThuocId: [{ required: true, message: 'Vui lòng chọn kích thước' }],
    giaGoc: [
        {
            validator: (_, value) => {
                if (value === null || value === undefined || value === '') {
                    return Promise.reject('Vui lòng nhập giá bán');
                }
                if (value <= 0) {
                    return Promise.reject('Giá bán phải > 0');
                }
                return Promise.resolve();
            },
            trigger: ['change', 'input']
        }
    ],
    soLuong: [
        {
            validator: (_, value) => {
                if (value === null || value === undefined || value === '') {
                    return Promise.reject('Vui lòng nhập số lượng');
                }
                if (value < 0) {
                    return Promise.reject('Số lượng phải >= 0');
                }
                return Promise.resolve();
            },
            trigger: ['change', 'input']
        }
    ],
    anhUrl: [{
        required: true,
        validator: () => {
            if (imageOption.value === 'link' && !formState.anhUrl && !currentImageUrl.value) {
                return Promise.reject('Vui lòng nhập URL ảnh');
            }
            if (imageOption.value === 'upload' && uploadedFiles.value.length === 0 && !currentImageUrl.value) {
                return Promise.reject('Vui lòng tải ảnh lên');
            }
            if (currentImageUrl.value && uploadedFiles.value.length > 0) {
                return Promise.reject('Chỉ được phép tải lên 1 ảnh. Vui lòng xoá ảnh cũ trước khi tải ảnh mới.');
            }
            if (currentImageUrl.value && formState.anhUrl) {
                return Promise.reject('Chỉ được phép tải lên 1 ảnh. Vui lòng xoá ảnh cũ trước khi nhập link ảnh mới.');
            }
            if (imageOption.value === 'link' && formState.anhUrl && !isImageUrl(formState.anhUrl)) {
                return Promise.reject('URL ảnh không hợp lệ. Vui lòng nhập lại');
            }
            return Promise.resolve();
        }
    }]
};

const isImageUrl = (url) => {
    return /\.(jpg|jpeg|png|gif|bmp|webp|svg)$/.test(url);
};

const handleImageOptionChange = () => {
    formState.anhUrl = '';
    uploadedFiles.value = [];
    formRef.value.validateFields(['anhUrl']);
};

const removeCurrentImage = () => {
    Modal.confirm({
        title: 'Xác nhận xóa ảnh',
        content: 'Bạn có chắc chắn muốn xóa ảnh hiện tại?',
        okText: 'Xác nhận',
        cancelText: 'Hủy bỏ',
        okType: 'danger',
        onOk() {
            currentImageUrl.value = '';
            if (formRef.value) {
                formRef.value.validateFields(['anhUrl']);
            }
            message.success('Đã xóa ảnh hiện tại');
        }
    });
};


onMounted(async () => {
    try {
        const [product, colorRes, sizeRes] = await Promise.all([
            getSanPhamChiTietById(route.params.id),
            getMauSacsAll(),
            getKichThuocsAll()
        ]);
        Object.assign(formState, { ...product, anhUrl: '' });
        productName.value = `${product.maAoDai} - ${product.tenAoDai}`;
        colors.value = colorRes;
        sizes.value = sizeRes;

        currentImageUrl.value = product.anhUrl;
    } catch (error) {
        message.error('Lỗi tải dữ liệu');
    }
});

const hasChangeData = async () => {
    const id = route.params.id;
    const responseData = await getSanPhamChiTietById(id);

    const formStateCopy = JSON.parse(JSON.stringify(formState));
    // Sử dụng tên file hiện tại từ currentImageUrl
    formStateCopy.anhUrl = currentImageUrl.value;

    return (
        formStateCopy.aoDaiId !== responseData.aoDaiId ||
        formStateCopy.mauSacId !== responseData.mauSacId ||
        formStateCopy.kichThuocId !== responseData.kichThuocId ||
        formStateCopy.giaGoc !== responseData.giaGoc ||
        formStateCopy.soLuong !== responseData.soLuong ||
        formStateCopy.anhUrl !== responseData.anhUrl ||
        formStateCopy.trangThai !== responseData.trangThai ||
        uploadedFiles.value.length > 0
    );
};

const handleCancel = async () => {
    if (!await hasChangeData()) {
        router.push('/san-pham-chi-tiet/list/all');
        return;
    }

    Modal.confirm({
        title: 'Xác nhận hủy',
        content: 'Bạn có chắc chắn muốn hủy cập nhật? Tất cả thay đổi sẽ không được lưu!',
        okText: 'Xác nhận',
        cancelText: 'Hủy bỏ',
        onOk() {
            router.push('/san-pham-chi-tiet/list/all');
            message.info('Đã huỷ thao tác cập nhật');
        },
        onCancel() {
            message.info('Tiếp tục chỉnh sửa');
        }
    });
};

const handleBeforeUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
        message.error('Chỉ chấp nhận file ảnh!');
        uploadedFiles.value = [];
        return false;
    }
    return true;
};

const dummyRequest = ({ file, onSuccess }) => {
    uploadedFiles.value = [
        {
            uid: file.uid,
            name: file.name,
            status: 'done',
            originFileObj: file
        }
    ];
    onSuccess("ok");
};

const handleRemoveFile = (file) => {
    uploadedFiles.value = uploadedFiles.value.filter(f => f.uid !== file.uid);
    formState.anhUrl = '';
    formRef.value.validateFields(['anhUrl']);
};

const handleCancelPreview = () => {
    previewVisible.value = false;
};

const getBase64 = (file) => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = (error) => reject(error);
    });
};

const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj);
    }
    previewImage.value = file.url || file.preview;
    previewVisible.value = true;
};

const handleSubmit = async () => {
    if (!await hasChangeData()) {
        router.push('/san-pham-chi-tiet/list/all');
        return;
    }
    try {
        await formRef.value.validateFields();

        Modal.confirm({
            title: 'Xác nhận cập nhật',
            content: 'Bạn có chắc chắn muốn cập nhật? Tất cả thay đổi sẽ được lưu!',
            okText: 'Xác nhận',
            cancelText: 'Hủy bỏ',
            async onOk() {
                submitting.value = true;
                if (imageOption.value === 'upload' && uploadedFiles.value.length > 0) {
                    const uploadResponses = await Promise.all(
                        uploadedFiles.value.map(file =>
                            uploadImages(file.originFileObj)
                        )
                    );
                    formState.anhUrl = uploadResponses[0];
                }

                if (imageOption.value === 'link' && formState.anhUrl) {
                    formState.anhUrl = formState.anhUrl;
                }

                if (!formState.anhUrl && currentImageUrl.value) {
                    formState.anhUrl = currentImageUrl.value;
                }

                const response = await updateSanPhamChiTiet(route.params.id, formState);

                await new Promise(resolve => setTimeout(resolve, 1000));

                if (response) {
                    notification.success({
                        message: 'Thành công',
                        description: `Cập nhật biến thể thành công!`
                    });
                }
                router.push('/san-pham-chi-tiet/list/all');
            },
            onCancel() {
                message.info('Đã huỷ thao tác cập nhật');
            }
        });
    } catch (error) {
        if (error.errorFields) {
            message.error('Vui lòng kiểm tra lại thông tin biến thể!');
        } else {
            message.error('Có lỗi xảy ra: ' + error.message);
            console.log(error);
        }
    } finally {
        submitting.value = false;
    }
};
</script>

<style scoped>
.ant-upload-list-item-thumbnail {
    width: 150px;
    height: auto;
}

.delete-button {
    position: absolute;
    top: 10%;
    right: 2%;
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
