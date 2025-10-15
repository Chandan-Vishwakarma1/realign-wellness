package com.realignwellness.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Trainer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerProfile {
    @Id
    private String id;

    @Indexed
    private String fullName;

    @Indexed
    private boolean active;

    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;
}

