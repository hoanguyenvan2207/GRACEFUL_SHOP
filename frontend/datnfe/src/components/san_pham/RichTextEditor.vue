<template>
    <div ref="editor"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import { uploadImages } from './api';

const props = defineProps({
    value: String
});

const emit = defineEmits(['update:value', 'blur', 'change']);
const editor = ref(null);
let quill = null;
const LOADING_IMG_URL = 'https://i.gifer.com/ZKZg.gif';

const toolbarOptions = {
    container: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        ['link', 'image', 'video'],
        ['clean']
    ],
    handlers: {
        image: imageHandler
    }
};

onMounted(() => {
    quill = new Quill(editor.value, {
        theme: 'snow',
        placeholder: 'Nhập mô tả...',
        modules: {
            toolbar: toolbarOptions,
            clipboard: {
                matchVisual: false // This helps with some paste issues
            }
        }
    });

    if (props.value) {
        quill.root.innerHTML = props.value;
        // Sau khi nạp nội dung ban đầu, kiểm tra và tải lên các ảnh base64 đã có
        processExistingImages();
    }

    quill.on('text-change', () => {
        // Đảm bảo không hiển thị placeholder khi có ảnh
        let html = quill.root.innerHTML;
        const hasImage = html.includes('<img');
        const strippedHtml = html.replace(/<[^>]+>/g, '').trim();

        if (strippedHtml === '' && !hasImage) {
            html = '';
        }

        emit('update:value', html);
        emit('change', html);
    });

    quill.on('blur', () => {
        emit('blur');
    });

    // Cải thiện xử lý paste
    const clipboard = quill.getModule('clipboard');

    // Xử lý an toàn hơn cho paste
    try {
        // Xử lý ảnh
        clipboard.addMatcher('img', (node, delta) => {
            const src = node.getAttribute('src');
            if (src && (src.startsWith('data:') || src.startsWith('blob:'))) {
                // Ghi nhận và xử lý ảnh sau
                setTimeout(() => processImageAfterPaste(src), 10);
            }
            return delta; // Đảm bảo luôn trả về delta
        });
    } catch (err) {
        console.error('Error setting up clipboard matcher:', err);
    }

    // Tách sự kiện paste khỏi matcher để xử lý riêng
    quill.root.addEventListener('paste', handlePasteEvent);

    // Theo dõi thay đổi từ props
    watch(() => props.value, (newValue) => {
        if (quill && newValue !== quill.root.innerHTML) {
            quill.root.innerHTML = newValue;
            processExistingImages();
        }
    });
});

// Improved paste event handler
function handlePasteEvent(e) {
    try {
        // Let Quill handle the paste operation first
        setTimeout(() => {
            // Then process any pasted images
            processPastedImages();
        }, 100);
    } catch (err) {
        console.error('Error in paste handler:', err);
    }
}

// Xử lý tất cả ảnh base64 sau khi paste
function processPastedImages() {
    try {
        const images = quill.root.querySelectorAll('img');
        images.forEach(img => {
            const src = img.getAttribute('src');
            if (src && (src.startsWith('data:') || src.startsWith('blob:'))) {
                processImageAfterPaste(src, img);
            }
        });
    } catch (err) {
        console.error('Error processing pasted images:', err);
    }
}

// Xử lý ảnh sau khi paste
function processImageAfterPaste(src, imgElement = null) {
    try {
        // Nếu không có element cụ thể, tìm trong tất cả ảnh
        if (!imgElement) {
            const images = quill.root.querySelectorAll('img');
            for (let img of images) {
                if (img.src === src) {
                    imgElement = img;
                    break;
                }
            }
            if (!imgElement) return; // Không tìm thấy ảnh
        }

        // Lưu trữ vị trí và thông tin ảnh
        const originalSrc = imgElement.src;
        const originalWidth = imgElement.style.width;
        const originalHeight = imgElement.style.height;

        // Thay thế bằng ảnh loading
        imgElement.src = LOADING_IMG_URL;
        imgElement.classList.add('loading-image');
        imgElement.dataset.originalSrc = originalSrc;

        // Upload ảnh lên cloud
        uploadImageToCloud(originalSrc)
            .then(url => {
                // Tạo ảnh ẩn để load trước
                const tempImg = new Image();
                tempImg.onload = () => {
                    if (imgElement.parentNode) { // Kiểm tra xem ảnh còn tồn tại không
                        imgElement.src = url;
                        imgElement.classList.remove('loading-image');
                        imgElement.style.width = originalWidth || '100%';
                        imgElement.style.height = originalHeight || 'auto';
                        imgElement.removeAttribute('data-original-src');
                    }
                };
                tempImg.src = url;
            })
            .catch(err => {
                console.error('Lỗi upload ảnh:', err);
                // Giữ lại ảnh loading thay vì xóa, để đảm bảo có nội dung
                if (imgElement.parentNode) {
                    imgElement.src = LOADING_IMG_URL;
                    imgElement.classList.add('error-image');
                }
            });
    } catch (err) {
        console.error('Error processing image:', err);
    }
}

