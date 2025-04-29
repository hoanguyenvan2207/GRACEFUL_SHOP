<template>
    <!-- Chat Box -->
    <div class="chat-container" v-if="isOpen">
        <div class="chat-header">
            <span>{{ t('chat.chatbot.title') }}</span>
            <button class="close-button" @click="toggleChat"><i class="bi bi-x-lg"></i></button>
        </div>
        <div class="chat-body">
            <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.sender]">
                <span v-html="msg.text"></span>
            </div>

            <div v-if="isTyping" class="typing-indicator">
                <strong>{{ t('chat.chatbot.typing') }}<span class="dot">.</span><span class="dot">.</span><span
                        class="dot">.</span></strong>
            </div>
        </div>
        <div class="chat-input">
            <input v-model="userInput" type="text" ref="textInput" :placeholder="t('chat.chatbot.message')"
                @keyup.enter="sendMessage" />
            <button @click="toggleRecording" class="ms-2">
                <i :class="recording ? 'bi bi-stop-circle-fill text-danger' : 'bi bi-mic-fill'"></i>
            </button>
            <button @click="sendMessage"><i class="bi bi-send-fill"></i></button>
        </div>
    </div>
    <!-- Nút mở chat -->
    <button class="open-chat-btn shadow" @click="toggleChat" v-if="!isOpen">
        <i class="bi bi-wechat"></i>
    </button>
</template>

<script setup>
import { ref, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import axios from 'axios';

const { t } = useI18n();
const isOpen = ref(false);
const userInput = ref('');
const messages = ref([]);
const isTyping = ref(false);
const recording = ref(false);
let recognition = null;

if ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window) {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
    recognition = new SpeechRecognition()
    recognition.lang = 'vi-VN'
    recognition.continuous = false
    recognition.interimResults = false

    recognition.onresult = (event) => {
        // Lấy kết quả chuyển giọng nói thành văn bản
        userInput.value = event.results[0][0].transcript
        recording.value = false

        // Sau khi kết quả được gán, đặt con trỏ ở cuối tin nhắn
        nextTick(() => {
            if (textInput.value) {
                textInput.value.focus()
                const length = userInput.value.length
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

// Toggle Chat Box
const toggleChat = () => {
    isOpen.value = !isOpen.value;
    if (isOpen.value) {
        nextTick(() => {
            const inputField = document.querySelector('.chat-input input');
            if (inputField) {
                inputField.focus();
            }
            scrollToBottom(); // Cuộn xuống khi mở chat
        });
    }
};

// Gửi tin nhắn đến API backend
const sendMessage = async () => {
    if (!userInput.value.trim()) return;

    messages.value.push({ sender: 'user', text: userInput.value });
    scrollToBottom();

    const questions = ref(userInput.value);
    userInput.value = '';

    // Hiển thị trạng thái Gemini đang nhập
    isTyping.value = true;

    // Gửi request đến API Spring Boot (Sử dụng /api/advisor/consult để tư vấn sản phẩm)
    try {
        const response = await axios.post('/api/advisor/consult', {
            query: questions.value
        })

        const reply = response.data.advice

        // Ẩn hiệu ứng "Gemini đang nhập..." và hiển thị phản hồi từ Gemini
        isTyping.value = false;
        messages.value.push({ sender: 'gemini', text: reply });
        scrollToBottom();
    } catch (error) {
        isTyping.value = false;
        console.log(error);

        messages.value.push({ sender: 'gemini', text: 'Có lỗi xảy ra, vui lòng thử lại.' });
    }
};

const scrollToBottom = () => {
    nextTick(() => {
        const chatBody = document.querySelector('.chat-body');
        if (chatBody) {
            chatBody.scrollTop = chatBody.scrollHeight;
        }
    });
};

</script>

<style scoped>
/* Floating Chat Styles */
.chat-container {
    position: fixed;
    bottom: 20px;
    right: 20px;
    width: 400px;
    height: 600px;
    display: flex;
    flex-direction: column;
    background: white;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    overflow: hidden;
    z-index: 9999;
}

.chat-header {
    background: #007bff;
    color: white;
    padding: 10px;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    cursor: pointer;
}

button {
    padding: 2px 5px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
}

.close-button {
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 16px;
}

.close-button:hover {
    color: red;
    transform: scale(1.2);
}

.chat-body {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
    background: #f9f9f9;
    display: flex;
    flex-direction: column;
}

.message {
    margin-bottom: 10px;
    padding: 5px;
    border-radius: 5px;
}

.message.user {
    text-align: right;
    background: #007bff;
    border: 1px solid #0090a6;
    white-space: pre-wrap;
    word-wrap: break-word;
    width: fit-content;
    align-self: flex-end;
    color: white;
}

.message.gemini {
    text-align: left;
    background: #eeeeee;
    white-space: pre-wrap;
    word-wrap: break-word;
    border: 1px solid #ccc;
    align-self: flex-start;
    /* Căn trái tin nhắn của Gemini */
}

.chat-input {
    display: flex;
    padding: 10px;
    border-top: 1px solid #ccc;
    background: white;
}

.chat-input input {
    flex: 1;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 20px;
    outline: none;
}

.chat-input input:focus {
    border-color: #007bff;
    box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
    transition: all 0.3s ease;

}

.chat-input button {
    margin-left: 5px;
    padding: 5px 10px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 50%;
    cursor: pointer;
}

.chat-input button:hover {
    background: #0056b3;
    transition: all 0.3s ease;
    box-shadow: 0 0 5px rgba(99, 174, 255, 0.5);
    transform: scale(1.1);
}

/* Hiệu ứng "Gemini đang nhập..." với dấu ba chấm lượn giống rắn */
.typing-indicator {
    font-style: italic;
    color: #888;
    padding: 10px 0;
    display: inline-block;
    white-space: nowrap;
}

.typing-indicator .dot {
    display: inline-block;
    font-size: 20px;
    animation: snake 1.5s infinite ease-in-out;
}

/* Điều chỉnh độ trễ cho mỗi dấu chấm */
.typing-indicator .dot:nth-child(1) {
    animation-delay: 0s;
}

.typing-indicator .dot:nth-child(2) {
    animation-delay: 0.3s;
}

.typing-indicator .dot:nth-child(3) {
    animation-delay: 0.6s;
}

@keyframes snake {

    0%,
    100% {
        transform: translateY(0);
    }

    50% {
        transform: translateY(-5px);
    }
}

/* Floating Chat Button */
.open-chat-btn {
    position: fixed;
    bottom: 30px;
    right: 20px;
    background: #007bff;
    color: white;
    border: none;
    border-radius: 50%;
    padding: 3px 14px;
    font-size: 40px;
    cursor: pointer;
    z-index: 999;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}

.open-chat-btn:hover {
    background: #0056b3;
    box-shadow: 0 0 5px rgba(99, 174, 255, 0.5);
    transform: scale(1.1);
    transition: all 0.3s ease;
}
</style>
