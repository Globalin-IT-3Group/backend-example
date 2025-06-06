package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
}
