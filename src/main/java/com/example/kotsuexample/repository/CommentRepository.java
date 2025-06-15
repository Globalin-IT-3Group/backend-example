package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
