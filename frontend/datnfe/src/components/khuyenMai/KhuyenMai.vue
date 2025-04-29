<template>
    <div v-if="isDetailVisible1" class=" container d-flex ">
        <div class="d-flex">
            <div class="fom-group-r col-2">
                <h3 class=" d-flex justify-content-center badge text-bg-info"
                    style="font-size: 1.8rem; padding: 10px 10px; width: 100%;">Khuyến mãi</h3>
                <div class="form-group2">
                    <h5>Trạng thái</h5>
                    <hr>
                    <div class="form-check m-3">
                        <input class="form-check-input" type="radio" @change="searchKhuyenMai()" name="statusFilter3"
                            id="statusFilter3" value="1" v-model="statusFilter" />
                        <label class="form-check-label" for="statusFilter3">Kích hoạt</label>
                    </div>
                    <div class="form-check mt-4 ms-3">
                        <input class="form-check-input" type="radio" @change="searchKhuyenMai()" name="statusFilter1"
                            id="statusFilter1" value="0" v-model="statusFilter" />
                        <label class="form-check-label" for="statusFilter1">Chưa áp dụng</label>
                    </div>
                    <h5 class="mt-5">Hiệu lực</h5>
                    <hr>
                    <div class="form-check m-3">
                        <input class="form-check-input" type="radio" @change="searchKhuyenMai()" name="validityFilter4"
                            id="validityFilter4" value="1" v-model="validityFilter" />
                        <label class="form-check-label" for="validityFilter4">Còn hiệu lực</label>
                    </div>
                    <div class="form-check mt-4 ms-3">
                        <input class="form-check-input" type="radio" @change="searchKhuyenMai()" name="validActive1"
                            id="validActive1" value="2" v-model="validityFilter" />
                        <label class="form-check-label" for="validActive1">Hết hiệu lực</label>
                    </div>
                    <div class="form-check mt-4 ms-3">
                        <input class="form-check-input" type="radio" @change="searchKhuyenMai()" name="validityFilter3"
                            id="validityFilter3" value="3" v-model="validityFilter" />
                        <label class="form-check-label" for="validityFilter3">Chưa bắt đầu</label>
                    </div>
                </div>
            </div>
            <!-- <-- Form chứa danh sách khuyến mãi -->
            <div class="form-group3 col-9">
                <div class=" d-flex flex-column align-items-center justify-content-center">
                    <div class="row w-100 mb-3 d-flex align-items-center mt-3">
                        <div class="col-4">
                            <input type="search" class="form-control" placeholder="Tìm theo mã + tên + giá trị giảm"
                                v-model="searchKey" @keyup="searchKhuyenMai(0)" />
                        </div>

                        <div class="col-4 d-flex">
                            <a-date-picker class="me-1" style=" min-height: 40px;" v-model:value="start"
                                format="DD/MM/YYYY" @change="onStartChange" placeholder="Chọn ngày bắt đầu" />
                            <a-date-picker v-model:value="end" format="DD/MM/YYYY" @change="onEndChange"
                                placeholder="Chọn ngày kết thúc" />
                        </div>

                        <div class="col-2">
                            <div class="form-group d-flex">
                                <label for="pageSize" style="white-space: nowrap; font-size: 14px;" class="mt-2">Số bản
                                    ghi:</label>
                                <select style="max-width: 100px; max-height: 40px;font-size: 15px; " class="form-select"
                                    id="pageSize" v-model="pageSize" @change="searchKhuyenMai(0)">
                                    <option value="5">5</option>
                                    <option value="10">10</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-2 d-flex justify-content-start">
                            <button @click="openAddForm" class="btn btn-info" v-if="isAdmin">
                                <i class="bi bi-plus-circle"></i>Khuyến mãi
                            </button>
                        </div>
                    </div>
                </div>
                <div class=" mt-4">
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover">
                            <thead>
                                <tr class="table-primary">
                                    <th>Mã khuyến mãi</th>
                                    <th>Tên Khuyến Mãi</th>
                                    <th>Giá trị giảm</th>
                                    <th>Từ ngày</th>
                                    <th>Đến ngày</th>
                                    <th>Mô tả</th>
                                    <th>Trạng thái</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-if="khuyenMais.length === 0">
                                    <td colspan="8" class="text-center text-muted">Không có thông tin nào</td>
                                </tr>
                                <tr v-for="km in khuyenMais" :key="km.id">
                                    <td>{{ km.maKhuyenMai }}</td>
                                    <td>{{ km.tenKhuyenMai }}</td>
                                    <td><span v-if="km.phanTramGiam && km.phanTramGiam > 0">
                                            {{ km.phanTramGiam }}%
                                        </span>
                                        <span v-else-if="km.soTienGiam && km.soTienGiam > 0">
                                            {{ formatCurrency(km.soTienGiam) }}
                                        </span>
                                    </td>

                                    <td>{{ formatDate(km.ngayBatDau) }}</td>
                                    <td>{{ formatDate(km.ngayKetThuc) }}</td>
                                    <td>{{ km.moTa }}</td>
                                    <td>
                                        <span :class="km.trangThai === true ? 'text-success' : 'text-danger'">
                                            {{ km.trangThai ? "Kích hoạt" : "Chưa áp dụng" }}
                                        </span>
                                    </td>

                                    <td> <button v-if="isAdmin" class="btn btn-outline-warning"
                                            @click="openEditFormKM(km)"><i class="bi bi-pencil-square"></i>
                                        </button><button class="btn btn-outline-danger" @click="showDetailsKM(km)"
                                            data-bs-toggle="offcanvas" data-bs-target="#detailOffcanvaskm">
                                            <i class="bi bi-eye"></i>
                                        </button></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div class="pagination-container">
                        <div class="pagination1">
                            <button :disabled="currentPage === 0" @click="searchKhuyenMai(0)" class="btn btn-info m-1">
                                <i class="bi bi-chevron-double-left"></i>
                            </button>
                            <button :disabled="currentPage === 0" @click="searchKhuyenMai(currentPage - 1)"
                                class="btn btn-info m-1">
                                <i class="bi bi-chevron-left"></i>
                            </button>
                            <span class="mt-1"> {{ currentPage + 1 }} / {{ totalPages }}</span>
                            <button :disabled="currentPage >= totalPages - 1" @click="searchKhuyenMai(currentPage + 1)"
                                class="btn btn-info m-1">
                                <i class="bi bi-chevron-right"></i>
                            </button>
                            <button :disabled="currentPage >= totalPages - 1" @click="searchKhuyenMai(totalPages - 1)"
                                class="btn btn-info m-1">
                                <i class="bi bi-chevron-double-right"></i>
                            </button>
                        </div>
                    </div>

                    <div class="offcanvas offcanvas-body custom-offcanvas" tabindex="-1" id="detailOffcanvaskm">
                        <div class="offcanvas-header bg-info text-white">
                            <h5 class="offcanvas-title mx-auto">Chi Tiết Khuyến Mãi</h5>
                            <button type="button" class="btn-close btn-close-white btn-warning"
                                data-bs-dismiss="offcanvas"></button>
                        </div>
                        <div class="offcanvas-body">
                            <div class="container">
                                <!-- Tên & Mã khuyến mãi -->
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Tên Khuyến Mãi:</div>
                                    <div class="col-8">{{ selectedItem?.tenKhuyenMai }}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Mã Khuyến Mãi:</div>
                                    <div class="col-8">{{ selectedItem?.maKhuyenMai }}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Giá trị giảm:</div>
                                    <div class="col-8">
                                        <span v-if="selectedItem?.phanTramGiam && selectedItem?.phanTramGiam > 0">
                                            {{ selectedItem.phanTramGiam }}%
                                        </span>
                                        <span v-else-if="selectedItem?.soTienGiam && selectedItem?.soTienGiam > 0">
                                            {{ formatCurrency(selectedItem.soTienGiam) }} VNĐ
                                        </span>
                                        <span v-else>Không có thông tin giảm giá</span>
                                    </div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Ngày bắt đầu:</div>
                                    <div class="col-8">{{ formatDate(selectedItem?.ngayBatDau) }}</div>
                                </div>
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Đến trước ngày:</div>
                                    <div class="col-8">{{ formatDate(selectedItem?.ngayKetThuc) }}</div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Mô tả:</div>
                                    <div class="col-8">{{ selectedItem?.moTa }}</div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="row mb-3">
                                    <div class="col-4 fw-bold">Trạng thái:</div>
                                    <div class="col-8">
                                        <span :class="selectedItem?.trangThai ? 'badge bg-success' : 'badge bg-danger'">
                                            {{ selectedItem?.trangThai ? "Kích hoạt" : "Chưa áp dụng" }}
                                        </span>
                                    </div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="row">
                                    <div class="col-4 fw-bold">Sản phẩm áp dụng:</div>
                                    <div class="col-8">
                                        <span v-for="(sp, index) in selectedSanPhamChiTietIds" :key="sp.id"
                                            class="d-inline-block me-3 small">
                                            {{ sp.tenAoDai }} ({{ sp.maAoDaiChiTiet }})
                                            <span v-if="index < selectedSanPhamChiTietIds.length - 1">, </span>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
    <!-- Form Offcanvas add update -->
    <div v-if="isFrom1" class="fom-form container">
        <div class="offcanvas-header">
            <h5 class="offcanvas-title text-primary text-center mx-auto"> {{ isEditing ? 'Chỉnh sửa' : 'Thêm mới' }}
                Khuyến
                Mãi
            </h5>
            <button type="button" class=" btn  btn-outline-danger" @click="closeFrom"><i
                    class="bi bi-arrow-return-left"></i></button>
        </div>
        <div v-if="isAddVisible1" class="offcanvas-body">
            <form @submit.prevent="addKhuyenMai" id="myForm" class="form">
                <!-- Danh sách sản phẩm -->
                <div @click="fetchSanPhamCT(page = 0)" v-if="isDetaiSPCTVisible" class="table-spct-overlay mt-3">
                    <div class="row">
                        <div class="col-10">
                            <input type="search" class="form-control me-2" placeholder="Tìm theo mã + tên Áo dài "
                                v-model="searchKeySpct" @keyup="searchSpct(0)" />
                        </div>
                        <div class="col-2">
                            <button class="btn btn-danger" @click="isDetaiSPCTVisible = false">
                                <i class="bi bi-x-lg"></i>
                            </button>
                        </div>
                    </div>
                    <!-- List group spct-->
                    <ul class="list-group mt-3">
                        <li v-for="spct in sanPhamChiTietIds" :key="spct.id" class="list-group-item">
                            <div @click="toggleCheckbox(spct)"
                                class="d-flex w-100 justify-content-between align-items-center"
                                style="cursor: pointer;">
                                <span>{{ spct.maAoDaiChiTiet }} - {{ spct.tenAoDai }}- {{ spct.kichThuoc }} - {{
                                    spct.mauSac }}</span>
                                <input type="checkbox" :checked="toggleSelection(spct.id)" @click.prevent
                                    style="pointer-events: none;">
                            </div>
                        </li>
                    </ul>
                    <div class="pagination">
                        <button type="button" :disabled="currentPage1 === 0" @click="searchSpct(0)"
                            class="btn btn-info m-1">
                            <i class="bi bi-chevron-double-left"></i>
                        </button>
                        <button class="btn btn-info " type="button" :disabled="currentPage1 === 0"
                            @click="searchSpct(currentPage1 - 1)">
                            <i class="bi bi-chevron-left"></i>
                        </button>
                        <span>Trang {{ currentPage1 + 1 }} / {{ totalPages1 == null ? "0" : totalPages1 }}</span>
                        <button class="btn btn-info " type="button" :disabled="currentPage1 >= totalPages1 - 1"
                            @click="searchSpct(currentPage1 + 1)">
                            <i class="bi bi-chevron-right"></i>
                        </button>

                        <button type="button" :disabled="currentPage1 >= totalPages1 - 1"
                            @click="searchSpct(totalPages1 - 1)" class="btn btn-info m-1">
                            <i class="bi bi-chevron-double-right"></i>
                        </button>
                    </div>
                </div>



                <div class="row">
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-1" style="white-space: nowrap;" for="tenKhuyenMai">Tên khuyến
                                    mãi:</label>
                                <input v-model="newItem.tenKhuyenMai" placeholder="Tên khuyến mãi" type="text"
                                    @blur="validateField('tenKhuyenMai')" @input="validateField('tenKhuyenMai')"
                                    id="tenKhuyenMai" class="form-control custom-input mx-5">
                            </div>
                            <span v-if="errors.tenKhuyenMai" style="margin-left: 150px;"
                                class="text-danger error-message">{{
                                    errors.tenKhuyenMai }}</span>
                        </div>
                    </div>
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3 ms-5">
                            <div class="d-flex">
                                <label class="mt-2" style="white-space: nowrap;">Loại Khuyến Mãi</label>
                                <a-select v-model:value="newItem.loaiKhuyenMai" class=" mx-5" style="width: 30%;">
                                    <a-select-option value="phanTram">Theo phần trăm (%)</a-select-option>
                                    <a-select-option value="soTien">Theo số tiền (VNĐ)</a-select-option>
                                </a-select>

                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3 d-flex">
                            <div>
                                <div class="d-flex ">
                                    <label class="mt-3" style="white-space: nowrap;" for="ngayBatDau">Từ
                                        ngày:</label>

                                    <input v-model="newItem.ngayBatDau" type="date" @blur="validateField('ngayBatDau')"
                                        @input="validateField('ngayBatDau')" id="ngayBatDau"
                                        class="datepicker-custom ms-5 me-3">
                                </div>
                                <span v-if="errors.ngayBatDau" style="margin-left: 150px;"
                                    class="text-danger error-message">{{
                                        errors.ngayBatDau }}</span>
                            </div>
                            <div>
                                <div class="form-group mb-3">
                                    <div class="d-flex ">
                                        <label class="mt-3" style="white-space: nowrap;" for="ngayKetThuc">Đến
                                            :</label>
                                        <input v-model="newItem.ngayKetThuc" type="date"
                                            @blur="validateField('ngayKetThuc')" @input="validateField('ngayKetThuc')"
                                            id="ngayKetThuc" class="datepicker-custom me-2">
                                    </div>
                                    <span v-if="errors.ngayKetThuc" style="margin-left: 40px;"
                                        class="text-danger error-message">{{
                                            errors.ngayKetThuc }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 my-3 ">
                        <div class="d-flex ms-5">
                            <label style="white-space: nowrap;" class="mt-3">
                                {{ newItem.loaiKhuyenMai === 'phanTram' ? 'Giá Trị Giảm (%)' : 'SốTiền'
                                    +
                                    'Giảm(VNĐ)' }}
                            </label>
                            <input @input="formatGiaTriGiam" @blur="validateGiaTriGiam" v-model="formattedGiaTriGiam"
                                style="width: 30%;" type="text" class="form-control custom-input mx-5"
                                :placeholder="newItem.loaiKhuyenMai === 'phanTram' ? 'Nhập phần trăm giảm' : 'Nhập số tiền giảm'" />
                        </div>

                        <span v-if="errors.giaTriGiam" style="margin-left: 160px; white-space: nowrap;"
                            class="text-danger">{{
                                errors.giaTriGiam
                            }}</span>

                    </div>
                </div>
                <span v-if="errors.thoiGianTrungLap" style="margin-left: 160px;" class="text-danger error-message">{{
                    errors.thoiGianTrungLap
                }}</span>
                <div class="row">
                    <div class="col-md-6 my-3">
                        <label>Mô Tả</label>
                        <textarea v-model="newItem.moTa" class="form-control"></textarea>
                    </div>
                    <div class="col-md-6 my-3">
                        <div class="form-group d-flex align-items-center mb-3 ms-5">
                            <p class="me-3 mb-0">Trạng Thái</p>
                            <div class="d-flex">
                                <div class="form-check me-3 mt-2">
                                    <input v-model="newItem.trangThai" class="form-check-input" type="radio"
                                        :value="true" id="trangThaiKichHoat">
                                    <label class="form-check-label" for="trangThaiKichHoat">Kich hoạt</label>
                                </div>
                                <div class="form-check mt-2">
                                    <input v-model="newItem.trangThai" class="form-check-input" type="radio"
                                        :value="false" id="trangThaiKetThuc">
                                    <label class="form-check-label" for="trangThaiKetThuc">Chưa áp dụng</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <span v-if="errors.chonspct" class="text-danger">{{ errors.chonspct
                    }}</span>
                </div>
                <hr class="border-success custom-hr">
                <div class="form-group">
                    <div class="form-group">
                        <label>Sản phẩm áp dụng</label>
                        <button type="button" class="btn btn-info mx-3"
                            @click="isDetaiSPCTVisible = !isDetaiSPCTVisible">
                            <i class="bi bi-card-list small"></i> Chọn sản phẩm
                        </button>
                    </div>
                    <!-- Danh sách sản phẩm chi tiết đã chọn -->
                    <div class="d-flex flex-wrap">
                        <span v-for="(sp, index) in selectedSanPhamChiTietIds" :key="sp.id"
                            class="d-inline-block me-3 small">
                            {{ sp.tenAoDai }} ({{ sp.maAoDaiChiTiet }} - {{ sp.kichThuoc }} - {{ sp.mauSac }})
                            <a href="#" @click.prevent="removeSelectedProduct(sp.id)" class="text-decoration-none">
                                <p class="mb-0 d-inline text-danger" style="font-size: 20px;"> X</p>
                            </a>
                            <span v-if="index < selectedSanPhamChiTietIds.length - 1">, </span>
                        </span>
                    </div>



                </div>
            </form>
            <div class="d-flex justify-content-end gap-2 mt-3">
                <button type="submit" @click="submitForm" class="btn btn-outline-success"> <i
                        class="bi bi-floppy me-2"></i>Lưu</button>
                <button type="button" class="btn btn-outline-secondary" @click="closeFrom">Bỏ qua</button>
            </div>

        </div>
    </div>
    <!-- End form offcanvas -->
</template>

<script>
import axios from "axios";
import { notification, Modal } from 'ant-design-vue';
import { computed } from 'vue';
import { mapState } from 'vuex';

export default {
    computed: {
        ...mapState(['roles']),
        isAdmin() {
            return this.roles.includes('ROLE_QUANLY');
        }
    },
    data() {
        return {
            userRole: 2,
            selectedSanPhamChiTietIds: [],
            previouslyDeselectedProducts: new Set(),
            isFrom1: false,
            isDetailVisible1: true,
            statusFilter: '1',
            validityFilter: '1',
            start: null,
            end: null,
            startDate: "",
            endDate: "",
            errors: {},
            isEditing: false,
            searchQuery: "",
            searchResults: [],
            searchKey: '',
            searchKeySpct: '',
            khuyenMais: [],
            totalPages: 0,
            currentPage: 0,
            totalPages1: 0,
            currentPage1: 0,
            giaTriGiam: 0,
            pageSize: 5,
            formattedGiaTriGiam: "",
            isEditVisible1: false,
            isAddVisible1: false,
            selectedItem: null,
            isDetaiSPCTVisible: false,
            newItem: {
                id: null,
                tenKhuyenMai: '',
                loaiKhuyenMai: '',
                soTienGiam: '',
                phanTramGiam: '',
                ngayBatDau: '',
                ngayKetThuc: '',
                moTa: '',
                trangThai: true,
                sanPhamChiTietIds: []
            },
            selectedSanPhamChiTietIds: [],

        };
    },

    created() {
        this.fetchSanPhamCT();
    },
    mounted() {
        this.searchKhuyenMai();
    },
    methods: {

        getGiaGocThapNhat() {
            if (!this.sanPhamChiTietIds || this.sanPhamChiTietIds.length === 0) {
                return "Không có sản phẩm";
            }
            const minGiaGoc = Math.min(...this.sanPhamChiTietIds.map(sp => sp.giaGoc));
            return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(minGiaGoc);
        },
        validateField(field) {
            this.errors[field] = '';
            if (field === 'tenKhuyenMai' && !this.newItem.tenKhuyenMai.trim()) {
                this.errors.tenKhuyenMai = "Tên khuyến mãi không được để trống.";
            }
            if (field === 'ngayBatDau' && !this.newItem.ngayBatDau) {
                this.errors.ngayBatDau = "Chọn ngày bắt đầu.";
            }
            if (field === 'ngayKetThuc') {
                if (!this.newItem.ngayKetThuc) {
                    this.errors.ngayKetThuc = "Chọn ngày kết thúc.";
                } else if (new Date(this.newItem.ngayKetThuc) < new Date(this.newItem.ngayBatDau)) {
                    this.errors.ngayKetThuc = "Thời gian kết thúc không thể trước ngày bắt đầu.";
                }
            }
            if (field === 'giaTriGiam') {
                if (!this.newItem.giaTriGiam) {
                    this.errors.giaTriGiam = "Vui lòng nhập giá trị giảm.";
                } else if (this.newItem.loaiKhuyenMai === 'phanTram') {
                    if (this.newItem.giaTriGiam <= 0 || this.newItem.giaTriGiam > 100) {
                        this.errors.giaTriGiam = "Giá trị giảm phải từ 1% đến 100%.";
                    }
                } else if (this.newItem.loaiKhuyenMai === 'soTien') {
                    if (this.newItem.giaTriGiam <= 0) {
                        this.errors.giaTriGiam = "Giá trị giảm phải lớn hơn 0 VNĐ.";
                    }

                }
            }
        },

        submitForm() {


            const fieldsToValidate = [
                'tenKhuyenMai',
                'ngayBatDau',
                'ngayKetThuc',
                'giaTriGiam',
            ];

            fieldsToValidate.forEach(field => {
                this.validateField(field);
            });
            this.validateGiaTriGiam();
            const hasErrors = Object.values(this.errors).some(error => error && error.trim() !== "");

            if (hasErrors) {
                notification.error({
                    message: 'Lỗi xác thực',
                    description: 'Vui lòng kiểm tra lại các trường dữ liệu và thử lại.',
                    duration: 3,
                });
                return;
            }

            Modal.confirm({
                title: 'Xác nhận lưu',
                content: 'Bạn có chắc chắn muốn lưu thông tin này?',
                okText: 'Lưu',
                cancelText: 'Hủy',
                onOk: () => {
                    const form = document.getElementById("myForm");
                    if (form) {
                        form.dispatchEvent(new Event("submit", { bubbles: true }));
                    }
                },
                onCancel: () => {
                    notification.info({
                        message: 'Hủy thao tác',
                        description: 'Thao tác lưu đã bị hủy.',
                        duration: 2,
                    });
                },
            });

            this.isDetaiSPCTVisible = false;
        }

        ,
        validateGiaTriGiam() {
            this.errors.giaTriGiam = '';

            if (this.newItem.loaiKhuyenMai === 'phanTram') {
                if (!this.newItem.giaTriGiam || this.newItem.giaTriGiam <= 0 || this.newItem.giaTriGiam > 100) {
                    this.errors.giaTriGiam = "Giá trị giảm phải từ 1% đến 100%.";
                }
            } else if (this.newItem.loaiKhuyenMai === 'soTien') {
                if (!this.newItem.giaTriGiam || this.newItem.giaTriGiam <= 0) {
                    this.errors.giaTriGiam = "Số tiền giảm phải lớn hơn 0 VNĐ.";
                } else {
                    const invalidProducts = this.selectedSanPhamChiTietIds.filter(sp =>
                        this.newItem.giaTriGiam >= sp.giaGoc
                    );
                    if (invalidProducts.length > 0) {
                        const minGiaGoc = Math.min(...invalidProducts.map(sp => sp.giaGoc));
                        const productWithMinPrice = invalidProducts.find(sp => sp.giaGoc === minGiaGoc);
                        this.errors.giaTriGiam = `Tiền giảm phải nhỏ hơn giá gốc ${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(minGiaGoc)} (${productWithMinPrice.maAoDaiChiTiet})`;
                    }
                }
            }
        }

        ,
        formatGiaTriGiam() {
            if (!this.formattedGiaTriGiam) {
                this.newItem.giaTriGiam = null;
                return;
            }
            let rawNumber = this.formattedGiaTriGiam.replace(/\D/g, '');
            this.newItem.giaTriGiam = rawNumber ? parseFloat(rawNumber) : null;
            this.formattedGiaTriGiam = new Intl.NumberFormat('vi-VN').format(this.newItem.giaTriGiam);
            this.validateGiaTriGiam();
        },
        formatCurrency(value) {
            return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
        },
        formatDateToYYYYMMDD(date) {
            if (!date) return "";
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, "0");
            const day = String(d.getDate()).padStart(2, "0");
            return `${year}-${month}-${day}`;
        },
        onStartChange(date) {
            this.startDate = this.formatDateToYYYYMMDD(date);
            this.searchKhuyenMai();
        },
        onEndChange(date) {
            this.endDate = this.formatDateToYYYYMMDD(date);
            this.searchKhuyenMai();
        },
        searchKhuyenMai(page = 0) {
            if (!this.searchKey.trim() && !this.statusFilter && !this.validityFilter) {
                this.fetchKhuyenMais(page);
                return;
            }
            let params = {
                keyq: this.searchKey.trim() || null,
                status: this.statusFilter !== "null" ? this.statusFilter : null,
                validity: this.validityFilter !== "null" ? this.validityFilter : null,
                page: page,
                startDate: this.startDate || null,
                endDate: this.endDate || null,
                pageSize: this.pageSize
            };

            axios.get('/api/khuyen-mai/tim-kiem-khuyen-mai', { params })
                .then((response) => {
                    this.khuyenMais = response.data.content.sort((a, b) => {
                        return new Date(b.ngayTao) - new Date(a.ngayTao);
                    });
                    this.totalPages = response.data.page.totalPages;
                    this.currentPage = page;
                })
                .catch((error) => {
                    console.error('Có lỗi xảy ra khi tìm kiếm:', error);
                });
        },
        getValidityStatus(km) {
            const currentDate = new Date();
            const startDate = new Date(km.ngayBatDau);
            const endDate = new Date(km.ngayKetThuc);

            if (currentDate < startDate) {
                return "3";
            } else if (currentDate > endDate) {
                return "2";
            } else {
                return "1";
            }
        },
        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleString('vi-VN', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            });
        },

        fetchKhuyenMais(page = 0) {

            axios
                .get(`/api/khuyen-mai/hien-thi?page=${page}&pageSize=${this.pageSize}`)
                .then((response) => {
                    this.khuyenMais = response.data.content;
                    this.totalPages = response.data.totalPages;
                    this.currentPage = page;
                    this.khuyenMais.sort((a, b) => {
                        return new Date(b.ngayTao) - new Date(a.ngayTao);
                    });

                })
                .catch((error) => console.error(error));
        },
        fetchKhuyenMaisWithCallback(callback) {
            axios
                .get(`/api/khuyen-mai/hien-thi?page=0&pageSize=${this.pageSize}`)
                .then((response) => {
                    this.khuyenMais = response.data.content;
                    this.totalPages = response.data.totalPages;
                    this.currentPage = 0;
                    if (callback) {
                        callback();
                    }
                })
                .catch((error) => console.error(error));
        },
        openEditFormKM(km) {
            this.errors = {};
            this.isAddVisible1 = true;
            this.isDetailVisible1 = false;
            this.isFrom1 = true;
            this.isEditing = true;
            this.selectedItem = { ...km };
            this.newItem = {
                id: km.id,
                tenKhuyenMai: km.tenKhuyenMai,
                maKhuyenMai: km.maKhuyenMai,
                loaiKhuyenMai: km.phanTramGiam > 0 ? 'phanTram' : 'soTien',
                giaTriGiam: km.phanTramGiam > 0 ? km.phanTramGiam : km.soTienGiam,
                ngayBatDau: km.ngayBatDau,
                ngayKetThuc: km.ngayKetThuc,
                moTa: km.moTa,
                trangThai: km.trangThai,
                sanPhamChiTietIds: []
            };
            axios.get(`/api/khuyen-mai/lay-san-pham-chi-tiet?idKhuyenMai=${km.id}`)
                .then(response => {
                    this.selectedSanPhamChiTietIds = response.data;
                })
                .catch(error => {
                    console.error("Lỗi khi lấy danh sách sản phẩm chi tiết:", error);
                });

            this.formattedGiaTriGiam = new Intl.NumberFormat('vi-VN').format(this.newItem.giaTriGiam);

        },
        openAddForm() {
            this.isAddVisible1 = true;
            this.isDetailVisible1 = false;
            this.isFrom1 = true;
            this.errors = {};
            this.clearFormData();
        },

        async addKhuyenMai() {

            const khuyenMaiData = {
                tenKhuyenMai: this.newItem.tenKhuyenMai,
                phanTramGiam: this.newItem.loaiKhuyenMai === 'phanTram' ? this.newItem.giaTriGiam : 0,
                soTienGiam: this.newItem.loaiKhuyenMai === 'soTien' ? this.newItem.giaTriGiam : 0,
                ngayBatDau: this.newItem.ngayBatDau,
                ngayKetThuc: this.newItem.ngayKetThuc,
                trangThai: this.newItem.trangThai,
                moTa: this.newItem.moTa,
                sanPhamChiTietIds: this.selectedSanPhamChiTietIds.map(sp => sp.id)
            };
            if (this.isEditing) {
                this.updateKhuyenMai();
                return;
            }
            try {
                const response = await axios.post(
                    "/api/khuyen-mai/them",
                    khuyenMaiData
                );
                if (response.status === 200) {
                    notification.success({ message: 'Thêm khuyến mãi thành công!' });
                    this.closeFrom();
                    this.searchKhuyenMai();
                    this.isAddVisible = false;
                    this.isDetailVisible = true;
                    this.isFrom = false;
                }
            } catch (error) {
                if (error.response?.status === 400 && error.response.data.errors) {
                    // gán tất cả lỗi field-level vào this.errors
                    this.errors = { ...error.response.data.errors };
                    notification.error({
                        message: 'Lỗi xác thực',
                        description: 'Vui lòng kiểm tra lại các trường dữ liệu.'
                    });
                } else {
                    console.error("Lỗi khi thêm khuyến mãi:", error);
                }
            }
        },
        async updateKhuyenMai() {
            const khuyenMaiData = {
                id: this.newItem.id,
                tenKhuyenMai: this.newItem.tenKhuyenMai,
                phanTramGiam: this.newItem.loaiKhuyenMai === 'phanTram' ? this.newItem.giaTriGiam : 0,
                soTienGiam: this.newItem.loaiKhuyenMai === 'soTien' ? this.newItem.giaTriGiam : 0,
                ngayBatDau: this.newItem.ngayBatDau,
                ngayKetThuc: this.newItem.ngayKetThuc,
                trangThai: this.newItem.trangThai,
                moTa: this.newItem.moTa,
                sanPhamChiTietIds: this.selectedSanPhamChiTietIds.map(sp => sp.id)
            };

            try {
                const response = await axios.put("/api/khuyen-mai/cap-nhat", khuyenMaiData)
                if (response.status === 200) {
                    notification.success({ message: 'Cập nhật khuyến mãi thành công!' });
                    this.closeFrom();
                    this.searchKhuyenMai();
                    this.isAddVisible = false;
                    this.isDetailVisible = true;
                    this.isFrom = false;
                }
            } catch (error) {
                if (error.response?.status === 400 && error.response.data.errors) {
                    this.errors = { ...error.response.data.errors };
                    notification.error({
                        message: 'Lỗi xác thực',
                        description: 'Vui lòng kiểm tra lại các trường dữ liệu.'
                    });
                } else {
                    console.error("Lỗi khi cập nhật khuyến mãi:", error);
                }
            }
        },

        fetchSanPhamCT(page = 0) {
            axios
                .get(`/api/khuyen-mai/spct?page=${page}`)
                .then((response) => {
                    this.sanPhamChiTietIds = response.data.content;
                    this.totalPages1 = response.data.totalPages;
                    this.currentPage1 = page;
                })
                .catch((error) => console.error(error));
        },

        searchSpct(page = 0) {
            if (this.searchKeySpct.trim() === "") {
                this.fetchSanPhamCT(page);
            } else {
                axios.get('/api/khuyen-mai/tim-kiem-spct', {
                    params: {
                        keyq: this.searchKeySpct.trim(),
                        page: page
                    }
                })
                    .then((response) => {
                        this.sanPhamChiTietIds = response.data.content;
                        this.totalPages1 = response.data.page.totalPages;
                        this.currentPage1 = page;
                    })
                    .catch((error) => {
                        console.error('Có lỗi xảy ra khi tìm kiếm:', error);
                    });
            }
        }
        ,
        showDetailsKM(km) {
            this.selectedItem = km;
            this.isDetailVisible1 = true;
            this.isEditVisible1 = false;
            axios.get(`/api/khuyen-mai/lay-san-pham-chi-tiet?idKhuyenMai=${km.id}`)
                .then(response => {
                    this.selectedSanPhamChiTietIds = response.data;
                })
                .catch(error => {
                    console.error("Lỗi khi lấy danh sách sản phẩm chi tiết:", error);
                });


        },
        closeDetail() {
            this.isDetailVisible1 = false;
        },
        toggleCheckbox(spct) {
            const isInCurrentPromotion = this.newItem.sanPhamChiTietIds && this.newItem.sanPhamChiTietIds.includes(spct.id);
            const index = this.selectedSanPhamChiTietIds.findIndex(item => item.id === spct.id);

            // Đã chọn => bỏ chọn ngay
            if (index !== -1) {
                this.selectedSanPhamChiTietIds.splice(index, 1);
                this.previouslyDeselectedProducts.add(spct.id);
                return;
            }

            // Nếu thuộc chương trình hiện tại hoặc từng bỏ chọn => thêm ngay
            if (isInCurrentPromotion || this.previouslyDeselectedProducts.has(spct.id)) {
                this.selectedSanPhamChiTietIds.push(spct);
                return;
            }

            // Gọi API kiểm tra sản phẩm thuộc CT khác không
            axios.get(`/api/khuyen-mai/kiem-tra-san-pham-khuyen-mai`, {
                params: { sanPhamIds: spct.id }
            }).then((response) => {
                if (!response.data || response.data.length === 0) {
                    // Không thuộc chương trình nào => thêm ngay
                    this.selectedSanPhamChiTietIds.push(spct);
                } else {
                    // Đã thuộc chương trình khác => hiển thị confirm
                    Modal.confirm({
                        title: 'Đã có trong chương trình khác!',
                        content: `Sản phẩm ${spct.maAoDaiChiTiet} đã nằm trong chương trình khuyến mãi khác. Bạn có muốn chuyển sang chương trình này không?`,
                        icon: 'warning',
                        okText: 'Có, thêm tiếp',
                        cancelText: 'Hủy',
                        onOk: () => {
                            this.addOrRemoveProduct(spct); // ✅ Chỉ khi người dùng đồng ý mới thêm
                        },
                        onCancel: () => {
                            // ❌ Không thêm gì hết -> giữ nguyên trạng checkbox (chưa chọn)
                            // Vì chưa thêm vào selected list => tự động hiển thị không tích
                        }
                    });
                }
            }).catch((error) => {
                console.error("Lỗi khi kiểm tra sản phẩm:", error);
            });
        }
        ,
        removeSelectedProduct(id) {
            this.selectedSanPhamChiTietIds = this.selectedSanPhamChiTietIds.filter(sp => sp.id !== id);
        },
        toggleSelection(productId) {
            return this.selectedSanPhamChiTietIds.some(item => item.id === productId);
        },
        addOrRemoveProduct(spct) {
            const index = this.selectedSanPhamChiTietIds.findIndex(item => item.id === spct.id);
            if (index === -1) {
                this.selectedSanPhamChiTietIds.push(spct);
            } else {
                this.selectedSanPhamChiTietIds.splice(index, 1);
            }
        }
        ,
        clearFormData() {
            this.errors = {};
            this.isEditing = false;
            this.formattedGiaTriGiam = "";
            this.selectedSanPhamChiTietIds = [];
            this.newItem = {
                tenKhuyenMai: "",
                maKhuyenMai: "",
                giaTriGiam: "",
                phanTramGiam: "",
                loaiKhuyenMai: "phanTram",
                ngayBatDau: "",
                ngayKetThuc: "",
                moTa: "",
                trangThai: true,
                sanPhamChiTietIds: []
            };
        },
        closeFrom() {
            this.isAddVisible1 = false;
            this.isDetailVisible1 = true;
            this.isFrom1 = false;
            this.errors = {};
            this.isEditing = false;
            this.formattedGiaTriGiam = "";
            this.selectedSanPhamChiTietIds = [];
            this.newItem = {
                tenKhuyenMai: "",
                maKhuyenMai: "",
                phanTramGiam: "",
                loaiKhuyenMai: "phanTram",
                ngayBatDau: "",
                ngayKetThuc: "",
                moTa: "",
                trangThai: true,
                sanPhamChiTietIds: []
            };
        }

    }

};
</script>

