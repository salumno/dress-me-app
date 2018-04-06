package ru.kpfu.itis.dressmeapp.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.form.UserPhotoUploadForm;
import ru.kpfu.itis.dressmeapp.model.ClassifiedImageFilenameDuo;
import ru.kpfu.itis.dressmeapp.model.FileInfo;
import ru.kpfu.itis.dressmeapp.model.Image;
import ru.kpfu.itis.dressmeapp.repositories.ImageRepository;
import ru.kpfu.itis.dressmeapp.services.AuthenticationService;
import ru.kpfu.itis.dressmeapp.services.ImageClassificationService;
import ru.kpfu.itis.dressmeapp.util.BodyShapeClassificationUtil;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ImageClassificationServiceImpl implements ImageClassificationService {
    private ImageRepository imageRepository;
    private FileStorageUtil fileStorageUtil;
    private BodyShapeClassificationUtil classificationUtil;
    private AuthenticationService authenticationService;

    @Autowired
    public ImageClassificationServiceImpl(ImageRepository imageRepository, FileStorageUtil fileStorageUtil, BodyShapeClassificationUtil classificationUtil, AuthenticationService authenticationService) {
        this.imageRepository = imageRepository;
        this.fileStorageUtil = fileStorageUtil;
        this.classificationUtil = classificationUtil;
        this.authenticationService = authenticationService;
    }

    @Override
    public String classify(Authentication authentication, UserPhotoUploadForm form) {
        String sex = authenticationService.getUserByAuthentication(authentication).getUser().getSex().toString();
        ClassifiedImageFilenameDuo filenameDuo = saveUploadedImages(form);
        return classificationUtil.startClassification(filenameDuo, sex);
    }

    private ClassifiedImageFilenameDuo saveUploadedImages(UserPhotoUploadForm form) {
        String faceFilename = saveImage(form.getFace()).getFileInfo().getStorageFileName();
        String profileFilename = saveImage(form.getProfile()).getFileInfo().getStorageFileName();
        return ClassifiedImageFilenameDuo.builder().faceFileName(faceFilename).profileFileName(profileFilename).build();
    }

    private Image saveImage(MultipartFile imageFile) {
        FileInfo fileInfo = fileStorageUtil.getImageFileInfoByMultipart(imageFile);
        Image image = Image.builder().fileInfo(fileInfo).build();
        imageRepository.save(image);
        fileStorageUtil.saveImageToStorage(imageFile, image);
        return image;
    }
}
