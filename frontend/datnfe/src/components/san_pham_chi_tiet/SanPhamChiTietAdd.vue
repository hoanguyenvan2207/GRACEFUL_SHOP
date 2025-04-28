<template>
    <a-spin v-if="submitting" :spinning="true" size="large" class="full-screen-spin">
    </a-spin>
    <a-layout :disabled="submitting" class="container rounded mb-5 shadow mt-3" style="width: 80%;">
        <h4 class="text-center mt-4 mb-4">THÊM MỚI BIẾN THỂ SẢN PHẨM</h4>
        <a-form :loading="submitting" :model="formState" :rules="rules" @finish="handleSubmit" layout="vertical"
            ref="formRef">
            <!-- Phần quản lý biến thể -->
            <a-card class="m-4 shadow px-5 " style="margin-bottom: 20px">
                <a-form-item label="Sản phẩm" name="aoDaiId" required class="mt-2 mb-3">
                    <a-auto-complete v-model:value="searchQuery" :options="productOptions" @focus="handleSearch('')"
                        @search="handleSearch" @input="handleInput" @select="handleSelectProduct"
                        placeholder="Tìm kiếm sản phẩm...">
                        <template #option="{ maAoDai, tenAoDai, imageUrl }">
                            <div class="product-option" style="display: flex; align-items: center;">
                                <img :src="imageUrl" alt="Product Image"
                                    style="width: 70px; height: auto; object-fit: cover; margin-right: 10px;" />
                                <div>
                                    <div class="product-code">{{ maAoDai }}</div>
                                    <div class="product-name">{{ tenAoDai }}</div>
                                </div>
                            </div>
                        </template>
                    </a-auto-complete>
                </a-form-item>
                <!-- Trạng thái chung của sản phẩm -->
                <a-form-item label="Trạng thái" name="trangThai">
                    <a-switch v-model:checked="formState.trangThai" checked-children="Đang kinh doanh"
                        un-checked-children="Ngừng kinh doanh" />
                </a-form-item>
                <div class="row p-0" v-if="formState.aoDaiId">
                    <div class="col-6">
                        <a-form-item label="Màu sắc">
                            <a-select v-model:value="selectedColors" mode="multiple" allowClear
                                placeholder="Chọn màu sắc" style="width: 100%;">
                                <a-select-option v-for="item in colors" :key="item.id" :value="item.id">
                                    {{ item.tenMauSac }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </div>
                    <div class="col-6">
                        <a-form-item label="Kích thước">
                            <a-select v-model:value="selectedSizes" mode="multiple" allowClear
                                placeholder="Chọn kích thước" style="width: 100%;">
                                <a-select-option v-for="item in sizes" :key="item.id" :value="item.id">
                                    {{ item.tenKichThuoc }}
                                </a-select-option>
                            </a-select>
                        </a-form-item>
                    </div>
                </div>
                <span v-if="errorDuplicate" class="text-danger ">{{ errorDuplicate }}</span>

                <!-- Phần nhập giá chung -->
                <div class=" d-flex justify-content-around mt-3 mb-3 align-items-center"
                    v-if="selectedColors.length > 0 && selectedSizes.length > 0">
                    <a-form-item>
                        <small class="ms-1">Giá bán chung</small>
                        <a-input-number v-model:value="commonGiaGoc" :min="0" placeholder="Nhập giá bán chung"
                            :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')" style="width: 100%">
                            <template #addonAfter>₫</template>
                        </a-input-number>
                    </a-form-item>
                    <a-form-item>
                        <small class="ms-1">Số lượng chung</small>
                        <a-input-number v-model:value="commonSoLuong" :min="0" placeholder="Nhập số lượng chung"
                            style="width: 100%" />
                    </a-form-item>
                    <div class="col-1 p-0">
                        <a-button type="primary" @click="applyCommonValues" :disabled="!commonGiaGoc && !commonSoLuong">
                            Áp dụng
                        </a-button>
                    </div>
                </div>

                <!-- Bảng hiển thị danh sách biến thể -->
                <table v-if="selectedColors.length > 0 && selectedSizes.length > 0"
                    class="table align-middle table-bordered text-center mt-2 rounded">
                    <thead>
                        <tr>
                            <th style="width: 15%;">Ảnh</th>
                            <th style="width: 10%;">Màu sắc</th>
                            <th style="width: 10%;">Kích thước</th>
                            <th style="width: 30%;">Giá bán</th>
                            <th style="width: 30%;">Số lượng</th>
                            <th style="width: 10%;">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <template v-for="(rowVariant, rowIndex) in variantRowsData" :key="rowVariant.mauSac">
                            <tr v-for="(variant, colIndex) in rowVariant.variants" :key="variant.kichThuoc">
                                <!-- Ô ảnh và ô màu (chỉ render 1 lần cho mỗi màu) -->
                                <template v-if="colIndex === 0">
                                    <td :rowspan="rowVariant.variants.length">
                                        <a-form-item :name="['sanPhamChiTietList', rowIndex, 'anhUrl']"
                                            :rules="variantRulesCommon.anhUrl" validateTrigger="change">
                                            <a-upload v-model:file-list="rowVariant.fileList" list-type="picture-card"
                                                @change="() => handleUploadChange(rowVariant)"
                                                :custom-request="(e) => handleRowImageUpload(e, rowVariant)"
                                                :before-upload="beforeUpload" accept="image/*"
                                                @preview="handleVariantPreview"
                                                @remove="(file) => handleRemoveVariant(file, rowVariant)">
                                                <div v-if="rowVariant.fileList.length < 1">
                                                    <plus-outlined />
                                                    <div style="margin-top: 8px">Tải ảnh</div>
                                                </div>
                                            </a-upload>
                                            <!-- hidden input để bind ảnh vào form nếu cần -->
                                            <input type="hidden" v-model="rowVariant.anhUrl" />
                                        </a-form-item>
                                        <a-modal :open="previewVariantVisible" :title="previewVariantTitle"
                                            :footer="null" @cancel="handleVariantCancel">
                                            <img alt="example" style="width: 100%" :src="previewVariantImage" />
                                        </a-modal>
                                    </td>
                                    <td :rowspan="rowVariant.variants.length">
                                        <div class="d-block">{{ getMauSacName(rowVariant.mauSac) }}</div>
                                    </td>
                                </template>
                                <!-- Ô kích thước -->
                                <td>
                                    <div class="d-block">{{ getKichThuocName(variant.kichThuoc) }}</div>
                                </td>
                                <!-- Ô giá bán -->
                                <td>
                                    <a-form-item
                                        :name="['sanPhamChiTietList', getFlatIndex(rowIndex, colIndex), 'giaGoc']"
                                        :rules="variantRulesCommon.giaGoc" validateTrigger="change">
                                        <a-input-number v-model:value="variant.giaGoc"
                                            @change="(value) => validateGiaGoc(value, variant, rowIndex, colIndex)"
                                            placeholder="Giá bán"
                                            :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                            style="width: 100%">
                                            <template #addonAfter>₫</template>
                                        </a-input-number>
                                    </a-form-item>
                                </td>
                                <!-- Ô số lượng -->
                                <td>
                                    <a-form-item
                                        :name="['sanPhamChiTietList', getFlatIndex(rowIndex, colIndex), 'soLuong']"
                                        :rules="variantRulesCommon.soLuong" validateTrigger="change">
                                        <a-input-number v-model:value="variant.soLuong"
                                            @change="(value) => validateSoLuong(value, variant, rowIndex, colIndex)"
                                            placeholder="Số lượng" style="width: 100%;" />
                                    </a-form-item>
                                </td>
                                <!-- Ô hành động: xoá 1 biến thể -->
                                <td>
                                    <a-button type="text" danger @click="removeVariantRow(rowIndex, colIndex)">
                                        <i class="bi bi-trash" style="font-size: 20px;"></i>
                                    </a-button>
                                </td>
                            </tr>
                        </template>
                    </tbody>
                </table>

            </a-card>

            <!-- Nút submit -->
            <a-form-item class="d-flex justify-content-end py-5 px-4">
                <button class="mx-2 btn btn-danger" @click="handleCancel">
                    <i class="bi bi-x-lg"> Huỷ bỏ</i>
                </button>
                <button class="btn btn-primary">
                    <i class="bi bi-floppy"> Lưu tất cả biến thể</i>
                </button>
            </a-form-item>
        </a-form>
    </a-layout>
</template>

<script setup>
import { ref, reactive, watch, onMounted, nextTick, h } from 'vue';
import { message, Modal, notification } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { getSanPhamsEn, uploadImages } from '../san_pham/api';
import { getMauSacsEn } from '../mau_sac/api';
import { getKichThuocsEn } from '../kich_thuoc/api';
import { importSanPhamChiTiets } from './api';
import router from '../../router/router';

// ----- PHẦN FORM CHÍNH -----
const formState = reactive({
    aoDaiId: null,
    trangThai: true,
    sanPhamChiTietList: []
});

const rules = {
    aoDaiId: [{ required: true, message: 'Vui lòng chọn sản phẩm', trigger: ['change', 'blur'] }
    ]
};

const validateGiaGoc = (value, variant, rowIndex, colIndex) => {
    variant.giaGoc = value;
    setTimeout(() => {
        const flatIndex = getFlatIndex(rowIndex, colIndex);
        formRef.value.validateFields([[`sanPhamChiTietList`, flatIndex, 'giaGoc']]);
    }, 10);
};

const validateSoLuong = (value, variant, rowIndex, colIndex) => {
    variant.soLuong = value;
    setTimeout(() => {
        const flatIndex = getFlatIndex(rowIndex, colIndex);
        formRef.value.validateFields([[`sanPhamChiTietList`, flatIndex, 'soLuong']]);
    }, 10);
};

const searchQuery = ref('');
const productOptions = ref([]);

const colors = ref([]);
const sizes = ref([]);

const fullProductList = ref([])
const commonGiaGoc = ref(null);
const commonSoLuong = ref(null);

const variantRowsData = ref([]);
const selectedColors = ref([]);
const selectedSizes = ref([]);

const variantsList = ref([]);
const formRef = ref();
const submitting = ref(false);

const applyCommonValues = () => {
    variantRowsData.value.forEach((row, rowIndex) => {
        row.variants.forEach((variant, colIndex) => {
            if (commonGiaGoc.value !== null) {
                variant.giaGoc = commonGiaGoc.value;
            }
            if (commonSoLuong.value !== null) {
                variant.soLuong = commonSoLuong.value;
            }
            // Tính chỉ số phẳng (flatIndex) để lấy đúng đường dẫn trong form
            const flatIndex = getFlatIndex(rowIndex, colIndex);
            // Xoá validate của từng trường cụ thể sau khi cập nhật
            formRef.value.clearValidate([['sanPhamChiTietList', flatIndex, 'giaGoc']]);
            formRef.value.clearValidate([['sanPhamChiTietList', flatIndex, 'soLuong']]);
        });
    });

    // Reset các giá trị chung sau khi áp dụng
    commonGiaGoc.value = null;
    commonSoLuong.value = null;

    message.success('Đã áp dụng giá trị cho tất cả biến thể và xoá validate');
};

const variantRulesCommon = {
    anhUrl: [
        {
            validator: (_, value) => {
                if (!value || (value === 'pending' && !variantRowsData.value.some(row => row.fileList.length > 0))) {
                    return Promise.reject('Vui lòng tải ảnh lên');
                }
                return Promise.resolve();
            },
            trigger: ['change', 'blur']
        }
    ],
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
            trigger: ['change', 'blur', 'input']
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
            trigger: ['change', 'blur', 'input']
        }
    ]
};

