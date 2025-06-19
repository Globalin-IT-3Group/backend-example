package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    Page<Inquiry> findByUserId(Integer userId, Pageable pageable);
}
