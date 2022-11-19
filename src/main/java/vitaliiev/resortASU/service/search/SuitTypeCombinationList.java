package vitaliiev.resortASU.service.search;


import com.google.common.collect.Sets;
import lombok.Getter;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.model.suit.SuitType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Object that holds valid search request with all possible SuitTypes combinations with needed quantity
 */

public class SuitTypeCombinationList {

    private final BedsCombinationList bedsSearchRequest;
    /**
     * All possible SuitTypes
     */
    private final List<SuitType> suitTypes;
    /**
     * List that hold all possible suittypes combinations for given bedsSearchrequest
     */
    @Getter
    private List<SuitTypeCombination> suitTypesCombinations;
    @Getter
    private boolean ready = false;


    public SuitTypeCombinationList(BedsCombinationList bedsSearchRequest, List<SuitType> suitTypes) {
        Objects.requireNonNull(bedsSearchRequest);
        if (!bedsSearchRequest.isReady()) {
            throw new IllegalStateException("Bed search request must be in state \"ready\"");
        }
        if (suitTypes == null || suitTypes.isEmpty()) {
            throw new IllegalArgumentException("Empty beds list");
        }
        this.bedsSearchRequest = bedsSearchRequest;
        this.suitTypes = suitTypes;
    }

    /**
     * Form list of all possible suit type to quantity combinations
     */
    public void formSearchRequest() {
        List<SuitTypeCombination> result = new ArrayList<>();
        List<BedsCombination> initialCombinations = this.bedsSearchRequest.getBedsCombinations();
        for (BedsCombination bedsCombination : initialCombinations) {
            List<Beds> beds = new ArrayList<>(bedsCombination.getBedsCombination().keySet());
            List<Set<SuitType>> suitTypes = beds.stream()
                    .map(extractOnlyMatching(this.suitTypes))
                    .filter(set -> !set.isEmpty())
                    .collect(Collectors.toList());
            if (suitTypes.size() == beds.size()) { // situation can occur when only part of beds have no matching SuitTypes
                Set<List<SuitType>> cartesianProduct = Sets.cartesianProduct(suitTypes);
                for (List<SuitType> st : cartesianProduct) {
                    result.add(new SuitTypeCombination(bedsCombination, st));
                }
            }
        }
        this.suitTypesCombinations = result;
        this.ready = true;
    }

    public Function<Beds, Set<SuitType>> extractOnlyMatching(List<SuitType> allowedSuitTypes) {
        return bed -> bed.getSuitTypes().stream()
                .filter(allowedSuitTypes::contains)
                .collect(Collectors.toSet());
    }

}
