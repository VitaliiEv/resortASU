package vitaliiev.resortASU.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.service.suit.BedsService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SearchService {

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("suitClass", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("beds", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("area", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("currentprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("minimumprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("mainphoto", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("deleted", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "lastchanged", "features", "services");

    private final SuitTypeService suitTypeService;
    private final BedsService bedsService;

    @Autowired
    public SearchService(SuitTypeService suitTypeService, BedsService bedsService) {
        this.suitTypeService = suitTypeService;
        this.bedsService = bedsService;
    }
    // search steps
    // 1. find acceptable beds combinations
    // 2. for each bed combination
    //      2a. for each bed check if there are free suits
    //      2b. if no free siuts found - drop combination

    public List<Set<Beds>> getPossibleBedsCombinations(int bedsAdult, int bedsChild) {
        HashMap<Beds, Integer> bedCombination = new HashMap<>();
        List<Beds> allBeds = bedsService.findAll();
        int maxAdultBeds = allBeds.stream()
                .map(Beds::getBeds_adult)
                .max(Integer::compareTo)
                .orElseThrow();
        int maxChildBeds = allBeds.stream()
                .map(Beds::getBeds_child)
                .max(Integer::compareTo)
                .orElseThrow();
        // min numbers
        double minSuits = Math.max((double) bedsChild / maxChildBeds, (double) bedsAdult / maxAdultBeds);
        BigDecimal minSuitsDecimal = new BigDecimal(minSuits, new MathContext(3, RoundingMode.UP));

        // sort beds by overall bed number
        allBeds.sort(Comparator.comparing(Beds::getBeds_adult, Comparator.reverseOrder())
                .thenComparing(Beds::getBeds_child, Comparator.reverseOrder()));
//        int adultBedsLeft = bedCombination.keySet().stream().mapToInt(Beds::getBeds_adult).sum();
        int adultBedsLeft = maxAdultBeds;
        int childBedsLeft = maxChildBeds;
        while (adultBedsLeft > 0 && childBedsLeft > 0) {
            for (Beds bed : allBeds) {
                if (adultBedsLeft - bed.getBeds_adult() >= 0 && childBedsLeft - bed.getBeds_child() >= 0) {
                    // fixme stopped here
                }
            }
        }

            // for each beds:
//        set left beds vars
            //for each bedType - add to hash map.
            // calculate left beds. if left beds <0 - try next bed type
            return null;
    }
    public List<Set<Beds>> getPossibleBedsCombinations(int bedsAdult, int bedsChild, int suits) {
        return null;
    }

}
