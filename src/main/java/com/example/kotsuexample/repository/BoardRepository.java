package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Board;
import com.example.kotsuexample.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    Optional<Board> findByIdAndUser(Integer id, User user);

    Page<Board> findByUserId(Integer userId, Pageable pageable);

    List<Board> findTop4ByOrderByCreatedAtDesc();
}
