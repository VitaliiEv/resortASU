package vitaliiev.resortASU.service.search;


import lombok.Getter;
import vitaliiev.resortASU.model.suit.Beds;

import java.util.*;


public class BedsCombination {

    /**
     * Initial beds quantity combination
     */
    private final BedsQuantityCombination bedsQuantityCombination;
    /**
     * List of actual bed types
     */
    private final List<Beds> beds;
    @Getter
    private final Map<Beds, Integer> bedsCombination = new HashMap<>();

    public BedsCombination(BedsQuantityCombination bedsQuantityCombination, List<Beds> beds) {
        Objects.requireNonNull(bedsQuantityCombination);
        Objects.requireNonNull(beds);
        if (!bedsQuantityCombination.isCompleted()) {
            throw new IllegalStateException("Bed search request must be in state \"completed\"");
        }

        Map<Beds, Integer> initialCombination = bedsQuantityCombination.getBedsCombinations();
        List<Beds> quantityList = new ArrayList<>(initialCombination.keySet());
        if (quantityList.size() != beds.size()) {
            throw new IllegalArgumentException("Non matching list sizes");
        }
        for (int i = 0; i < beds.size(); i++) {
            Beds b = beds.get(i);
            Beds quantity = quantityList.get(i);
            if (!b.quantityEquals(quantity)) {
                throw new IllegalStateException("Wrong order");
            }
            this.bedsCombination.put(b, initialCombination.get(quantity));
        }
        if (this.bedsCombination.size() != initialCombination.size()) {
            throw new IllegalStateException("Combination creation not finished");
        }
        this.bedsQuantityCombination = bedsQuantityCombination;
        this.beds = beds;
    }
}