// Xử lý các hình ảnh hiện có trong nội dung
function processExistingImages() {
    setTimeout(() => {
        try {
            const images = quill.root.querySelectorAll('img');
            images.forEach(img => {
                const src = img.getAttribute('src');
                if (src && (src.startsWith('data:') || src.startsWith('blob:'))) {
                    const originalSrc = img.src;
                    const originalStyle = img.getAttribute('style');

                    // Đổi thành ảnh loading
                    img.src = LOADING_IMG_URL;
                    img.classList.add('loading-image');

                    // Upload ảnh
                    uploadImageToCloud(originalSrc)
                        .then(url => {
                            // Tạo ảnh ẩn để load trước
                            const tempImg = new Image();
                            tempImg.onload = () => {
                                if (img.parentNode) { // Kiểm tra xem ảnh còn tồn tại không
                                    img.src = url;
                                    img.classList.remove('loading-image');
                                    if (originalStyle) {
                                        img.setAttribute('style', originalStyle);
                                    } else {
                                        img.style.width = '100%';
                                        img.style.height = 'auto';
                                    }
                                }
                            };
                            tempImg.src = url;
                        })
                        .catch(err => {
                            console.error('Lỗi upload ảnh:', err);
                            // Giữ lại ảnh loading thay vì xóa, để đảm bảo có nội dung
                            if (img.parentNode) {
                                img.classList.add('error-image');
                            }
                        });
                }
            });
        } catch (err) {
            console.error('Error processing existing images:', err);
        }
    }, 0);
}

// Xử lý khi người dùng chọn hình ảnh từ công cụ
function imageHandler() {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
        try {
            const file = input.files[0];
            if (!file) return;

            // Lưu vị trí hiện tại
            const range = quill.getSelection(true);
            const index = range ? range.index : quill.getLength();

            // Hiển thị loading
            quill.insertEmbed(index, 'image', LOADING_IMG_URL, 'user');

            // Thêm class loading
            setTimeout(() => {
                const imgs = quill.root.querySelectorAll('img[src="' + LOADING_IMG_URL + '"]');
                if (imgs.length > 0) {
                    const lastImg = imgs[imgs.length - 1];
                    lastImg.classList.add('loading-image');
                }
            }, 0);

            // Upload trực tiếp lên cloud, không lưu base64
            const url = await uploadImages(file);

            // Cập nhật ảnh loading
            const imgs = quill.root.querySelectorAll('img.loading-image');
            if (imgs.length > 0) {
                const lastImg = imgs[imgs.length - 1];

                // Tạo ảnh ẩn để load trước
                const tempImg = new Image();
                tempImg.onload = () => {
                    lastImg.src = url;
                    lastImg.classList.remove('loading-image');
                    lastImg.style.width = '100%';
                    lastImg.style.height = 'auto';
                };
                tempImg.src = url;
            }

            quill.setSelection(index + 1);
        } catch (err) {
            console.error('Upload toolbar image thất bại', err);

            // Không xóa ảnh, chỉ đánh dấu lỗi để người dùng thử lại
            const imgs = quill.root.querySelectorAll('img.loading-image');
            if (imgs.length > 0) {
                const lastImg = imgs[imgs.length - 1];
                lastImg.classList.add('error-image');
            }
        }
    };
}

// Upload hình ảnh lên cloud
async function uploadImageToCloud(src) {
    try {
        // Nếu đã là URL web (không phải base64/blob), trả về luôn
        if (/^https?:\/\//.test(src) && !src.includes(location.origin) && !src.includes(LOADING_IMG_URL)) {
            return src;
        }

        // Chuyển đổi data URL hoặc blob URL thành Blob
        const fileData = await fetch(src);
        const blob = await fileData.blob();

        // Kiểm tra kích thước và loại tập tin
        if (blob.size > 0) {
            // Gọi API upload
            return await uploadImages(blob);
        } else {
            throw new Error('Không thể lấy dữ liệu hình ảnh');
        }
    } catch (error) {
        console.error('Lỗi xử lý hình ảnh:', error);
        throw error;
    }
}
</script>

<style>
.ql-editor {
    min-height: 200px;
}

.loading-image {
    width: 30px !important;
    height: 30px !important;
    display: block;
    margin: 10px auto;
    opacity: 0.7;
}

.error-image {
    border: 2px solid #ff4d4f;
}

.ql-editor img {
    max-width: 50% !important;
    height: auto !important;
    display: block;
    margin: 10px auto;
    transition: all 0.4s ease;
}

/* Đảm bảo trình soạn thảo không trống khi chỉ có hình ảnh */
.ql-editor:has(img):not(:has(p:not(:empty))) {
    min-height: 200px;
}
</style>