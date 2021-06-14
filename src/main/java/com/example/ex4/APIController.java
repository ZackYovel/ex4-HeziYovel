package com.example.ex4;

import com.example.ex4.entities.ChatUser;
import com.example.ex4.entities.Message;
import com.example.ex4.repositories.ChatUserRepository;
import com.example.ex4.repositories.MessageRepository;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * A controller for all api methods
 */
@RestController
@RequestMapping("/api")
public class APIController {

    /**
     * Key for the json object returned by the update method
     */
    public static final String JSON_KEY_USERS = "users";
    /**
     * Key for the json object returned by the update method
     */
    public static final String JSON_KEY_MESSAGES = "messages";
    /**
     * Response when a message was recorded in the database
     */
    public static final String MESSAGE_RECEIVED_SUCCESS = "success";
    /**
     * Response when a message was recorded in the database
     */
    public static final String MESSAGE_RECEIVED_FAILURE = "failure";
    /**
     * Access to ChatUser objects stored in the database
     */
    private final ChatUserRepository chatUserRepository;
    /**
     * Access to ChatUser objects stored in the database
     */
    private final MessageRepository messageRepository;

    /**
     * Make an APIController with user and message repositories.
     * @param chatUserRepository user repository
     * @param messageRepository message repository
     */
    public APIController(ChatUserRepository chatUserRepository, MessageRepository messageRepository) {
        this.chatUserRepository = chatUserRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Provides the current state of the chatroom.
     *
     * @return "users" - a list of {@code ChatUser}s and "messages" - a list of {@code Message}s
     */
    @GetMapping("/chat/update")
    HashMap<String, Object> update() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(JSON_KEY_USERS, chatUserRepository.findAll());
        result.put(JSON_KEY_MESSAGES, messageRepository.findFirst5MessagesByOrderByTimePostedDesc());
        return result;
    }

    /**
     * Get handler for illegal get requests. Sets the response code to BAD_REQUEST (400).
     *
     * @param response the response to the client
     * @return an error message.
     */
    @GetMapping("/chat/send-message")
    String thisFunctionNeedsPost(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "This function requires a post request.";
    }

    /**
     * Handler for the send-message path. If the user is logged in, the message is recorded.
     *
     * @param message the message given
     * @param response the response to the client
     * @return an error/success message
     * @throws IOException if redirect fails
     */
    @PostMapping(value = "/chat/send-message", consumes = "application/json", produces = "application/json")
    String sendMessage(@RequestBody Message message, HttpServletResponse response) throws IOException {
        ChatUser chatUser = new ChatUser(message.getUser(), Utils.getSessionId());
        if (chatUserRepository.exists(Example.of(chatUser))) {
            messageRepository.save(message);
            return MESSAGE_RECEIVED_SUCCESS;
        } else {
            response.sendRedirect("/");
            return MESSAGE_RECEIVED_FAILURE;
        }
    }

    /**
     * Handler for the /search/username/{username} path.
     *
     * @param username searching for all messages by {@code username}
     * @return all messages by {@code username}
     */
    @GetMapping("/search/username/{username}")
    List<Message> searchByUsername(@PathVariable String username) {
        return messageRepository.findByUser(username);
    }

    /**
     * Handler for the /search/message/{textInMessage} path.
     *
     * @param textInMessage searching for all messages containing {@code textInMessage}
     * @return all messages containing {@code textInMessage}
     */
    @GetMapping("/search/message/{textInMessage}")
    List<Message> searchByTextInMessage(@PathVariable String textInMessage) {
        return messageRepository.findByTextContains(textInMessage);
    }

    /**
     * Handler for the logout path.
     *
     * @param request the request from the client
     * @param response the response to the client
     * @return a message
     * @throws IOException if redirect fails
     */
    @GetMapping("/chat/logout")
    String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ChatUser chatUser = chatUserRepository.findBySessionId(Utils.getSessionId());
        chatUserRepository.delete(chatUser);
        request.getSession().setAttribute(LoginTools.SESSION_ATTR_LOGIN_TIME, -1L);
        response.sendRedirect("/");
        return "Please log in.";
    }
}
