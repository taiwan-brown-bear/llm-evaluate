package com.taiwan_brown_bear.llm_evaluate.repository;

import com.taiwan_brown_bear.llm_evaluate.dao.LlmEvaluateRequestDAO;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called llmEvaluateRequestRepository
// CRUD refers Create, Read, Update, Delete

public interface LlmEvaluateRequestRespository extends CrudRepository<LlmEvaluateRequestDAO, Integer> {
}
