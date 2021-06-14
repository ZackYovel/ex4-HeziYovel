package com.example.ex4.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * Message entity
 */
@Entity
public class Message {

    /**
     * Identifies the message in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Name of the user who posted this message
     */
    @NotEmpty(message = "User name is mandatory")
    private String user;

    /**
     * Message text
     */
    @NotEmpty(message = "Message text is mandatory")
    private String text;

    /**
     * The time in which this message was posted
     */
    private Long timePosted;

    /**
     * Empty constructor (set's the {@code timePosted} field to current time)
     */
    public Message() {
        this.timePosted = System.currentTimeMillis();
    }

    /**
     * Make a message from {@code user} with {@code text}
     * @param user the sender of the message
     * @param text the text of the message
     */
    public Message(String user, String text) {
        this();
        this.user = user;
        this.text = text;
    }

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return timePosted
     */
    public Long getTimePosted() {
        return timePosted;
    }

    /**
     * @param timePosted to set
     */
    public void setTimePosted(Long timePosted) {
        this.timePosted = timePosted;
    }
}
