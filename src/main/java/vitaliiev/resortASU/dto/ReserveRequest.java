package vitaliiev.resortASU.dto;

import lombok.*;
import vitaliiev.resortASU.model.customer.Customer;
import vitaliiev.resortASU.model.reserve.PaymentStatus;
import vitaliiev.resortASU.model.reserve.PaymentType;
import vitaliiev.resortASU.service.search.SuitSearchResult;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ReserveRequest {

    @NotNull(message = "Check-in date must be set")
    private Date checkIn;
    @NotNull(message = "Check-out date must be set")
    private Date checkOut;
    @PositiveOrZero(message = "Adult beds quantity must be zero or positive")
    private int adultBeds;
    @PositiveOrZero(message = "Child beds quantity must be zero or positive")
    private int childBeds;

    private PaymentType paymentType;

    private PaymentStatus paymentStatus;

    private BigDecimal days = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private List<SuitSearchResult> suitTypes;
    private List<Customer> adults;
    private List<Customer> children;


    public BigDecimal getTotalDays() {
        if (this.days.equals(BigDecimal.ZERO)) {
            LocalDate start = this.checkIn.toLocalDate();
            LocalDate end = this.checkOut.toLocalDate();
            this.days = new BigDecimal(start.until(end, ChronoUnit.DAYS));
        }
        return this.days;
    }

    public BigDecimal getTotalPrice() {
        BigDecimal days = this.getTotalDays();
        if (this.totalPrice.equals(BigDecimal.ZERO)) {
            this.suitTypes.forEach(st -> this.totalPrice = this.totalPrice.add(st.getPrice().multiply(days)));
        }
        return this.totalPrice;
    }

}
