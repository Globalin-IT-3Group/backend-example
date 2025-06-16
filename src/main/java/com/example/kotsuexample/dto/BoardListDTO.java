package com.example.kotsuexample.dto;

import com.example.kotsuexample.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardListDTO {
    private Integer id;
    private String title;
    private String content;
    private Integer viewCount;
    private User user;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
