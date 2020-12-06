package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final String questionContent, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorizationToken,"ATHR-002","User is signed out.Sign in first to post a question.");

        UserEntity userEntity = userAuthTokenEntity.getUser();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionContent);
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(userEntity);
        questionEntity.setUuid(UUID.randomUUID().toString());
        return questionDao.createQuestion(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions(final String authorizationToken)
        throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorizationToken,"ATHR-002","User is signed out.Sign in first to get all questions");


        List<QuestionEntity> allQuestions = questionDao.getAllQuestions();
        return allQuestions;
    }

    @Transactional(propagation =  Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String questionID, final String questionContent, final String authorizationToken)  throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorizationToken,"ATHR-002","User is signed out.Sign in first to edit the question");

        QuestionEntity questionEntity = questionDao.getQuestionByuuid(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().getUuid().equals(userEntity.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setContent(questionContent);
        questionDao.updateQuestion(questionEntity);

        return questionEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionID, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorizationToken,"ATHR-002","User is signed out.Sign in first to delete the question");

        QuestionEntity questionEntity = questionDao.getQuestionByuuid(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().getUuid().equals(userEntity.getUuid())&&userEntity.getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        questionDao.deleteQuestion(questionEntity);

        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsByUser(String authorizationToken, String userUuid)
        throws AuthorizationFailedException, UserNotFoundException {

        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorizationToken,"ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");


        UserEntity userEntity = userDao.getUserByUuid(userUuid);

        if(userEntity == null){
            throw new UserNotFoundException("USR-001",
                "User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionEntity> allQuestionsByUser = questionDao.getAllQuestionsByUser(userEntity);
        return allQuestionsByUser;
    }
}
