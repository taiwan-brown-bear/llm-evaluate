package com.taiwan_brown_bear.llm_evaluate.repository;

import com.taiwan_brown_bear.llm_evaluate.dao.LlmEvaluateResultDAO;
import org.springframework.data.repository.CrudRepository;

public interface LlmEvaluateResultRespository extends CrudRepository<LlmEvaluateResultDAO, Integer> {
}
