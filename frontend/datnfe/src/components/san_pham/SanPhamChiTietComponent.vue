<template>
    <a-card class="my-4 shadow" title="Thông tin biến thể" style="margin-bottom: 20px">
        <div v-if="showVariantSelect" class="text-center mb-4">
            <a-button type="dashed" block @click="clearVariants">
                <i class="bi bi-x-circle"> Huỷ bỏ</i>
            </a-button>
        </div>
        <div v-if="!showVariantSelect" class="text-center">
            <a-button type="dashed" block @click="showVariantSelect = true">
                <i class="bi bi-plus-circle-dotted"> Thêm biến thể mới</i>
            </a-button>
        </div>
        <!-- Các select lựa chọn màu sắc và kích thước -->
        <div v-else>
            <div class="row mb-3">
                <div class="col-6">
                    <a-form-item label="Màu sắc">
                        <a-select v-model:value="selectedColors" mode="multiple" allowClear placeholder="Chọn màu sắc"
                            style="width: 100%;">
                            <a-select-option v-for="item in mauSacOptions" :key="item.id" :value="item.id">
                                {{ item.tenMauSac }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>

                </div>
                <div class="col-6">
                    <a-form-item label="Kích thước">
                        <a-select v-model:value="selectedSizes" mode="multiple" allowClear placeholder="Chọn kích thước"
                            style="width: 100%;">
                            <a-select-option v-for="item in kichThuocOptions" :key="item.id" :value="item.id">
                                {{ item.tenKichThuoc }}
                            </a-select-option>
                        </a-select>
                    </a-form-item>
                </div>
            </div>
        </div>
        <!-- Thêm phần input chung và nút áp dụng -->
        <div class="d-flex mb-3 align-items-center justify-content-around"
            v-if="selectedColors.length > 0 && selectedSizes.length > 0">
            <div>
                <a-form-item>
                    <small class="ms-1">Giá bán chung</small>
                    <a-input-number v-model:value="commonGiaGoc" :min="0" placeholder="Nhập giá bán chung"
                        :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')" style="width: 120%">
                        <template #addonAfter>₫</template>
                    </a-input-number>
                </a-form-item>
            </div>
            <div>
                <a-form-item>
                    <small class="ms-1">Số lượng chung</small>
                    <a-input-number v-model:value="commonSoLuong" :min="0" placeholder="Nhập số lượng chung"
                        style="width: 120%;" />
                </a-form-item>
            </div>
            <div>
                <a-button type="primary" @click="applyCommonValues" :disabled="!commonGiaGoc && !commonSoLuong">
                    Áp dụng
                </a-button>
            </div>
        </div>
        <!-- Bảng hiển thị biến thể -->
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
                        <!-- Ô ảnh và ô màu hiển thị ở hàng đầu tiên -->
                        <template v-if="colIndex === 0">
                            <td :rowspan="rowVariant.variants.length">
                                <a-form-item :name="['sanPhamChiTietList', rowIndex, 'anhUrl']"
                                    :rules="variantRulesCommon.anhUrl" validateTrigger="change">
                                    <a-upload v-model:file-list="rowVariant.fileList" list-type="picture-card"
                                        @change="() => handleUploadChange(rowVariant)"
                                        :custom-request="(e) => handleRowImageUpload(e, rowVariant)"
                                        :before-upload="beforeUpload" accept="image/*" @preview="handleVariantPreview"
                                        @remove="(file) => handleRemoveVariant(file, rowVariant)">
                                        <div v-if="rowVariant.fileList.length < 1">
                                            <plus-outlined />
                                            <div style="margin-top: 8px">Tải ảnh</div>
                                        </div>
                                    </a-upload>
                                    <!-- Thêm hidden input để bind giá trị anhUrl vào form -->
                                    <input type="hidden" v-model="rowVariant.anhUrl" />
                                </a-form-item>
                                <a-modal :open="previewVariantVisible" :title="previewVariantTitle" :footer="null"
                                    @cancel="handleVariantCancel">
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
                            <a-form-item :name="['sanPhamChiTietList', getFlatIndex(rowIndex, colIndex), 'giaGoc']"
                                :rules="variantRulesCommon.giaGoc" validateTrigger="['change', 'blur', 'input']">
                                <div class="d-block">
                                    <a-input-number v-model:value="variant.giaGoc"
                                        @change="(value) => validateGiaGoc(value, variant, rowIndex, colIndex)"
                                        placeholder="Giá bán"
                                        :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                                        style=" width: 120%;">
                                        <template #addonAfter>₫</template>
                                    </a-input-number>
                                </div>
                            </a-form-item>
                        </td>
                        <!-- Ô số lượng -->
                        <td>
                            <a-form-item :name="['sanPhamChiTietList', getFlatIndex(rowIndex, colIndex), 'soLuong']"
                                :rules="variantRulesCommon.soLuong" validateTrigger="['change', 'blur', 'input']">
                                <div class="d-block">
                                    <a-input-number v-model:value="variant.soLuong"
                                        @change="(value) => validateSoLuong(value, variant, rowIndex, colIndex)"
                                        placeholder="Số lượng" style="width: 120%;" />
                                </div>
                            </a-form-item>
                        </td>
                        <!-- Ô hành động: nút xoá variant -->
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
</template>

<script setup>
import { ref, watch } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';

// Nhận các prop từ component cha
const props = defineProps({
    mauSacOptions: { type: Array, required: true },
    kichThuocOptions: { type: Array, required: true },
    formRef: Object
});

// Sự kiện emit để trả về danh sách biến thể (sanPhamChiTietList)
const emit = defineEmits(['update:variants']);
const showVariantSelect = ref(false);
const commonGiaGoc = ref(null);
const commonSoLuong = ref(null);

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
            props.formRef?.clearValidate([['sanPhamChiTietList', flatIndex, 'giaGoc']]);
            props.formRef?.clearValidate([['sanPhamChiTietList', flatIndex, 'soLuong']]);
        });
    });

    // Reset các giá trị chung sau khi áp dụng
    commonGiaGoc.value = null;
    commonSoLuong.value = null;

    message.success('Đã áp dụng giá trị cho tất cả biến thể và xoá validate');
};

