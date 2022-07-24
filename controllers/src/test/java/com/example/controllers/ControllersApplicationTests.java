package com.example.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ControllersApplicationTests {

	@Test
	void contextLoads(ApplicationContext context) {
    Assertions.assertNotNull(context);
	}

}
