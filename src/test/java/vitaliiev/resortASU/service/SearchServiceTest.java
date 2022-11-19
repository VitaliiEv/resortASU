package vitaliiev.resortASU.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.dto.SuitSearchRequest;
import vitaliiev.resortASU.dto.SuitSearchResultSet;
import vitaliiev.resortASU.service.search.SearchService;

import java.sql.Date;
import java.util.List;

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
    void find() {
        List<SuitSearchResultSet> result1 = searchService.find(new SuitSearchRequest(1, 0,
                Date.valueOf("2022-11-19"), Date.valueOf("2022-11-20"), null));
        List<SuitSearchResultSet> result2 = searchService.find(new SuitSearchRequest(2, 0,
                Date.valueOf("2022-11-19"), Date.valueOf("2022-11-20"), null));
        System.out.println("finished");
    }

    @Test
    void getPossibleBedsCombinations() {
        System.out.println(searchService.findPossibleBedsCombinations(1, 0));
        System.out.println(searchService.findPossibleBedsCombinations(5, 2));
        System.out.println(searchService.findPossibleBedsCombinations(10, 4));
    }


    @Test
    void formBedsSearchRequest() {
        assertEquals(searchService.formBedsSearchRequest(1, 0).getBedsCombinations().size(), 1);
        assertEquals(searchService.formBedsSearchRequest(2, 1).getBedsCombinations().size(), 2);
        assertEquals(searchService.formBedsSearchRequest(4, 1).getBedsCombinations().size(), 4);
    }

}