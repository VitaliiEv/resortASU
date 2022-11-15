package vitaliiev.resortASU.dto;

import lombok.Data;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.model.suit.SuitClass;
import vitaliiev.resortASU.model.suit.SuitType;

import java.math.BigDecimal;
import java.nio.file.Path;

@Data
public class SuitSearchResult {
//    What should be returned:

    private Photo photo;
    private Path photoStorage;
    private SuitClass suitClass;
    private Beds beds;
    private BigDecimal price;

    public SuitSearchResult(SuitType suitType) {
//        this.suitType = suitType;
    }


    // Photo, suitClass, beds, features, services

    private SuitType suitType;
//    Set of suit types with price and list of service
//    Set<SuitType> searchResults;

}
