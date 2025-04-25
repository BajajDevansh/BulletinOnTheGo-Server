package com.codecatalyst.BulletinOnTheGo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(
		scanBasePackages = "com.codecatalyst.BulletinOnTheGo"
)

public class BulletinOnTheGoApplication {
	private static final Logger log=LoggerFactory.getLogger(BulletinOnTheGoApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(BulletinOnTheGoApplication.class, args);
	}

}
