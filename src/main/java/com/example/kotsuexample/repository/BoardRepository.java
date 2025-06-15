package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Board;
import com.example.kotsuexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Integer> {

    Optional<Board> findByIdAndUser(Integer id, User user);
}