<style scoped>
.datepicker-custom {
    appearance: none;
    background: #fff url('https://cdn-icons-png.flaticon.com/512/2920/2920346.png') no-repeat right 10px center;
    background-size: 20px;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 8px 12px;
    font-size: 16px;
    width: 200px;
    /* Cố định chiều rộng để gọn gàng */
    cursor: pointer;
    transition: border-color 0.3s ease;
}

.datepicker-custom:focus {
    border-color: #1890ff;
    outline: none;
    box-shadow: 0 0 5px rgba(24, 144, 255, 0.5);
}

#addKhuyenMaiOffcanvas {
    width: 50vw !important;
    /* 50% chiều rộng của màn hình */
}

.product-list {
    margin-top: 10px;
}

.table {
    width: 100%;
    border-collapse: collapse;
}

.table thead {
    display: table;
    width: calc(100% - 17px);
    /* Trừ đi độ rộng của scrollbar để tránh lệch */
    table-layout: fixed;
    background-color: #f1f1f1;
    position: sticky;
    top: 0;
    z-index: 1;
}

.table tbody {
    display: block;
    max-height: 450px;
    overflow-y: auto;
    width: 100%;
}

.table tbody tr {
    display: table;
    width: 100%;
    table-layout: fixed;
}

.table th,
.table td {
    padding: 8px;
    border: 1px solid #ddd;
    text-align: center;
    font-size: 13px;
}

