package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.entity.Board;
import com.example.kotsuexample.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<Board> createBoard(
            @RequestBody Board board,
            @CurrentUser Integer userId
    ) {
        return ResponseEntity.ok(boardService.saveBoard(board, userId));
    }

    // 게시글 목록 조회: 최신순 or 조회순
//    최신순:
//            /boards?page=0&size=10&sort=createdAt,desc
//
//    조회순:
//            /boards?page=0&size=10&sort=viewCount,desc
    @GetMapping // ex) /boards?page=0&size=10&sort=createdAt,desc or sort=viewCount,desc
    public ResponseEntity<Page<Board>> getBoards(Pageable pageable) {
        return ResponseEntity.ok(boardService.getBoardPage(pageable));
    }

    // 상세 조회 + 조회수 증가
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoard(@PathVariable Integer id) {
        boardService.increaseViewCount(id);
        return boardService.getBoardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(
            @PathVariable Integer id,
            @CurrentUser Integer userId,
            @RequestBody Board board) {
        Board updated = boardService.updateBoard(id, userId, board.getTitle(), board.getContent());
        return ResponseEntity.ok(updated);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Integer id,
            @CurrentUser Integer userId) {
        boardService.deleteBoard(id, userId);
        return ResponseEntity.noContent().build();
    }
}
