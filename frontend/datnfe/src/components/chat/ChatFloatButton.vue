<template>
    <div class="floating-chat shadow">
        <!-- Nút chat toggle -->
        <div v-if="customerId">
            <a-badge :count="unreadCount">
                <button v-if="!isOpen" @click="toggleChat" class="chat-toggle">
                    <i class="bi bi-chat-quote-fill"></i>
                </button>
            </a-badge>
        </div>
        <transition name="chat">
            <div v-if="isOpen" class="chat-window shadow">
                <div class="header">
                    <h5>Chat với nhân viên</h5>
                    <button @click="toggleChat" class="close-button"><i class="bi bi-x-lg"></i></button>
                </div>

                <div class="message-history" ref="messageHistory">
                    <div v-for="msg in messages" :key="msg.id || msg.tempId" :class="['chat-message', msg.senderType]">
                        <!-- Nếu tin nhắn là link ảnh thì hiển thị ảnh -->
                        <div class="message-content">
                            <template v-if="isImage(msg.noiDung)">
                                <img :src="msg.noiDung" height="300" alt="Ảnh chat" class="chat-image" />
                            </template>
                            <template v-else>
                                {{ msg.noiDung }}
                            </template>
                        </div>
                        <small class="font-italic">{{ formatDateTime(msg.ngayGui) }}</small>
                    </div>
                </div>

                <div class="input-container d-flex align-items-center w-100">
                    <input ref="textInput" type="text" v-model="newMessage" @keyup.enter="sendMessage"
                        placeholder="Nhập tin nhắn..." />
                    <!-- Nút chọn file ảnh -->
                    <input type="file" ref="imageInput" @change="handleFileChange" accept="image/*" hidden />
                    <button @click="triggerFileInput" class="btn btn-secondary ms-2">
                        <i class="bi bi-image"></i>
                    </button>
                    <!-- Nút thu âm -->
                    <button @click="toggleRecording" class="mic-button ms-2">
                        <i :class="recording ? 'bi bi-stop-circle-fill text-danger' : 'bi bi-mic-fill'"></i>
                    </button>
                </div>
            </div>
        </transition>
    </div>
</template>


<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import SockJS from 'sockjs-client'
import { over } from 'stompjs'
import axios from 'axios'
import { uploadImages } from '../san_pham/api'

const props = defineProps({
    customerId: {
        type: Number,
        required: true
    }
})
const textInput = ref(null)
const imageInput = ref(null)
const isOpen = ref(false)
const room = ref(null)
const messages = ref([])
const stompClient = ref(null)
const newMessage = ref('')
const messageHistory = ref(null)
const isConnected = ref(false)
const unreadCount = ref(0)
const recording = ref(false)
let recognition = null

if ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window) {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    recognition = new SpeechRecognition()
    recognition.lang = 'vi-VN'
    recognition.continuous = false
    recognition.interimResults = false

    recognition.onresult = (event) => {
        // Lấy kết quả chuyển giọng nói thành văn bản
        newMessage.value = event.results[0][0].transcript
        recording.value = false

        // Sau khi kết quả được gán, đặt con trỏ ở cuối tin nhắn
        nextTick(() => {
            if (textInput.value) {
                textInput.value.focus()
                const length = newMessage.value.length
                textInput.value.setSelectionRange(length, length)
            }
        })
    }
    recognition.onerror = (err) => {
        console.error('Speech recognition error:', err)
        recording.value = false
    }
    recognition.onend = () => {
        recording.value = false
    }
} else {
    console.warn('Trình duyệt của bạn không hỗ trợ SpeechRecognition')
}

// Hàm toggle thu âm: bắt đầu hoặc dừng thu âm
const toggleRecording = () => {
    if (!recording.value) {
        try {
            recognition && recognition.start()
            recording.value = true
        } catch (err) {
            console.error('Không thể bắt đầu thu âm:', err)
        }
    } else {
        recognition && recognition.stop()
        recording.value = false
    }
}

const triggerFileInput = () => {
    imageInput.value && imageInput.value.click()
}

