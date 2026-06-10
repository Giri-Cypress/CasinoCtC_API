package com.CasinoCtC.CCtCAPI.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabasePasswordPrinter implements CommandLineRunner {

	@Value("${spring.datasource.password:NOT_FOUND}")
	private String dbPassword;
	
	@Value("${spring.datasource.url:NOT_FOUND}")
	private String dbUrl;
	
	@Override
	public void run(String... args) throws Exception {
	System.out.println("=========================================");
	System.out.println("RESOLVED DB URL: " + dbUrl);
	System.out.println("RESOLVED DB PASSWORD: [" + dbPassword + "]");
	System.out.println("=========================================");
	}
}
