package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kpfu.itis.dressmeapp.form.UserPhotoUploadForm;
import ru.kpfu.itis.dressmeapp.model.ClothesItem;
import ru.kpfu.itis.dressmeapp.model.Sex;
import ru.kpfu.itis.dressmeapp.services.ClothesService;
import ru.kpfu.itis.dressmeapp.services.ImageClassificationService;

import java.util.List;

/**
 * created by Iskander Valiev
 * on 22.03.2018
 *
 * @author Iskander Valiev (Kazan Federal University Higher School of Information Technologies and Information Systems)
 * @version 1.0
 */

@Controller
public class AdviserController {

    private ImageClassificationService imageClassificationService;
    private ClothesService clothesService;

    public AdviserController(ImageClassificationService imageClassificationService, ClothesService clothesService) {
        this.imageClassificationService = imageClassificationService;
        this.clothesService = clothesService;
    }

    @ResponseBody
    @PostMapping("/upload")
    public List<ClothesItem> classifyImage(@ModelAttribute UserPhotoUploadForm form) {
        String resultBodyType = imageClassificationService.classify(form);
        System.out.println(resultBodyType);
        return clothesService.getClothesAdvice(resultBodyType, Sex.valueOf(form.getSex()));
    }
}
