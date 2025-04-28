package com.example.datn_be.service.sanpham.serviceImpl;

import com.example.datn_be.dto.sanpham.ChatLieuDTO;
import com.example.datn_be.entiy.san_pham.ChatLieu;
import com.example.datn_be.exception.ChatLieuDuplicateException;
import com.example.datn_be.repository.san_pham.ChatLieuRepository;
import com.example.datn_be.service.sanpham.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    public static ChatLieuDTO toDTO(ChatLieu chatLieu) {
        if (chatLieu == null) {
            return null;
        }
        return new ChatLieuDTO(
                chatLieu.getId(),
                chatLieu.getTen(),
                chatLieu.getMoTa(),
                chatLieu.getTrangThai(),
                chatLieu.getNgayTao()
        );
    }

    public static ChatLieu toEntity(ChatLieuDTO dto) {
        if (dto == null) {
            return null;
        }
        ChatLieu chatLieu = new ChatLieu();
        chatLieu.setId(dto.getId());
        chatLieu.setTen(dto.getTenChatLieu());
        chatLieu.setMoTa(dto.getMoTa());
        chatLieu.setTrangThai(dto.getTrangThai());
        chatLieu.setNgayTao(dto.getNgayTao());
        return chatLieu;
    }

    public static String removeAccentAndToLower(String input) {
        if (input == null) {
            return null;
        }

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String noAccent = normalized.replaceAll("\\p{M}", "");

        return noAccent.toLowerCase();
    }

    @Override
    public List<ChatLieuDTO> getChatLieusByTrangThaiTrue() {
        return chatLieuRepository.findChatLieusByTrangThaiIsTrue().stream().map(ChatLieuServiceImpl::toDTO).toList();
    }

    @Override
    public List<ChatLieuDTO> findAllChatLieu() {
        return chatLieuRepository.findAll().stream().map(ChatLieuServiceImpl::toDTO).toList();
    }

    @Override
    public ChatLieuDTO getChatLieuByTen(String tenChatLieu) {
        Optional<ChatLieu> chatLieu = chatLieuRepository.findChatLieusByTen(tenChatLieu);
        return chatLieu.map(ChatLieuServiceImpl::toDTO).orElse(null);
    }

    @Override
    public Page<ChatLieuDTO> findByTenContainingIgnoreCaseAndTrangThaiIsNullOrTrangThai(int page, int size, String keyword, Boolean trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatLieu> chatLieuPage = chatLieuRepository.findChatLieusByTenContainingIgnoreCase(
                removeAccentAndToLower(keyword), trangThai, pageable);
        return chatLieuPage.map(ChatLieuServiceImpl::toDTO);
    }


    @Override
    public ChatLieuDTO addChatLieu(ChatLieuDTO chatLieuDTO) {

        if (chatLieuRepository.existsChatLieuByTen(chatLieuDTO.getTenChatLieu())) {
            throw new ChatLieuDuplicateException("Tên chất liệu đã tồn tại");
        }
        ChatLieu chatLieu = toEntity(chatLieuDTO);
        chatLieu.setNgayTao(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        ChatLieu savedChatLieu = chatLieuRepository.save(chatLieu);
        return toDTO(savedChatLieu);
    }


    @Override
    public ChatLieuDTO updateChatLieu(int id, ChatLieuDTO chatLieuDTO) {
        Optional<ChatLieu> existingChatLieuOptional = chatLieuRepository.findById(id);

        if (existingChatLieuOptional.isPresent()) {
            ChatLieu existingChatLieu = existingChatLieuOptional.get();

        if (!existingChatLieu.getTen().equals(chatLieuDTO.getTenChatLieu()) &&
                chatLieuRepository.existsChatLieuByTen(chatLieuDTO.getTenChatLieu())) {
            throw new ChatLieuDuplicateException("Tên chất liệu đã tồn tại");
        }

            existingChatLieu.setTen(chatLieuDTO.getTenChatLieu());
            existingChatLieu.setMoTa(chatLieuDTO.getMoTa());
            existingChatLieu.setTrangThai(chatLieuDTO.getTrangThai());

            ChatLieu updatedChatLieu = chatLieuRepository.save(existingChatLieu);

            return toDTO(updatedChatLieu);
        } else {
            return null;
        }
    }

    @Override
    public ChatLieuDTO getChatLieuById(int id) {
        Optional<ChatLieu> chatLieuOptional = chatLieuRepository.findById(id);
        if (chatLieuOptional.isPresent()) {
            ChatLieu chatLieu = chatLieuOptional.get();
            return toDTO(chatLieu);
        } else {
            return null;
        }
    }

}