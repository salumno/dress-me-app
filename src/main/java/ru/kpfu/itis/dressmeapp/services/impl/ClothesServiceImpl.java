package ru.kpfu.itis.dressmeapp.services.impl;

import com.google.common.collect.Lists;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.form.ClothesItemAddForm;
import ru.kpfu.itis.dressmeapp.model.*;
import ru.kpfu.itis.dressmeapp.repositories.ClothesItemRepository;
import ru.kpfu.itis.dressmeapp.repositories.LookImageRepository;
import ru.kpfu.itis.dressmeapp.services.AuthenticationService;
import ru.kpfu.itis.dressmeapp.services.ClothesService;
import ru.kpfu.itis.dressmeapp.util.BodyShapeClassificationUtil;
import ru.kpfu.itis.dressmeapp.util.DockerUtil;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;
import ru.kpfu.itis.dressmeapp.util.LookImageClassificationUtil;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Random;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ClothesServiceImpl implements ClothesService {
    private final int TRY_LIMIT = 5;

    private ClothesItemRepository clothesItemRepository;
    private AuthenticationService authenticationService;
    private FileStorageUtil fileStorageUtil;
    private LookImageRepository lookImageRepository;
    private DockerUtil dockerUtil;
    private LookImageClassificationUtil lookImageClassificationUtil;
    private Random random;

    public ClothesServiceImpl(ClothesItemRepository clothesItemRepository, AuthenticationService authenticationService, FileStorageUtil fileStorageUtil, LookImageRepository lookImageRepository, DockerUtil dockerUtil, LookImageClassificationUtil lookImageClassificationUtil) {
        this.clothesItemRepository = clothesItemRepository;
        this.authenticationService = authenticationService;
        this.fileStorageUtil = fileStorageUtil;
        this.lookImageRepository = lookImageRepository;
        this.dockerUtil = dockerUtil;
        this.lookImageClassificationUtil = lookImageClassificationUtil;
        random = new Random();
    }

    @Override
    public ClothesAdviceBunch getClothesAdvice(Authentication authentication, String bodyType) {
        ClothesAdviceBunch bunch = ClothesAdviceBunch.builder().type(bodyType).lookImage(null).build();
        User user = authenticationService.getUserByAuthentication(authentication).getUser();
        if (!bodyType.equals(BodyShapeClassificationUtil.CLASSIFICATION_DONT_HAVE_RESULT) && !bodyType.equals(BodyShapeClassificationUtil.CLASSIFICATION_COMPLETELY_FAILED)) {
            LookImage lookImage = createLookImageForUser(user, bodyType);
            lookImageRepository.save(lookImage);
            bunch.setLookImage(lookImage);
        }
        return bunch;
    }

    private LookImage createLookImageForUser(User user, String bodyType) {
        FileInfo lookFileInfo = fileStorageUtil.createUserLookFileInfo(user.getId());
        LookImage lookImage = LookImage.builder().user(user).fileInfo(lookFileInfo).bodyType(bodyType).sex(user.getSex()).tryCount(0).build();
        do {
            List<ClothesItem> clothesItems = formClothesItems(bodyType, user.getSex());
            putLookImageIntoUserFolder(user, clothesItems, lookFileInfo.getStorageFileName());
        } while (!lookImageClassificationUtil.isLookLikable(lookImage, user));
        return lookImage;
    }

    private void updateLookImageForUser(LookImage lookImage) {
        User user = lookImage.getUser();
        FileInfo lookFileInfo = fileStorageUtil.createUserLookFileInfo(user.getId());
        lookImage.setFileInfo(lookFileInfo);
        do {
            List<ClothesItem> clothesItems = formClothesItems(lookImage.getBodyType(), lookImage.getSex());
            putLookImageIntoUserFolder(user, clothesItems, lookFileInfo.getStorageFileName());
        } while (!lookImageClassificationUtil.isLookLikable(lookImage, user));
    }
    
    private void putLookImageIntoUserFolder(User user, List<ClothesItem> clothesItems, String lookFileName) {
        String[] command = getLookCreatingCommand(user, clothesItems, lookFileName);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execTensorflowContainerCmd(command, resultStream);
    }

    private String[] getLookCreatingCommand(User user, List<ClothesItem> clothesItems, String lookName) {
        String command1 = "cd local/";
        String command2 = formJoinerCommand(user, clothesItems, lookName);
        return dockerUtil.createExecCommands(command1, command2);
    }

    private String formJoinerCommand(User user, List<ClothesItem> clothesItems, String lookName) {
        String command2 = "python image-vertical-joiner.py ";
        for (ClothesItem clothesItem: clothesItems) {
            String itemPath = "clothes/".concat(clothesItem.getFileInfo().getStorageFileName());
            command2 = command2.concat(itemPath + " ");
        }
        String userFolderName = "user-" + user.getId();
        command2 = command2.concat(userFolderName + "/" + lookName);
        return command2;
    }

    private List<ClothesItem> formClothesItems(String bodyType, Sex sex) {
        List<ClothesItem> clothesItems = Lists.newArrayList();
        if (!bodyType.equals(BodyShapeClassificationUtil.CLASSIFICATION_DONT_HAVE_RESULT) && !bodyType.equals(BodyShapeClassificationUtil.CLASSIFICATION_COMPLETELY_FAILED)) {
            for (ClothesItemCategory category: ClothesItemCategory.values()) {
                List<ClothesItem> items = clothesItemRepository.findAllBySexAndBodyTypeAndCategory(sex, bodyType, category);
                int randomIndex = calculateRandomIndex(items.size());
                ClothesItem randomItem = items.get(randomIndex);
                clothesItems.add(randomItem);
            }
        }
        return clothesItems;
    }

    @Override
    public void addClothesItems(ClothesItemAddForm form) {
        for (MultipartFile file: form.getFiles()) {
            FileInfo fileInfo = fileStorageUtil.getClothesItemFileInfoByMultipart(file);
            ClothesItem clothesItem = ClothesItem.builder()
                    .bodyType(form.getBodyType())
                    .category(ClothesItemCategory.valueOf(form.getItemCategory()))
                    .fileInfo(fileInfo)
                    .sex(Sex.valueOf(form.getSex()))
                    .build();
            fileStorageUtil.saveClothesItemToStorage(file, clothesItem);
            clothesItemRepository.save(clothesItem);
        }
    }

    @Override
    public ClothesAdviceBunch userDislikedLook(Long lookImageId, Authentication authentication) {
        User user = authenticationService.getUserByAuthentication(authentication).getUser();
        LookImage lookImage = lookImageRepository.findOne(lookImageId);
        if (!isLookImageRequestDataValid(lookImage, user)) {
            return null;
        }
        fileStorageUtil.putFileIntoUserDislikedFolder(user.getId(), lookImage.getFileInfo());

        lookImage.setTryCount(lookImage.getTryCount() + 1);
        lookImageRepository.save(lookImage);

        if (lookImage.getTryCount() < TRY_LIMIT) {
            return offerUserAnotherLook(lookImage);
        } else {
            lookImageClassificationUtil.retrainUserNetwork(user);
            return ClothesAdviceBunch.builder().lookImage(null).type("limit").build();
        }
    }

    private ClothesAdviceBunch offerUserAnotherLook(LookImage lookImage) {
        ClothesAdviceBunch bunch = ClothesAdviceBunch.builder().type(lookImage.getBodyType()).lookImage(lookImage).build();
        updateLookImageForUser(lookImage);
        lookImageRepository.save(lookImage);
        bunch.setLookImage(lookImage);
        return bunch;
    }

    @Override
    public String userLikedLook(Long lookImageId, Authentication authentication) {
        User user = authenticationService.getUserByAuthentication(authentication).getUser();
        LookImage lookImage = lookImageRepository.findOne(lookImageId);
        if (!isLookImageRequestDataValid(lookImage, user)) {
            return null;
        }
        FileInfo fileInfo = lookImage.getFileInfo();
        fileStorageUtil.putFileIntoUserLikedFolder(user.getId(), fileInfo);
        lookImageClassificationUtil.retrainUserNetwork(user);
        return "Success";
    }

    private boolean isLookImageRequestDataValid(LookImage lookImage, User user) {
        return lookImage != null && lookImage.getUser().getId().equals(user.getId());
    }

    private int calculateRandomIndex(int size) {
        return random.nextInt(size);
    }



}
