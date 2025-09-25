package com.realignwellness.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.realignwellness.dto.CreateClassScheduleRequest;
import com.realignwellness.dto.UpdateClassScheduleRequest;
import com.realignwellness.entity.ClassSchedule;
import com.realignwellness.exception.BadRequestException;
import com.realignwellness.exception.DuplicateResourceException;
import com.realignwellness.exception.NotFoundException;
import com.realignwellness.repository.ClassScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassScheduleService {
    private final ClassScheduleRepository scheduleRepo;
    private final InstructorService instructorService;

    private static final Set<String> DAYS = Set.of(
            "MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY"
    );

    public List<ClassSchedule> listByDay(String day) {
        if (day == null || day.isBlank()) {
            return scheduleRepo.findByActiveTrueOrderByDayOfWeekAscStartTimeAsc();
        }
        String norm = normalizeDay(day);
        return scheduleRepo.findByActiveTrueAndDayOfWeekOrderByStartTimeAsc(norm);
    }

    public Map<String, List<ClassSchedule>> listWeek() {
        var all = scheduleRepo.findByActiveTrueOrderByDayOfWeekAscStartTimeAsc();
        var map = new LinkedHashMap<String, List<ClassSchedule>>();
        DAYS.forEach(d -> map.put(d, new ArrayList<>()));
        for (var cs : all) {
            map.computeIfAbsent(cs.getDayOfWeek(), k -> new ArrayList<>()).add(cs);
        }
        return map;
    }

    public ClassSchedule create(CreateClassScheduleRequest req) {
        String day = normalizeDay(req.getDay());
        var instr = instructorService.getActiveOrThrow(req.getInstructorId());

        if (Boolean.TRUE.equals(req.getActive())) {
            boolean dup = scheduleRepo.existsByDayOfWeekAndStartTimeAndInstructorIdAndActive(day, req.getStartTime(), instr.getId(), true);
            if (dup) throw new DuplicateResourceException("Duplicate active slot for instructor at day+time");
        }

        var now = Instant.now();
        var entity = ClassSchedule.builder()
                .dayOfWeek(day)
                .startTime(req.getStartTime())
                .endTime(req.getEndTime())
                .className(req.getClassName())
                .instructorId(instr.getId())
                .instructorName(instr.getFullName())
                .joinUrl(req.getJoinUrl())
                .active(req.getActive() == null ? true : req.getActive())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return scheduleRepo.save(entity);
    }

    public ClassSchedule update(String id, UpdateClassScheduleRequest req) {
        var cs = scheduleRepo.findById(id).orElseThrow(() -> new NotFoundException("ClassSchedule not found"));

        if (req.getDay() != null && !req.getDay().isBlank()) cs.setDayOfWeek(normalizeDay(req.getDay()));
        if (req.getStartTime() != null) cs.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) cs.setEndTime(req.getEndTime());
        if (req.getClassName() != null) cs.setClassName(req.getClassName());
        if (req.getJoinUrl() != null) cs.setJoinUrl(req.getJoinUrl());
        if (req.getActive() != null) cs.setActive(req.getActive());

        if (req.getInstructorId() != null && !req.getInstructorId().isBlank()) {
            var instr = instructorService.getActiveOrThrow(req.getInstructorId());
            cs.setInstructorId(instr.getId());
            cs.setInstructorName(instr.getFullName());
        }

        cs.setUpdatedAt(Instant.now());
        return scheduleRepo.save(cs);
    }

    public ClassSchedule toggle(String id) {
        var cs = scheduleRepo.findById(id).orElseThrow(() -> new NotFoundException("ClassSchedule not found"));
        cs.setActive(!cs.isActive());
        cs.setUpdatedAt(Instant.now());
        return scheduleRepo.save(cs);
    }

    public void delete(String id) {
        scheduleRepo.deleteById(id);
    }

    private String normalizeDay(String input) {
        String d = input.trim().toUpperCase();
        if (!DAYS.contains(d)) throw new BadRequestException("Invalid day: " + input);
        return d;
    }
}
