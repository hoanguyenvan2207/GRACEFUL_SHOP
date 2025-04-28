<template>
    <div ref="editor"></div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import Quill from 'quill'
import 'quill/dist/quill.snow.css'

const props = defineProps({
    value: String
})

const emit = defineEmits(['update:value', 'blur', 'change'])
const editor = ref(null)
let quill = null

const toolbarOptions = [
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
]

onMounted(() => {
    quill = new Quill(editor.value, {
        theme: 'snow',
        placeholder: 'Nhập mô tả...',
        modules: {
            toolbar: toolbarOptions
        }
    })

    if (props.value) {
        quill.root.innerHTML = props.value
    }

    quill.on('text-change', () => {
        let html = quill.root.innerHTML
        const strippedHtml = html.replace(/<[^>]+>/g, '').trim()
        if (strippedHtml === '') {
            html = ''
        }
        emit('update:value', html)
        emit('change', html) // Thêm dòng này để phát sự kiện change
    })

    quill.on('blur', () => {
        emit('blur')
    })
})

watch(() => props.value, (newValue) => {
    if (quill && newValue !== quill.root.innerHTML) {
        quill.root.innerHTML = newValue
    }
})
</script>

<style>
.ql-editor {
    min-height: 200px;
}
</style>