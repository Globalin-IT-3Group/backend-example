package com.example.kotsuexample.repository;

import com.globalin.kotsukotsu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
