package com.example.kotsuexample.service;

import com.example.kotsuexample.entity.VocabGrammar;
import com.example.kotsuexample.entity.enums.EntryType;
import com.example.kotsuexample.entity.enums.ExamType;
import com.example.kotsuexample.entity.enums.Level;
import com.example.kotsuexample.repository.VocabGrammarRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VocabGrammarExcelService {

    private final VocabGrammarRepository vocabGrammarRepository;

    public List<VocabGrammar> parseExcelFile(MultipartFile file) {
        List<VocabGrammar> list = new ArrayList<>();

        EntryType type = null;
        Level level = null;
        ExamType examType = null;

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // i=1: 메타데이터 파싱
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                if (i == 1) {
                    String typeStr = getStringValue(row.getCell(0));
                    String levelStr = getStringValue(row.getCell(1));
                    String examTypeStr = getStringValue(row.getCell(2));
                    type = EntryType.from(typeStr);
                    level = Level.from(levelStr);
                    examType = ExamType.from(examTypeStr);
                    continue;
                }

                VocabGrammar entity;
                if (type == EntryType.WORD) {
                    entity = parseWordRow(row, type, level, examType);
                } else if (type == EntryType.GRAMMAR) {
                    entity = parseGrammarRow(row, type, level, examType); // 직접 로직 구현
                } else {
                    continue; // 기타 타입 무시
                }

                if (entity != null) {
                    list.add(entity);
                }
            }

            vocabGrammarRepository.saveAll(list);

        } catch (Exception e) {
            throw new RuntimeException(("실패 ㅜㅜ: " + e.getMessage()), e);
        }

        return list;
    }

    private VocabGrammar parseWordRow(Row row, EntryType type, Level level, ExamType examType) {
        VocabGrammar entity = new VocabGrammar();
        entity.setType(type);
        entity.setLevel(level);
        entity.setExamType(examType);

        entity.setJpWord(getStringValue(row.getCell(0)));
        entity.setHiragana(getStringValue(row.getCell(1)));
        entity.setAltForm(getStringValue(row.getCell(2)));
        entity.setPos(getStringValue(row.getCell(3)));
        entity.setMeaning(getStringValue(row.getCell(4)));

        String example = getStringValue(row.getCell(5));
        String synonym = null;
        if (example != null && example.contains("동의어")) {
            String[] arr = example.split("동의어", 2);
            example = arr[0].trim();
            synonym = arr.length > 1 ? arr[1].trim() : null;
        }
        entity.setExample(example);
        entity.setSynonym(synonym);
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    // GRAMMAR용 틀만 제공
    private VocabGrammar parseGrammarRow(Row row, EntryType type, Level level, ExamType examType) {
        VocabGrammar entity = new VocabGrammar();
        entity.setType(type);
        entity.setLevel(level);
        entity.setExamType(examType);

         entity.setJpWord(getStringValue(row.getCell(0)));
         entity.setMeaning(getStringValue(row.getCell(1)));
         entity.setExample(getStringValue(row.getCell(2)) + "   " + getStringValue(row.getCell(4)));

        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }


    // 셀의 값을 안전하게 문자열로 변환하는 메서드 (널 처리 포함)
    private String getStringValue(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
