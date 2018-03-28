package ru.kpfu.itis.dressmeapp.services.impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.dressmeapp.model.ClassificationAnswer;
import ru.kpfu.itis.dressmeapp.model.FileInfo;
import ru.kpfu.itis.dressmeapp.model.Image;
import ru.kpfu.itis.dressmeapp.repositories.ImageRepository;
import ru.kpfu.itis.dressmeapp.services.FileStorageService;
import ru.kpfu.itis.dressmeapp.services.ImageClassificationService;
import ru.kpfu.itis.dressmeapp.util.DockerUtil;
import ru.kpfu.itis.dressmeapp.util.FileStorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Service
public class ImageClassificationServiceImpl implements ImageClassificationService {
    private static final String containerName = "tensorflow";
    private DockerUtil dockerUtil;
    private ImageRepository imageRepository;
    private FileStorageUtil fileStorageUtil;

    @Value("${advice.slender}")
    private String slenderAdvice;
    @Value("${advice.normal}")
    private String normalAdvice;
    @Value("${advice.overweight}")
    private String overweightAdvice;


    @Autowired
    public ImageClassificationServiceImpl(DockerUtil dockerUtil, ImageRepository imageRepository, FileStorageUtil fileStorageUtil) {
        this.dockerUtil = dockerUtil;
        this.imageRepository = imageRepository;
        this.fileStorageUtil = fileStorageUtil;
    }

    @Override
    public String classify(MultipartFile image) {
        String fileName = saveImage(image).getFileInfo().getStorageFileName();
        String classificationResult = startImageClassification(fileName);
        System.out.println(classificationResult);
        return classificationResult;
    }

    private Image saveImage(MultipartFile imageFile) {
        FileInfo fileInfo = fileStorageUtil.getImageFileInfoByMultipart(imageFile);
        Image image = Image.builder().fileInfo(fileInfo).build();
        imageRepository.save(image);
        fileStorageUtil.saveImageToStorage(imageFile, image);
        return image;
    }

    @Override
    public ClassificationAnswer getAdvicesByResult(String result) {
        String resultType = parseClassificationAnswer(result);
        ClassificationAnswer classificationAnswer = ClassificationAnswer.builder().type(result).build();
        switch (resultType) {
            case "slender":
                classificationAnswer.setAdvices(getListOfAdvice(slenderAdvice));
                break;
            case "normal":
                classificationAnswer.setAdvices(getListOfAdvice(normalAdvice));
                break;
            case "overweight":
                classificationAnswer.setAdvices(getListOfAdvice(overweightAdvice));
                break;
            default:
                classificationAnswer.setAdvices(getListOfAdvice("Sorry, we do not have any advice for you :(\n"));
        }
        return classificationAnswer;
    }

    @SneakyThrows
    private String startImageClassification(String fileName) {
        String[] command = getImageClassificationCommand(fileName);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execDockerCmd(containerName, command, resultStream);
        return new String(resultStream.toByteArray(), "UTF-8");
    }

    private String[] getImageClassificationCommand(String fileName) {
        String command1 = "cd local/";
        String command2 = "python bw-converter.py " + fileName;
        String command3 = "python classify.py bw-" + fileName;
        return new String[]{"sh", "-c", command1 + " && " + command2 + " && " + command3};
    }

    private String parseClassificationAnswer(String result) {
        return result.split(" ")[0];
    }

    private List<String> getListOfAdvice(String advice) {
        return Arrays.asList(advice.split("\n"));
    }
}
