package com.realignwellness.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.realignwellness.entity.InstructorProfile;

public interface InstructorRepository extends MongoRepository<InstructorProfile, String> {
    List<InstructorProfile> findByActiveTrueOrderByFullNameAsc();
}
