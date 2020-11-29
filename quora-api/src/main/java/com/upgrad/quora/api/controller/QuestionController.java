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
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.GET,
        path = "/question/all",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException{
        List<QuestionEntity> allQuestions = questionBusinessService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> allQuestionResponse = new ArrayList<>();

        for (QuestionEntity question: allQuestions){
            allQuestionResponse.add(new QuestionDetailsResponse().id(question.getUuid())
                .content(question.getContent()));
        }

        return new ResponseEntity<>(allQuestionResponse, HttpStatus.OK);
    }

}
