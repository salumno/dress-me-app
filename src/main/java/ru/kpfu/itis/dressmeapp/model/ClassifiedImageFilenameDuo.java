package ru.kpfu.itis.dressmeapp.model;

import lombok.*;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class ClassifiedImageFilenameDuo {
    private String faceFileName;
    private String profileFileName;
}
