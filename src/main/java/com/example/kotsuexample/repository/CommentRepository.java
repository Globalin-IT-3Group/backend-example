package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByBoardIdOrderByCreatedAtAsc(Integer boardId);
}
