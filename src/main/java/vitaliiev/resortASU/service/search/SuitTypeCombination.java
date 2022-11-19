package vitaliiev.resortASU.service.search;


import lombok.Getter;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.model.suit.SuitType;

import java.util.*;


public class SuitTypeCombination {

    /**
     * Initial beds combination
     */
    private final BedsCombination bedsCombination;
    /**
     * List of actual bed types, which has the same
     */
    private final List<SuitType> suitTypes;
    @Getter
    private final Map<SuitType, Integer> suitTypeCombination = new HashMap<>();

    public SuitTypeCombination(BedsCombination bedsCombination, List<SuitType> suitTypes) {
        Objects.requireNonNull(bedsCombination);
        Objects.requireNonNull(suitTypes);

        Map<Beds, Integer> initialCombination = bedsCombination.getBedsCombination();
        List<Beds> bedsList = new ArrayList<>(initialCombination.keySet());
        if (bedsList.size() != suitTypes.size()) {
            throw new IllegalArgumentException("Non matching list sizes");
        }
        for (int i = 0; i < suitTypes.size(); i++) {
            SuitType suitType = suitTypes.get(i);
            Beds beds = bedsList.get(i);
            if (!suitType.getBeds().equals(beds)) {
                throw new IllegalStateException("Wrong order");
            }
            this.suitTypeCombination.put(suitType, initialCombination.get(beds));
        }
        if (this.suitTypeCombination.size() != initialCombination.size()) {
            throw new IllegalStateException("Combination creation not finished");
        }
        this.bedsCombination = bedsCombination;
        this.suitTypes = suitTypes;
    }
}
