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

const Delta = Quill.import('delta');

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
            toolbar: toolbarOptions
        }
    });

    if (props.value) {
        quill.root.innerHTML = props.value;
    }

    quill.on('text-change', () => {
        let html = quill.root.innerHTML;
        const strippedHtml = html.replace(/<[^>]+>/g, '').trim();
        if (strippedHtml === '') {
            html = '';
        }
        emit('update:value', html);
        emit('change', html);
    });

    quill.on('blur', () => {
        emit('blur');
    });

    const clipboard = quill.getModule('clipboard');
    clipboard.addMatcher('img', (node, delta) => {
        const src = node.src;
        if (src.startsWith('data:') || src.startsWith('blob:')) {
            const loadingSrc = 'https://i.gifer.com/ZKZg.gif';
            const loadingDelta = new Delta().insert({ image: loadingSrc });

            const selection = quill.getSelection(true);
            const index = selection ? selection.index : quill.getLength();
            quill.updateContents(new Delta().retain(index).concat(loadingDelta), 'user');

            // Thêm class loading ngay lập tức
            setTimeout(() => {
                const imgs = quill.root.querySelectorAll('img');
                imgs.forEach(img => {
                    if (img.src === loadingSrc) {
                        img.classList.add('loading-image');
                        img.dataset.originalSrc = node.src; // Lưu URL gốc
                    }
                });
            });

            uploadImageToCloudinary(node.src)
                .then(url => {
                    const imgs = quill.root.querySelectorAll('img');
                    imgs.forEach(img => {
                        if (img.src === loadingSrc) {
                            // Tạo ảnh ẩn để load trước
                            const tempImg = new Image();
                            tempImg.onload = () => {
                                // Khi ảnh thật đã load xong
                                img.src = url;
                                img.classList.remove('loading-image');
                                img.style.width = '100%';
                                img.style.height = 'auto';
                                img.removeAttribute('data-original-src');
                            };
                            tempImg.src = url;
                        }
                    });
                })
                .catch(err => {
                    console.error('Lỗi upload ảnh:', err);
                    // Khôi phục ảnh gốc nếu upload thất bại
                    const imgs = quill.root.querySelectorAll('img');
                    imgs.forEach(img => {
                        if (img.src === loadingSrc && img.dataset.originalSrc) {
                            img.src = img.dataset.originalSrc;
                            img.classList.remove('loading-image');
                        }
                    });
                });
        } else {
            return new Delta().insert({ image: src });
        }

        return new Delta();
    });

});

function imageHandler() {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();
    input.onchange = async () => {
        const file = input.files[0];
        if (!file) return;
        // cho hiển thị tạm loading (có thể dùng một URL gif hoặc insert text…)
        const range = quill.getSelection(true);
        quill.insertEmbed(range.index, 'image', 'https://i.gifer.com/ZKZg.gif');
        try {
            const url = await uploadImages(file); // gọi API upload lên Cloudinary
            // xóa loading
            quill.deleteText(range.index, 1);
            // chèn ảnh thật
            quill.insertEmbed(range.index, 'image', url, 'user');
            quill.setSelection(range.index + 1);
        } catch (err) {
            console.error('Upload toolbar image thất bại', err);
            // bạn có thể hiện thông báo lỗi, hoặc rollback
        }
    };
}

const uploadImageToCloudinary = async (src) => {
    if (/^https?:\/\//.test(src)) {
        return src;
    }
    const fileData = await fetch(src);
    const blob = await fileData.blob();
    return await uploadImages(blob);
};


watch(() => props.value, (newValue) => {
    if (quill && newValue !== quill.root.innerHTML) {
        quill.root.innerHTML = newValue;
    }
});
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

.ql-editor img {
    max-width: 50% !important;
    height: auto !important;
    display: block;
    margin: 10px auto;
    transition: all 0.4s ease;
}
</style>
