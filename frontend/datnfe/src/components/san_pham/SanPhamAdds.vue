<template>
    <a-spin v-if="submitting" :spinning="true" size="large" class="full-screen-spin">
    </a-spin>
    <a-form :disabled="submitting" ref="formRef" :model="formState" @finish="handleSubmit" :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }" class="container border rounded p-5 shadow" style="width: 80%;">
        <h4 class="text-primary text-center">THÊM MỚI SẢN PHẨM</h4>
        <!-- Thông tin cơ bản -->
        <a-card class="my-4 shadow" title="Thông tin cơ bản" style="margin-bottom: 20px">
            <a-form-item label="Tên áo dài" name="tenAoDai" :rules="rules.tenAoDai">
                <a-textarea v-model:value="formState.tenAoDai" placeholder="Nhập tên áo dài"
                    :auto-size="{ minRows: 1, maxRows: 3 }" @input="formErrors.tenAoDai = ''"
                    @keydown="handleKeyDown" />
            </a-form-item>

            <!-- Đưa radio group ra ngoài Form.Item -->
            <a-form-item label="Hình ảnh sản phẩm">
                <!-- Radio group không cần thu thập dữ liệu -->
                <a-form-item-rest>
                    <a-radio-group v-model:value="uploadOption" button-style="solid" class="mb-4">
                        <a-radio-button value="upload">Tải lên</a-radio-button>
                        <a-radio-button value="link">Nhập link</a-radio-button>
                    </a-radio-group>
                </a-form-item-rest>

                <!-- Form.Item riêng để thu thập dữ liệu của ảnh (upload hoặc link) -->
                <a-form-item name="anhList" :rules="rules.anhList" validateTrigger="change">
                    <div v-if="uploadOption === 'upload'">
                        <a-upload v-model:file-list="fileList" list-type="picture-card"
                            :custom-request="handleMainImageUpload" @preview="handlePreview"
                            :before-upload="beforeUpload" accept="image/*" @input="formErrors.anhList = ''">
                            <div v-if="fileList.length < 5">
                                <plus-outlined />
                                <div style="margin-top: 8px">Upload</div>
                            </div>
                        </a-upload>
                        <a-modal :open="previewVisible" :title="previewTitle" :footer="null" @cancel="handleCancel">
                            <img alt="example" style="width: 100%" :src="previewImage" />
                        </a-modal>
                    </div>

                    <div v-else>
                        <a-input-group compact class="d-flex align-items-center">
                            <a-textarea :auto-size="{ minRows: 1, maxRows: 3 }" v-model:value="newImageLink"
                                placeholder="Nhập link ảnh" @input="formErrors.anhList = ''"
                                :disabled="formState.anhList.length >= 5" alow-clear />
                            <a-button type="primary" @click="addImageLink" :disabled="formState.anhList.length >= 5 || !newImageLink || newImageLink.trim() === ''
                                || !isValidImageUrl(newImageLink)">
                                Thêm link
                            </a-button>
                        </a-input-group>

                        <a-list bordered :data-source="formState.anhList" class="mt-2">
                            <template #renderItem="{ item, index }">
                                <a-list-item>
                                    <a-image :src="item" width="80px" />
                                    <a :href="item" target="_blank" style="margin-left: 15px;">{{ item }}</a>
                                    <a href="#" @click.prevent="removeImageLink(index)"
                                        style="color: red; margin-right: 8px;" class="ms-2">[X]</a>

                                </a-list-item>
                            </template>
                        </a-list>
                    </div>
                </a-form-item>
            </a-form-item>

            <a-form-item label="Link YouTube" name="linkYoutube" :rules="rules.linkYoutube">
                <a-textarea v-model:value="formState.linkYoutube" placeholder="Nhập link youtube"
                    :auto-size="{ minRows: 1, maxRows: 3 }" />
            </a-form-item>
            <a-form-item label="Mô tả" name="moTa" :rules="rules.moTa" validate-trigger="change,blur">
                <RichTextEditor v-model:value="formState.moTa" @change="handleRichTextChange"
                    @blur="() => formRef.validateFields(['moTa'])" />
            </a-form-item>
        </a-card>
        <!-- Thông tin chi tiết -->
        <a-card class="my-4 shadow" title="Thông tin chi tiết" style="margin-bottom: 20px">
            <a-row :gutter="16">
                <a-col :span="12">
                    <a-form-item label="Loại áo" name="loaiAoDaiId" :rules="rules.loaiAoDaiId">
                        <a-select v-model:value="formState.loaiAoDaiId" :loading="loadingOptions" allowClear
                            placeholder="Chọn loại áo dài" @input="formErrors.loaiAoDaiId = ''">
                            <a-select-option v-for="item in loaiAoDaiOptions" :key="item.id" :value="item.id">
                                {{ item.tenLoaiAoDai }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="Chất liệu" name="chatLieuId" :rules="rules.chatLieuId">
                        <a-select v-model:value="formState.chatLieuId" :loading="loadingOptions" allowClear
                            placeholder="Chọn chất liệu" @input="formErrors.chatLieuId = ''">
                            <a-select-option v-for="item in chatLieuOptions" :key="item.id" :value="item.id">
                                {{ item.tenChatLieu }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row :gutter="16">
                <a-col :span="12">
                    <a-form-item label="Tà áo" name="taAoId" :rules="rules.taAoId">
                        <a-select v-model:value="formState.taAoId" :loading="loadingOptions" allowClear
                            placeholder="Chọn tà áo" @input="formErrors.taAoId = ''">
                            <a-select-option v-for="item in taAoOptions" :key="item.id" :value="item.id">
                                {{ item.tenTaAo }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item label="Nhà Cung Cấp" name="nhaCungCapId" :rules="rules.nhaCungCapId">
                        <a-select v-model:value="formState.nhaCungCapId" :loading="loadingOptions" allowClear
                            placeholder="Chọn nhà cung cấp" @input="formErrors.nhaCungCapId = ''">
                            <a-select-option v-for="item in nhaCungCapOptions" :key="item.id" :value="item.id">
                                {{ item.tenNhaCungCap }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </a-col>
            </a-row>
        </a-card>
        <!-- Thông tin biến thể -->

        <ProductVariants v-model:variants="formState.sanPhamChiTietList" :mauSacOptions="mauSacOptions"
            :kichThuocOptions="kichThuocOptions" :formRef="formRef" />
    </a-form>

    <!-- Nút lưu ở cuối trang -->
    <div class="py-5">
        <div class="container d-flex justify-content-end gap-2" style="width: 80%;">
            <button type="button" class="btn btn-danger" @click="handleCancelForm">
                <i class="bi bi-x-lg"> Huỷ bỏ</i>
            </button>
            <button type="button" class="btn btn-secondary" @click="handleSaveAndHide">
                <i class="bi bi-eye-slash"> Lưu & Ẩn</i>
            </button>
            <button type="button" class="btn btn-primary" @click="handleSaveAndShow">
                <i class="bi bi-eye"> Lưu & Hiển thị</i>
            </button>
        </div>
    </div>

</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { message, Modal, notification } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';

// Import các API từ module riêng của bạn
import { getMauSacsEn } from '../mau_sac/api';
import { getKichThuocsEn } from '../kich_thuoc/api';
import { getLoaiAoDaisEn } from '../loai_ao_dai/api';
import { getChatLieusEn } from '../chat_lieu/api';
import { getTaAosEn } from '../ta_ao/api';
import { getNhaCungCapsEn } from '../nha_cung_cap/api';
import { uploadImages, addSanPham } from '../san_pham/api';
import { useRouter } from "vue-router";
import ProductVariants from '../san_pham/SanPhamChiTietComponent.vue';
import RichTextEditor from './RichTextEditor.vue';

const router = useRouter();
// --- Form chính ---
const formState = reactive({
    tenAoDai: '',
    moTa: '',
    linkYoutube: '',
    trangThai: true,
    loaiAoDaiId: null,
    chatLieuId: null,
    taAoId: null,
    nhaCungCapId: null,
    anhList: [],
    sanPhamChiTietList: [] // Mảng biến thể
});

// --- Options ---
const loaiAoDaiOptions = ref([]);
const chatLieuOptions = ref([]);
const taAoOptions = ref([]);
const nhaCungCapOptions = ref([]);
const mauSacOptions = ref([]);
const kichThuocOptions = ref([]);
const loadingOptions = ref(true);
const uploadOption = ref('upload'); // Mặc định là tải lên
// --- File Upload & Preview ---
const fileList = ref([]);
const previewVisible = ref(false);
const previewImage = ref('');
const previewTitle = ref('');
const newImageLink = ref('');
let initialFormStateClone = null;

const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
    }
};

const isValidImageUrl = (url) => {
    try {
        new URL(url);
        return /\.(jpeg|jpg|gif|png|webp)(\?.*)?$/i.test(url);
    } catch (e) {
        return false;
    }
};

const addImageLink = () => {
    if (uploadOption.value === 'link') {
        fileList.value = [];
    }
    const link = newImageLink.value.trim();
    if (!link) {
        message.error('Vui lòng nhập link ảnh');
        return;
    }
    if (!isValidImageUrl(link)) {
        message.error('Link ảnh không hợp lệ! Vui lòng nhập URL ảnh (JPEG, JPG, PNG, GIF)');
        return;
    }
    for (const item of formState.anhList) {
        if (item === link) {
            message.error('Link ảnh đã tồn tại!');
            return;
        }
    }

    formState.anhList.push(link);
    newImageLink.value = '';
    formRef.value.validateFields('anhList');
};

const removeImageLink = (index) => {
    formState.anhList.splice(index, 1);
    formRef.value.validateFields('anhList');
};

watch(uploadOption, (newVal, oldVal) => {
    // Reset toàn bộ dữ liệu ảnh khi chuyển đổi chế độ
    formState.anhList = [];
    fileList.value = [];
    newImageLink.value = '';
    // Clear validation errors
    formRef.value.clearValidate('anhList');
});

const formErrors = ref({});
const handleRichTextChange = () => {
    formRef.value.validateFields(['moTa'])
}
const rules = computed(() => ({
    tenAoDai: [
        { required: true, message: 'Vui lòng nhập tên áo dài' },
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
    anhList: [
        {
            validator: async (_rule, value) => {
                if (uploadOption.value === 'upload' && fileList.value.length === 0) {
                    throw new Error('Vui lòng tải lên ít nhất 1 ảnh');
                }
                if (uploadOption.value === 'link' && formState.anhList.length === 0 && !newImageLink.value) {
                    throw new Error('Vui lòng nhập ít nhất 1 link ảnh');
                }
            }
        },
        {
            validator: async (_, value) => {
                if (formErrors.value.anhList) {
                    return Promise.reject(formErrors.value.anhList);
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
    linkYoutube: [
        { pattern: /^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/, message: 'Link YouTube không hợp lệ' }
    ],
    loaiAoDaiId: [
        { required: true, message: 'Vui lòng chọn loại áo dài' },
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
        { required: true, message: 'Vui lòng chọn chất liệu' },
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
        { required: true, message: 'Vui lòng chọn tà áo' },
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
        { required: true, message: 'Vui lòng chọn nhà cung cấp' },
        {
            validator: async (_, value) => {
                if (formErrors.value.nhaCungCapId) {
                    return Promise.reject(formErrors.value.nhaCungCapId);
                }
                return Promise.resolve();
            }
        }
    ]
}));

// --- Fetch options on mount ---
onMounted(async () => {
    initialFormStateClone = JSON.stringify(getComparableFormState());
    try {
        const [mauSacs, kichThuocs, loaiAoDais, chatLieus, taAos, nhaCungCaps] = await Promise.all([
            getMauSacsEn(),
            getKichThuocsEn(),
            getLoaiAoDaisEn(),
            getChatLieusEn(),
            getTaAosEn(),
            getNhaCungCapsEn()
        ]);
        mauSacOptions.value = mauSacs;
        kichThuocOptions.value = kichThuocs;
        loaiAoDaiOptions.value = loaiAoDais;
        chatLieuOptions.value = chatLieus;
        taAoOptions.value = taAos;
        nhaCungCapOptions.value = nhaCungCaps;
    } catch (error) {
        message.error('Lỗi tải dữ liệu danh mục');
    } finally {
        loadingOptions.value = false;
    }
});

const getComparableFormState = () => {
    return {
        tenAoDai: formState.tenAoDai,
        moTa: formState.moTa,
        linkYoutube: formState.linkYoutube,
        loaiAoDaiId: formState.loaiAoDaiId,
        chatLieuId: formState.chatLieuId,
        taAoId: formState.taAoId,
        nhaCungCapId: formState.nhaCungCapId,
        // Đối với anhList, chỉ lấy uid, name, size (nếu có)
        anhList: Array.isArray(formState.anhList)
            ? formState.anhList.map(file => ({
                uid: file.uid,
                name: file.name,
                size: file.size
            }))
            : [],
        // Đối với sanPhamChiTietList, chuyển đổi từng variant
        sanPhamChiTietList: formState.sanPhamChiTietList.map(variant => ({
            mauSac: variant.mauSac,
            kichThuoc: variant.kichThuoc,
            giaGoc: variant.giaGoc,
            // giaBan: variant.giaBan,
            soLuong: variant.soLuong,
            // Với fileList của mỗi variant, chuyển đổi tương tự
            fileList: Array.isArray(variant.fileList)
                ? variant.fileList.map(file => ({
                    uid: file.uid,
                    name: file.name,
                    size: file.size
                }))
                : []
        }))
    };
};

const hasFormChange = () => {
    return JSON.stringify(getComparableFormState()) !== initialFormStateClone;
};

const submitting = ref(false);
const formRef = ref(null);
const submissionType = ref('show');

const handleSaveAndShow = async () => {
    submissionType.value = 'show';
    try {
        await formRef.value.validate();
        handleSubmit();
    } catch (error) {
        console.log("Validation failed", error);
    }
};

const handleSaveAndHide = async () => {
    submissionType.value = 'hide';
    try {
        await formRef.value.validate();
        handleSubmit();
    } catch (error) {
        console.log("Validation failed", error);
    }
};

const handleCancelForm = () => {
    if (!hasFormChange()) {
        router.push('/san-pham/list/all');
    } else {
        // Nếu có thay đổi, hiển thị Modal xác nhận
        Modal.confirm({
            title: 'Xác nhận hủy bỏ thay đổi',
            content: 'Bạn có chắc chắn muốn hủy bỏ các thay đổi? Dữ liệu sẽ không được lưu lại.',
            okText: 'Xác nhận',
            cancelText: 'Huỷ bỏ',
            onOk() {
                router.push('/san-pham/list/all');
            },
            onCancel() {

            }
        });
    }
};

const openNotification = () => {
    notification.success({
        message: 'Thành công!',
        description:
            'Sản phẩm và biến thể được lưu lại!',
    });
};

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = (error) => reject(error);
    });
}
const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj);
    }
    previewImage.value = file.url || file.preview;
    previewVisible.value = true;
    previewTitle.value =
        file.name || 'Xem trước ';
};

const beforeUpload = (file) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
        message.error('Bạn chỉ có thể tải lên file ảnh!');
        return false;
    }
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
        message.error('Ảnh phải nhỏ hơn 2MB!');
        return false;
    }
    return isImage && isLt2M;
};

