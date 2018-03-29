package ru.kpfu.itis.dressmeapp.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.model.*;

import java.io.ByteArrayOutputStream;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class ClassificationUtil {
    private final String containerName = "tensorflow";

    private final String femaleFaceClassifier = "female-face-classify.py";
    private final String femaleProfileClassifier = "female-profile-classify.py";
    private final String maleFaceClassifier = "male-face-classify.py";
    private final String maleProfileClassifier = "male-profile-classify.py";

    private DockerUtil dockerUtil;

    public ClassificationUtil(DockerUtil dockerUtil) {
        this.dockerUtil = dockerUtil;
    }

    public String startClassification(ClassifiedImageFilenameDuo filenameDuo, String sex) {
        Sex sexEnum = Sex.valueOf(sex);
        String faceResult = classifyImage(filenameDuo.getFaceFileName(), sexEnum, ImageType.FACE);
        String profileResult = classifyImage(filenameDuo.getProfileFileName(), sexEnum, ImageType.PROFILE);
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

            if (face.equals(FaceFemaleBodyType.INVERTED_TRIANGLE)) {
                throw new IllegalArgumentException("Invalid type. Try another photos.");
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

            if (face.equals(FaceMaleBodyType.INVERTED_TRIANGLE)) {
                throw new IllegalArgumentException("Invalid type. Try another photos.");
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
        String classifierName = "";
        if (sex.equals(Sex.FEMALE)) {
            if (ImageType.FACE.equals(type)) {
                classifierName = femaleFaceClassifier;
            } else {
                classifierName = femaleProfileClassifier;
            }
        } else if (sex.equals(Sex.MALE)) {
            if (ImageType.FACE.equals(type)) {
                classifierName = maleFaceClassifier;
            } else {
                classifierName = maleProfileClassifier;
            }
        }
        return "python " + classifierName + " bw-" + fileName;
    }

    private String parseClassificationAnswer(String result) {
        return result.split(" ")[0].toUpperCase();
    }
}