const getFlatIndex = (rowIndex, colIndex) => {
    let index = 0;
    for (let i = 0; i < rowIndex; i++) {
        index += variantRowsData.value[i].variants.length;
    }
    return index + colIndex;
};

watch([selectedColors, selectedSizes], () => {
    const newRows = selectedColors.value.map(colorId => {
        const existing = variantRowsData.value.find(row => row.mauSac === colorId) || {};
        const variants = selectedSizes.value.map(sizeId => {
            const existVariant = (existing.variants || []).find(v => v.kichThuoc === sizeId);
            return {
                ...existVariant,
                kichThuoc: sizeId,
                giaGoc: existVariant?.giaGoc ?? null,
                soLuong: existVariant?.soLuong ?? null
            };
        });
        return {
            mauSac: colorId,
            fileList: existing.fileList || [],
            anhUrl: existing.anhUrl || '',
            variants: variants
        };
    });
    errorDuplicate.value = null;
    variantRowsData.value = newRows;
    emitVariants();
}, { deep: true });

const emitVariants = () => {
    const flatVariants = variantRowsData.value.flatMap((row, rowIndex) =>
        row.variants.map(variant => ({
            mauSacId: row.mauSac,
            kichThuocId: variant.kichThuoc,
            giaGoc: variant.giaGoc,
            soLuong: variant.soLuong,
            anhUrl: row.anhUrl,
            fileList: row.fileList,
            __path: `sanPhamChiTietList.${rowIndex}.anhUrl`
        }))
    );
    variantsList.value = flatVariants;
    formState.sanPhamChiTietList = flatVariants;
};

