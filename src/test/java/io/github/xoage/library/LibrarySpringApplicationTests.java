package io.github.xoage.library;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "DB_PASSWORD=unused")
@Import(TestcontainersConfiguration.class)
class LibrarySpringApplicationTests {

    @Test
    void contextLoads() {
    }
}
