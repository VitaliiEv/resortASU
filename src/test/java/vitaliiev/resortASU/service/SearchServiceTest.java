package vitaliiev.resortASU.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;

class SearchServiceTest extends ResortApplicationTests {

    @Autowired
    private SearchService searchService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getPossibleBedsCombinations() {
        searchService.getPossibleBedsCombinations(5,2);
    }
}