package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.study.StudyRequestCreateDTO;
import com.example.kotsuexample.dto.study.StudyRequestResponse;
import com.example.kotsuexample.service.StudyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/study-request")
public class StudyRequestController {

    private final StudyRequestService studyRequestService;

    // 1. 신청하기
    @PostMapping
    public Integer create(
            @CurrentUser Integer userId,
            @RequestBody StudyRequestCreateDTO req
    ) {
        return studyRequestService.createStudyRequest(userId, req);
    }

    // 2. 내가 신청한 내역 전체 조회
    @GetMapping("/my")
    public List<StudyRequestResponse> getMyRequests(@CurrentUser Integer userId) {
        return studyRequestService.getMyStudyRequests(userId);
    }

    // 3. 내가 신청한 특정 모집글의 내 신청 내역 단건 조회 (있으면 반환, 없으면 404)
    @GetMapping("/my/{studyRecruitId}")
    public StudyRequestResponse getMyRequestByRecruit(
            @CurrentUser Integer userId,
            @PathVariable Integer studyRecruitId
    ) {
        return studyRequestService.getMyRequestByRecruit(userId, studyRecruitId);
    }

    // 4. 내가 신청한 내역 취소 (soft delete or delete)
    @DeleteMapping("/{requestId}")
    public void cancelMyRequest(@CurrentUser Integer userId, @PathVariable Integer requestId) {
        studyRequestService.cancelMyRequest(userId, requestId);
    }

    // 5. 특정 모집글의 모든 지원자 내역 (리더 권한 필요)
    @GetMapping("/recruit/{studyRecruitId}")
    public List<StudyRequestResponse> getRequestsByRecruit(
            @CurrentUser Integer userId,
            @PathVariable Integer studyRecruitId
    ) {
        return studyRequestService.getRequestsByRecruit(userId, studyRecruitId);
    }

    // 6. 리더가 지원자의 요청을 승인/거절
    @PatchMapping("/{requestId}/status")
    public void updateRequestStatus(
            @CurrentUser Integer leaderId,
            @PathVariable Integer requestId,
            @RequestParam("status") String status // "ACCEPTED" or "REJECTED"
    ) {
        studyRequestService.updateRequestStatus(leaderId, requestId, status);
    }
}
