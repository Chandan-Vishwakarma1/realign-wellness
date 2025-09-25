package com.realignwellness.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.realignwellness.entity.InstructorProfile;
import com.realignwellness.exception.NotFoundException;
import com.realignwellness.repository.InstructorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstructorService {
    private final InstructorRepository repo;

    public List<InstructorProfile> listActive() {
        return repo.findByActiveTrueOrderByFullNameAsc();
    }

    public InstructorProfile getActiveOrThrow(String id) {
        return repo.findById(id)
                .filter(InstructorProfile::isActive)
                .orElseThrow(() -> new NotFoundException("Instructor not found or inactive"));
    }

    public InstructorProfile create(String fullName, boolean active) {
        var now = Instant.now();
        var p = InstructorProfile.builder()
                .fullName(fullName)
                .active(active)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return repo.save(p);
    }

    public InstructorProfile update(String id, String fullName, Boolean active) {
        var p = repo.findById(id).orElseThrow(() -> new NotFoundException("Instructor not found"));
        if (fullName != null && !fullName.isBlank()) p.setFullName(fullName);
        if (active != null) p.setActive(active);
        p.setUpdatedAt(Instant.now());
        return repo.save(p);
    }

    public void toggle(String id) {
        var p = repo.findById(id).orElseThrow(() -> new NotFoundException("Instructor not found"));
        p.setActive(!p.isActive());
        p.setUpdatedAt(Instant.now());
        repo.save(p);
    }
}

