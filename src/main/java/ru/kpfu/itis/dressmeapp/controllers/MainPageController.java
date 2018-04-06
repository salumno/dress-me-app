package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.kpfu.itis.dressmeapp.model.User;
import ru.kpfu.itis.dressmeapp.services.AuthenticationService;
import ru.kpfu.itis.dressmeapp.services.ClassificationWaitingService;

/**
 * created by Iskander Valiev
 * on 22.03.2018
 *
 * @author Iskander Valiev (Kazan Federal University Higher School of Information Technologies and Information Systems)
 * @version 1.0
 */
@Controller
public class MainPageController {

    private ClassificationWaitingService classificationWaitingService;
    private AuthenticationService authenticationService;

    public MainPageController(ClassificationWaitingService classificationWaitingService, AuthenticationService authenticationService) {
        this.classificationWaitingService = classificationWaitingService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/")
    public String mainPage(@ModelAttribute("model")ModelMap model, Authentication authentication) {
        User user = authenticationService.getUserByAuthentication(authentication).getUser();
        model.addAttribute("isUserInWaiting", classificationWaitingService.isUserInWaiting(user));
        return "main-page";
    }
}
