package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
      TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT q FROM QuestionEntity q WHERE q.user=:user", QuestionEntity.class);
      query.setParameter("user", user);
      List<QuestionEntity> allQuestions = query.getResultList();
      return allQuestions;
    }
  
    public List<QuestionEntity> getAllQuestions(){
        TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT q FROM QuestionEntity q", QuestionEntity.class);
        List<QuestionEntity> allQuestions = query.getResultList();
        return allQuestions;
    }

    public QuestionEntity getQuestion(String uuid) {
        try {
            return entityManager.createNamedQuery("questionByID", QuestionEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void updateQuestion(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

    public void deleteQuestion(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}
