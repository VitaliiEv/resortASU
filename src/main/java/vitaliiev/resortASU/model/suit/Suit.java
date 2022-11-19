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
    @JoinColumn(name="suitstatus")
    private SuitStatus suitstatus;
    @ManyToOne
    @JoinColumn(name="suittype")
    private SuitType suittype;
    private String comment;
    @OneToMany(mappedBy = "suit")
    private Set<ReserveSuit> reserveSuit;
    private Boolean deleted = false;
}
