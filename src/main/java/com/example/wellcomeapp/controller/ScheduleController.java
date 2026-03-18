package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Schedule;
import com.example.wellcomeapp.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/class/{className}")
    public ResponseEntity<?> getSchedulesByClass(@PathVariable String className) {
        List<Schedule> schedules = scheduleRepository.findByClassNameOrderByStartTimeAsc(className);

        List<Map<String, Object>> result = schedules.stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",          s.getId());
            m.put("dayOfWeek",   s.getDayOfWeek());
            m.put("subjectName", s.getSubject() != null ? s.getSubject().getName() : "");
            m.put("room",        s.getRoom() != null ? s.getRoom() : "");
            m.put("startTime",   s.getStartTime() != null ? s.getStartTime().toString() : "");
            m.put("endTime",     s.getEndTime()   != null ? s.getEndTime().toString()   : "");
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

