package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.inquiry.InquiryCreateDTO;
import com.example.kotsuexample.dto.inquiry.InquiryDTO;
import com.example.kotsuexample.dto.inquiry.InquiryReplyDTO;
import com.example.kotsuexample.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    // 1. 문의 리스트 조회 (페이지네이션)
    @GetMapping
    public Page<InquiryDTO> getInquiries(Pageable pageable) {
        return inquiryService.getInquiries(pageable);
    }

    // 2. 내가 쓴 문의 조회 (로그인 유저)
    @GetMapping("/my")
    public Page<InquiryDTO> getMyInquiries(@CurrentUser Integer userId, Pageable pageable) {
        return inquiryService.getMyInquiries(userId, pageable);
    }

    // 3. 문의 작성
    @PostMapping
    public void createInquiry(@CurrentUser Integer userId, @RequestBody InquiryCreateDTO dto) {
        inquiryService.createInquiry(userId, dto);
    }

    // 4. 문의 답변 (관리자 한정, 답변 알림도 이쪽에서 트리거)
    @PostMapping("/{id}/reply")
    public void replyToInquiry(
            @PathVariable Integer id,
            @CurrentUser Integer adminId,
            @RequestBody InquiryReplyDTO dto) {
        inquiryService.replyToInquiry(id, adminId, dto);
        // 알림 서비스 호출 등 (예: notificationService.notifyUser(...))
    }
}
