package ru.kpfu.itis.dressmeapp.util;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class DockerUtil {

    private final String TENSORFLOW_CONTAINER_NAME = "tensorflow";

    @Autowired
    private DockerClient docker;

    public void execTensorflowContainerCmd(String[] command, ByteArrayOutputStream resultStream) {
        execDockerCmd(TENSORFLOW_CONTAINER_NAME, command, resultStream);
    }

    public String[] createExecCommands(String... commands) {
        String[] result = new String[3];
        result[0] = "sh";
        result[1] = "-c";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            sb.append(commands[i]);
            if (i != commands.length - 1) {
                sb.append(" && ");
            }
        }
        result[2] = sb.toString();
        return result;
    }

    private void execDockerCmd(String containerName, String[] command, ByteArrayOutputStream resultStream) {
        try {
            String containerId = getContainersByName(containerName).get(0).getId();
            ExecCreateCmdResponse execCreateCmdResponse = docker.execCreateCmd(containerId)
                    .withCmd(command)
                    .withAttachStdout(true)
                    .exec();
            docker.execStartCmd(execCreateCmdResponse.getId())
                    .withTty(true)
                    .withStdIn(System.in)
                    .exec(new ExecStartResultCallback(resultStream, System.err))
                    .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private List<Container> getContainersByName(String containerName) {
        final List<Container> containers = docker.listContainersCmd().exec();
        return containers.stream()
                .filter(x -> x.getNames()[0].contains(containerName))
                .collect(Collectors.toList());
    }
}
