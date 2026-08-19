package com.jobportal;

import org.springframework.boot.SpringApplication;

public class TestJobportalApplication {

	public static void main(String[] args) {
		SpringApplication.from(JobportalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
