package com.example.kotsuexample.controller;

import com.example.kotsuexample.config.CurrentUser;
import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import com.example.kotsuexample.service.UserService;
import com.example.kotsuexample.service.VocabService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocab-grammar")
public class VocabController {

    private final VocabService vocabService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<VocabGrammar>> getVocabGrammars(
//            @CurrentUser Integer userId,
            @RequestParam EntryType entryType,
            @RequestParam Level level,
            @RequestParam ExamType examType,
            @PageableDefault Pageable pageable) {

//        userService.validExistUser(userId);

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
}
