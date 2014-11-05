package com.nakedwines.displayscreen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jakewharton.pingdom.ServiceManager;

@Configuration
public class PingdomConfig {
	@Value("${PINGDOM_USERNAME}")
	private String pingdomUsername;
	@Value("${PINGDOM_PASSWORD}")
	private String pingdomPassword;
	@Value("${PINGDOM_API_KEY}")
	private String pingdomApiKey;
	
	@Bean
	public ServiceManager serviceManager(){
		ServiceManager pingdomServiceManager = new ServiceManager();
		pingdomServiceManager.setAuthentication(pingdomUsername,pingdomPassword);
		pingdomServiceManager.setAppKey(pingdomApiKey);
		return pingdomServiceManager;
	}
}
