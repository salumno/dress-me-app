package ru.kpfu.itis.dressmeapp.model;

import lombok.*;

import java.util.Map;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClothesAdviceBunch {
    private Map<String, ClothesItem> clothesItemMap;
}
