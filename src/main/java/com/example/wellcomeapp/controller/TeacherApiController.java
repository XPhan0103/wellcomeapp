package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.service.ExcelParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin(origins = "*")
public class TeacherApiController {

    @Autowired
    private ExcelParserService excelParserService;

    @PostMapping("/upload-grades")
    public ResponseEntity<?> uploadGrades(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("error", "Vui lòng chọn file Excel.");
            return ResponseEntity.badRequest().body(response);
        }
        
        String result = excelParserService.parseAndSaveGrades(file);
        if (result.startsWith("Lỗi")) {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
        
        response.put("message", result);
        return ResponseEntity.ok(response);
    }
}
