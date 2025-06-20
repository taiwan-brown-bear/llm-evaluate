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
    public ResponseEntity<LlmEvaluateResponseDTO> evaluate(@RequestBody LlmEvaluateRequestDTO llmEvaluateRequestDTO)
    {
        LlmEvaluateResponseDTO llmEvaluateResponseDTO = llmEvaluateService.save(llmEvaluateRequestDTO);
        final Integer requestId = llmEvaluateResponseDTO.getRequestId();

        List<LlmEvaluateResultDTO> evaluationResutls = new ArrayList<>();
        if(requestId != null)// if saved successfully, we should have id !!!
        {
            for(LlmModel targetModel : llmModels)// going to ask each model ...
            {
                final String userProvidedSystemMsg = llmEvaluateRequestDTO.getSystemMessage();
                final String userProvidedUserMsg   = llmEvaluateRequestDTO.getRequest();

                final LlmEvaluateResultDTO targetModelResponse = targetModel.getResponse(userProvidedSystemMsg, userProvidedUserMsg);
                log.debug("targetModel:{}\t returning \"{}\".", targetModelResponse.getTargetModel(), targetModelResponse.getTargetModelResponse());

                for(LlmModel evaluationModel : llmModels)// going to ask each model to evaluate the target response
                {
//                    try
//                    {
                        LlmEvaluateResultDTO llmEvaluateResult = evaluationModel.getEvaluationResult(userProvidedUserMsg, targetModelResponse.getTargetModelResponse());
                        log.info("{} is evaluated by {} and the evaluation result is {}", targetModelResponse.getTargetModel(), llmEvaluateResult.getEvaluatedBy(), llmEvaluateResult.getEvaluationResult());

                        evaluationResutls.add(
                                llmEvaluateResult.toBuilder()
                                .targetModel        (targetModelResponse.getTargetModel())
                                .targetModelResponse(targetModelResponse.getTargetModelResponse())
                                .build()
                        );

//                    } catch (NonTransientAiException nonTransientAiException) {
//                        log.warn("failed to evaluate the llm evaluatedBy response due to {}",
//                                nonTransientAiException.getMessage(), nonTransientAiException);
//                        evaluationResutls.add(
//                                LlmEvaluateResultDTO.builder()
//                                .targetModel        (targetModelResponse.getEvaluatedBy())
//                                .targetModelResponse(targetModelResponse.getEvaluationResult())
//                                .issues             (List.of(nonTransientAiException.getMessage()))
//                                .build()
//                        );
//                    }
                }
                llmEvaluateResponseDTO = llmEvaluateResponseDTO.toBuilder()
                        .targetModelEvaluationResults(evaluationResutls)
                        .build();
            }
        }

        log.info("evaluation result size is " + evaluationResutls.size());
        return ResponseEntity.ok(llmEvaluateResponseDTO);
    }

    @GetMapping
    public ResponseEntity<LlmEvaluateResponseDTO> get(@RequestBody LlmEvaluateRequestDTO llmEvaluateRequestDTO) {
        LlmEvaluateResponseDTO llmEvaluateResponseDTO =  llmEvaluateService.load(llmEvaluateRequestDTO);
        return ResponseEntity.ok(llmEvaluateResponseDTO);
    }
}
