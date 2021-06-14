package com.example.ex4.repositories;

import com.example.ex4.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Access to Message objects stored in the database
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * @return last 5 messages (by post time) descending
     */
    List<Message> findFirst5MessagesByOrderByTimePostedDesc();

    /**
     * @param user name of the user who's messages are wanted
     * @return all messages by {@code user}
     */
    List<Message> findByUser(String user);

    /**
     * @param textContains potential part of a message
     * @return all messages that contain {@code textContains}
     */
    List<Message> findByTextContains(String textContains);
}
