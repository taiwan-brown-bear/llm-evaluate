package com.taiwan_brown_bear.llm_evaluate.controller;

import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateRequestDTO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResponseDTO;
import com.taiwan_brown_bear.llm_evaluate.service.llm.LlmModel;
import com.taiwan_brown_bear.llm_evaluate.service.LlmEvaluateService;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/llm/evaluate")
public class LlmEvaluateController {

    @Autowired
    private LlmEvaluateService llmEvaluateService;

    @Autowired
    private List<LlmModel> llmModels;

    @PostMapping
    public ResponseEntity<LlmEvaluateResponseDTO> evaluate(@RequestBody LlmEvaluateRequestDTO llmEvaluateRequestDTO) {
        LlmEvaluateResponseDTO llmEvaluateResponseDTO = llmEvaluateService.save(llmEvaluateRequestDTO);

        final Integer requestId = llmEvaluateResponseDTO.getRequestId();
        if(requestId != null){
            for(LlmModel targetModel : llmModels)
            {
                final String userProvidedSystemMsg = llmEvaluateRequestDTO.getSystemMessage();
                final String userProvidedUserMsg   = llmEvaluateRequestDTO.getRequest();
                final LlmEvaluateResultDTO targetModelResponse   = targetModel.getResponse(userProvidedSystemMsg, userProvidedUserMsg);
                log.info("answerModel:{}\t returning \"{}\".", targetModel, targetModelResponse.evaluationResult());

                List<LlmEvaluateResultDTO> evaluationResutls = new ArrayList<>();
                for(LlmModel evalModel : llmModels){
                    try {
                        LlmEvaluateResultDTO llmEvaluateResult = evalModel.getEvaluationResult(userProvidedUserMsg, targetModelResponse.evaluationResult());
                        log.info("evaluationModel:{}\t returning \"{}\".", evalModel, llmEvaluateResult.evaluationResult());
                        evaluationResutls.add(llmEvaluateResult);
                    } catch (NonTransientAiException nonTransientAiException) {
                        log.warn("failed to evaluate the llm evaluatedBy response due to {}",
                                nonTransientAiException.getMessage(), nonTransientAiException);
                        evaluationResutls.add(new LlmEvaluateResultDTO(
                                targetModelResponse.evaluatedBy(),
                                targetModelResponse.evaluationResult(),
                                nonTransientAiException.getMessage()));
                    }
                }
                llmEvaluateResponseDTO = llmEvaluateResponseDTO.toBuilder()
                        .targetModelEvaluationResults(evaluationResutls)
                        .build();
            }
        }

        return ResponseEntity.ok(llmEvaluateResponseDTO);
    }

    @GetMapping
    public ResponseEntity<LlmEvaluateResponseDTO> get(@RequestBody LlmEvaluateRequestDTO llmEvaluateRequestDTO) {
        LlmEvaluateResponseDTO llmEvaluateResponseDTO =  llmEvaluateService.load(llmEvaluateRequestDTO);
        return ResponseEntity.ok(llmEvaluateResponseDTO);
    }
}