watch(variantRowsData, () => emitVariants(), { deep: true });

const getMauSacName = id => {
    const found = colors.value.find(item => item.id === id);
    return found ? found.tenMauSac : id;
};

const getKichThuocName = id => {
    const found = sizes.value.find(item => item.id === id);
    return found ? found.tenKichThuoc : id;
};

const previewVariantVisible = ref(false);
const previewVariantImage = ref('');
const previewVariantTitle = ref('');

const handleRowImageUpload = async ({ file, onSuccess, onError }, rowVariant) => {
    try {
        const uploadedUrl = await mockUploadApi(file);
        rowVariant.anhUrl = uploadedUrl;
        rowVariant.fileList = [
            {
                uid: file.uid,
                name: file.name,
                status: 'done',
                url: uploadedUrl,
                originFileObj: file
            }
        ];
        onSuccess();
        message.success('Upload ảnh thành công');
    } catch (error) {
        message.error('Upload ảnh thất bại');
        rowVariant.anhUrl = '';
        onError(error);
    }
};

const mockUploadApi = file => {
    return new Promise(resolve => {
        setTimeout(() => {
            resolve(`https://example.com/uploads/${file.name}`);
        }, 500);
    });
};

const handleUploadChange = rowVariant => {
    if (rowVariant.fileList.length > 0) {
        if (!rowVariant.anhUrl || rowVariant.anhUrl === '') {
            rowVariant.anhUrl = 'pending';
        }
    } else {
        rowVariant.anhUrl = '';
    }
};

