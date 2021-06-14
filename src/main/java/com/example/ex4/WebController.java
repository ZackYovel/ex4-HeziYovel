package com.example.ex4;

import com.example.ex4.entities.ChatUser;
import com.example.ex4.repositories.ChatUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller for all web requests (all requests which return HTML).
 */
@Controller
public class WebController {

    /**
     * Key to store a user in the model by
     */
    public static final String MODEL_ATTRIBUTE_KEY_USER = "user";

    /**
     * Access to user objects in the database
     */
    private final ChatUserRepository chatUserRepository;

    /**
     * Make an instance with a user repository.
     *
     * @param chatUserRepository user repository to use for the instance
     */
    public WebController(ChatUserRepository chatUserRepository) {
        this.chatUserRepository = chatUserRepository;
    }

    /**
     * Handler for the landing page/login page/home page.
     *
     * @param response the response
     * @param model    the model to use when rendering the view (the login page)
     * @return the name of the login page template
     * @throws IOException if redirect fails
     */
    @GetMapping("/")
    public String home(HttpServletResponse response, Model model) throws IOException {
        if (LoginTools.isCurrentSessionLoggedIn(chatUserRepository)) {
            response.sendRedirect("/chat");
        }
        model.addAttribute("chatUser", new ChatUser());
        return "index";
    }

    /**
     * Login of a new of existing user.
     * <p>
     * There are three flows to this function:
     * 1. Returning user with the right name - should be logged in successfully.
     * 2. New user with a free name - should be logged in successfully.
     * 3. New user with a taken name - should be rejected.
     *
     * @param user     the attempted new user
     * @param model    the model
     * @param response the response
     * @param request  the request
     * @return the name of the template to use
     * @throws IOException if redirect fails
     */
    @PostMapping("/login")
    public String login(@ModelAttribute ChatUser user, Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {

        ChatUser existingUser = chatUserRepository.findByName(user.getName());

        if (existingUser == null) {
            // Flow 2: this is a new user with a free name - we log them in.
            // First we make sure the user instance contains it's session id:
            user.setSessionId(Utils.getSessionId());
            // Then we save the user in the database:
            chatUserRepository.save(user);
        } else if (!existingUser.getSessionId().equals(user.getSessionId())) {
            // Flow 3: this is a new user, but the name is taken, so we have to reject the user.
            model.addAttribute("invalidFeedbackClass", "is-invalid");
            return "index";
        }

        // This is either flow 2 (new user with a free name) or flow 1 - returning user. In both cases we want
        // to redirect to the chat. But first we refresh the login time in the session:
        request.getSession().setAttribute(LoginTools.SESSION_ATTR_LOGIN_TIME, System.currentTimeMillis());
        // Last we redirect the user to the chat:
        response.sendRedirect("/chat");
        return null;
    }

    /**
     * Handler for erroneous get requests
     *
     * @param response the response
     * @throws IOException thrown if redirect fails
     */
    @GetMapping("/login")
    public void loginGet(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    /**
     * Handler for the /chat path
     *
     * @param model the model
     * @return the name of the template to use
     */
    @GetMapping("/chat")
    public String chat(Model model) {
        String name = chatUserRepository.findBySessionId(Utils.getSessionId()).getName();
        model.addAttribute(MODEL_ATTRIBUTE_KEY_USER, name);
        return "chat";
    }

    /**
     * Handler for the /search path
     *
     * @return the name of the template to use
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }

    /**
     * Handler for the /error path
     *
     * @return the name of the template to use
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