const handleFileChange = async (event) => {
    const file = event.target.files[0]
    if (!file) return

    try {
        // Hiển thị loading hoặc thông báo đang upload nếu cần
        const imageUrl = await uploadImages(file)
        // Gửi tin nhắn với nội dung là đường dẫn ảnh
        await sendImageMessage(imageUrl)
        // Reset input file
        event.target.value = ''
    } catch (error) {
        console.error('Lỗi khi upload ảnh:', error)
    }
}

function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    const days = ["CN", "T2", "T3", "T4", "T5", "T6", "T7"];
    const day = days[date.getDay()];
    const hours = date.getHours();
    const minutes = date.getMinutes().toString().padStart(2, "0");
    return `${hours}:${minutes} ${day}`;
}

onMounted(async () => {
    if (!props.customerId) return
    try {
        const response = await fetch(`/api/chat/room/${props.customerId}`)
        if (!response.ok) {
            throw new Error('Network response was not ok')
        }
        room.value = await response.json()
        await fetchMessages()
        await connectWebSocket()
    } catch (error) {
        console.error('Error getting chat room info:', error)
    }
})

onBeforeUnmount(() => {
    disconnectWebSocket()
})

const markAsRead = async () => {
    try {
        await axios.post(`/api/chat/messages/${room.value.id}/read?readerType=STAFF`);
        unreadCount.value = 0
    } catch (error) {
        console.error('Lỗi khi cập nhật trạng thái đã đọc', error);
    }
}

const fetchMessages = async () => {
    if (!room.value || !room.value.id) return

    try {
        const response = await fetch(`/api/chat/messages/${room.value.id}`)
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        messages.value = data

        unreadCount.value = messages.value.filter(
            (msg) => msg.ngayDoc === null && msg.senderType === 'STAFF'
        ).length

        await nextTick()
        scrollToBottom()
    } catch (error) {
        console.error('Error fetching messages:', error)
    }
}

const connectWebSocket = async () => {
    if (!room.value || !room.value.id) return

    try {
        if (stompClient.value && stompClient.value.connected) {
            stompClient.value.disconnect()
        }

        const socket = new SockJS('/ws-chat')
        stompClient.value = over(socket)
        stompClient.value.debug = null

        return new Promise((resolve, reject) => {
            stompClient.value.connect({}, frame => {
                isConnected.value = true

                stompClient.value.subscribe(`/topic/room/${room.value.id}`, messageData => {
                    try {
                        const receivedMessage = JSON.parse(messageData.body)

                        const tempIndex = messages.value.findIndex(m =>
                            m.tempId && m.noiDung === receivedMessage.noiDung &&
                            m.senderType === receivedMessage.senderType
                        )

                        if (tempIndex >= 0) {
                            messages.value.splice(tempIndex, 1, receivedMessage)
                        } else {
                            if (!messages.value.some(m => m.id === receivedMessage.id)) {
                                messages.value.push(receivedMessage)
                            }
                        }
                        nextTick(() => fetchMessages())
                        nextTick(() => scrollToBottom())
                    } catch (error) {
                        console.error('Error processing received message:', error)
                    }
                })

                resolve()
            }, error => {
                console.error('WebSocket connection error:', error)
                isConnected.value = false
                reject(error)
            })
        })
    } catch (error) {
        console.error('Error setting up WebSocket:', error)
        isConnected.value = false
        setTimeout(() => connectWebSocket(), 3000)
    }
}

const disconnectWebSocket = () => {
    if (stompClient.value && stompClient.value.connected) {
        stompClient.value.disconnect()
        isConnected.value = false
    }
}

const sendImageMessage = async (imageUrl) => {
    if (!room.value) return

    // Tạo tin nhắn tạm thời để hiển thị
    const tempId = `temp-${Date.now()}`
    const tempMessage = {
        tempId,
        idChatRoom: room.value.id,
        senderType: 'CUSTOMER',
        noiDung: imageUrl  // Nội dung tin nhắn là đường link ảnh
    }
    messages.value.push(tempMessage)
    nextTick(() => scrollToBottom())

    try {
        if (!isConnected.value) {
            await connectWebSocket()
        }

        const msg = {
            idChatRoom: room.value.id,
            senderType: 'CUSTOMER',
            noiDung: imageUrl
        }
        stompClient.value.send("/app/chat.sendMessage", {}, JSON.stringify(msg))
    } catch (error) {
        console.error('Error sending image message:', error)
        connectWebSocket()
    }
}

