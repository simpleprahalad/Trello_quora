package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST,
        path = "question/all/{userId}",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@PathVariable("userId") final String userUuid,
        @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionEntity> allQuestionsByUser = questionBusinessService.getAllQuestionsByUser(authorization, userUuid);
        List<QuestionDetailsResponse> allQuestionByUserResponse = new ArrayList<>();

        for (QuestionEntity question: allQuestionsByUser){
            allQuestionByUserResponse.add(new QuestionDetailsResponse().id(question.getUuid())
                .content(question.getContent()));
        }

        return new ResponseEntity<>(allQuestionByUserResponse, HttpStatus.OK);

    }

}
