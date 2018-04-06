package ru.kpfu.itis.dressmeapp.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ru.kpfu.itis.dressmeapp.form.UserRegistrationForm;
import ru.kpfu.itis.dressmeapp.model.UserData;
import ru.kpfu.itis.dressmeapp.repositories.UserDataRepository;

import java.util.Optional;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class UserRegistrationFormValidator implements Validator{

    private UserDataRepository userDataRepository;

    public UserRegistrationFormValidator(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.getName().equals(UserRegistrationForm.class.getName());
    }

    @Override
    public void validate(Object o, Errors errors) {
        UserRegistrationForm form = (UserRegistrationForm) o;

        Optional<UserData> userDataOptional = userDataRepository.findOneByLogin(form.getLogin());

        if (userDataOptional.isPresent()) {
            errors.reject("bad.login", "Логин занят!");
        }

        if (!isPasswordsIdentical(form.getPassword(), form.getRepeatedPassword())) {
            errors.reject("bad.password", "Пароли не совпадают!");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "empty.login", "Пустой логин");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "empty.password", "Пустой пароль");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "repeatedPassword", "empty.repeatedPassword", "Пустой второй пароль");
    }

    private boolean isPasswordsIdentical(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }
}
