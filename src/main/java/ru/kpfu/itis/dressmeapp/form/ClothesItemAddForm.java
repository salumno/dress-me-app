package ru.kpfu.itis.dressmeapp.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClothesItemAddForm {
    private String sex;
    private String bodyType;
    private String itemCategory;
    private MultipartFile[] files;
}
