package com.example.kotsuexample.repository;

import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabGrammarRepository extends JpaRepository<VocabGrammar, Integer> {
    Page<VocabGrammar> findByEntryTypeAndLevelAndExamType(
            EntryType entryType,
            Level level,
            ExamType examType,
            Pageable pageable
    );
}
