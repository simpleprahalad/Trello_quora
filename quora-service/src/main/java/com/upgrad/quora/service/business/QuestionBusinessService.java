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

  public List<QuestionEntity> getAllQuestionsByUser(String authorizationToken, String userUuid)
      throws AuthorizationFailedException, UserNotFoundException {

      UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
      if (userAuthTokenEntity == null) {
          throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
      } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
          .isBefore(ZonedDateTime.now())) {
          throw new AuthorizationFailedException("ATHR-002",
              "User is signed out.Sign in first to post a question.");
      }

      UserEntity userEntity = userDao.getUser(userUuid);

      if(userEntity == null){
          throw new UserNotFoundException("USR-001",
              "User with entered uuid whose question details are to be seen does not exist");
      }

      List<QuestionEntity> allQuestionsByUser = questionDao.getAllQuestionsByUser(userEntity);
      return allQuestionsByUser;
  }

    public List<QuestionEntity> getAllQuestions(final String authorizationToken)
        throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
            .isAfter(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002",
                "User is signed out.Sign in first to post a question.");
        }

        List<QuestionEntity> allQuestions = questionDao.getAllQuestions();
        return allQuestions;
    }

    @Transactional(propagation =  Propagation.REQUIRED)
    public QuestionEntity editQuestion(final String questionID, final String questionContent, final String authorizationToken)  throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
                 .isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity questionEntity = questionDao.getQuestion(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().equals(userEntity)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        questionEntity.setContent(questionContent);
        questionDao.updateQuestion(questionEntity);

        return questionEntity;
    }

    @Transactional(propagation =  Propagation.REQUIRED)
    public QuestionEntity deleteQuestion(final String questionID, final String authorizationToken)  throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);

        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
        } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
                .isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to delete the question");
        }
        QuestionEntity questionEntity = questionDao.getQuestion(questionID);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        UserEntity userEntity = userAuthTokenEntity.getUser();
        if (!questionEntity.getUser().equals(userEntity)) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can delete the question");
        }

        questionDao.deleteQuestion(questionEntity);

        return questionEntity;
    }
}
