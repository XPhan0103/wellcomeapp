package com.example.wellcomeapp.controller;

import com.example.wellcomeapp.model.Schedule;
import com.example.wellcomeapp.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/class/{className}")
    public ResponseEntity<List<Schedule>> getSchedulesByClass(@PathVariable String className) {
        List<Schedule> schedules = scheduleRepository.findByClassNameOrderByStartTimeAsc(className);
        return ResponseEntity.ok(schedules);
    }
}
