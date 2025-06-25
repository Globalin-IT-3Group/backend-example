package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.dto.study.request.MyStudyRequestResponse;
import com.example.kotsuexample.dto.study.request.StudyRequestCreateDTO;
import com.example.kotsuexample.dto.study.request.StudyRequestResponse;
import com.example.kotsuexample.service.StudyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    // 2. 내가 신청한 내역 전체 조회 (페이지네이션, 기본 6개)
    @GetMapping("/my")
    public Page<MyStudyRequestResponse> getMyRequests(
            @CurrentUser Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return studyRequestService.getMyStudyRequests(userId, page, size);
    }

    // 3. 내가 신청한 특정 모집글의 내 신청 내역 단건 조회 (있으면 반환, 없으면 404)
    @GetMapping("/my/{studyRecruitId}")
    public MyStudyRequestResponse getMyRequestByRecruit(
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

    // 5. 특정 모집글의 모든 지원자 내역 (리더 권한 필요, 페이지네이션, 기본 5개)
    @GetMapping("/recruit/{studyRecruitId}")
    public Page<StudyRequestResponse> getRequestsByRecruit(
            @CurrentUser Integer userId,
            @PathVariable Integer studyRecruitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return studyRequestService.getRequestsByRecruit(userId, studyRecruitId, page, size);
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
