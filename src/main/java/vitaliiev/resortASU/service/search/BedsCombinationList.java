package vitaliiev.resortASU.service.search;


import com.google.common.collect.Sets;
import lombok.Getter;
import vitaliiev.resortASU.model.suit.Beds;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Object that holds valid search request with all possible Beds combinations with needed quantity
 */

public class BedsCombinationList {

    /**
     * Initial beds quantity combination
     */
    private final BedsQuantityCombination bedsQuantityCombination;
    /**
     * All possible beds
     */
    private final List<Beds> beds;
    /**
     * List that hold all possible beds combinations of beds objects for given adult and child beds number
     */
    private List<BedsCombination> bedsCombinations;
    @Getter
    private boolean ready = false;


    public BedsCombinationList(BedsQuantityCombination bedsQuantityCombination, List<Beds> beds) {
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
     * Form list of all possible bed combinations
     */
    public void formSearchRequest() {
        Map<Beds, Integer> initialCombination = this.bedsQuantityCombination.getBedsCombinations();
        List<Set<Beds>> combinationsInitial = initialCombination.keySet().stream()
                .map(b-> new HashSet<>(this.getBedsWithEqualQuantity(b)))
                .collect(Collectors.toList());
        Set<List<Beds>> cartesianProduct = Sets.cartesianProduct(combinationsInitial);

        List<BedsCombination> result = new ArrayList<>();
        for (List<Beds> bedsList : cartesianProduct) {
            result.add(new BedsCombination(this.bedsQuantityCombination, bedsList));

        }
        this.bedsCombinations = result;
        this.ready = true;
    }

    private List<Beds> getBedsWithEqualQuantity(Beds bed) {
        return this.beds.stream()
                .filter(b -> b.quantityEquals(bed))
                .collect(Collectors.toList());
    }

    public List<BedsCombination> getBedsCombinations() {
        if (!this.ready) {
            throw new IllegalStateException("Must form search request before accessing it");
        }
        return this.bedsCombinations;
    }
}
