package com.taiwan_brown_bear.llm_evaluate.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

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

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getSystemMessage() {
        return systemMessage;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
