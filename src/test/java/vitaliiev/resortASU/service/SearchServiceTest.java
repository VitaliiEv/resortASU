package vitaliiev.resortASU.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.service.search.SearchService;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        System.out.println(searchService.findPossibleBedsCombinations(1,0));
        System.out.println(searchService.findPossibleBedsCombinations(5,2));
        System.out.println(searchService.findPossibleBedsCombinations(10,4));
    }


    @Test
    void formBedsSearchRequest() {
        assertEquals(searchService.formBedsSearchRequest(1,0).getBedsCombinations().size(), 1);
        assertEquals(searchService.formBedsSearchRequest(2,1).getBedsCombinations().size(), 2);
        assertEquals(searchService.formBedsSearchRequest(4,1).getBedsCombinations().size(), 4);
    }
}