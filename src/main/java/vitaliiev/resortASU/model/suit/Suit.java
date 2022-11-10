package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.reserve.ReserveSuit;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Suit {
    public static final String ENTITY_NAME = "Suit";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @EqualsAndHashCode.Include
    private String number;
    @ManyToOne
    private SuitStatus suitstatus;
    @ManyToOne
    private SuitType suittype;
    private String comment;
    @OneToMany
    private Set<ReserveSuit> reserveSuit;
}
