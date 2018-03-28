package ru.kpfu.itis.dressmeapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.model.ClassificationAnswer;
import ru.kpfu.itis.dressmeapp.services.ImageClassificationService;

/**
 * created by Iskander Valiev
 * on 22.03.2018
 *
 * @author Iskander Valiev (Kazan Federal University Higher School of Information Technologies and Information Systems)
 * @version 1.0
 */
@Controller
public class ImageUploadController {

    @Autowired
    private ImageClassificationService imageClassificationService;

    @ResponseBody
    @PostMapping("/upload")
    public ClassificationAnswer classifyImage(@RequestParam("file")MultipartFile file) {
        String result = imageClassificationService.classify(file);
        return imageClassificationService.getAdvicesByResult(result);
    }
}
