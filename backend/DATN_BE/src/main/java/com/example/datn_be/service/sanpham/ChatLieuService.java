package com.example.datn_be.service.sanpham;

import com.example.datn_be.dto.sanpham.ChatLieuDTO;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatLieuService {

    List<ChatLieuDTO> getChatLieusByTrangThaiTrue();

    List<ChatLieuDTO> findAllChatLieu();

    ChatLieuDTO getChatLieuByTen(String tenChatLieu);

    Page<ChatLieuDTO> findByTenContainingIgnoreCaseAndTrangThaiIsNullOrTrangThai(int page, int size, String keyword, Boolean trangThai);

    @Transactional
    ChatLieuDTO addChatLieu(ChatLieuDTO chatLieuDTO);

    @Transactional
    ChatLieuDTO updateChatLieu(int id, ChatLieuDTO chatLieuDTO);

    ChatLieuDTO getChatLieuById(int id);


}