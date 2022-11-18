package vitaliiev.resortASU.service.search;


import vitaliiev.resortASU.model.suit.Beds;

import java.util.HashMap;
import java.util.Map;


public class BedsQuantityCombination {

    private final int adultBedsRequested;
    private final int childBedsRequested;
    private final int suitsRequested;
    private int adultBeds = 0;
    private int childBeds = 0;
    private int suits = 0;
    private final Map<Beds, Integer> bedsCombinations = new HashMap<>();

    public BedsQuantityCombination(int adultBedsRequested, int childBedsRequested, int suitsRequested) {
        if (suitsRequested <= 0) {
            throw new IllegalArgumentException("Expected nonzero suits. Got: " + suitsRequested);
        }
        if (adultBedsRequested < 0 || childBedsRequested < 0 ||
                (adultBedsRequested == 0 && childBedsRequested == 0)) {
            throw new IllegalArgumentException("Expected positive number of beds, and one argument should be non zero" +
                    ". Got: "
                    + adultBedsRequested + ", " + childBedsRequested);
        }
        this.adultBedsRequested = adultBedsRequested;
        this.childBedsRequested = childBedsRequested;
        this.suitsRequested = suitsRequested;
    }

    public void add(Beds bed) {
        // only quantity allowed for correct object distinction
        Beds temp = new Beds();
        temp.setBeds_adult(bed.getBeds_adult());
        temp.setBeds_child(bed.getBeds_child());
        bed = temp;
        if (this.bedCanBeAdded(bed)) {
            this.bedsCombinations.merge(bed, 1, Integer::sum);
            this.adultBeds += bed.getBeds_adult();
            this.childBeds += bed.getBeds_child();
            this.suits++;
        }

    }

    public boolean isCompleted() {
        return this.adultBeds >= this.adultBedsRequested && this.childBeds >= this.childBedsRequested;
    }

    public boolean bedCanBeAdded(Beds bed) {
        return this.suits < this.suitsRequested &&
                this.adultBeds + bed.getBeds_adult() <= this.adultBedsRequested &&
                this.childBeds + bed.getBeds_child() <= this.childBedsRequested;
    }

    public Map<Beds, Integer> getBedsCombinations() {
        if (!this.isCompleted()) {
            throw new IllegalStateException("Must complete bed combination before accessing it");
        }
        Map<Beds, Integer> result = new HashMap<>(this.bedsCombinations.size());
        for (Map.Entry<Beds, Integer> oldEntry : this.bedsCombinations.entrySet()) {
            Beds temp = new Beds();
            temp.setBeds_adult(oldEntry.getKey().getBeds_adult());
            temp.setBeds_child(oldEntry.getKey().getBeds_child());
            result.put(temp, oldEntry.getValue());
        }
        return result;

    }

}
