package ru.kpfu.itis.dressmeapp.services.impl;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.form.ClothesItemAddForm;
import ru.kpfu.itis.dressmeapp.model.*;
import ru.kpfu.itis.dressmeapp.repositories.ClothesItemRepository;
import ru.kpfu.itis.dressmeapp.services.ClothesService;
import ru.kpfu.itis.dressmeapp.util.ClassificationUtil;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ClothesServiceImpl implements ClothesService {
    private ClothesItemRepository clothesItemRepository;
    private FileStorageUtil fileStorageUtil;
    private Random random;

    public ClothesServiceImpl(ClothesItemRepository clothesItemRepository, FileStorageUtil fileStorageUtil) {
        this.clothesItemRepository = clothesItemRepository;
        this.fileStorageUtil = fileStorageUtil;
        random = new Random();
    }

    @Override
    public ClothesAdviceBunch getClothesAdvice(String bodyType, Sex sex) {
        List<ClothesItem> clothesItems = Lists.newArrayList();
        if (!bodyType.equals(ClassificationUtil.CLASSIFICATION_DONT_HAVE_RESULT) && !bodyType.equals(ClassificationUtil.CLASSIFICATION_COMPLETELY_FAILED)) {
            for (ClothesItemCategory category: ClothesItemCategory.values()) {
                List<ClothesItem> items = clothesItemRepository.findAllBySexAndBodyTypeAndCategory(sex, bodyType, category);
                int randomIndex = calculateRandomIndex(items.size());
                ClothesItem randomItem = items.get(randomIndex);
                clothesItems.add(randomItem);
            }
        }
        return ClothesAdviceBunch.builder().type(bodyType).clothesItems(clothesItems).build();
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

    private int calculateRandomIndex(int size) {
        return random.nextInt(size);
    }



}
