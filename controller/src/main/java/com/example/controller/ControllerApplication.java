package com.example.controller;

import io.kubernetes.client.informer.SharedInformerFactory;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;

@Slf4j
@SpringBootApplication
public class ControllerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllerApplication.class, args);
    }


    @Bean
    GenericKubernetesApi<V1Pod, V1PodList> podGenericKubernetesApi(
            ApiClient apiClient) {
        return new GenericKubernetesApi<>(V1Pod.class, V1PodList.class, "",
                "v1", "pods", apiClient);
    }

    @Bean
    ApplicationRunner applicationRunner(
            GenericKubernetesApi<V1Pod, V1PodList> podGenericKubernetesApi) {
        return event -> {
            var response = podGenericKubernetesApi
                    .get("default", "nginx");
            var pod = response.getObject();
            log.info("pod: " + pod );
        };
    }
}
