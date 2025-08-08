package com.apex.firefighter;

import com.apex.firefighter.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Import(TestConfig.class)
class FireFighterAccessManagementApplicationTests {

	@Test
	void contextLoads() {
	}

}