.table tbody td {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
}



.table-spct-overlay {
    position: fixed;
    top: 30%;
    overflow-y: auto;
    /* Cuộn khi nội dung quá dài */
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(255, 255, 255, 0.95);
    border: 1px solid #ddd;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    width: 40%;
    min-height: 45vh;
    max-height: 100vh;
    overflow-x: auto;
}

.fom-group-r {
    background: rgba(255, 255, 255, 0.98);
    margin-right: 20px;
    /* Viền cho dễ nhìn */
    /* Bo góc nhẹ */
    /* Đổ bóng */
    /* Màu nền nhẹ */
}

.form-group1 {
    margin-top: 115px;
    vertical-align: top;
    /* Căn thẳng hàng trên */
    padding: 10px;
    border: 1px solid #ccc;
    /* Viền cho dễ nhìn */
    border-radius: 5px;
    /* Bo góc nhẹ */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    /* Đổ bóng */
    background-color: #f9f9f9;
    /* Màu nền nhẹ */

}

.fom-home {
    max-width: 100vw;
    overflow-x: hidden;
}

.form-group2 {
    margin-top: 50px;
    /* Căn thẳng hàng trên */
    padding: 10px;
    border: 1px solid #ccc;
    /* Viền cho dễ nhìn */
    border-radius: 5px;
    /* Bo góc nhẹ */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    /* Đổ bóng */
    background-color: #f9f9f9;
    min-width: 215px;
    /* Màu nền nhẹ */
    min-height: 500px;
}

