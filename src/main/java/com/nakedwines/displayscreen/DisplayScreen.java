package com.nakedwines.displayscreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class DisplayScreen extends SpringBootServletInitializer{
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(DisplayScreen.class);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(DisplayScreen.class, args);
	}
}
