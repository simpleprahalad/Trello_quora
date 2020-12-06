package com.upgrad.quora.service.entity;


import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "question", schema = "quora")
@NamedQueries(
        {
                @NamedQuery(name = "getQuestionByUUID", query = "SELECT q FROM QuestionEntity q WHERE q.uuid=:uuid"),
                @NamedQuery(name = "getAllQuestion", query = "SELECT q FROM QuestionEntity q"),
                @NamedQuery(name = "getAllQuestionsByUser", query = "SELECT q FROM QuestionEntity q WHERE q.user=:user")
        }
)
public class QuestionEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "UUID")
    @Size(max = 64)
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    private String content;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity user;

    @Column(name = "DATE")
    @NotNull
    private ZonedDateTime date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}