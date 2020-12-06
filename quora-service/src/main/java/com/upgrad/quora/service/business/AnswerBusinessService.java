package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final String content, final String uuid, final String authorisation) throws AuthorizationFailedException, InvalidQuestionException {


        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorisation,"ATHR-002","User is signed out.Sign in first to post an answer");

        QuestionEntity question = questionDao.getQuestionByuuid(uuid);
        if (question == null)
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");


        AnswerEntity answer = new AnswerEntity();
        answer.setUuid(UUID.randomUUID().toString());
        answer.setDate(ZonedDateTime.now());
        answer.setQuestion(question);
        answer.setAns(content);
        answer.setUser(userAuthTokenEntity.getUser());

        answerDao.createAnswer(answer);
        return answer;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(final String answerUuid, final String content, final String authorisation) throws AuthorizationFailedException, AnswerNotFoundException {


        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorisation,"ATHR-002","User is signed out.Sign in first to edit an answer");

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

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswer(final String answerUuid,final String authorisation) throws AuthorizationFailedException, AnswerNotFoundException {


        UserAuthTokenEntity userAuthTokenEntity=authenticationService.validateToken(authorisation,"ATHR-002","User is signed out.Sign in first to delete an answer");

        AnswerEntity answerToBeDeleted = answerDao.getAnswerByUuid(answerUuid);
        if (answerToBeDeleted == null)
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");

        String loggedInUserUuid = userAuthTokenEntity.getUser().getUuid();
        String loggedInUserRole=userAuthTokenEntity.getUser().getRole();

        if(answerToBeDeleted.getUser().getUuid()!=loggedInUserUuid&&loggedInUserRole.equals("nonadmin"))
             throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        else
        return answerDao.deleteAnswer(answerToBeDeleted);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity>getAllAnswersToQuestion(final String question_uuid,final String authorisation) throws AuthorizationFailedException, InvalidQuestionException {

        authenticationService.validateToken(authorisation,"ATHR-002","User is signed out.Sign in first to get the answers");

        QuestionEntity question = questionDao.getQuestionByuuid(question_uuid);
        if (question == null)
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");

         List<AnswerEntity> getAllAnswers=answerDao.getAllAnswersToQuestion(question_uuid);
         return getAllAnswers;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestion(final String question_uuid) throws InvalidQuestionException {
        QuestionEntity question = questionDao.getQuestionByuuid(question_uuid);
        if (question == null)
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        return question;
    }
}
