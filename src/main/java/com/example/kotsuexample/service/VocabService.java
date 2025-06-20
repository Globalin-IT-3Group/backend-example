package com.example.kotsuexample.service;

import com.example.kotsuexample.dto.SimpleVocabDTO;
import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import com.example.kotsuexample.exception.NoneInputValueException;
import com.example.kotsuexample.repository.VocabGrammarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabService {

    private final VocabGrammarRepository vocabGrammarRepository;

    public Page<VocabGrammar> getVocabGrammarsByParams(
            EntryType entryType,
            Level level,
            ExamType examType,
            Pageable pageable
    ) {
        return vocabGrammarRepository.findByEntryTypeAndLevelAndExamType(
                entryType, level, examType, pageable
        );
    }

    @Transactional
    public void updateExample(Integer id, String example) {
        VocabGrammar vg = vocabGrammarRepository.findById(id)
                .orElseThrow(() -> new NoneInputValueException("아이디 값에 따른 단어가 조회되지 않습니다."));
        vg.setExample(example);
        // JPA는 트랜잭션 내에서 변경 감지하여 자동 업데이트
    }

    public List<SimpleVocabDTO> getRandomSimpleVocab9() {
        List<VocabGrammar> vocabList = vocabGrammarRepository.findRandomNine();
        return vocabList.stream()
                .map(v -> SimpleVocabDTO.builder()
                        .entryType(v.getEntryType())
                        .level(v.getLevel())
                        .examType(v.getExamType())
                        .jpWord(v.getJpWord())
                        .hiragana(v.getHiragana())
                        .altForm(v.getAltForm())
                        .pos(v.getPos())
                        .meaning(v.getMeaning())
                        .build())
                .collect(Collectors.toList());
    }
}
