<template>
    <h4 class="text-center my-3">CHAT VỚI KHÁCH HÀNG</h4>
    <div class="staff-interface">
        <div class="sidebar">
            <!-- Bộ lọc trạng thái -->
            <div class="filter">
                <a-select v-model:value="statusFilter" placeholder="Chọn trạng thái" style="width: 100%"
                    @change="handleFilterChange">
                    <a-select-option value="">Tất cả</a-select-option>
                    <a-select-option value="true">Đã có nhân viên</a-select-option>
                    <a-select-option value="false">Chưa có nhân viên</a-select-option>
                </a-select>
            </div>
            <!-- Danh sách phòng chat -->
            <div class="room-list">
                <div v-for="room in rooms" :key="room.id" class="room-item" :class="{ 'disabled': room.trangThai }"
                    @click="!room.trangThai && handleRoomClick(room)">
                    <a-space :size="24" direction="vertical">
                        <a-badge :count="room.unreadCount">
                            <a-avatar :size="50" shape="square">
                                <template #icon>
                                    <i class="bi bi-person-fill"></i>
                                </template>
                            </a-avatar>
                        </a-badge>
                    </a-space>
                    <div class="room-info">
                        <span>
                            Khách hàng {{ room.id }} - {{ room.trangThai ? 'Đã có NV' : 'Chưa có NV' }}
                        </span>
                        <!-- Hiển thị tin nhắn mới nhất nếu có -->
                        <p class="last-message" :class="{ unread: room.unreadCount > 0 }">
                            {{ room.lastMessage ? room.lastMessage : 'Không có tin nhắn' }}
                        </p>
                    </div>
                </div>
            </div>
        </div>
        <!-- Chat Window -->
        <transition name="chat">
            <ChatWindow v-if="selectedRoom" :room-id="selectedRoom" sender-type="STAFF" @close="handleClose" />
        </transition>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import ChatWindow from './ChatWindow.vue'
import SockJS from 'sockjs-client'
import { Client } from '@stomp/stompjs'
const staffId = 1
const rooms = ref([])
const selectedRoom = ref(null)
const statusFilter = ref('')
const isJoinedRoom = ref(false)
const isConnected = ref(false)

const socketUrl = '/ws-chat'
const stompClient = new Client({
    webSocketFactory: () => new SockJS(socketUrl),
    reconnectDelay: 5000
})

stompClient.onConnect = (frame) => {
    isConnected.value = true
    stompClient.subscribe('/topic/room/update', (message) => {
        let update = JSON.parse(message.body);
        if (!Array.isArray(update)) {
            updateRoom(update);
        } else {
            mergeRooms(update);
        }
    });


    stompClient.subscribe('/topic/messages/updates', (message) => {
        const updates = JSON.parse(message.body)
        updateRoomMessages(updates)
    })

    requestRoomsUpdate()
}

const updateRoom = (updatedRoom) => {
    rooms.value = rooms.value.map(room => {
        if (room.id === updatedRoom.id) {
            return {
                ...room,
                ...updatedRoom
            }
        }
        return room;
    });
}


const mergeRooms = (updatedRooms) => {
    if (!updatedRooms || !Array.isArray(updatedRooms)) return

    if (rooms.value.length === 0) {
        rooms.value = updatedRooms
        return
    }

    const currentRoomsMap = {}
    rooms.value.forEach(room => {
        currentRoomsMap[room.id] = room
    })

    rooms.value = updatedRooms.map(newRoom => {
        const currentRoom = currentRoomsMap[newRoom.id]
        if (currentRoom) {
            return {
                ...newRoom,
                lastMessage: newRoom.lastMessage || currentRoom.lastMessage,
                unreadCount: newRoom.unreadCount !== undefined ? newRoom.unreadCount : currentRoom.unreadCount
            }
        }
        return newRoom
    })
}

