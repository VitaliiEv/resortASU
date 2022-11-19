package vitaliiev.resortASU.service.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.ResortASUGeneralException;
import vitaliiev.resortASU.dto.SuitSearchRequest;
import vitaliiev.resortASU.dto.SuitSearchResultSet;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.service.suit.BedsService;
import vitaliiev.resortASU.service.suit.SuitClassService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
//@Transactional
public class SearchService {

    private final SuitTypeService suitTypeService;
    private final SuitClassService suitClassService;
    private final BedsService bedsService;

    @Autowired
     public SearchService(SuitTypeService suitTypeService, SuitClassService suitClassService, BedsService bedsService) {
        this.suitTypeService = suitTypeService;
        this.suitClassService = suitClassService;
        this.bedsService = bedsService;
    }

    /**
     * search steps
    * 1. find acceptable beds combinations
    * 2. for each bed combination find set of suitType combinations
     * 3. for each suit combination check if there is enough free suits
     */
    public List<SuitSearchResultSet> find(SuitSearchRequest request) throws ResortASUGeneralException {
        validateRequest(request);
        BedsCombinationList bedsSearchRequest = formBedsSearchRequest(request.getAdultBeds(), request.getChildBeds());
        SuitTypeCombinationList suitTypeCombinationList = formSuitTypeSearchRequest(bedsSearchRequest, request);
        SuitTypeSearch suitTypeSearch = new SuitTypeSearch(request.getCheckIn(), request.getCheckOut(),
                suitTypeCombinationList);
        suitTypeSearch.performSearch();
        return suitTypeSearch.getSearchResults();
    }

    public void validateRequest(SuitSearchRequest request) throws ResortASUGeneralException {
        LocalDate today = LocalDate.now();
        if (request.getCheckIn().toLocalDate().isBefore(today)) {
            throw new ResortASUGeneralException("Check-in date can't be from past");
        }
        if (request.getCheckOut().toLocalDate().isBefore(today)) {
            throw new ResortASUGeneralException("Check-out date can't be from past");
        }
        if (!request.getCheckIn().before(request.getCheckOut())) {
            throw new ResortASUGeneralException("Check-out date before check-in date.");
        }
    }

    /**
     * Finds one optimal combination of beds from sorted descending beds list
     * In case if there can be several combinations of suits it will first search
     * for suits with bigger quantity of adult beds, then with bigger quantity of child beds.
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

    public BedsCombinationList formBedsSearchRequest(int bedsAdult, int bedsChild) {
        BedsQuantityCombination bedsQuantityCombination = findPossibleBedsCombinations(bedsAdult, bedsChild);
        BedsCombinationList bedsSearchRequest = new BedsCombinationList(bedsQuantityCombination, this.bedsService.findAll());
        bedsSearchRequest.formSearchRequest();
        return bedsSearchRequest;
    }

    public SuitTypeCombinationList formSuitTypeSearchRequest(BedsCombinationList bedsSearchRequest, SuitSearchRequest suitSearchRequest) {
        List<SuitType> suitTypes;
        if (suitSearchRequest.getSuitClassId() != null) {
            SuitType suitType = new SuitType();
            suitType.setSuitClass(suitClassService.findById(suitSearchRequest.getSuitClassId()));
            suitType.setDeleted(false);
            suitTypes =  this.suitTypeService.find(suitType);
        } else {
            suitTypes = this.suitTypeService.findAllPresent();
        }
        SuitTypeCombinationList suitTypeCombinationList = new SuitTypeCombinationList(bedsSearchRequest, suitTypes);
        suitTypeCombinationList.formSearchRequest();
        return suitTypeCombinationList;
    }

}