.form-group3 {

    vertical-align: top;
    /* Căn thẳng hàng trên */
    border: 1px solid #ccc;
    /* Viền cho dễ nhìn */
    border-radius: 5px;
    /* Bo góc nhẹ */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    /* Đổ bóng */
    background-color: #f9f9f9;
    /* Màu nền nhẹ */
}

.offcanvas-body {
    overflow-x: hidden;
    white-space: normal;
}



.form-control {
    width: 100%;
    /* Đảm bảo input chiếm toàn bộ chiều rộng */
}

.form-check {
    margin-bottom: 10px;
    /* Tạo khoảng cách giữa các radio button */
}

.close-btn {
    position: absolute;
    top: 10px;
    right: 10px;
}

.offcanvas-body {
    overflow-x: hidden;
    white-space: normal;
}

.fom-form {
    padding: 10px;
    border: 1px solid #ccc;
    /* Viền cho dễ nhìn */
    border-radius: 5px;
    /* Bo góc nhẹ */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    /* Đổ bóng */
    background-color: #f9f9f9;
    max-width: 80vw;
    overflow-y: hidden;
    min-height: 600px;
}

.offcanvas.offcanvas-body {
    top: 50%;
    /* Đặt ngang tầm 50% chiều cao */
    left: 50%;
    /* Đặt ngang tầm 50% chiều rộng */
    transform: translate(-50%, -50%);
    min-height: 90vh;
    width: 50vw !important;
    background-color: #fff;
    /* Màu nền trắng cho form */
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    /* Bóng đổ cho form */
    border-radius: 8px;

}

