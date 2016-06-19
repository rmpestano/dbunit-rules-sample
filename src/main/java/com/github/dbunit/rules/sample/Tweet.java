package com.github.dbunit.rules.sample;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pestano on 22/07/15.
 */
@Entity
public class Tweet {

    @Id
    @GeneratedValue
    private long id;

    @Size(min = 1, max = 140)
    private String content;

    private Integer likes;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
