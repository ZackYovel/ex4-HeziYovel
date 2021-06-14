package com.example.ex4.entities;

import com.example.ex4.Utils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

/**
 * Chat user entity
 */
@Entity
public class ChatUser {

    /**
     * Identifies the user in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Unique username chosen by the user
     */
    @NotEmpty(message = "name is mandatory")
    private String name;

    /**
     * Session id of the user
     */
    @NotEmpty(message = "sessionId is mandatory")
    private String sessionId;

    /**
     * Constructs a {@link ChatUser} with a name and a session id
     *
     * @param name the name chosen by the user
     * @param sessionId the id of the session attached to this user
     */
    public ChatUser(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    /**
     * Empty constructor
     */
    public ChatUser() {

    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
