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

    @Autowired
    private DockerClient docker;

    public void execDockerCmd(String containerName, String[] command, ByteArrayOutputStream resultStream) {
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
