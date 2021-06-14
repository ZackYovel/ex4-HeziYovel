package com.example.ex4.repositories;

import com.example.ex4.entities.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Access to ChatUser objects stored in the database
 */
@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    /**
     * @return all users
     */
    List<ChatUser> findAll();

    /**
     * @param name of user to find
     * @return a user with name {@code name} is one exists, else null
     */
    ChatUser findByName(String name);

    /**
     * @param sessionId to find
     * @return a user with this session id if exists, else null
     */
    ChatUser findBySessionId(String sessionId);
}
