package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.entity.StudyRoom;
import com.example.kotsuexample.entity.StudyRoomMember;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.exception.NoneInputValueException;
import com.example.kotsuexample.repository.StudyRoomMemberRepository;
import com.example.kotsuexample.repository.StudyRoomRepository;
import com.example.kotsuexample.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;

    // 1. 생성
    @Transactional
    public StudyRoomDto createStudyRoom(Integer userId, CreateStudyRoomRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (studyRoomRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("이미 존재하는 스터디룸 이름입니다.");
        }

        StudyRoom studyRoom = StudyRoom.builder()
                .name(dto.getName())
                .rule(dto.getRule())
                .notice(dto.getNotice())
                .imageUrl(dto.getImageUrl())
                .tags(dto.getTags())
                .leader(user)
                .createdAt(LocalDateTime.now())
                .maxUserCount(dto.getMaxUserCount())
                .build();

        // 저장 먼저 (ID 생성 및 cascade용)
        studyRoomRepository.save(studyRoom);

        // 리더도 멤버로 등록
        StudyRoomMember member = StudyRoomMember.builder()
                .studyRoom(studyRoom)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();

        // 양방향일 때는 addMember를 통해 컬렉션 양쪽 동기화
        studyRoom.addMember(member);

        // member 저장 (CascadeType.ALL 이면 studyRoom만 저장해도 persist됨. 안전하게 별도 저장)
        studyRoomMemberRepository.save(member);

        return toDto(studyRoom);
    }


    // 2. 목록 조회
    public List<StudyRoomSummaryDto> getStudyRoomList(Integer userId) {
        List<StudyRoom> rooms = studyRoomRepository.findAllByMemberUserId(userId);

        return rooms.stream().map(room -> StudyRoomSummaryDto.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .imageUrl(room.getImageUrl())
                        .tags(room.getTags())
                        .currentMemberCount(studyRoomMemberRepository.countByStudyRoom(room))
                        .maxUserCount(room.getMaxUserCount())
                        .leaderId(room.getLeader().getId())
                        .build())
                .collect(Collectors.toList());
    }

    // 3. 상세 조회
    public StudyRoomDetailDto getStudyRoom(Integer userId, Integer id) {
        StudyRoom room = studyRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디룸이 존재하지 않습니다."));

        boolean isMember = studyRoomMemberRepository.existsByStudyRoomAndUserId(room, userId);
        if (!isMember) throw new NoneInputValueException("멤버가 아니라서 접근 권한이 없습니다!");

        int memberCount = studyRoomMemberRepository.countByStudyRoom(room);

        return StudyRoomDetailDto.builder()
                .id(room.getId())
                .name(room.getName())
                .rule(room.getRule())
                .notice(room.getNotice())
                .imageUrl(room.getImageUrl())
                .maxUserCount(room.getMaxUserCount())
                .currentMemberCount(memberCount)
                .tags(room.getTags())
                .leaderId(room.getLeader().getId())
                .createdAt(room.getCreatedAt())
                .build();
    }

    // 4. 수정 (리더만 가능)
    @Transactional
    public StudyRoomDto updateStudyRoom(Integer userId, Integer id, UpdateStudyRoomRequest dto) {
        StudyRoom room = studyRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디룸이 존재하지 않습니다."));

        if (!room.getLeader().getId().equals(userId)) {
            throw new SecurityException("수정 권한이 없습니다.");
        }

        room.update(
                dto.getName(),
                dto.getRule(),
                dto.getNotice(),
                dto.getImageUrl(),
                dto.getMaxUserCount(),
                dto.getTags()
        );

        return toDto(room);
    }

    // 5. 삭제 (리더만 가능)
    @Transactional
    public void deleteStudyRoom(Integer userId, Integer id) {
        StudyRoom room = studyRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스터디룸이 존재하지 않습니다."));

        if (!room.getLeader().getId().equals(userId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        studyRoomRepository.delete(room);
    }

    // ---- 내부 변환 메서드/업데이트 로직 예시 ----
    private StudyRoomDto toDto(StudyRoom room) {
        return StudyRoomDto.builder()
                .id(room.getId())
                .name(room.getName())
                .rule(room.getRule())
                .notice(room.getNotice())
                .imageUrl(room.getImageUrl())
                .maxUserCount(room.getMaxUserCount())
                .tags(room.getTags())
                .leaderId(room.getLeader().getId())
                .createdAt(room.getCreatedAt())
                .build();
    }
}
