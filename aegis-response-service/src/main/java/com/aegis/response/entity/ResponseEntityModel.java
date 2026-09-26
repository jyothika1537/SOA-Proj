package com.aegis.response.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseEntityModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "response_id")
    private String responseId;

    @Column(nullable = false)
    private String incidentId;

    @Column(nullable = false)
    private String resourceId;

    @Column(nullable = false)
    private String responderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseStatus responseStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponsePriority priority;

    private LocalDateTime dispatchedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
