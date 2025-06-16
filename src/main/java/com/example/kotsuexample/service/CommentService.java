package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.CommentDTO;
import com.example.kotsuexample.entity.Board;
import com.example.kotsuexample.entity.Comment;
import com.example.kotsuexample.entity.User;
import com.example.kotsuexample.exception.NoAuthorizationException;
import com.example.kotsuexample.exception.NotFoundByParamException;
import com.example.kotsuexample.repository.BoardRepository;
import com.example.kotsuexample.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;

    // 댓글 저장
    public Comment createComment(Integer userId, Integer boardId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundByParamException("게시글이 조회되지 않습니다."));
        User user = userService.getUserById(userId);
        Comment comment = new Comment();
        comment.setBoard(board);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(Integer commentId, Integer userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundByParamException("댓글이 조회되지 않습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new NoAuthorizationException("수정할 권한이 없습니다.");
        }
        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundByParamException("댓글이 조회되지 않습니다."));
        if (!comment.getUser().getId().equals(userId)) {
            throw new NoAuthorizationException("삭제할 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }

    public List<CommentDTO> getCommentsByBoardId(Integer boardId) {
        List<Comment> comments = commentRepository.findAllByBoardIdOrderByCreatedAtAsc(boardId);
        return comments.stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
