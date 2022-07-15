package com.example.edge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
@SpringBootApplication
public class EdgeApplication {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EdgeApplication.class, args);
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder
                .routes()
                .route(rs -> rs
                        .path("/proxy")
                        .filters(fs -> fs.setPath("/customers")
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                        .uri("http://localhost:8080/")
                )
                .build();
    }

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @EventListener({ApplicationReadyEvent.class, RefreshScopeRefreshedEvent.class})
    public void refresh() {
        log.info("message is " + environment.getProperty("message"));
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class MessageHttpController {

    private final Environment environment;

    @GetMapping("/message")
    String get() {
        return environment.getProperty("message");
    }
}

@Controller
@RequiredArgsConstructor
class CustomerGraphqlController {

    private final WebClient http;

    @QueryMapping
    Flux<Customer> customers() {
        return this.http.get().uri("http://localhost:8080/customers")
                .retrieve()
                .bodyToFlux(Customer.class);
    }

    @SchemaMapping(typeName = "Customer")
    Profile profile(Customer customer) {
        return new Profile(customer.id());
    }
}

record Profile(Integer id) {
}

record Customer(Integer id, String name) {
}