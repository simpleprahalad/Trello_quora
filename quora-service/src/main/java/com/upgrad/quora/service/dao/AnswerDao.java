package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

}

