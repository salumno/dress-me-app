package ru.kpfu.itis.dressmeapp.services;

import org.springframework.security.core.Authentication;
import ru.kpfu.itis.dressmeapp.form.UserPhotoUploadForm;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ImageClassificationService {
    String classify(Authentication authentication, UserPhotoUploadForm form);
}
