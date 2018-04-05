package ru.kpfu.itis.dressmeapp.services;

import org.springframework.security.core.Authentication;
import ru.kpfu.itis.dressmeapp.form.ClothesItemAddForm;
import ru.kpfu.itis.dressmeapp.model.ClothesAdviceBunch;
import ru.kpfu.itis.dressmeapp.model.ClothesItem;
import ru.kpfu.itis.dressmeapp.model.Sex;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ClothesService {
    ClothesAdviceBunch getClothesAdvice(Authentication authentication, String bodyType, Sex sex);
    void addClothesItems(ClothesItemAddForm form);

    ClothesAdviceBunch userDislikedLook(Long lookImageId, Authentication authentication);

    String userLikedLook(Long lookImageId, Authentication authentication);
}
