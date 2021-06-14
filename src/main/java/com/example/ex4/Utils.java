package com.example.ex4;

import org.springframework.web.context.request.RequestContextHolder;

/**
 * Non-specific utils
 */
public interface Utils {

    /**
     * @return the current session's id
     */
    static String getSessionId(){
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