const updateRoomMessages = (updates) => {
    if (!updates || !updates.roomId) return

    rooms.value = rooms.value.map(room => {
        if (room.id === updates.roomId) {
            return {
                ...room,
                lastMessage: updates.lastMessage || room.lastMessage,
                unreadCount: updates.unreadCount !== undefined ? updates.unreadCount : room.unreadCount
            }
        }
        return room
    })
}

const requestRoomsUpdate = () => {
    if (!isConnected.value) {
        console.warn('STOMP chưa kết nối, không thể gửi yêu cầu')
        return
    }
    const status = statusFilter.value === '' ? null : statusFilter.value
    stompClient.publish({
        destination: '/app/fetch-rooms',
        body: JSON.stringify({ status })
    })
}

const handleFilterChange = () => {
    requestRoomsUpdate()
}

onMounted(() => {
    stompClient.activate()
})

// Hàm joinRoom: thực hiện join vào phòng được truyền vào
const joinRoom = async (roomId) => {
    try {
        const response = await fetch(
            `/api/chat/room/${roomId}/join?staffId=${staffId}`,
            { method: 'POST' }
        )
        // Nếu join thành công, server trả về thông tin phòng mới cập nhật (đã có nhân viên)
        const updatedRoom = await response.json()
        rooms.value = rooms.value.map(r => {
            if (r.id === roomId) {
                return {
                    ...updatedRoom,
                    lastMessage: updatedRoom.lastMessage || r.lastMessage
                }
            }
            return r
        })
        selectedRoom.value = roomId
    } catch (error) {
        console.error('Join error:', error)
        // Có thể hiển thị thông báo lỗi cho người dùng
    }
}

// Hàm leaveRoom: rời khỏi phòng hiện tại
const leaveRoom = async (roomId) => {
    try {
        const currentRoom = rooms.value.find(r => r.id === roomId)
        const currentLastMessage = currentRoom ? currentRoom.lastMessage : null

        const response = await fetch(
            `/api/chat/room/${roomId}/out`,
            { method: 'POST' }
        )
        const updatedRoom = await response.json()
        rooms.value = rooms.value.map(room => {
            if (room.id === roomId) {
                return {
                    ...updatedRoom,
                    lastMessage: currentLastMessage || updatedRoom.lastMessage
                }
            }
            return room
        })
        selectedRoom.value = null
    } catch (error) {
        console.error('Leave error:', error)
    }
}

// Xử lý click vào 1 phòng: chỉ cho phép join nếu phòng chưa có nhân viên
const handleRoomClick = async (room) => {
    if (room.trangThai) {
        console.warn('Phòng này đã có nhân viên, không thể join.')
        return
    }
    // Nếu đã có phòng đang join và khác với phòng hiện tại, rời khỏi phòng đó
    if (selectedRoom.value && selectedRoom.value !== room.id) {
        await leaveRoom(selectedRoom.value)
    }
    await joinRoom(room.id)
}

const handleClose = () => {
    if (selectedRoom.value) {
        leaveRoom(selectedRoom.value)
    }
    selectedRoom.value = null
}

onUnmounted(() => {
    stompClient.deactivate()
})
</script>

<style scoped>
.room-item {
    cursor: pointer;
    padding: 10px;
    border-bottom: 1px solid #ccc;
}

/* Khi phòng bị disable */
.room-item.disabled {
    opacity: 0.5;
    pointer-events: none;
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

.staff-interface {
    display: grid;
    grid-template-columns: 800px 1fr;
    gap: 20px;
    padding: 20px;
}

.sidebar {
    display: flex;
    flex-direction: column;
}

.filter {
    margin-bottom: 10px;
}

.room-list {
    border: 1px solid #eee;
    border-radius: 10px;
}

.room-item {
    padding: 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #f0f0f0;
}

.room-info {
    flex: 1;
    margin: 0 20px;
}

.last-message {
    margin: 0;
    color: #888;
    font-size: 14px;
    font-weight: normal;
}

.last-message.unread {
    font-weight: bold;
    color: #000;
}

.room-actions button {
    padding: 5px 10px;
    background: #2196F3;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
}
</style>
