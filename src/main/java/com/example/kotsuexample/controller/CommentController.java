package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.CommentDTO;
import com.example.kotsuexample.entity.Comment;
import com.example.kotsuexample.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 저장
    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam Integer userId,
            @RequestParam Integer boardId,
            @RequestBody String content) {
        Comment comment = commentService.createComment(userId, boardId, content);
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Integer commentId,
            @RequestParam Integer userId,
            @RequestBody String content) {
        Comment updated = commentService.updateComment(commentId, userId, content);
        return ResponseEntity.ok(updated);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    // ✅ 댓글 목록 조회 (특정 게시글 기준)
    @GetMapping("/list")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(@RequestParam Integer boardId) {
        List<CommentDTO> comments = commentService.getCommentsByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }
}
