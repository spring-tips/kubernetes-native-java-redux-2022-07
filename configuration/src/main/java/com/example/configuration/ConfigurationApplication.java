package com.example.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@RequiredArgsConstructor
public class ConfigurationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationApplication.class, args);
    }

    private final Environment environment;

    @EventListener({RefreshScopeRefreshedEvent.class, ApplicationReadyEvent.class})
    public void handleRefresh() {
        System.out.println("the message is " + this.environment.getProperty("message"));
    }

}

@RefreshScope
@Controller
@ResponseBody
class MessageHttpController {

    private final String message;

    MessageHttpController(@Value("${message}") String message) {
        this.message = message;
    }

    @GetMapping("/message")
    String message() {
        return this.message;
    }
}


