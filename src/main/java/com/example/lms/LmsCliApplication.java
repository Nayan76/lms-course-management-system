package com.example.lms;

import com.example.lms.runner.CommandProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class LmsCliApplication {

	public static void main(String[] args) {
		SpringApplication.run(LmsCliApplication.class, args);
	}

    @Bean
    @Profile("!test") // exclude during test
    CommandLineRunner runner(CommandProcessor processor) {
        return args -> processor.processStdin(); // reads stdin until EOF
    }

}
