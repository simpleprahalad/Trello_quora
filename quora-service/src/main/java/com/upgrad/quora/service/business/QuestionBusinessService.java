package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * @param questionContent
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final String questionContent,
                                         final String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.Sign in first to post a question.");

        UserEntity userEntity = userAuthTokenEntity.getUser();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionContent);
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(userEntity);
        questionEntity.setUuid(UUID.randomUUID().toString());
        return questionDao.createQuestion(questionEntity);
    }

    /**
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken)
            throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.Sign in first to get all questions");
        return questionDao.getAllQuestions();
    }

    /**
     * @param questionID
     * @param questionContent
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String questionID,
                                       final String questionContent,
                                       final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        //Validate the user auth token
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.Sign in first to edit the question");

        //Get the question to be edited by questionDAO
        //Throw InvalidQuestionException if given UUID question does not exist in database
        QuestionEntity questionEntity = questionDao.getQuestionByuuid(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        //Throw AuthorizationFailedException if signed in user is not owner of question
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().getUuid().equals(userEntity.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        //Alter and return question entity
        questionEntity.setContent(questionContent);
        questionDao.updateQuestion(questionEntity);

        return questionEntity;
    }

    /**
     * @param questionID
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionID,
                                         final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        //Validate the user auth token
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.Sign in first to delete the question");

        //Get the question to be edited by questionDAO
        //Throw InvalidQuestionException if given UUID question does not exist in database
        QuestionEntity questionEntity = questionDao.getQuestionByuuid(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        ////Throw AuthorizationFailedException if signed in user is not owner of question or admin
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().getUuid().equals(userEntity.getUuid()) && userEntity.getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        //Delete the respective question using questionDAO
        questionDao.deleteQuestion(questionEntity);
        return questionEntity;
    }

    /**
     * @param authorizationToken
     * @param userUuid
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public List<QuestionEntity> getAllQuestionsByUser(String authorizationToken, String userUuid)
            throws AuthorizationFailedException, UserNotFoundException {

        //Validate the user auth token
        UserAuthTokenEntity userAuthTokenEntity = authenticationService.validateToken(authorizationToken,
                "ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");

        //Get the user from userDAO
        //Throw UserNotFoundException if user does not exist in database
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001",
                    "User with entered uuid whose question details are to be seen does not exist");
        }

        //Return list of all question by user using userDAO method getAllQuestionsByUser
        return questionDao.getAllQuestionsByUser(userEntity);
    }
}