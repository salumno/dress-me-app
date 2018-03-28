package ru.kpfu.itis.dressmeapp.services;

import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.model.ClassificationAnswer;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ImageClassificationService {

    String classify(MultipartFile image);

    ClassificationAnswer getAdvicesByResult(String resultType);
}