const handleCancel = () => {
    previewVisible.value = false;
    previewTitle.value = '';
};

// 1. Hàm upload cho ảnh sản phẩm chính (cho phép nhiều ảnh)
const handleMainImageUpload = async ({ file, onSuccess, onError }) => {
    try {
        if (uploadOption.value === 'upload') {
            formState.anhList = [];
        }
        fileList.value = fileList.value.filter(f => f.status !== 'uploading');

        fileList.value.push({
            uid: file.uid,
            name: file.name,
            status: 'done',
            originFileObj: file
        });

        formState.anhList = fileList.value.map(f => f.originFileObj);
        formRef.value.clearValidate('anhList');
        onSuccess("ok");
        message.success('Chọn ảnh thành công');
    } catch (error) {
        onError(error);
        message.error('Tải ảnh thất bại');
    }
};

const handleSubmit = async () => {
    Modal.confirm({
        title: 'Xác nhận lưu sản phẩm',
        content: 'Bạn có chắc chắn muốn lưu sản phẩm này không?',
        okText: 'Xác nhận',
        cancelText: 'Huỷ bỏ',
        async onOk() {
            submitting.value = true;
            formState.trangThai = submissionType.value === 'show';
            formState.tenAoDai = formState.tenAoDai.replace(/\s+/g, " ").trim();
            formState.moTa = formState.moTa.replace(/\s+/g, " ").trim();
            try {
                await formRef.value.validateFields();

                for (let i = 0; i < formState.sanPhamChiTietList.length; i++) {
                    const variant = formState.sanPhamChiTietList[i];
                    if (!variant.giaGoc || !variant.soLuong) {
                        throw new Error(`Biến thể thứ ${i + 1} chưa nhập đủ thông tin giá/số lượng`);
                    }
                    if (!variant.fileList?.[0]?.originFileObj) {
                        throw new Error(`Biến thể thứ ${i + 1} chưa có ảnh`);
                    }
                }

                if (uploadOption.value === 'upload') {
                    if (fileList.value.length === 0) {
                        throw new Error('Vui lòng tải lên ít nhất 1 ảnh sản phẩm chính');
                    }
                    const mainUploads = fileList.value.map(file =>
                        uploadImages(file.originFileObj).catch(error => {
                            message.error(`Tải lên ảnh ${file.name} thất bại`);
                            throw error;
                        })
                    );
                    const mainImageUrls = await Promise.all(mainUploads);
                    formState.anhList = mainImageUrls;
                }

                await Promise.all(
                    formState.sanPhamChiTietList.map(async (variant, index) => {
                        try {
                            const file = variant.fileList?.[0]?.originFileObj;
                            if (file) {
                                const variantImageUrl = await uploadImages(file);
                                variant.anhUrl = variantImageUrl;
                            }
                        } catch (error) {
                            message.error(`Lỗi tải lên ảnh biến thể ${index + 1}`);
                            throw error;
                        }
                    })
                );

                // Chuẩn bị dữ liệu gửi lên server
                const payload = {
                    ...formState,
                    sanPhamChiTietList: formState.sanPhamChiTietList.map(variant => ({
                        mauSacId: variant.mauSacId,
                        kichThuocId: variant.kichThuocId,
                        giaGoc: Number(variant.giaGoc),
                        giaBan: Number(variant.giaGoc),
                        soLuong: Number(variant.soLuong),
                        anhUrl: variant.anhUrl,
                        trangThai: true
                    }))
                };

                // Gọi API thêm sản phẩm
                const response = await addSanPham(payload);

                await new Promise(resolve => setTimeout(resolve, 1000));

                if (response) {
                    openNotification();
                    router.push('/san-pham/list/all');
                }
            } catch (error) {
                // Xử lý lỗi từ backend
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
                        message.error('Vui lòng kiểm tra lại thông tin sản phẩm!');
                    } catch (validateError) {
                        console.error('Lỗi validate:', validateError);
                    }
                } else {
                    // Hiển thị thông báo lỗi chung
                    message.error(error.message || 'Thêm sản phẩm thất bại');
                }
            } finally {
                submitting.value = false;
            }
        },
        onCancel() {
            message.info('Đã hủy thao tác lưu');
        }
    });
};

</script>

<style scoped>
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