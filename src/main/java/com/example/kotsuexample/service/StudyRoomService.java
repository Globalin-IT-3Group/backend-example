package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.*;
import com.example.kotsuexample.entity.*;
import com.example.kotsuexample.entity.enums.ChatRoomType;
import com.example.kotsuexample.exception.*;
import com.example.kotsuexample.exception.user.DuplicateException;
import com.example.kotsuexample.exception.user.UserNotFoundByIdException;
import com.example.kotsuexample.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;
    private final StudyRequestRepository studyRequestRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 1. 생성
    @Transactional
    public StudyRoomDto createStudyRoom(Integer userId, CreateStudyRoomRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException("유저를 찾을 수 없습니다."));

        if (studyRoomRepository.existsByName(dto.getName())) {
            throw new DuplicateException("이미 존재하는 스터디룸 이름입니다.");
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

        // ✅ [추가] 스터디방 생성과 동시에 그룹 ChatRoom 생성!
        ChatRoom groupChatRoom = ChatRoom.builder()
                .type(ChatRoomType.GROUP)
                .studyRoomId(studyRoom.getId())
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(groupChatRoom);

        // ✅ [추가] 리더를 그룹 ChatRoom의 멤버로 등록
        ChatRoomMember chatMember = ChatRoomMember.builder()
                .chatRoomId(groupChatRoom.getId())
                .userId(user.getId())
                .joinedAt(LocalDateTime.now())
                .build();
        chatRoomMemberRepository.save(chatMember);

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
                .orElseThrow(() -> new StudyDataNotFoundException("스터디룸이 존재하지 않습니다."));

        boolean isMember = studyRoomMemberRepository.existsByStudyRoomAndUserId(room, userId);
        if (!isMember) throw new OperationNotAllowedException("멤버가 아니라서 접근 권한이 없습니다!");

        int memberCount = studyRoomMemberRepository.countByStudyRoom(room);

        List<StudyRoomMemberDto> members = room.getMembers().stream()
                .map(member -> StudyRoomMemberDto.builder()
                        .userId(member.getUser().getId())
                        .nickname(member.getUser().getNickname())
                        .profileImageUrl(member.getUser().getProfileImage())
                        .isLeader(member.getUser().getId().equals(room.getLeader().getId()))
                        .build())
                .toList();

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
                .members(members)
                .build();
    }

    // 4. 수정 (리더만 가능)
    @Transactional
    public StudyRoomDto updateStudyRoom(Integer userId, Integer id, UpdateStudyRoomRequest dto) {
        StudyRoom room = studyRoomRepository.findById(id)
                .orElseThrow(() -> new StudyDataNotFoundException("스터디룸이 존재하지 않습니다."));

        if (!room.getLeader().getId().equals(userId)) throw new OperationNotAllowedException("수정 권한이 없습니다.");

        int currentMemberCount = room.getMembers().size();
        if (dto.getMaxUserCount() < currentMemberCount) {
            throw new ExceedStudyMemberException(
                    String.format("최대 인원 수는 현재 멤버 수(%d)보다 적을 수 없습니다.", currentMemberCount)
            );
        }

        String inputtedName = dto.getName();
        boolean isExistStudyRoomName = studyRoomRepository.existsByName(inputtedName);
        if (!room.getName().equals(inputtedName) && isExistStudyRoomName) throw new DuplicateException("같은 이름의 스터디방이 존재합니다! 다른 방 이름을 입력해주세요!");

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
                .orElseThrow(() -> new StudyDataNotFoundException("스터디룸이 존재하지 않습니다."));

        if (!room.getLeader().getId().equals(userId)) throw new OperationNotAllowedException("삭제 권한이 없습니다.");

        // 1. 그룹채팅방 먼저 조회
        Optional<ChatRoom> groupChatRoomOpt = chatRoomRepository.findByTypeAndStudyRoomId(ChatRoomType.GROUP, room.getId());
        groupChatRoomOpt.ifPresent(chatRoom -> {
            // 2. 채팅 멤버/메시지 삭제 (cascade/orphanRemoval 적용 안되어 있으면 명시적으로 삭제)
            chatRoomMemberRepository.deleteByChatRoomId(chatRoom.getId());
            // 만약 ChatMessage 등도 있으면 repository.deleteByChatRoomId()도 실행
            // chatMessageRepository.deleteByChatRoomId(chatRoom.getId());
            chatRoomRepository.delete(chatRoom);
        });

        // 3. 스터디룸 멤버 삭제 (cascade면 자동)
        studyRoomMemberRepository.deleteByStudyRoom_Id(room.getId());

        // 4. 스터디룸 삭제
        studyRoomRepository.delete(room);
    }

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

    public StudyRoom getStudyRoomEntity(Integer studyRoomId) {
        return studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 스터디방이 존재하지 않습니다."));
    }

    @Transactional
    public void leaveStudyRoom(Integer userId, Integer studyRoomId) {
        // 1. 스터디룸 존재/본인이 멤버인지 확인
        StudyRoom room = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new StudyDataNotFoundException("스터디룸이 존재하지 않습니다."));

        // 2. 리더면 방 탈퇴 불가
        if (room.getLeader().getId().equals(userId)) {
            throw new OperationNotAllowedException("방장은 탈퇴할 수 없습니다. 방을 삭제하세요.");
        }

        // 3. 멤버인지 확인 후 멤버십 삭제
        StudyRoomMember member = studyRoomMemberRepository
                .findByStudyRoom_IdAndUser_Id(studyRoomId, userId)
                .orElseThrow(() -> new OperationNotAllowedException("멤버가 아닙니다."));

        Integer studyRecruitId = room.getStudyRecruit().getId();

        studyRequestRepository.deleteByUserIdAndStudyRecruitId(userId, studyRecruitId);
        studyRoomMemberRepository.delete(member);

        Optional<ChatRoom> groupChatRoomOpt = chatRoomRepository
                .findByTypeAndStudyRoomId(ChatRoomType.GROUP, studyRoomId);

        groupChatRoomOpt.ifPresent(chatRoom -> chatRoomMemberRepository.deleteByChatRoomIdAndUserId(chatRoom.getId(), userId));
    }
}
