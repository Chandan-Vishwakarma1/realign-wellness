package com.realignwellness.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.realignwellness.dto.ApiResponse;
import com.realignwellness.dto.ClassScheduleItemDTO;
import com.realignwellness.dto.CreateClassScheduleRequest;
import com.realignwellness.dto.UpdateClassScheduleRequest;
import com.realignwellness.mapper.ClassScheduleMapper;
import com.realignwellness.service.ClassScheduleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ClassScheduleService service;
    private final ClassScheduleMapper mapper;

    @PreAuthorize("hasAnyRole('USER','TRAINER','ADMIN')")
    @GetMapping
    public ApiResponse<List<ClassScheduleItemDTO>> listByDay(@RequestParam(name = "day", required = false) String day) {
        var list = service.listByDay(day).stream().map(mapper::toItemDTO).toList();
        return ApiResponse.<List<ClassScheduleItemDTO>>builder()
                .success(true).code("OK").message(day == null ? "All active classes" : "Classes for " + day.toUpperCase())
                .timestamp(Instant.now()).data(list).build();
    }

//    @PreAuthorize("hasAnyRole('USER','TEACHER','ADMIN')")
    @GetMapping("/week")
    public ApiResponse<Map<String, List<ClassScheduleItemDTO>>> week() {
        var map = new LinkedHashMap<String, List<ClassScheduleItemDTO>>();
        service.listWeek().forEach((day, entries) -> map.put(day, entries.stream().map(mapper::toItemDTO).toList()));
        return ApiResponse.<Map<String, List<ClassScheduleItemDTO>>>builder()
                .success(true).code("OK").message("Weekly classes")
                .timestamp(Instant.now()).data(map).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ClassScheduleItemDTO>> create(@Valid @RequestBody CreateClassScheduleRequest req) {
        var saved = service.create(req);
        var dto = mapper.toItemDTO(saved);
        var body = ApiResponse.<ClassScheduleItemDTO>builder()
                .success(true).code("CREATED").message("Schedule created")
                .timestamp(Instant.now()).data(dto).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<ClassScheduleItemDTO> update(@PathVariable String id, @Valid @RequestBody UpdateClassScheduleRequest req) {
        var saved = service.update(id, req);
        return ApiResponse.<ClassScheduleItemDTO>builder()
                .success(true).code("UPDATED").message("Schedule updated")
                .timestamp(Instant.now()).data(mapper.toItemDTO(saved)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/toggle")
    public ApiResponse<ClassScheduleItemDTO> toggle(@PathVariable String id) {
        var saved = service.toggle(id);
        return ApiResponse.<ClassScheduleItemDTO>builder()
                .success(true).code("TOGGLED").message("Schedule toggled")
                .timestamp(Instant.now()).data(mapper.toItemDTO(saved)).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        service.delete(id);
        var body = ApiResponse.<Void>builder()
                .success(true).code("DELETED").message("Schedule deleted")
                .timestamp(Instant.now()).data(null).build();
        return ResponseEntity.ok(body);
    }

//    @Pattern(regexp="^([01]\\d|2[0-3]):[0-5]\\d$", message="time must be HH:mm")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/availability")
    public ApiResponse<Map<String,Object>> availability(
            @RequestParam String day,
            @RequestParam String time,
            @RequestParam String instructorId) {

        boolean occupied = service.isOccupied(day, time, instructorId);

        var data = new LinkedHashMap<String,Object>();
        data.put("day", day.toUpperCase());
        data.put("time", time);
        data.put("instructorId", instructorId);
        data.put("available", !occupied);

        String msg = occupied
                ? "Instructor is already occupied for the selected slot."
                : "Instructor is available for the selected slot.";

        return ApiResponse.<Map<String,Object>>builder()
                .success(true).code("OK").message(msg)
                .timestamp(Instant.now()).data(data).build();
    }
}

