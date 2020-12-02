package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerBusinessService {

  @Autowired
  QuestionDao questionDao;

  @Autowired
  UserDao userDao;

  @Autowired
  AnswerDao answerDao;

  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity editAnswer(final String answerUuid, final String content, final String authorisation) throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorisation);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in.");
    } else if (userAuthTokenEntity.getLogoutAt() != null || userAuthTokenEntity.getExpiresAt()
        .isBefore(ZonedDateTime.now())) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to edit an answer");
    }

    AnswerEntity answerToBeEdited = answerDao.getAnswerByUuid(answerUuid);
    if (answerToBeEdited == null)
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

    String userUuid = userAuthTokenEntity.getUser().getUuid();

    if (answerToBeEdited.getUser().getUuid() != userUuid)
      throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
    else
      answerToBeEdited.setAns(content);
    return answerDao.updateAnswer(answerToBeEdited);

  }
}
