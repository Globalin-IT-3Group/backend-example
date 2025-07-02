package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {
    private final UserService userService;

    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @CurrentUser Integer userId
    ) {
        String imageUrl = userService.uploadProfileImageToS3(userId, file);
        Map<String, String> res = new HashMap<>();
        res.put("imageUrl", imageUrl);
        System.out.println("imageUrl = " + imageUrl);
        return ResponseEntity.ok(res);
    }
}