const handleVariantPreview = async file => {
    if (!file.url && !file.preview) {
        file.preview = await getBase64(file.originFileObj);
    }
    previewVariantImage.value = file.url || file.preview;
    previewVariantVisible.value = true;
    previewVariantTitle.value = file.name || 'Ảnh biến thể';
};

const handleVariantCancel = () => {
    previewVariantVisible.value = false;
    previewVariantTitle.value = '';
};

const getBase64 = file => {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
};

const beforeUpload = file => {
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

const handleRemoveVariant = (file, rowVariant) => {
    rowVariant.fileList = [];
    rowVariant.anhUrl = '';
    return true;
};

const removeVariantRow = (rowIndex, colIndex) => {
    const row = variantRowsData.value[rowIndex];
    if (row) {
        row.variants.splice(colIndex, 1);
        if (row.variants.length === 0) {
            variantRowsData.value.splice(rowIndex, 1);
            selectedColors.value = selectedColors.value.filter(colorId => colorId !== row.mauSac);
        }
        emitVariants();
    }
};
const loadFullProductList = async () => {
    try {
        const response = await getSanPhamsEn();
        fullProductList.value = response
    } catch (error) {
        message.error('Lỗi tải danh sách sản phẩm')
    }
}

onMounted(() => {
    loadFullProductList()
})

const handleSearch = (value) => {
    if (!value) {
        // Nếu giá trị rỗng, hiển thị tất cả sản phẩm
        productOptions.value = fullProductList.value.map(product => ({
            value: product.id,
            maAoDai: product.maAoDai,
            tenAoDai: product.tenAoDai,
            imageUrl: product.anhList[0],
            label: `${product.maAoDai} - ${product.tenAoDai}`
        }));
    } else {
        const filtered = fullProductList.value.filter(product =>
            product.maAoDai.toLowerCase().includes(value.toLowerCase()) ||
            product.tenAoDai.toLowerCase().includes(value.toLowerCase())
        );
        productOptions.value = filtered.map(product => ({
            value: product.id,
            maAoDai: product.maAoDai,
            tenAoDai: product.tenAoDai,
            imageUrl: product.anhList[0],
            label: `${product.maAoDai} - ${product.tenAoDai}`
        }));
    }
}

const handleInput = (value) => {
    formState.aoDaiId = "";
    nextTick(() => {
        formRef.value.validateFields(['aoDaiId']);
    });
}

const handleSelectProduct = (value, option) => {
    formState.aoDaiId = value;
    searchQuery.value = option.label;
    nextTick(() => {
        formRef.value.clearValidate(['aoDaiId']);
    });

    errorDuplicate.value = "";
};

const handleCancel = () => {
    if (selectedColors.value.length === 0 || selectedSizes.value.length === 0) {
        router.push('/san-pham-chi-tiet/list/all')
    } else {
        Modal.confirm({
            title: 'Xác nhận hủy',
            content: 'Tất cả thay đổi sẽ không được lưu!',
            okText: 'Xác nhận',
            cancelText: 'Hủy bỏ',
            onOk() {
                router.push('/san-pham-chi-tiet/list/all');
                message.info('Đã huỷ thao tác thêm mới biến thể');
            }
        });
    }
};

const formErrors = ref({});
const errorDuplicate = ref("");

const handleSubmit = async () => {
    try {
        await formRef.value.validateFields();
        if (variantsList.value.length === 0) {
            message.error('Vui lòng thêm ít nhất một biến thể');
            return;
        }
        Modal.confirm({
            title: 'Xác nhận thêm mới',
            content: `Bạn có chắc chắn muốn thêm ${variantsList.value.length} biến thể?`,
            okText: 'Xác nhận',
            cancelText: 'Hủy bỏ',
            async onOk() {
                try {
                    submitting.value = true;
                    const formattedData = await Promise.all(variantsList.value.map(async (variant) => ({
                        aoDaiId: formState.aoDaiId,
                        giaGoc: variant.giaGoc,
                        soLuong: variant.soLuong,
                        trangThai: formState.trangThai,
                        anhUrl: await uploadImages(variant.fileList[0].originFileObj),
                        mauSacId: variant.mauSacId,
                        kichThuocId: variant.kichThuocId
                    })));

                    const requests = await importSanPhamChiTiets(formattedData);

                    await Promise.all(requests);

                    await new Promise(resolve => setTimeout(resolve, 1000));

                    notification.success({
                        message: 'Thêm mới thành công',
                        description: `Thêm thành công ${variantsList.value.length} biến thể!`
                    });
                    router.push('/san-pham-chi-tiet/list/all');
                } catch (error) {
                    if (error.response?.data?.errors) {
                        formErrors.value = error.response.data.errors;
                        errorDuplicate.value = formErrors.value.duplicate || 'Có lỗi xảy ra';
                        message.error('Vui lòng kiểm tra lại thông tin biến thể!');
                    }
                } finally {
                    submitting.value = false;
                }
            }
        });
    } catch (error) {
        message.error('Vui lòng kiểm tra lại thông tin');
    }
};


onMounted(async () => {
    try {
        const [mauSacs, kichThuocs] = await Promise.all([getMauSacsEn(), getKichThuocsEn()]);
        colors.value = mauSacs;
        sizes.value = kichThuocs;
    } catch (error) {
        message.error('Lỗi tải dữ liệu danh mục');
    }
});
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