function isImage(content) {
    // Kiểm tra nếu nội dung là URL ảnh (có đuôi .jpg, .png, .gif, ...)
    return typeof content === 'string' && /\.(jpg|jpeg|png|gif)$/i.test(content)
}

const sendMessage = async () => {
    if (!newMessage.value.trim() || !room.value) return

    const tempId = `temp-${Date.now()}`
    const tempMessage = {
        tempId,
        idChatRoom: room.value.id,
        senderType: 'CUSTOMER',
        noiDung: newMessage.value
    }
    messages.value.push(tempMessage)
    const messageContent = newMessage.value
    newMessage.value = ''
    nextTick(() => scrollToBottom())

    try {
        if (!isConnected.value) {
            await connectWebSocket()
        }
        const msg = {
            idChatRoom: room.value.id,
            senderType: 'CUSTOMER',
            noiDung: messageContent
        }
        stompClient.value.send("/app/chat.sendMessage", {}, JSON.stringify(msg))
    } catch (error) {
        console.error('Error sending message:', error)
        connectWebSocket()
    }
}

const scrollToBottom = () => {
    if (messageHistory.value) {
        messageHistory.value.scrollTop = messageHistory.value.scrollHeight
        markAsRead()
    }
}

const toggleChat = async () => {
    isOpen.value = !isOpen.value

    if (isOpen.value && room.value) {
        await fetchMessages()
        await connectWebSocket()
        nextTick(() => {
            if (textInput.value) {
                textInput.value.focus()
                const length = newMessage.value.length
                textInput.value.setSelectionRange(length, length)
            }
        })
    }
}
</script>

<style scoped>
.input-container {
    display: flex;
    width: 100%;
    border-top: 1px solid #ddd;
    border-radius: 0 0 10px 10px;
}

.input-container input {
    flex: 1;
    padding: 8px;
    border: none;
    outline: none;
}

.input-container .mic-button {
    padding: 8px 12px;
    background: #2196F3;
    color: white;
    border-radius: 50%;
    border: none;
    cursor: pointer;
}

.chat-enter-active,
.chat-leave-active {
    transition: opacity 0.3s, transform 0.3s;
}

.chat-enter-from,
.chat-leave-to {
    opacity: 0;
    transform: translate(70%, 70%);
}

.floating-chat {
    position: fixed;
    bottom: 70px;
    right: 30px;
    border-radius: 50%;
    z-index: 100;
}

/* Style cho nút chat toggle có icon */
.chat-toggle {
    padding: 15px;
    border-radius: 50%;
    background: #2196F3;
    color: white;
    border: none;
    cursor: pointer;
    font-size: 34px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
}

.chat-toggle:hover {
    background: #0d8af0;
    color: rgb(234, 231, 231);
    transform: scale(1.1);
}

.chat-window {
    width: 400px;
    height: 600px;
    border: 1px solid #ccc;
    display: flex;
    flex-direction: column;
    position: fixed;
    bottom: 20px;
    right: 20px;
    z-index: 9999;
    background: #fff;
    border-radius: 10px;
}

.header {
    padding: 10px;
    background: #2196F3;
    color: white;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-radius: 5px 5px 0 0;
}

.message-history {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    display: flex;
    flex-direction: column;
}

.chat-message {
    margin: 5px 0;
    padding: 10px;
    border-radius: 5px;
    max-width: 70%;
    background-color: #f1f1f1;
}

.chat-message .message-content {
    font-size: 1rem;
    font-weight: 500;
    margin: 0;
}

.chat-message small {
    font-size: 0.7rem;
    color: #888;
}

.CUSTOMER {
    align-self: flex-end;
    background: #e3f2fd;
    text-align: right;
}

.STAFF {
    align-self: flex-start;
    background: #f5f5f5;
    text-align: left;
}

input {
    padding: 8px;
    border: none;
    border-top: 1px solid #ddd;
    border-radius: 0 0 10px 10px;
    outline: none;
}

button {
    padding: 5px 10px;
    background: #2196F3;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
}

.close-button {
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 18px;
}

.close-button:hover {
    color: red;
    transform: scale(1.5);
}
</style>
