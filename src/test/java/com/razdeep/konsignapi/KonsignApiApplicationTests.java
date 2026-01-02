package com.razdeep.konsignapi;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("ci")
class KonsignApiApplicationTests {

    /*
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.24"))
            .withUsername("root")
            .withPassword("")
            .withDatabaseName("konsign");
    @DynamicPropertySource
    static void property(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @Test
    void contextLoads() {

    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }
    */

}
