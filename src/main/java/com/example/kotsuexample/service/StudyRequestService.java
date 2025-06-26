package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.request.MyStudyRequestResponse;
import com.example.kotsuexample.dto.study.request.StudyRequestCreateDTO;
import com.example.kotsuexample.dto.study.request.StudyRequestResponse;
import com.example.kotsuexample.entity.*;
import com.example.kotsuexample.entity.enums.StudyRequestStatus;
import com.example.kotsuexample.exception.ExceedStudyMemberException;
import com.example.kotsuexample.exception.StudyDataNotFoundException;
import com.example.kotsuexample.exception.user.DuplicateException;
import com.example.kotsuexample.exception.user.UserUnauthorizedException;
import com.example.kotsuexample.repository.StudyRecruitRepository;
import com.example.kotsuexample.repository.StudyRequestRepository;
import com.example.kotsuexample.repository.StudyRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRequestService {

    private final StudyRequestRepository studyRequestRepository;
    private final StudyRecruitRepository studyRecruitRepository;
    private final UserService userService;
    private final StudyRoomMemberRepository studyRoomMemberRepository;

    // 1. 신청
    @Transactional
    public Integer createStudyRequest(Integer userId, StudyRequestCreateDTO req) {
        User user = userService.getUserById(userId);
        StudyRecruit recruit = studyRecruitRepository.findById(req.getStudyRecruitId())
                .orElseThrow(() -> new StudyDataNotFoundException("모집글 없음"));

        boolean exists = studyRequestRepository.findByUserIdAndStudyRecruitId(userId, recruit.getId()).isPresent();
        if (exists) throw new DuplicateException("이미 신청한 내역이 있습니다.");

        StudyRequest entity = StudyRequest.builder()
                .user(user)
                .studyRecruit(recruit)
                .title(req.getTitle())
                .message(req.getMessage())
                .status(StudyRequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
        studyRequestRepository.save(entity);
        return entity.getId();
    }

    // 2. 내가 신청한 전체 내역
    @Transactional(readOnly = true)
    public Page<MyStudyRequestResponse> getMyStudyRequests(Integer userId, int page, int size) {
        Page<StudyRequest> requests = studyRequestRepository.findByUserId(
                userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"))
        );
        return requests.map(MyStudyRequestResponse::from);
    }

    // 3. 내가 특정 모집글에 신청한 내역 단건
    @Transactional(readOnly = true)
    public MyStudyRequestResponse getMyRequestByRecruit(Integer userId, Integer studyRecruitId) {
        StudyRequest req = studyRequestRepository
                .findByUserIdAndStudyRecruitId(userId, studyRecruitId)
                .orElseThrow(() -> new StudyDataNotFoundException("신청 내역 없음"));
        return MyStudyRequestResponse.from(req);
    }

    // 4. 신청 취소
    @Transactional
    public void cancelMyRequest(Integer userId, Integer requestId) {
        StudyRequest req = studyRequestRepository.findById(requestId)
                .orElseThrow(() -> new StudyDataNotFoundException("신청 내역 없음"));
        if (!req.getUser().getId().equals(userId)) {
            throw new UserUnauthorizedException("본인 신청만 취소 가능");
        }
        studyRequestRepository.delete(req);
    }

    // 5. 리더가 모집글에 지원한 전체 내역 조회
    @Transactional(readOnly = true)
    public Page<StudyRequestResponse> getRequestsByRecruit(
            Integer leaderId, Integer studyRecruitId, int page, int size, String status) {

        StudyRecruit recruit = studyRecruitRepository.findById(studyRecruitId)
                .orElseThrow(() -> new StudyDataNotFoundException("모집글 없음"));
        if (!recruit.getLeader().getId().equals(leaderId)) {
            throw new UserUnauthorizedException("리더만 조회 가능");
        }

        Page<StudyRequest> requests;
        if (status != null && !status.isBlank()) {
            // status 필터가 있을 때
            requests = studyRequestRepository.findByStudyRecruitIdAndStatus(
                    studyRecruitId, StudyRequestStatus.valueOf(status.toUpperCase()),
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"))
            );
        } else {
            // 전체 조회
            requests = studyRequestRepository.findByStudyRecruitId(
                    studyRecruitId,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestedAt"))
            );
        }
        return requests.map(StudyRequestResponse::from);
    }


    // 6. 리더가 지원 요청 승인/거절
    @Transactional
    public void updateRequestStatus(Integer leaderId, Integer requestId, String status) {
        StudyRequest req = studyRequestRepository.findById(requestId)
                .orElseThrow(() -> new StudyDataNotFoundException("지원 내역 없음"));
        StudyRecruit recruit = req.getStudyRecruit();
        if (!recruit.getLeader().getId().equals(leaderId)) {
            throw new UserUnauthorizedException("리더만 상태 변경 가능");
        }
        StudyRequestStatus newStatus = StudyRequestStatus.valueOf(status.toUpperCase());
        req.updateStatus(newStatus);

        // 승인 시에만 멤버로 추가
        if (newStatus == StudyRequestStatus.ACCEPTED) {
            StudyRoom studyRoom = recruit.getStudyRoom();
            User user = req.getUser();

            // 이미 멤버인지 중복체크
            boolean alreadyMember = studyRoom.getMembers().stream()
                    .anyMatch(member -> member.getUser().getId().equals(user.getId()));

            int membersCount = studyRoom.getMembers().size();

            if (membersCount >= 4) throw new ExceedStudyMemberException("스터디 제한 인원은 네 명입니다!");

            if (!alreadyMember) {
                StudyRoomMember newMember = StudyRoomMember.builder()
                        .studyRoom(studyRoom)
                        .user(user)
                        .joinedAt(LocalDateTime.now())
                        .build();
                studyRoom.addMember(newMember); // 양방향 세팅
                studyRoomMemberRepository.save(newMember);
            }
        }
        // save 생략 (영속성 컨텍스트에 의해 자동 반영)
    }

    // 매일 새벽 3시에 실행 (cron: 0 0 3 * * ?)
    @Scheduled(cron = "0 0 1 * * ?")
    public void deleteExpiredStudyRequests() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<StudyRequestStatus> statuses = List.of(StudyRequestStatus.PENDING, StudyRequestStatus.REJECTED);

        List<StudyRequest> expired =
                studyRequestRepository.findByRequestedAtBeforeAndStatusIn(oneWeekAgo, statuses);

        if (!expired.isEmpty()) {
            studyRequestRepository.deleteAll(expired);
        }
    }
}
