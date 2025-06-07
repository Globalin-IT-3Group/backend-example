package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.FollowUserRequest;
import com.example.kotsuexample.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
public class FriendController {

    private final FriendService friendService;

//    @PostMapping("/request")
//    public ResponseEntity<?> followUser(@RequestBody FollowUserRequest followUserRequest) {
//// TODO: 2025-06-06 friend 처음 만들고 있던 중
//    }
}
