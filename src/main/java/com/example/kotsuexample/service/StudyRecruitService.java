package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.study.StudyRecruitSaveRequestDTO;
import com.example.kotsuexample.dto.study.StudyRecruitDTO;
import com.example.kotsuexample.entity.StudyRecruit;
import com.example.kotsuexample.entity.StudyRoom;
import com.example.kotsuexample.entity.enums.StudyTag;
import com.example.kotsuexample.repository.StudyRecruitRepository;
import com.example.kotsuexample.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyRecruitService {

    private final StudyRecruitRepository studyRecruitRepository;
    private final StudyRoomRepository studyRoomRepository;

    // 태그로 필터(없으면 전체 반환)
    public Page<StudyRecruitDTO> getOpenStudyRecruits(List<StudyTag> tags, Pageable pageable) {
        Page<StudyRecruit> page;
        if (tags == null || tags.isEmpty()) {
            page = studyRecruitRepository.findAllOpen(pageable);
        } else {
            page = studyRecruitRepository.findOpenByTagsAllMatched(tags, tags.size(), pageable);
        }
        return page.map(StudyRecruitDTO::fromEntity);
    }

    // 검색 (title 기준, 페이지네이션)
    public Page<StudyRecruitDTO> searchStudyRecruitsByTitle(String title, Pageable pageable) {
        Page<StudyRecruit> recruits = studyRecruitRepository.findByTitleContainingAndIsOpenTrue(title, pageable);
        return recruits.map(StudyRecruitDTO::fromEntity);
    }

    // 신규 모집글 작성
    public StudyRecruitDTO createStudyRecruit(StudyRecruitSaveRequestDTO dto) {
        StudyRoom studyRoom = studyRoomRepository.findById(dto.getStudyRoomId())
                .orElseThrow(() -> new IllegalArgumentException("스터디방을 찾을 수 없습니다."));
        // 이미 모집글이 있으면 예외
        if (studyRecruitRepository.existsByStudyRoom(studyRoom)) {
            throw new IllegalStateException("이미 등록된 모집글이 있습니다.");
        }

        StudyRecruit entity = new StudyRecruit();
        entity.setStudyRoom(studyRoom);
        entity.setTitle(dto.getTitle());
        entity.setStudyExplain(dto.getStudyExplain());
        entity.setIsOpen(dto.getIsOpen() != null ? dto.getIsOpen() : true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setViewCount(0);
        studyRecruitRepository.save(entity);
        return StudyRecruitDTO.fromEntity(entity);
    }

    // 모집글 수정
    public StudyRecruitDTO updateStudyRecruit(Integer recruitId, StudyRecruitSaveRequestDTO dto) {
        StudyRecruit entity = studyRecruitRepository.findById(recruitId)
                .orElseThrow(() -> new IllegalArgumentException("모집글을 찾을 수 없습니다."));
        entity.setTitle(dto.getTitle());
        entity.setStudyExplain(dto.getStudyExplain());
        if (dto.getIsOpen() != null) entity.setIsOpen(dto.getIsOpen());
        studyRecruitRepository.save(entity);
        return StudyRecruitDTO.fromEntity(entity);
    }

    public StudyRecruitSaveRequestDTO getStudyRecruitInStudyRoom(Integer studyRoomId) {
        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("스터디방을 찾을 수 없습니다."));

        return studyRoom.getStudyRecruit().toStudyRecruitSaveRequestDTO(studyRoomId);
    }

    public void increaseViewCount(Integer recruitId) {
        StudyRecruit recruit = studyRecruitRepository.findById(recruitId)
                .orElseThrow(() -> new IllegalArgumentException("모집글을 찾을 수 없습니다."));
        recruit.setViewCount(recruit.getViewCount() + 1);
        studyRecruitRepository.save(recruit);
    }
}
