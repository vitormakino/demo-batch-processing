package com.github.vitor.makino.demobatchprocessing;

import org.springframework.boot.SpringApplication;

import com.github.vitormakino.demobatchprocessing.DemoBatchProcessingApplication;

public class LocalDevDemoBatchProcessingApplication {

	public static void main(String[] args) {
		SpringApplication
		  .from(DemoBatchProcessingApplication::main)
		  .with(TestConfig.class)
		  .run(args);
	}

}
