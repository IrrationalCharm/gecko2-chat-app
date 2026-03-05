package eu.irrationalcharm.messaging_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;


@EnableKafka
@EnableCaching
@EnableWebSecurity
@EnableFeignClients
@SpringBootApplication
//@EnableWebSocketSecurity
@EnableWebSocketMessageBroker
public class MessagingServiceApplication implements CommandLineRunner {

	static void main(String[] args) {
		SpringApplication.run(MessagingServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("last version 2");
	}
}