const validateGiaGoc = (value, variant, rowIndex, colIndex) => {
    variant.giaGoc = value;
    setTimeout(() => {
        const flatIndex = getFlatIndex(rowIndex, colIndex);
        // Sử dụng props.formRef thay vì formRef.value
        props.formRef?.validateFields([[`sanPhamChiTietList`, flatIndex, 'giaGoc']]);
    }, 10);
};

const validateSoLuong = (value, variant, rowIndex, colIndex) => {
    variant.soLuong = value;
    setTimeout(() => {
        const flatIndex = getFlatIndex(rowIndex, colIndex);
        // Sử dụng props.formRef thay vì formRef.value
        props.formRef?.validateFields([[`sanPhamChiTietList`, flatIndex, 'soLuong']]);
    }, 10);

};
// Các rule validate cho biến thể
const variantRulesCommon = {
    anhUrl: [
        {
            validator: (_, value) => {
                // Check if there's a valid URL or if upload is in progress
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
    ],
};

const getFlatIndex = (rowIndex, colIndex) => {
    let index = 0;
    for (let i = 0; i < rowIndex; i++) {
        index += variantRowsData.value[i].variants.length;
    }
    return index + colIndex;
};

const variantRowsData = ref([]);
const selectedColors = ref([]);
const selectedSizes = ref([]);

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
    variantRowsData.value = newRows;
    emitVariants();
}, { deep: true });

const clearVariants = () => {
    showVariantSelect.value = false;
    variantRowsData.value = [];
    selectedColors.value = [];
    selectedSizes.value = [];
    // Nếu cần, emit variants rỗng về component cha
    emitVariants();
};

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
    emit('update:variants', flatVariants);
};

watch(variantRowsData, () => emitVariants(), { deep: true });
const getMauSacName = (id) => {
    const found = props.mauSacOptions.find(item => item.id === id);
    return found ? found.tenMauSac : id;
};
const getKichThuocName = (id) => {
    const found = props.kichThuocOptions.find(item => item.id === id);
    return found ? found.tenKichThuoc : id;
};
const formRef = ref(null);
const previewVariantVisible = ref(false);
const previewVariantImage = ref('');
const previewVariantTitle = ref('');

const handleRowImageUpload = async ({ file, onSuccess }, rowVariant) => {
    try {
        const uploadedUrl = await mockUploadApi(file);

        rowVariant.anhUrl = uploadedUrl;
        rowVariant.fileList = [{
            uid: file.uid,
            name: file.name,
            status: 'done',
            url: uploadedUrl,
            originFileObj: file
        }];
        const form = formRef.value;
        if (form) {
            const fieldPath = variantRowsData.value.findIndex(row => row === rowVariant);
            if (fieldPath !== -1) {
                await form.validateFields([`sanPhamChiTietList.${fieldPath}.anhUrl`]);
            }
        }
        onSuccess();
        message.success('Upload ảnh thành công');
    } catch (error) {
        message.error('Upload ảnh thất bại');
        rowVariant.anhUrl = '';
        onError(error);
    }
};

const mockUploadApi = (file) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(`https://example.com/uploads/${file.name}`);
        }, 500);
    });
};

const handleUploadChange = (rowVariant) => {
    if (rowVariant.fileList.length > 0) {
        if (!rowVariant.anhUrl || rowVariant.anhUrl === '') {
            rowVariant.anhUrl = 'pending';
        }
    } else {
        rowVariant.anhUrl = '';
    }
};

const handleVariantPreview = async (file) => {
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

function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = (error) => reject(error);
    });
}

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

const handleRemoveVariant = (file, rowVariant) => {
    rowVariant.fileList = [];
    rowVariant.anhUrl = '';
    formRef.value.validateFields([`sanPhamChiTietList.${fieldPath}.anhUrl`])
    return true;
};

// Hàm xoá 1 variant (dựa theo rowIndex và colIndex)
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
</script>
