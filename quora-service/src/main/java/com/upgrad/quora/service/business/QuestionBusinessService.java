package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
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
}
