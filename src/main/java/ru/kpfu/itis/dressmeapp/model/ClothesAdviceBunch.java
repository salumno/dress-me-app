package ru.kpfu.itis.dressmeapp.model;

import lombok.*;

import java.util.List;

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
    private String type;
    private List<ClothesItem> clothesItems;
}
