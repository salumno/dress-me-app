package ru.kpfu.itis.dressmeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.dressmeapp.model.ClassificationWaiting;
import ru.kpfu.itis.dressmeapp.model.ClassificationWaitingStatus;

import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface ClassificationWaitingRepository extends JpaRepository<ClassificationWaiting, Long> {
    List<ClassificationWaiting> findAllByUserIdAndStatus(Long userId, ClassificationWaitingStatus status);
}
