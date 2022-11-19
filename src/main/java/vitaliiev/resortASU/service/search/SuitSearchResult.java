package vitaliiev.resortASU.service.search;

import lombok.Data;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.model.suit.SuitType;

import java.math.BigDecimal;

@Data
public class SuitSearchResult {

    //    What should be returned:

    private final Photo photo;
    private final String suitClass;
    private final String beds;
    private final BigDecimal price;
    private int quantity;

    public SuitSearchResult(SuitType suitType, int quantity) {
        this.photo = suitType.getMainphoto();
        this.suitClass = suitType.getSuitClass().getSuitclass();
        this.beds = suitType.getBeds().getBeds();
        this.price = suitType.getCurrentprice();
        this.quantity = quantity;

    }


}
