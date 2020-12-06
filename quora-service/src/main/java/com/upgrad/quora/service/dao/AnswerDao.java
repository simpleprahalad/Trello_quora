package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * @param answerEntity
     * @return
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * @param uuid
     * @return
     */
    public AnswerEntity getAnswerByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("getAnswerByUUID", AnswerEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * @param updatedAnswerEntity
     * @return
     */
    public AnswerEntity updateAnswer(final AnswerEntity updatedAnswerEntity) {
        entityManager.merge(updatedAnswerEntity);
        return updatedAnswerEntity;
    }

    /**
     * @param answerToBeDeleted
     * @return
     */
    public AnswerEntity deleteAnswer(final AnswerEntity answerToBeDeleted) {
        entityManager.remove(answerToBeDeleted);
        return answerToBeDeleted;
    }

    /**
     * @param question_id
     * @return
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String question_id) {
        return entityManager.createNamedQuery("getAllAnswersToQuestion", AnswerEntity.class).setParameter("uuid", question_id).getResultList();
    }
}