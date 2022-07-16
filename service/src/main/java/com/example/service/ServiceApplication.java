package com.example.service;

import lombok.RequiredArgsConstructor;
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

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Bean
    ApplicationListener<AvailabilityChangeEvent<?>> availabilityChangeEventApplicationListener() {
        return event -> System.out.println(event.getResolvableType() + ":" + event.getState());
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener(CustomerRepository customerRepository) {
        return event -> Flux.just("Yuxin", "Zhouyue", "Mùchén", "Ruòxī")
                .map(name -> new Customer(null, name))
                .flatMap(customerRepository::save)
                .subscribe(System.out::println);
    }

}

@Controller
@ResponseBody
@RequiredArgsConstructor
class AvailabilityHttpController {

    private final ApplicationContext context;

    @GetMapping("/down")
    void down() {
        AvailabilityChangeEvent.publish(this.context, LivenessState.BROKEN);
    }

    @GetMapping("/slow")
    void slow() throws Exception {
        Thread.sleep(10_000);
    }
}

@Controller
@ResponseBody
@RequiredArgsConstructor
class CustomerHttpController {

    private final CustomerRepository repository;

    @GetMapping("/customers")
    Flux<Customer> get() {
        return this.repository.findAll();
    }
}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

record Customer(@Id Integer id, String name) {
}