package com.realignwellness.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.realignwellness.entity.ClassSchedule;

public interface ClassScheduleRepository extends MongoRepository<ClassSchedule, String> {
    List<ClassSchedule> findByActiveTrueAndDayOfWeekOrderByTimeAsc(String dayOfWeek);
    List<ClassSchedule> findByActiveTrueOrderByDayOfWeekAscTimeAsc();
    List<ClassSchedule> findByInstructorId(String instructorId);
    boolean existsByDayOfWeekAndTimeAndInstructorIdAndActive(String dayOfWeek, String startTime, String instructorId, boolean active);
}
