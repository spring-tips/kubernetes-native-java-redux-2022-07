package com.example.controllers;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

@Slf4j
@SpringBootApplication
public class ControllersApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControllersApplication.class, args);
    }

    @Bean
    GenericKubernetesApi<V1Pod, V1PodList> v1PodV1PodListGenericKubernetesApi(ApiClient apiClient) {
        return new GenericKubernetesApi<>(
                V1Pod.class, V1PodList.class, "", "v1", "pods", apiClient);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> runner(
            GenericKubernetesApi<V1Pod, V1PodList> apis
    ) {
        return event -> {
            var response = apis.get("default", "nginx");
            Assert.state(response.isSuccess(), () -> "the call to query a Pod was not successful");
            var pod = response.getObject();
            log.info("pod: " + pod.toString());
        };
    }

}
