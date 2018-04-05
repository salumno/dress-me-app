package ru.kpfu.itis.dressmeapp.services.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.form.UserRegistrationForm;
import ru.kpfu.itis.dressmeapp.model.User;
import ru.kpfu.itis.dressmeapp.model.UserData;
import ru.kpfu.itis.dressmeapp.repositories.UserDataRepository;
import ru.kpfu.itis.dressmeapp.repositories.UserRepository;
import ru.kpfu.itis.dressmeapp.security.role.Role;
import ru.kpfu.itis.dressmeapp.services.RegistrationService;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class RegistrationServiceImpl implements RegistrationService {

    private UserDataRepository userDataRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private FileStorageUtil fileStorageUtil;

    public RegistrationServiceImpl(UserDataRepository userDataRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, FileStorageUtil fileStorageUtil) {
        this.userDataRepository = userDataRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public void register(UserRegistrationForm form) {
        User user = User.builder().name(form.getLogin()).build();
        userRepository.save(user);

        UserData userData = UserData.builder()
                .login(form.getLogin())
                .hashPassword(passwordEncoder.encode(form.getPassword()))
                .role(Role.USER)
                .user(user)
                .build();
        userDataRepository.save(userData);
        fileStorageUtil.createUserFolder(userData.getId());

        // TODO: 05.04.18 Add preset data to the folder
    }

}
