package vitaliiev.resortASU.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuitSearchRequest {
    @PositiveOrZero(message = "Adult beds quantity must be zero or positive")
    private int adultBeds;
    @PositiveOrZero(message = "Child beds quantity must be zero or positive")
    private int childBeds;
    @NotNull(message = "Check-in date must be set")
    private Date checkIn;
    @NotNull(message = "Check-out date must be set")
    private Date checkOut;
    private Integer suitClassId;

}
