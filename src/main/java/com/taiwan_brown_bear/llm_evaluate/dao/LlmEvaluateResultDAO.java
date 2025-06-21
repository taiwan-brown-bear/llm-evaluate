package com.taiwan_brown_bear.llm_evaluate.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Entity // This tells Hibernate to make a table out of this class
@Table(name = "llm_evaluate_results")
public class LlmEvaluateResultDAO
{
    private Integer      requestId;
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer      resultId;
//    private String       request;//                (1/6)
//    @Column(length = 1000)
//    private String       systemMessage;//          (2/6)
    private String       targetModel;
//    @Column(length = 10000)
//    private String       targetModelResponse;//    (3/6)
    private String       evaluatedBy;
//    @Column(length = 10000)
//    private String       guidelineForEvaluation;// (4/6)
//    private String       evaluationResult;//       (5/6)
    private Boolean      isValid;
//    private List<String> issues;//                 (6/6)
    private Double       confidence;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant      createdOn;

}
