package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final UserService userService;

    @PutMapping("/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestBody Map<String, String> request,
            @CurrentUser Integer userId
    ) {
        String profileImage = request.get("image");
        String savedBase64 = userService.updateProfileImage(userId, profileImage);
        Map<String, String> res = new HashMap<>();
        res.put("image", savedBase64);
        return ResponseEntity.ok(res);
    }
}
