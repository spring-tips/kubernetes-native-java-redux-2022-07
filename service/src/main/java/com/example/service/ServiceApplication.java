package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.Objects;

@Slf4j
@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> log.info(event.getState() + ":" + Objects.requireNonNull(event.getResolvableType()));
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(CustomerRepository customerRepository) {
        return event -> Flux.just("Yuxin", "Zhouyue", "Mùchén", "Ruòxī")
                .map(name -> new Customer(null, name))
                .flatMap(customerRepository::save)
                .subscribe(l -> log.info(l.toString()));
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class AvailabilityHttpController {

    private final ApplicationContext context;

    @GetMapping("/slow")
    void sleep() throws Exception {
        Thread.sleep(20_000);
    }

    @GetMapping("/down")
    void down() {
        AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class CustomerHttpController {

    private final CustomerRepository customerRepository;

    @GetMapping("/customers")
    Flux<Customer> customers() {
        return this.customerRepository.findAll();
    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}
