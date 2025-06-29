package com.example.kotsuexample.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatRoomRequest {
    private Integer studyRoomId;
    private List<Integer> memberIds;
}
