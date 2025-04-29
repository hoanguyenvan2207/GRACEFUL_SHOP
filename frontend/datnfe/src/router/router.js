// datnfe/src/router/router.js
import { createRouter, createWebHistory } from 'vue-router';
import store from './store';
import { message, notification } from 'ant-design-vue';
import axios from 'axios';

const routes = [

  {
    path: '/hoa-don/hien-thi',
    component: () => import('../components/hoadon/HoaDonList.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/ban-hang',
    component: () => import('../components/banhang/BanHang.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }

  },
  {
    path: '/quan-ly-don-hang',
    component: () => import('../components/quanlydonhang/DonHangOnline.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },

  // Sản phẩm
  {
    path: '/san-pham/list/all',
    component: () => import('../components/san_pham/SanPhamList.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/san-pham/add',
    component: () => import('../components/san_pham/SanPhamAdds.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/san-pham/edit/:id',
    component: () => import('../components/san_pham/SanPhamEdit.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/chat-lieu/list/all',
    component: () => import('../components/chat_lieu/ChatLieuList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/loai-ao-dai/list/all',
    component: () => import('../components/loai_ao_dai/LoaiAoDaiList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/nha-cung-cap/list/all',
    component: () => import('../components/nha_cung_cap/NhaCungCapList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/ta-ao/list/all',
    component: () => import('../components/ta_ao/TaAoList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/kich-thuoc/list/all',
    component: () => import('../components/kich_thuoc/KichThuocList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/mau-sac/list/all',
    component: () => import('../components/mau_sac/MauSacList.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/san-pham-chi-tiet/list/all',
    component: () => import('../components/san_pham_chi_tiet/SanPhamChiTietList.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/san-pham-chi-tiet/add',
    component: () => import('../components/san_pham_chi_tiet/SanPhamChiTietAdd.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/san-pham-chi-tiet/edit/:id',
    component: () => import('../components/san_pham_chi_tiet/SanPhamChiTietEdit.vue'),
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/chat-list',
    component: () => import('../components/chat/StaffChatList.vue'),
    meta: { title: 'Chat', isAdmin: true }
  },
  {
    path: '/danh-sach-san-pham',
    component: () => import('../components/san_pham_gio_hang/ProductList.vue'),
    meta: { layout: 'customer' }
  },
  {
    path: '/danh-sach-don-hang',
    component: () => import('../components/san_pham_gio_hang/DanhSachDonHang.vue'),
    meta: { requiresAuth: true, layout: 'customer' }
  },
  {
    path: '/chat',
    component: () => import('../components/chat/ChatFloatButton.vue'),
  },
  {
    path: '/thanh-toan',
    component: () => import('../components/san_pham_gio_hang/ThanhToan.vue'),
    meta: { requiresAuth: true, layout: 'payment' },
  },
  {
    path: '/san-pham/:id',
    component: () => import('../components/san_pham_gio_hang/ProductDetail.vue'),
    meta: { layout: 'customer' }
  },

  // tuand
  { path: "/khach-hang", redirect: "/khach-hang/list" },
  {
    path: '',
    component: () => import('../components/Home.vue'),
    meta: { layout: 'customer' }
  },
  {
    path: "/khach-hang/list",
    component: () => import("../components/khachhang/ListKhachHang.vue"),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/khach-hang/add",
    component: () => import("../components/khachhang/AddKhachHang.vue"),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/khach-hang/update/:id",
    component: () => import("../components/khachhang/EditKhachHang.vue"),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/thong-ke",
    component: () => import("../components/thongke/ThongKe.vue"),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/dang-ki",
    name: "Register",
    component: () => import("../components/auth/DangKi.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/dang-nhap",
    name: "KhachHangLogin",
    component: () => import("../components/auth/DangNhap.vue"),
    meta: { requiresGuest: true }
  },
  {
    path: "/thong-tin",
    component: () => import("../components/auth/ThongTin.vue"),
    meta: { requiresAuth: true, layout: 'customer' }
  },

  //hinhnn
  {
    path: '/nhan-vien/list',
    component: () => import('../components/nhanvien/listnhanvien.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/nhan-vien/add',
    component: () => import('../components/nhanvien/addnhanvien.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/nhan-vien/update/:id',
    component: () => import('../components/nhanvien/updatenhanvien.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/nhan-vien/detail/:id',
    component: () => import('../components/nhanvien/detailnhanvien.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY'], layout: 'admin' }
  },
  {
    path: '/staff',
    component: () => import('../components/user/userdetail.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/admin',
    redirect: '/admin/login',
    meta: { requiresAuth: false }
  },
  {
    path: '/admin/login',
    name: 'login',
    component: () => import('../components/login/login.vue'),
    props: true,
    meta: { requiresAuth: false },
  },
  {
    path: '/reset-password',
    component: () => import('../components/user/resetpassword.vue'),
    props: true,
    meta: { requiresAuth: false }
  },
  {
    path: '/access-denied',
    component: () => import('../components/login/accessDenied.vue'),
    props: true,
    meta: { requiresAuth: false }
  },
  //DONG
  {
    path: '/quan-ly-giam-gia',
    redirect: '/giam-gia',
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/giam-gia",
    component: () => import('../components/khuyenMai/QuanLyKhuyenMai.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: "/khuyen-mai",
    component: () => import('../components/khuyenMai/QuanLyKhuyenMai.vue'),
    props: true,
    meta: { requiresAuth: true, allowedRoles: ['ROLE_QUANLY', 'ROLE_NHANVIEN'], layout: 'admin' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../components/common/PageNotFound.vue'),
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Hàm chuẩn hóa quyền
const normalizeRoles = (roles) => {
  return roles.map(role =>
    role.toUpperCase().startsWith('ROLE_') ? role.toUpperCase() : `ROLE_${role.toUpperCase()}`
  );
};

router.beforeEach(async (to, from, next) => {
  const layout = to.meta.layout;

  if ((layout === 'customer' || layout === 'payment') && to.meta.requiresAuth) {
    const hasSession = !!sessionStorage.getItem('customerInfo');
    if (!hasSession && to.meta.requiresAuth) {
      return next({ path: '/dang-nhap' });
    } else if (!hasSession && !to.meta.requiresAuth) {
      return window.location.reload();
    }
    try {
      const res = await axios.get('/api/auth/check', { withCredentials: true });
      if (!res.data.authenticated) throw new Error();
      return next();
    } catch {
      message.warning('Phiên đã hết hạn, vui lòng đăng nhập lại.');
      return next({ path: '/dang-nhap' });
    }
  }

  const savedRoles = JSON.parse(sessionStorage.getItem('roles')) || [];
  const normalizedRoles = normalizeRoles(savedRoles);

  if (to.meta.allowedRoles) {
    const hasPermission = to.meta.allowedRoles.some(role =>
      normalizedRoles.includes(role.toUpperCase())
    );

    if (!hasPermission) {
      message.error('Bạn không có quyền truy cập!');
      return next('/access-denied');
    }
  }

  next();
});

export default router;