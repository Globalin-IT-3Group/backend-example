package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.inquiry.InquiryCreateDTO;
import com.example.kotsuexample.dto.inquiry.InquiryDTO;
import com.example.kotsuexample.dto.inquiry.InquiryReplyDTO;
import com.example.kotsuexample.entity.Inquiry;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserService userService;

    public Page<InquiryDTO> getInquiries(Pageable pageable) {
        return inquiryRepository.findAll(pageable)
                .map(InquiryDTO::fromEntity);
    }

    public Page<InquiryDTO> getMyInquiries(Integer userId, Pageable pageable) {
        return inquiryRepository.findByUserId(userId, pageable)
                .map(InquiryDTO::fromEntity);
    }

    public void createInquiry(Integer userId, InquiryCreateDTO dto) {
        User user = userService.getUserById(userId);

        Inquiry inquiry = Inquiry.createNewInquiryForSave(
                user,
                dto.getTitle(),
                dto.getContent(),
                dto.getIsPrivate(),
                LocalDateTime.now());
        inquiryRepository.save(inquiry);
    }

    public void replyToInquiry(Integer id, Integer adminId, InquiryReplyDTO dto) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("문의 없음"));

        // (관리자 권한 체크는 보통 Security/AOP에서 처리)
        inquiry.setAdminReply(dto.getAdminReply());
        inquiryRepository.save(inquiry);

        // 👉 여기에 알림 전송 로직 (필요하면)
        // notificationService.notifyUser(inquiry.getUser().getId(), ...);
    }
}
