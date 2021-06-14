package com.example.ex4;

import com.example.ex4.repositories.ChatUserRepository;

/**
 * Project-specific login utilities
 */
public interface LoginTools {

    /**
     * Key to save the login time by in the session
     */
    String SESSION_ATTR_LOGIN_TIME = "loginTime";

    /**
     * Checks repository to see if the current session is logged in
     *
     * @param repository the repository to check
     * @return true if a user with the current session id presents in the repository
     */
    static boolean isCurrentSessionLoggedIn(ChatUserRepository repository) {
        return repository.findBySessionId(Utils.getSessionId()) != null;
    }
}
