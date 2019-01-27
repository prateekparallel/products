package com.kela.common.config;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties
public class InitConfig {
	@Autowired
	private Environment env;
	
	@PostConstruct
	   public void init(){
	     initConfig();
	     System.out.println("Initialized");
	   }		
	@Bean
	public static ApplicationConfig applicationConfig(){
		return ApplicationConfig.getInstance();
	}
	
	public InitConfig(){
	}
	
	public void initConfig() {
		
		System.out.println("initConfig - CommonConfig");
		String logPropertyFilePath = env.getProperty("LOG_PROPERTY_FILE_PATH");
		System.out.println(logPropertyFilePath);
		ApplicationConfig ac = ApplicationConfig.getInstance();
		ac.setHttpsUrl600001506(env.getProperty("https.server.url.600001506"));
		if (logPropertyFilePath != null) {
			System.out.println("Loading log properties file: "
					+ logPropertyFilePath);
			PropertyConfigurator.configure(logPropertyFilePath);
		} else {
			System.out.println("Called BasicConfigurator.configure()");
			BasicConfigurator.configure();
		}
	}

}

