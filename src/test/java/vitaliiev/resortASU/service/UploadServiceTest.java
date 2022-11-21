package vitaliiev.resortASU.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;

import java.io.IOException;

class UploadServiceTest extends ResortApplicationTests {

    private final String filename = "test.txt";

    @Autowired
    private UploadService uploadService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void load() throws IOException {
//        Path path = uploadService.load(this.filename);
//        System.out.println(path);
    }

    @Test
    void loadAsResource() throws IOException {
//        Resource resource = uploadService.loadAsResource(this.filename);
//        System.out.println(resource);
    }
}