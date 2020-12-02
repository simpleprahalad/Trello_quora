package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, value = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String question_uuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        //String accessToken = authorization.split("Bearer ")[1];
        AnswerEntity newAnswer = answerBusinessService.createAnswer(answerRequest.getAnswer(), question_uuid, authorization);
        AnswerResponse answerResponse = new AnswerResponse().id(newAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);

    }
}
