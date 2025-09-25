package com.realignwellness.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.realignwellness.entity.ClassSchedule;

public interface ClassScheduleRepository extends MongoRepository<ClassSchedule, String> {
    List<ClassSchedule> findByActiveTrueAndDayOfWeekOrderByStartTimeAsc(String dayOfWeek);
    List<ClassSchedule> findByActiveTrueOrderByDayOfWeekAscStartTimeAsc();
    List<ClassSchedule> findByInstructorId(String instructorId);
    boolean existsByDayOfWeekAndStartTimeAndInstructorIdAndActive(String dayOfWeek, String startTime, String instructorId, boolean active);
}

