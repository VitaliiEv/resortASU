package vitaliiev.resortASU.model.reserve;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.suit.Services;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class ReserveServices {
    public static final String ENTITY_NAME = "ReserveServices";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Services services;
    @ManyToOne
    private ReserveSuit reservesuit;
    private BigDecimal price;
}
