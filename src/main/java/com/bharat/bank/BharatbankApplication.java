package com.bharat.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BharatbankApplication {


	public static void main(String[] args) {
		SpringApplication.run(BharatbankApplication.class, args);
	}



}
