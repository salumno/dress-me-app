package ru.kpfu.itis.dressmeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.dressmeapp.model.ClothesItem;
import ru.kpfu.itis.dressmeapp.model.ClothesItemCategory;
import ru.kpfu.itis.dressmeapp.model.Sex;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ClothesItemRepository extends JpaRepository<ClothesItem, Long> {
    List<ClothesItem> findAllBySexAndBodyTypeAndCategory(Sex sex, String bodyType, ClothesItemCategory category);
}
