package ru.kpfu.itis.dressmeapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.dressmeapp.model.FileInfo;

import java.util.Optional;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findById(Long id);
}
