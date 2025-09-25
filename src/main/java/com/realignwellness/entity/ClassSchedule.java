package com.realignwellness.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("class_schedule")
@CompoundIndexes({
        @CompoundIndex(name = "day_time_idx", def = "{ 'dayOfWeek': 1, 'startTime': 1 }"),
        @CompoundIndex(name = "active_day_idx", def = "{ 'active': 1, 'dayOfWeek': 1 }")
})
public class ClassSchedule {

    @Id
    private String id;

    // Store normalized uppercase: MONDAY..SUNDAY
    @Indexed
    private String dayOfWeek;

    // Store as "HH:mm" for sortability and simplicity
    @Indexed
    private String startTime;

    private String endTime;

    @Indexed
    private boolean active;

    private String className;

    @Indexed
    private String instructorId;
    private String instructorName;

    private String joinUrl;

    private Instant createdAt;
    private Instant updatedAt;
}
