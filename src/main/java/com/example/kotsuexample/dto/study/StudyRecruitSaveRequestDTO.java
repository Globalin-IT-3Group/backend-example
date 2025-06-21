package com.example.kotsuexample.dto.study;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class StudyRecruitSaveRequestDTO {
    private Integer id;
    private Integer studyRoomId; // StudyRoom과 1:1이므로, 해당 ID 필요
    private String title;
    private String studyExplain;
    private Boolean isOpen; // 모집글 공개 여부(종료: false)

    @Builder
    public StudyRecruitSaveRequestDTO(Integer id, Integer studyRoomId, String title, String studyExplain, Boolean isOpen) {
        this.id = id;
        this.studyRoomId = studyRoomId;
        this.title = title;
        this.studyExplain = studyExplain;
        this.isOpen = isOpen;
    }
}
