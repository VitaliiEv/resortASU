package vitaliiev.resortASU.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.service.search.SuitSearchResult;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class SuitSearchResultSet {

    private final Set<SuitSearchResult> suitTypes = new HashSet<>();
    private final Date checkIn;
    private final Date checkOut;
    private final BigDecimal days;
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public SuitSearchResultSet(Date checkIn, Date checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        if (!checkOut.after(checkIn)) {
            throw new IllegalArgumentException("Checkout date before checkin date.");
        }
        LocalDate start = checkIn.toLocalDate();
        LocalDate end = checkOut.toLocalDate();
        this.days = new BigDecimal(start.until(end, ChronoUnit.DAYS));
    }

    public void add(SuitSearchResult suitSearchResult) {
        this.suitTypes.add(suitSearchResult);
        this.totalPrice= this.totalPrice.add(suitSearchResult.getPrice().multiply(this.days));
    }


}
