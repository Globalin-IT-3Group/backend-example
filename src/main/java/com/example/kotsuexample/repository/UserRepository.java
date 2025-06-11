package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumberAndQuestionAndAnswer(String phoneNumber, String question, String answer);
    Optional<User> findByEmailAndQuestionAndAnswer(String email, String question, String answer);
}
