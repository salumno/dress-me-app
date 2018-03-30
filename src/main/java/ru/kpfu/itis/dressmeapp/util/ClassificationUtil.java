package ru.kpfu.itis.dressmeapp.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.model.*;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class ClassificationUtil {
    private final String containerName = "tensorflow";

    private final String femaleFacePrefix = "female_face_";
    private final String femaleProfilePrefix = "female_profile_";
    private final String maleFacePrefix = "male_face_";
    private final String maleProfilePrefix = "male_profile_";

    private final String labelsPathSuffix = "output_labels.txt";
    private final String graphPathSuffix = "output_graph.pb";

    private DockerUtil dockerUtil;
    private ExecutorService executorService;

    public ClassificationUtil(DockerUtil dockerUtil) {
        this.dockerUtil = dockerUtil;
        executorService = Executors.newCachedThreadPool();
    }

    @SneakyThrows
    public String startClassification(ClassifiedImageFilenameDuo filenameDuo, String sex) {
        Sex sexEnum = Sex.valueOf(sex);
        Future<String> faceResultFuture = executorService.submit(
                () -> classifyImage(filenameDuo.getFaceFileName(), sexEnum, ImageType.FACE)
        );
        Future<String> profileResultFuture = executorService.submit(
                () -> classifyImage(filenameDuo.getProfileFileName(), sexEnum, ImageType.PROFILE)
        );
        String faceResult = faceResultFuture.get();
        String profileResult = profileResultFuture.get();
        System.out.println("Face classification result: " + faceResult);
        System.out.println("Profile classification result: " + profileResult);
        return resultClassification(faceResult, profileResult, sexEnum);
    }

    @SneakyThrows
    private String classifyImage(String filename, Sex sex, ImageType type) {
        String[] command = getImageClassificationCommand(filename, sex, type);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execDockerCmd(containerName, command, resultStream);
        String rawResult =  new String(resultStream.toByteArray(), "UTF-8");
        return parseClassificationAnswer(rawResult);
    }

    private String resultClassification(String faceResult, String profileResult, Sex sex) {
        if (sex.equals(Sex.MALE)) {
            return resultMaleClassification(faceResult, profileResult);
        } else {
            return resultFemaleClassification(faceResult, profileResult);
        }
    }

    private String resultFemaleClassification(String faceResult, String profileResult) {
        ProfileBodyType profile = ProfileBodyType.valueOf(profileResult);
        FaceFemaleBodyType face = FaceFemaleBodyType.valueOf(faceResult);
        if (profile.equals(ProfileBodyType.THIN)) {

            if (face.equals(FaceFemaleBodyType.APPLE) || face.equals(FaceFemaleBodyType.PEAR)) {
                throw new IllegalArgumentException("Invalid type. Try another photos.");
            } else {
                return face.toString();
            }

        } else if (profile.equals(ProfileBodyType.AVERAGE)) {

            return face.toString();

        } else if (profile.equals(ProfileBodyType.OVERWEIGHT)) {

            if (face.equals(FaceFemaleBodyType.INVERTED)) {
                return FaceFemaleBodyType.APPLE.toString();
            } else {
                return face.toString();
            }

        } else {
            throw new IllegalArgumentException("Invalid profile body type value");
        }
    }

    private String resultMaleClassification(String faceResult, String profileResult) {
        ProfileBodyType profile = ProfileBodyType.valueOf(profileResult);
        FaceMaleBodyType face = FaceMaleBodyType.valueOf(faceResult);
        if (profile.equals(ProfileBodyType.THIN)) {

            if (face.equals(FaceMaleBodyType.TRIANGLE) || face.equals(FaceMaleBodyType.OVAL)) {
                throw new IllegalArgumentException("Invalid type. Try another photos.");
            } else {
                return face.toString();
            }

        } else if (profile.equals(ProfileBodyType.AVERAGE)) {

            return face.toString();

        } else if (profile.equals(ProfileBodyType.OVERWEIGHT)) {

            if (face.equals(FaceMaleBodyType.INVERTED)) {
                return FaceMaleBodyType.OVAL.toString();
            } else {
                return face.toString();
            }

        } else {
            throw new IllegalArgumentException("Invalid profile body type value");
        }
    }

    private String[] getImageClassificationCommand(String fileName, Sex sex, ImageType type) {
        String command1 = "cd local/";
        String command2 = "python bw-converter.py " + fileName;
        String command3 = detectRightClassifier(fileName, sex, type);
        return new String[]{"sh", "-c", command1 + " && " + command2 + " && " + command3};
    }

    private String detectRightClassifier(String fileName, Sex sex, ImageType type) {
        String prefix = "";
        if (sex.equals(Sex.FEMALE)) {
            if (ImageType.FACE.equals(type)) {
                prefix = femaleFacePrefix;
            } else {
                prefix = femaleProfilePrefix;
            }
        } else if (sex.equals(Sex.MALE)) {
            if (ImageType.FACE.equals(type)) {
                prefix = maleFacePrefix;
            } else {
                prefix = maleProfilePrefix;
            }
        }
        String graphPath = prefix + graphPathSuffix;
        String labelsPath = prefix + labelsPathSuffix;
        return "python classify.py bw-" + fileName + " " + graphPath + " " + labelsPath;
    }

    private String parseClassificationAnswer(String result) {
        return result.split(" ")[0].toUpperCase();
    }
}
