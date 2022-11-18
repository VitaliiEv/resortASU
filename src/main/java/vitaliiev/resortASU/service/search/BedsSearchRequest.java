package vitaliiev.resortASU.service.search;


import vitaliiev.resortASU.model.suit.Beds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Object that holds valid search request and search result for each Beds object
 */

public class BedsSearchRequest {

    /**
     * Initial beds quantity combination
     */
    private final BedsQuantityCombination bedsQuantityCombination;
    /**
     * All possible combinations of beds
     */
    private final List<Beds> beds;
    /**
     * List that hold all possible beds combinations of beds objects
     */
    private List<Map<Beds, Integer>> bedsCombinations;

    private boolean ready = false;


    public BedsSearchRequest(BedsQuantityCombination bedsQuantityCombination, List<Beds> beds) {
        if (bedsQuantityCombination == null) {
            throw new IllegalArgumentException("Empty search set");
        }
        if (beds == null || beds.isEmpty()) {
            throw new IllegalArgumentException("Empty beds list");
        }
        this.bedsQuantityCombination = bedsQuantityCombination;
        this.beds = beds;
    }

    /**
     * Form list of all possible bed combinations based on matrix multiplication principle
     */
    public void formSearchRequest() {
        List<Map<Beds, Integer>> combinations = new ArrayList<>();
        Map<Beds, Integer> initialCombination = this.bedsQuantityCombination.getBedsCombinations();
        combinations.add(initialCombination);
        for (Beds bedQuantity : initialCombination.keySet()) {
            List<Map<Beds, Integer>> newCombinations = new ArrayList<>();
            for (Map<Beds, Integer> oldCombination : combinations) {
                if (oldCombination.containsKey(bedQuantity)) {
                    List<Beds> equalBeds = getBedsWithEqualQuantity(bedQuantity);
                    for (Beds beds : equalBeds) {
                        Map<Beds, Integer> newCombination = new HashMap<>(oldCombination);
                        newCombination.put(beds, oldCombination.get(bedQuantity));
                        newCombination.remove(bedQuantity);
                        newCombinations.add(newCombination);
                    }
                } else {
                    newCombinations.add(oldCombination);
                }
            }
            combinations = newCombinations;
        }
        this.bedsCombinations = combinations;
        this.ready = true;
    }

    private List<Beds> getBedsWithEqualQuantity(Beds bed) {
        return this.beds.stream()
                .filter(b -> (b.getBeds_adult().equals(bed.getBeds_adult()) &&
                        b.getBeds_child().equals(bed.getBeds_child())))
                .collect(Collectors.toList());
    }

    public List<Map<Beds, Integer>> getBedsCombinations() {
        if (!this.ready) {
            throw new IllegalStateException("Must form search request before accessing it");
        }
        return this.bedsCombinations;
    }
}
