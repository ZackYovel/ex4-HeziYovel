package com.example.ex4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * Application class for this project
 */
@SpringBootApplication
public class Ex4Application {

    /**
     * In this application I save login time in the user's session, so I can then use a filter to test if the
     * request is from a logged in user. Since the session is persisted during a server restart but the ChatUser
     * table empties, I can't just save a boolean for a logged in user, so I save the login time and compare it to the
     * restart time - if the login predates the restart it is treated as invalid and filtered out (and the user
     * is sent to the login page).
     */
    private long timeOfLastRefresh;

    /**
     * Main function
     *
     * @param args cmdline arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Ex4Application.class, args);
    }

    /**
     * Registers the un-logged users out for the relevant paths
     *
     * @return a filter object of type {@link FilterRegistrationBean<LoginFilter>}.
     */
    @Bean
    public FilterRegistrationBean<LoginFilter> logFilter() {
        FilterRegistrationBean<LoginFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoginFilter(this));
        registrationBean.addUrlPatterns("/chat","/search", "/api/chat/**", "/api/search/**");
        return registrationBean;
    }

    /**
     * Stores the server restart time in a variable.
     *
     * @param contextRefreshedEvent not used
     */
    @EventListener
    public void handleContextRefreshEvent(ContextRefreshedEvent contextRefreshedEvent) {
        timeOfLastRefresh = System.currentTimeMillis();
    }

    /**
     * @return the time in milliseconds of the last server refresh
     */
    public long getTimeOfLastRefresh() {
        return timeOfLastRefresh;
    }
}
