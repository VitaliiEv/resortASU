package vitaliiev.resortASU.service.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.service.suit.BedsService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.util.Comparator;
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
    //      2b. if no free suits found - drop combination

    /**
     * Finds one optimal combination of beds from sorted descending beds list
     * In case if there can be several combinations of suits it will first search
     * for suits with bigger quantity of adult beds, then with bigger quantity of child beds
     *
     * For example, we need to search for 3 adult beds and 1 child bed.
     * This algorithm will give you suits: 2a+1c, 1a+0c
     * and it will never give you suits: 1a+1c, 2a+0c;
     */
    public BedsQuantityCombination findPossibleBedsCombinations(int bedsAdult, int bedsChild) {
        // sort beds by overall bed number from max to min
        Set<Beds> beds = this.bedsService.findDistinctByBedsQuantity(this.bedsService.findAll());
        Comparator<Beds> descBedQuantityComparator = Comparator.comparing(Beds::getBeds_adult,
                        Comparator.reverseOrder())
                .thenComparing(Beds::getBeds_child, Comparator.reverseOrder());
        List<Beds> sortedBeds = this.bedsService.getSortedBeds(beds, descBedQuantityComparator);

        // calculate minimum and maximum suits needed for this search
        int maxAdultBeds = sortedBeds.get(0).getBeds_adult();
        int maxChildBeds = sortedBeds.get(0).getBeds_child();
        int minAdultBeds = sortedBeds.get(beds.size() - 1).getBeds_adult();
        int minChildBeds = sortedBeds.get(beds.size() - 1).getBeds_child();

        int minSuits = (int) Math.ceil(Math.max(maxChildBeds == 0 ? 0 : (double) bedsChild / maxChildBeds,
                maxAdultBeds == 0 ? 0 : (double) bedsAdult / maxAdultBeds));
        int maxSuits = (int) Math.ceil(Math.max(minChildBeds == 0 ? 0 : (double) bedsChild / minChildBeds,
                minAdultBeds == 0 ? 0 : (double) bedsAdult / minAdultBeds));

        for (int i = minSuits; i <= maxSuits; i++) {
            BedsQuantityCombination result = findBedCombination(bedsAdult, bedsChild, i, sortedBeds);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public BedsQuantityCombination findBedCombination(int bedsAdult, int bedsChild, int suits, List<Beds> possibleBedsQuantities) {
        BedsQuantityCombination bedsQuantityCombination = new BedsQuantityCombination(bedsAdult, bedsChild, suits);
        for (Beds bed : possibleBedsQuantities) {
            while (bedsQuantityCombination.bedCanBeAdded(bed)) {
                bedsQuantityCombination.add(bed);
                if (bedsQuantityCombination.isCompleted()) {
                    return bedsQuantityCombination;
                }
            }
        }
        return null;
    }

    public BedsSearchRequest formBedsSearchRequest(int bedsAdult, int bedsChild) {
        BedsQuantityCombination bedsQuantityCombination = findPossibleBedsCombinations(bedsAdult, bedsChild);
        BedsSearchRequest bedsSearchRequest = new BedsSearchRequest(bedsQuantityCombination, this.bedsService.findAll());
        bedsSearchRequest.formSearchRequest();
        return bedsSearchRequest;
    }

}
