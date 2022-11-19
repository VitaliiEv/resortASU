package vitaliiev.resortASU.model.reserve;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.suit.Suit;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
public class ReserveSuit {
    public static final String ENTITY_NAME = "ReserveSuit";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="reserve_id")
    private Reserve reserve;
    @ManyToOne
    @JoinColumn(name="suit_id")
    private Suit suit;
    private BigDecimal price;
    @OneToMany
    private Set<ReserveServices> reserveServices;
}
