package com.example.ex4;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filters a request by the user login status (logged in passes through, not logged in filtered out)
 */
public class LoginFilterRest extends OncePerRequestFilter {

    /**
     * The {@link Ex4Application}, which knows the last restart time time
     */
    private final Ex4Application ex4Application;
    public static final String RESPONSE_HEADER_LOCATION = "location";

    /**
     * Make a filter with an {@link Ex4Application}
     *
     * @param ex4Application the application
     */
    public LoginFilterRest(Ex4Application ex4Application) {
        this.ex4Application = ex4Application;
    }

    /**
     * Filter out a request if the session indicates no login or a login that predates the latest server restart.
     *
     * @param httpServletRequest the request
     * @param httpServletResponse the response
     * @param filterChain the filter chain
     * @throws ServletException may be thrown by other filters in the chain
     * @throws IOException thrown if redirect fails
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        Object loginTime = httpServletRequest.getSession().getAttribute(LoginTools.SESSION_ATTR_LOGIN_TIME);
        long refreshTime = ex4Application.getTimeOfLastRefresh();
        if (loginTime == null || (long) loginTime < refreshTime) {
            httpServletResponse.addHeader(RESPONSE_HEADER_LOCATION, "/");
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
