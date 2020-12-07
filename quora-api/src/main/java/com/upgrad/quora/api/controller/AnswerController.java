package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

    /**
     * This class handles all the endpoints pertaining to AnswerEntity (answer table) in database.
     * This class uses the AnswerBusinessService class which is autowired and thus instantiated
     * by Spring Framework for us.
     */

    @Autowired
    AnswerBusinessService answerBusinessService;

    /**
     * @param answerRequest
     * @param question_uuid
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.POST,
            value = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String question_uuid,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        //Answer business service's createAnswer method is called to run the business logic of
        // creating answer in database. It passes following as parameters:
        //1. Question UUID (input as path variables)
        //2. Content for the respective answer
        //3. User Auth token of the signed in user
        AnswerEntity newAnswer = answerBusinessService.createAnswer(answerRequest.getAnswer(), question_uuid, authorization);

        //Appropriate response model (generated by swagger 2.0) is configured and returned as JSON.
        AnswerResponse answerResponse = new AnswerResponse().id(newAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * @param answerEditRequest
     * @param answer_uuid
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.PUT,
            value = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answer_uuid,
                                                                @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        //Answer business service's editAnswer method is called to run the business logic of
        // editing answer in database. It passes following as parameters:
        //1. Answer UUID (input as path variables)
        //2. New Content for the respective answer
        //3. User Auth token of the signed in user
        AnswerEntity updatedAnswerEntity = answerBusinessService.editAnswer(answer_uuid, answerEditRequest.getContent(), authorization);

        //Appropriate response model (generated by swagger 2.0) is configured and returned as JSON.
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * @param answer_uuid
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws AnswerNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE,
            value = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answer_uuid,
                                                             @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        //Answer business service's deleteAnswer method is called to run the business logic of
        // delete answer in database. It passes following as parameters:
        //1. Answer UUID (input as path variables)
        //2. User Auth token of the signed in user
        AnswerEntity deletedAnswer = answerBusinessService.deleteAnswer(answer_uuid, authorization);

        //Appropriate response model (generated by swagger 2.0) is configured and returned as JSON.
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswer.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * @param question_uuid
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String question_uuid,
                                                                               @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        //Answer business service's getAllAnswersToQuestion method is called to run the business logic of
        // getting answer for particular question from database. It passes following as parameters:
        //1. Question UUID (input as path variables)
        //2. User Auth token of the signed in user

        //Get all answer as a list of AnswerEntity
        List<AnswerEntity> getAllAnswers = answerBusinessService.getAllAnswersToQuestion(question_uuid, authorization);
        QuestionEntity question = answerBusinessService.getQuestion(question_uuid);

        //Dynamically declare list of AnswerDetailsResponse
        List<AnswerDetailsResponse> allAnswersToQuestion = new ArrayList<>();

        //Populate the list of response for each answer using for each loop over getAllAnswers
        for (AnswerEntity answerEntity : getAllAnswers) {
            allAnswersToQuestion.add(new AnswerDetailsResponse().id(answerEntity.getUuid()).
                    questionContent(question.getContent()).answerContent(answerEntity.getAns()));
        }

        //Appropriate response model (generated by swagger 2.0) is configured and returned as JSON.
        return new ResponseEntity<List<AnswerDetailsResponse>>(allAnswersToQuestion, HttpStatus.OK);
    }
}