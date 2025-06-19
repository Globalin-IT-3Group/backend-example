package com.example.kotsuexample.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "admin_reply")
    private String adminReply;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    public static Inquiry createNewInquiryForSave(User user, String title, String content, Boolean isPrivate, LocalDateTime createdAt) {
        Inquiry inquiry = new Inquiry();
        inquiry.user = user;
        inquiry.title = title;
        inquiry.content = content;
        inquiry.isPrivate = isPrivate;
        inquiry.createdAt = createdAt;
        return inquiry;
    }

    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }
}
