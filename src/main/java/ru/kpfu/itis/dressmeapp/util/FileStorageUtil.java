package ru.kpfu.itis.dressmeapp.util;

import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.model.ClothesItem;
import ru.kpfu.itis.dressmeapp.model.FileInfo;
import ru.kpfu.itis.dressmeapp.model.Image;
import ru.kpfu.itis.dressmeapp.repositories.FileInfoRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Value("${storage.path.clothes}")
    private String clothesStoragePath;

    private FileInfoRepository fileInfoRepository;

    public FileStorageUtil(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @SneakyThrows
    public void createUserFolder(Long userId) {
        String storagePath = getUserFolderPath(userId);
        String likeSubfolderPath = storagePath + "/like";
        String dislikeSubfolderPath = storagePath + "/dislike";
        String[] paths = { storagePath, likeSubfolderPath, dislikeSubfolderPath };
        for (String dir: paths) {
            Path path = Paths.get(dir);
            Files.createDirectories(path);
        }
    }

    private String getUserFolderPath(Long userId) {
        return storagePath + "/" + "user-" + userId;
    }

    public FileInfo getImageFileInfoByMultipart(MultipartFile file) {
        FileInfo fileInfo = convertFromMultipart(file);
        fileInfo.setPath(getFullPath(fileInfo.getStorageFileName()));
        return fileInfo;
    }

    public FileInfo getClothesItemFileInfoByMultipart(MultipartFile file) {
        FileInfo fileInfo = convertFromMultipart(file);
        fileInfo.setPath(getClothesItemFullPath(fileInfo.getStorageFileName()));
        return fileInfo;
    }

    private String getClothesItemFullPath(String storageFileName) {
        return clothesStoragePath + "/" + storageFileName;
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
    public void saveClothesItemToStorage(MultipartFile file, ClothesItem clothesItem) {
        Files.copy(file.getInputStream(), Paths.get(clothesStoragePath, clothesItem.getFileInfo().getStorageFileName()));
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

    @SneakyThrows
    public void writeFileToResponse(Long fileInfoId, HttpServletResponse response) {
        FileInfo fileInfo = fileInfoRepository.findById(fileInfoId).orElseThrow(
                () -> new IllegalArgumentException("FileInfo with id = " + fileInfoId + " not fount")
        );
        response.setContentType(fileInfo.getType());
        InputStream inputStream = new FileInputStream(new File(fileInfo.getPath()));
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

    public FileInfo createUserLookFileInfo(Long userId) {
        String name = "user-look.jpg";
        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(name)
                .storageFileName(createStorageFileName(name))
                .type("image/jpeg")
                .build();
        fileInfo.setPath(getLookImageFullPath(fileInfo.getStorageFileName(), userId));
        return fileInfo;
    }

    private String getLookImageFullPath(String storageFileName, Long userId) {
        return getUserFolderPath(userId) + "/" + storageFileName;
    }

    public void putFileIntoUserDislikedFolder(Long userId, FileInfo fileInfo) {
        moveFileIntoSubfolder("dislike", fileInfo, userId);
    }

    public void putFileIntoUserLikedFolder(Long userId, FileInfo fileInfo) {
        moveFileIntoSubfolder("like", fileInfo, userId);
    }

    @SneakyThrows
    private void moveFileIntoSubfolder(String subfolderName, FileInfo fileInfo, Long userId) {
        String newPath = getUserFolderPath(userId) + "/" + subfolderName + "/" + fileInfo.getStorageFileName();
        Path currentDir = Paths.get(fileInfo.getPath());
        Path newDir = Paths.get(newPath);
        Files.move(currentDir, newDir);
        fileInfo.setPath(newPath);
        fileInfoRepository.save(fileInfo);
    }

}
