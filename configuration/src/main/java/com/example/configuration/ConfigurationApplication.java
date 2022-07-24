package com.example.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnCloudPlatform;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ConfigurationApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConfigurationApplication.class, args);
  }

  private final Environment environment;

  @EventListener({ApplicationReadyEvent.class, RefreshScopeRefreshedEvent.class})
  public void begin() {
    log.info("The message is {}", this.environment.getProperty("message"));
  }

  @Bean
  @ConditionalOnCloudPlatform(CloudPlatform.KUBERNETES)
  ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener () {
    return args -> log.info("The application is running on K8S!");
  }
}
