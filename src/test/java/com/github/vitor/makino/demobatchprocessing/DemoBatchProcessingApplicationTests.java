package com.github.vitor.makino.demobatchprocessing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.github.vitormakino.demobatchprocessing.DemoBatchProcessingApplication;

@Import(TestConfig.class)
@SpringBootTest(classes = DemoBatchProcessingApplication.class)
class DemoBatchProcessingApplicationTests {

	@Test
	void contextLoads() {
	}

}
