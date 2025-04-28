<template>
    <div v-if="isDetailVisible" class="container d-flex fom-home">
        <div class="d-flex">
            <div class="row">
                <div class="fom-group-r p-0 col-2">
                    <h3 class=" d-flex justify-content-center badge text-bg-info"
                        style="font-size: 1.8rem; padding: 10px 20px;">Giảm giá</h3>

                    <div class="form-group2">
                        <h5 class="">Trạng thái</h5>
                        <hr>

                        <div class="form-check m-3">
                            <input class="form-check-input" type="radio" @change="searchGiamGia()" name="statusFilter"
                                id="statusActive" value="1" v-model="statusFilter" />
                            <label class="form-check-label" for="statusActive" checked>Kích hoạt</label>
                        </div>
                        <div class="form-check mt-4 ms-3">
                            <input class="form-check-input" type="radio" @change="searchGiamGia()" name="statusFilter"
                                id="statusInactive" value="0" v-model="statusFilter" />
                            <label class="form-check-label" for="statusInactive">Chưa áp dụng</label>
                        </div>

                        <h5 class="mt-5">Hiệu lực</h5>
                        <hr>

                        <div class="form-check m-3">
                            <input class="form-check-input" type="radio" @change="searchGiamGia()" name="validityFilter"
                                id="validActive" value="1" v-model="validityFilter" />
                            <label class="form-check-label" for="validActive">Còn hiệu lực</label>
                        </div>
                        <div class="form-check ms-3 mt-4">
                            <input class="form-check-input" type="radio" @change="searchGiamGia()" name="validityFilter"
                                id="validExpired" value="2" v-model="validityFilter" />
                            <label class="form-check-label" for="validExpired">Hết hiệu lực</label>
                        </div>
                        <div class="form-check ms-3 mt-4">
                            <input class="form-check-input" type="radio" @change="searchGiamGia()" name="validityFilter"
                                id="validNotStarted" value="3" v-model="validityFilter" />
                            <label class="form-check-label" for="validNotStarted">Chưa bắt đầu</label>
                        </div>
                    </div>
                </div>

                <div class="form-group3 col-9">
                    <div class=" d-flex flex-column align-items-center justify-content-center">
                        <!-- Dòng chứa ô tìm kiếm và nút thêm -->
                        <div class="row w-100 mb-3 d-flex align-items-center mt-3">
                            <!-- Ô tìm kiếm -->
                            <div class="col-4 d-flex">
                                <input type="search" class="form-control" placeholder="Tìm theo mã + tên + giá trị giảm"
                                    v-model="searchKey" @keyup="searchGiamGia(0)" />
                                <!-- <button id="voice-btn"
                                    class="btn btn-light ms-2" @click="startVoiceSearch">🎤</button> -->
                            </div>
                            <div class="col-4 d-flex">
                                <a-date-picker class="me-1" style=" min-height: 40px;" v-model:value="start"
                                    format="DD/MM/YYYY" @change="onStartChange" placeholder="Chọn ngày bắt đầu" />
                                <a-date-picker v-model:value="end" format="DD/MM/YYYY" @change="onEndChange"
                                    placeholder="Chọn ngày kết thúc" />
                            </div>

                            <div class="col-2">
                                <div class="form-group d-flex">
                                    <label for="pageSize" style="white-space: nowrap; font-size: 14px;" class="mt-2">Số
                                        bản ghi:</label>
                                    <select style="max-width: 100px; max-height: 40px;font-size: 15px; "
                                        class="form-select" id="pageSize" v-model="pageSize" @change="searchGiamGia(0)">
                                        <option value="5">5</option>
                                        <option value="10">10</option>
                                    </select>
                                </div>
                            </div>
                            <!-- Nút thêm lệch trái -->
                            <div class="col-2 d-flex justify-content-start">
                                <button v-if="isAdmin" @click="openAddForm" class="btn btn-info">
                                    <i class="bi bi-plus-circle"></i>Giảm giá
                                </button>
                            </div>
                        </div>
                    </div>


                    <div class=" mt-4">
                        <div class="table-responsive">
                            <table class="table table-bordered table-hover">
                                <thead>
                                    <tr class="table-primary">
                                        <th>Mã giảm giá</th>
                                        <th>Tên giảm giá</th>
                                        <th>Giá trị giảm</th>
                                        <th>Từ ngày</th>
                                        <th>Đến ngày </th>
                                        <th>Mô tả</th>
                                        <th>Trạng thái</th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-if="giamGias.length === 0">
                                        <td colspan="8" class="text-center text-muted">Không có thông tin nào</td>
                                    </tr>
                                    <tr v-for="gg in giamGias" :key="gg.id">
                                        <td>{{ gg.maGiamGia }}</td>
                                        <td>{{ gg.tenGiamGia }}</td>
                                        <td>{{ formatCurrency(gg.giaTriGiam) }} {{ gg.loaiGiamGia == 0 ? "VNĐ" : "%" }}
                                        </td>
                                        <td>{{ formatDate(gg.ngayBatDau) }}</td>
                                        <td>{{ formatDate(gg.ngayKetThuc) }}</td>
                                        <td>{{ gg.moTa }}</td>
                                        <td>
                                            <span :class="gg.trangThai === true ? 'text-success' : 'text-danger'">
                                                {{ gg.trangThai ? "Kích hoạt" : "Chưa áp dụng" }}
                                            </span>
                                        </td>
                                        <td>
                                            <button v-if="isAdmin" class="btn btn-outline-warning"
                                                @click="openEditForm(gg)"><i class="bi bi-pencil-square"></i>
                                            </button><button class="btn btn-outline-danger" @click="showDetails(gg)"
                                                data-bs-toggle="offcanvas" data-bs-target="#detailOffcanvas">
                                                <i class="bi bi-eye"></i>
                                            </button>

                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <div class="pagination-container">
                            <div class="pagination">
                                <button :disabled="currentPage === 0" @click="searchGiamGia(0)"
                                    class="btn btn-info  m-1">
                                    <i class="bi bi-chevron-double-left"></i>
                                </button>
                                <button :disabled="currentPage === 0" @click="searchGiamGia(currentPage - 1)"
                                    class="btn btn-info  m-1">
                                    <i class="bi bi-chevron-left"></i>
                                </button>
                                <span class="mt-2"> {{ totalPages === 0 ? '0 / 0' : (currentPage + 1) + ' / ' +
                                    totalPages }}</span>
                                <button :disabled="currentPage >= totalPages - 1"
                                    @click="searchGiamGia(currentPage + 1)" class="btn btn-info  m-1">
                                    <i class="bi bi-chevron-right"></i>
                                </button>
                                <button :disabled="currentPage >= totalPages - 1" @click="searchGiamGia(totalPages - 1)"
                                    class="btn btn-info  m-1">
                                    <i class="bi bi-chevron-double-right"></i>
                                </button>
                            </div>
                        </div>
                        <!-- Chi Tiết Giảm giá -->
                        <div class="offcanvas offcanvas-body custom-offcanvas" tabindex="-1" id="detailOffcanvas">
                            <div class="offcanvas-header bg-info text-white">
                                <h5 class="offcanvas-title mx-auto">Chi Tiết Giảm Giá</h5>
                                <button type="button" class="btn-close btn-close-white"
                                    data-bs-dismiss="offcanvas"></button>
                            </div>
                            <div class="offcanvas-body">
                                <div class="mb-3">
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Tên Giảm Giá:</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ selectedItem?.tenGiamGia }}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Mã Giảm Giá:</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ selectedItem?.maGiamGia }}</p>
                                        </div>
                                    </div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="mb-3">
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>{{ selectedItem?.loaiGiamGia === 0 ? "Giảm theo phần trăm:" :
                                                "Giảm theo tiền:" }}</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ formatCurrency(selectedItem?.giaTriGiam) }} {{
                                                selectedItem?.loaiGiamGia == 1 ? "%" : "VNĐ" }}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-4" v-if="newItem.loaiGiamGia == '0'">
                                            <p><strong>Số tiền giảm tối đa:</strong></p>
                                        </div>
                                        <div class="col-8" v-if="newItem.loaiGiamGia == '0'">
                                            <p v-if="newItem.loaiGiamGia == '0'">{{
                                                formatCurrency(selectedItem?.toiDaGiamGia) }} VNĐ</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Giá trị đơn hàng tối thiểu:</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ formatCurrency(selectedItem?.giaTriToiThieu) }} VNĐ</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Ngày bắt đầu:</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ formatDate(selectedItem?.ngayBatDau) }}</p>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Đến :</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ formatDate(selectedItem?.ngayKetThuc) }}</p>
                                        </div>
                                    </div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="mb-3">
                                    <div class="row">
                                        <div class="col-4">
                                            <p><strong>Mô tả:</strong></p>
                                        </div>
                                        <div class="col-8">
                                            <p>{{ selectedItem?.moTa }}</p>
                                        </div>
                                    </div>
                                </div>
                                <hr class="border-success custom-hr">
                                <div class="row">
                                    <div class="col-4">
                                        <p><strong>Trạng thái:</strong></p>
                                    </div>
                                    <div class="col-8">
                                        <p>
                                            <span
                                                :class="selectedItem?.trangThai ? 'badge bg-success' : 'badge bg-danger'">
                                                {{ selectedItem?.trangThai ? "Kích hoạt" : "Chưa áp đụng" }}
                                            </span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div v-if="isFrom" class="fom-form container">
        <div v-if="isAddVisible" class="offcanvas-body">
            <div class=" d-flex justify-content-between">
                <h5 class="offcanvas-title text-primary text-center mx-auto"> {{ isEditing ? 'Chỉnh sửa' : 'Thêm mới' }}
                    Giảm giá
                </h5>
                <button type="button" class=" btn  btn-outline-danger" @click="closeFrom"><i
                        class="bi bi-arrow-return-left"></i></button>
            </div>
            <form @submit.prevent="addGiamGia" id="myForm" class="form">
                <div class="row">
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-1" style="white-space: nowrap;" for="tenGiamGia">Tên giảm
                                    giá:</label>
                                <input v-model="newItem.tenGiamGia" placeholder="Tên giảm giá" type="text"
                                    @input="validateField('tenGiamGia')" @blur="validateField('tenGiamGia')"
                                    id="tenGiamGia" class="form-control custom-input mx-5" style="width: 50%;">
                            </div>
                            <span v-if="errors.tenGiamGia" style="margin-left: 140px;"
                                class="text-danger error-message">{{
                                    errors.tenGiamGia }}</span>
                        </div>
                    </div>
                    <div class="col-md-6 my-3 d-flex ">
                        <div>
                            <p class="me-3 mt-4">Trạng Thái:</p>
                        </div>

                        <div class="d-flex ms-5">
                            <div class="form-check me-3 mt-4">
                                <input v-model="newItem.trangThai" class="form-check-input" type="radio" :value="true"
                                    id="trangThaiKichHoat">
                                <label class="form-check-label" for="trangThaiKichHoat">Kích
                                    hoạt</label>
                            </div>
                            <div class="form-check mt-4">
                                <input v-model="newItem.trangThai" class="form-check-input" type="radio" :value="false"
                                    id="trangThaiKetThuc">
                                <label class="form-check-label" for="trangThaiKetThuc">Chưa áp dụng</label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-2" style="white-space: nowrap;" for="giaTriToiThieu">Hóa đơn tối
                                    thiểu:</label>
                                <input v-model="formattedGiaTriToiThieu" style="width: 50%;" type="text"
                                    @blur="validateField('giaTriToiThieu')"
                                    @input="updateGiaTriToiThieu($event); validateField('giaTriToiThieu')"
                                    id="giaTriToiThieu" class="form-control custom-input me-5 ms-2">
                            </div>
                            <span v-if="errors.giaTriToiThieu" style="margin-left: 140px;"
                                class="text-danger error-message">{{
                                    errors.giaTriToiThieu }}</span>
                        </div>
                    </div>
                    <div class="col-md-6 my-3 d-flex ">
                        <div>
                            <p class="me-3 mt-4">Loại giảm giá:</p>
                        </div>

                        <div class="d-flex ms-4">
                            <div class="form-check me-3 mt-4">
                                <input v-model="newItem.loaiGiamGia" class="form-check-input" type="radio" :value="0"
                                    id="loaiGiamTien">
                                <label class="form-check-label" for="loaiGiamTien">Theo tiền</label>
                            </div>
                            <div class="form-check mt-4">
                                <input v-model="newItem.loaiGiamGia" class="form-check-input" type="radio" :value="1"
                                    id="loaiGiamPhanTram">
                                <label class="form-check-label" for="loaiGiamPhanTram">Phần trăm</label>
                            </div>
                        </div>
                    </div>
                </div>
                <hr class="border-success custom-hr">
                <div class="row">
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-4" style="white-space: nowrap;" for="giaTriGiam">Giá trị
                                    giảm:</label>
                                <input v-model="formattedGiaTriGiam" style="width: 50%;" type="text"
                                    @blur="validateField('giaTriGiam')"
                                    @input="updateGiaTriGiam($event); validateField('giaTriGiam')" id="giaTriGiam"
                                    class="form-control custom-input mx-4">
                            </div>
                            <span v-if="errors.giaTriGiam" style="margin-left: 140px;"
                                class="text-danger error-message">{{
                                    errors.giaTriGiam }}</span>
                        </div>

                    </div>
                    <div v-if="newItem.loaiGiamGia == '1'" class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-2" style="white-space: nowrap;" for="toiDaGiamGia">Số tiền giảm
                                    tối đa:</label>
                                <input v-model="formattedToiDaGiamGia" style="width: 50%;" type="text"
                                    @blur="validateField('toiDaGiamGia')"
                                    @input="updateToiDaGiamGia($event); validateField('toiDaGiamGia')" id="toiDaGiamGia"
                                    class="form-control custom-input me-5 ms-1">
                            </div>

                            <span v-if="errors.toiDaGiamGia" style="margin-left: 140px;"
                                class="text-danger error-message">{{
                                    errors.toiDaGiamGia }}</span>
                        </div>
                    </div>
                </div>
                <div class="row">

                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3">
                            <div class="d-flex ">
                                <label class="mt-3 me-4" style="white-space: nowrap;" for="soLuong">Số lượng:</label>
                                <input v-model="newItem.soLuong" style="width: 50%;" type="number"
                                    @blur="validateField('soLuong')" @input="validateField('soLuong')" id="soLuong"
                                    class="form-control custom-input mx-5">
                            </div>
                            <span v-if="errors.soLuong" style="margin-left: 130px;" class="text-danger error-message">{{
                                errors.soLuong }}</span>
                        </div>
                    </div>
                    <div class="col-md-6 my-3">
                        <div class="form-group mb-3 d-flex">
                            <div>
                                <div class="d-flex ">
                                    <label class="mt-2" style="white-space: nowrap;" for="ngayBatDau">Từ
                                        ngày:</label>

                                    <input v-model="newItem.ngayBatDau" type="date" @blur="validateField('ngayBatDau')"
                                        @input="validateField('ngayBatDau')" id="ngayBatDau" class="datepicker-custom  "
                                        style="margin-left: 30px;">
                                </div>
                                <span v-if="errors.ngayBatDau" style="margin-left: 140px;"
                                    class="text-danger error-message">{{
                                        errors.ngayBatDau }}</span>
                            </div>
                            <div>
                                <div class="form-group mb-3">
                                    <div class="d-flex ">
                                        <label class="mt-2  " style="white-space: nowrap;" for="ngayKetThuc">Đến
                                            :</label>
                                        <input v-model="newItem.ngayKetThuc" type="date"
                                            @blur="validateField('ngayKetThuc')" @input="validateField('ngayKetThuc')"
                                            id="ngayKetThuc" class="datepicker-custom ">
                                    </div>
                                    <span v-if="errors.ngayKetThuc" style="margin-right: 10px;"
                                        class="text-danger error-message">{{
                                            errors.ngayKetThuc }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 my-3 d-flex">
                        <label class="mt-5 me-2" style="white-space: nowrap;">Mô Tả</label>
                        <textarea v-model="newItem.moTa" class="form-control "></textarea>
                    </div>
                </div>
            </form>
            <div class="d-flex justify-content-end gap-2 mt-3">
                <button type="submit" @click="submitForm" class="btn btn-outline-success"> <i
                        class="bi bi-floppy me-2"></i>Lưu</button>
                <button type="button" class="btn btn-outline-secondary " @click="closeFrom">Bỏ
                    qua</button>
            </div>
        </div><!-- End form  -->
    </div>
</template>

<script>
import axios from "axios";
import { notification, Modal } from 'ant-design-vue';
import { computed } from 'vue';
import { mapState } from 'vuex';

export default {

    data() {
        return {
            recognition: null,
            listeningNotification: null,
            isFrom: false,
            isDetailVisible: true,
            statusFilter: '1',
            validityFilter: '1',
            start: null,
            end: null,
            startDate: "",
            endDate: "",
            pageSize: 5,
            errors: {},
            isEditing: false,
            searchKey: '',
            giamGias: [],
            totalPages: 0,
            currentPage: 0,
            isDetailVisible: false,
            isEditVisible: false,
            isAddVisible: false,
            selectedItem: null,
            newItem: {
                tenGiamGia: null,
                maGiamGia: null,
                loaiGiamGia: 0,
                giaTriGiam: null,
                toiDaGiamGia: null,
                giaTriToiThieu: null,
                soLuong: "",
                ngayBatDau: "",
                ngayKetThuc: "",
                moTa: "",
                trangThai: true
            }

        };
    }

    ,
    mounted() {
        this.isDetailVisible = true;
        this.searchGiamGia();
    },
    computed: {
        ...mapState(['roles']),
        isAdmin() {
            return this.roles.includes('ROLE_QUANLY');
        },
        formattedToiDaGiamGia() {
            if (!this.newItem.toiDaGiamGia) return '';
            return this.newItem.toiDaGiamGia.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        },
        formattedGiaTriToiThieu() {
            if (!this.newItem.giaTriToiThieu) return '';
            return this.newItem.giaTriToiThieu.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        },
        formattedGiaTriGiam() {
            if (!this.newItem.giaTriGiam) return '';
            return this.newItem.giaTriGiam.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        }
    },
    methods: {
        updateToiDaGiamGia(event) {
            let value = event.target.value.replace(/,/g, '');
            if (!isNaN(value)) {
                this.newItem.toiDaGiamGia = Number(value);
            }
        },
        updateGiaTriToiThieu(event) {
            let value = event.target.value.replace(/,/g, '');
            if (!isNaN(value)) {
                this.newItem.giaTriToiThieu = Number(value);
            }
        },
        updateGiaTriGiam(event) {
            let value = event.target.value.replace(/,/g, '');
            if (!isNaN(value)) {
                this.newItem.giaTriGiam = Number(value);
            }
        },
        closeFrom() {
            this.isAddVisible = false;
            this.isDetailVisible = true;
            this.isFrom = false;
            this.newItem = {
                tenGiamGia: "",
                giaTriGiam: null,
                loaiGiamGia: 0,
                toiDaGiamGia: null,
                giaTriToiThieu: null,
                soLuong: null,
                ngayBatDau: "",
                ngayKetThuc: "",
                moTa: "",
                trangThai: true
            };
            this.errors = {};
            this.isEditing = false;
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
            this.searchGiamGia();
        },

        onEndChange(date) {
            this.endDate = this.formatDateToYYYYMMDD(date);
            this.searchGiamGia();
        },
        startVoiceSearch() {
            const SpeechRecognition =
                window.SpeechRecognition || window.webkitSpeechRecognition;

            if (!SpeechRecognition) {
                alert("Trình duyệt không hỗ trợ tìm kiếm bằng giọng nói!");
                return;
            }

            this.recognition = new SpeechRecognition();
            this.recognition.lang = "vi-VN";

            this.listeningNotification = notification.info({
                message: "Đang nghe...",
                description: "Hệ thống đang nhận diện giọng nói của bạn.",
                duration: 3,
            });

            this.recognition.start();

            this.recognition.onresult = (event) => {
                this.searchKey = event.results[0][0].transcript;
                this.searchGiamGia(0);

                this.listeningNotification.close();
            };

            this.recognition.onerror = (event) => {
                console.error("Lỗi nhận diện giọng nói: ", event.error);
                this.listeningNotification.close(); x
            };

            this.recognition.onend = () => {
                this.listeningNotification.close();
            };
        },


        searchGiamGia(page = 0) {

            let params = {
                keyq: this.searchKey.trim() || null,
                status: this.statusFilter !== "null" ? this.statusFilter : null,
                validity: this.validityFilter !== "null" ? this.validityFilter : null,
                page: page,
                startDate: this.startDate || null,
                endDate: this.endDate || null,
                pageSize: this.pageSize
            };

            axios.get('/api/giam-gia/loc', { params })
                .then((response) => {
                    this.giamGias = response.data.content.sort((a, b) => {
                        return new Date(b.ngayTao) - new Date(a.ngayTao);
                    });
                    this.totalPages = response.data.page.totalPages;
                    this.currentPage = page;
                })
                .catch((error) => {
                    console.error('Có lỗi xảy ra khi tìm kiếm:', error);
                });
        }
        ,

        openEditForm(gg) {
            this.isAddVisible = true;
            this.isDetailVisible = false;
            this.isFrom = true;
            this.isEditing = true;
            this.selectedItem = { ...gg };

            // Convert date to ISO format (YYYY-MM-DDTHH:mm)
            this.newItem = {
                ...gg
            };
        }, convertToLocalDateTime(dateTime) { // <== THÊM HÀM NÀY VÀO METHODS
            if (!dateTime) return null;
            const date = new Date(dateTime);
            return date.toISOString().slice(0, 16);
        },
        showDetails(gg) {
            this.selectedItem = { ...gg };
            this.isDetailVisible = true;
            this.isEditVisible = false;
        },
        validateField(field) {
            this.errors[field] = '';

            if (field === 'tenGiamGia' && !this.newItem.tenGiamGia.trim()) {
                this.errors.tenGiamGia = "Tên giảm giá không được để trống.";
            }

            if (field === 'soLuong') {
                if (!this.newItem.soLuong) {
                    this.errors.soLuong = "Vui lòng nhập số lượng.";
                } else if (this.newItem.soLuong <= 0) {
                    this.errors.soLuong = "Số lượng phải lớn hơn 0.";
                }

            }

            if (field === 'giaTriGiam') {
                if (!this.newItem.giaTriGiam) {
                    this.errors.giaTriGiam = "Vui lòng nhập giá trị giảm.";
                } else if (this.newItem.loaiGiamGia === 1) {
                    if (this.newItem.giaTriGiam <= 0 || this.newItem.giaTriGiam > 100) {
                        this.errors.giaTriGiam = "Giá trị giảm phải từ 1% đến 100%.";
                    }
                } else if (this.newItem.loaiGiamGia === 0) {
                    if (this.newItem.giaTriGiam <= 0) {
                        this.errors.giaTriGiam = "Giá trị giảm phải lớn hơn 0 VNĐ.";
                    }
                    if (this.newItem.giaTriGiam > this.newItem.giaTriToiThieu) {
                        this.errors.giaTriGiam = "Số tiền giảm không được lớn hơn giá trị đơn hàng tối thiểu.";
                    }

                }
            }

            if (field === 'toiDaGiamGia' && this.newItem.loaiGiamGia == 1) {
                if (!this.newItem.toiDaGiamGia) {
                    this.errors.toiDaGiamGia = "Vui lòng nhập số tiền giảm tối đa.";
                } else if (this.newItem.toiDaGiamGia < 0) {
                    this.errors.toiDaGiamGia = "Số tiền giảm tối đa không được âm.";
                } else if (this.newItem.toiDaGiamGia > this.newItem.giaTriToiThieu) {
                    this.errors.toiDaGiamGia = "Số tiền giảm tối đa không được lớn hơn giá trị đơn hàng.";
                }
            }

            if (field === 'giaTriToiThieu' && !this.newItem.giaTriToiThieu) {
                this.errors.giaTriToiThieu = "Vui lòng nhập giá trị đơn hàng tối thiểu.";
            } else if (this.newItem.giaTriToiThieu < 0) {
                this.errors.giaTriToiThieu = "Giá trị đơn hàng tối thiểu không được âm.";
            }
            if (field === 'ngayBatDau') {
                const ngayHienTai = new Date();
                ngayHienTai.setHours(0, 0, 0, 0);

                const ngayBatDau = new Date(this.newItem.ngayBatDau);
                ngayBatDau.setHours(0, 0, 0, 0);

                if (!this.newItem.ngayBatDau) {
                    this.errors.ngayBatDau = "Chọn ngày bắt đầu.";
                }
                //  else if (ngayBatDau < ngayHienTai) {
                //     this.errors.ngayBatDau = "Không thể bắt đầu trong quá khứ!";
                // }
            }
            if (field === 'ngayKetThuc') {
                if (!this.newItem.ngayKetThuc) {
                    this.errors.ngayKetThuc = "Chọn ngày kết thúc.";
                } else if (new Date(this.newItem.ngayKetThuc) < new Date(this.newItem.ngayBatDau)) {
                    this.errors.ngayKetThuc = "Thời gian kết thúc không thể trước ngày bắt đầu!";
                }
            }

        },
        submitForm() {
            const fieldsToValidate = [
                'tenGiamGia',
                'soLuong',
                'giaTriGiam',
                'toiDaGiamGia',
                'giaTriToiThieu',
                'ngayBatDau',
                'ngayKetThuc'
            ];

            fieldsToValidate.forEach(field => {
                this.validateField(field);
            });

            if (Object.keys(this.errors).some(field => this.errors[field] !== '')) {
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
        }
        ,
        formatCurrency(value) {
            return new Intl.NumberFormat({ style: 'currency', currency: 'VND' }).format(value);
        },
        async addGiamGia() {

            if (this.newItem.loaiGiamGia === 0) {
                this.newItem.toiDaGiamGia = 0;
            }

            const giamGiaData = {
                tenGiamGia: this.newItem.tenGiamGia,
                loaiGiamGia: this.newItem.loaiGiamGia,
                giaTriGiam: this.newItem.giaTriGiam,
                toiDaGiamGia: this.newItem.toiDaGiamGia,
                giaTriToiThieu: this.newItem.giaTriToiThieu,
                soLuong: this.newItem.soLuong,
                ngayBatDau: this.newItem.ngayBatDau,
                ngayKetThuc: this.newItem.ngayKetThuc,
                trangThai: this.newItem.trangThai,
                moTa: this.newItem.moTa
            };


            if (this.isEditing) {
                this.updateGiamGia(giamGiaData);
            } else {
                try {
                    const response = await axios.post("/api/giam-gia/them", giamGiaData)
                    if (response.status === 200) {
                        notification.success({ message: 'Thêm mới giảm giá thành công!' });
                        this.resetForm();
                        this.searchGiamGia();
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
                        console.error("Lỗi khi thêm mới giảm giá:", error);
                    }
                }
            }
        },
        resetForm() {
            this.newItem = {
                tenGiamGia: "",
                giaTriGiam: null,
                loaiGiamGia: 0,
                toiDaGiamGia: null,
                giaTriToiThieu: null,
                soLuong: null,
                ngayBatDau: "",
                ngayKetThuc: "",
                moTa: "",
                trangThai: true
            };
            this.errors = {};
            this.isEditing = false;
        },
        async updateGiamGia() {

            if (this.newItem.loaiGiamGia === 0) {
                this.newItem.toiDaGiamGia = 0;
            }
            const giamGiaData = {
                id: this.newItem.id,
                tenGiamGia: this.newItem.tenGiamGia,
                loaiGiamGia: this.newItem.loaiGiamGia,
                giaTriGiam: this.newItem.giaTriGiam,
                toiDaGiamGia: this.newItem.toiDaGiamGia,
                giaTriToiThieu: this.newItem.giaTriToiThieu,
                ngayBatDau: this.newItem.ngayBatDau,
                ngayKetThuc: this.newItem.ngayKetThuc,
                trangThai: this.newItem.trangThai,
                soLuong: this.newItem.soLuong,
                moTa: this.newItem.moTa,
            };


            try {
                const response = await axios.put("/api/giam-gia/cap-nhat", giamGiaData)
                if (response.status === 200) {
                    notification.success({ message: 'Cập nhật giảm giá thành công!' });
                    this.resetForm();
                    this.searchGiamGia();
                    this.isAddVisible = false;
                    this.isDetailVisible = true;
                    this.isFrom = false;
                }
            } catch (error) {
                this.openEditForm(this.newItem);
                if (error.response?.status === 400 && error.response.data.errors) {
                    this.errors = { ...error.response.data.errors };
                    notification.error({
                        message: 'Lỗi xác thực',
                        description: 'Vui lòng kiểm tra lại các trường dữ liệu.'
                    });
                } else {
                    console.error("Lỗi khi cập nhật giảm giá:", error);
                }
            }
        },

        openAddForm() {

            this.isDetailVisible = false;
            this.isFrom = true;
            this.isEditing = false;
            this.isAddVisible = true;
            this.resetForm();
        },


        fetchGiamGias(page = 0) {
            axios
                .get(`/api/giam-gia/hien-thi?page=${page}&pageSize=${this.pageSize}`)
                .then((response) => {
                    this.giamGias = response.data.content;
                    this.totalPages = response.data.totalPages;
                    this.currentPage = page;
                })
                .catch((error) => console.error(error));
        }
        ,
        formatDate(date) {
            if (!date) return '';
            return new Date(date).toLocaleString('vi-VN', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
            });
        },


        navigateToAdd() {
            this.$router.push("/them");
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

.error-message {
    position: absolute;
    font-size: 0.875rem;
    color: #e74c3c;
    white-space: nowrap;
    max-width: 90%;
    overflow: hidden;
    visibility: visible;
}

#addKhuyenMaiOffcanvas {
    width: 50vw !important;
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
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(255, 255, 255, 0.95);
    border: 1px solid #ddd;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    width: 40%;
    max-height: 90vh;
    overflow-x: auto;
}


.fom-group-r {
    background: rgba(255, 255, 255, 0.98);
    margin-right: 20px;
}

.form-group1 {
    margin-top: 115px;
    vertical-align: top;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;

}

.fom-home {
    max-width: 100vw;
    overflow-x: hidden;
}

.form-group2 {
    margin-top: 50px;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;
    min-width: 215px;
    min-height: 500px;
}

.form-group3 {

    vertical-align: top;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;
}

.offcanvas-body {
    overflow-x: hidden;
    white-space: normal;
}

.custom-input {
    background-color: #f9f9f9;
    border: none;
    border-bottom: 2px solid #007bff;
    outline: none;
    border-radius: 0;
    width: 100%;
    padding: 0.375rem 0.75rem;
}

.custom-input:focus {
    border: none;
    border-bottom: 2px solid #df551a;
    box-shadow: none;
}

.form-check {
    margin-bottom: 10px;
}

.close-btn {
    position: absolute;
    top: 10px;
    right: 10px;
}

.fom-form {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
    background-color: #f9f9f9;
    max-width: 80vw;
    overflow-y: hidden;
}

.custom-offcanvas .offcanvas-body {
    font-size: 16px;

}

.offcanvas.offcanvas-body {
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    min-height: 90vh;
    width: 50vw !important;
    background-color: #fff;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
    border-radius: 8px;

}

.custom-offcanvas hr {
    border-top: 1px solid #ddd;
    margin: 10px 0;
}

.custom-offcanvas .btn {
    min-width: 100px;
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

/* Thêm độ dày cho đường kẻ hr */
.custom-hr {
    border-style: dashed;
    /* Tạo kiểu nét đứt */
    border-width: 1px;
    /* Tăng độ dày viền */
}
</style>