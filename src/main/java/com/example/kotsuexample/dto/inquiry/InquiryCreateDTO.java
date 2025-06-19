package com.example.kotsuexample.dto.inquiry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryCreateDTO {
    private String title;
    private String content;
    private Boolean isPrivate;
}
