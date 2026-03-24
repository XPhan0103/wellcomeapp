package com.example.wellcomeapp.service;

import com.example.wellcomeapp.model.Grade;
import com.example.wellcomeapp.model.Subject;
import com.example.wellcomeapp.model.User;
import com.example.wellcomeapp.repository.GradeRepository;
import com.example.wellcomeapp.repository.SubjectRepository;
import com.example.wellcomeapp.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelParserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private GradeRepository gradeRepository;

    public String parseAndSaveGrades(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Grade> gradesToSave = new ArrayList<>();
            List<Subject> allSubjects = subjectRepository.findAll();
            int rowCount = 0;

            for (Row row : sheet) {
                // Better header detection: if the first cell of the first row starts with "HS", it's likely data, not a header
                if (row.getRowNum() == 0) {
                    Cell firstCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    if (firstCell != null && getCellValueAsString(firstCell).toUpperCase().startsWith("MÃ")) {
                        continue; // This looks like a header (e.g. "Mã học sinh")
                    }
                }

                Cell studentCodeCell = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell subjectCell = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell scoreCell = row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell typeCell = row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell semesterCell = row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                Cell weightCell = row.getCell(5, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                if (studentCodeCell == null || subjectCell == null || scoreCell == null) continue;

                String studentCode = getCellValueAsString(studentCodeCell);
                String subjectName = getCellValueAsString(subjectCell);
                
                Double score = parseDouble(scoreCell);
                if (score == null) continue;

                String type = typeCell != null ? getCellValueAsString(typeCell) : "Miệng";
                if (type.isEmpty()) type = "Miệng";
                
                String semester = semesterCell != null ? getCellValueAsString(semesterCell) : "HK1";
                if (semester.isEmpty()) semester = "HK1";
                
                Integer weight = weightCell != null ? parseInteger(weightCell) : 1;

                Optional<User> studentOpt = userRepository.findByStudentCode(studentCode);
                
                // Fuzzy subject matching
                Optional<Subject> subjectOpt = allSubjects.stream()
                        .filter(s -> s.getName().equalsIgnoreCase(subjectName) || 
                                    s.getName().toLowerCase().contains(subjectName.toLowerCase()) ||
                                    subjectName.toLowerCase().contains(s.getName().toLowerCase()))
                        .findFirst();

                if (studentOpt.isPresent() && subjectOpt.isPresent()) {
                    Grade grade = new Grade();
                    grade.setStudent(studentOpt.get());
                    grade.setSubject(subjectOpt.get());
                    grade.setScore(score);
                    grade.setType(type);
                    grade.setSemester(semester);
                    grade.setWeight(weight);
                    
                    gradesToSave.add(grade);
                    rowCount++;
                }
            }

            gradeRepository.saveAll(gradesToSave);
            return "Tải lên thành công: Đã import " + rowCount + " bản ghi điểm vào hệ thống.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi phân tích file Excel: " + e.getMessage();
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        }
        return "";
    }

    private Double parseDouble(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Double.parseDouble(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Integer parseInteger(Cell cell) {
        if (cell == null) return 1;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Integer.parseInt(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {}
        return 1;
    }
}
