package com.example.kotsuexample.controller;

import com.example.kotsuexample.dto.SimpleVocabDTO;
import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import com.example.kotsuexample.service.VocabService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocab-grammar")
public class VocabController {

    private final VocabService vocabService;

    @GetMapping
    public ResponseEntity<Page<VocabGrammar>> getVocabGrammars(
            @RequestParam EntryType entryType,
            @RequestParam Level level,
            @RequestParam ExamType examType,
            @PageableDefault Pageable pageable) {
        Page<VocabGrammar> page = vocabService.getVocabGrammarsByParams(entryType, level, examType, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}/example")
    public ResponseEntity<Void> updateExample(
            @PathVariable Integer id,
            @RequestBody ExampleUpdateRequest request) {
        vocabService.updateExample(id, request.getExample());
        return ResponseEntity.ok().build();
    }

    public static class ExampleUpdateRequest {
        private String example;
        public String getExample() { return example; }
        public void setExample(String example) { this.example = example; }
    }

    // 랜덤 9개 조회 API
    @GetMapping("/random9")
    public ResponseEntity<List<SimpleVocabDTO>> getRandomVocab9() {
        return ResponseEntity.ok(vocabService.getRandomSimpleVocab9());
    }
}