.custom-offcanvas {
    width: 500px;
    /* Đặt chiều rộng nhỏ gọn hơn */
}

.custom-offcanvas .offcanvas-body {
    font-size: 16px;
}

.custom-offcanvas hr {
    border-top: 1px solid #ddd;
    margin: 10px 0;
}

.custom-offcanvas .btn {
    min-width: 100px;
}

/* Tùy chỉnh input với chỉ viền dưới */
.custom-input {
    background-color: #f9f9f9;
    border: none;
    /* Loại bỏ viền mặc định */
    border-bottom: 2px solid #007bff;
    /* Đường viền dưới thẳng */
    outline: none;
    /* Loại bỏ hiệu ứng viền khi focus */
    border-radius: 0;
    /* Đảm bảo không có độ cong */
    width: 100%;
    /* Đảm bảo input chiếm toàn bộ chiều rộng */
    padding: 0.375rem 0.75rem;
    /* Khoảng cách trong input */
}

/* Khi focus, giữ chỉ đường viền dưới, không có viền xung quanh */
.custom-input:focus {
    border: none;
    /* Loại bỏ viền khi focus */
    border-bottom: 2px solid #df551a;
    /* Màu sắc khi focus */
    box-shadow: none;
    /* Loại bỏ bóng mờ khi focus (để không có viền xung quanh) */
}

.pagination {
    display: flex;
    justify-content: center;
    /* Căn giữa ngang */
    align-items: center;
    /* Căn giữa dọc */
    gap: 10px;
    /* Khoảng cách giữa các nút */
    margin-top: auto;
    /* Đẩy phần pagination xuống dưới */
    position: absolute;
    bottom: 10px;
    /* Điều chỉnh khoảng cách với đáy */
    left: 50%;
    transform: translateX(-50%);
    width: 100%;
}

.pagination-container {
    left: 60%;
    transform: translateX(-50%);
    position: fixed;
    bottom: 20px;
    width: 295px;
    padding: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    max-height: 30px;
}

.error-message {
    position: absolute;
    font-size: 0.875rem;
    color: #e74c3c;
    white-space: nowrap;
    /* Ngăn lỗi kéo dài chiều rộng div */
    max-width: 90%;
    /* Giới hạn chiều rộng */
    overflow: hidden;
    visibility: visible;
}

/* Thêm độ dày cho đường kẻ hr */
.custom-hr {
    border-style: dashed;
    /* Tạo kiểu nét đứt */
    border-width: 1px;
    /* Tăng độ dày viền */
}
</style>