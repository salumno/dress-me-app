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
public class BodyShapeClassificationUtil {
    private final String FEMALE_FACE_PREFIX = "female_face_";
    private final String FEMALE_PROFILE_PREFIX = "female_profile_";
    private final String MALE_FACE_PREFIX = "male_face_";
    private final String MALE_PROFILE_PREFIX = "male_profile_";

    private final String LABELS_PATH_SUFFIX = "output_labels.txt";
    private final String GRAPH_PATH_SUFFIX = "output_graph.pb";

    public final static String CLASSIFICATION_COMPLETELY_FAILED = "failed";
    public final static String CLASSIFICATION_DONT_HAVE_RESULT = "no-result";

    private DockerUtil dockerUtil;
    private ExecutorService executorService;

    public BodyShapeClassificationUtil(DockerUtil dockerUtil) {
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
        System.out.println("Classification started");
        String[] command = getImageClassificationCommand(filename, sex, type);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execTensorflowContainerCmd(command, resultStream);
        String rawResult =  new String(resultStream.toByteArray(), "UTF-8");
        return classificationAnswer(rawResult);
    }

    private String resultClassification(String faceResult, String profileResult, Sex sex) {
        if (faceResult.equals(CLASSIFICATION_COMPLETELY_FAILED) || profileResult.equals(CLASSIFICATION_COMPLETELY_FAILED)) {
            return CLASSIFICATION_COMPLETELY_FAILED;
        } else if (faceResult.equals(CLASSIFICATION_DONT_HAVE_RESULT) || profileResult.equals(CLASSIFICATION_DONT_HAVE_RESULT)) {
            return CLASSIFICATION_DONT_HAVE_RESULT;
        }
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
                return CLASSIFICATION_DONT_HAVE_RESULT;
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
            return CLASSIFICATION_COMPLETELY_FAILED;
        }
    }

    private String resultMaleClassification(String faceResult, String profileResult) {
        ProfileBodyType profile = ProfileBodyType.valueOf(profileResult);
        FaceMaleBodyType face = FaceMaleBodyType.valueOf(faceResult);
        if (profile.equals(ProfileBodyType.THIN)) {

            if (face.equals(FaceMaleBodyType.TRIANGLE) || face.equals(FaceMaleBodyType.OVAL)) {
                return CLASSIFICATION_DONT_HAVE_RESULT;
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
            return CLASSIFICATION_COMPLETELY_FAILED;
        }
    }

    private String[] getImageClassificationCommand(String fileName, Sex sex, ImageType type) {
        String command1 = "cd local/";
        String command2 = "python bw-converter.py " + fileName;
        String command3 = detectRightClassifier(fileName, sex, type);
        return dockerUtil.createExecCommands(command1, command2, command3);
    }

    private String detectRightClassifier(String fileName, Sex sex, ImageType type) {
        String prefix = "";
        if (sex.equals(Sex.FEMALE)) {
            if (ImageType.FACE.equals(type)) {
                prefix = FEMALE_FACE_PREFIX;
            } else {
                prefix = FEMALE_PROFILE_PREFIX;
            }
        } else if (sex.equals(Sex.MALE)) {
            if (ImageType.FACE.equals(type)) {
                prefix = MALE_FACE_PREFIX;
            } else {
                prefix = MALE_PROFILE_PREFIX;
            }
        }
        String graphPath = prefix + GRAPH_PATH_SUFFIX;
        String labelsPath = prefix + LABELS_PATH_SUFFIX;
        return "python classify.py bw-" + fileName + " " + graphPath + " " + labelsPath;
    }

    private String classificationAnswer(String result) {
        String[] parsedResult = result.split(" ");
        String type = parsedResult[0].toUpperCase();
        Double probability  = Double.parseDouble(parsedResult[1]);
        System.out.println("Probability: " + probability);
        if (probability >= 0.75) {
            return type;
        } else if (probability >= 0.50 && probability < 0.75) {
            return CLASSIFICATION_DONT_HAVE_RESULT;
        } else {
            return CLASSIFICATION_COMPLETELY_FAILED;
        }
    }
}
