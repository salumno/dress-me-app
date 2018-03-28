package ru.kpfu.itis.dressmeapp.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient dockerClient() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();

        return DockerClientBuilder.getInstance(dockerClientConfig).build();
    }
}
