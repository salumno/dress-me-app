package ru.kpfu.itis.dressmeapp.services;

import org.springframework.security.core.Authentication;
import ru.kpfu.itis.dressmeapp.model.UserData;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface AuthenticationService {
    UserData getUserByAuthentication(Authentication authentication);
}
