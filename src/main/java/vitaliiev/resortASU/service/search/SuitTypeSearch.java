package vitaliiev.resortASU.service.search;


import lombok.Getter;
import vitaliiev.resortASU.dto.SuitSearchResultSet;
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.model.suit.Suit;
import vitaliiev.resortASU.model.suit.SuitType;

import java.sql.Date;
import java.util.*;
import java.util.function.Predicate;

public class SuitTypeSearch {

    private final Date checkIn;
    private final Date checkOut;
    private final SuitTypeCombinationList suitTypeCombinationList;

    private final Map<SuitType, Integer> cache = new HashMap<>();
    @Getter
    private final List<SuitSearchResultSet> searchResults = new ArrayList<>();

    public SuitTypeSearch(Date checkIn, Date checkOut,
                          SuitTypeCombinationList suitTypeCombinationList) {
        if (!checkIn.before(checkOut)) {
            throw new IllegalArgumentException("Checkout date before checkin date.");
        }
        Objects.requireNonNull(suitTypeCombinationList);
        if (!suitTypeCombinationList.isReady()) {
            throw new IllegalStateException("Bed search request must be in state \"ready\"");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.suitTypeCombinationList = suitTypeCombinationList;
    }

    public void performSearch() {
        List<SuitTypeCombination> suitTypeCombinations = this.suitTypeCombinationList.getSuitTypesCombinations();
        for (SuitTypeCombination combination : suitTypeCombinations) {
            Set<SuitSearchResult> tempSet = new HashSet<>();
            for (Map.Entry<SuitType, Integer> entry : combination.getSuitTypeCombination().entrySet()) {
                Integer requestedSuits = entry.getValue();
                Integer freeSuits;
                if (this.cache.containsKey(entry.getKey())) {
                    freeSuits = this.cache.get(entry.getKey());
                } else {
                    freeSuits = countMatchingSuits(entry.getKey(), isAvailable(checkIn, checkOut));
                    this.cache.put(entry.getKey(), freeSuits);
                }
                if (freeSuits >= requestedSuits) {
                    tempSet.add(new SuitSearchResult(entry.getKey(), requestedSuits));
                } else {
                    break;
                }
            }
            if (tempSet.size() == combination.getSuitTypeCombination().size()) {
                SuitSearchResultSet suitSearchResultSet = new SuitSearchResultSet(this.checkIn, this.checkOut);
                tempSet.forEach(suitSearchResultSet::add);
                this.searchResults.add(suitSearchResultSet);
            }
            tempSet.clear();
        }
    }

    public int countMatchingSuits(SuitType suitType, Predicate<Suit> matcher) {
        return (int) suitType.getSuits().stream()
                .filter(matcher)
                .count();
    }

    public Predicate<Suit> isAvailable(Date checkIn, Date checkOut) {
        return suit -> suit.getReserveSuit().stream()
                .map(ReserveSuit::getReserve)
                .allMatch(periodsDontOverlap(checkIn, checkOut));
    }


    public Predicate<Reserve> periodsDontOverlap(Date checkIn, Date checkOut) {
        return reserve -> checkIn.after(reserve.getCheckout()) || checkIn.equals(reserve.getCheckout()) ||
                checkOut.before(reserve.getCheckin()) || checkOut.equals(reserve.getCheckin());
    }
}
