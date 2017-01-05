package com.ironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by scofieldservices on 1/3/17.
 */

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender;

    @ManyToOne
    User recipient;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    LocalDateTime postedTime;

    @Column(nullable = false)
    Boolean publicORnot;

    @Column(nullable = false)
    Long lifeTime;

    public Photo() {
    }

    public Photo(User sender, User recipient, String filename, LocalDateTime postedTime, Boolean publicORnot, Long lifeTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.postedTime = postedTime;
        this.publicORnot = publicORnot;
        this.lifeTime = lifeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(LocalDateTime postedTime) {
        this.postedTime = postedTime;
    }

    public Boolean getPublicORnot() {
        return publicORnot;
    }

    public void setPublicORnot(Boolean publicORnot) {
        this.publicORnot = publicORnot;
    }

    public Long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(Long lifeTime) {
        this.lifeTime = lifeTime;
    }
}
