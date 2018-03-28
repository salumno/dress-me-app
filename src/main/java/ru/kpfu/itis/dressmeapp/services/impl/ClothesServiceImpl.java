package ru.kpfu.itis.dressmeapp.services.impl;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.dressmeapp.model.ClothesAdviceBunch;
import ru.kpfu.itis.dressmeapp.model.ClothesItem;
import ru.kpfu.itis.dressmeapp.model.ClothesItemCategory;
import ru.kpfu.itis.dressmeapp.model.Sex;
import ru.kpfu.itis.dressmeapp.repositories.ClothesItemRepository;
import ru.kpfu.itis.dressmeapp.services.ClothesService;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ClothesServiceImpl implements ClothesService {
    private ClothesItemRepository clothesItemRepository;
    private Random random;

    public ClothesServiceImpl(ClothesItemRepository clothesItemRepository) {
        this.clothesItemRepository = clothesItemRepository;
        random = new Random();
    }

    @Override
    public ClothesAdviceBunch getClothesAdvice(String bodyType, Sex sex) {
        Map<String, ClothesItem> clothesItemMap = Maps.newHashMap();
        for (ClothesItemCategory category: ClothesItemCategory.values()) {
            List<ClothesItem> items = clothesItemRepository.findAllBySexAndBodyTypeAndCategory(sex, bodyType, category);
            int randomIndex = calculateRandomIndex(items.size());
            ClothesItem randomItem = items.get(randomIndex);
            clothesItemMap.put(category.toString(), randomItem);
        }
        return ClothesAdviceBunch.builder().clothesItemMap(clothesItemMap).build();
    }

    private int calculateRandomIndex(int size) {
        return random.nextInt(size);
    }

}
