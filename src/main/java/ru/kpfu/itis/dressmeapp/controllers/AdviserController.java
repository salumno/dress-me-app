package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.dressmeapp.form.UserPhotoUploadForm;
import ru.kpfu.itis.dressmeapp.model.ClothesAdviceBunch;
import ru.kpfu.itis.dressmeapp.model.Sex;
import ru.kpfu.itis.dressmeapp.services.ClothesService;
import ru.kpfu.itis.dressmeapp.services.ImageClassificationService;

/**
 * created by Iskander Valiev
 * on 22.03.2018
 *
 * @author Iskander Valiev (Kazan Federal University Higher School of Information Technologies and Information Systems)
 * @version 1.0
 */

@Controller
@RestController
public class AdviserController {

    private ImageClassificationService imageClassificationService;
    private ClothesService clothesService;

    public AdviserController(ImageClassificationService imageClassificationService, ClothesService clothesService) {
        this.imageClassificationService = imageClassificationService;
        this.clothesService = clothesService;
    }

    @PostMapping("/upload")
    public ClothesAdviceBunch classifyImage(@ModelAttribute UserPhotoUploadForm form, Authentication authentication) {
        String resultBodyType = imageClassificationService.classify(form);
        System.out.println(resultBodyType);
        return clothesService.getClothesAdvice(authentication, resultBodyType, Sex.valueOf(form.getSex()));
    }

    @GetMapping("/look/{id}/dislike")
    public ClothesAdviceBunch userDislikedLook(@PathVariable("id") Long lookImageId, Authentication authentication) {
        return clothesService.userDislikedLook(lookImageId, authentication);
    }

    @GetMapping("/look/{id}/like")
    public String userLikedLook(@PathVariable("id") Long lookImageId, Authentication authentication) {
        return clothesService.userLikedLook(lookImageId, authentication);
    }
}
