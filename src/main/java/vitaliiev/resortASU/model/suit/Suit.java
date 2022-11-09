package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
//    @ManyToOne
//    @JoinTable(name = "buildingfloor")
//    private Suit suit;
}
