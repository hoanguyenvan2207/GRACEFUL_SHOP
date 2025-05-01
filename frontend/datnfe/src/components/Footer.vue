<template>
    <footer class="border-top mt-5 border-secondary">
        <!-- Phần header footer: Liên kết mạng xã hội và đăng ký nhận tin -->
        <div class="bg-dark text-white py-2">
            <div class="container d-flex justify-content-between align-items-center">
                <!-- Mạng xã hội -->
                <div class="d-flex align-items-center mb-2 mb-md-0">
                    <span>{{ $t('footer.social.connect') }}</span>
                    <a class="text-white ms-3" href="#"><i class="fab fa-facebook-f"></i></a>
                    <a class="text-white ms-3" href="#"><i class="fab fa-twitter"></i></a>
                    <a class="text-white ms-3" href="#"><i class="fab fa-instagram"></i></a>
                    <a class="text-white ms-3" href="#"><i class="fab fa-pinterest"></i></a>
                </div>
                <!-- Đăng ký nhận tin -->
                <div class="d-flex align-items-center mb-2 mb-md-0" style="width: 500px;">
                    <span>{{ $t('footer.subscribe.newsletter') }}</span>
                    <input type="email" class="form-control ms-2" :placeholder="$t('footer.subscribe.emailPlaceholder')"
                        style="max-width: 250px;">
                    <button class="btn btn-dark border ms-2">{{ $t('footer.subscribe.send') }}</button>
                </div>
                <!-- Thông điệp -->
                <div class="mb-2 mb-md-0">
                    <span>{{ $t('footer.social.follow') }}</span>
                </div>
            </div>
        </div>

        <!-- Phần thông tin và liên hệ -->
        <div class="container py-5">
            <div class="row">
                <!-- Cột thông tin công ty -->
                <div class="col-md-3 mb-4 mb-md-0">
                    <img src="https://res.cloudinary.com/defcm50t7/image/upload/v1740129073/logo_graceful_lpd32u.png"
                        alt="Graceful logo" class="mb-4 p-0" width=200px height="auto" style="margin-left: -20px;">
                    <p class="fw-bold">{{ $t('footer.company.name') }}</p>
                    <p>{{ $t('footer.company.taxCode') }}</p>
                    <p>{{ $t('footer.company.phone') }}</p>
                    <p>Email: support@graceful.com.vn</p>
                    <p>{{ $t('footer.company.website') }}</p>
                    <div class="bg-dark text-white text-center py-2 mt-4">{{ $t('footer.company.hotline') }}</div>
                </div>
                <!-- Cột GIỚI THIỆU -->
                <div class="col-md-3 mb-4 mb-md-0">
                    <h5 class="fw-bold mb-3">{{ $t('footer.about.title') }}</h5>
                    <ul class="list-unstyled">
                        <li>{{ $t('footer.about.aoDai') }}</li>
                        <li>{{ $t('footer.about.vest') }}</li>
                        <li>{{ $t('footer.about.dress') }}</li>
                        <li>{{ $t('footer.about.aboutUs') }}</li>
                        <li>{{ $t('footer.about.account') }}</li>
                        <li>{{ $t('footer.about.stores') }}</li>
                        <li>{{ $t('footer.about.contact') }}</li>
                    </ul>
                </div>
                <!-- Cột CHÍNH SÁCH -->
                <div class="col-md-3 mb-4 mb-md-0">
                    <h5 class="fw-bold mb-3">{{ $t('footer.policy.title') }}</h5>
                    <ul class="list-unstyled">
                        <li>{{ $t('footer.policy.payment') }}</li>
                        <li>{{ $t('footer.policy.shipping') }}</li>
                        <li>{{ $t('footer.policy.privacy') }}</li>
                        <li>{{ $t('footer.policy.returns') }}</li>
                    </ul>
                </div>
                <!-- Cột PHƯƠNG THỨC THANH TOÁN -->
                <div class="col-md-3">
                    <h5 class="fw-bold mb-3">{{ $t('footer.paymentMethods.title') }}</h5>
                    <div class="d-flex flex-wrap align-items-center">
                        <img src="https://cdn.haitrieu.com/wp-content/uploads/2022/10/Logo-VNPAY-QR-1.png"
                            alt="MasterCard" class="me-2 mb-2" height="30">
                    </div>
                    <h5 class="fw-bold mb-3 mt-4">{{ $t('footer.shipping.title') }}</h5>
                    <div class="d-flex flex-wrap align-items-center">
                        <img src="https://cdn.haitrieu.com/wp-content/uploads/2022/05/Logo-GHTK-H.png" alt="MoMo"
                            class="me-2 mb-2" height="30">
                    </div>
                </div>
            </div>
        </div>

        <!-- Phần copyright -->
        <div class="bg-light text-center py-3">
            <p class="mb-0">{{ $t('footer.copyright') }}</p>
        </div>
    </footer>
</template>

<script setup>
import { useI18n } from 'vue-i18n'

const { t, locale } = useI18n()

</script>