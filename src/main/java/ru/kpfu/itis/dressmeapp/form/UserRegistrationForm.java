package ru.kpfu.itis.dressmeapp.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationForm {
    private String login;
    private String password;
    private String repeatedPassword;
    private String sex;
}
