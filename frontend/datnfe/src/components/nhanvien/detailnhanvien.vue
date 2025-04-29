<template>
    <div v-show="show" class="modal-overlay" @click.self="$emit('close')">
        <div class="modal-container">
            <div class="modal-header">
                <h2 class="text-center">Thông tin nhân viên</h2>
                <button @click="$emit('close')" class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                <form class="small-form">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mã Nhân Viên</label>
                            <input type="text" v-model="nhanVien2.maNhanVien" class="form-control" disabled>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Giới Tính</label>
                            <div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="gioiTinh" value="1"
                                        v-model="nhanVien2.gioiTinh" disabled />
                                    <label class="form-check-label" for="male">Nam</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="gioiTinh" value="0"
                                        v-model="nhanVien2.gioiTinh" disabled />
                                    <label class="form-check-label" for="female">Nữ</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Họ Tên</label>
                            <input type="text" v-model="nhanVien2.hoVaTen" class="form-control" disabled>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Tên Đăng Nhập</label>
                            <input type="text" v-model="nhanVien2.tenDangNhap" class="form-control" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Ngày Sinh</label>
                            <input type="date" v-model="nhanVien2.ngaySinh" class="form-control" disabled>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" v-model="nhanVien2.email" class="form-control" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Số Điện Thoại</label>
                            <input type="text" v-model="nhanVien2.soDienThoai" class="form-control" disabled>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Địa Chỉ</label>
                            <input type="text" v-model="nhanVien2.diaChi" class="form-control" disabled>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Chức Vụ</label>
                            <input type="text" v-model="nhanVien2.vaiTro.tenVaiTro" class="form-control" disabled>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Trạng Thái</label>
                            <input type="text" :value="nhanVien2.trangThai ? 'Đang làm' : 'Đã nghỉ làm'"
                                class="form-control" disabled>
                        </div>
                    </div>
                    <div class="text-center mt-3">
                        <button type="button" @click="$emit('close')" class="btn btn-secondary">Quay lại</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
import axios from 'axios';


export default {
    props: {
        show: {
            type: Boolean,
            required: true
        },
        employeeId: {
            type: [String, Number],
            required: true
        }
    },
    data() {
        return {
            nhanVien2: {
                id: '',
                maNhanVien: '',
                hoVaTen: '',
                tenDangNhap: '',
                matKhau: '',
                gioiTinh: null,
                ngaySinh: '',
                email: '',
                soDienThoai: '',
                diaChi: '',
                vaiTro: { id: null },
                trangThai: null,
            },
            vaiTros: [],
        };
    },
    watch: {
        show: {
            immediate: true,
            handler(newVal) {
                if (newVal && this.employeeId) {
                    this.fetchEmployeeDetails();
                }
            }
        }
    },
    methods: {
        async fetchEmployeeDetails() {

            this.loading = true;
            this.error = null;
            try {
                const response = await axios.get(`/api/nhan-vien/details/${this.employeeId}`);
                this.nhanVien2 = response.data;
            } catch (error) {
                console.error('Error fetching employee details:', error);
                this.error = 'Không thể tải thông tin nhân viên. Vui lòng thử lại sau.';
            } finally {
                this.loading = false;
            }

        },
        fetchVaiTros() {
            axios.get('/api/nhan-vien/show-vai-tro')
                .then(response => this.vaiTros = response.data)
                .catch(error => console.error(error));
        }
    }
};
</script>
<style scoped>
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
}

.modal-container {
    background-color: white;
    border-radius: 8px;
    width: 90%;
    max-width: 800px;
    max-height: 90vh;
    overflow-y: auto;
    padding: 20px;
}

.modal-header {
    display: flex;
    justify-content: center;
    align-items: center;
    position: relative;
    margin-bottom: 20px;
    border-bottom: 2px solid #ccc;
}


.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    position: absolute;
    right: 20px;
    top: 50%;
    transform: translateY(-50%);
}


.modal-body {
    padding: 20px 0;
}
</style>
