package com.realignwellness.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.realignwellness.entity.TrainerProfile;

public interface InstructorRepository extends MongoRepository<TrainerProfile, String> {
    List<TrainerProfile> findByActiveTrueOrderByFullNameAsc();
}
