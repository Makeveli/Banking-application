package com.bharat.bank;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.enums.NotificationType;
import com.bharat.bank.notification.dtos.NotificationDTO;
import com.bharat.bank.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BharatbankApplication {


	public static void main(String[] args) {
		SpringApplication.run(BharatbankApplication.class, args);
	}



}
