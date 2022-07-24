package com.example.controllers;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Yaml;
import io.kubernetes.client.util.generic.GenericKubernetesApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ControllersApplication {

  private final ApplicationArguments args;

	public static void main(String[] args) {
		SpringApplication.run(ControllersApplication.class, args);
	}

	@Bean
  GenericKubernetesApi<V1Pod, V1PodList> v1PodV1PodListGenericKubernetesApi(ApiClient apiClient) {
	  return  new GenericKubernetesApi<>(
	    V1Pod.class, V1PodList.class, "", "v1", "pods", apiClient);
  }

	@Bean
  ApplicationListener<ApplicationReadyEvent> runner(GenericKubernetesApi<V1Pod, V1PodList> apis) {
    var namespaces = Optional.ofNullable(args.getOptionValues("namespace")).orElse(List.of("7949-0192-sio-dev-intra"));
    var pods = Optional.ofNullable(args.getOptionValues("pod")).orElse(List.of("grafana-0"));
    log.info("namespaces: {} - pods {}", namespaces, pods);

    return event -> {
      var response = apis.get(namespaces.get(0), pods.get(0));
      Assert.state(response.isSuccess(), () -> "The call to query a pod was not successful");
      var pod = response.getObject();
      log.info("pod: {}", Yaml.dump(pod));

      try {
        var path = Paths.get("tmp" + File.separator
          + Objects.requireNonNull(pod.getMetadata()).getName() + ".yml");
        Files.createDirectories(path.getParent());

        var bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        Yaml.dump(pod, bufferedWriter);
      } catch (IOException e) {
        log.error("File no write: {}", e.getMessage());
      }
    };
  }
}
