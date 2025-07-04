package com.example.kotsuexample.entity;

import com.example.kotsuexample.entity.enums.StudyTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "study_rooms")
@Getter
@NoArgsConstructor
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Lob
    private String rule;

    @Lob
    private String notice;

    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader", nullable = false)
    private User leader;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "study_room_tags", joinColumns = @JoinColumn(name = "study_room_id"))
    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private Set<StudyTag> tags = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "max_user_count", nullable = false)
    private Integer maxUserCount;

    @OneToMany(mappedBy = "studyRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyRoomMember> members = new ArrayList<>();

    @OneToOne(mappedBy = "studyRoom", fetch = FetchType.LAZY)
    private StudyRecruit studyRecruit;

    @Builder
    public StudyRoom(String name, String rule, String notice, String imageUrl, User leader, Set<StudyTag> tags, LocalDateTime createdAt, Integer maxUserCount) {
        this.name = name;
        this.rule = rule;
        this.notice = notice;
        this.imageUrl = imageUrl;
        this.leader = leader;
        this.tags = tags;
        this.createdAt = createdAt;
        this.maxUserCount = maxUserCount;
    }

    // 멤버 추가 (양방향 관계를 위한 헬퍼)
    public void addMember(StudyRoomMember member) {
        members.add(member);
        member.setStudyRoom(this);
    }

    // 업데이트용 메서드(필요시)
    public void update(String name, String rule, String notice, String imageUrl, Integer maxUserCount, Set<StudyTag> tags) {
        this.name = name;
        this.rule = rule;
        this.notice = notice;
        this.imageUrl = imageUrl;
        this.maxUserCount = maxUserCount;
        this.tags = tags;
    }
}
