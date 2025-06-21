package com.taiwan_brown_bear.llm_evaluate.service;

import com.taiwan_brown_bear.llm_evaluate.dao.LlmEvaluateRequestDAO;
import com.taiwan_brown_bear.llm_evaluate.dao.LlmEvaluateResultDAO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateRequestDTO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResponseDTO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResultDTO;
import com.taiwan_brown_bear.llm_evaluate.repository.LlmEvaluateRequestRespository;
import com.taiwan_brown_bear.llm_evaluate.repository.LlmEvaluateResultRespository;
import com.taiwan_brown_bear.llm_evaluate.service.llm.LlmModel;
import com.taiwan_brown_bear.llm_evaluate.service.template.impl.LlmPromptGuidelineTemplateForModelEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LlmEvaluateService {

    @Autowired
    private List<LlmModel> llmModels;

    @Autowired
    private LlmEvaluateRequestRespository llmEvaluateRequestRespository;

    @Autowired
    private LlmEvaluateResultRespository llmEvaluateResultRespository;

    public LlmEvaluateResponseDTO save(LlmEvaluateRequestDTO llmEvaluateRequestDTO) {
        // convert dto -> dao
        LlmEvaluateRequestDAO llmEvaluateRequestDAO = LlmEvaluateRequestDAO.builder()
                .request(llmEvaluateRequestDTO.getRequest())
                .systemMessage(llmEvaluateRequestDTO.getSystemMessage())
                .build();

        // save dao
        LlmEvaluateRequestDAO savedRequest = llmEvaluateRequestRespository.save(llmEvaluateRequestDAO);

        // convert dao -> dto
        return LlmEvaluateResponseDTO.builder().requestId(savedRequest.getRequestId()).request(savedRequest.getRequest()).systemMessage(savedRequest.getSystemMessage()).build();
    }

    public LlmEvaluateResponseDTO load(LlmEvaluateRequestDTO llmEvaluateRequestDTO) {

        Integer requestId = llmEvaluateRequestDTO.getRequestId();

        if (requestId != null) {
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

    public LlmEvaluateResponseDTO evaluate(LlmEvaluateRequestDTO llmEvaluateRequestDTO, LlmEvaluateResponseDTO llmEvaluateResponseDTO) {
        List<LlmEvaluateResultDTO> evaluationResutls = new ArrayList<>();// goal is to get the evaluation results !!!
        for (LlmModel targetModel : llmModels)// going to ask each model ...
        {
            final String userProvidedSystemMsg = llmEvaluateRequestDTO.getSystemMessage();
            final String userProvidedUserMsg = llmEvaluateRequestDTO.getRequest();
            final LlmEvaluateResultDTO targetModelResponse = targetModel.getResponse(userProvidedSystemMsg, userProvidedUserMsg);
            log.debug("targetModel:{}\t returning \"{}\".", targetModelResponse.getTargetModel(), targetModelResponse.getTargetModelResponse());

            for (LlmModel evaluationModel : llmModels)// going to ask each model to evaluate the target response
            {
                LlmEvaluateResultDTO llmEvaluateResult = evaluationModel.getResult(new LlmPromptGuidelineTemplateForModelEvaluation(), userProvidedUserMsg, targetModelResponse.getTargetModelResponse());
                log.info("{} is evaluated by {} and the evaluation result is {}", targetModelResponse.getTargetModel(), llmEvaluateResult.getEvaluatedBy(), llmEvaluateResult.getEvaluationResult());

                llmEvaluateResult = llmEvaluateResult.toBuilder()
                        .targetModel(targetModelResponse.getTargetModel())
                        .targetModelResponse(targetModelResponse.getTargetModelResponse())
                        .build();

                evaluationResutls.add(llmEvaluateResult);
                save(llmEvaluateResponseDTO, llmEvaluateResult);
            }
        }
        return llmEvaluateResponseDTO.toBuilder().targetModelEvaluationResults(evaluationResutls).build();
    }

    public void save(LlmEvaluateResponseDTO llmEvaluateResponseDTO, LlmEvaluateResultDTO llmEvaluateResultDTO) {
        // convert dto -> dao
        LlmEvaluateResultDAO llmEvaluateResultDAO = LlmEvaluateResultDAO.builder()
                .requestId             (llmEvaluateResponseDTO.getRequestId()           )
                //.request               (llmEvaluateResponseDTO.getRequest()             )
                //.systemMessage         (llmEvaluateResponseDTO.getSystemMessage()       )
                .targetModel           (llmEvaluateResultDTO.getTargetModel()           )
                //.targetModelResponse   (llmEvaluateResultDTO.getTargetModelResponse()   )
                .evaluatedBy           (llmEvaluateResultDTO.getEvaluatedBy()           )
                //.guidelineForEvaluation(llmEvaluateResultDTO.getGuidelineForEvaluation())
                //.evaluationResult      (llmEvaluateResultDTO.getEvaluationResult()      )
                .isValid               (llmEvaluateResultDTO.getIsValid()               )
                //.issues                (llmEvaluateResultDTO.getIssues()                )
                .confidence            (llmEvaluateResultDTO.getConfidence()            )
                .build();;

        // save dao
        LlmEvaluateResultDAO savedResult = llmEvaluateResultRespository.save(llmEvaluateResultDAO);
    }
}
