package ru.kpfu.itis.dressmeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.dressmeapp.model.Image;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}
