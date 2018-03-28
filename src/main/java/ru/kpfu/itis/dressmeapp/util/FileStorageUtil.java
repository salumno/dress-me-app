package ru.kpfu.itis.dressmeapp.util;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.model.FileInfo;
import ru.kpfu.itis.dressmeapp.model.Image;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */
@Component
public class FileStorageUtil {

    @Value("${storage.path}")
    private String storagePath;

    public FileInfo getImageFileInfoByMultipart(MultipartFile file) {
        FileInfo fileInfo = convertFromMultipart(file);
        fileInfo.setPath(getFullPath(fileInfo.getStorageFileName()));
        return fileInfo;
    }

    private String getFullPath(String storageFileName) {
        return storagePath + "/" + storageFileName;
    }

    private FileInfo convertFromMultipart(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        String storageFileName = createStorageFileName(originalFileName);
        return FileInfo.builder()
                .originalFileName(originalFileName)
                .storageFileName(storageFileName)
                .size(size)
                .type(contentType)
                .build();
    }

    @SneakyThrows
    public void saveImageToStorage(MultipartFile file, Image image) {
        Files.copy(file.getInputStream(), Paths.get(storagePath, image.getFileInfo().getStorageFileName()));
    }

    private String createStorageFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString();
        return newFileName + "." + extension;
    }


    @SneakyThrows
    public void removeFileFromStorage(FileInfo fileInfo) {
        Files.delete(Paths.get(fileInfo.getPath()));
    }
}
