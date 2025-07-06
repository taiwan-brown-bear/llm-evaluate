package com.taiwan_brown_bear.llm_evaluate.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "llm_evaluate_requests")
public class LlmEvaluateRequestDAO {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer requestId;

    private String request;

    @Column(length = 1000)
    private String systemMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    //private LocalDateTime createdAt; // Use LocalDateTime for modern Java applications e.g., 2025-06-18 15:45:02.184259
    private Instant createdOn;// e.g., 2025-06-18 22:48:58.381838 <- UTC time. The above is Bay Area time.

}
