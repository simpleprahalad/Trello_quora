package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import java.util.List;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {
    @PersistenceContext
    private EntityManager entityManager;

    public List<QuestionEntity> getAllQuestionsByUser(UserEntity user) {
      TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT q FROM QuestionEntity q WHERE q.user=:user", QuestionEntity.class);
      query.setParameter("user", user);
      List<QuestionEntity> allQuestions = query.getResultList();
      return allQuestions;
    }
}
