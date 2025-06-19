package com.example.kotsuexample.dto.inquiry;

import com.example.kotsuexample.dto.UserResponse;
import com.example.kotsuexample.entity.Inquiry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InquiryDTO {
    private Integer id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private String adminReply;
    private LocalDateTime createdAt;
    private UserResponse user;

    public static InquiryDTO fromEntity(Inquiry inquiry) {
        InquiryDTO dto = new InquiryDTO();
        dto.setId(inquiry.getId());
        dto.setTitle(inquiry.getTitle());
        dto.setContent(inquiry.getContent());
        dto.setIsPrivate(inquiry.getIsPrivate());
        dto.setAdminReply(inquiry.getAdminReply());
        dto.setCreatedAt(inquiry.getCreatedAt());
        dto.setUser(inquiry.getUser().toUserResponse());
        return dto;
    }
}
