package vitaliiev.resortASU.service.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.model.suit.SuitType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SuitSearchResult {

    private Integer suitTypeId;
    private Photo photo;
    private String suitClass;
    private String beds;
    private BigDecimal price;
    private int quantity;

    public SuitSearchResult(SuitType suitType, int quantity) {
        this.suitTypeId = suitType.getId();
        this.photo = suitType.getMainphoto();
        this.suitClass = suitType.getSuitClass().getSuitclass();
        this.beds = suitType.getBeds().getBeds();
        this.price = suitType.getCurrentprice();
        this.quantity = quantity;
    }

    public void update(SuitType suitType) {
        this.photo = suitType.getMainphoto();
        this.beds = suitType.getBeds().getBeds();
        this.price = suitType.getCurrentprice();
        this.suitClass = suitType.getSuitClass().getSuitclass();
    }

}
