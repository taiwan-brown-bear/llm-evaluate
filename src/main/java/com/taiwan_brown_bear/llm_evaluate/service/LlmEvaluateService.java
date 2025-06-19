package com.taiwan_brown_bear.llm_evaluate.service;

import com.taiwan_brown_bear.llm_evaluate.dao.LlmEvaluateRequestDAO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateRequestDTO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResponseDTO;
import com.taiwan_brown_bear.llm_evaluate.repository.LlmEvaluateRequestRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LlmEvaluateService {

    @Autowired
    private LlmEvaluateRequestRespository llmEvaluateRequestRespository;

    public LlmEvaluateResponseDTO save(LlmEvaluateRequestDTO llmEvaluateRequestDTO){

        // convert dto -> dao
        LlmEvaluateRequestDAO llmEvaluateRequestDAO = new LlmEvaluateRequestDAO();
        llmEvaluateRequestDAO.setRequest(llmEvaluateRequestDTO.getRequest());
        llmEvaluateRequestDAO.setSystemMessage(llmEvaluateRequestDTO.getSystemMessage());

        // save dao
        LlmEvaluateRequestDAO savedRequest = llmEvaluateRequestRespository.save(llmEvaluateRequestDAO);

        // convert dao -> dto
        return LlmEvaluateResponseDTO.builder()
                .requestId(savedRequest.getRequestId())
                .request(savedRequest.getRequest())
                .systemMessage(savedRequest.getSystemMessage())
                .build();
    }

    public LlmEvaluateResponseDTO load(LlmEvaluateRequestDTO llmEvaluateRequestDTO){

        Integer requestId = llmEvaluateRequestDTO.getRequestId();

        if(requestId != null) {
            Optional<LlmEvaluateRequestDAO> loadedRequest = llmEvaluateRequestRespository.findById(requestId);
            if (loadedRequest.isPresent()) {
                return LlmEvaluateResponseDTO.builder()
                        .requestId(loadedRequest.get().getRequestId())
                        .request(loadedRequest.get().getRequest())
                        .systemMessage(loadedRequest.get().getSystemMessage())
                        .build();
            }
        }

        return null;
    }
}
