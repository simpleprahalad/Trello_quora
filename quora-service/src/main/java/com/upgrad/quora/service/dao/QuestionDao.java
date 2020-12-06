package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @param user
     * @return
     */
    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
        List<QuestionEntity> allQuestions = entityManager.createNamedQuery("getAllQuestionsByUser", QuestionEntity.class)
                .setParameter("user", user).getResultList();
        return allQuestions;
    }

    /**
     * @param questionEntity
     * @return
     */
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * @param uuid
     * @return
     */
    public QuestionEntity getQuestionByuuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getQuestionByUUID", QuestionEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @param questionEntity
     */
    public void updateQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public List<QuestionEntity> getAllQuestions() {
        List<QuestionEntity> allQuestions = entityManager.createNamedQuery("getAllQuestion", QuestionEntity.class).getResultList();
        return allQuestions;
    }

    /**
     * @param questionEntity
     */
    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}