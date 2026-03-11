package com.example.wellcomeapp.repository;

import com.example.wellcomeapp.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByClassNameOrderByStartTimeAsc(String className);
    List<Schedule> findByClassNameAndDayOfWeekOrderByStartTimeAsc(String className, Integer dayOfWeek);
}
