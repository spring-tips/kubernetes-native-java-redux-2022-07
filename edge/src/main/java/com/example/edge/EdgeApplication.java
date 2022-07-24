package com.example.edge;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class EdgeApplication {

  public static void main(String[] args) {
    SpringApplication.run(EdgeApplication.class, args);
  }

  @Bean
  RouteLocator gateway(RouteLocatorBuilder rlb) {
    return rlb.routes()
      .route(rs -> rs.path("/proxy")
        .filters(fs -> fs
          .setPath("/customers")
          .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
          .retry(10))
        .uri("http://localhost:8080")
      ).build();
  }

  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder.build();
  }
}


@Controller
@RequiredArgsConstructor
class CustomerHttpController {
  private final WebClient http;

  @SchemaMapping(typeName = "Customer")
  Profile profile(Customer customer) {
    return new Profile(customer.id());
  }

  @QueryMapping
  Flux<Customer> customers() {
    return this.http.get().uri("http://localhost:8080/customers").retrieve().bodyToFlux(Customer.class);
  }
}

record Profile(Integer id) {}
record Customer(Integer id, String name) {}