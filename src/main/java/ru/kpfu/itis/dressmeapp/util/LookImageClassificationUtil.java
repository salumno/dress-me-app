package ru.kpfu.itis.dressmeapp.util;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.model.ClassificationWaiting;
import ru.kpfu.itis.dressmeapp.model.LookImage;
import ru.kpfu.itis.dressmeapp.model.User;
import ru.kpfu.itis.dressmeapp.services.ClassificationWaitingService;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class LookImageClassificationUtil {
    private DockerUtil dockerUtil;
    private ExecutorService executorService;
    private ClassificationWaitingService classificationWaitingService;

    public LookImageClassificationUtil(DockerUtil dockerUtil, ClassificationWaitingService classificationWaitingService) {
        this.dockerUtil = dockerUtil;
        this.classificationWaitingService = classificationWaitingService;
        executorService = Executors.newCachedThreadPool();
    }

    @SneakyThrows
    public void retrainUserNetwork(User user) {
        ClassificationWaiting cw = classificationWaitingService.createWaitingNote(user);
        Long userId = user.getId();
        Future<Boolean> retrain = executorService.submit(
                () -> startRetrain(userId, cw)
        );
    }

    @SneakyThrows
    public boolean isLookLikable(LookImage lookImage, User user) {
        Future<String> classify = executorService.submit(
                () -> startClassification(lookImage, user)
        );
        String rawClassificationResult  = classify.get();
        String[] resultParts = rawClassificationResult.split(" ");
        return analiseLikableResult(resultParts[0], resultParts[1]);
    }

    private Boolean startRetrain(Long userId, ClassificationWaiting cw) {
        String[] command = getRetrainCommands(userId);
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execTensorflowContainerCmd(command, resultStream);
        classificationWaitingService.setStatusDone(cw);
        return true;
    }

    @SneakyThrows
    private String startClassification(LookImage lookImage, User user) {
        String[] command = getClassifyCommands(user.getId(), lookImage.getFileInfo().getStorageFileName());
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        dockerUtil.execTensorflowContainerCmd(command, resultStream);
        return new String(resultStream.toByteArray(), "UTF-8");
    }

    private boolean analiseLikableResult(String type, String probability) {
        return type.equals("like") && Double.valueOf(probability) > 0.7;
    }

    private String[] getRetrainCommands(Long userId) {
        String command1 = "cd local/";
        String dataContainer = "user-" + userId;
        String graph = "user_" + userId + "_output_graph.pb";
        String labels = "user_" + userId + "_output_labels.txt";
        String command2 = "python retrain.py --image_dir " + dataContainer + " --output_graph " + graph + " --output_labels " + labels;
        return dockerUtil.createExecCommands(command1, command2);
    }

    private String[] getClassifyCommands(Long userId, String filename) {
        String command1 = "cd local/";
        String dataContainer = "user-" + userId;
        String graph = "user_" + userId + "_output_graph.pb";
        String labels = "user_" + userId + "_output_labels.txt";
        String command2 = "python classify.py " + dataContainer + "/" + filename + " " + graph + " " + labels;
        return dockerUtil.createExecCommands(command1, command2);
    }
}
