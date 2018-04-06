package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.kpfu.itis.dressmeapp.form.UserRegistrationForm;
import ru.kpfu.itis.dressmeapp.services.RegistrationService;
import ru.kpfu.itis.dressmeapp.validators.UserRegistrationFormValidator;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private RegistrationService registrationService;
    private UserRegistrationFormValidator userRegistrationFormValidator;

    public RegistrationController(RegistrationService registrationService, UserRegistrationFormValidator userRegistrationFormValidator) {
        this.registrationService = registrationService;
        this.userRegistrationFormValidator = userRegistrationFormValidator;
    }

    @InitBinder("userForm")
    public void initUserFormValidator(WebDataBinder binder) {
        binder.addValidators(userRegistrationFormValidator);
    }


    @PostMapping(value = "/registration")
    public String signUp(@Valid @ModelAttribute("userForm") UserRegistrationForm userRegistrationForm,
                         BindingResult errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            attributes.addFlashAttribute("error", errors.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/registration";
        }
        registrationService.register(userRegistrationForm);
        return "success-registration";
    }

    @GetMapping(value = "/registration")
    public String getSignUpPage(@ModelAttribute("model")ModelMap model) {
        return "registration";
    }
}