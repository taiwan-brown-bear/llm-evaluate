package com.taiwan_brown_bear.llm_evaluate.controller;

import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateRequestDTO;
import com.taiwan_brown_bear.llm_evaluate.dto.LlmEvaluateResponseDTO;
import com.taiwan_brown_bear.llm_evaluate.service.llm.LlmModel;
import com.taiwan_brown_bear.llm_evaluate.service.LlmEvaluateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            for(LlmModel answerModel : llmModels)
            {
                final String userProvidedSystemMsg = llmEvaluateRequestDTO.getSystemMessage();
                final String userProvidedUserMsg   = llmEvaluateRequestDTO.getRequest();
                final String answerModelResponse   = answerModel.getResponse(
                        userProvidedSystemMsg,
                        userProvidedUserMsg);
                log.info("answerModel:{}}\t returning \"{}}\".", answerModel, answerModelResponse);
                //System.out.println("answerModel:" + answerModel + "\t returning \"" + answerModelResponse + "\".");

                for(LlmModel evalModel : llmModels){
                    // TODO: use a builder . passing a builder ... dao builder ... ?
                    String evalModelResponse = evalModel.getEvaluationResult(userProvidedUserMsg, answerModelResponse);
                    // TODO: convert to json ...
                    System.out.println("evalModel:" + evalModel + "\t returning \"" + evalModelResponse + "\".");
                }
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
