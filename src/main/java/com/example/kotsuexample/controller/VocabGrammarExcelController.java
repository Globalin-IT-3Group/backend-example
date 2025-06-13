package com.example.kotsuexample.controller;

import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.service.VocabGrammarExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocab-grammar")
public class VocabGrammarExcelController {
    private final VocabGrammarExcelService vocabGrammarExcelService;

    @PostMapping("/upload")
    public ResponseEntity<List<VocabGrammar>> uploadExcel(@RequestParam("file") MultipartFile file) {
        List<VocabGrammar> parsedList = vocabGrammarExcelService.parseExcelFile(file);
        return ResponseEntity.ok(parsedList);
    }
}
